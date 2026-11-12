package controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import excepciones.PagoInsuficienteException;
import modelo.*;
import repositorio.RepositorioGenerico;

public class FacturaController {

    private RepositorioGenerico<Factura> facturas;
    private RepositorioGenerico<OrdenTrabajo> ordenes;
    private HistorialController historial;
    private double porcentajeImpuesto = 15; // valor inicial de ejemplo, se puede cambiar desde la pantalla
    private int contador = 0;

    public FacturaController(RepositorioGenerico<Factura> facturas, RepositorioGenerico<OrdenTrabajo> ordenes,
            HistorialController historial) {
        this.facturas = facturas;
        this.ordenes = ordenes;
        this.historial = historial;
    }

    public double getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(String porcentaje) {
        double p = Validaciones.decimal(porcentaje, "Impuesto (%)");
        if (p < 0 || p > 100) {
            throw new IllegalArgumentException("El impuesto debe estar entre 0 y 100.");
        }
        porcentajeImpuesto = p;
    }

    private Factura buscarPorOrden(String numeroOrden) {
        for (Factura f : facturas.listar()) {
            if (f.getOrden().getNumero().equals(numeroOrden)) {
                return f;
            }
        }
        return null;
    }

    // Órdenes finalizadas que todavía no tienen factura.
    public ArrayList<OrdenTrabajo> ordenesFacturables() {
        ArrayList<OrdenTrabajo> resultado = new ArrayList<>();
        for (OrdenTrabajo o : ordenes.listar()) {
            if (o.getEstado() == EstadoOrden.FINALIZADA && buscarPorOrden(o.getNumero()) == null) {
                resultado.add(o);
            }
        }
        return resultado;
    }

    public void generar(String numeroOrden) {
        OrdenTrabajo o = ordenes.buscar(numeroOrden);
        if (o == null) {
            throw new IllegalArgumentException("Seleccione una orden finalizada.");
        }
        if (o.getEstado() != EstadoOrden.FINALIZADA) {
            throw new IllegalStateException("Solo se puede facturar una orden Finalizada.");
        }
        if (buscarPorOrden(o.getNumero()) != null) {
            throw new IllegalStateException("La orden " + o.getNumero() + " ya tiene factura.");
        }
        contador++;
        String numero = String.format("FAC-%03d", contador);
        Factura f = new Factura(numero, o, LocalDate.now(), porcentajeImpuesto);
        facturas.agregar(numero, f);
        historial.registrar(new Operacion("FACTURA_GENERADA", "Generación de la factura " + numero, f, null, 0));
    }

    // Simula el pago en efectivo. Si el monto no alcanza, lanza PagoInsuficienteException.
    public void registrarPago(String numeroFactura, String monto) throws PagoInsuficienteException {
        Factura f = facturas.buscar(numeroFactura);
        if (f == null) {
            throw new IllegalArgumentException("Seleccione una factura.");
        }
        if (f.isPagada()) {
            throw new IllegalStateException("La factura " + f.getNumero() + " ya fue pagada.");
        }
        double recibido = Validaciones.decimal(monto, "Monto recibido");
        if (recibido < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }
        if (recibido < f.getTotal()) {
            throw new PagoInsuficienteException(f.getTotal(), recibido);
        }
        f.registrarPago(recibido);
        historial.registrar(new Operacion("PAGO_REGISTRADO", "Pago de la factura " + f.getNumero(), f, null, 0));
    }

    public Factura buscar(String numeroFactura) {
        return facturas.buscar(numeroFactura);
    }

    public ArrayList<Factura> listar() {
        return facturas.listar();
    }
}
