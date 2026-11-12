package controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import excepciones.ClienteNoEncontradoException;
import excepciones.StockInsuficienteException;
import modelo.*;
import repositorio.RepositorioGenerico;

public class OrdenController {

    private RepositorioGenerico<OrdenTrabajo> ordenes;
    private RepositorioGenerico<Cliente> clientes;
    private RepositorioGenerico<Vehiculo> vehiculos;
    private RepositorioGenerico<Servicio> servicios;
    private RepositorioGenerico<Repuesto> repuestos;
    private HistorialController historial;
    private int contador = 0;

    public OrdenController(RepositorioGenerico<OrdenTrabajo> ordenes, RepositorioGenerico<Cliente> clientes,
            RepositorioGenerico<Vehiculo> vehiculos, RepositorioGenerico<Servicio> servicios,
            RepositorioGenerico<Repuesto> repuestos, HistorialController historial) {
        this.ordenes = ordenes;
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.servicios = servicios;
        this.repuestos = repuestos;
        this.historial = historial;
    }

    public void crear(String codigoCliente, String placa, String diagnostico, String mecanico)
            throws ClienteNoEncontradoException {
        String codCliente = codigoCliente == null ? "" : codigoCliente.trim().toUpperCase();
        Cliente cliente = clientes.buscar(codCliente);
        if (cliente == null) {
            throw new ClienteNoEncontradoException(codCliente);
        }
        Vehiculo vehiculo = vehiculos.buscar(Validaciones.codigo(placa, "Vehículo"));
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no existe.");
        }
        if (vehiculo.getPropietario() != cliente) {
            throw new IllegalArgumentException("El vehículo " + vehiculo.getPlaca() + " no pertenece al cliente " + codCliente);
        }
        String diag = Validaciones.texto(diagnostico, "Diagnóstico");
        String mec = Validaciones.texto(mecanico, "Mecánico");

