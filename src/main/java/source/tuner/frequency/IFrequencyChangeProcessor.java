package source.tuner.frequency;

import source.SourceException;

public interface IFrequencyChangeProcessor
{
  void frequencyChanged(FrequencyChangeEvent event) throws SourceException;
}
