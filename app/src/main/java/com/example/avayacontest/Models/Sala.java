package com.example.avayacontest.Models;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class Sala {
    public UUID idSala;
    public String nombreSala = "Seleccione Sala";
    public String avayaSpacesId;
    public String estatusSala = "TERMINATED"; //Default status
    public Date fechaSala;
    public String horaSala;

    public String asistencia;
}
