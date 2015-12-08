package com.example.bingchang.androidweartest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bingchang on 9/11/2015.
 */
public class SensorActivity extends WearableActivity implements SensorEventListener{

    private static final String TAG = "BingDataWearSensor";
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private Button mbutton;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor heartrate;
    private int minstart;

    private FileOutputStream fos;
    private FileOutputStream fosg;
    private FileOutputStream fosh;
    private File file;
    private File fileg;
    private File fileh;
    private TestReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        setAmbientEnabled();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.stext);
        mClockView = (TextView) findViewById(R.id.sclock);

        mbutton = (Button) findViewById(R.id.sbutton);
        mbutton.setOnClickListener(new exitonListener());
        mbutton.setVisibility(View.INVISIBLE);
        receiver = new TestReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.bingchang.androidweartest");
        registerReceiver(receiver, filter);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager == null){
            Log.d(TAG,"mSensorManager == null!!!");
            return;
        }

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        heartrate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        Calendar c = Calendar.getInstance();
        minstart = c.get(Calendar.MINUTE);

        file   = new File(Environment.getExternalStorageDirectory()+"/SensorData","a.txt");
        fileg  = new File(Environment.getExternalStorageDirectory()+"/SensorData","g.txt");
        fileh  = new File(Environment.getExternalStorageDirectory()+"/SensorData","h.txt");

        if(!fileh.exists())
            try {
                fileh.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            if(fosh == null)
                fosh = new FileOutputStream(fileh);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(!fileg.exists())
            try {
                fileg.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            if(fosg == null)
                fosg = new FileOutputStream(fileg);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            if(fos == null)
                fos = new FileOutputStream(file);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    class exitonListener implements View.OnClickListener {
        public void onClick(View v) {
            Log.d(TAG, "onClick Exit!");
            finish();
            onDestroy();
        }
    }

    public class TestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive if");
            if(intent.getAction().equals("com.example.bingchang.androidweartest")){
                Log.d(TAG, "onReceive!");
                finish();
            }
        }
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        //if(null != mGoogleApiClient && mGoogleApiClient.isConnected()){
        float lux,luy,luz;
            //long time= System.currentTimeMillis();
            Calendar c = Calendar.getInstance();
            int mseconds = c.get(Calendar.MILLISECOND);
            String strmsec = new DecimalFormat("000").format(mseconds);
            int seconds = c.get(Calendar.SECOND);
            if(minstart != c.get(Calendar.MINUTE))
                seconds += 60;
            switch(event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    lux = event.values[0];
                    luy = event.values[1];
                    luz = event.values[2];
                    try {
                        String message = seconds + "." + strmsec + " " + lux + " " + luy + " " + luz;
                        byte[] buffer = message.getBytes();
                        fos.write(buffer);
                        fos.write("\r\n".getBytes());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    lux = event.values[0];
                    luy = event.values[1];
                    luz = event.values[2];
                    try {
                        String message = seconds + "." + strmsec + " " + lux + " " + luy + " " + luz;
                        byte[] buffer = message.getBytes();
                        fosg.write(buffer);
                        fosg.write("\r\n".getBytes());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case Sensor.TYPE_HEART_RATE:
                    lux = event.values[0];
                    Log.d(TAG,"length:"+event.values.length);
                    try {
                        String message = seconds + "." + strmsec + " " + lux;
                        byte[] buffer = message.getBytes();
                        fosh.write(buffer);
                        fosh.write("\r\n".getBytes());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, heartrate, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if(mSensorManager != null)
            mSensorManager.unregisterListener(this);
        try {
            if(fos != null){
                fos.close();
                fos = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            if(fosg != null){
                fosg.close();
                fosg = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            if(fosh != null){
                fosh.close();
                fosh = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
