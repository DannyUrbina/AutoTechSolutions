package controlador;

import java.util.ArrayList;
import java.util.Stack;
import modelo.*;
import repositorio.RepositorioGenerico;

// Guarda las operaciones en un Stack (pila) y permite deshacer la última.
// Cada tipo de operación se deshace con su acción inversa, restaurando
// todas las estructuras que había afectado (repositorios, inventario, órdenes...).
public class HistorialController {

    private Stack<Operacion> pila = new Stack<>();
    private RepositorioGenerico<Cliente> clientes;
    private RepositorioGenerico<Vehiculo> vehiculos;
    private RepositorioGenerico<Servicio> servicios;
    private RepositorioGenerico<Repuesto> repuestos;
    private RepositorioGenerico<OrdenTrabajo> ordenes;
    private RepositorioGenerico<Factura> facturas;

    public HistorialController(RepositorioGenerico<Cliente> clientes, RepositorioGenerico<Vehiculo> vehiculos,
            RepositorioGenerico<Servicio> servicios, RepositorioGenerico<Repuesto> repuestos,
            RepositorioGenerico<OrdenTrabajo> ordenes, RepositorioGenerico<Factura> facturas) {
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.servicios = servicios;
        this.repuestos = repuestos;
        this.ordenes = ordenes;
        this.facturas = facturas;
    }

    public void registrar(Operacion operacion) {
        pila.push(operacion);
    }

    public boolean hayOperaciones() {
        return !pila.isEmpty();
    }

    public String deshacerUltima() {
        if (pila.isEmpty()) {
            throw new IllegalStateException("No hay operaciones para deshacer.");
        }
        // No se deshace nada mientras una orden se está procesando.
        for (OrdenTrabajo o : ordenes.listar()) {
            if (o.getEstado() == EstadoOrden.EN_PROCESO) {
                throw new IllegalStateException("No se puede deshacer mientras la orden "
                        + o.getNumero() + " se está procesando. Espere a que termine.");
            }
        }

        Operacion op = pila.peek();
        String tipo = op.getTipo();

        if (tipo.equals("CLIENTE_REGISTRADO")) {
            Cliente c = (Cliente) op.getObjeto();
            clientes.eliminar(c.getCodigo());

        } else if (tipo.equals("CLIENTE_MODIFICADO")) {
            Cliente c = (Cliente) op.getObjeto();
            Cliente anterior = (Cliente) op.getExtra();
            c.setNombreCompleto(anterior.getNombreCompleto());
            c.setIdentidad(anterior.getIdentidad());
            c.setTelefono(anterior.getTelefono());
            c.setDireccion(anterior.getDireccion());

        } else if (tipo.equals("CLIENTE_ELIMINADO")) {
            Cliente c = (Cliente) op.getObjeto();
            clientes.agregar(c.getCodigo(), c);

        } else if (tipo.equals("VEHICULO_REGISTRADO")) {
            Vehiculo v = (Vehiculo) op.getObjeto();
            vehiculos.eliminar(v.getPlaca());

        } else if (tipo.equals("SERVICIO_REGISTRADO")) {
            Servicio s = (Servicio) op.getObjeto();
            servicios.eliminar(s.getCodigo());

        } else if (tipo.equals("REPUESTO_REGISTRADO")) {
            Repuesto r = (Repuesto) op.getObjeto();
            repuestos.eliminar(r.getCodigo());

        } else if (tipo.equals("ENTRADA_INVENTARIO")) {
            Repuesto r = (Repuesto) op.getObjeto();
            if (r.getCantidad() < op.getCantidad()) {
                throw new IllegalStateException("No se puede deshacer la entrada: esas existencias ya fueron usadas.");
            }
            r.setCantidad(r.getCantidad() - op.getCantidad());

        } else if (tipo.equals("ORDEN_CREADA")) {
            OrdenTrabajo o = (OrdenTrabajo) op.getObjeto();
            ordenes.eliminar(o.getNumero());

        } else if (tipo.equals("SERVICIO_AGREGADO")) {
            OrdenTrabajo o = (OrdenTrabajo) op.getObjeto();
            o.getServicios().remove((Servicio) op.getExtra());

        } else if (tipo.equals("REPUESTO_AGREGADO")) {
            OrdenTrabajo o = (OrdenTrabajo) op.getObjeto();
            DetalleRepuesto d = (DetalleRepuesto) op.getExtra();
            d.setCantidad(d.getCantidad() - op.getCantidad());
            if (d.getCantidad() <= 0) {
                o.getRepuestos().remove(d);
            }

        } else if (tipo.equals("ORDEN_CANCELADA")) {
            OrdenTrabajo o = (OrdenTrabajo) op.getObjeto();
            o.setEstado(EstadoOrden.PENDIENTE);

        } else if (tipo.equals("ORDEN_FINALIZADA")) {
            // Devuelve al inventario lo que se había descontado y deja la orden Pendiente.
            OrdenTrabajo o = (OrdenTrabajo) op.getObjeto();
            for (DetalleRepuesto d : o.getRepuestos()) {
                d.getRepuesto().aumentar(d.getCantidad());
            }
            o.setInventarioDescontado(false);
            o.setEstado(EstadoOrden.PENDIENTE);

        } else if (tipo.equals("FACTURA_GENERADA")) {
            Factura f = (Factura) op.getObjeto();
            facturas.eliminar(f.getNumero());

        } else if (tipo.equals("PAGO_REGISTRADO")) {
            Factura f = (Factura) op.getObjeto();
            f.deshacerPago();
        }

        pila.pop();
        return op.getDescripcion();
    }
}
