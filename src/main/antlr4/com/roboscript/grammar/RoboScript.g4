      grammar RoboScript;

// antlr4 no acepta {n}, por eso [0-9]{3} se escribe repitiendo DIGITO tres veces

// parser

programa        : declaracionRobot posicionInicial instruccion* EOF ;

declaracionRobot: T_ROBOT T_CODIGO_ROBOT ;
posicionInicial : T_INICIO T_COORDENADA ;

instruccion     : avanzar
                | retroceder
                | girar
                | seguir
                | levantar
                | mover
                | soltar
                | velocidad
                | repetir
                | condicional
                ;

avanzar         : T_AVANZAR T_NUMERO ;
retroceder      : T_RETROCEDER T_NUMERO ;
girar           : T_GIRAR (T_DERECHA | T_IZQUIERDA) ;
seguir          : T_SEGUIR T_LINEA T_DURACION ;
levantar        : T_LEVANTAR T_CODIGO_OBJETO ;
mover           : T_MOVER (T_COORDENADA | T_CODIGO_ZONA) ;
soltar          : T_SOLTAR ;
velocidad       : T_VELOCIDAD T_PORCENTAJE ;

// estructuras de control
repetir         : T_REPETIR T_NUMERO bloque T_FIN ;
condicional     : T_SI condicion bloque (T_SINO bloque)? T_FIN ;
condicion       : T_OBSTACULO
                | T_VELOCIDAD T_OP_REL T_PORCENTAJE
                ;

// un bloque puede estar vacio
bloque          : instruccion* ;

// lexer

// palabras reservadas, van antes del identificador
T_ROBOT      : 'ROBOT' ;
T_INICIO     : 'INICIO' ;
T_AVANZAR    : 'AVANZAR' ;
T_RETROCEDER : 'RETROCEDER' ;
T_GIRAR      : 'GIRAR' ;
T_DERECHA    : 'DERECHA' ;
T_IZQUIERDA  : 'IZQUIERDA' ;
T_SEGUIR     : 'SEGUIR' ;
T_LINEA      : 'LINEA' ;
T_LEVANTAR   : 'LEVANTAR' ;
T_MOVER      : 'MOVER' ;
T_SOLTAR     : 'SOLTAR' ;
T_REPETIR    : 'REPETIR' ;
T_SI         : 'SI' ;
T_SINO       : 'SINO' ;
T_FIN        : 'FIN' ;
T_OBSTACULO  : 'OBSTACULO' ;
T_VELOCIDAD  : 'VELOCIDAD' ;

// tokens con expresiones regulares
T_CODIGO_ROBOT  : DIGITO DIGITO DIGITO LETRA LETRA LETRA ;          // 123ROB
T_CODIGO_ZONA   : 'Z-' DIGITO DIGITO ;                              // Z-05
T_CODIGO_OBJETO : 'OBJ-' DIGITO DIGITO DIGITO DIGITO ;              // OBJ-0025
T_COORDENADA    : 'X' DIGITO DIGITO ',' [ \t]* 'Y' DIGITO DIGITO ;  // X10,Y25
T_PORCENTAJE    : ('100' | DIGITO DIGITO? ) '%' ;                   // 0% - 100%
T_DURACION      : DIGITO+ ('s' | 'm') ;                             // 30s, 5m
T_NUMERO        : DIGITO+ ;

// operadores relacionales
T_OP_REL        : '==' | '!=' | '<=' | '>=' | '<' | '>' ;

// otros
T_COMA          : ',' ;
T_IDENTIFICADOR : [A-Za-z_] [A-Za-z0-9_]* ;

// se ignoran
ESPACIOS    : [ \t\r\n]+ -> skip ;
COMENTARIO  : '//' ~[\r\n]* -> skip ;

// tokens de error, van al canal oculto y se reportan como errores lexicos
// si el formato es correcto gana T_CODIGO_* porque esta primero
T_ERROR_ROBOT   : DIGITO+ LETRA+ -> channel(HIDDEN) ;
T_ERROR_OBJETO  : 'OBJ-' DIGITO* -> channel(HIDDEN) ;

// cualquier otro caracter, tiene que ir al final
T_ERROR_SIMBOLO : . -> channel(HIDDEN) ;

fragment DIGITO : [0-9] ;
fragment LETRA  : [A-Z] ;
