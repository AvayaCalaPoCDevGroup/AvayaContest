package com.example.avayacontest.Clases.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.avayacontest.Models.Evento;
import com.example.avayacontest.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterEventos extends ArrayAdapter<Evento> {

    ArrayList<Evento> eventos;
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public AdapterEventos(@NonNull Context context, int resource, @NonNull ArrayList<Evento> objects) {
        super(context, resource, objects);
        eventos = objects;
    }

    private View GetCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.unit_spnr_evento,parent,false);

        TextView tv_unitevento_nombre = view.findViewById(R.id.tv_unitevento_nombre);
        TextView tv_unitevento_fecha = view.findViewById(R.id.tv_unitevento_fecha);
        TextView tv_unitevento_hora = view.findViewById(R.id.tv_unitevento_hora);

        if(position == 0){
            tv_unitevento_fecha.setVisibility(View.GONE);
            tv_unitevento_hora.setVisibility(View.GONE);
        } else {
            dateFormatter.format(eventos.get(position).fechaEvento);

            tv_unitevento_fecha.setText(dateFormatter.format(eventos.get(position).fechaEvento));
            tv_unitevento_hora.setText(eventos.get(position).horaEvento);
        }

        tv_unitevento_nombre.setText(eventos.get(position).nombreEvento);

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return GetCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return GetCustomView(position, convertView, parent);
    }
}
