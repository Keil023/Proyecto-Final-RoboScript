package com.roboscript.analyzer;

import com.roboscript.grammar.RoboScriptBaseListener;
import com.roboscript.grammar.RoboScriptParser;
import com.roboscript.model.ResumenEstadistico;

// cuenta las instrucciones al recorrer el arbol, solo se usa si el archivo es valido
public class StatisticsListener extends RoboScriptBaseListener {

    private final ResumenEstadistico resumen;

    public StatisticsListener(ResumenEstadistico resumen) {
        this.resumen = resumen;
    }

    @Override
    public void enterDeclaracionRobot(RoboScriptParser.DeclaracionRobotContext ctx) {
        resumen.robot = ctx.T_CODIGO_ROBOT().getText();
        resumen.instrucciones++;
    }

    @Override
    public void enterPosicionInicial(RoboScriptParser.PosicionInicialContext ctx) {
        resumen.posicionInicial = ctx.T_COORDENADA().getText();
        resumen.instrucciones++;
    }

    // cuenta tambien las que estan dentro de bloques
    @Override
    public void enterInstruccion(RoboScriptParser.InstruccionContext ctx) {
        resumen.instrucciones++;
    }

    @Override
    public void enterAvanzar(RoboScriptParser.AvanzarContext ctx) {
        resumen.movimientos++;
    }

    @Override
    public void enterRetroceder(RoboScriptParser.RetrocederContext ctx) {
        resumen.movimientos++;
    }

    @Override
    public void enterMover(RoboScriptParser.MoverContext ctx) {
        resumen.movimientos++;
    }

    @Override
    public void enterGirar(RoboScriptParser.GirarContext ctx) {
        resumen.giros++;
    }

    @Override
    public void enterLevantar(RoboScriptParser.LevantarContext ctx) {
        resumen.objetosLevantados++;
    }

    @Override
    public void enterSoltar(RoboScriptParser.SoltarContext ctx) {
        resumen.objetosSoltados++;
    }

    @Override
    public void enterCondicional(RoboScriptParser.CondicionalContext ctx) {
        resumen.bloquesSi++;
    }

    @Override
    public void enterRepetir(RoboScriptParser.RepetirContext ctx) {
        resumen.bloquesRepetir++;
    }
}
