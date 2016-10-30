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
package alias.id.mpt1327;

import alias.id.AliasID;
import alias.id.AliasIDType;

import javax.xml.bind.annotation.XmlAttribute;

public class MPT1327ID extends AliasID
{
  private String mID;

  public MPT1327ID()
  {
  }

  @XmlAttribute
  public String getIdent()
  {
    return mID;
  }

  public void setIdent(String ident)
  {
    mID = ident;
  }

  @Override
  public boolean isValid()
  {
    return mID != null;
  }

  public String toString()
  {
    return "MPT-1327: " + mID;
  }

  @Override
  public boolean matches(AliasID id)
  {
    boolean retVal = false;

    if (mID != null && id instanceof MPT1327ID)
    {
      MPT1327ID tgid = (MPT1327ID) id;

      //Create a pattern - replace * wildcards with regex single-char wildcard
      String pattern = mID.replace("*", ".?");

      retVal = tgid.getIdent().matches(pattern);
    }

    return retVal;
  }

  @Override
  public AliasIDType getType()
  {
    return AliasIDType.MPT1327;
  }
}
