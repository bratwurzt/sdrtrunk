package module.decode.state;

import sample.Listener;

public interface IDecoderStateEventProvider
{
  void setDecoderStateListener(Listener<DecoderStateEvent> listener);

  void removeDecoderStateListener();
}
