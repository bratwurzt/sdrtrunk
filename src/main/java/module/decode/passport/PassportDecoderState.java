/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014,2015 Dennis Sheirer
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
package module.decode.passport;

import alias.Alias;
import alias.AliasList;
import audio.metadata.Metadata;
import audio.metadata.MetadataType;
import message.Message;
import module.decode.DecoderType;
import module.decode.event.CallEvent;
import module.decode.event.CallEvent.CallEventType;
import module.decode.state.ChangedAttribute;
import module.decode.state.DecoderState;
import module.decode.state.DecoderStateEvent;
import module.decode.state.DecoderStateEvent.Event;
import module.decode.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class PassportDecoderState extends DecoderState
{
  private final static Logger mLog =
      LoggerFactory.getLogger(PassportDecoderState.class);

  private HashSet<String> mTalkgroupsFirstHeard = new HashSet<String>();
  private TreeSet<String> mTalkgroups = new TreeSet<String>();
  private TreeSet<String> mMobileIDs = new TreeSet<String>();
  private HashMap<Integer, String> mSiteLCNs = new HashMap<Integer, String>();
  private HashMap<Integer, String> mNeighborLCNs = new HashMap<Integer, String>();

  private String mTalkgroup;
  private Alias mTalkgroupAlias;
  private String mMobileID;
  private Alias mMobileIDAlias;
  private int mChannelNumber;
  private int mSiteNumber;
  private PassportBand mSiteBand;
  private HashMap<Integer, String> mActiveCalls = new HashMap<Integer, String>();
  private long mFrequency;

  public PassportDecoderState(AliasList aliasList)
  {
    super(aliasList);
  }

  @Override
  public DecoderType getDecoderType()
  {
    return DecoderType.PASSPORT;
  }

  @Override
  public void start(ScheduledExecutorService executor)
  {
  }

  @Override
  public void init()
  {
  }

  @Override
  public void stop()
  {
  }

  private void logTalkgroup(String talkgroup)
  {
    if (mTalkgroupsFirstHeard.contains(talkgroup))
    {
      mTalkgroups.add(talkgroup);
    }
    else
    {
      mTalkgroupsFirstHeard.add(talkgroup);
    }
  }

  private PassportCallEvent getCurrentCallEvent()
  {
    return (PassportCallEvent) mCurrentCallEvent;
  }

  /**
   * Indicates if the talkgroup is different than the talkgroup specified in
   * the current call event
   */
  private boolean isDifferentTalkgroup(String talkgroup)
  {
    return talkgroup != null &&
        mCurrentCallEvent != null &&
        mCurrentCallEvent.getToID() != null &&
        !mCurrentCallEvent.getToID().contentEquals(talkgroup);
  }

  @Override
  public void receive(Message message)
  {
    if (message instanceof PassportMessage)
    {
      PassportMessage passport = (PassportMessage) message;

      if (passport.isValid())
      {
        switch (passport.getMessageType())
        {
          case CA_STRT:
            mSiteLCNs.put(passport.getLCN(),
                passport.getLCNFrequencyFormatted());

            String talkgroup =
                String.valueOf(passport.getTalkgroupID());

            logTalkgroup(talkgroup);

            if (mChannelNumber == 0)
            {
              mChannelNumber = passport.getLCN();
            }

	                    /* Call on this channel */
            if (passport.getLCN() == mChannelNumber)
            {
              setTalkgroup(talkgroup);

              setTalkgroupAlias(passport.getTalkgroupIDAlias());

              PassportCallEvent current = getCurrentCallEvent();

		                    /* If we're already in a call event, add the message
                         * to the current call event ... if false, then we
		                     * have a different call ... cleanup the old one. */
              if (current != null && isDifferentTalkgroup(talkgroup))
              {
                mCurrentCallEvent.end();
                mCurrentCallEvent = null;
              }

              if (mCurrentCallEvent == null)
              {
                mCurrentCallEvent = new PassportCallEvent
                    .Builder(CallEventType.CALL)
                    .aliasList(getAliasList())
                    .channel(String.valueOf(mChannelNumber))
                    .frequency(passport.getLCNFrequency())
                    .to(String.valueOf(passport.getTalkgroupID()))
                    .build();

                broadcast(mCurrentCallEvent);

                broadcast(new Metadata(MetadataType.TO,
                    mTalkgroup, mTalkgroupAlias, true));

                broadcast(new DecoderStateEvent(this,
                    Event.START, State.CALL));
              }
              else
              {
                broadcast(new DecoderStateEvent(this,
                    Event.CONTINUATION, State.CALL));
              }
            }
            else
            {
              //Call Detection
              int lcn = passport.getLCN();
              String tg = String.valueOf(passport.getTalkgroupID());

              if (!mActiveCalls.containsKey(lcn) ||
                  !mActiveCalls.get(lcn).contentEquals(tg))
              {
                mActiveCalls.put(lcn, tg);

                broadcast(new PassportCallEvent
                    .Builder(CallEventType.CALL_DETECT)
                    .aliasList(getAliasList())
                    .channel(String.valueOf(lcn))
                    .details("Site: " + passport.getSite())
                    .frequency(passport.getLCNFrequency())
                    .to(tg)
                    .build());
              }
            }
            break;
          case CA_ENDD:
            String endTalkgroup =
                String.valueOf(passport.getTalkgroupID());

            logTalkgroup(endTalkgroup);
            setTalkgroup(endTalkgroup);

            setTalkgroupAlias(passport.getTalkgroupIDAlias());

            broadcast(new DecoderStateEvent(this,
                Event.END, State.CALL));

            if (mCurrentCallEvent != null)
            {
              mCurrentCallEvent.end();
              broadcast(mCurrentCallEvent);
              mCurrentCallEvent = null;

              broadcast(new DecoderStateEvent(this, Event.RESET, State.FADE));
            }
            break;
          case DA_STRT:
            mSiteLCNs.put(passport.getLCN(),
                passport.getLCNFrequencyFormatted());

            String dataTalkgroup =
                String.valueOf(passport.getTalkgroupID());

            logTalkgroup(dataTalkgroup);

            if (mChannelNumber == 0)
            {
              mChannelNumber = passport.getLCN();
            }
		
		                 /* Data call on this channel */
            if (passport.getLCN() == mChannelNumber)
            {
              setTalkgroup(dataTalkgroup);

              setTalkgroupAlias(passport.getTalkgroupIDAlias());

              PassportCallEvent current = getCurrentCallEvent();
	
		                    /* If we're already in a call event, add the message
		                     * to the current call event ... if false, then we
		                     * have a different call ... cleanup the old one. */
              if (current != null && isDifferentTalkgroup(dataTalkgroup))
              {
                mCurrentCallEvent.end();
                mCurrentCallEvent = null;
              }

              if (mCurrentCallEvent == null)
              {
                mCurrentCallEvent = new PassportCallEvent
                    .Builder(CallEventType.DATA_CALL)
                    .aliasList(getAliasList())
                    .channel(String.valueOf(mChannelNumber))
                    .frequency(passport.getLCNFrequency())
                    .to(String.valueOf(passport.getTalkgroupID()))
                    .build();

                broadcast(mCurrentCallEvent);

                broadcast(new Metadata(MetadataType.TO,
                    mTalkgroup, mTalkgroupAlias, true));

                broadcast(new DecoderStateEvent(this,
                    Event.START, State.DATA));
              }
              else
              {
                broadcast(new DecoderStateEvent(this,
                    Event.CONTINUATION, State.DATA));
              }
            }
            else
            {
              //Call Detection
              int lcn = passport.getLCN();
              String tg = String.valueOf(passport.getTalkgroupID());

              if (!mActiveCalls.containsKey(lcn) ||
                  !mActiveCalls.get(lcn).contentEquals(tg))
              {
                mActiveCalls.put(lcn, tg);

                broadcast(new PassportCallEvent
                    .Builder(CallEventType.DATA_CALL)
                    .aliasList(getAliasList())
                    .channel(String.valueOf(lcn))
                    .details("Site: " + passport.getSite())
                    .frequency(passport.getLCNFrequency())
                    .to(tg)
                    .build());
              }
            }
            break;
          case DA_ENDD:
            String dataEndTalkgroup =
                String.valueOf(passport.getTalkgroupID());

            logTalkgroup(dataEndTalkgroup);
            setTalkgroup(dataEndTalkgroup);

            setTalkgroupAlias(passport.getTalkgroupIDAlias());

            broadcast(new DecoderStateEvent(this,
                Event.END, State.DATA));

            if (mCurrentCallEvent != null)
            {
              mCurrentCallEvent.end();
              broadcast(mCurrentCallEvent);
              mCurrentCallEvent = null;

              broadcast(new DecoderStateEvent(this, Event.RESET, State.FADE));
            }
            break;
          case ID_RDIO:
            String min = passport.getMobileID();

            if (min != null)
            {
              mMobileIDs.add(min);
            }

            setMobileID(min);
            setMobileIDAlias(passport.getMobileIDAlias());

            broadcast(new Metadata(MetadataType.FROM,
                mMobileID, mMobileIDAlias, true));

            final CallEvent current = getCurrentCallEvent();

            if (current != null)
            {
              current.setFromID(min);
              broadcast(current);

            }
            break;
          case RA_REGI:
            if (mCurrentCallEvent == null ||
                mCurrentCallEvent.getCallEventType() != CallEventType.REGISTER)
            {
              mCurrentCallEvent = new PassportCallEvent
                  .Builder(CallEventType.REGISTER)
                  .aliasList(getAliasList())
                  .channel(String.valueOf(passport.getLCN()))
                  .frequency(passport.getLCNFrequency())
                  .to(passport.getToID())
                  .build();

              broadcast(mCurrentCallEvent);

              broadcast(new DecoderStateEvent(this,
                  Event.START, State.DATA));
            }
            else
            {
              broadcast(new DecoderStateEvent(this,
                  Event.CONTINUATION, State.DATA));
            }
            break;
          case SY_IDLE:
            if (passport.getFree() != 0)
            {
              mNeighborLCNs.put(passport.getFree(),
                  passport.getFreeFrequencyFormatted());
            }
            PassportBand band = passport.getSiteBand();

            if (mSiteBand == null || mSiteBand != band)
            {
              mSiteBand = band;

              mSiteNumber = passport.getSite();

              mChannelNumber = mSiteBand.getChannel(mFrequency);

              broadcast(ChangedAttribute.CHANNEL_NUMBER);
            }
            break;
          default:
            break;
        }
      }
    }
  }

  public static String formatTalkgroup(String talkgroup)
  {
    StringBuilder sb = new StringBuilder();

    if (talkgroup.length() == 6)
    {
      sb.append(talkgroup.substring(0, 1));
      sb.append("-");
      sb.append(talkgroup.substring(1, 3));
      sb.append("-");
      sb.append(talkgroup.substring(3, 6));

      return sb.toString();
    }
    else
    {
      return talkgroup;
    }
  }

  @Override
  public String getActivitySummary()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("Activity Summary\n");
    sb.append("Decoder:\tPassport\n\n");
    sb.append("Site Channels\n");

    if (mSiteLCNs.isEmpty())
    {
      sb.append("  None\n");
    }
    else
    {
      ArrayList<Integer> channels = new ArrayList<>(mSiteLCNs.keySet());
      Collections.sort(channels);

      for (Integer channel : channels)
      {
        sb.append("  " + channel);
        sb.append("\t" + mSiteLCNs.get(channel));
        sb.append("\n");
      }
    }

    sb.append("\nNeighbor Channels\n");

    if (mNeighborLCNs.isEmpty())
    {
      sb.append("  None\n");
    }
    else
    {
      ArrayList<Integer> channels = new ArrayList<>(mNeighborLCNs.keySet());
      Collections.sort(channels);

      for (Integer channel : channels)
      {
        sb.append("  " + channel);
        sb.append("\t" + mNeighborLCNs.get(channel));
        sb.append("\n");
      }
    }

    sb.append("\nTalkgroups\n");

    if (mTalkgroups.isEmpty())
    {
      sb.append("  None\n");
    }
    else
    {
      Iterator<String> it = mTalkgroups.iterator();

      while (it.hasNext())
      {
        String tgid = it.next();

        sb.append("  ");
        sb.append(tgid);
        sb.append(" ");

        if (hasAliasList())
        {
          Alias alias = getAliasList().getTalkgroupAlias(tgid);

          if (alias != null)
          {
            sb.append(alias.getName());
          }
        }

        sb.append("\n");
      }
    }

    sb.append("\nMobile ID Numbers\n");

    if (mMobileIDs.isEmpty())
    {
      sb.append("  None\n");
    }
    else
    {
      Iterator<String> it = mMobileIDs.iterator();

      while (it.hasNext())
      {
        String min = it.next();

        sb.append("  ");
        sb.append(min);
        sb.append(" ");

        if (hasAliasList())
        {
          Alias alias = getAliasList().getMobileIDNumberAlias(min);

          if (alias != null)
          {
            sb.append(alias.getName());
          }
        }

        sb.append("\n");
      }
    }

    return sb.toString();
  }

  public void reset()
  {
    mTalkgroupsFirstHeard.clear();
    mTalkgroups.clear();
    mMobileIDs.clear();
    mSiteLCNs.clear();
    mNeighborLCNs.clear();

    resetState();
  }

  private void resetState()
  {
    setTalkgroup(null);
    setTalkgroupAlias(null);

    setMobileID(null);
    setMobileIDAlias(null);
  }

  public int getChannelNumber()
  {
    return mChannelNumber;
  }

  public int getSiteNumber()
  {
    return mSiteNumber;
  }

  public String getTalkgroup()
  {
    return mTalkgroup;
  }

  public void setTalkgroup(String talkgroup)
  {
    mTalkgroup = talkgroup;
    broadcast(ChangedAttribute.TO_TALKGROUP);
  }

  public Alias getTalkgroupAlias()
  {
    return mTalkgroupAlias;
  }

  public void setTalkgroupAlias(Alias alias)
  {
    mTalkgroupAlias = alias;
    broadcast(ChangedAttribute.TO_TALKGROUP_ALIAS);
  }

  public String getMobileID()
  {
    return mMobileID;
  }

  public void setMobileID(String id)
  {
    mMobileID = id;
    broadcast(ChangedAttribute.FROM_TALKGROUP);
  }

  public Alias getMobileIDAlias()
  {
    return mMobileIDAlias;
  }

  public void setMobileIDAlias(Alias alias)
  {
    mMobileIDAlias = alias;
    broadcast(ChangedAttribute.FROM_TALKGROUP_ALIAS);
  }

  @Override
  public void receiveDecoderStateEvent(DecoderStateEvent event)
  {
    switch (event.getEvent())
    {
      case RESET:
        resetState();
        break;
      case SOURCE_FREQUENCY:
        mFrequency = event.getFrequency();
        break;
      default:
        break;
    }
  }
}
