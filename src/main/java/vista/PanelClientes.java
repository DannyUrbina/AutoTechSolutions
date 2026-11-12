package vista;

import controlador.ClienteController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;

public class PanelClientes extends PanelBase {

    private ClienteController controlador;
    private JTextField txtCodigo = new JTextField();
    private JTextField txtNombre = new JTextField();
    private JTextField txtIdentidad = new JTextField();
    private JTextField txtTelefono = new JTextField();
    private JTextField txtDireccion = new JTextField();
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelClientes(ClienteController controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));
        formulario.add(new JLabel("Código:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Nombre completo:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Identidad:"));
        formulario.add(txtIdentidad);
        formulario.add(new JLabel("Teléfono:"));
        formulario.add(txtTelefono);
        formulario.add(new JLabel("Dirección:"));
        formulario.add(txtDireccion);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(crearBoton("Registrar", e -> registrar()));
        botones.add(crearBoton("Consultar por código", e -> consultar()));
        botones.add(crearBoton("Modificar", e -> modificar()));
        botones.add(crearBoton("Eliminar", e -> eliminar()));
        botones.add(crearBoton("Limpiar", e -> limpiar()));

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modelo = crearModelo("Código", "Nombre completo", "Identidad", "Teléfono", "Dirección");
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionada();
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void registrar() {
        try {
            controlador.registrar(txtCodigo.getText(), txtNombre.getText(), txtIdentidad.getText(),
                    txtTelefono.getText(), txtDireccion.getText());
            limpiar();
            actualizar();
            mostrarMensaje("Cliente registrado correctamente.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void consultar() {
        try {
            Cliente c = controlador.buscar(txtCodigo.getText()); // lanza ClienteNoEncontradoException
            txtCodigo.setText(c.getCodigo());
            txtNombre.setText(c.getNombreCompleto());
            txtIdentidad.setText(c.getIdentidad());
            txtTelefono.setText(c.getTelefono());
            txtDireccion.setText(c.getDireccion());
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void modificar() {
        try {
            controlador.modificar(txtCodigo.getText(), txtNombre.getText(), txtIdentidad.getText(),
                    txtTelefono.getText(), txtDireccion.getText());
            actualizar();
            mostrarMensaje("Cliente modificado correctamente.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        try {
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar al cliente " + txtCodigo.getText() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                controlador.eliminar(txtCodigo.getText());
                limpiar();
                actualizar();
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void limpiar() {
        tabla.clearSelection();
        txtCodigo.setText("");
        txtNombre.setText("");
        txtIdentidad.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
    }

    // Al hacer clic en una fila, sus datos pasan al formulario.
    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            txtCodigo.setText((String) modelo.getValueAt(fila, 0));
            txtNombre.setText((String) modelo.getValueAt(fila, 1));
            txtIdentidad.setText((String) modelo.getValueAt(fila, 2));
            txtTelefono.setText((String) modelo.getValueAt(fila, 3));
            txtDireccion.setText((String) modelo.getValueAt(fila, 4));
        }
    }

    @Override
    public void actualizar() {
        modelo.setRowCount(0);
        for (Cliente c : controlador.listar()) {
            modelo.addRow(new Object[]{c.getCodigo(), c.getNombreCompleto(), c.getIdentidad(),
                c.getTelefono(), c.getDireccion()});
        }
    }
}
