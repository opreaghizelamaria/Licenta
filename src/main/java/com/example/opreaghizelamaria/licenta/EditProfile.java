package com.example.opreaghizelamaria.licenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class EditProfile extends AppCompatActivity {
    Button save,back;
    SharedPreferences sharedPreferences;
    TextView sarcina;
    private EditText numar,varsta,nume,afectiuni,medicamente;
    private RadioButton a,b,ab,o,f,m,poz,neg;
    private String grupaSanguina,rhpozneg,sexfm;
    private Switch s1,s2,s3;
    Intent intent;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);


        sharedPreferences=getSharedPreferences("Start",MODE_PRIVATE);
        sarcina=(TextView)findViewById(R.id.textView4e);
        sarcina.setVisibility(sarcina.INVISIBLE);
        s3=(Switch)findViewById(R.id.switch1e);
        s3.setVisibility(s3.INVISIBLE);

        numar=(EditText)findViewById(R.id.numare);
        nume=(EditText)findViewById(R.id.numeUtilize);
        varsta=(EditText)findViewById(R.id.varstae);
        medicamente=(EditText)findViewById(R.id.medicamentee);
        medicamente.setEnabled(false);
        afectiuni=(EditText)findViewById(R.id.afectiunie);
        afectiuni.setEnabled(false);
        a=(RadioButton)findViewById(R.id.ae);
        b=(RadioButton)findViewById(R.id.be);
        ab=(RadioButton)findViewById(R.id.abe);
        o=(RadioButton)findViewById(R.id.oe);
        f=(RadioButton)findViewById(R.id.fe);
        m=(RadioButton)findViewById(R.id.me);
        poz=(RadioButton)findViewById(R.id.poze);
        neg=(RadioButton)findViewById(R.id.nege);
        s1=(Switch)findViewById(R.id.mede);
        s2=(Switch)findViewById(R.id.afecte);

        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(s1.isChecked())
                    medicamente.setEnabled(true);
                if(!s1.isChecked()){
                    medicamente.setEnabled(false);
                    medicamente.setText("");
                }

            }
        });
        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(s2.isChecked())
                    afectiuni.setEnabled(true);
                if(!s2.isChecked()){
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
                s3.setChecked(false);
            }
        });
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sarcina.setVisibility(sarcina.INVISIBLE);
                s3.setVisibility(s3.INVISIBLE);
            }
        });
        numar.setText(sharedPreferences.getString("Numar","necunoscut"));
        nume.setText(sharedPreferences.getString("Nume","necunoscut"));
        varsta.setText(sharedPreferences.getString("Varsta","necunoscut"));
        if(!sharedPreferences.getString("Medicamente","").equals(""))
        { s1.setChecked(true);
        medicamente.setText(sharedPreferences.getString("Medicamente",""));}
        if(!sharedPreferences.getString("Afectiuni","").equals(""))
        {s2.setChecked(true);
            afectiuni.setText(sharedPreferences.getString("Afectiuni",""));}
        if(sharedPreferences.getString("Sarcina","nu").equals("da")){
            { sarcina.setVisibility(sarcina.VISIBLE);
            s3.setVisibility(s3.VISIBLE);
            s3.setChecked(true);}
        }
        switch (sharedPreferences.getString("GrupaSanguina","necunoscut")){
            case "A":
                a.setChecked(true);
                  break;
            case "B":
                b.setChecked(true);
               break;
            case "0":
                 o.setChecked(true); break;
            case "AB":
                 ab.setChecked(true);
                break;
            default:
                break;
        }


        switch (sharedPreferences.getString("Rh","necunoscut")){
            case "+":
                poz.setChecked(true);
                break;
            case "-":
                neg.setChecked(true);
                break;
            default:
                break;
        }


        switch (sharedPreferences.getString("Sex","necunoscut")){
            case "Feminin":
                sarcina.setVisibility(sarcina.VISIBLE);
                s3.setVisibility(s3.VISIBLE);
                f.setChecked(true);
                break;
            case "Masculin":
                m.setChecked(true);
                break;
            default:
                break;
        }


        save=(Button)findViewById(R.id.salvaree);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                grupaSanguina="null";
                sexfm="null";
                rhpozneg="null";
                if(!numar.getText().toString().equals(""))
                editor.putString("Numar",numar.getText().toString());
                if(!nume.getText().toString().equals(""))
                    editor.putString("Nume",nume.getText().toString());
                if(!varsta.getText().toString().equals(""))
                    editor.putString("Varsta",varsta.getText().toString());
                if(s1.isChecked())
                    if(!medicamente.getText().toString().equals(""))
                    editor.putString("Medicamente",medicamente.getText().toString());
                if(s2.isChecked())
                    if(!afectiuni.getText().toString().equals(""))
                        editor.putString("Afectiuni",afectiuni.getText().toString());
                editor.putString("Afectiuni",afectiuni.getText().toString());
                if(a.isChecked())
                    grupaSanguina="A";
                if(b.isChecked())
                    grupaSanguina="B";
                if(ab.isChecked())
                    grupaSanguina="AB";
                if(o.isChecked())
                    grupaSanguina="0";
                if(grupaSanguina!="null")
                editor.putString("GrupaSanguina",grupaSanguina);
                if(f.isChecked()) {
                    sexfm = "Feminin";
                    if(s3.isChecked()){ System.out.println("hbvb");
                        editor.putString("Sarcina","da");

                    }else editor.putString("Sarcina","nu");
                }
                if(m.isChecked())
                    sexfm="Masculin";
                if(sexfm!="null")
                editor.putString("Sex",sexfm);
                if(poz.isChecked())
                    rhpozneg="+";
                if(neg.isChecked())
                    rhpozneg="-";
                if(rhpozneg!="null")
                editor.putString("Rh",rhpozneg);
                editor.apply();
                intent=new Intent(EditProfile.this,Profile.class);

                startActivity(intent);
            }
        });
        back=(Button)findViewById(R.id.inapoi);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(EditProfile.this,Profile.class);
                startActivity(intent);
            }
        });

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
                    intent = new Intent(EditProfile.this, Profile.class);
                    startActivity(intent);
                } else if (id == R.id.chat) {

                    intent = new Intent(EditProfile.this, ChatMap.class);
                    startActivity(intent);
                } else if (id == R.id.home) {
                    intent = new Intent(EditProfile.this, Main.class);
                    startActivity(intent);
                } else if (id == R.id.monitorizare) {
                    intent = new Intent(EditProfile.this, Monitoring.class);
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
