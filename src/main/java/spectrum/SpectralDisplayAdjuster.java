package spectrum;

import dsp.filter.smoothing.SmoothingFilter.SmoothingType;

public interface SpectralDisplayAdjuster
{
  /**
   * Gets the averaging value indicating the number of FFT results that are
   * averaged to produce each FFT results output.
   *
   * @return averaging value ( 2 - 50 )
   */
  int getAveraging();

  void setAveraging(int averaging);

  /**
   * Sets the smoothing filter averaging window width.
   * <p>
   * Valid values are odd in the range 3 - 29
   */
  int getSmoothing();

  void setSmoothing(int smoothing);

  /**
   * Sets the type of smoothing filter to use.
   */
  SmoothingType getSmoothingType();

  void setSmoothingType(SmoothingType type);

}
