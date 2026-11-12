package vista;

import controlador.*;
import hilos.ProcesoServicioHilo;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.*;

public class PanelOrdenes extends PanelBase {

    private OrdenController ordenes;
    private ClienteController clientes;
    private VehiculoController vehiculos;
    private ServicioController servicios;
    private RepuestoController repuestos;

    // Nueva orden
    private JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private JComboBox<Vehiculo> cmbVehiculo = new JComboBox<>();
    private JTextField txtDiagnostico = new JTextField();
    private JTextField txtMecanico = new JTextField();

    // Tabla de órdenes
    private DefaultTableModel modeloOrdenes;
    private JTable tablaOrdenes;

    // Detalle de la orden seleccionada
    private JComboBox<Servicio> cmbServicio = new JComboBox<>();
    private JComboBox<Repuesto> cmbRepuesto = new JComboBox<>();
    private JTextField txtCantidad = new JTextField("1", 5);
    private DefaultTableModel modeloDetalle;
    private JTable tablaDetalle;
    private JLabel lblOrden = new JLabel("Seleccione una orden de la tabla");
    private JLabel lblTotales = new JLabel(" ");
    private JLabel lblProceso = new JLabel("Sin procesamiento en curso");
    private JProgressBar barra = new JProgressBar(0, 100);
    private JButton btnAgregarServicio;
    private JButton btnAgregarRepuesto;
    private JButton btnProcesar;
    private JButton btnCancelar;

    private String ordenSeleccionada = null;
    private boolean cargando = false;

