package vista;

import controlador.ServicioController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Servicio;

public class PanelServicios extends PanelBase {

    private ServicioController controlador;
    private JTextField txtCodigo = new JTextField();
    private JTextField txtDescripcion = new JTextField();
    private JTextField txtPrecio = new JTextField();
    private JTextField txtTiempo = new JTextField();
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelServicios(ServicioController controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
        formulario.add(new JLabel("Código:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Descripción:"));
        formulario.add(txtDescripcion);
        formulario.add(new JLabel("Precio base (L):"));
        formulario.add(txtPrecio);
        formulario.add(new JLabel("Tiempo estimado (minutos):"));
        formulario.add(txtTiempo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(crearBoton("Registrar servicio", e -> registrar()));
        botones.add(new JLabel("Los precios y tiempos de los 5 servicios iniciales son valores de ejemplo."));

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modelo = crearModelo("Código", "Descripción", "Precio base", "Tiempo estimado (min)");
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void registrar() {
        try {
            controlador.registrar(txtCodigo.getText(), txtDescripcion.getText(), txtPrecio.getText(), txtTiempo.getText());
            txtCodigo.setText("");
            txtDescripcion.setText("");
            txtPrecio.setText("");
            txtTiempo.setText("");
            actualizar();
            mostrarMensaje("Servicio registrado correctamente.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    @Override
    public void actualizar() {
        modelo.setRowCount(0);
        for (Servicio s : controlador.listar()) {
            modelo.addRow(new Object[]{s.getCodigo(), s.getDescripcion(), Formato.dinero(s.getPrecioBase()),
                s.getTiempoEstimado()});
        }
    }
}
