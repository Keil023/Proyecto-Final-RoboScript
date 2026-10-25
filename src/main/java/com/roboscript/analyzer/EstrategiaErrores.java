package com.roboscript.analyzer;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

// igual que la de antlr pero reporta todos los FIN que faltan al llegar al final del archivo
public class EstrategiaErrores extends DefaultErrorStrategy {

    @Override
    public void reportError(Parser recognizer, RecognitionException e) {
        permitirSiEsFinDeArchivo(recognizer);
        super.reportError(recognizer, e);
    }

    @Override
    protected void reportMissingToken(Parser recognizer) {
        permitirSiEsFinDeArchivo(recognizer);
        super.reportMissingToken(recognizer);
    }

    // antlr no reporta otro error mientras se recupera del anterior, aqui se quita ese bloqueo
    private void permitirSiEsFinDeArchivo(Parser recognizer) {
        if (recognizer.getCurrentToken().getType() == Token.EOF) {
            endErrorCondition(recognizer);
        }
    }
}
