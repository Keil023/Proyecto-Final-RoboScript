package com.roboscript.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.roboscript.analyzer.GeneradorReporte;
import com.roboscript.analyzer.RoboScriptAnalyzer;
import com.roboscript.model.ResultadoAnalisis;
import com.roboscript.model.TokenInfo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

// ventana principal
public class VentanaPrincipal extends JFrame {

    private static final Color COLOR_VALIDO = Color.decode("#D4EDDA");
    private static final Color COLOR_ERROR = Color.decode("#F8D7DA");
    private static final Color COLOR_NEUTRO = Color.decode("#E9ECEF");

    private final JTextField txtRuta = new JTextField("Ningún archivo cargado");
    private final JTextArea txtCodigo = new JTextArea();
    private final JTextArea txtResultado = new JTextArea();
    private final JLabel lblEstado = new JLabel("Sin analizar", SwingConstants.CENTER);

    // la tabla no se puede editar
    private final DefaultTableModel modeloTokens = new DefaultTableModel(
            new String[]{"Lexema", "Token", "Línea", "Columna"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final RoboScriptAnalyzer analizador = new RoboScriptAnalyzer();
    private String nombreArchivo = "sin_titulo.robot";

    public VentanaPrincipal() {
        super("RoboScript - Analizador Léxico y Sintáctico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelDividido(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    // armado de la interfaz

    private JPanel crearBarraSuperior() {
        JButton btnCargar = new JButton("Cargar Archivo");
        JButton btnEjecutar = new JButton("Ejecutar Análisis");
        btnCargar.addActionListener(e -> cargarArchivo());
        btnEjecutar.addActionListener(e -> ejecutarAnalisis());

        txtRuta.setEditable(false);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.add(btnCargar);
        botones.add(btnEjecutar);

        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        barra.add(botones, BorderLayout.WEST);
        barra.add(txtRuta, BorderLayout.CENTER);
        return barra;
    }

    private JSplitPane crearPanelDividido() {
        // editor de codigo
        txtCodigo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        txtCodigo.setTabSize(4);
        JScrollPane scrollCodigo = new JScrollPane(txtCodigo);

        // pestañas de la derecha
        JTable tablaTokens = new JTable(modeloTokens);
        tablaTokens.setRowHeight(24);

        txtResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        txtResultado.setEditable(false);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Tabla de Tokens", new JScrollPane(tablaTokens));
        pestanas.addTab("Resultado del Análisis", new JScrollPane(txtResultado));

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, pestanas);
        division.setResizeWeight(0.45);
        division.setDividerLocation(520);
        return division;
    }

    private JPanel crearBarraEstado() {
        lblEstado.setOpaque(true);
        lblEstado.setBackground(COLOR_NEUTRO);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD, 14f));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(lblEstado, BorderLayout.CENTER);
        return panel;
    }

    // acciones de los botones

    private void cargarArchivo() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Archivos RoboScript (*.robot, *.txt)", "robot", "txt"));

        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        try {
            txtCodigo.setText(Files.readString(archivo.toPath()));
            txtCodigo.setCaretPosition(0);
            txtRuta.setText(archivo.getAbsolutePath());
            nombreArchivo = archivo.getName();
            limpiarResultados();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el archivo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarAnalisis() {
        String texto = txtCodigo.getText();
        if (texto.isBlank()) {
            JOptionPane.showMessageDialog(this, "Primero carga un archivo o escribe código.",
                    "Sin código", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoAnalisis resultado = analizador.analizarTexto(texto, nombreArchivo);

        // tabla de tokens
        modeloTokens.setRowCount(0);
        for (TokenInfo t : resultado.tokens()) {
            modeloTokens.addRow(new Object[]{t.lexema(), t.token(), t.linea(), t.columna()});
        }

        // resumen en texto
        txtResultado.setText(GeneradorReporte.generar(resultado));
        txtResultado.setCaretPosition(0);

        // barra de estado
        if (resultado.esValido()) {
            lblEstado.setBackground(COLOR_VALIDO);
            lblEstado.setText("✓ Archivo válido - " + resultado.tokens().size() + " tokens, 0 errores");
        } else {
            lblEstado.setBackground(COLOR_ERROR);
            lblEstado.setText("✗ Archivo con errores - " + resultado.erroresLexicos().size()
                    + " léxicos, " + resultado.erroresSintacticos().size() + " sintácticos");
        }
    }

    private void limpiarResultados() {
        modeloTokens.setRowCount(0);
        txtResultado.setText("");
        lblEstado.setBackground(COLOR_NEUTRO);
        lblEstado.setText("Sin analizar");
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
