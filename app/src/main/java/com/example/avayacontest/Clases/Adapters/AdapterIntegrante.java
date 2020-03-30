package com.example.avayacontest.Clases.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.avayacontest.Models.Integrante;
import com.example.avayacontest.R;

import java.util.ArrayList;

public class AdapterIntegrante extends ArrayAdapter<Integrante> {
    private ArrayList<Integrante> integrantes;

    // View lookup cache
    private static class ViewHolder {
        TextView tvNombre;
        TextView tvEmpresa;
        TextView tvTelefono;
        ImageView ivFoto;
    }

    public AdapterIntegrante(@NonNull Context context, int resource, @NonNull ArrayList<Integrante> objects) {
        super(context, resource, objects);
        integrantes = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Integrante integrante = integrantes.get(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.unit_lv_integrante,null);

            viewHolder.tvNombre = convertView.findViewById(R.id.tv_lvintegrante_nombre);
            viewHolder.tvEmpresa = convertView.findViewById(R.id.tv_lvintegrante_empresa);
            viewHolder.tvTelefono = convertView.findViewById(R.id.tv_lvintegrante_movil);
            viewHolder.ivFoto = convertView.findViewById(R.id.iv_lvintegrante_foto);

            result = convertView;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.tvNombre.setText(integrante.nombre);
        viewHolder.tvEmpresa.setText(integrante.empresa);
        viewHolder.tvTelefono.setText(integrante.telefonoMovil);
        viewHolder.ivFoto.setImageBitmap(integrante.bitmap);

        return convertView;
    }
}
