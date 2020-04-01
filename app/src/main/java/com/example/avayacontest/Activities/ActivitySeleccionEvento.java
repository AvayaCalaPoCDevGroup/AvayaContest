package com.example.avayacontest.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.avayacontest.BuildConfig;
import com.example.avayacontest.Clases.Adapters.AdapterEventos;
import com.example.avayacontest.Clases.Adapters.AdapterSalas;
import com.example.avayacontest.Clases.Constants;
import com.example.avayacontest.Models.Evento;
import com.example.avayacontest.Models.Eventos;
import com.example.avayacontest.Models.Sala;
import com.example.avayacontest.Models.Salas;
import com.example.avayacontest.R;
import com.example.avayacontest.WebMethods.WebMethods;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ActivitySeleccionEvento extends AppCompatActivity {

    private ArrayList<Evento> eventos = new ArrayList<>();
    private ArrayList<Sala> salas = new ArrayList<>();

    private Spinner spnr_seleccion_evento;
    private Spinner spnr_seleccion_sala;
    private Button btn_seleccion_ir;

    private AdapterEventos adapterEventos;
    private AdapterSalas adapterSalas;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_evento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mSharedPreferences = getSharedPreferences(Constants.AVAYA_SHARED, 0);
        iniciarControles();
        inicializarListas();
        getEvents();
    }

    private void getEvents() {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "todosLosEventos");
        new DoWebMethod(WebMethods.URL_SERVER, 0).executeOnExecutor(THREAD_POOL_EXECUTOR,params);
    }

    private void getSalasByIdEvento(UUID id){
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "informacionPorEvento");
        params.put("idEvento", id.toString());
        new DoWebMethod(WebMethods.URL_SERVER, 1).executeOnExecutor(THREAD_POOL_EXECUTOR,params);
    }

    private void iniciarControles() {
        spnr_seleccion_evento = findViewById(R.id.spnr_seleccion_evento);
        spnr_seleccion_sala = findViewById(R.id.spnr_seleccion_sala);
        btn_seleccion_ir = findViewById(R.id.btn_seleccion_ir);
    }

    private void inicializarListas() {
        //eventos
        eventos.add(new Evento());
        adapterEventos = new AdapterEventos(this, R.layout.unit_spnr_evento, eventos);
        spnr_seleccion_evento.setAdapter(adapterEventos);
        spnr_seleccion_evento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSalasByIdEvento(eventos.get(position).idEvento);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //salas
        salas.add(new Sala());
        adapterSalas = new AdapterSalas(this, R.layout.unit_spnr_evento, salas);
        spnr_seleccion_sala.setAdapter(adapterSalas);
        spnr_seleccion_sala.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(salas.get(position).estatusSala.equals("TERMINATED")){ //Aqui falta quitar las inactivas
                    if(position != 0)
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msg_activitiseleccion_sala_invalid), Toast.LENGTH_SHORT).show();
                    spnr_seleccion_sala.setSelection(0);
                    btn_seleccion_ir.setVisibility(View.GONE);
                } else {
                    btn_seleccion_ir.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_seleccion_ir.setOnClickListener(v -> {

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(Constants.SHARED_EVENT_JSON, new Gson().toJson(eventos.get(spnr_seleccion_evento.getSelectedItemPosition())));
            editor.putString(Constants.SHARED_SALA_JSON, new Gson().toJson(salas.get(spnr_seleccion_sala.getSelectedItemPosition())));
            editor.commit();

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        });

    }

    private class DoWebMethod extends AsyncTask<HashMap<String,String>, Void, String >{

        private String URL = "";
        private int option = -1;
        ProgressDialog dialog;

        /**
         * Constructor del AsyncTask para consumir los metodos web
         * @param url url del endpoint
         * @param option 0 Eventos, 1 Salas
         */
        public DoWebMethod(String url, int option){
            URL = url;
            this.option = option;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivitySeleccionEvento.this, getResources().getString(R.string.dialog_wait_title), getResources().getString(R.string.dialog_wait_msg), true);
        }

        @Override
        protected String doInBackground(HashMap<String, String>... hashMaps) {

            String resp = WebMethods.requestPostMethodAvayaEndpoint(hashMaps[0], URL);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("ActivitySeleccionEvento", "On post Execute, resp = " + s);
            Log.e("FragmentRegistro", "Response: " + s);
            if(s.equals("-1")) {
                Toast.makeText(ActivitySeleccionEvento.this, getResources().getString(R.string.web_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            switch (option){
                case 0:
                    ActualizaEventos(new Gson().fromJson(s, Eventos.class).eventos);
                    break;
                case 1:
                    ActualizaSalas(new Gson().fromJson(s, Salas.class).salas);
                    break;
                default:
                    Log.e("ActivitySeleccionEvento", "invalid option");
            }
            dialog.dismiss();
        }
    }

    private void ActualizaEventos(ArrayList<Evento> eventos_act){
        eventos.clear();
        eventos.add(new Evento());
        if(eventos_act != null) //Llega null cuando no existe ningub evento
            eventos.addAll(eventos_act);
        adapterEventos.notifyDataSetChanged();
    }

    private void ActualizaSalas(ArrayList<Sala> salas_act){
        salas.clear();
        salas.add(new Sala());
        if(salas_act != null) //Llega null cuando el evento no existe
            salas.addAll(salas_act);
        adapterSalas.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_seleccion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_about:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("AVAYA CONTEST - version " + BuildConfig.VERSION_NAME);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
