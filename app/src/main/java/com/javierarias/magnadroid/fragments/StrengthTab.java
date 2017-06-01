package com.javierarias.magnadroid.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.javierarias.magnadroid.Magnadroid;
import com.javierarias.magnadroid.R;
import com.javierarias.magnadroid.events.accuracyChange;
import com.javierarias.magnadroid.events.sensorChange;
import com.javierarias.magnadroid.sys.utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
public class StrengthTab extends mainFragment implements View.OnClickListener {

    private TextView stregthText;
    private GeomagneticField geo;
    private TextView geoStrengthText;
    private TextView alertText;
    private SeekBar strengthSensitivityBar;
    private int strengthSensitivity = 9;
    private TextView accuracyText;
    private double lastHighestValue;
    private TextView highestValueText;
    private EditText manualGeoValue;
    private float lowBoundary = 0.1f;
    private float highBoundary = 1.9f;
    private float lowBoundaryPlot = -180f;
    private float highBoundaryPlot = 360f;
    private double geoStrength = 25.0f;
    private double strength;
    private double lastValue;
    private XYPlot aprHistoryPlot = null;
    private float highPlotBoundary;
    private float lowPlotBoundary;
    private SimpleXYSeries differenceHistorySeries = null;
    private static final int HISTORY_SIZE = 200;            // number of points to plot in history

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View plotLayout = inflater.inflate(R.layout.strength_tab, container, false);
        stregthText = (TextView) plotLayout.findViewById(R.id.strengthText);
        mActivity = (Magnadroid) getActivity();
        //Button magneticButton = (Button) plotLayout.findViewById(R.id.magneticFieldButton);
        Button defaultSetButton = (Button) plotLayout.findViewById(R.id.setDefaultButton);
        Button manualSetButton = (Button) plotLayout.findViewById(R.id.setManualButton);
        geoStrengthText = (TextView) plotLayout.findViewById(R.id.geomagneticText);
        alertText = (TextView) plotLayout.findViewById(R.id.alertText);
        accuracyText = (TextView) plotLayout.findViewById(R.id.accuracy);
        manualGeoValue = (EditText) plotLayout.findViewById(R.id.editGeomagnetic);
        aprHistoryPlot = (XYPlot) plotLayout.findViewById(R.id.strengthHistory);

        manualGeoValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean pass = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    pass = true;
                } else if (event != null) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
                        if ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                            pass = true;

                        }
                    }
                }
                // hide virtual keyboard
                if (pass) {
                    try {
                        geoStrength = Double.parseDouble(manualGeoValue.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(mActivity, "The input number is invalid", Toast.LENGTH_LONG).show();
                        geoStrength = 0;
                    } finally {
                        geoStrengthText.setText(String.valueOf(geoStrength));
                    }
                      /*  geoStrength = Double.parseDouble(manualGeoValue.getText().toString());
                        geoStrengthText.setText(String.valueOf(geoStrength));*/
                    InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.

                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    utils.hide_keyboard(mActivity);
                    return true;
                }
                return false;
            }
        });

        highestValueText = (TextView) plotLayout.findViewById(R.id.highestVal);
        highestValueText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    lastHighestValue = 0;
                    return true;


                }
                return false;
            }
        });
        strengthSensitivityBar = (SeekBar) plotLayout.findViewById(R.id.strengthSensitivity);
        strengthSensitivityBar.setProgress(9);

        strengthSensitivityBar.setOnSeekBarChangeListener(this);

        manualSetButton.setOnClickListener(this);
        defaultSetButton.setOnClickListener(this);
        aprHistoryPlot.getGraph().getGridBackgroundPaint().setColor(Color.parseColor("#007D94"));
        aprHistoryPlot.setBackgroundColor(Color.parseColor("#A5B1BD"));
        aprHistoryPlot.setDrawingCacheBackgroundColor(Color.parseColor("#A5B1BD"));
        aprHistoryPlot.getGraph().getDomainGridLinePaint().setAlpha(0);
        aprHistoryPlot.getGraph().getDomainOriginLinePaint().setAlpha(0);

        differenceHistorySeries = new SimpleXYSeries("Magnetic Strength");
        differenceHistorySeries.useImplicitXVals();

        aprHistoryPlot.setRangeBoundaries(lowBoundaryPlot, highBoundaryPlot, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, 200, BoundaryMode.AUTO);
        aprHistoryPlot.addSeries(differenceHistorySeries, new LineAndPointFormatter(Color.GREEN, Color.TRANSPARENT, Color.TRANSPARENT, null));
        aprHistoryPlot.setDomainStepValue(5);
        aprHistoryPlot.setRangeLabel("micro-Tesla (uT)");
        aprHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
        updateSensitivity(strengthSensitivity);
        return plotLayout;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        strengthSensitivity = progress;
        updateSensitivity(progress);
    }

    @Override
    public void updateSensitivity(int progress) {
        lowBoundary = progress / 10f;
        highBoundary = 2f - (progress / 10f);
        highPlotBoundary = (progress == 10) ? highBoundaryPlot * 0.05f : highBoundaryPlot * (highBoundary - 1);
        lowPlotBoundary = (progress == 10) ? lowBoundaryPlot * 0.05f : lowBoundaryPlot * (highBoundary - 1);
        aprHistoryPlot.setRangeBoundaries(lowPlotBoundary, highPlotBoundary, BoundaryMode.FIXED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setDefaultButton:
                geoStrength = strength;
                geoStrengthText.setText(String.valueOf(geoStrength));
                break;
            case R.id.setManualButton:
                try {
                    geoStrength = Double.parseDouble(manualGeoValue.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(mActivity, "The input number is invalid", Toast.LENGTH_LONG).show();
                    geoStrength = 0;
                } finally {
                    geoStrengthText.setText(String.valueOf(geoStrength));
                }
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSensorChange(sensorChange sensor) {
        updateSignal(sensor.series1Numbers);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAccuracyChange(accuracyChange accuracy) {
        updateAccuracy();
    }

    @Override
    public void updateSignal(Number[] sensor) {
        sensor = updateSensorValues(sensor);
        strength = Math.sqrt(Math.pow(sensor[0].doubleValue(), 2) + Math.pow(sensor[1].doubleValue(), 2) + Math.pow(sensor[2].doubleValue(), 2));
        geoStrengthText.setText(String.format(Locale.US, "%.2f", geoStrength));
        if (strength < lowBoundary * geoStrength || strength > highBoundary * geoStrength) {
            lastValue = Math.abs(strength - geoStrength);
            if (lastValue > lastHighestValue) {
                lastHighestValue = lastValue;
            }
            alertText.setText(String.format(Locale.US, "%.2f", lastValue));
        }
        highestValueText.setText(String.format(Locale.US, "%.2f", lastHighestValue));
        stregthText.setText(String.format(Locale.US, "%.2f", strength));

        if (differenceHistorySeries.size() > HISTORY_SIZE) {
            differenceHistorySeries.removeFirst();

        }
        differenceHistorySeries.addLast(null, lastValue);
        aprHistoryPlot.redraw();
    }

    @Override
    public void updateAccuracy() {
        if (accuracyText != null)
            accuracyText.setText(String.valueOf((mActivity).accuracy));

    }

    @Override
    public Number[] updateSensorValues(Number[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].floatValue() < lowPlotBoundary) {
                values[i] = lowPlotBoundary + 1;
            } else if (values[i].floatValue() > highPlotBoundary) {
                values[i] = highPlotBoundary - 1;
            }
        }
        return values;
    }

}
