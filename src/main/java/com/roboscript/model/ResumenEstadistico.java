package com.roboscript.model;

// contadores del programa, campos publicos para no llenar de getters
public class ResumenEstadistico {
    public String robot = "";
    public String posicionInicial = "";
    public int instrucciones = 0;
    public int movimientos = 0;   // avanzar + retroceder + mover
    public int giros = 0;
    public int objetosLevantados = 0;
    public int objetosSoltados = 0;
    public int bloquesSi = 0;
    public int bloquesRepetir = 0;
}