        contador++;
        String numero = String.format("ORD-%03d", contador);
        OrdenTrabajo orden = new OrdenTrabajo(numero, LocalDate.now(), cliente, vehiculo, diag, mec);
        ordenes.agregar(numero, orden);
        historial.registrar(new Operacion("ORDEN_CREADA", "Creación de la orden " + numero, orden, null, 0));
    }

    public OrdenTrabajo buscar(String numero) {
        OrdenTrabajo o = ordenes.buscar(numero);
        if (o == null) {
            throw new IllegalArgumentException("No existe la orden " + numero);
        }
        return o;
    }

    private void exigirPendiente(OrdenTrabajo o) {
        if (o.getEstado() != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("Solo se puede modificar una orden Pendiente. "
                    + "La orden " + o.getNumero() + " está " + o.getEstado() + ".");
        }
    }

    public void agregarServicio(String numeroOrden, String codigoServicio) {
        OrdenTrabajo o = buscar(numeroOrden);
        exigirPendiente(o);
        Servicio s = servicios.buscar(codigoServicio);
        if (s == null) {
            throw new IllegalArgumentException("Seleccione un servicio.");
        }
        o.getServicios().add(s);
        historial.registrar(new Operacion("SERVICIO_AGREGADO",
                "Servicio \"" + s.getDescripcion() + "\" agregado a " + o.getNumero(), o, s, 0));
    }

    public void agregarRepuesto(String numeroOrden, String codigoRepuesto, String cantidad)
            throws StockInsuficienteException {
        OrdenTrabajo o = buscar(numeroOrden);
        exigirPendiente(o);
        Repuesto r = repuestos.buscar(codigoRepuesto);
        if (r == null) {
            throw new IllegalArgumentException("Seleccione un repuesto.");
        }
        int can = Validaciones.entero(cantidad, "Cantidad");
        if (can <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        // Si la orden ya tiene ese repuesto, se suma la cantidad.
        DetalleRepuesto detalle = null;
        for (DetalleRepuesto d : o.getRepuestos()) {
            if (d.getRepuesto() == r) {
                detalle = d;
            }
        }
        int totalPedido = can;
        if (detalle != null) {
            totalPedido = detalle.getCantidad() + can;
        }
        if (totalPedido > r.getCantidad()) {
            throw new StockInsuficienteException("Stock insuficiente del repuesto " + r.getCodigo()
                    + ": disponible " + r.getCantidad() + ", solicitado " + totalPedido);
        }
        if (detalle == null) {
            detalle = new DetalleRepuesto(r, can);
            o.getRepuestos().add(detalle);
        } else {
            detalle.setCantidad(totalPedido);
        }
        historial.registrar(new Operacion("REPUESTO_AGREGADO",
                "Repuesto " + r.getCodigo() + " x" + can + " agregado a " + o.getNumero(), o, detalle, can));
    }

    public void cancelar(String numeroOrden) {
        OrdenTrabajo o = buscar(numeroOrden);
        exigirPendiente(o);
        o.setEstado(EstadoOrden.CANCELADA);
        historial.registrar(new Operacion("ORDEN_CANCELADA", "Cancelación de la orden " + o.getNumero(), o, null, 0));
    }

    // Paso 1 del procesamiento: la orden pasa a "En proceso".
    // Si ya está en proceso (o en otro estado) se rechaza, así no se procesa dos veces.
    public void iniciarProcesamiento(String numeroOrden) {
        OrdenTrabajo o = buscar(numeroOrden);
        if (o.getEstado() == EstadoOrden.EN_PROCESO) {
            throw new IllegalStateException("La orden " + o.getNumero() + " ya se está procesando.");
        }
        if (o.getEstado() != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("Solo se puede procesar una orden Pendiente. Estado actual: " + o.getEstado());
        }
        if (o.getServicios().isEmpty()) {
            throw new IllegalStateException("Agregue al menos un servicio a la orden antes de procesarla.");
        }
        o.setEstado(EstadoOrden.EN_PROCESO);
    }

    // Si el procesamiento se interrumpe, la orden vuelve a Pendiente (sin tocar el inventario).
    public void revertirProcesamiento(String numeroOrden) {
        OrdenTrabajo o = buscar(numeroOrden);
        if (o.getEstado() == EstadoOrden.EN_PROCESO) {
            o.setEstado(EstadoOrden.PENDIENTE);
        }
    }

    // Paso 2: al terminar el proceso se finaliza la orden y se descuenta el inventario UNA sola vez.
    public void finalizarOrden(String numeroOrden) throws StockInsuficienteException {
        OrdenTrabajo o = buscar(numeroOrden);
        if (o.isInventarioDescontado() || o.getEstado() == EstadoOrden.FINALIZADA) {
            throw new IllegalStateException("La orden " + o.getNumero() + " ya fue finalizada: no se descuenta dos veces.");
        }
        if (o.getEstado() != EstadoOrden.EN_PROCESO) {
            throw new IllegalStateException("La orden " + o.getNumero() + " no está en proceso.");
        }
        // Primero se revisa que alcance TODO el stock, para no dejar descuentos a medias.
        for (DetalleRepuesto d : o.getRepuestos()) {
            Repuesto r = d.getRepuesto();
            if (r.getCantidad() < d.getCantidad()) {
                o.setEstado(EstadoOrden.PENDIENTE);
                throw new StockInsuficienteException("No se puede finalizar la orden " + o.getNumero()
                        + ". Stock insuficiente del repuesto " + r.getCodigo()
                        + ": disponible " + r.getCantidad() + ", necesario " + d.getCantidad());
            }
        }
        // Ahora sí se descuenta.
        for (DetalleRepuesto d : o.getRepuestos()) {
            d.getRepuesto().descontar(d.getCantidad());
        }
        o.setInventarioDescontado(true);
        o.setEstado(EstadoOrden.FINALIZADA);
        historial.registrar(new Operacion("ORDEN_FINALIZADA", "Finalización de la orden " + o.getNumero(), o, null, 0));
    }

    public ArrayList<OrdenTrabajo> listar() {
        return ordenes.listar();
    }
}
