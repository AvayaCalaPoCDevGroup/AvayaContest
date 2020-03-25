package com.example.avayacontest.ui.Frgaments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.avayacontest.Models.Sala;
import com.example.avayacontest.R;

public class FragmentRegistro extends BaseFragment {
    public FragmentRegistro(String name, Sala sala) {
        super(name, sala);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_registro, container, false);

        return root;
    }

}
