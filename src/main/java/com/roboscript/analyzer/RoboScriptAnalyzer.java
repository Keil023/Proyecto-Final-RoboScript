package com.roboscript.analyzer;

import com.roboscript.grammar.RoboScriptLexer;
import com.roboscript.grammar.RoboScriptParser;
import com.roboscript.model.ResultadoAnalisis;
import com.roboscript.model.ResumenEstadistico;
import com.roboscript.model.TokenInfo;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// hace todo el proceso: lexer, parser y estadisticas
public class RoboScriptAnalyzer {

    // lee el archivo directo del disco
    public ResultadoAnalisis analizarArchivo(Path ruta) throws IOException {
        return analizar(CharStreams.fromPath(ruta), ruta.getFileName().toString());
    }

    // analiza el texto del editor, que se pudo haber modificado
    public ResultadoAnalisis analizarTexto(String texto, String nombreArchivo) {
        return analizar(CharStreams.fromString(texto), nombreArchivo);
    }

    private ResultadoAnalisis analizar(CharStream entrada, String nombreArchivo) {
        CustomErrorListener errores = new CustomErrorListener();

        // analisis lexico
        RoboScriptLexer lexer = new RoboScriptLexer(entrada);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errores);

        CommonTokenStream flujo = new CommonTokenStream(lexer);
        flujo.fill();

        List<TokenInfo> tokens = new ArrayList<>();
        for (Token t : flujo.getTokens()) {
            switch (t.getType()) {
                case Token.EOF -> { }
                case RoboScriptLexer.T_ERROR_ROBOT ->
                        errores.agregarErrorLexico(t, t.getText() + " no cumple con la expresión regular de CODIGO_ROBOT");
                case RoboScriptLexer.T_ERROR_OBJETO ->
                        errores.agregarErrorLexico(t, t.getText() + " no cumple con la expresión regular de CODIGO_OBJETO");
                case RoboScriptLexer.T_ERROR_SIMBOLO ->
                        errores.agregarErrorLexico(t, "Símbolo no reconocido: " + t.getText());
                default -> tokens.add(new TokenInfo(
                        t.getText(),
                        lexer.getVocabulary().getSymbolicName(t.getType()),
                        t.getLine(),
                        t.getCharPositionInLine() + 1));
            }
        }

        // analisis sintactico
        RoboScriptParser parser = new RoboScriptParser(flujo);
        parser.removeErrorListeners();
        parser.addErrorListener(errores);
        parser.setErrorHandler(new EstrategiaErrores());
        ParseTree arbol = parser.programa();

        // estadisticas, solo si no hubo errores
        ResumenEstadistico resumen = new ResumenEstadistico();
        if (errores.getErrores().isEmpty()) {
            new ParseTreeWalker().walk(new StatisticsListener(resumen), arbol);
        }

        return new ResultadoAnalisis(nombreArchivo, tokens, errores.getErrores(), resumen);
    }
}
