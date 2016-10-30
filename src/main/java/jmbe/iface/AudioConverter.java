package jmbe.iface;

import javax.sound.sampled.AudioFormat;

/**
 * Audio converter interface.  Defines methods for a stand-alone converter that
 * can convert byte data from one audio format into byte data of another.
 */
public interface AudioConverter
{
  /**
   * Name of the CODEC for this audio converter
   */
  String getCodecName();

  /**
   * Converts frameDate into the audio format specified by the
   * getConvertedAudioFormat() method
   * <p>
   * This method is deprecated in version 0.3.0 in favor of returning
   * primitive (float) samples.
   *
   * @see jmbe.iface.AudioConverer.decode()
   */
  @Deprecated
  byte[] convert(byte[] frameDate);

  /**
   * Converts frameData into the audio format specified by the
   * getConvertedAudioFormat() method
   */
  float[] decode(byte[] frameData);

  /**
   * Output (converted) audio format provided by this audio converter.
   */
  AudioFormat getConvertedAudioFormat();
}
