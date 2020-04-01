package com.example.avayacontest.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.avayacontest.R;

public class DialogUnregister extends Dialog {

    public boolean unregister = false;
    public String cause = "";

    private EditText et_dialogunregister_motivo;
    private Button btn_unregister_cancel;
    private Button btn_unregister_accept;

    public DialogUnregister(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_unregister);
        setViews();
    }

    private void setViews() {
        et_dialogunregister_motivo = findViewById(R.id.et_dialogunregister_motivo);
        btn_unregister_cancel = findViewById(R.id.btn_unregister_cancel);
        btn_unregister_accept= findViewById(R.id.btn_unregister_accept);

        btn_unregister_cancel.setOnClickListener(v -> {
            dismiss();
        });
        btn_unregister_accept.setOnClickListener(v->{
            if(et_dialogunregister_motivo.getText().toString().equals("")){
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.dialog_unregister_condition_one), Toast.LENGTH_SHORT).show();
                return;
            }

            cause = et_dialogunregister_motivo.getText().toString();
            unregister = true;
            dismiss();
        });
    }
}
