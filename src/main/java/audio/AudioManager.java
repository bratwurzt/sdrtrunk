/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014-2016 Dennis Sheirer
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package audio;

import audio.AudioEvent.Type;
import audio.output.AudioOutput;
import audio.output.MonoAudioOutput;
import audio.output.StereoAudioOutput;
import controller.ThreadPoolManager;
import controller.ThreadPoolManager.ThreadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.SystemProperties;
import sample.Broadcaster;
import sample.Listener;
import source.mixer.MixerChannel;
import source.mixer.MixerChannelConfiguration;
import source.mixer.MixerManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AudioManager implements Listener<AudioPacket>, IAudioController
{
  private static final Logger mLog = LoggerFactory.getLogger(AudioManager.class);

  public static final int AUDIO_TIMEOUT = 1000; //1 second

  public static final String AUDIO_CHANNELS_PROPERTY = "audio.manager.channels";
  public static final String AUDIO_MIXER_PROPERTY = "audio.manager.mixer";

  public static final AudioEvent CONFIGURATION_CHANGE_STARTED =
      new AudioEvent(Type.AUDIO_CONFIGURATION_CHANGE_STARTED, null);

  public static final AudioEvent CONFIGURATION_CHANGE_COMPLETE =
      new AudioEvent(Type.AUDIO_CONFIGURATION_CHANGE_COMPLETE, null);

  private LinkedTransferQueue<AudioPacket> mAudioPacketQueue = new LinkedTransferQueue<>();
  private Map<Integer, AudioOutputConnection> mChannelConnectionMap = new HashMap<>();
  private List<AudioOutputConnection> mAudioOutputConnections = new ArrayList<>();
  private AudioOutputConnection mLowestPriorityConnection;
  private int mAvailableConnectionCount;

  private Map<String, AudioOutput> mAudioOutputMap = new HashMap<>();

  private Broadcaster<AudioEvent> mControllerBroadcaster = new Broadcaster<>();

  private ThreadPoolManager mThreadPoolManager;
  private ScheduledFuture<?> mProcessingTask;
  private MixerManager mMixerManager;
  private MixerChannelConfiguration mMixerChannelConfiguration;


  /**
   * Processes all audio produced by the decoding channels and routes audio
   * packets to any combination of outputs based on any alias audio routing
   * options specified by the user.
   */
  public AudioManager(ThreadPoolManager manager, MixerManager mixerManager)
  {
    mThreadPoolManager = manager;
    mMixerManager = mixerManager;

    loadSettings();
  }

  private void loadSettings()
  {
    MixerChannelConfiguration configuration = null;

    SystemProperties properties = SystemProperties.getInstance();

    Mixer defaultMixer = AudioSystem.getMixer(null);

    String mixer = properties.get(AUDIO_MIXER_PROPERTY, defaultMixer.getMixerInfo().getName());

    String channels = properties.get(AUDIO_CHANNELS_PROPERTY, MixerChannel.MONO.name());

    MixerChannelConfiguration[] mixerConfigurations = mMixerManager.getOutputMixers();

    for (MixerChannelConfiguration mixerConfig : mixerConfigurations)
    {
      if (mixerConfig.matches(mixer, channels))
      {
        configuration = mixerConfig;
      }
    }

    if (configuration == null)
    {
      configuration = getDefaultConfiguration();
    }

    try
    {
      setMixerChannelConfiguration(configuration);
    }
    catch (Exception e)
    {
      mLog.error("Couldn't set stored audio mixer/channel configuration"
          + " - using default", e);

      try
      {
        setMixerChannelConfiguration(getDefaultConfiguration());
      }
      catch (Exception e2)
      {
        mLog.error("Couldn't set default audio mixer/channel "
            + "configuration - no audio will be available", e2);
      }
    }
  }

  private MixerChannelConfiguration getDefaultConfiguration()
  {
    /* Use the system default mixer and mono channel as default startup */
    Mixer defaultMixer = AudioSystem.getMixer(null);

    return new MixerChannelConfiguration(defaultMixer, MixerChannel.MONO);
  }

  public void dispose()
  {
    if (mThreadPoolManager != null && mProcessingTask != null)
    {
      mThreadPoolManager.cancel(mProcessingTask);
    }

    mAudioPacketQueue.clear();

    mThreadPoolManager = null;
    mProcessingTask = null;

    mChannelConnectionMap.clear();

    for (AudioOutputConnection connection : mAudioOutputConnections)
    {
      connection.dispose();
    }

    mAudioOutputConnections.clear();
  }

  @Override
  public synchronized void receive(AudioPacket packet)
  {
    mAudioPacketQueue.add(packet);
  }

  /**
   * Checks each audio channel assignment and disconnects any inactive connections
   */
  private void disconnectInactiveChannelAssignments()
  {
    boolean changed = false;

    for (AudioOutputConnection connection : mAudioOutputConnections)
    {
      if (connection.isInactive() &&
          mChannelConnectionMap.containsKey(connection.getSource()))
      {
        mChannelConnectionMap.remove(connection.getSource());
        connection.disconnect();
        changed = true;
      }
    }

    if (changed)
    {
      updateLowestPriorityAssignment();
    }
  }

  /**
   * Identifies the lowest priority channel connection
   */
  private void updateLowestPriorityAssignment()
  {
    mLowestPriorityConnection = null;

    for (AudioOutputConnection connection : mAudioOutputConnections)
    {
      if (connection.isConnected() &&
          (mLowestPriorityConnection == null ||
              mLowestPriorityConnection.getPriority() > connection.getPriority()))
      {
        mLowestPriorityConnection = connection;
      }
    }
  }

  @Override
  public void setMixerChannelConfiguration(MixerChannelConfiguration entry) throws AudioException
  {
    if (entry != null &&
        (entry.getMixerChannel() == MixerChannel.MONO ||
            entry.getMixerChannel() == MixerChannel.STEREO))
    {
      mControllerBroadcaster.broadcast(CONFIGURATION_CHANGE_STARTED);

      if (mThreadPoolManager != null && mProcessingTask != null)
      {
        mThreadPoolManager.cancel(mProcessingTask);
      }

      disposeCurrentConfiguration();

      switch (entry.getMixerChannel())
      {
        case MONO:
          AudioOutput mono = new MonoAudioOutput(mThreadPoolManager,
              entry.getMixer());
          mAudioOutputConnections.add(new AudioOutputConnection(mono));
          mAvailableConnectionCount++;
          mAudioOutputMap.put(mono.getChannelName(), mono);
          break;
        case STEREO:
          AudioOutput left = new StereoAudioOutput(mThreadPoolManager,
              entry.getMixer(), MixerChannel.LEFT);
          mAudioOutputConnections.add(new AudioOutputConnection(left));
          mAvailableConnectionCount++;
          mAudioOutputMap.put(left.getChannelName(), left);

          AudioOutput right = new StereoAudioOutput(mThreadPoolManager,
              entry.getMixer(), MixerChannel.RIGHT);
          mAudioOutputConnections.add(new AudioOutputConnection(right));
          mAvailableConnectionCount++;
          mAudioOutputMap.put(right.getChannelName(), right);
          break;
        default:
          throw new AudioException("Unsupported mixer channel "
              + "configuration: " + entry.getMixerChannel());
      }

      mProcessingTask = mThreadPoolManager.scheduleFixedRate(
          ThreadType.AUDIO_PROCESSING, new AudioPacketProcessor(), 15,
          TimeUnit.MILLISECONDS);

      mControllerBroadcaster.broadcast(CONFIGURATION_CHANGE_COMPLETE);

      SystemProperties properties = SystemProperties.getInstance();

      properties.set(AUDIO_MIXER_PROPERTY, entry.getMixer().getMixerInfo().getName());
      properties.set(AUDIO_CHANNELS_PROPERTY, entry.getMixerChannel().name());
      properties.save();
    }
  }

  /**
   * Clears all channel assignments and terminates all audio outputs in
   * preparation for complete shutdown or change to another mixer/channel
   * configuration
   */
  private void disposeCurrentConfiguration()
  {
    mChannelConnectionMap.clear();

    for (AudioOutputConnection connection : mAudioOutputConnections)
    {
      connection.dispose();
    }

    mAvailableConnectionCount = 0;

    mAudioOutputConnections.clear();

    mAudioOutputMap.clear();

    mLowestPriorityConnection = null;
  }

  @Override
  public MixerChannelConfiguration getMixerChannelConfiguration() throws AudioException
  {
    return mMixerChannelConfiguration;
  }

  @Override
  public List<AudioOutput> getAudioOutputs()
  {
    List<AudioOutput> outputs = new ArrayList<>(mAudioOutputMap.values());

    Collections.sort(outputs, new Comparator<AudioOutput>()
    {
      @Override
      public int compare(AudioOutput first, AudioOutput second)
      {
        return first.getChannelName().compareTo(second.getChannelName());
      }
    });

    return outputs;
  }

  @Override
  public void addControllerListener(Listener<AudioEvent> listener)
  {
    mControllerBroadcaster.addListener(listener);
  }

  @Override
  public void removeControllerListener(Listener<AudioEvent> listener)
  {
    mControllerBroadcaster.removeListener(listener);
  }

  /**
   * Returns an audio output connection for the packet if one is
   * available, or overrides an existing lower priority connection.
   * Returns null if no connection is available for the audio packet.
   *
   * @param packet from a source
   * @return an audio output connection or null
   */
  private AudioOutputConnection getConnection(AudioPacket packet)
  {
    int source = packet.getAudioMetadata().getSource();

    //Use an existing connection
    if (mChannelConnectionMap.containsKey(source))
    {
      return mChannelConnectionMap.get(source);
    }

    //Connect to and use an available connection
    if (mAvailableConnectionCount > 0)
    {
      for (AudioOutputConnection connection : mAudioOutputConnections)
      {
        if (connection.isDisconnected())
        {
          connection.connect(source);
          mChannelConnectionMap.put(source, connection);
          return connection;
        }
      }
    }
    //Preempt an existing connection and connect it to this higher priority source
    else //Check for a channel priority override
    {
      int priority = packet.getAudioMetadata().getPriority();

      if (mLowestPriorityConnection != null &&
          priority < mLowestPriorityConnection.getPriority())
      {
        AudioOutputConnection connection = mLowestPriorityConnection;

        mChannelConnectionMap.remove(connection.getSource());

        connection.connect(source);

        mChannelConnectionMap.put(source, connection);

        updateLowestPriorityAssignment();

        return connection;
      }
    }

    return null;
  }

  public class AudioPacketProcessor implements Runnable
  {
    @Override
    public void run()
    {
      try
      {
        disconnectInactiveChannelAssignments();

        if (mAudioPacketQueue != null)
        {
          List<AudioPacket> packets = new ArrayList<AudioPacket>();

          mAudioPacketQueue.drainTo(packets);

          for (AudioPacket packet : packets)
          {
						/* Don't process any packet's marked as do not monitor */
            if (!packet.getAudioMetadata().isDoNotMonitor() &&
                packet.getType() == AudioPacket.Type.AUDIO)
            {
              AudioOutputConnection connection = getConnection(packet);

              if (connection != null)
              {
                connection.receive(packet);
              }
            }
          }
        }
      }
      catch (Exception e)
      {
        mLog.error("Encountered error while processing audio packets", e);
      }
    }
  }

  /**
   * Audio output connection manages a connection between a source and an audio
   * output and maintains current state information about the audio activity
   * received form the source.
   */
  public class AudioOutputConnection
  {
    private static final int DISCONNECTED = -1;

    private AudioOutput mAudioOutput;
    private int mPriority = 0;
    private int mSource = DISCONNECTED;

    public AudioOutputConnection(AudioOutput audioOutput)
    {
      mAudioOutput = audioOutput;
    }

    public void receive(AudioPacket packet)
    {
      if (packet.hasAudioMetadata() &&
          packet.getAudioMetadata().getSource() == mSource)
      {
        int priority = packet.getAudioMetadata().getPriority();

        if (mPriority != priority)
        {
          mPriority = priority;
          updateLowestPriorityAssignment();
        }

        if (mAudioOutput != null)
        {
          mAudioOutput.receive(packet);
        }
      }
      else
      {
        if (packet.hasAudioMetadata())
        {
          mLog.error("Received audio packet from channel source [" +
              packet.getAudioMetadata().getSource() +
              "] however this assignment is currently connected "
              + "to channel source [" + mSource + "]");
        }
        else
        {
          mLog.error("Received audio packet with no metadata - "
              + "cannot route audio packet");
        }
      }
    }

    /**
     * Terminates the audio output and prepares this connection for disposal
     */
    public void dispose()
    {
      mAudioOutput.dispose();
      mAudioOutput = null;
    }

    /**
     * Indicates if this assignment is currently disconnected from a channel source
     */
    public boolean isDisconnected()
    {
      return mSource == DISCONNECTED;
    }

    /**
     * Indicates if this assignment is currently connected to a channel source
     */
    public boolean isConnected()
    {
      return !isDisconnected();
    }

    /**
     * Connects this assignment to the indicated source so that audio
     * packets from this source can be sent to the audio output
     */
    public void connect(int source)
    {
      mSource = source;

      mAudioOutput.updateTimestamp();
    }

    /**
     * Currently connected source or -1 if disconnected
     */
    public int getSource()
    {
      return mSource;
    }

    /**
     * Disconnects this assignment from the source and prevents any audio
     * from being routed to the audio output until another source is assigned
     */
    public void disconnect()
    {
      mSource = DISCONNECTED;
      mPriority = 0;
    }

    /**
     * Indicates if audio output is current inactive, meaning that the
     * audio output hasn't recently processed any audio packets.
     */
    public boolean isInactive()
    {
      return (mAudioOutput.getLastActivityTimestamp() + AUDIO_TIMEOUT) < System.currentTimeMillis();
    }

    public int getPriority()
    {
      return mPriority;
    }
  }
}
