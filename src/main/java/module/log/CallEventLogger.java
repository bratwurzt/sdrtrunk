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
package module.log;

import module.decode.event.CallEvent;
import module.decode.event.ICallEventListener;
import sample.Listener;

import java.nio.file.Path;

public class CallEventLogger extends EventLogger implements ICallEventListener,
    Listener<CallEvent>
{
  public CallEventLogger(Path logDirectory, String fileNameSuffix)
  {
    super(logDirectory, fileNameSuffix);
  }

  @Override
  public void receive(CallEvent callEvent)
  {
    write(callEvent.toCSV());
  }

  @Override
  public String getHeader()
  {
    return CallEvent.getCSVHeader();
  }

  @Override
  public Listener<CallEvent> getCallEventListener()
  {
    return this;
  }

  @Override
  public void dispose()
  {
    super.stop();
  }

  @Override
  public void reset()
  {
  }
}
