package vista;

import controlador.FacturaController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.*;

public class PanelFacturacion extends PanelBase {

    private FacturaController controlador;
    private JTextField txtImpuesto = new JTextField(6);
    private JComboBox<OrdenTrabajo> cmbOrden = new JComboBox<>();
    private JTextField txtMonto = new JTextField(10);
    private JTextArea areaFactura = new JTextArea(10, 40);
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelFacturacion(FacturaController controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel filaImpuesto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaImpuesto.add(new JLabel("Impuesto (%):"));
        filaImpuesto.add(txtImpuesto);
        filaImpuesto.add(crearBoton("Aplicar impuesto", e -> aplicarImpuesto()));
        filaImpuesto.add(new JLabel("(valor inicial de ejemplo; se aplica a las facturas nuevas)"));

        JPanel filaGenerar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaGenerar.add(new JLabel("Orden finalizada:"));
        filaGenerar.add(cmbOrden);
        filaGenerar.add(crearBoton("Generar factura", e -> generar()));

        JPanel norte = new JPanel(new GridLayout(2, 1));
        norte.add(filaImpuesto);
        norte.add(filaGenerar);
        add(norte, BorderLayout.NORTH);

        modelo = crearModelo("Factura", "Orden", "Cliente", "Fecha", "Sub. servicios", "Sub. repuestos",
                "Impuesto", "Total", "Pago");
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarFactura();
            }
        });
        areaFactura.setEditable(false);
        areaFactura.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tabla),
                new JScrollPane(areaFactura));
        division.setResizeWeight(0.5);
        add(division, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(new JLabel("Pago en efectivo - monto recibido (L):"));
        sur.add(txtMonto);
        sur.add(crearBoton("Registrar pago de la factura seleccionada", e -> pagar()));
        add(sur, BorderLayout.SOUTH);

        actualizar();
    }

    private String facturaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Seleccione una factura de la tabla.");
        }
        return (String) modelo.getValueAt(fila, 0);
    }

    private void aplicarImpuesto() {
        try {
            controlador.setPorcentajeImpuesto(txtImpuesto.getText());
            mostrarMensaje("Impuesto configurado en " + controlador.getPorcentajeImpuesto() + " %.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void generar() {
        try {
            OrdenTrabajo o = (OrdenTrabajo) cmbOrden.getSelectedItem();
            if (o == null) {
                throw new IllegalArgumentException("No hay órdenes finalizadas pendientes de facturar.");
            }
            controlador.generar(o.getNumero());
            actualizar();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void pagar() {
        try {
            String numero = facturaSeleccionada();
            controlador.registrarPago(numero, txtMonto.getText()); // lanza PagoInsuficienteException
            Factura f = controlador.buscar(numero);
            txtMonto.setText("");
            actualizar();
            mostrarMensaje("Pago registrado.\nTotal: " + Formato.dinero(f.getTotal())
                    + "\nRecibido: " + Formato.dinero(f.getMontoRecibido())
                    + "\nCambio: " + Formato.dinero(f.getCambio()));
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    // Muestra el detalle de la factura seleccionada.
    private void mostrarFactura() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            areaFactura.setText("");
            return;
        }
        Factura f = controlador.buscar((String) modelo.getValueAt(fila, 0));
        if (f == null) {
            areaFactura.setText("");
            return;
        }
        OrdenTrabajo o = f.getOrden();
        String t = "FACTURA " + f.getNumero() + "     Fecha: " + f.getFecha() + "\n";
        t = t + "Orden: " + o.getNumero() + "     Vehículo: " + o.getVehiculo().getPlaca() + "\n";
        t = t + "Cliente: " + f.getCliente() + "\n\nServicios:\n";
        for (Servicio s : o.getServicios()) {
            t = t + String.format("   %-28s %s\n", s.getDescripcion(), Formato.dinero(s.getPrecioBase()));
        }
        t = t + "Repuestos:\n";
        for (DetalleRepuesto d : o.getRepuestos()) {
            t = t + String.format("   %-28s %s\n", d.getRepuesto().getNombre() + " x" + d.getCantidad(),
                    Formato.dinero(d.getSubtotal()));
        }
        t = t + "\nSubtotal servicios: " + Formato.dinero(f.getSubtotalServicios());
        t = t + "\nSubtotal repuestos: " + Formato.dinero(f.getSubtotalRepuestos());
        t = t + "\nImpuesto (" + f.getPorcentajeImpuesto() + " %): " + Formato.dinero(f.getImpuesto());
        t = t + "\nTOTAL: " + Formato.dinero(f.getTotal()) + "\n";
        if (f.isPagada()) {
            t = t + "\nPAGADA. Recibido: " + Formato.dinero(f.getMontoRecibido())
                    + "  Cambio: " + Formato.dinero(f.getCambio());
        } else {
            t = t + "\nPago pendiente.";
        }
        areaFactura.setText(t);
        areaFactura.setCaretPosition(0);
    }

    @Override
    public void actualizar() {
        txtImpuesto.setText(String.valueOf(controlador.getPorcentajeImpuesto()));

        cmbOrden.removeAllItems();
        for (OrdenTrabajo o : controlador.ordenesFacturables()) {
            cmbOrden.addItem(o);
        }

        String seleccionada = null;
        if (tabla.getSelectedRow() >= 0) {
            seleccionada = (String) modelo.getValueAt(tabla.getSelectedRow(), 0);
        }
        modelo.setRowCount(0);
        int fila = 0;
        for (Factura f : controlador.listar()) {
            modelo.addRow(new Object[]{f.getNumero(), f.getOrden().getNumero(), f.getCliente().getNombreCompleto(),
                f.getFecha(), Formato.dinero(f.getSubtotalServicios()), Formato.dinero(f.getSubtotalRepuestos()),
                Formato.dinero(f.getImpuesto()), Formato.dinero(f.getTotal()), f.isPagada() ? "Pagada" : "Pendiente"});
            if (f.getNumero().equals(seleccionada)) {
                tabla.setRowSelectionInterval(fila, fila);
            }
            fila++;
        }
        mostrarFactura();
    }
}
