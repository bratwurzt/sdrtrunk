package audio;

import sample.Listener;

public interface IAudioPacketProvider
{
  void setAudioPacketListener(Listener<AudioPacket> listener);

  void removeAudioPacketListener();
}
