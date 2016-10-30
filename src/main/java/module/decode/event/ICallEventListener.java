package module.decode.event;

import sample.Listener;

public interface ICallEventListener
{
  Listener<CallEvent> getCallEventListener();
}
