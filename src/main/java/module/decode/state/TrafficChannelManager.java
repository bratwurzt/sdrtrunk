package module.decode.state;

import alias.Alias;
import alias.id.priority.Priority;
import controller.channel.*;
import controller.channel.Channel.ChannelType;
import controller.channel.ChannelEvent.Event;
import module.Module;
import module.decode.config.DecodeConfiguration;
import module.decode.event.CallEvent;
import module.decode.event.CallEvent.CallEventType;
import module.decode.event.ICallEventProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import record.config.RecordConfiguration;
import sample.Listener;
import source.config.SourceConfigTuner;
import source.tuner.TunerChannel;
import source.tuner.TunerChannel.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

public class TrafficChannelManager extends Module
    implements ICallEventProvider, IDecoderStateEventListener
{
  private final static Logger mLog = LoggerFactory.getLogger(TrafficChannelManager.class);
  public static final String CHANNEL_START_REJECTED = "CHANNEL START REJECTED";
  public static final String NO_TUNER_AVAILABLE = "NO TUNER AVAILABLE";
  public static final String UNKNOWN_FREQUENCY = "UNKNOWN FREQUENCY";

  private int mTrafficChannelPoolMaximumSize =
      DecodeConfiguration.TRAFFIC_CHANNEL_LIMIT_DEFAULT;
  private List<Channel> mTrafficChannelPool = new ArrayList<Channel>();
  private Map<String, Channel> mTrafficChannelsInUse = new ConcurrentHashMap<String, Channel>();

  private DecoderStateEventListener mEventListener = new DecoderStateEventListener();
  private Listener<CallEvent> mCallEventListener;

  private ChannelModel mChannelModel;
  private ChannelProcessingManager mChannelProcessingManager;
  private DecodeConfiguration mDecodeConfiguration;
  private RecordConfiguration mRecordConfiguration;
  private String mSystem;
  private String mSite;
  private String mAliasListName;
  private CallEvent mPreviousDoNotMonitorCallEvent;

  /**
   * Monitors call events and allocates traffic decoder channels in response
   * to traffic channel allocation call events.  Manages a pool of reusable
   * traffic channel allocations.
   *
   * @param channelModel             containing channels currently in use
   * @param channelProcessingManager
   * @param decodeConfiguration      - decoder configuration to use for each
   *                                 traffic channel allocation.
   * @param recordConfiguration      - recording options for each traffic channel
   * @param system                   label to use to describe the system
   * @param site                     label to use to describe the site
   * @param aliasListName            designated for the channel
   * @param trafficChannelTimeout    - millisecond call timer limit used when an
   *                                 end of call signalling event is not decoded.
   * @param trafficChannelPoolSize   - maximum number of allocated traffic channels
   *                                 in the pool
   */
  public TrafficChannelManager(ChannelModel channelModel,
                               ChannelProcessingManager channelProcessingManager,
                               DecodeConfiguration decodeConfiguration,
                               RecordConfiguration recordConfiguration,
                               String system,
                               String site,
                               String aliasListName,
                               long trafficChannelTimeout,
                               int trafficChannelPoolSize)
  {
    mChannelModel = channelModel;
    mChannelProcessingManager = channelProcessingManager;
    mDecodeConfiguration = decodeConfiguration;
    mRecordConfiguration = recordConfiguration;
    mSystem = system;
    mSite = site;
    mAliasListName = aliasListName;
    mTrafficChannelPoolMaximumSize = trafficChannelPoolSize;
  }

  @Override
  public void dispose()
  {
    for (Channel trafficChannel : mTrafficChannelPool)
    {
      mChannelModel.broadcast(new ChannelEvent(trafficChannel, Event.REQUEST_DISABLE));
    }

    mTrafficChannelPool.clear();

    mTrafficChannelsInUse.clear();

    mCallEventListener = null;
    mDecodeConfiguration = null;
    mPreviousDoNotMonitorCallEvent = null;
  }

  /**
   * Provides a channel by either reusing an existing channel or constructing
   * a new one, limited by the total number of constructed channels allowed.
   * <p>
   * Note: you must enforce thread safety on the mTrafficChannelsInUse
   * external to this method.
   *
   * @param channelNumber
   * @param tunerChannel
   * @return
   */
  private Channel getChannel(String channelNumber, TunerChannel tunerChannel)
  {
    Channel channel = null;

    if (!mTrafficChannelsInUse.containsKey(channelNumber))
    {
      for (Channel configuredChannel : mTrafficChannelPool)
      {
        if (!configuredChannel.getEnabled())
        {
          channel = configuredChannel;
          break;
        }
      }

      if (channel == null && mTrafficChannelPool.size() < mTrafficChannelPoolMaximumSize)
      {
        channel = new Channel("Traffic", ChannelType.TRAFFIC);

        channel.setDecodeConfiguration(mDecodeConfiguration);

        channel.setRecordConfiguration(mRecordConfiguration);

        channel.setAliasListName(mAliasListName);

        mChannelModel.addChannel(channel);

        mTrafficChannelPool.add(channel);
      }

			/* If we have a configured channel, update metadata */
      if (channel != null)
      {
        channel.setSourceConfiguration(new SourceConfigTuner(tunerChannel));
        channel.setSystem(mSystem);
        channel.setSite(mSite);
        channel.setName(channelNumber);

        mChannelModel.broadcast(new ChannelEvent(channel, Event.NOTIFICATION_CONFIGURATION_CHANGE));
      }
    }

    return channel;
  }

  /**
   * Processes the event and creates a traffic channel is resources are
   * available
   */
  private void process(TrafficChannelAllocationEvent event)
  {
    CallEvent callEvent = event.getCallEvent();

		/* Check for duplicate events and suppress */
    synchronized (mTrafficChannelsInUse)
    {
      if (mTrafficChannelsInUse.containsKey(callEvent.getChannel()))
      {
        return;
      }

      long frequency = callEvent.getFrequency();

			/* Check the from/to aliases for do not monitor priority */
      if (isDoNotMonitor(callEvent))
      {
        callEvent.setCallEventType(CallEventType.CALL_DO_NOT_MONITOR);

        if (isSameCallEvent(mPreviousDoNotMonitorCallEvent, callEvent))
        {
          return;
        }
        else
        {
          mPreviousDoNotMonitorCallEvent = callEvent;
        }
      }
      else if (frequency > 0)
      {
        Channel channel = getChannel(callEvent.getChannel(), new TunerChannel(Type.TRAFFIC,
            frequency, mDecodeConfiguration.getDecoderType().getChannelBandwidth()));

        if (channel != null)
        {
          TrafficChannelEvent trafficChannelEvent =
              new TrafficChannelEvent(this, channel, Event.REQUEST_ENABLE, callEvent);

          //Request to enable the channel
          mChannelModel.broadcast(trafficChannelEvent);

          if (channel.getEnabled())
          {
            mTrafficChannelsInUse.put(callEvent.getChannel(), channel);
          }
          else
          {
            callEvent.setCallEventType(CallEventType.CALL_DETECT);

            String details = callEvent.getDetails();

            if (details == null || details.isEmpty())
            {
              callEvent.setDetails(CHANNEL_START_REJECTED);
            }
            else if (!details.contains(CHANNEL_START_REJECTED))
            {
              callEvent.setDetails(new StringBuilder(CHANNEL_START_REJECTED)
                  .append(" : ")
                  .append(callEvent.getDetails()).toString());
            }
          }
        }
        else
        {
          callEvent.setCallEventType(CallEventType.CALL_DETECT);

          String details = callEvent.getDetails();

          if (details == null || details.isEmpty())
          {
            callEvent.setDetails(NO_TUNER_AVAILABLE);
          }
          else if (!details.contains(NO_TUNER_AVAILABLE))
          {
            callEvent.setDetails(new StringBuilder(NO_TUNER_AVAILABLE)
                .append(" : ")
                .append(callEvent.getDetails()).toString());
          }
        }
      }
      else
      {
        callEvent.setCallEventType(CallEventType.CALL_DETECT);

        String details = callEvent.getDetails();

        if (details == null || details.isEmpty())
        {
          callEvent.setDetails(UNKNOWN_FREQUENCY);
        }
        else if (!details.contains(UNKNOWN_FREQUENCY))
        {
          callEvent.setDetails(new StringBuilder(UNKNOWN_FREQUENCY)
              .append(" : ")
              .append(callEvent.getDetails()).toString());
        }
      }

      final Listener<CallEvent> listener = mCallEventListener;

      if (listener != null)
      {
        listener.receive(callEvent);
      }
    }
  }

  /**
   * Compares the call type, channel and to fields for equivalence and the
   * from field for either both null, or equivalence.
   *
   * @param e1
   * @param e2
   * @return
   */
  public static boolean isSameCallEvent(CallEvent e1, CallEvent e2)
  {
    if (e1 == null || e2 == null)
    {
      return false;
    }

    if (e1.getCallEventType() != e2.getCallEventType())
    {
      return false;
    }

    if (e1.getChannel() == null ||
        e2.getChannel() == null ||
        !e1.getChannel().contentEquals(e2.getChannel()))
    {
      return false;
    }

    if (e1.getToID() == null ||
        e2.getToID() == null ||
        !e1.getToID().contentEquals(e2.getToID()))
    {
      return false;
    }

    if (e1.getFromID() == null || e2.getFromID() == null)
    {
      return e1.getFromID() == null && e2.getFromID() == null;
    }
    else if (e1.getFromID().contentEquals(e2.getFromID()))
    {
      return true;
    }

    return false;
  }

  /**
   * Checks for aliases that contain a do not follow/process priority.
   *
   * @param event
   * @return - true if there is an alias that has a do not process alias id
   */
  private boolean isDoNotMonitor(CallEvent event)
  {
    Alias to = event.getToIDAlias();

    if (to != null &&
        to.hasCallPriority() &&
        to.getCallPriority() == Priority.DO_NOT_MONITOR)
    {
      return true;
    }

    Alias from = event.getFromIDAlias();

    return from != null &&
        from.hasCallPriority() &&
        from.getCallPriority() == Priority.DO_NOT_MONITOR;

  }

  @Override
  public Listener<DecoderStateEvent> getDecoderStateListener()
  {
    return mEventListener;
  }

  @Override
  public void addCallEventListener(Listener<CallEvent> listener)
  {
    mCallEventListener = listener;
  }

  @Override
  public void removeCallEventListener(Listener<CallEvent> listener)
  {
    mCallEventListener = null;
  }

  @Override
  public void reset()
  {
  }

  @Override
  public void start(ScheduledExecutorService executor)
  {
  }

  @Override
  public void stop()
  {
    if (!mTrafficChannelsInUse.isEmpty())
    {
      List<String> channels = new ArrayList<>();

			/* Copy the keyset so we don't get concurrent modification of the map */
      channels.addAll(mTrafficChannelsInUse.keySet());

      for (String channel : channels)
      {
        callEnd(channel);
      }
    }
  }

  /**
   * Callback used by the TrafficChannelStatusListener class to signal the
   * end of a an allocated traffic channel call event
   *
   * @param channelNumber - channel number from the call event that signaled
   *                      the start of a traffic channel allocation
   */
  public void callEnd(String channelNumber)
  {
    synchronized (mTrafficChannelsInUse)
    {
      if (channelNumber != null && mTrafficChannelsInUse.containsKey(channelNumber))
      {
        Channel channel = mTrafficChannelsInUse.get(channelNumber);

        mChannelModel.broadcast(new ChannelEvent(channel, Event.REQUEST_DISABLE));

        mTrafficChannelsInUse.remove(channelNumber);
      }
    }
  }

  /**
   * Wrapper class for the decoder state event listener interface to catch
   * traffic channel allocation requests
   */
  public class DecoderStateEventListener implements Listener<DecoderStateEvent>
  {
    @Override
    public void receive(DecoderStateEvent event)
    {
      switch (event.getEvent())
      {
        case TRAFFIC_CHANNEL_ALLOCATION:
          process((TrafficChannelAllocationEvent) event);
          break;
        default:
          break;
      }
    }
  }
}
