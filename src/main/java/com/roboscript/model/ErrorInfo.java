package com.roboscript.model;

public record ErrorInfo(String tipo, int linea, int columna, String mensaje) {

    public static final String LEXICO = "LEXICO";
    public static final String SINTACTICO = "SINTACTICO";

    public boolean esLexico() {
        return LEXICO.equals(tipo);
    }

    // ejemplo: ERROR LÉXICO: Línea: 2, Columna: 11, mensaje
    @Override
    public String toString() {
        String nombre = esLexico() ? "LÉXICO" : "SINTÁCTICO";
        return "ERROR " + nombre + ": Línea: " + linea + ", Columna: " + columna + ", " + mensaje;
    }

    // ejemplo: Línea 5, columna 12: mensaje
    public String enLista() {
        return "Línea " + linea + ", columna " + columna + ": " + mensaje;
    }
}
