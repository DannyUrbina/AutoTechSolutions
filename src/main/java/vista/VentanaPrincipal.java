package vista;

import controlador.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.*;
import modelo.*;
import repositorio.RepositorioGenerico;

// Ventana principal: pestañas para navegar a cada módulo.
// Aquí se crean los repositorios (los datos viven solo en memoria) y los controladores.
public class VentanaPrincipal extends JFrame {

    private JTabbedPane pestanas = new JTabbedPane();
    private ArrayList<PanelBase> paneles = new ArrayList<>();
    private HistorialController historial;

    public VentanaPrincipal() {
        super("AutoTech Solutions - Sistema de Gestión de Taller Automotriz");

        // Un repositorio genérico por cada tipo de dato
        RepositorioGenerico<Cliente> repoClientes = new RepositorioGenerico<>();
        RepositorioGenerico<Vehiculo> repoVehiculos = new RepositorioGenerico<>();
        RepositorioGenerico<Servicio> repoServicios = new RepositorioGenerico<>();
        RepositorioGenerico<Repuesto> repoRepuestos = new RepositorioGenerico<>();
        RepositorioGenerico<OrdenTrabajo> repoOrdenes = new RepositorioGenerico<>();
        RepositorioGenerico<Factura> repoFacturas = new RepositorioGenerico<>();

        // Controladores
        historial = new HistorialController(repoClientes, repoVehiculos, repoServicios, repoRepuestos,
                repoOrdenes, repoFacturas);
        ClienteController clientes = new ClienteController(repoClientes, repoVehiculos, historial);
        VehiculoController vehiculos = new VehiculoController(repoVehiculos, repoClientes, historial);
        ServicioController servicios = new ServicioController(repoServicios, historial);
        RepuestoController repuestos = new RepuestoController(repoRepuestos, historial);
        OrdenController ordenes = new OrdenController(repoOrdenes, repoClientes, repoVehiculos,
                repoServicios, repoRepuestos, historial);
        FacturaController facturas = new FacturaController(repoFacturas, repoOrdenes, historial);
        ReporteController reportes = new ReporteController(repoServicios, repoFacturas, repoOrdenes);

        // Pestañas
        agregarPestana("Clientes", new PanelClientes(clientes));
        agregarPestana("Vehículos", new PanelVehiculos(vehiculos, clientes));
        agregarPestana("Servicios", new PanelServicios(servicios));
        agregarPestana("Repuestos", new PanelRepuestos(repuestos));
        agregarPestana("Órdenes", new PanelOrdenes(ordenes, clientes, vehiculos, servicios, repuestos));
        agregarPestana("Facturación", new PanelFacturacion(facturas));
        agregarPestana("Reportes", new PanelReportes(reportes));

        // Al cambiar de pestaña se recargan sus datos, para que muestre lo último registrado.
        pestanas.addChangeListener(e -> {
            int indice = pestanas.getSelectedIndex();
            if (indice >= 0) {
                paneles.get(indice).actualizar();
            }
        });

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        superior.add(new JLabel("AutoTech Solutions"));
        JButton btnDeshacer = new JButton("Deshacer última operación");
        btnDeshacer.addActionListener(e -> deshacer());
        superior.add(btnDeshacer);

        setLayout(new BorderLayout());
        add(superior, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
    }

    private void agregarPestana(String nombre, PanelBase panel) {
        pestanas.addTab(nombre, panel);
        paneles.add(panel);
    }

    private void deshacer() {
        try {
            String descripcion = historial.deshacerUltima();
            paneles.get(pestanas.getSelectedIndex()).actualizar();
            JOptionPane.showMessageDialog(this, "Se deshizo: " + descripcion, "Deshacer",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se puede deshacer",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
