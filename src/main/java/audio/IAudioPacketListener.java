package audio;

import sample.Listener;

public interface IAudioPacketListener
{
  Listener<AudioPacket> getAudioPacketListener();
}
