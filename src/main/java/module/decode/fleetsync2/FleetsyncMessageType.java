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

public enum FleetsyncMessageType
{
  ACKNOWLEDGE("ACK"),
  ANI("ANI"),
  EMERGENCY("EMERG"),
  GPS("GPS"),
  LONE_WORKER_EMERGENCY("LONE WORKER"),
  PAGING("PAGE"),
  STATUS("STATUS"),
  UNKNOWN("UNK");

  private String mLabel;

  FleetsyncMessageType(String label)
  {
    mLabel = label;
  }

  public String getLabel()
  {
    return mLabel;
  }
}
