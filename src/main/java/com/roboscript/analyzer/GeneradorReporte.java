package com.roboscript.analyzer;

import com.roboscript.model.ErrorInfo;
import com.roboscript.model.ResultadoAnalisis;
import com.roboscript.model.ResumenEstadistico;

// arma el texto del resumen con el formato del enunciado
public class GeneradorReporte {

    private static final String DOBLE = "=".repeat(41);
    private static final String SIMPLE = "-".repeat(41);

    public static String generar(ResultadoAnalisis r) {
        StringBuilder sb = new StringBuilder();
        linea(sb, DOBLE);
        linea(sb, " ANALISIS DE ROBOSCRIPT");
        linea(sb, DOBLE);
        linea(sb, "Archivo: " + r.archivo());
        linea(sb, "RESULTADO: " + (r.esValido() ? "ARCHIVO VALIDO" : "ARCHIVO NO VALIDO"));
        linea(sb, SIMPLE);

        if (r.esValido()) {
            reporteValido(sb, r);
        } else {
            reporteConErrores(sb, r);
        }
        return sb.toString();
    }

    private static void reporteValido(StringBuilder sb, ResultadoAnalisis r) {
        ResumenEstadistico s = r.resumen();

        linea(sb, "ANALISIS LEXICO");
        linea(sb, SIMPLE);
        linea(sb, "Tokens reconocidos: " + r.tokens().size());
        linea(sb, "Errores léxicos: 0");
        linea(sb, SIMPLE);
        linea(sb, "ANALISIS SINTACTICO");
        linea(sb, SIMPLE);
        linea(sb, "Instrucciones analizadas: " + s.instrucciones);
        linea(sb, "Errores sintácticos: 0");
        linea(sb, SIMPLE);
        linea(sb, "RESUMEN DEL PROGRAMA");
        linea(sb, SIMPLE);
        linea(sb, "Robot: " + s.robot);
        linea(sb, "Posición inicial: " + s.posicionInicial);
        linea(sb, "Movimientos: " + s.movimientos);
        linea(sb, "Giros: " + s.giros);
        linea(sb, "Objetos levantados: " + s.objetosLevantados);
        linea(sb, "Objetos soltados: " + s.objetosSoltados);
        linea(sb, "Bloques SI: " + s.bloquesSi);
        linea(sb, "Bloques REPETIR: " + s.bloquesRepetir);
        linea(sb, DOBLE);
        linea(sb, " ✓ ARCHIVO SINTACTICAMENTE VALIDO");
        linea(sb, DOBLE);
    }

    private static void reporteConErrores(StringBuilder sb, ResultadoAnalisis r) {
        linea(sb, "ERRORES LEXICOS");
        linea(sb, SIMPLE);
        if (r.erroresLexicos().isEmpty()) {
            linea(sb, "(ninguno)");
        }
        for (ErrorInfo e : r.erroresLexicos()) {
            linea(sb, e.enLista());
        }
        linea(sb, SIMPLE);
        linea(sb, "ERRORES SINTACTICOS");
        linea(sb, SIMPLE);
        if (r.erroresSintacticos().isEmpty()) {
            linea(sb, "(ninguno)");
        }
        for (ErrorInfo e : r.erroresSintacticos()) {
            linea(sb, e.enLista());
        }
        linea(sb, SIMPLE);
        linea(sb, "RESUMEN");
        linea(sb, SIMPLE);
        linea(sb, "Tokens reconocidos: " + r.tokens().size());
        linea(sb, "Errores léxicos: " + r.erroresLexicos().size());
        linea(sb, "Errores sintácticos: " + r.erroresSintacticos().size());
        linea(sb, DOBLE);
    }

    private static void linea(StringBuilder sb, String texto) {
        sb.append(texto).append('\n');
    }
}
