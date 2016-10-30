package source.tuner;

import source.SourceException;

import java.util.concurrent.RejectedExecutionException;

public interface TunerChannelProvider
{
  /**
   * Returns a tuner frequency channel source, tuned to the correct frequency
   *
   * @param frequency - desired frequency
   * @return - source for 48k sample rate
   */
  TunerChannelSource getChannel(TunerChannel channel)
      throws RejectedExecutionException, SourceException;

  /**
   * Releases the tuned channel resources
   *
   * @param channel - previously obtained tuner channel
   */
  void releaseChannel(TunerChannelSource source);


}
