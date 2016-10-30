package module.decode.state;

import sample.Listener;

public interface IDecoderStateEventListener
{
  Listener<DecoderStateEvent> getDecoderStateListener();
}
