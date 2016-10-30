/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014 Dennis Sheirer
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
package module.decode.fleetsync2;

import alias.Alias;
import alias.AliasList;
import module.decode.DecoderType;
import module.decode.event.CallEvent;

public class FleetsyncCallEvent extends CallEvent
{
  private long mFrequency = 0;

  public FleetsyncCallEvent(CallEventType callEventType,
                            AliasList aliasList,
                            String fromID,
                            String toID,
                            String details,
                            long frequency)
  {
    super(DecoderType.FLEETSYNC2, callEventType,
        aliasList, fromID, toID, details);

    mFrequency = frequency;
  }

  private Alias getAlias(String ident)
  {
    if (hasAliasList())
    {
      return getAliasList().getFleetsyncAlias(ident);
    }

    return null;
  }

  @Override
  public Alias getFromIDAlias()
  {
    return getAlias(getFromID());
  }

  @Override
  public Alias getToIDAlias()
  {
    return getAlias(getToID());
  }

  @Override
  public String getChannel()
  {
    return null;
  }

  @Override
  public long getFrequency()
  {
    return mFrequency;
  }

  public static class Builder
  {
    /* Required parameters */
    private CallEventType mCallEventType;

    /* Optional parameters */
    private AliasList mAliasList;
    private String mFromID;
    private String mToID;
    private String mDetails;
    private long mFrequency;

    public Builder(CallEventType callEventType)
    {
      mCallEventType = callEventType;
    }

    public Builder aliasList(AliasList aliasList)
    {
      mAliasList = aliasList;
      return this;
    }

    public Builder frequency(long frequency)
    {
      mFrequency = frequency;
      return this;
    }

    public Builder from(String val)
    {
      mFromID = val;
      return this;
    }

    public Builder details(String details)
    {
      mDetails = details;
      return this;
    }

    public Builder to(String toID)
    {
      mToID = toID;
      return this;
    }

    public FleetsyncCallEvent build()
    {
      return new FleetsyncCallEvent(this);
    }
  }

  /**
   * Private constructor for the builder
   */
  private FleetsyncCallEvent(Builder builder)
  {
    this(builder.mCallEventType,
        builder.mAliasList,
        builder.mFromID,
        builder.mToID,
        builder.mDetails,
        builder.mFrequency);
  }

  public static FleetsyncCallEvent getFleetsync2Event(
      FleetsyncMessage message, long frequency)
  {
    CallEventType type = CallEventType.UNKNOWN;
    StringBuilder sbDetails = new StringBuilder();
    sbDetails.append("Fleetsync ");

    switch (message.getMessageType())
    {
      case ACKNOWLEDGE:
        type = CallEventType.RESPONSE;
        sbDetails.append("ACK");
        break;
      case ANI:
        type = CallEventType.ID_ANI;
        break;
      case EMERGENCY:
        type = CallEventType.EMERGENCY;
        break;
      case GPS:
        type = CallEventType.GPS;
        sbDetails.append(message.getGPSLocation());
        break;
      case LONE_WORKER_EMERGENCY:
        type = CallEventType.EMERGENCY;
        sbDetails.append("LONE WORKER EMERGENCY ALERT");
        break;
      case PAGING:
        type = CallEventType.PAGE;
        break;
      case STATUS:
        type = CallEventType.STATUS;
        sbDetails.append("Status: ");
        sbDetails.append(message.getStatus());

        if (message.getStatusAlias() != null)
        {
          sbDetails.append("/");
          sbDetails.append(message.getStatusAlias().getName());
        }
        break;
      default:
        break;
    }

    if (type == CallEventType.ID_ANI ||
        type == CallEventType.EMERGENCY)
    {
      return new FleetsyncCallEvent.Builder(type)
          .details(sbDetails.toString())
          .frequency(frequency)
          .from(message.getFromID())
          .build();
    }
    else
    {
      return new FleetsyncCallEvent.Builder(type)
          .details(sbDetails.toString())
          .frequency(frequency)
          .from(message.getFromID())
          .to(message.getToID())
          .build();
    }
  }
}
