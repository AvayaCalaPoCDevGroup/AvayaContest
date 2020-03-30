package com.example.avayacontest.Clases.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.avayacontest.Models.Evento;
import com.example.avayacontest.Models.Sala;
import com.example.avayacontest.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterSalas extends ArrayAdapter<Sala> {

    ArrayList<Sala> salas;
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public AdapterSalas(@NonNull Context context, int resource, @NonNull ArrayList<Sala> objects) {
        super(context, resource, objects);
        salas = objects;
    }

    private View GetCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());

        //Voy a usar el mismo layput por que ocupa los mismo datos que evento;
        View view = inflater.inflate(R.layout.unit_spnr_sala,parent,false);

        TextView tv_unitevento_nombre = view.findViewById(R.id.tv_unitevento_nombre);
        TextView tv_unitevento_fecha = view.findViewById(R.id.tv_unitevento_fecha);
        TextView tv_unitevento_hora = view.findViewById(R.id.tv_unitevento_hora);
        LinearLayout ll_unitevento_main = view.findViewById(R.id.ll_unitevento_main);

        if(position == 0){
            tv_unitevento_fecha.setVisibility(View.GONE);
            tv_unitevento_hora.setVisibility(View.GONE);
        } else {
            dateFormatter.format(salas.get(position).fechaSala);

            tv_unitevento_fecha.setText(getContext().getResources().getString(R.string.fecha) + dateFormatter.format(salas.get(position).fechaSala));
            tv_unitevento_hora.setText(getContext().getResources().getString(R.string.hora) + salas.get(position).horaSala);
        }

        if(position == 0){//El elemento seleccione siempre va le el color activo
            ll_unitevento_main.setBackgroundResource(R.drawable.bg_spnr_unitsala);
        } else if(salas.get(position).estatusSala.equals("TERMINATED")){ //Condicion para determinar si la sala sigue activa.
            ll_unitevento_main.setBackgroundResource(R.drawable.bg_spnr_unit_terminated);
        } else {
            ll_unitevento_main.setBackgroundResource(R.drawable.bg_spnr_unitsala);
        }

        tv_unitevento_nombre.setText(salas.get(position).nombreSala);

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
