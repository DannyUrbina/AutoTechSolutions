package vista;

import controlador.RepuestoController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Repuesto;

public class PanelRepuestos extends PanelBase {

    private RepuestoController controlador;
    private JTextField txtCodigo = new JTextField();
    private JTextField txtNombre = new JTextField();
    private JTextField txtCantidad = new JTextField();
    private JTextField txtPrecio = new JTextField();
    private JTextField txtMinimo = new JTextField();
    private JTextField txtEntrada = new JTextField(8);
    private JLabel lblAlertas = new JLabel(" ");
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelRepuestos(RepuestoController controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));
        formulario.add(new JLabel("Código:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Cantidad disponible:"));
        formulario.add(txtCantidad);
        formulario.add(new JLabel("Precio unitario (L):"));
        formulario.add(txtPrecio);
        formulario.add(new JLabel("Stock mínimo:"));
        formulario.add(txtMinimo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(crearBoton("Registrar repuesto", e -> registrar()));
        botones.add(new JLabel("   Entrada de inventario (use el código de arriba) - cantidad:"));
        botones.add(txtEntrada);
        botones.add(crearBoton("Registrar entrada", e -> registrarEntrada()));

        lblAlertas.setForeground(Color.RED);
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(botones, BorderLayout.CENTER);
        sur.add(lblAlertas, BorderLayout.SOUTH);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(sur, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modelo = crearModelo("Código", "Nombre", "Cantidad", "Precio unitario", "Stock mínimo", "Alerta");
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtCodigo.setText((String) modelo.getValueAt(fila, 0));
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void registrar() {
        try {
            controlador.registrar(txtCodigo.getText(), txtNombre.getText(), txtCantidad.getText(),
                    txtPrecio.getText(), txtMinimo.getText());
            txtCodigo.setText("");
            txtNombre.setText("");
            txtCantidad.setText("");
            txtPrecio.setText("");
            txtMinimo.setText("");
            actualizar();
            mostrarMensaje("Repuesto registrado correctamente.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void registrarEntrada() {
        try {
            controlador.registrarEntrada(txtCodigo.getText(), txtEntrada.getText());
            txtEntrada.setText("");
            actualizar();
            mostrarMensaje("Entrada de inventario registrada.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    @Override
    public void actualizar() {
        modelo.setRowCount(0);
        for (Repuesto r : controlador.listar()) {
            String alerta = r.estaBajoMinimo() ? "STOCK BAJO" : "OK";
            modelo.addRow(new Object[]{r.getCodigo(), r.getNombre(), r.getCantidad(),
                Formato.dinero(r.getPrecioUnitario()), r.getStockMinimo(), alerta});
        }
        // Mensaje de alerta con los repuestos que están en el mínimo o por debajo.
        ArrayList<Repuesto> alertas = controlador.alertasStockMinimo();
        if (alertas.isEmpty()) {
            lblAlertas.setText(" ");
        } else {
            String texto = "ALERTA de stock mínimo: ";
            for (Repuesto r : alertas) {
                texto = texto + r.getCodigo() + " (" + r.getCantidad() + ")  ";
            }
            lblAlertas.setText(texto);
        }
    }
}
