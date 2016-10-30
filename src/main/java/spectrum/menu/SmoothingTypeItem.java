package spectrum.menu;

import dsp.filter.smoothing.SmoothingFilter.SmoothingType;
import spectrum.SpectralDisplayAdjuster;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SmoothingTypeItem extends JCheckBoxMenuItem
{
  private static final long serialVersionUID = 1L;

  private SpectralDisplayAdjuster mAdjuster;
  private SmoothingType mSmoothingType;

  public SmoothingTypeItem(SpectralDisplayAdjuster adjuster, SmoothingType type)
  {
    super(type.name());

    mAdjuster = adjuster;
    mSmoothingType = type;

    setSelected(mAdjuster.getSmoothingType() == type);

    addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        mAdjuster.setSmoothingType(mSmoothingType);
      }
    });
  }
}
