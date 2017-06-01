package com.javierarias.magnadroid.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.javierarias.magnadroid.Magnadroid;
import com.javierarias.magnadroid.R;

import java.util.Locale;

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Javier Felipe Arias
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class BarsPlot extends mainFragment {

    private XYPlot aprLevelsPlot = null;
    private SimpleXYSeries aprLevelsSeriesX = null;
    private SimpleXYSeries aprLevelsSeriesY = null;
    private SimpleXYSeries aprLevelsSeriesZ = null;
    private SimpleXYSeries aprLevelsSeriesStrength = null;
    private SeekBar seekBar;
    private TextView xAxis;
    private TextView yAxis;
    private TextView zAxis;
    private TextView accuracyText;
    private int multiplier = 1;
    private TextView strengthBar;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View plotLayout = inflater.inflate(R.layout.bars_plot, container, false);
        seekBar = (SeekBar) plotLayout.findViewById(R.id.sensitivity);
        strengthBar = (TextView) plotLayout.findViewById(R.id.strengthBar);
        seekBar.setProgress(sliderValue);
        seekBar.setOnSeekBarChangeListener(this);
        accuracyText = (TextView) plotLayout.findViewById(R.id.accuracyBar);

        yAxis = (TextView) plotLayout.findViewById(R.id.yAxis);
        xAxis = (TextView) plotLayout.findViewById(R.id.xAxis);
        zAxis = (TextView) plotLayout.findViewById(R.id.zAxis);

        aprLevelsPlot = (XYPlot) plotLayout.findViewById(R.id.aprLevelsPlot);

        aprLevelsSeriesX = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "X Axis");
        aprLevelsSeriesY = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Y Axis");
        aprLevelsSeriesZ = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Z Axis");
        aprLevelsSeriesStrength = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Strength");

        BarFormatter barFormatterX = new BarFormatter(Color.parseColor("#5FA9DD"), Color.argb(20, 100, 50, 0));
        BarFormatter barFormatterY = new BarFormatter(Color.parseColor("#DEDC16"), Color.argb(100, 200, 100, 0));
        BarFormatter barFormatterZ = new BarFormatter(Color.parseColor("#D80000"), Color.argb(100, 50, 100, 0));
        BarFormatter barFormatterStrength = new BarFormatter(Color.GREEN, Color.argb(100, 50, 100, 0));

        barFormatterX.setMarginLeft(PixelUtils.dpToPix(1));
        barFormatterX.setMarginRight(PixelUtils.dpToPix(1));

        barFormatterY.setMarginLeft(PixelUtils.dpToPix(1));
        barFormatterY.setMarginRight(PixelUtils.dpToPix(1));

        barFormatterZ.setMarginLeft(PixelUtils.dpToPix(1));
        barFormatterZ.setMarginRight(PixelUtils.dpToPix(1));

        aprLevelsPlot.addSeries(aprLevelsSeriesX, barFormatterX);
        aprLevelsPlot.addSeries(aprLevelsSeriesY, barFormatterY);
        aprLevelsPlot.addSeries(aprLevelsSeriesZ, barFormatterZ);
        aprLevelsPlot.addSeries(aprLevelsSeriesStrength, barFormatterStrength);
        aprLevelsPlot.setDomainStepValue(4);
        aprLevelsPlot.setRangeBoundaries(lowBoundary, highBoundary, BoundaryMode.FIXED);
        aprLevelsPlot.setDomainLabel("Axis");
        aprLevelsPlot.setRangeLabel("micro-Tesla (uT)");
        aprLevelsPlot.setPlotPadding(100, 0, 100, 0);

        updateSensitivity(sliderValue);
        aprLevelsPlot.setLayerType(View.LAYER_TYPE_NONE, null);
        BarRenderer barRenderer = aprLevelsPlot.getRenderer(BarRenderer.class);

        if (barRenderer != null) {
            barRenderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);
            barRenderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(5));
        }
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
        if (aprLevelsSeriesX.size() > 1) {
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
        if (accuracyText != null)
            accuracyText.setText(String.valueOf( mActivity.accuracy));

    }


}
