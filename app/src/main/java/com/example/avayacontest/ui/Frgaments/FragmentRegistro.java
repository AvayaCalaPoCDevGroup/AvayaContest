package com.example.avayacontest.ui.Frgaments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.avayacontest.Activities.ActivitySeleccionEvento;
import com.example.avayacontest.Activities.MainActivity;
import com.example.avayacontest.Clases.Constants;
import com.example.avayacontest.Interfaces.IScanResultListener;
import com.example.avayacontest.Models.Integrante;
import com.example.avayacontest.Models.IntegrantesResponse;
import com.example.avayacontest.Models.ResgistroResponse;
import com.example.avayacontest.Models.Sala;
import com.example.avayacontest.R;
import com.example.avayacontest.WebMethods.WebMethods;
import com.example.avayacontest.ui.Dialogs.DialogIntegrante;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.HashMap;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class FragmentRegistro extends BaseFragment implements IScanResultListener {

    private ImageView iv_fragmentreg_scan;
    private int LAST_IDINTEGRANTE = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_registro, container, false);
        ((MainActivity)getActivity()).setmScanResultListener(this); //Nos registrmos al listener que recibe los resultados del scan en main activity
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv_fragmentreg_scan = view.findViewById(R.id.iv_fragmentreg_scan);
        iv_fragmentreg_scan.setOnClickListener(v -> {
            new IntentIntegrator(getActivity()).initiateScan();
        });
    }

    @Override
    public void onScanResult(String result) {
        //Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
        try{
            int idIntegrante = Integer.parseInt(result);
            LAST_IDINTEGRANTE = idIntegrante;
            HashMap<String, String> params = new HashMap<>();
            params.put("action", "asistencia");
            params.put("idSala", ((MainActivity)getActivity()).mSala.idSala.toString());
            params.put("idIntegrante", ""+idIntegrante);
            new DoWebMethodsAsync(WebMethods.URL_SERVER, 0).executeOnExecutor(THREAD_POOL_EXECUTOR,params);

        } catch (Exception ex){
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.fragment_reg_invlaidqr), Toast.LENGTH_SHORT).show();
        }
    }

    private class DoWebMethodsAsync extends AsyncTask<HashMap<String,String>,Void,String> {

        private int option = -1;
        private String url = "";
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.dialog_wait_title), getResources().getString(R.string.dialog_wait_msg), true);
        }

        public DoWebMethodsAsync(String url, int option){
            this.option = option;
            this.url = url;
        }

        @Override
        protected String doInBackground(HashMap<String, String>... hashMaps) {
            String resp = "";
            resp = WebMethods.requestPostMethodAvayaEndpoint(hashMaps[0] , this.url);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("FragmentRegistro", "Response: " + s);
            switch (option){
                case 0:
                    ResgistroResponse resgistroResponse = new Gson().fromJson(s,ResgistroResponse.class);
                    if(resgistroResponse.message.equals("El participante ya se ha registrado a la sala") || resgistroResponse.code == 200){
                        if(resgistroResponse.code == 400){
                            //reproducir sonido y vibrar, registro doble
                        }
                        Toast.makeText(getContext(), resgistroResponse.message, Toast.LENGTH_SHORT).show();
                        HashMap<String, String> params = new HashMap<>() ;
                        params.put("action", "integrantesConAsistenciaSala");
                        params.put("idSala", ((MainActivity)getActivity()).mSala.idSala.toString());
                        new DoWebMethodsAsync(WebMethods.URL_SERVER, 1).executeOnExecutor(THREAD_POOL_EXECUTOR,params);
                    } else if (resgistroResponse.message.equals("El participante no se ha registrado al Evento")){
                        Toast.makeText(getContext(), getResources().getString(R.string.fragment_reg_invaliduser), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.fragment_reg_unkonwerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    IntegrantesResponse integrantes = new Gson().fromJson(s, IntegrantesResponse.class);

                    Integrante integrante = null;
                    for(int i = 0; i < integrantes.participantes.size(); i++){
                        if(integrantes.participantes.get(i).idIntegrante == LAST_IDINTEGRANTE){
                            integrante = integrantes.participantes.get(i);
                            Log.e("FragmentRegistro", "Integrante encontrado");
                            break;
                        }
                    }
                    //Mostrar el integrante en el dialog
                    DialogIntegrante dialogIntegrante = new DialogIntegrante(getContext(), integrante);
                    dialogIntegrante.setCancelable(false);
                    dialogIntegrante.show();
                    break;
            }
            dialog.dismiss();
        }
    }
}
