package com.example.avayacontest.ui.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.avayacontest.Activities.MainActivity;
import com.example.avayacontest.Clases.Constants;
import com.example.avayacontest.Clases.Utils;
import com.example.avayacontest.Models.GenericResponse;
import com.example.avayacontest.Models.Integrante;
import com.example.avayacontest.R;
import com.example.avayacontest.WebMethods.WebMethods;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.HashMap;

public class DialogIntegrante extends Dialog {

    private Integrante integrante;
    /**
     * Esta variable la ponemos en true si se le va a quitar la asistencia, asi cuando el dialog se cierre cachamos desde donde fue llamado si se debe eliminar
     */
    public boolean deleteIntegrante = false;

    private ImageView   iv_dialogIntegrante_foto;
    private TextView    tv_dialogIntegrante_name;
    private TextView    tv_dialogIntegrante_empresa;
    private TextView    tv_dialogIntegrante_movil;
    private TextView    tv_dialogIntegrante_correo;
    private TextView    tv_dialogIntegrante_hora;
    private Button      btn_dialogIntegrante_quitar;
    private Button      btn_dialogIntegrante_continuar;

    private Activity mActivity;


    public DialogIntegrante(@NonNull Context context, Integrante integrante, Activity mActivity) {
        super(context);
        this.integrante = integrante;
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_integrante);
        setViews();
    }

    private void setViews() {
        iv_dialogIntegrante_foto        = findViewById(R.id.iv_dialogIntegrante_foto      );
        tv_dialogIntegrante_name        = findViewById(R.id.tv_dialogIntegrante_name      );
        tv_dialogIntegrante_empresa     = findViewById(R.id.tv_dialogIntegrante_empresa   );
        tv_dialogIntegrante_movil       = findViewById(R.id.tv_dialogIntegrante_movil     );
        tv_dialogIntegrante_correo      = findViewById(R.id.tv_dialogIntegrante_correo    );
        tv_dialogIntegrante_hora        = findViewById(R.id.tv_dialogIntegrante_hora      );
        btn_dialogIntegrante_quitar     = findViewById(R.id.btn_dialogIntegrante_quitar   );
        btn_dialogIntegrante_continuar  = findViewById(R.id.btn_dialogIntegrante_continuar);

        tv_dialogIntegrante_name      .setText(integrante.nombre);
        tv_dialogIntegrante_empresa   .setText(integrante.empresa);
        tv_dialogIntegrante_movil     .setText(integrante.telefonoMovil);
        tv_dialogIntegrante_correo    .setText(integrante.correo);
        tv_dialogIntegrante_hora      .setText(integrante.horaDeIngresoSala);

        String imageDataBytes = integrante.foto.substring(integrante.foto.indexOf(",")+1);
        Bitmap bitmap = Utils.ConvertToImage(imageDataBytes);
        iv_dialogIntegrante_foto.setImageBitmap(bitmap);

        btn_dialogIntegrante_continuar.setOnClickListener(v -> {
            dismiss();
        });
        btn_dialogIntegrante_quitar.setOnClickListener(v -> {
            HashMap<String,String> parametros = new HashMap<>();
            parametros.put("action", "quitarAsistenciaSala");
            parametros.put("idEvento", ((MainActivity)mActivity).mEvento.idEvento.toString());
            parametros.put("idSala", ((MainActivity)mActivity).mSala.idSala.toString());
            parametros.put("idIntegrante", ""+integrante.idIntegrante);
            parametros.put("comentarios", ""); //Aqui falta especificar el motivo por el que se quita la asistencia
            new DoWebMethodsAsync(WebMethods.URL_SERVER, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, parametros);
        });
    }

    private class DoWebMethodsAsync extends AsyncTask<HashMap<String,String>,Void,String> {

        private int option = -1;
        private String url = "";
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getContext(), getContext().getResources().getString(R.string.dialog_wait_title), getContext().getResources().getString(R.string.dialog_wait_msg), true);
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
            Log.e("FragmentRegistro", "Response: " + s);
            if(s.equals("-1")) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.web_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            dialog.dismiss();
            switch (option){
                case 0:
                    GenericResponse quitarResponse = new Gson().fromJson(s,GenericResponse.class);
                    Toast.makeText(getContext(),quitarResponse.message,Toast.LENGTH_SHORT).show();
                    //PENDIENTE: falta validar el esttusde la respuesta, ya qu eel end point siempre regresa code=400
                    //Si se quito correctamente la asistencia quitamos al integrante de la lista
                    deleteIntegrante = true;
                    DialogIntegrante.this.dismiss();
                    break;
            }
        }
    }
}
