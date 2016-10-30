package source.tuner.frequency;

import sample.Listener;

public interface IFrequencyChangeProvider
{
  void setFrequencyChangeListener(Listener<FrequencyChangeEvent> listener);

  void removeFrequencyChangeListener();
}
