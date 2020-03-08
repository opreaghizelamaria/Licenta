package com.example.opreaghizelamaria.licenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Profile extends AppCompatActivity {

    private EditText numar,varsta,nume,afectiuni,medicamente;
    private RadioGroup rh,grupaS,sex;
    private RadioButton a,b,ab,o,f,m,poz,neg;
    private Switch s3;
    private TextView sarcina;
    Button edit;
    SharedPreferences sharedPreferences;

    Intent intent;


    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       sharedPreferences=getSharedPreferences("Start",MODE_PRIVATE);
       edit=(Button)findViewById(R.id.editare);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(Profile.this,EditProfile.class);
                startActivity(intent);
            }
        });
        sarcina=(TextView)findViewById(R.id.textView4p);
        //s3.setEnabled(false);

        numar=(EditText)findViewById(R.id.numarp);
        nume=(EditText)findViewById(R.id.numeUtilizp);
        varsta=(EditText)findViewById(R.id.varstap);
        medicamente=(EditText)findViewById(R.id.medicamentep);
        afectiuni=(EditText)findViewById(R.id.afectiunip);
        grupaS=(RadioGroup)findViewById(R.id.sangep);
        rh=(RadioGroup)findViewById(R.id.RHp);
        sex=(RadioGroup)findViewById(R.id.sexp);
        a=(RadioButton)findViewById(R.id.ap);
        b=(RadioButton)findViewById(R.id.bp);
        ab=(RadioButton)findViewById(R.id.abp);
        o=(RadioButton)findViewById(R.id.op);
        f=(RadioButton)findViewById(R.id.fp);
        m=(RadioButton)findViewById(R.id.mp);
        poz=(RadioButton)findViewById(R.id.pozp);
        neg=(RadioButton)findViewById(R.id.negp);
        System.out.println(sharedPreferences.getString("Sarcina","nu"));
        if(sharedPreferences.getString("Sarcina","nu").equals("da")){
            sarcina.setVisibility(sarcina.VISIBLE);

        }
        else{
            sarcina.setVisibility(sarcina.INVISIBLE);

        }
        numar.setText(sharedPreferences.getString("Numar","necunoscut"));
        nume.setText(sharedPreferences.getString("Nume","necunoscut"));
        varsta.setText(sharedPreferences.getString("Varsta","necunoscut"));
        medicamente.setText(sharedPreferences.getString("Medicamente",""));
        afectiuni.setText(sharedPreferences.getString("Afectiuni",""));
        afectiuni.setEnabled(false);
        numar.setEnabled(false);
        nume.setEnabled(false);
        varsta.setEnabled(false);
        medicamente.setEnabled(false);

        switch (sharedPreferences.getString("GrupaSanguina","necunoscut")){
            case "A":
                a.setChecked(true);
                b.setEnabled(false);
                ab.setEnabled(false);
                o.setEnabled(false);
                break;
            case "B":
                a.setEnabled(false);
                b.setChecked(true);
                ab.setEnabled(false);
                o.setEnabled(false);
                break;
            case "0":
                a.setEnabled(false);
                b.setEnabled(false);
                ab.setEnabled(false);
                o.setChecked(true); break;
            case "AB":
                a.setEnabled(false);
                b.setEnabled(false);
                ab.setChecked(true);
                o.setEnabled(false);
                break;
            default:    a.setEnabled(false);
                b.setEnabled(false);
                ab.setEnabled(false);
                o.setEnabled(false);
                break;
        }

        switch (sharedPreferences.getString("Rh","necunoscut")){
            case "+":
                poz.setChecked(true);
                neg.setEnabled(false);
                break;
            case "-":
                neg.setChecked(true);
                poz.setEnabled(false);
                break;
            default: neg.setEnabled(false);
            poz.setEnabled(false);
            break;
        }


        switch (sharedPreferences.getString("Sex","necunoscut")){
            case "Feminin":
                f.setChecked(true);
                m.setEnabled(false);
                break;
            case "Masculin":
                m.setChecked(true);
                f.setEnabled(false);
                break;
            default: m.setEnabled(false);
            f.setEnabled(false);
            break;
        }


        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.app_name,R.string.app_name);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile) {
                    intent = new Intent(Profile.this, Profile.class);
                    startActivity(intent);
                } else if (id == R.id.chat) {

                    intent = new Intent(Profile.this, ChatMap.class);
                    startActivity(intent);
                } else if (id == R.id.home) {
                    intent = new Intent(Profile.this, Main.class);
                    startActivity(intent);
                } else if (id == R.id.monitorizare) {
                    intent = new Intent(Profile.this, Monitoring.class);
                    startActivity(intent);
                }
                return true;

            }




        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
