package message;

import sample.Listener;

public interface IMessageListener
{
  Listener<Message> getMessageListener();
}
