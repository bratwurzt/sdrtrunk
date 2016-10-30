package message;

import sample.Listener;

public interface IMessageProvider
{
  void setMessageListener(Listener<Message> listener);

  void removeMessageListener();
}
