package com.example.avayacontest.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.avayacontest.Clases.Constants;
import com.example.avayacontest.Models.Sala;
import com.example.avayacontest.R;
import com.example.avayacontest.ui.Frgaments.BaseFragment;
import com.example.avayacontest.ui.Frgaments.FragmentAsistentes;
import com.example.avayacontest.ui.Frgaments.FragmentRegistro;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.avayacontest.ui.Frgaments.SectionsPagerAdapter;
import com.google.gson.Gson;

import java.sql.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Sala mSala;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        mSharedPreferences = getSharedPreferences(Constants.AVAYA_SHARED,0);
        mSala = new Gson().fromJson(mSharedPreferences.getString(Constants.SHARED_EVENT_JSON, ""), Sala.class);
        //Aqui Creamos los fragments que se van a llenar en el tab
        ArrayList<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FragmentRegistro("Registro", mSala));
        fragmentList.add(new FragmentAsistentes("Asistentes", mSala));

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), fragmentList);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        TextView tv_mainactivity_evento = findViewById(R.id.tv_mainactivity_evento);
        tv_mainactivity_evento.setText("SALA: "+mSala.nombreSala);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_main_exit:
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Constants.SHARED_EVENT_JSON, "");
                editor.commit();
                Intent i = new Intent(getApplicationContext(), ActivitySeleccionEvento.class);
                startActivity(i);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}