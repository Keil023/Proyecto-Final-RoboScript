package com.roboscript.model;

import java.util.List;

// lo que devuelve el analisis: tokens, errores y estadisticas
public record ResultadoAnalisis(String archivo,
                                List<TokenInfo> tokens,
                                List<ErrorInfo> errores,
                                ResumenEstadistico resumen) {

    public boolean esValido() {
        return errores.isEmpty();
    }

    public List<ErrorInfo> erroresLexicos() {
        return errores.stream().filter(ErrorInfo::esLexico).toList();
    }

    public List<ErrorInfo> erroresSintacticos() {
        return errores.stream().filter(e -> !e.esLexico()).toList();
    }
}
