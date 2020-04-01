package com.example.avayacontest.ui.Frgaments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.avayacontest.Activities.MainActivity;
import com.example.avayacontest.Interfaces.IScanResultListener;
import com.example.avayacontest.Models.Integrante;
import com.example.avayacontest.Models.IntegrantesResponse;
import com.example.avayacontest.Models.GenericResponse;
import com.example.avayacontest.Models.Sala;
import com.example.avayacontest.R;
import com.example.avayacontest.WebMethods.WebMethods;
import com.example.avayacontest.ui.Dialogs.DialogIntegrante;
import com.example.avayacontest.ui.Dialogs.DialogUnregister;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class FragmentRegistro extends BaseFragment implements IScanResultListener {

    private ImageView iv_fragmentreg_scan;
    private final int CON_REGISTRO_Y_EN_SALA = 2;
    private final int CON_REGISTRO_NO_EN_SALA = 1;
    private final int PRIMER_REGISTRO = 0;

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
            UUID idIntegrante = UUID.fromString(result);
            HashMap<String, String> params = new HashMap<>();
            params.put("action", "integrantesPorID");
            params.put("integranteId", idIntegrante.toString());
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

        /**
         * Clase para consumir los metodos del endpoint en segundo plano
         * @param url url del endpoint
         * @param option opciones que indican el metodo a consumir y la respuesta esperada
         *               0 - Buscar Integrante por ID
         *               1 - Solicitud de registro, en onpostexecute se espera la respuesta generica
         */
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
            if(s.equals("-1")) {
                Toast.makeText(getContext(), getResources().getString(R.string.web_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            switch (option){
                case 0:
                    Integrante integrante = new Gson().fromJson(s, Integrante.class);
                    if(integrante.code == 400){
                        Toast.makeText(getContext(),integrante.message,Toast.LENGTH_SHORT).show();
                    } else {
                        //Vemos en la informacion del integrante si es que ya esta registrado en la sala actual
                        DialogIntegrante dialogIntegrante = new DialogIntegrante(getActivity(), integrante, getActivity());
                        int isOnSala = buscarIntegranteEnSala(integrante.salas);
                        switch(isOnSala){
                            case PRIMER_REGISTRO:
                                dialogIntegrante.setBtnMessages(getResources().getString(R.string.dialog_itegrante_cancelar),getResources().getString(R.string.dialog_itegrante_registrar));
                                dialogIntegrante.setOnActionListener(new DialogIntegrante.OnActionListener() {
                                    @Override
                                    public void onActionOne() {
                                        dialogIntegrante.dismiss();
                                    }

                                    @Override
                                    public void onActionTwo() {
                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("action", "asistencia");
                                        params.put("idSala", ((MainActivity)getActivity()).mSala.idSala.toString());
                                        params.put("idIntegrante", integrante.idIntegrante);
                                        new DoWebMethodsAsync(WebMethods.URL_SERVER,1).executeOnExecutor(THREAD_POOL_EXECUTOR,params);
                                        dialogIntegrante.dismiss();
                                    }
                                });
                                break;
                            case CON_REGISTRO_NO_EN_SALA: //Aqui el integrante ya estaba en la sala pero se le quito la asistencia
                                dialogIntegrante.setBtnMessages(getResources().getString(R.string.dialog_itegrante_cancelar),getResources().getString(R.string.dialog_itegrante_registrar));
                                dialogIntegrante.setOnActionListener(new DialogIntegrante.OnActionListener() {
                                    @Override
                                    public void onActionOne() {
                                        dialogIntegrante.dismiss();
                                    }

                                    @Override
                                    public void onActionTwo() {
                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("action", "quitarAsistenciaSala");
                                        params.put("idEvento", ((MainActivity)getActivity()).mEvento.idEvento.toString());
                                        params.put("idIntegrante", integrante.idIntegrante);
                                        params.put("idSala", ((MainActivity)getActivity()).mSala.idSala.toString());
                                        params.put("comentarios", "TRUE"); //CON TRUE SE LE REGRESA LA ASISTENCIA
                                        new DoWebMethodsAsync(WebMethods.URL_SERVER,1).executeOnExecutor(THREAD_POOL_EXECUTOR,params);
                                        dialogIntegrante.dismiss();
                                    }
                                });
                                break;
                            case CON_REGISTRO_Y_EN_SALA: //aqui el integrante ya esta en la sala, y un segundo integrante intenta entrar con la misma credencial
                                dialogIntegrante.setBtnMessages(getResources().getString(R.string.dialog_itegrante_quitar),getResources().getString(R.string.dialog_itegrante_continuar));
                                dialogIntegrante.setAlertVisibility(View.VISIBLE);
                                dialogIntegrante.setOnActionListener(new DialogIntegrante.OnActionListener() {
                                    @Override
                                    public void onActionOne() {
                                        DialogUnregister dialogUnregister = new DialogUnregister(getActivity());
                                        dialogUnregister.setCancelable(false);
                                        dialogUnregister.setOnDismissListener(dialog -> {
                                            boolean unregister = ((DialogUnregister)dialog).unregister;
                                            if(unregister){
                                                HashMap<String, String> params = new HashMap<>();
                                                params.put("action", "quitarAsistenciaSala");
                                                params.put("idEvento", ((MainActivity)getActivity()).mEvento.idEvento.toString());
                                                params.put("idIntegrante", integrante.idIntegrante);
                                                params.put("idSala", ((MainActivity)getActivity()).mSala.idSala.toString());
                                                params.put("comentarios", ((DialogUnregister)dialog).cause); //CON TRUE SE LE REGRESA LA ASISTENCIA
                                                new DoWebMethodsAsync(WebMethods.URL_SERVER,1).executeOnExecutor(THREAD_POOL_EXECUTOR,params);
                                                dialogIntegrante.dismiss();
                                            } else {

                                            }
                                        });
                                        dialogUnregister.show();
                                    }

                                    @Override
                                    public void onActionTwo() {
                                        dialogIntegrante.dismiss();
                                    }
                                });
                                break;
                        }
                        dialogIntegrante.setCancelable(false);
                        dialogIntegrante.show();
                    }
                    break;
                case 1:
                    GenericResponse genericResponse = new Gson().fromJson(s,GenericResponse.class);
                    String msg = "";
                    if(genericResponse.code == 200){
                        msg = getResources().getString(R.string.web_response_ok);
                    } else {
                        msg = genericResponse.message;
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    break;
            }
            dialog.dismiss();
        }
    }

    private int buscarIntegranteEnSala(ArrayList<Sala> salas) {
        UUID idsala = ((MainActivity)getActivity()).mSala.idSala;
        for (Sala sala: salas) {
            if(sala.idSala.equals(idsala)){
                if(sala.asistencia.equals("TRUE"))
                    return CON_REGISTRO_Y_EN_SALA;
                else
                    return CON_REGISTRO_NO_EN_SALA;
            }
        }
        return PRIMER_REGISTRO;
    }
}
