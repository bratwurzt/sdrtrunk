package alias.action.clip;

import alias.Alias;
import alias.action.AliasActionType;
import alias.action.RecurringAction;
import message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;

public class ClipAction extends RecurringAction
{
  private final static Logger mLog = LoggerFactory.getLogger(ClipAction.class);

  private String mFilePath;

  @XmlTransient
  private Clip mClip;

  public ClipAction()
  {
  }

  @Override
  public AliasActionType getType()
  {
    return AliasActionType.CLIP;
  }

  public String getPath()
  {
    return mFilePath;
  }

  public void setPath(String path)
  {
    mFilePath = path;
  }

  @Override
  public void performAction(Alias alias, Message message)
  {
    try
    {
      play();
    }
    catch (Exception e)
    {
      mLog.error("Couldn't play audio clip", e);
    }
  }

  public void play() throws Exception
  {
    try
    {
      if (mFilePath != null)
      {
        if (mClip == null)
        {
          mClip = AudioSystem.getClip();

          AudioInputStream ais =
              AudioSystem.getAudioInputStream(new File(mFilePath));

          mClip.open(ais);
        }

        if (mClip.isRunning())
        {
          mClip.stop();
        }

        mClip.setFramePosition(0);

        mClip.start();
      }
    }
    catch (Exception e)
    {
      mClip = null;

      mLog.error("Error playing sound clip [" + mFilePath + "] - " + e.getMessage());

      throw e;
    }
  }


  @Override
  public String toString()
  {
    if (mFilePath == null)
    {
      return "Play Clip";
    }
    else
    {
      return "Play Clip: " + mFilePath;
    }
  }
}
