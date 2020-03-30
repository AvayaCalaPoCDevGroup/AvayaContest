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
import com.example.avayacontest.R;
import com.example.avayacontest.WebMethods.WebMethods;
import com.example.avayacontest.ui.Dialogs.DialogIntegrante;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.HashMap;
import java.util.UUID;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class FragmentRegistro extends BaseFragment implements IScanResultListener {

    private ImageView iv_fragmentreg_scan;
    private UUID LAST_IDINTEGRANTE = new UUID(0l,0l);

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
            LAST_IDINTEGRANTE = idIntegrante;
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
         *               0 - Resgistrar participante con el metodo Registrar Participante del endpoint
         *               1 - Buscar integrante en sala y registrarlo de no existir con el segundo metodo del endpoint (quitar registro, QuitarAsistencia comentarios TRUE)
         *               2 - Buscar Integrante en la sala actual y mostrar su informacion
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
                        DialogIntegrante dialogIntegrante = new DialogIntegrante(getActivity(), integrante, getActivity());
                        dialogIntegrante.setCancelable(false);
                        dialogIntegrante.show();
                    }
                    /*if(resgistroResponse.message.equals("El participante ya se ha registrado a la sala") || resgistroResponse.code == 200){
                        if(resgistroResponse.code == 400){
                            //reproducir sonido y vibrar, registro doble
                        }
                        //PENDIENTE: aqui falta procedimiento, ya que si un usuario no esta en la sala, pero ya se registro antes aun nos manda que ya se registro en la sala
                        Toast.makeText(getContext(), resgistroResponse.message, Toast.LENGTH_SHORT).show();
                        HashMap<String, String> params = new HashMap<>() ;
                        params.put("action", "integrantesConAsistenciaSala");
                        params.put("idSala", ((MainActivity)getActivity()).mSala.idSala.toString());
                        new DoWebMethodsAsync(WebMethods.URL_SERVER, 2).executeOnExecutor(THREAD_POOL_EXECUTOR,params);
                    } else if (resgistroResponse.message.equals("El participante no se ha registrado al Evento")){
                        Toast.makeText(getContext(), getResources().getString(R.string.fragment_reg_invaliduser), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.fragment_reg_unkonwerror), Toast.LENGTH_SHORT).show();
                    }*/
                    break;
                case 2:
                    /*IntegrantesResponse integrantes = new Gson().fromJson(s, IntegrantesResponse.class);

                    Integrante integrante = null;
                    for(int i = 0; i < integrantes.participantes.size(); i++){
                        if(integrantes.participantes.get(i).idIntegrante == LAST_IDINTEGRANTE){
                            integrante = integrantes.participantes.get(i);
                            Log.e("FragmentRegistro", "Integrante encontrado");
                            break;
                        }
                    }
                    //Mostrar el integrante en el dialog
                    DialogIntegrante dialogIntegrante = new DialogIntegrante(getActivity(), integrante, getActivity());
                    dialogIntegrante.setCancelable(false);
                    dialogIntegrante.show();*/
                    break;
            }
            dialog.dismiss();
        }
    }
}
