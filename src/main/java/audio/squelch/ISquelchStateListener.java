package audio.squelch;

import sample.Listener;

public interface ISquelchStateListener
{
  Listener<SquelchState> getSquelchStateListener();
}
