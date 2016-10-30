package audio.squelch;

import sample.Listener;

public interface ISquelchStateProvider
{
  void setSquelchStateListener(Listener<SquelchState> listener);

  void removeSquelchStateListener();
}
