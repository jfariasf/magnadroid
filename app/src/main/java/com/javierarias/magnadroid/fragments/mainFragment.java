package com.javierarias.magnadroid.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.widget.SeekBar;

import com.javierarias.magnadroid.Magnadroid;
import com.javierarias.magnadroid.events.accuracyChange;
import com.javierarias.magnadroid.events.sensorChange;
import com.javierarias.magnadroid.sys.BusProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
public class mainFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    public boolean isVisible;

    private EventBus bus;

    protected int lowBoundary = -10000;
    protected int highBoundary = 10000;
    protected int sliderValue = 9900;
    protected Magnadroid mActivity;

    public static Fragment newInstance(int sortOrder) {//, CharSequence title, int indicatorColor, int dividerColor
        Bundle args;
        switch (sortOrder) {
            case 0:
                StrengthTab mainTab;
                mainTab = new StrengthTab();
                mainTab.getTag();
                args = new Bundle();
                mainTab.setArguments(args);
                return mainTab;

            /* FragmentStatePagerAdapter would keep the next tabs state ONLY, 3 tabs would keep state of middle tab constant  */
            case 1:
                BarsPlot barsTab; //testing
                barsTab = new BarsPlot();
                barsTab.getTag();
                args = new Bundle();
                barsTab.setArguments(args);
                return barsTab;

            case 2:
                HistoryPlot secTab;
                secTab = new HistoryPlot();
                secTab.getTag();
                args = new Bundle();
                secTab.setArguments(args);
                return secTab;

            default:
                return null;
        }


    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mActivity = (Magnadroid) getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mActivity == null)
            mActivity = (Magnadroid) getActivity();
        if (isVisibleToUser && !BusProvider.getInstance().isRegistered(this)) {
            isVisible = true;
            BusProvider.getInstance().register(this);
        } else {

            if (BusProvider.getInstance().isRegistered(this)) {
                BusProvider.getInstance().unregister(this);
            }
        }
    }

    public void calculatePlotBoundaries(int progress) {

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSensorChange(sensorChange sensor) {
        updateSignal(sensor.series1Numbers);
    }



    public void updateSignal(Number[] sensor) {

    }
/*
    public Number[] updateSensorValues(Number[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].floatValue() < lowBoundary + sliderValue) {
                values[i] = lowBoundary + sliderValue + 1;
            } else if (values[i].floatValue() > highBoundary - sliderValue) {
                values[i] = (highBoundary - sliderValue) - 1;
            }
        }
        return values;
    }
*/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