    public PanelOrdenes(OrdenController ordenes, ClienteController clientes, VehiculoController vehiculos,
            ServicioController servicios, RepuestoController repuestos) {
        this.ordenes = ordenes;
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.servicios = servicios;
        this.repuestos = repuestos;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ----- Parte de arriba: crear orden -----
        JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
        formulario.add(new JLabel("Cliente:"));
        formulario.add(cmbCliente);
        formulario.add(new JLabel("Vehículo del cliente:"));
        formulario.add(cmbVehiculo);
        formulario.add(new JLabel("Diagnóstico:"));
        formulario.add(txtDiagnostico);
        formulario.add(new JLabel("Mecánico:"));
        formulario.add(txtMecanico);
        JPanel botonCrear = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botonCrear.add(crearBoton("Crear orden", e -> crear()));
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botonCrear, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        cmbCliente.addActionListener(e -> {
            if (!cargando) {
                cargarVehiculos();
            }
        });

        // ----- Centro: tabla de órdenes y detalle -----
        modeloOrdenes = crearModelo("Número", "Fecha", "Cliente", "Vehículo", "Mecánico", "Estado", "Costo total");
        tablaOrdenes = new JTable(modeloOrdenes);
        tablaOrdenes.setRowHeight(24);
        tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !cargando) {
                int fila = tablaOrdenes.getSelectedRow();
                if (fila >= 0) {
                    ordenSeleccionada = (String) modeloOrdenes.getValueAt(fila, 0);
                } else {
                    ordenSeleccionada = null;
                }
                cargarDetalle();
            }
        });

        JPanel detalle = new JPanel(new BorderLayout(5, 5));
        detalle.setBorder(BorderFactory.createTitledBorder("Detalle de la orden seleccionada"));

        btnAgregarServicio = crearBoton("Agregar servicio", e -> agregarServicio());
        btnAgregarRepuesto = crearBoton("Agregar repuesto", e -> agregarRepuesto());
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controles.add(new JLabel("Servicio:"));
        controles.add(cmbServicio);
        controles.add(btnAgregarServicio);
        controles.add(new JLabel("   Repuesto:"));
        controles.add(cmbRepuesto);
        controles.add(new JLabel("Cantidad:"));
        controles.add(txtCantidad);
        controles.add(btnAgregarRepuesto);
        JPanel arriba = new JPanel(new GridLayout(2, 1));
        arriba.add(lblOrden);
        arriba.add(controles);
        detalle.add(arriba, BorderLayout.NORTH);

        modeloDetalle = crearModelo("Tipo", "Código", "Descripción", "Cantidad", "Precio", "Subtotal");
        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setRowHeight(24);
        detalle.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        btnProcesar = crearBoton("Procesar orden", e -> procesar());
        btnCancelar = crearBoton("Cancelar orden", e -> cancelar());
        barra.setStringPainted(true);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnProcesar);
        acciones.add(btnCancelar);
        acciones.add(barra);
        acciones.add(lblProceso);
        JPanel sur = new JPanel(new GridLayout(2, 1));
        sur.add(lblTotales);
        sur.add(acciones);
        detalle.add(sur, BorderLayout.SOUTH);

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tablaOrdenes), detalle);
        division.setResizeWeight(0.35);
        add(division, BorderLayout.CENTER);

        actualizar();
    }

    private void crear() {
        try {
            Cliente c = (Cliente) cmbCliente.getSelectedItem();
            Vehiculo v = (Vehiculo) cmbVehiculo.getSelectedItem();
            if (c == null || v == null) {
                throw new IllegalArgumentException("Seleccione un cliente y uno de sus vehículos.");
            }
            ordenes.crear(c.getCodigo(), v.getPlaca(), txtDiagnostico.getText(), txtMecanico.getText());
            txtDiagnostico.setText("");
            txtMecanico.setText("");
            actualizar();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private String exigirOrden() {
        if (ordenSeleccionada == null) {
            throw new IllegalArgumentException("Seleccione una orden de la tabla.");
        }
        return ordenSeleccionada;
    }

    private void agregarServicio() {
        try {
            Servicio s = (Servicio) cmbServicio.getSelectedItem();
            ordenes.agregarServicio(exigirOrden(), s == null ? "" : s.getCodigo());
            actualizar();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void agregarRepuesto() {
        try {
            Repuesto r = (Repuesto) cmbRepuesto.getSelectedItem();
            ordenes.agregarRepuesto(exigirOrden(), r == null ? "" : r.getCodigo(), txtCantidad.getText());
            actualizar();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void cancelar() {
        try {
            String numero = exigirOrden();
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Cancelar la orden " + numero + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                ordenes.cancelar(numero);
                actualizar();
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    // Procesa la orden con un hilo. La interfaz sigue funcionando mientras tanto.
    private void procesar() {
        try {
            String numero = exigirOrden();
            ordenes.iniciarProcesamiento(numero); // pasa a "En proceso"; rechaza si ya se procesa
            barra.setValue(0);
            actualizar(); // deshabilita los botones de la orden
            ProcesoServicioHilo proceso = new ProcesoServicioHilo(ordenes, numero, barra, lblProceso,
                    this, () -> actualizar());
            new Thread(proceso).start();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    // Llena el combo de vehículos con los del cliente elegido.
    private void cargarVehiculos() {
        cmbVehiculo.removeAllItems();
        Cliente c = (Cliente) cmbCliente.getSelectedItem();
        if (c != null) {
            try {
                for (Vehiculo v : vehiculos.listarPorCliente(c.getCodigo())) {
                    cmbVehiculo.addItem(v);
                }
            } catch (Exception ex) {
                // el cliente ya no existe: el combo queda vacío
            }
        }
    }

    private void cargarDetalle() {
        modeloDetalle.setRowCount(0);
        boolean pendiente = false;
        if (ordenSeleccionada == null) {
            lblOrden.setText("Seleccione una orden de la tabla");
            lblTotales.setText(" ");
        } else {
            OrdenTrabajo o = ordenes.buscar(ordenSeleccionada);
            lblOrden.setText("Orden " + o.getNumero() + " | Cliente: " + o.getCliente().getNombreCompleto()
                    + " | Vehículo: " + o.getVehiculo().getPlaca() + " | Diagnóstico: " + o.getDiagnostico()
                    + " | Estado: " + o.getEstado());
            for (Servicio s : o.getServicios()) {
                modeloDetalle.addRow(new Object[]{"Servicio", s.getCodigo(), s.getDescripcion(), 1,
                    Formato.dinero(s.getPrecioBase()), Formato.dinero(s.getPrecioBase())});
            }
            for (DetalleRepuesto d : o.getRepuestos()) {
                modeloDetalle.addRow(new Object[]{"Repuesto", d.getRepuesto().getCodigo(),
                    d.getRepuesto().getNombre(), d.getCantidad(),
                    Formato.dinero(d.getRepuesto().getPrecioUnitario()), Formato.dinero(d.getSubtotal())});
            }
            lblTotales.setText("Subtotal servicios: " + Formato.dinero(o.calcularSubtotalServicios())
                    + "   |   Subtotal repuestos: " + Formato.dinero(o.calcularSubtotalRepuestos())
                    + "   |   COSTO TOTAL: " + Formato.dinero(o.calcularTotal()));
            pendiente = (o.getEstado() == EstadoOrden.PENDIENTE);
        }
        // Solo una orden Pendiente se puede modificar, cancelar o procesar.
        btnAgregarServicio.setEnabled(pendiente);
        btnAgregarRepuesto.setEnabled(pendiente);
        btnProcesar.setEnabled(pendiente);
        btnCancelar.setEnabled(pendiente);
    }

    @Override
    public void actualizar() {
        cargando = true;

        Cliente clienteSel = (Cliente) cmbCliente.getSelectedItem();
        cmbCliente.removeAllItems();
        for (Cliente c : clientes.listar()) {
            cmbCliente.addItem(c);
        }
        if (clienteSel != null) {
            cmbCliente.setSelectedItem(clienteSel);
        }
        Vehiculo vehiculoSel = (Vehiculo) cmbVehiculo.getSelectedItem();
        cargarVehiculos();
        if (vehiculoSel != null) {
            cmbVehiculo.setSelectedItem(vehiculoSel);
        }

        Servicio servicioSel = (Servicio) cmbServicio.getSelectedItem();
        cmbServicio.removeAllItems();
        for (Servicio s : servicios.listar()) {
            cmbServicio.addItem(s);
        }
        if (servicioSel != null) {
            cmbServicio.setSelectedItem(servicioSel);
        }

        Repuesto repuestoSel = (Repuesto) cmbRepuesto.getSelectedItem();
        cmbRepuesto.removeAllItems();
        for (Repuesto r : repuestos.listar()) {
            cmbRepuesto.addItem(r);
        }
        if (repuestoSel != null) {
            cmbRepuesto.setSelectedItem(repuestoSel);
        }

        modeloOrdenes.setRowCount(0);
        int filaSeleccionada = -1;
        int fila = 0;
        for (OrdenTrabajo o : ordenes.listar()) {
            modeloOrdenes.addRow(new Object[]{o.getNumero(), o.getFecha(), o.getCliente().getNombreCompleto(),
                o.getVehiculo().getPlaca(), o.getMecanico(), o.getEstado().toString(),
                Formato.dinero(o.calcularTotal())});
            if (o.getNumero().equals(ordenSeleccionada)) {
                filaSeleccionada = fila;
            }
            fila++;
        }
        if (filaSeleccionada >= 0) {
            tablaOrdenes.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
        } else {
            ordenSeleccionada = null;
        }

        cargando = false;
        cargarDetalle();
    }
}
