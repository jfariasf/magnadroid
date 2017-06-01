package com.javierarias.magnadroid.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.javierarias.magnadroid.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Javier Felipe Arias
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class HistoryPlot extends mainFragment {

    private static final int HISTORY_SIZE = 30;            // number of points to plot in history
    private SeekBar seekBar;
    private TextView xAxis;
    private TextView yAxis;
    private TextView zAxis;
    private XYPlot aprLevelsPlot = null;
    private SimpleXYSeries aprLevelsSeriesX = null;
    private SimpleXYSeries aprLevelsSeriesY = null;
    private SimpleXYSeries aprLevelsSeriesZ = null;
    private SimpleXYSeries aprLevelsSeriesStrength = null;
    private EventBus bus;
    private TextView strengthBar;
    private TextView accuracyText;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View plotLayout = inflater.inflate(R.layout.history_plot, container, false);
        aprLevelsPlot = (XYPlot) plotLayout.findViewById(R.id.aprHistoryPlot);
        aprLevelsPlot.getGraph().getGridBackgroundPaint().setColor(Color.BLACK);
        aprLevelsPlot.getGraph().getDomainGridLinePaint().setAlpha(0);
        aprLevelsPlot.getGraph().getDomainOriginLinePaint().setAlpha(0);
        accuracyText = (TextView) plotLayout.findViewById(R.id.accuracyHistory);

        aprLevelsSeriesX = new SimpleXYSeries("X Axis");
        aprLevelsSeriesX.useImplicitXVals();
        aprLevelsSeriesY = new SimpleXYSeries("Y Axis");
        aprLevelsSeriesY.useImplicitXVals();
        aprLevelsSeriesZ = new SimpleXYSeries("Z Axis");
        aprLevelsSeriesZ.useImplicitXVals();
        aprLevelsSeriesStrength = new SimpleXYSeries("Strength");
        aprLevelsSeriesStrength.useImplicitXVals();
        aprLevelsPlot.setRangeBoundaries(lowBoundary, highBoundary, BoundaryMode.FIXED);
        aprLevelsPlot.setDomainBoundaries(0, 30, BoundaryMode.FIXED);

        seekBar = (SeekBar) plotLayout.findViewById(R.id.sensitivityHistory);
        strengthBar = (TextView) plotLayout.findViewById(R.id.strengthHistory);

        seekBar.setOnSeekBarChangeListener(this);
        yAxis = (TextView) plotLayout.findViewById(R.id.yAxisHistory);
        xAxis = (TextView) plotLayout.findViewById(R.id.xAxisHistory);
        zAxis = (TextView) plotLayout.findViewById(R.id.zAxisHistory);

        aprLevelsPlot.addSeries(aprLevelsSeriesX, new LineAndPointFormatter(Color.parseColor("#5FA9DD"), Color.TRANSPARENT, Color.TRANSPARENT, null));
        aprLevelsPlot.addSeries(aprLevelsSeriesY, new LineAndPointFormatter(Color.parseColor("#DEDC16"), Color.TRANSPARENT, Color.TRANSPARENT, null));
        aprLevelsPlot.addSeries(aprLevelsSeriesZ, new LineAndPointFormatter(Color.parseColor("#D80000"), Color.TRANSPARENT, Color.TRANSPARENT, null));
        aprLevelsPlot.addSeries(aprLevelsSeriesStrength, new LineAndPointFormatter(Color.GREEN, Color.TRANSPARENT, Color.TRANSPARENT, null));

        aprLevelsPlot.setDomainStepValue(5);
        aprLevelsPlot.setDomainLabel("Axis");
        aprLevelsPlot.setRangeLabel("micro-Tesla (uT)");
        aprLevelsPlot.setLayerType(View.LAYER_TYPE_NONE, null);
        seekBar.setProgress(sliderValue);
        return plotLayout;
    }

    @Override
    public void updateSensitivity(int progress) {
        sliderValue = progress;

        aprLevelsPlot.setRangeBoundaries(lowBoundary + progress, highBoundary - progress, BoundaryMode.FIXED);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateSensitivity(progress);
    }

    @Override
    public void updateSignal(Number[] sensor) {
        sensor = new Number[]{sensor[0], sensor[1], sensor[2], Math.sqrt(Math.pow(sensor[0].doubleValue(), 2) + Math.pow(sensor[1].doubleValue(), 2) + Math.pow(sensor[2].doubleValue(), 2))};
        sensor = updateSensorValues(sensor);

        xAxis.setText(String.format(Locale.US, "%.2f", sensor[0].doubleValue()));
        yAxis.setText(String.format(Locale.US, "%.2f", sensor[1].doubleValue()));
        zAxis.setText(String.format(Locale.US, "%.2f", sensor[2].doubleValue()));
        strengthBar.setText(String.format(Locale.US, "%.2f", sensor[3].doubleValue()));

        if (aprLevelsSeriesX.size() > 30) {
            aprLevelsSeriesX.removeFirst();
            aprLevelsSeriesY.removeFirst();
            aprLevelsSeriesZ.removeFirst();
            aprLevelsSeriesStrength.removeFirst();
        }
        aprLevelsSeriesX.addLast(0, sensor[0]);
        aprLevelsSeriesY.addLast(1, sensor[1]);
        aprLevelsSeriesZ.addLast(2, sensor[2]);
        aprLevelsSeriesStrength.addLast(3, sensor[3]);
        aprLevelsPlot.redraw();
    }

    @Override
    public void updateAccuracy() {
        //Log.i("change history", ""+((Magnadroid) mActivity).accuracy);
        if (accuracyText != null)
            accuracyText.setText(String.valueOf( mActivity.accuracy));

    }


}
