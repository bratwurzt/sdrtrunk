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
package audio.metadata;

import alias.Alias;

public class Metadata
{
  private MetadataType mMetadataType;
  private String mValue;
  private Alias mValueAlias;
  private boolean mTemporal;

  /**
   * Constucts a metadata object with the temporal settings.  Temporal
   * indicates that this metadata will be removed at the end of a reset
   * event, or call end event.
   */
  public Metadata(MetadataType metadataType, String value, Alias alias, boolean temporal)
  {
    mMetadataType = metadataType;
    mValue = value;
    mValueAlias = alias;
    mTemporal = temporal;
  }

  /**
   * Constucts a metadata object with the temporal settings.  Temporal
   * indicates that this metadata will be removed at the end of a reset
   * event, or call end event.
   */
  public Metadata(MetadataType metadataType, String value, boolean temporal)
  {
    this(metadataType, value, null, temporal);
  }

  /**
   * Constucts a non-temporal metadata object
   */
  public Metadata(MetadataType metadataType, String value, Alias alias)
  {
    this(metadataType, value, alias, false);
  }

  /**
   * Constucts a non-temporal metadata object
   */
  public Metadata(MetadataType metadataType, String value)
  {
    this(metadataType, value, null, false);
  }

  public MetadataType getMetadataType()
  {
    return mMetadataType;
  }

  public String getKey()
  {
    return mMetadataType.getLabel();
  }

  public String getValue()
  {
    return mValue;
  }

  public Alias getAlias()
  {
    return mValueAlias;
  }

  public boolean hasAlias()
  {
    return mValueAlias != null;
  }

  /**
   * Indicates that this piece of metadata is only valid for the duration of
   * a call event and should be removed during a reset
   */
  public boolean isTemporal()
  {
    return mTemporal;
  }

  public boolean isReset()
  {
    return false;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(mMetadataType.getLabel());
    sb.append(":");
    sb.append(mValue);
    sb.append(" Alias:");
    sb.append(mValueAlias == null ? "null" : getAlias().getName());
    sb.append(" [");
    sb.append(mMetadataType.name());
    sb.append("] temporal:");
    sb.append(mTemporal);

    return sb.toString();
  }

  public boolean equals(Metadata other)
  {
    if (mMetadataType != other.getMetadataType())
    {
      return false;
    }

    if (mTemporal != other.isTemporal())
    {
      return false;
    }

    if (mValue != null && other.getValue() != null &&
        !mValue.contentEquals(other.getValue()))
    {
      return false;
    }

    if ((mValue == null && other.getValue() != null) ||
        (mValue != null && other.getValue() == null))
    {
      return false;
    }

    if (mValueAlias != null && other.getAlias() != null &&
        !mValueAlias.getName().contentEquals(other.getAlias().getName()))
    {
      return false;
    }

    return !((mValueAlias == null && other.getAlias() != null) ||
        (mValueAlias != null && other.getAlias() == null));

  }
}
