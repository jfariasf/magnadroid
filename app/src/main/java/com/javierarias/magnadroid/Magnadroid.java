package com.javierarias.magnadroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/*import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;*/
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
//import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.javierarias.magnadroid.events.accuracyChange;
import com.javierarias.magnadroid.events.sensorChange;
import com.javierarias.magnadroid.fragments.magneTabs;
import com.javierarias.magnadroid.sys.BusProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import io.fabric.sdk.android.Fabric;
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

/**
 * TODO: Custom limits for sensibility references
 * TODO: Check which Fragment class is present and send data or experiment with EventBus StickyPost *
 */


public class Magnadroid extends AppCompatActivity implements SensorEventListener {

    public float magneticMaxRange;
    public int accuracy = 0;

    private SensorManager mSensorManager;
    private Sensor mMagnetic;
    private boolean initialAccuracy;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        // Debug.startMethodTracing();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // Success! There's a magnetometer.
            mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            magneticMaxRange = mMagnetic.getMaximumRange();
        }
        setContentView(R.layout.activity_magnadroid);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        magneTabs fragment = new magneTabs();
        transaction.replace(R.id.frame_content, fragment);
        transaction.commit();
    }

    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        if (!initialAccuracy) {
            initialAccuracy = true;
            this.accuracy = sensorEvent.accuracy;
            BusProvider.getInstance().post(new accuracyChange());
        }
        BusProvider.getInstance().postSticky(new sensorChange(new Number[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]}));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        this.accuracy = accuracy;
        BusProvider.getInstance().post(new accuracyChange());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_magnadroid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            /*case R.id.action_settings:
                return true;*/
            case R.id.action_accuracy:
                Intent myIntent = new Intent(this, generalInfo.class);
                //myIntent.putExtra("key", value); //Optional parameters
                this.startActivity(myIntent);
                return true;
            case R.id.action_ubik:
                String url = "https://play.google.com/store/apps/details?id=com.javierarias.ubika&hl=en";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public static class APRIndexFormat extends Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            Number num = (Number) obj;
            // using num.intValue() will floor the value, so we add 0.5 to round instead:
            int roundNum = (int) (num.floatValue() + 0.5f);

            switch (roundNum) {
                case 0:
                    toAppendTo.append("X");
                    break;
                case 1:
                    toAppendTo.append("Y");
                    break;
                case 2:
                    toAppendTo.append("Z");
                    break;
                case 3:
                    toAppendTo.append("Strength");
                    break;
                default:
                    ////Log.i("roundNum",""+roundNum);
                    toAppendTo.append("Unknown");
                    break;
            }
            return toAppendTo;
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return null;  // We don't use this so just return null for now.
        }
    }
}
