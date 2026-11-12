package modelo;

import java.time.LocalDate;

public class Factura {

    private String numero;
    private OrdenTrabajo orden;
    private Cliente cliente;
    private LocalDate fecha;
    private double subtotalServicios;
    private double subtotalRepuestos;
    private double porcentajeImpuesto;
    private double impuesto;
    private double total;
    private boolean pagada = false;
    private double montoRecibido = 0;
    private double cambio = 0;

    public Factura(String numero, OrdenTrabajo orden, LocalDate fecha, double porcentajeImpuesto) {
        this.numero = numero;
        this.orden = orden;
        this.cliente = orden.getCliente();
        this.fecha = fecha;
        this.porcentajeImpuesto = porcentajeImpuesto;
        this.subtotalServicios = redondear(orden.calcularSubtotalServicios());
        this.subtotalRepuestos = redondear(orden.calcularSubtotalRepuestos());
        this.impuesto = redondear((subtotalServicios + subtotalRepuestos) * porcentajeImpuesto / 100);
        this.total = redondear(subtotalServicios + subtotalRepuestos + impuesto);
    }

    // Redondea a 2 decimales para evitar errores como 1761.8000000000002
    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public void registrarPago(double monto) {
        montoRecibido = monto;
        cambio = redondear(monto - total);
        pagada = true;
    }

    public void deshacerPago() {
        montoRecibido = 0;
        cambio = 0;
        pagada = false;
    }

    public String getNumero() { return numero; }
    public OrdenTrabajo getOrden() { return orden; }
    public Cliente getCliente() { return cliente; }
    public LocalDate getFecha() { return fecha; }
    public double getSubtotalServicios() { return subtotalServicios; }
    public double getSubtotalRepuestos() { return subtotalRepuestos; }
    public double getPorcentajeImpuesto() { return porcentajeImpuesto; }
    public double getImpuesto() { return impuesto; }
    public double getTotal() { return total; }
    public boolean isPagada() { return pagada; }
    public double getMontoRecibido() { return montoRecibido; }
    public double getCambio() { return cambio; }
}
