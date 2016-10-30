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
package module.decode.mpt1327;

import module.decode.DecoderType;
import module.decode.config.DecodeConfiguration;
import module.decode.mpt1327.MPT1327Decoder.Sync;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DecodeConfigMPT1327 extends DecodeConfiguration
{
  private String mChannelMapName;
  private Sync mSync = Sync.NORMAL;

  private int mCallTimeout = DEFAULT_CALL_TIMEOUT_SECONDS;
  private int mTrafficChannelPoolSize = TRAFFIC_CHANNEL_LIMIT_DEFAULT;

  public DecodeConfigMPT1327()
  {
    super(DecoderType.MPT1327);
  }

  @XmlElement
  public String getChannelMapName()
  {
    return mChannelMapName;
  }

  public void setChannelMapName(String name)
  {
    mChannelMapName = name;
  }

  @XmlAttribute(name = "sync")
  public Sync getSync()
  {
    return mSync;
  }

  public void setSync(Sync sync)
  {
    mSync = sync;
  }

  @XmlAttribute(name = "call_timeout")
  public int getCallTimeout()
  {
    return mCallTimeout;
  }

  /**
   * Sets the call timeout value in seconds ( 10 - 600 );
   *
   * @param timeout
   */
  public void setCallTimeout(int timeout)
  {
    if (CALL_TIMEOUT_MINIMUM <= timeout && timeout <= CALL_TIMEOUT_MAXIMUM)
    {
      mCallTimeout = timeout;
    }
    else
    {
      mCallTimeout = DEFAULT_CALL_TIMEOUT_SECONDS;
    }
  }

  @XmlAttribute(name = "traffic_channel_pool_size")
  public int getTrafficChannelPoolSize()
  {
    return mTrafficChannelPoolSize;
  }

  /**
   * Sets the traffic channel pool size which is the maximum number of
   * simultaneous traffic channels that can be allocated.
   * <p>
   * This limits the maximum calls so that busy systems won't cause more
   * traffic channels to be allocated than the decoder/software/host computer
   * can support.
   */
  public void setTrafficChannelPoolSize(int size)
  {
    mTrafficChannelPoolSize = size;
  }
}
