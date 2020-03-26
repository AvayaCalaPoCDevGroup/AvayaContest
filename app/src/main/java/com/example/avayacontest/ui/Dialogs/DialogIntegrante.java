package com.example.avayacontest.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.avayacontest.Clases.Utils;
import com.example.avayacontest.Models.Integrante;
import com.example.avayacontest.R;

import org.w3c.dom.Text;

public class DialogIntegrante extends Dialog {

    private Integrante integrante;
    private ImageView   iv_dialogIntegrante_foto;
    private TextView    tv_dialogIntegrante_name;
    private TextView    tv_dialogIntegrante_empresa;
    private TextView    tv_dialogIntegrante_movil;
    private TextView    tv_dialogIntegrante_correo;
    private TextView    tv_dialogIntegrante_hora;
    private Button      btn_dialogIntegrante_quitar;
    private Button      btn_dialogIntegrante_continuar;


    public DialogIntegrante(@NonNull Context context, Integrante integrante) {
        super(context);
        this.integrante = integrante;
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
        tv_dialogIntegrante_empresa   .setText(integrante.emmpresa);
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
            dismiss();
        });
    }
}
