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
package module.log.config;

import controller.config.Configuration;
import module.log.EventLogType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "event_log_configuration")
public class EventLogConfiguration extends Configuration
{
  protected List<EventLogType> mLoggers = new ArrayList<EventLogType>();

  public EventLogConfiguration()
  {
  }

  @XmlElement(name = "logger")
  public List<EventLogType> getLoggers()
  {
    return mLoggers;
  }

  public void setLoggers(ArrayList<EventLogType> loggers)
  {
    mLoggers = loggers;
  }

  public void addLogger(EventLogType logger)
  {
    mLoggers.add(logger);
  }

  public void clear()
  {
    mLoggers.clear();
  }
}
