package jmbe.iface;

import javax.sound.sampled.AudioFormat;

public interface AudioConversionLibrary
{
  String getVersion();

  int getMajorVersion();

  int getMinorVersion();

  int getBuildVersion();

  boolean supports(String codec);

  AudioConverter getAudioConverter(String codec, AudioFormat output);
}
