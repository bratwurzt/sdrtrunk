package controller.channel;

import sample.Listener;

public interface IChannelEventProvider
{
  void setChannelEventListener(Listener<ChannelEvent> listener);

  void removeChannelEventListener();
}
