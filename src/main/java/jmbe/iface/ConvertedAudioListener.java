package jmbe.iface;

public interface ConvertedAudioListener
{
  /**
   * Converted audio provided by the audio converter
   */
  void receive(byte[] convertedAudio);
}
