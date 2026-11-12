package vista;

import controlador.ClienteController;
import controlador.VehiculoController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import modelo.Vehiculo;

public class PanelVehiculos extends PanelBase {

    private VehiculoController controlador;
    private ClienteController clientes;
    private JTextField txtPlaca = new JTextField();
    private JTextField txtMarca = new JTextField();
    private JTextField txtModelo = new JTextField();
    private JTextField txtAnio = new JTextField();
    private JTextField txtColor = new JTextField();
    private JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private DefaultTableModel modelo;
    private JTable tabla;
    private String filtroCliente = null; // si tiene valor, la tabla muestra solo los vehículos de ese cliente

    public PanelVehiculos(VehiculoController controlador, ClienteController clientes) {
        this.controlador = controlador;
        this.clientes = clientes;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(6, 2, 5, 5));
        formulario.add(new JLabel("Placa:"));
        formulario.add(txtPlaca);
        formulario.add(new JLabel("Marca:"));
        formulario.add(txtMarca);
        formulario.add(new JLabel("Modelo:"));
        formulario.add(txtModelo);
        formulario.add(new JLabel("Año:"));
        formulario.add(txtAnio);
        formulario.add(new JLabel("Color:"));
        formulario.add(txtColor);
        formulario.add(new JLabel("Cliente propietario:"));
        formulario.add(cmbCliente);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(crearBoton("Registrar vehículo", e -> registrar()));
        botones.add(crearBoton("Buscar por placa", e -> buscarPorPlaca()));
        botones.add(crearBoton("Listar vehículos del cliente seleccionado", e -> listarPorCliente()));
        botones.add(crearBoton("Listar todos", e -> {
            filtroCliente = null;
            actualizar();
        }));

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        modelo = crearModelo("Placa", "Marca", "Modelo", "Año", "Color", "Propietario");
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void registrar() {
        try {
            Cliente propietario = (Cliente) cmbCliente.getSelectedItem();
            String codigoCliente = propietario == null ? "" : propietario.getCodigo();
            controlador.registrar(txtPlaca.getText(), txtMarca.getText(), txtModelo.getText(),
                    txtAnio.getText(), txtColor.getText(), codigoCliente);
            txtPlaca.setText("");
            txtMarca.setText("");
            txtModelo.setText("");
            txtAnio.setText("");
            txtColor.setText("");
            actualizar();
            mostrarMensaje("Vehículo registrado correctamente.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void buscarPorPlaca() {
        Vehiculo v = controlador.buscarPorPlaca(txtPlaca.getText());
        if (v == null) {
            mostrarMensaje("No existe ningún vehículo con esa placa.");
        } else {
            modelo.setRowCount(0);
            agregarFila(v);
            filtroCliente = null;
        }
    }

    private void listarPorCliente() {
        try {
            Cliente c = (Cliente) cmbCliente.getSelectedItem();
            if (c == null) {
                throw new IllegalArgumentException("Seleccione un cliente.");
            }
            filtroCliente = c.getCodigo();
            actualizar();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void agregarFila(Vehiculo v) {
        modelo.addRow(new Object[]{v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnio(),
            v.getColor(), v.getPropietario().toString()});
    }

    @Override
    public void actualizar() {
        // Se recarga el combo de clientes (pueden haber cambiado) conservando la selección.
        Cliente seleccionado = (Cliente) cmbCliente.getSelectedItem();
        cmbCliente.removeAllItems();
        for (Cliente c : clientes.listar()) {
            cmbCliente.addItem(c);
        }
        if (seleccionado != null) {
            cmbCliente.setSelectedItem(seleccionado);
        }

        ArrayList<Vehiculo> lista;
        try {
            if (filtroCliente == null) {
                lista = controlador.listar();
            } else {
                lista = controlador.listarPorCliente(filtroCliente);
            }
        } catch (Exception ex) {
            filtroCliente = null;
            lista = controlador.listar();
        }
        modelo.setRowCount(0);
        for (Vehiculo v : lista) {
            agregarFila(v);
        }
    }
}
