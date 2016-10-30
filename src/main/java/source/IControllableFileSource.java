package source;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Defines a controllable source that can be manually controlled for stepping
 * through the file.
 */
public interface IControllableFileSource
{
  /**
   * Opens the file source
   */
  void open() throws IOException, UnsupportedAudioFileException;

  void close() throws IOException;

  File getFile();

  void next(int frames) throws IOException;

  void next(int frames, boolean broadcast) throws IOException;

  long getFrameCount() throws IOException;

  void setListener(IFrameLocationListener listener);

  void removeListener(IFrameLocationListener listener);
}
