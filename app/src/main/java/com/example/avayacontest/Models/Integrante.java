package com.example.avayacontest.Models;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class Integrante {
    public int code;
    public String message;

    public String idIntegrante;
    public String empresa;
    public String horaDeIngresoSala;
    public String foto;
    public String correo;
    public String telefonoMovil;
    public String estatusAsistencia;
    public String nombre;
    public String apellido;
    public Date fechaDeIngresoSala;

    /**
     * Salas a las que el integrante ha confirmado asistencia
     */
    public ArrayList<Sala> salas;

    //Custom
    public Bitmap bitmap;
}
