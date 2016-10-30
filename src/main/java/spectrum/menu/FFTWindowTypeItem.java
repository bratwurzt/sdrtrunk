package spectrum.menu;

import dsp.filter.Window.WindowType;
import spectrum.DFTProcessor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FFTWindowTypeItem extends JCheckBoxMenuItem
{
  private static final long serialVersionUID = 1L;

  private DFTProcessor mDFTProcessor;
  private WindowType mWindowType;

  public FFTWindowTypeItem(DFTProcessor processor, WindowType windowType)
  {
    super(windowType.toString());

    mDFTProcessor = processor;
    mWindowType = windowType;

    if (processor.getWindowType() == mWindowType)
    {
      setSelected(true);
    }

    addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        mDFTProcessor.setWindowType(mWindowType);
      }
    });
  }
}

