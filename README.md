# RoboScript - Analizador léxico y sintáctico

Proyecto final del curso de Autómatas y Lenguajes Formales (Universidad Mariano Gálvez de Guatemala).

Programa en Java que analiza archivos escritos en RoboScript, un lenguaje sencillo para dar instrucciones a un robot. El proyecto solo analiza el lenguaje: no simula ni mueve ningún robot.

## Qué hace

- Análisis léxico: convierte el archivo en tokens (lexema, token, línea y columna).
- Análisis sintáctico: revisa que las instrucciones sigan la gramática del lenguaje.
- Reporta errores léxicos y sintácticos con su línea y columna.
- Muestra un resumen del análisis, con estadísticas si el archivo es válido.

## Tecnologías

- Java 17
- ANTLR 4.13.2 (genera el lexer y el parser)
- Maven
- Swing y FlatLaf (interfaz)

## Requisitos

- JDK 17 o superior
- Maven (viene incluido en IntelliJ IDEA)
- Conexión a internet la primera vez, para descargar las dependencias

## Cómo ejecutarlo

### Con IntelliJ IDEA

1. Abrir el archivo `pom.xml` como proyecto (Open as Project).
2. Asignar un JDK 17 o superior (Setup SDK).
3. En la pestaña Maven, ejecutar `Lifecycle > compile`. Esto genera las clases de ANTLR.
4. Ejecutar la clase `com.roboscript.ui.VentanaPrincipal`.

### Con la terminal

```
mvn compile exec:java
```

## Cómo usarlo

1. Presionar **Cargar Archivo** y elegir un archivo `.robot` o `.txt` (hay ejemplos en la carpeta `pruebas`).
2. Presionar **Ejecutar Análisis**.
3. Revisar la pestaña **Tabla de Tokens** y la pestaña **Resultado del Análisis**.
4. La barra de abajo se pone verde si el archivo es válido y roja si tiene errores.

El análisis se hace sobre el texto que aparece en el editor, así que también se puede escribir o modificar el código directamente.

## Estructura del proyecto

```
roboscript
├── pom.xml
├── pruebas/                         archivos de prueba (.robot)
└── src/main
    ├── antlr4/com/roboscript/grammar/RoboScript.g4    gramática (lexer y parser)
    └── java/com/roboscript
        ├── model/       TokenInfo, ErrorInfo, ResumenEstadistico, ResultadoAnalisis
        ├── analyzer/    RoboScriptAnalyzer, CustomErrorListener, EstrategiaErrores,
        │                StatisticsListener, GeneradorReporte
        └── ui/          VentanaPrincipal
```

Las clases del paquete `com.roboscript.grammar` (lexer y parser) las genera ANTLR al compilar, por eso no aparecen en `src`.

## Archivos de prueba

| Archivo | Qué prueba |
|---|---|
| `valido1_recorrido.robot` | Ejemplo válido del enunciado |
| `valido2_estructuras.robot` | SINO, VELOCIDAD, zonas, bloques anidados y comentarios |
| `invalido1_ejemplo_pdf.robot` | Ejemplo con errores del enunciado (léxicos y sintácticos) |
| `invalido2_errores_mixtos.robot` | Símbolos no reconocidos, AVANZAR incompleto y REPETIR sin FIN |
| `valido_grande_30000.robot` | Archivo válido de unas 30 000 líneas |
| `invalido_grande_30000.robot` | Archivo de unas 30 000 líneas con errores repartidos |

## Ejemplo de programa

```
ROBOT 123ROB
INICIO X10,Y05

AVANZAR 5
GIRAR DERECHA
LEVANTAR OBJ-0025
MOVER X20,Y15
SOLTAR

REPETIR 2
   AVANZAR 10
   GIRAR DERECHA
FIN
```

## Notas

- Las palabras reservadas se escriben en mayúsculas.
- Los comentarios empiezan con `//`.
- Las columnas se cuentan desde 1.
- Si se modifica `RoboScript.g4`, hay que volver a ejecutar `compile` para regenerar el lexer y el parser.
