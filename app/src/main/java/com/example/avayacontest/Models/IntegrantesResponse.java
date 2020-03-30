package com.example.avayacontest.Models;

import java.util.ArrayList;

public class IntegrantesResponse {
    public int code;
    public String message;
    public String status;

    //PENDIENTE:
    //Esta respuesta la manda si buscas los participantes por sala
    public ArrayList<Integrante> participantes;
    //esta si haces una busqueda general
    public ArrayList<Integrante> integrantes;
}
