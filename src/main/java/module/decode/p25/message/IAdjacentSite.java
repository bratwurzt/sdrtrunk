package module.decode.p25.message;

/**
 * Interface for adjacent site (ie neighbor) messages
 */
public interface IAdjacentSite
{
  String getUniqueID();

  String getRFSS();

  String getSystemID();

  String getSiteID();

  String getLRA();

  String getSystemServiceClass();

  String getDownlinkChannel();

  long getDownlinkFrequency();

  String getUplinkChannel();

  long getUplinkFrequency();
}
