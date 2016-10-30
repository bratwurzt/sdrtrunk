package audio;

import audio.output.AudioOutput;
import sample.Listener;
import source.mixer.MixerChannelConfiguration;

import java.util.List;

/**
 * Interface for controlling one or more audio channels
 */
public interface IAudioController
{
  /* Current Mixer and MixerChannel configuration */
  void setMixerChannelConfiguration(MixerChannelConfiguration entry) throws AudioException;

  MixerChannelConfiguration getMixerChannelConfiguration() throws AudioException;

  /* Audio Output(s) */
  List<AudioOutput> getAudioOutputs();

  /* Controller Audio Event Listener */
  void addControllerListener(Listener<AudioEvent> listener);

  void removeControllerListener(Listener<AudioEvent> listener);
}
