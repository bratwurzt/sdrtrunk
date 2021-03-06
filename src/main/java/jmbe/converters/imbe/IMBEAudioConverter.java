package jmbe.converters.imbe;

/*******************************************************************************
 * jmbe - Java MBE Library
 * Copyright (C) 2015 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/

import jmbe.audio.JMBEAudioFormat;
import jmbe.iface.AudioConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

public class IMBEAudioConverter implements AudioConverter
{
  private final static Logger mLog =
      LoggerFactory.getLogger(IMBEAudioConverter.class);

  public static final int IMBE_FRAME_LENGTH = 18;

  public static final String CODEC_NAME = "IMBE";

  private static final boolean UPSAMPLE_TO_48KHZ = true;

  private IMBESynthesizer mSynthesizer;

  public IMBEAudioConverter(float sampleRate)
  {
    if (sampleRate == JMBEAudioFormat.PCM_8KHZ_RATE)
    {
      mSynthesizer = new IMBESynthesizer();
    }
    else if (sampleRate == JMBEAudioFormat.PCM_48KHZ_RATE)
    {
      mSynthesizer = new IMBESynthesizer(UPSAMPLE_TO_48KHZ);
    }
    else
    {
      throw new IllegalArgumentException("Sample rate [" + sampleRate + "] is not supported");
    }
  }

  public void dispose()
  {
  }

  /**
   * Converts the imbe frame data into PCM audio samples a 8kHz rate or if
   * upsampling, 48 kHz audio rate.
   */
  public float[] decode(byte[] frameData)
  {
    IMBEFrame frame = new IMBEFrame(frameData);

    return mSynthesizer.getAudio(frame);
  }

  /**
   * This method is deprecated in version 0.3.0.  Use the decode() method
   * instead.
   */
  @Override
  @Deprecated
  public byte[] convert(byte[] frameData)
  {
    IMBEFrame frame = new IMBEFrame(frameData);

    ByteBuffer converted = mSynthesizer.getConvertedAudio(frame);

    return converted.array();
  }

  /**
   * CODEC Name
   */
  @Override
  public String getCodecName()
  {
    return CODEC_NAME;
  }

  @Override
  public AudioFormat getConvertedAudioFormat()
  {
    return mSynthesizer.getOutputAudioFormat();
  }
}
