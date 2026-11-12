package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class OrdenTrabajo {

    private String numero;
    private LocalDate fecha;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private String diagnostico;
    private String mecanico;
    private ArrayList<Servicio> servicios = new ArrayList<>();
    private ArrayList<DetalleRepuesto> repuestos = new ArrayList<>();
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    private boolean inventarioDescontado = false; // evita descontar dos veces

    public OrdenTrabajo(String numero, LocalDate fecha, Cliente cliente, Vehiculo vehiculo,
                        String diagnostico, String mecanico) {
        this.numero = numero;
        this.fecha = fecha;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.diagnostico = diagnostico;
        this.mecanico = mecanico;
    }

    // El costo siempre se calcula con lo que hay en las listas.
    public double calcularSubtotalServicios() {
        double suma = 0;
        for (Servicio s : servicios) {
            suma = suma + s.getPrecioBase();
        }
        return suma;
    }

    public double calcularSubtotalRepuestos() {
        double suma = 0;
        for (DetalleRepuesto d : repuestos) {
            suma = suma + d.getSubtotal();
        }
        return suma;
    }

    public double calcularTotal() {
        return calcularSubtotalServicios() + calcularSubtotalRepuestos();
    }

    public String getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public String getDiagnostico() { return diagnostico; }
    public String getMecanico() { return mecanico; }
    public ArrayList<Servicio> getServicios() { return servicios; }
    public ArrayList<DetalleRepuesto> getRepuestos() { return repuestos; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public boolean isInventarioDescontado() { return inventarioDescontado; }
    public void setInventarioDescontado(boolean valor) { this.inventarioDescontado = valor; }

    @Override
    public String toString() {
        return numero + " - " + cliente.getNombreCompleto();
    }
}
