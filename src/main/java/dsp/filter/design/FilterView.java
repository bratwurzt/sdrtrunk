package dsp.filter.design;

import gui.control.CurveFittedAreaChart;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.jtransforms.fft.FloatFFT_1D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterView extends BorderPane
{
  private final static Logger mLog = LoggerFactory.getLogger(FilterView.class);

  private CurveFittedAreaChart mImpulseChart;
  private NumberAxis mImpulseXAxis;
  private NumberAxis mImpulseYAxis;
  private CurveFittedAreaChart mDFTChart;
  private NumberAxis mDFTXAxis;
  private NumberAxis mDFTYAxis;
  private static final int FFT_SIZE = 4096;
  private FloatFFT_1D mFFT = new FloatFFT_1D(FFT_SIZE);

  public FilterView(float[] taps)
  {
    init();

    if (taps != null)
    {
      setImpulseTaps(taps);

      float[] dft = new float[FFT_SIZE];
      System.arraycopy(taps, 0, dft, 0, taps.length);

      mFFT.realForward(dft);

      setDFTTaps(convertDFTToDecibel(dft, taps.length));
    }
  }

  private void init()
  {
    HBox hbox = new HBox();

    mImpulseXAxis = new NumberAxis(0.0, 0.5, 1.0);
    mImpulseYAxis = new NumberAxis(-0.2, 1.2, .1);

    mImpulseChart = new CurveFittedAreaChart(mImpulseXAxis, mImpulseYAxis);
    mImpulseChart.setLegendVisible(false);
    mImpulseChart.setHorizontalGridLinesVisible(false);
    mImpulseChart.setVerticalGridLinesVisible(false);
    mImpulseChart.setAlternativeColumnFillVisible(false);
    mImpulseChart.setAlternativeRowFillVisible(false);

    String css = FilterView.class.getResource("FilterView.css").toExternalForm();
    mImpulseChart.getStylesheets().add(css);

    hbox.getChildren().add(mImpulseChart);
    HBox.setHgrow(mImpulseChart, Priority.ALWAYS);

    mDFTXAxis = new NumberAxis(0.0, FFT_SIZE / 2, FFT_SIZE / 8);
    mDFTYAxis = new NumberAxis(-200.0, 10.0, 10.0);

    mDFTChart = new CurveFittedAreaChart(mDFTXAxis, mDFTYAxis);
    mDFTChart.setLegendVisible(false);
    mDFTChart.setHorizontalGridLinesVisible(false);
    mDFTChart.setVerticalGridLinesVisible(false);
    mDFTChart.setAlternativeColumnFillVisible(false);
    mDFTChart.setAlternativeRowFillVisible(false);
    mDFTChart.getStylesheets().add(css);

    hbox.getChildren().add(mDFTChart);
    HBox.setHgrow(mDFTChart, Priority.ALWAYS);

    setCenter(hbox);
  }

  public void setImpulseTaps(double[] taps)
  {
    float[] convertedTaps = new float[taps.length];

    for (int x = 0; x < taps.length; x++)
    {
      convertedTaps[x] = (float) taps[x];
    }

    setImpulseTaps(convertedTaps);
  }

  public void setImpulseTaps(float[] taps)
  {
    double smallest = 0.0;
    double largest = 0.0;

    final XYChart.Series<Number, Number> series = new XYChart.Series<>();

    for (int x = 0; x < taps.length; x++)
    {
      if (taps[x] < smallest)
      {
        smallest = taps[x];
      }

      if (taps[x] > largest)
      {
        largest = taps[x];
      }

      series.getData().add(new XYChart.Data<Number, Number>(x, taps[x]));
    }

    mImpulseXAxis.setLowerBound(0.0);
    mImpulseXAxis.setUpperBound(taps.length - 1);
    mImpulseXAxis.setTickUnit(1.0);

    double padding = Math.abs(largest - smallest) * .1;

    mImpulseYAxis.setLowerBound(smallest - padding);
    mImpulseYAxis.setUpperBound(largest + padding);
    mImpulseYAxis.setTickUnit(padding);

    ObservableList<Series<Number, Number>> list = mImpulseChart.getData();
    list.add(series);
  }

  public void setDFTTaps(float[] taps)
  {
    double smallest = 0.0;

    final XYChart.Series<Number, Number> series = new XYChart.Series<>();

    for (int x = 0; x < taps.length; x++)
    {
      if (taps[x] < smallest)
      {
        smallest = taps[x];
      }

      series.getData().add(new XYChart.Data<Number, Number>(x, taps[x]));
    }

    ObservableList<Series<Number, Number>> list = mDFTChart.getData();
    list.add(series);
  }

  public float[] convertDFTToDecibel(float[] dft, int tapLength)
  {
    float[] decibels = new float[dft.length / 2];

    int index = 0;

    for (int x = 0; x < decibels.length; x++)
    {
      index = x * 2;

      decibels[x] = 20.0f * (float) Math.log10(((dft[index] * dft[index]) +
          (dft[index + 1] * dft[index + 1])));
    }

    return decibels;
  }
}
