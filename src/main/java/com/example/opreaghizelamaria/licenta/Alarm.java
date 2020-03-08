package com.example.opreaghizelamaria.licenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Alarm extends Activity {
    private CountDownTimer countDownTimer;
    private Boolean alarmOn = false;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.count_down);
        stopService(new Intent(Alarm.this, AccidentDetect.class));
        Button stop = findViewById(R.id.buttonStop);
        Button send = findViewById(R.id.SendNow);
        final TextView countDown = findViewById(R.id.timer);
        sharedPreferences = getSharedPreferences("Start", MODE_PRIVATE);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.bleep);
        AccidentDetect.alarmOn = true;
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                mp.release();
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Alarma", false);
                editor.apply();
                AccidentDetect.alarmOn = false;
                finish();


            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();

                mp.release();
                AccidentDetect.alarmOn = false;
                //alertDialog.cancel();
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Alarma", false);
                editor.apply();

                sendText();

                finish();

            }
        });

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mp.start();
                countDown.setText(Float.toString(millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {
                sendText();
                mp.release();
                AccidentDetect.alarmOn = false;
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Alarma", false);
                editor.apply();
                finish();
            }
        }.start();

    }

    public void sendText() {

        final SharedPreferences sharedPreferences = getSharedPreferences("Start", MODE_PRIVATE);
        String numar = sharedPreferences.getString("Numar", "necunoscut");
        String varsta = sharedPreferences.getString("Varsta", "necunoscut");
        String locatia = sharedPreferences.getString("Locatia", "necunoscut");
        String sex = sharedPreferences.getString("Sex", "necunoscut");
        String sarcina = sharedPreferences.getString("Sarcina", "necunoscut");
        String grupasanguina = sharedPreferences.getString("GrupaSanguina", "necunoscut");
        String rh = sharedPreferences.getString("Rh", "necunoscut");
        String afectiuni = sharedPreferences.getString("Afectiuni", "necunoscut");
        String medicamente = sharedPreferences.getString("Medicamente", "necunoscut");
        String mesaj = "A avut loc un accident rutier";
        if (locatia != "necunoscut")
            mesaj += "la locația " + locatia;
        mesaj += " victima ";
        if (varsta != "necunoscut"&&varsta!="")
            mesaj += " în vârstă de " + varsta + " ani";
        if (sex != "necunoscut"&&sex!="")
            mesaj += " de sex " + sex;
        if (sarcina == "da"&&sarcina!="")
            mesaj += " însărcinată";
        if (grupasanguina != "necunoscut"&&grupasanguina!="")
            mesaj += " grupa sanguină " + grupasanguina;
        if (rh != "necunoscut")
            mesaj += " Rh " + rh;
        if (afectiuni != "necunoscut"&&afectiuni!="")
            mesaj += " suferă de urmatoarele afecțiuni/alergii: " + afectiuni;
        if (medicamente != "necunoscut"&&medicamente!="")
            mesaj += " urmează tratament medicamentos " + medicamente;

        try {

            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

            getApplicationContext().registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context arg0, Intent arg1) {
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:

                                    break;
                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                                    break;
                                case SmsManager.RESULT_ERROR_NO_SERVICE:
                                    break;
                                case SmsManager.RESULT_ERROR_NULL_PDU:
                                    break;
                                case SmsManager.RESULT_ERROR_RADIO_OFF:
                                    break;
                            }
                        }
                    }, new IntentFilter(SENT));
            getApplicationContext().registerReceiver(
                    new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context arg0, Intent arg1) {
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:
                                    break;
                                case Activity.RESULT_CANCELED:
                                    break;
                            }
                        }
                    }, new IntentFilter(DELIVERED));
            SmsManager sms = SmsManager.getDefault();
            if(mesaj.length() > 100)
            {
                ArrayList<String> messagelist = sms.divideMessage(mesaj);
                //sms.sendMultipartTextMessage("113", null, messagelist, null, null);

                sms.sendMultipartTextMessage(numar, null, messagelist, null, null);
            }
            else
            {   //sms.sendTextMessage("113", null, mesaj, sentPI, deliveredPI);

                sms.sendTextMessage(numar, null, mesaj, sentPI, deliveredPI);
            }

}catch (Exception e)
{
    System.out.println(e.toString());
}


    }
}
