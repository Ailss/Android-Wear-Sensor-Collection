package com.example.bingchang.androidweartest;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.spec.ECField;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity implements DataApi.DataListener{
    private static final String TAG = "BingDataWear";
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private Button mbutton;
    private Button mbuttonstart;

    private File file;
    private File fileg;
    private File fileh;

    private static final String SENSOR_DATA_PATH = "/sensor-data";
    private static final String CON_DATA_PATH = "/con-data";
    private GoogleApiClient mGoogleApiClient;
    private PutDataMapRequest sensorData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        File root = new File(Environment.getExternalStorageDirectory() + "/DCIM");
        File files[] = root.listFiles();
        for (File f : files) {
            if (f.exists())
                f.delete();
        }


        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);
        mbutton = (Button) findViewById(R.id.button);
        mbuttonstart = (Button) findViewById(R.id.buttonstart);
        mbuttonstart.setVisibility(View.INVISIBLE);
        mbutton.setOnClickListener(new exitonListener());
        mbuttonstart.setOnClickListener(new startonListener());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        sensorData = PutDataMapRequest.create(SENSOR_DATA_PATH);
        Wearable.DataApi.addListener(mGoogleApiClient, MainActivity.this);
        //Log.d(TAG,"DELAY!!!"+SensorManager.SENSOR_DELAY_FASTEST);
    }


    class exitonListener implements View.OnClickListener {
        public void onClick(View v) {
            Log.d(TAG, "onClick Exit!");
            finish();
            System.exit(0);
        }
    }

    class startonListener implements View.OnClickListener {
        public void onClick(View v) {
            Log.d(TAG, "onClick Start!");
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SensorActivity.class);
            startActivity(intent);

            new Thread(new Runnable(){

                public void run(){
                    try{
                        Thread.sleep(5000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    Intent intents = new Intent("com.example.bingchang.androidweartest");
                    sendBroadcast(intents);
                    Log.d(TAG,"Broadcast Send!");
                }

            }).start();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if(!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient.");
                return;
            }
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (CON_DATA_PATH.equals(path) && event.getType() == DataEvent.TYPE_CHANGED) {
                byte[] rawData = event.getDataItem().getData();
                DataMap condata = DataMap.fromByteArray(rawData);
                if(condata.getString("Start") != "")
                {
                    Log.d(TAG, "sensorData Start!");
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SensorActivity.class);
                    startActivity(intent);
                }
                if(condata.getString("Finish") != "")
                {
                    Intent intents = new Intent("com.example.bingchang.androidweartest");
                    sendBroadcast(intents);
                    Log.d(TAG, "Broadcast Send!");
                    try{
                        Thread.sleep(5);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    file  = new File(Environment.getExternalStorageDirectory()+"/DCIM","a.txt");
                    fileg  = new File(Environment.getExternalStorageDirectory()+"/DCIM","g.txt");
                    fileh  = new File(Environment.getExternalStorageDirectory()+"/DCIM","h.txt");
                    String content = new String("");
                    String contentg = new String("");
                    String contenth = new String("");
                    if(file.exists() && fileg.exists() && fileh.exists()){
                        try{
                            InputStream instream = new FileInputStream(file);
                            InputStream instreamg = new FileInputStream(fileg);
                            InputStream instreamh = new FileInputStream(fileh);
                            InputStreamReader inputreader = new InputStreamReader(instream);
                            BufferedReader buffreader = new BufferedReader(inputreader);
                            String line;
                            while (( line = buffreader.readLine()) != null) {
                                content += line + "\r\n";
                            }

                            InputStreamReader inputreaderg = new InputStreamReader(instreamg);
                            BufferedReader buffreaderg = new BufferedReader(inputreaderg);
                            String lineg;
                            while (( lineg = buffreaderg.readLine()) != null) {
                                contentg += lineg + "\r\n";
                            }

                            InputStreamReader inputreaderh = new InputStreamReader(instreamh);
                            BufferedReader buffreaderh = new BufferedReader(inputreaderh);
                            String lineh;
                            while (( lineh = buffreaderh.readLine()) != null) {
                                contenth += lineh + "\r\n";
                            }

                            instream.close();
                            instreamg.close();
                            instreamh.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        sensorData.getDataMap().putString("ACCELEROMETER",content);
                        sensorData.getDataMap().putString("GYROSCOPE",contentg);
                        sensorData.getDataMap().putString("HEARTRATE",contenth);
                        PutDataRequest request = sensorData.asPutDataRequest();
                        Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"wear Main onResume!");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        File root = new File(Environment.getExternalStorageDirectory() + "/DCIM");
        File files[] = root.listFiles();
        for (File f : files) {
            if (f.exists())
                f.delete();
        }
        Wearable.DataApi.removeListener(mGoogleApiClient, MainActivity.this);
        mGoogleApiClient.disconnect();
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
