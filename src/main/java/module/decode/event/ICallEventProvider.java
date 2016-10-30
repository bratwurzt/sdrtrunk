package module.decode.event;

import sample.Listener;

public interface ICallEventProvider
{
  void addCallEventListener(Listener<CallEvent> listener);

  void removeCallEventListener(Listener<CallEvent> listener);
}
