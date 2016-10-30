package module.decode.p25.message;

public interface IBandIdentifier
{

  /**
   * (Band) Identifier
   */
  int getIdentifier();

  /**
   * Channel spacing in hertz
   */
  long getChannelSpacing();

  /**
   * Base frequency in hertz
   */
  long getBaseFrequency();


  /**
   * Channel bandwidth in hertz
   */
  int getBandwidth();

  /**
   * Transmit offset in hertz
   */
  long getTransmitOffset();

}