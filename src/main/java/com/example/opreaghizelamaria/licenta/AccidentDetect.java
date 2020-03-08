package com.example.opreaghizelamaria.licenta;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccidentDetect extends Service implements  SensorEventListener,LocationListener {

    private double CurrentSpeed = 0;
    private SensorManager sensorManager;
    double X=0,Y=0,Z=0,G=0,A,g, biasX,biasY,biasZ,max;
    int i=0;long maxid;
    static final float ALPH = 0.01f;
    Button button;
    Location myLocation=null;
    MediaRecorder mRecorder;
    double cSound=Double.NEGATIVE_INFINITY;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference=firebaseDatabase.getReference();
    private DatabaseReference data1=databaseReference.child("Users");
    public static boolean alarmOn=false,mic=false;
    SharedPreferences sharedPreferences;
    Handler mHandler=new Handler();

    int  calibrate;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float dt;


    float[] gravSensorVals;
    GraphView graphView;
    double ti,t0=System.currentTimeMillis();
    @Override
    public void onCreate() {


        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy/HH:mm:ss");
        Date date = new Date();
        String dateTime = format.format(date);
        String[] parts = dateTime.split("/");
        String part1 = parts[0];
        sharedPreferences=getSharedPreferences("Start",MODE_PRIVATE);

        String part2 = parts[1];

        data1.child(sharedPreferences.getString("Nume","Necunoscut")).child(part1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid=(dataSnapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        onLocationChanged(null);
        if (locationManager != null)
            System.out.println("null");
//

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);

        } else {
            try {
                stop();
                start();




            } catch (IOException e) {
                e.printStackTrace();

            }
        }



            biasX=Double.parseDouble(sharedPreferences.getString("biasX","0"));
            biasY=Double.parseDouble(sharedPreferences.getString("biasY","0"));
            biasZ=Double.parseDouble(sharedPreferences.getString("biasZ","0"));
            max=Double.parseDouble(sharedPreferences.getString("Max","0"));

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Mic",false);
        editor.apply();
        stop();
        stopSelf();
    }

    public void onSensorChanged(SensorEvent event){

        calibrate=sharedPreferences.getInt("Shake", 0);

        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER&&calibrate==0&&CurrentSpeed>23 ) {

            X = event.values[0] + biasX;
            Y = event.values[1] + biasY;
            Z = event.values[2] + biasZ;
            gravSensorVals = lowPass(event.values.clone(), gravSensorVals);
            X = gravSensorVals[0];
            Y = gravSensorVals[1];
            Z = gravSensorVals[2];


            cSound = getAmplitude();

            A = Math.sqrt(X * X + Y * Y + Z * Z);
            g=A/9.81;
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy/HH:mm:ss");
            Date date = new Date();
            String dateTime = format.format(date);
            String[] parts = dateTime.split("/");
            String part1 = parts[0];
            String part2 = parts[1];
            if (null != myLocation&&mRecorder!=null) {
                cSound = getAmplitude();

                Data data = new Data(part2, Double.toString(X), Double.toString(Y), Double.toString(Z), Double.toString(g), Double.toString(cSound), Double.toString(CurrentSpeed));
                data1.child(sharedPreferences.getString("Nume","Necunoscu")).child(part1).child(String.valueOf(1)).setValue(data);
            }


            if (g > max && alarmOn == false&&myLocation!=null&&cSound==0.0 ) {
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Alarma", true);
                editor.apply();
                alarmOn = true;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                     if (CurrentSpeed== 0) {

                            editor.putString("Locatia","latitudine "+myLocation.getLatitude()+" longitudine "+myLocation.getLongitude());
                            editor.apply();
                            Intent popup = new Intent(getApplicationContext(), Alarm.class);
                            popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(popup);

                            }
                        else {
                            alarmOn=false;
                            editor.putBoolean("Alarma", false);
                            editor.apply();
                        }
                    }
                }, 10000);

            }
           }

        }

    public void start() throws IOException {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            mRecorder.start();

        }
    }
    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null){
        return 20 * Math.log10(mRecorder.getMaxAmplitude() / 32767.0);
    }
        else
            return 0;

    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPH * (input[i] - output[i]);
        }
        return output;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
        } else {
            CurrentSpeed = location.getSpeed();
            CurrentSpeed *= 3.6;
            myLocation=location;





        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}
