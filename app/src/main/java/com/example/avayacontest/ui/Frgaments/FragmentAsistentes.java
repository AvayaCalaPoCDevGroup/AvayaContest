package com.example.avayacontest.ui.Frgaments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.avayacontest.Activities.MainActivity;
import com.example.avayacontest.Clases.Adapters.AdapterIntegrante;
import com.example.avayacontest.Clases.Utils;
import com.example.avayacontest.Models.GenericResponse;
import com.example.avayacontest.Models.Integrante;
import com.example.avayacontest.Models.IntegrantesResponse;
import com.example.avayacontest.R;
import com.example.avayacontest.WebMethods.WebMethods;
import com.example.avayacontest.ui.Dialogs.DialogIntegrante;
import com.example.avayacontest.ui.Dialogs.DialogUnregister;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class FragmentAsistentes extends BaseFragment {

    private ArrayList<Integrante> integrantes = new ArrayList<>();

    private EditText et_fragasis_buscar;
    private Button btn_fragasis_buscar;
    private ListView lv_fragasis_result;

    private AdapterIntegrante adapterIntegrante;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_asistentes, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SetViews(view);
    }

    private void SetViews(View view) {
        et_fragasis_buscar = view.findViewById(R.id.et_fragasis_buscar);
        btn_fragasis_buscar = view.findViewById(R.id.btn_fragasis_buscar);
        lv_fragasis_result = view.findViewById(R.id.lv_fragasis_result);

        adapterIntegrante = new AdapterIntegrante(getContext(), R.layout.unit_lv_integrante, integrantes);
        lv_fragasis_result.setAdapter(adapterIntegrante);

        lv_fragasis_result.setOnItemClickListener((parent, view1, position, id) -> {
            DialogIntegrante dialogIntegrante = new DialogIntegrante(getActivity(), integrantes.get(position), getActivity());
            dialogIntegrante.setCancelable(true);
            dialogIntegrante.setBtnMessages(getResources().getString(R.string.dialog_itegrante_quitar),getResources().getString(R.string.dialog_itegrante_continuar));
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
                            params.put("idIntegrante", integrantes.get(position).idIntegrante);
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
            dialogIntegrante.show();
        });

        btn_fragasis_buscar.setOnClickListener(v -> {

            //PENDIENTE: aqui falta que el endpoint solo regrese los integrantes buscados en la sala actual
            HashMap<String, String> params = new HashMap<>();
            params.put("action", "likeNombreOApellido");
            params.put("idSala ", ((MainActivity)getActivity()).mSala.idSala.toString());
            params.put("phrase", et_fragasis_buscar.getText().toString());
            new DoWebMethodsAsync(WebMethods.URL_SERVER, 0).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
        });
    }

    private class DoWebMethodsAsync extends AsyncTask<HashMap<String, String>, Void, String> {

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
         *
         * @param url    url del endpoint
         * @param option opciones que indican el metodo a consumir y la respuesta esperada
         *               0 - Listar todos los integrantes de la busqueda
         *               1 - quitar
         */
        public DoWebMethodsAsync(String url, int option) {
            this.option = option;
            this.url = url;
        }

        @Override
        protected String doInBackground(HashMap<String, String>... hashMaps) {
            String resp = "";
            resp = WebMethods.requestPostMethodAvayaEndpoint(hashMaps[0], this.url);
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
            switch (option) {
                case 0:
                    IntegrantesResponse integrantesResponse = new Gson().fromJson(s, IntegrantesResponse.class);

                    if(integrantesResponse.integrantes != null && integrantesResponse.integrantes.size()>0) {
                        for (Integrante unit : integrantesResponse.integrantes) {
                            String imageDataBytes = unit.foto.substring(unit.foto.indexOf(",") + 1);
                            unit.bitmap = Utils.ConvertToImage(imageDataBytes);
                            Log.e("FragmentAsistentes", "onPostExecute - Generando imagen de " + unit.nombre);
                        }
                    } else {
                        Toast.makeText(getContext(), integrantesResponse.message, Toast.LENGTH_SHORT).show();
                    }
                    actualizarLista(integrantesResponse.integrantes);
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

    /**
     * Metodo para actualizar el lv de asistentes con los resultados de la busqueda
     * @param participantes
     */
    private void actualizarLista(ArrayList<Integrante> participantes) {
        integrantes.clear();
        if (participantes != null)
            integrantes.addAll(participantes);
        adapterIntegrante.notifyDataSetChanged();
        //Actualizar listview
    }

}
