package controller.channel;

import sample.Listener;

public interface IChannelEventListener
{
  Listener<ChannelEvent> getChannelEventListener();
}
