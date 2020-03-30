package com.example.avayacontest.Models;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.Base64;
import java.util.Date;

public class Integrante {
    public int code;
    public String message;

    public String idIntegrante;
    public String horaDeIngresoSala;
    public String foto;
    public String correo;
    public String telefonoMovil;
    public String estatusAsistencia;
    public String nombre;
    public Date fechaDeIngresoSala;

    //Esta propiedad la manda en busqueda general
    public String empresa;

    //Custom
    public Bitmap bitmap;
}
