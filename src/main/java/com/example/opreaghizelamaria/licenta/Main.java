package com.example.opreaghizelamaria.licenta;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import de.nitri.gauge.Gauge;

public class Main extends AppCompatActivity implements LocationListener{

    private TextView sarcina;
    private EditText numar,varsta,nume,afectiuni,medicamente;
    private RadioButton a,b,ab,o,f,m,poz,neg;
    private Boolean newUser;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

   private String grupaSanguina,rhpozneg,sexfm;
     float CurrentSpeed;
    private Gauge gauge;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Switch s1,s2,s3;
    Intent intent;
    public static final int RECORD_AUDIO = 0;
        int perm=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gauge = (Gauge) findViewById(R.id.gauge);

        dl = (DrawerLayout) findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.app_name, R.string.app_name);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile) {
                    intent = new Intent(Main.this, Profile.class);
                    startActivity(intent);
                } else if (id == R.id.chat) {

                    intent = new Intent(Main.this, ChatMap.class);
                    startActivity(intent);
                } else if (id == R.id.home) {
                    intent = new Intent(Main.this, Main.class);
                    startActivity(intent);
                } else if (id == R.id.monitorizare) {
                    intent = new Intent(Main.this, Monitoring.class);
                    startActivity(intent);
                }
                return true;


            }
        });
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        onLocationChanged(null);
        if (locationManager != null)
            System.out.println("null");



            final SharedPreferences sharedPreferences = getSharedPreferences("Start", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            newUser = sharedPreferences.getBoolean("UtilizatorNou", true);
            if (newUser == true) {

                final Dialog alert = new Dialog(Main.this);
                alert.setContentView(R.layout.start_pop_up);

                sarcina = (TextView) alert.findViewById(R.id.textView4);
                sarcina.setVisibility(sarcina.INVISIBLE);
                s3 = (Switch) alert.findViewById(R.id.switch1);
                s3.setVisibility(s3.INVISIBLE);
                numar = (EditText) alert.findViewById(R.id.numar);
                nume = (EditText) alert.findViewById(R.id.numeUtiliz);
                varsta = (EditText) alert.findViewById(R.id.varsta);
                medicamente = (EditText) alert.findViewById(R.id.medicamente);
                medicamente.setEnabled(false);
                afectiuni = (EditText) alert.findViewById(R.id.afectiuni);
                afectiuni.setEnabled(false);
                a = (RadioButton) alert.findViewById(R.id.a);
                b = (RadioButton) alert.findViewById(R.id.b);
                ab = (RadioButton) alert.findViewById(R.id.ab);
                o = (RadioButton) alert.findViewById(R.id.o);
                f = (RadioButton) alert.findViewById(R.id.f);
                m = (RadioButton) alert.findViewById(R.id.m);
                poz = (RadioButton) alert.findViewById(R.id.poz);
                neg = (RadioButton) alert.findViewById(R.id.neg);
                s1 = (Switch) alert.findViewById(R.id.med);
                s2 = (Switch) alert.findViewById(R.id.afect);

                s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (s1.isChecked())
                            medicamente.setEnabled(true);
                        if (!s1.isChecked()) {
                            medicamente.setEnabled(false);
                            medicamente.setText("");
                        }

                    }
                });
                s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (s2.isChecked())
                            afectiuni.setEnabled(true);
                        if (!s2.isChecked()) {
                            afectiuni.setEnabled(false);
                            afectiuni.setText("");
                        }

                    }
                });
                f.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sarcina.setVisibility(sarcina.VISIBLE);
                        s3.setVisibility(s3.VISIBLE);
                    }
                });
                m.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sarcina.setVisibility(sarcina.INVISIBLE);
                        s3.setVisibility(s3.INVISIBLE);
                    }
                });
                alert.show();

                Button done = alert.findViewById(R.id.salveaza);


                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        grupaSanguina = "null";
                        sexfm = "null";
                        rhpozneg = "null";
                        editor.putBoolean("UtilizatorNou", false);
                        if(!numar.getText().toString().equals(""))
                        editor.putString("Numar", numar.getText().toString());
                        if(!nume.getText().toString().equals(""))
                            editor.putString("Nume", nume.getText().toString());
                        if(!varsta.getText().toString().equals(""))
                            editor.putString("Varsta", varsta.getText().toString());
                        if (s1.isChecked()&&!medicamente.getText().toString().equals(""))
                            editor.putString("Medicamente", medicamente.getText().toString());
                        if (s2.isChecked()&&!afectiuni.getText().toString().equals(""))
                            editor.putString("Afectiuni", afectiuni.getText().toString());
                        if (a.isChecked())
                            grupaSanguina = "A";
                        if (b.isChecked())
                            grupaSanguina = "B";
                        if (ab.isChecked())
                            grupaSanguina = "AB";
                        if (o.isChecked())
                            grupaSanguina = "0";
                        if(grupaSanguina!="null")
                        editor.putString("GrupaSanguina", grupaSanguina);
                        if (f.isChecked()) {
                            sexfm = "Feminin";
                            if (s3.isChecked())
                                editor.putString("Sarcina", "da");
                        }
                        if (m.isChecked())
                            sexfm = "Masculin";
                        if(sexfm!="null")
                            editor.putString("Sex", sexfm);
                        if (poz.isChecked())
                            rhpozneg = "+";
                        if (neg.isChecked())
                            rhpozneg = "-";
                        if(rhpozneg!="null")
                            editor.putString("Rh", rhpozneg);
                        editor.apply();
                        alert.cancel();
                        Intent intent = new Intent(Main.this, Calibrate.class);
                        startActivity(intent);
                    }
                });

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                int displayWidth = displayMetrics.widthPixels;
                int displayHeight = displayMetrics.heightPixels;

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(alert.getWindow().getAttributes());

                int dialogWindowWidth = (int) (displayWidth * 0.8f);
                int dialogWindowHeight = (int) (displayHeight * 0.9f);
                layoutParams.width = displayWidth;
                layoutParams.height = dialogWindowHeight;

                // Apply the newly created layout parameters to the alert dialog window
                alert.getWindow().setAttributes(layoutParams);
            } else {
                perm=0;
                permissions();
                startMyService();
            }

        }
    public void startMyService(){
        startService(new Intent(Main.this, AccidentDetect.class));


    }

    public void permissions(){
        int nr=0;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            {
               ActivityCompat.requestPermissions(Main.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO,Manifest.permission.SEND_SMS},123);
            }

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {

        } else {
            CurrentSpeed = location.getSpeed();
            CurrentSpeed *= 3.6;
            //Log.d("else", "Location changed!!!!!!!!!!");
            gauge.setValue(CurrentSpeed);
            System.out.println(CurrentSpeed);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startService(new Intent(Main.this,AccidentDetect.class));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm++;
            } else {

            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm++;

            }
        }
        if(requestCode==1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm++;
            } else {


            }

        }

    }
}
