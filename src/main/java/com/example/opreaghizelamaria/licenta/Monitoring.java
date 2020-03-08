package com.example.opreaghizelamaria.licenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;

public class Monitoring extends AppCompatActivity implements SensorEventListener {

    double X,Y,Z,max,A,t0=System.currentTimeMillis(),biasX,biasY,biasZ;;
    GraphView graphView;
    private SensorManager sensorManager;
    LineGraphSeries<DataPoint> series1 ;
    LineGraphSeries<DataPoint> seriesx ;
    LineGraphSeries<DataPoint> seriesy ;
    LineGraphSeries<DataPoint> seriesz ;
    static final float ALPH = 0.08f;
    double g=0,maxGI=0,maxGF=0;
    float[] gravSensorVals;
    SharedPreferences sharedPreferences;
    Intent intent;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorizare);
    Button button=(Button) findViewById(R.id.calibrare2);
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Monitoring.this, Calibrate.class);
            startActivity(intent);
        }
    });


        series1 = new LineGraphSeries<DataPoint>();
        seriesx = new LineGraphSeries<DataPoint>();
        seriesy = new LineGraphSeries<DataPoint>();
        seriesz = new LineGraphSeries<DataPoint>();

        graphView=(GraphView)findViewById(R.id.graphview);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(50);
        graphView.getViewport().setMaxY(10);
        graphView.getViewport().setMinY(-5);

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        graphView.addSeries(seriesx);
        graphView.addSeries(seriesy);
        graphView.addSeries(seriesz);
        graphView.addSeries(series1);

        sharedPreferences=getSharedPreferences("Start",MODE_PRIVATE);
        biasX=Double.parseDouble(sharedPreferences.getString("biasX","0"));
        biasY=Double.parseDouble(sharedPreferences.getString("biasY","0"));
        biasZ=Double.parseDouble(sharedPreferences.getString("biasZ","0"));
        max=Double.parseDouble(sharedPreferences.getString("Max","0"));

        dl = (DrawerLayout) findViewById(R.id.activity_monit);
        t = new ActionBarDrawerToggle(this, dl, R.string.app_name, R.string.app_name);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile) {
                    intent = new Intent(Monitoring.this, Profile.class);
                    startActivity(intent);
                } else if (id == R.id.chat) {

                    intent = new Intent(Monitoring.this, ChatMap.class);
                    startActivity(intent);
                } else if (id == R.id.home) {
                    intent = new Intent(Monitoring.this, Main.class);
                    startActivity(intent);
                } else if (id == R.id.monitorizare) {
                    intent = new Intent(Monitoring.this, Monitoring.class);
                    startActivity(intent);
                }
                return true;


            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            X = event.values[0]+biasX;
            Y = event.values[1]+biasY;
            Z = event.values[2]+biasZ;
            A = Math.sqrt(X * X + Y * Y + Z * Z);

            g = A / 9.81;

            gravSensorVals = lowPass(event.values.clone(), gravSensorVals);
            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>();
            series1.setColor(Color.RED);
            series1.setDrawDataPoints(true);
            series1.setDataPointsRadius(5);

            series1.appendData(new DataPoint((System.currentTimeMillis() - t0) / 6000, max+0.5), true, 100);
            System.out.println((System.currentTimeMillis() - t0) / 6000);

            graphView.addSeries(series1);

            X = gravSensorVals[0];
            Y = gravSensorVals[1];
            Z = gravSensorVals[2];
            A = Math.sqrt(X * X + Y * Y + Z * Z);
            g = A / 9.81;

            if (g > maxGF) {
                maxGF=g;
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
            series.setColor(Color.BLUE);
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(6);

            series.appendData(new DataPoint((System.currentTimeMillis() - t0) / 6000, g), true, 100);

            graphView.addSeries(series);

        }
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPH * (input[i] - output[i]);
        }
        return output;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
