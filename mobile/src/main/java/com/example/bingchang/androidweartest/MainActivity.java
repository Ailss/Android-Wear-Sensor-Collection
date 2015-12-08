package com.example.bingchang.androidweartest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements DataApi.DataListener{
    private EditText editTextc;
    private EditText editTextn;
    private Button nextTrail;
    private Button start;
    private Button deletefiles;
    private FileOutputStream fos;
    private FileOutputStream fosg;
    private FileOutputStream fosh;
    private File file;
    private File fileg;
    private File fileh;
    private PutDataMapRequest conData;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "BingDataPhone";
    private static final String SENSOR_DATA_PATH = "/sensor-data";
    private static final String CON_DATA_PATH = "/con-data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        editTextc = (EditText)findViewById(R.id.edit_counter);
        editTextn = (EditText)findViewById(R.id.edit_name);
        nextTrail = (Button)findViewById(R.id.button_next);
        start = (Button)findViewById(R.id.button_start);
        deletefiles = (Button)findViewById(R.id.button_delete);
        nextTrail.setOnClickListener(new nextonListener());
        start.setOnClickListener(new startonListener());
        deletefiles.setOnClickListener(new deleteonListener());

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Wearable.API)
                .build();
        conData = PutDataMapRequest.create(CON_DATA_PATH);
        if (null != mGoogleApiClient && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Wearable.DataApi.addListener(mGoogleApiClient, MainActivity.this);
        }
    }

    class nextonListener implements View.OnClickListener {
        public void onClick(View v){
            String conter = editTextc.getText().toString();
            int i = Integer.parseInt(conter);
            editTextc.setText(""+(++i));
        }
    }
    class startonListener implements View.OnClickListener {
        public void onClick(View v){
            String conter = editTextc.getText().toString();
            String state = start.getText().toString();
            PutDataRequest request;
            switch (state) {
                case "Start!":
                    start.setText("Finish!");
                    conData.getDataMap().putString("Start","1");
                    conData.getDataMap().putString("Finish","");
                    request = conData.asPutDataRequest();
                    Wearable.DataApi.putDataItem(mGoogleApiClient, request);
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
                    break;
                case "Finish!":
                    start.setText("Start!");
                    conData.getDataMap().putString("Start","");
                    conData.getDataMap().putString("Finish","1");
                    request = conData.asPutDataRequest();
                    Wearable.DataApi.putDataItem(mGoogleApiClient, request);

                    String filename = editTextn.getText().toString() +editTextc.getText().toString()+".txt";
                    String filenameg = editTextn.getText().toString() +editTextc.getText().toString()+"g.txt";
                    String filenameh = editTextn.getText().toString() +editTextc.getText().toString()+"h.txt";
                    file  = new File(Environment.getExternalStorageDirectory()+"/SensorData",filename);
                    fileg  = new File(Environment.getExternalStorageDirectory()+"/SensorData",filenameg);
                    fileh  = new File(Environment.getExternalStorageDirectory()+"/SensorData",filenameh);
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
                    break;
                default:
            }
        }
    }
    class deleteonListener implements View.OnClickListener {
        public void onClick(View v){

            Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
                    setTitle("Comfirm").
                    setMessage("Do you want to delete the files?").
                    //setIcon(R.drawable.ic_launcher).
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            System.out.println("alert yes");
                            File root = new File(Environment.getExternalStorageDirectory() + "/SensorData");
                            File files[] = root.listFiles();
                            for (File f : files) {
                                if (f.exists())
                                    f.delete();
                            }
                        }
                    }).
                    setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            System.out.println("alert no");
                        }
                    }).
                    create();
            alertDialog.show();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG,"receive sensorDATA!!!");
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
            if (SENSOR_DATA_PATH.equals(path)) {
                byte[] rawData = event.getDataItem().getData();
                Log.d(TAG,"SENSOR_DATA_PATH!!!");
                DataMap sensorData = DataMap.fromByteArray(rawData);
                if(sensorData.getString("ACCELEROMETER") != "" && sensorData.getString("GYROSCOPE") != "" && sensorData.getString("HEARTRATE") != "")
                {
                    Log.d(TAG,"ACCELEROMETER && GYROSCOPE && HEARTRATE!!!");
                    try {
                        byte[] buffer = sensorData.getString("ACCELEROMETER").getBytes();
                        fos.write(buffer);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try {
                        byte[] buffer = sensorData.getString("GYROSCOPE").getBytes();
                        fosg.write(buffer);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try {
                        byte[] buffer = sensorData.getString("HEARTRATE").getBytes();
                        fosh.write(buffer);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, MainActivity.this);
            mGoogleApiClient.disconnect();
        }
    }
}
