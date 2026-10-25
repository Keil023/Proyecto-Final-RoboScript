package com.roboscript.analyzer;

import  com.roboscript.grammar.RoboScriptParser;
import com.roboscript.model.ErrorInfo;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

// guarda los errores del lexer y del parser en una lista
public class CustomErrorListener extends BaseErrorListener {

    private final List<ErrorInfo> errores = new ArrayList<>();

    public List<ErrorInfo> getErrores() {
        return errores;
    }

    // se usa para los tokens de error del lexer (@, 12ROB, OBJ-25)
    public void agregarErrorLexico(Token t, String mensaje) {
        errores.add(new ErrorInfo(ErrorInfo.LEXICO, t.getLine(), t.getCharPositionInLine() + 1, mensaje));
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {

        // error que viene del lexer
        if (recognizer instanceof Lexer) {
            errores.add(new ErrorInfo(ErrorInfo.LEXICO, line, charPositionInLine + 1, msg));
            return;
        }

        // error que viene del parser
        Parser parser = (Parser) recognizer;
        Token malo = (Token) offendingSymbol;
        String mensaje = construirMensaje(parser.getContext(), malo, msg);

        int linea = malo.getLine();
        int columna = malo.getCharPositionInLine() + 1;

        // si la instruccion quedo incompleta se reporta en la linea del ultimo token leido
        Token anterior = parser.getTokenStream().LT(-1);
        boolean esFin = mensaje.startsWith("Se esperaba FIN");
        if (!esFin && anterior != null && anterior.getLine() < linea) {
            linea = anterior.getLine();
            columna = anterior.getCharPositionInLine() + anterior.getText().length() + 1;
        }

        errores.add(new ErrorInfo(ErrorInfo.SINTACTICO, linea, columna, mensaje));
    }

    // el mensaje depende de la regla donde fallo el parser
    private String construirMensaje(ParserRuleContext ctx, Token malo, String mensajeAntlr) {
        int hijos = ctx.getChildCount();   // cuantas cosas ya se habian leido en la regla

        switch (ctx.getRuleIndex()) {
            case RoboScriptParser.RULE_declaracionRobot:
                return hijos == 0 ? "Se esperaba ROBOT al inicio del archivo."
                                  : "Se esperaba CODIGO_ROBOT después de ROBOT.";
            case RoboScriptParser.RULE_posicionInicial:
                return hijos == 0 ? "Se esperaba INICIO con la posición inicial después de la declaración del robot."
                                  : "Se esperaba COORDENADA (ej. X10,Y05) después de INICIO.";
            case RoboScriptParser.RULE_avanzar:
                return "Se esperaba NUMERO después de AVANZAR.";
            case RoboScriptParser.RULE_retroceder:
                return "Se esperaba NUMERO después de RETROCEDER.";
            case RoboScriptParser.RULE_girar:
                return "Se esperaba DERECHA o IZQUIERDA después de GIRAR.";
            case RoboScriptParser.RULE_seguir:
                return hijos <= 1 ? "Se esperaba LINEA después de SEGUIR."
                                  : "Se esperaba DURACION (ej. 30s o 2m) después de SEGUIR LINEA.";
            case RoboScriptParser.RULE_levantar:
                return "Se esperaba CODIGO_OBJETO (ej. OBJ-0025) después de LEVANTAR.";
            case RoboScriptParser.RULE_mover:
                return "Se esperaba COORDENADA o CODIGO_ZONA después de MOVER.";
            case RoboScriptParser.RULE_velocidad:
                return "Se esperaba PORCENTAJE (ej. 75%) después de VELOCIDAD.";
            case RoboScriptParser.RULE_condicion:
                if (hijos == 0) return "Se esperaba OBSTACULO o VELOCIDAD después de SI.";
                if (hijos == 1) return "Se esperaba operador relacional después de VELOCIDAD.";
                return "Se esperaba PORCENTAJE después del operador relacional.";
            case RoboScriptParser.RULE_repetir:
                if (hijos < 2) return "Se esperaba NUMERO después de REPETIR.";
                return mensajeFin("REPETIR", malo);
            case RoboScriptParser.RULE_condicional:
                return mensajeFin("SI", malo);
            case RoboScriptParser.RULE_programa:
                if (malo.getType() == Token.EOF) return mensajeAntlr;
                if (malo.getType() == RoboScriptParser.T_FIN)
                    return "FIN sin un bloque REPETIR o SI que cerrar.";
                return "Instrucción no válida o fuera de lugar: '" + malo.getText() + "'.";
            default:
                return mensajeAntlr;
        }
    }

    private String mensajeFin(String bloque, Token malo) {
        String base = "Se esperaba FIN para cerrar el bloque " + bloque;
        if (malo.getType() == Token.EOF) {
            return base + ".";
        }
        return base + ", pero se encontró '" + malo.getText() + "'.";
    }
}
