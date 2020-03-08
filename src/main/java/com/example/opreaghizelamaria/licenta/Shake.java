package com.example.opreaghizelamaria.licenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Shake extends AppCompatActivity {
    double X,Y,Z,A,t0=System.currentTimeMillis(),biasX,biasY,biasZ;;
    static final float ALPH = 0.08f;
    float[] accSensorVals;
    double g=0,maxGI=0,maxGF=0;
    SensorManager sensorM;
    int nr=0;
    private CountDownTimer countDownTimer;

    boolean count=false;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        AnimatePhone();
        stopService(new Intent(Shake.this, AccidentDetect.class));
        sharedPreferences=getSharedPreferences("Start",MODE_PRIVATE);
        biasX=Double.parseDouble(sharedPreferences.getString("biasX","0"));
        biasY=Double.parseDouble(sharedPreferences.getString("biasY","0"));
        biasZ=Double.parseDouble(sharedPreferences.getString("biasZ","0"));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Shake", 1);
        editor.apply();


        Button button=(Button)findViewById(R.id.shake);
        TextView text=(TextView)findViewById(R.id.textT);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nr = 0;
                final TextView countDown=(TextView)findViewById(R.id.countT);
                System.out.println(nr);
                sensorM = (SensorManager) getSystemService(SENSOR_SERVICE);
                sensorM.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(final SensorEvent event) {
                        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                            if(count==false)

                                countDownTimer = new CountDownTimer(10000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        count = true;
                                        countDown.setText(Float.toString(millisUntilFinished / 1000));
                                        X = event.values[0] + biasX;
                                        Y = event.values[1] + biasY;
                                        Z = event.values[2] + biasZ;
                                        accSensorVals = lowPass(event.values.clone(), accSensorVals);
                                        System.out.println(nr);

                                        X = accSensorVals[0];
                                        Y = accSensorVals[1];
                                        Z = accSensorVals[2];
                                        A = Math.sqrt(X * X + Y * Y + Z * Z);
                                        g = A / 9.81;

                                        if (g > maxGF) {
                                            maxGF = g;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        double max = Double.parseDouble(sharedPreferences.getString("Max", "0"));
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        if (max < maxGF) {
                                            editor.putString("Max", Double.toString(maxGF));
                                        }
                                        System.out.println("fccgcbn hgtrdedfg jhytredxcfgvhb gfdt");
                                        editor.putInt("Shake", 0);
                                        editor.apply();

                                        Intent intent = new Intent(Shake.this, Main.class);
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
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPH * (input[i] - output[i]);
        }
        return output;
    }
    public void AnimatePhone() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shakeanimation);
        ImageView imgBell= (ImageView) findViewById(R.id.imgBell);
        imgBell.setImageResource(R.drawable.phone);
        imgBell.setAnimation(shake);
    }
}
