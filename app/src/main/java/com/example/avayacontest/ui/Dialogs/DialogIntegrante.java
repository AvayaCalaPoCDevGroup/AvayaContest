package com.example.avayacontest.ui.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    //private TextView    tv_dialogIntegrante_hora;
    private TextView    tv_dialogIntegrante_alert;
    private Button      btn_dialogIntegrante_one;
    private Button      btn_dialogIntegrante_two;

    private Activity mActivity;
    private String message_btn_one;
    private String message_btn_two;
    private OnActionListener mOnActionListener;

    private int alertVisibility = View.GONE;


    public DialogIntegrante(@NonNull Context context, Integrante integrante, Activity mActivity) {
        super(context);
        this.integrante = integrante;
        this.mActivity = mActivity;
    }

    public interface OnActionListener {
        void onActionOne();
        void onActionTwo();
    }

    /**
     * Listener para los clicks de los botones del dialog
     * @param l
     */
    public void setOnActionListener( OnActionListener l){
        mOnActionListener = l;
    }

    /**
     * Metodo para poner texto a los dos botones que tiene el dialogo
     * @param message_btn_one
     * @param message_btn_two
     */
    public void setBtnMessages(String message_btn_one, String message_btn_two){
        this.message_btn_one = message_btn_one;
        this.message_btn_two = message_btn_two;
    }

    /**
     * Metodo para mostrar u ocultar el mensaje de integrante en sala
     * @param v View.VISIBLE o View.GONE
     */
    public void setAlertVisibility(int v){
        this.alertVisibility = v;
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
        //tv_dialogIntegrante_hora        = findViewById(R.id.tv_dialogIntegrante_hora      );
        tv_dialogIntegrante_alert       = findViewById(R.id.tv_dialogIntegrante_alert);
        btn_dialogIntegrante_one     = findViewById(R.id.btn_dialogIntegrante_one   );
        btn_dialogIntegrante_two  = findViewById(R.id.btn_dialogIntegrante_two);

        tv_dialogIntegrante_name      .setText(integrante.nombre);
        tv_dialogIntegrante_empresa   .setText(integrante.empresa);
        tv_dialogIntegrante_movil     .setText(integrante.telefonoMovil);
        tv_dialogIntegrante_correo    .setText(integrante.correo);
        //tv_dialogIntegrante_hora      .setText(integrante.horaDeIngresoSala);

        btn_dialogIntegrante_one.setText(message_btn_one);
        btn_dialogIntegrante_two.setText(message_btn_two);

        tv_dialogIntegrante_alert.setVisibility(alertVisibility);
        

        String imageDataBytes = integrante.foto.substring(integrante.foto.indexOf(",")+1);
        Bitmap bitmap = Utils.ConvertToImage(imageDataBytes);
        iv_dialogIntegrante_foto.setImageBitmap(bitmap);

        btn_dialogIntegrante_two.setOnClickListener(v -> {
            mOnActionListener.onActionTwo();
        });
        btn_dialogIntegrante_one.setOnClickListener(v -> {
            mOnActionListener.onActionOne();
        });
    }

}
