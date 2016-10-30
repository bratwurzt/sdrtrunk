package dsp.filter.design;

import dsp.filter.fir.FIRFilterSpecification;
import dsp.filter.fir.remez.RemezFIRFilterDesigner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterViewer extends Application
{
  private final static Logger mLog = LoggerFactory.getLogger(FilterViewer.class);

  /**
   * Developer tool to visualize filter designs
   */
  public FilterViewer()
  {
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    Scene scene = new Scene(new FilterView(getFilter()));

    primaryStage.setTitle("Filter Viewer");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }

  /**
   * Provides the filter to visualize.  Modify this method to visualize your filter design.
   */
  private float[] getFilter()
  {
//		FIRFilterSpecification specification = FIRFilterSpecification.lowPassBuilder()
//		        .sampleRate( 48000 )
//		        .gridDensity( 16 )
//                .passBandCutoff( 3000 )
//		        .passBandAmplitude( 1.0 )
//		        .passBandRipple( 0.01 )
//		        .stopBandStart( 4000 )
//		        .stopBandAmplitude( 0.0 )
//		        .stopBandRipple( 0.027 )
//		        .build();

    FIRFilterSpecification specification = FIRFilterSpecification.highPassBuilder()
        .sampleRate(8000)
        .stopBandCutoff(250)
        .stopBandAmplitude(0.0)
        .stopBandRipple(0.01)
        .passBandStart(300)
        .passBandAmplitude(1.0)
        .passBandRipple(0.01)
        .build();

//		FIRFilterSpecification specification = FIRFilterSpecification.bandPassBuilder()
//	        .sampleRate( 48000 )
//	        .stopFrequency1( 150 )
//	        .passFrequencyBegin( 200 )
//	        .passFrequencyEnd( 3950 )
//	        .stopFrequency2( 4000 )
//	        .stopRipple( 0.0005 )
//	        .passRipple( 0.003)
//	        .build();

    float[] taps = null;

//		taps = FilterFactory.getLowPass( 16000, 2400, 73, WindowType.HANNING );

    try
    {
      RemezFIRFilterDesigner designer = new RemezFIRFilterDesigner(specification);

      if (designer.isValid())
      {
        taps = designer.getImpulseResponse();
      }
    }
    catch (FilterDesignException fde)
    {
      mLog.error("Filter design error", fde);
    }

    return taps;
  }
}
