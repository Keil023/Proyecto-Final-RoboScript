package com.roboscript.model;

// token con su posicion en el archivo (la columna empieza en 1)
public record TokenInfo(String lexema, String token, int linea, int columna) {
}
