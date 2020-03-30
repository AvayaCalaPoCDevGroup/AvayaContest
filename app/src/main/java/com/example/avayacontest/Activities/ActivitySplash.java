package com.example.avayacontest.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.avayacontest.Clases.Constants;
import com.example.avayacontest.R;

public class ActivitySplash extends AppCompatActivity {

    final Handler handler = new Handler();
    private SharedPreferences sharedPreferencesAvaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferencesAvaya = getSharedPreferences(Constants.AVAYA_SHARED,0);
        //STimer.CURRENT_PERIOD = sharedPreferencesAvaya.getInt(Utils.AVAYA_INTERVALO,3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferencesAvaya.getString(Constants.SHARED_SALA_JSON,"").equals("")){
                    Intent i = new Intent(getApplicationContext(), ActivitySeleccionEvento.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 2000);
    }
}
