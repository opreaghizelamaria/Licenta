package com.example.opreaghizelamaria.licenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Calibrate extends AppCompatActivity  {

    SensorManager sensorM;
    int nr=0;
    double X,Y,Z;
    double[] xs=new double[50];
    double[] ys=new double[50];
    double[] zs=new double[50];
    SharedPreferences sharedPreferences;
    Handler mHandler=new Handler();
    private CountDownTimer countDownTimer;

boolean count=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrare);
        Button button=(Button)findViewById(R.id.calibrare);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nr=0;
                final TextView countDown=(TextView)findViewById(R.id.count);
                stopService(new Intent(Calibrate.this,AccidentDetect.class));
                sensorM=(SensorManager) getSystemService(SENSOR_SERVICE);
                sensorM.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(final SensorEvent event) {
                        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
                                if(count==false)

                            countDownTimer = new CountDownTimer(10000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    X+=event.values[0];
                                    Y+=event.values[1];
                                    Z+=event.values[2];
                                    nr++;
                                    count=true;
                                    countDown.setText(Float.toString(millisUntilFinished / 1000));

                                }

                                @Override
                                public void onFinish() {
                                    X/=nr; Y/=nr; Z/=nr;
                                    X=0-X;Y=0-Y;Z=9.81-Z;
                                    sharedPreferences=getSharedPreferences("Start",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("biasX",Double.toString(X));
                                    editor.putString("biasY",Double.toString(Y));
                                    editor.putString("biasZ",Double.toString(Z));
                                    editor.apply();
                                    Intent intent=new Intent(Calibrate.this,Shake.class);
                                    startActivity(intent);
                                }
                            }.start();


                        }

                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                }, sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
