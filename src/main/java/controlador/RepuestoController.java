package controlador;

import java.util.ArrayList;
import modelo.Operacion;
import modelo.Repuesto;
import repositorio.RepositorioGenerico;

public class RepuestoController {

    private RepositorioGenerico<Repuesto> repuestos;
    private HistorialController historial;

    public RepuestoController(RepositorioGenerico<Repuesto> repuestos, HistorialController historial) {
        this.repuestos = repuestos;
        this.historial = historial;
    }

    public void registrar(String codigo, String nombre, String cantidad, String precio, String stockMinimo) {
        String cod = Validaciones.codigo(codigo, "Código");
        String nom = Validaciones.texto(nombre, "Nombre");
        int can = Validaciones.entero(cantidad, "Cantidad");
        double pre = Validaciones.decimal(precio, "Precio unitario");
        int min = Validaciones.entero(stockMinimo, "Stock mínimo");
        if (can < 0 || min < 0) {
            throw new IllegalArgumentException("No se permiten cantidades negativas.");
        }
        if (pre <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser positivo.");
        }
        if (repuestos.existe(cod)) {
            throw new IllegalArgumentException("Ya existe un repuesto con el código " + cod);
        }
        Repuesto r = new Repuesto(cod, nom, can, pre, min);
        repuestos.agregar(cod, r);
        historial.registrar(new Operacion("REPUESTO_REGISTRADO", "Registro del repuesto " + cod, r, null, 0));
    }

    // Entrada de inventario: suma existencias a un repuesto que ya existe.
    public void registrarEntrada(String codigo, String cantidad) {
        String cod = Validaciones.codigo(codigo, "Código");
        int can = Validaciones.entero(cantidad, "Cantidad de la entrada");
        if (can <= 0) {
            throw new IllegalArgumentException("La cantidad de la entrada debe ser mayor que cero (no se aceptan negativos).");
        }
        Repuesto r = repuestos.buscar(cod);
        if (r == null) {
            throw new IllegalArgumentException("No existe un repuesto con el código " + cod);
        }
        r.aumentar(can);
        historial.registrar(new Operacion("ENTRADA_INVENTARIO",
                "Entrada de " + can + " unidades al repuesto " + cod, r, null, can));
    }

    public ArrayList<Repuesto> listar() {
        return repuestos.listar();
    }

    // Repuestos cuyas existencias están en el stock mínimo o por debajo.
    public ArrayList<Repuesto> alertasStockMinimo() {
        ArrayList<Repuesto> alertas = new ArrayList<>();
        for (Repuesto r : repuestos.listar()) {
            if (r.estaBajoMinimo()) {
                alertas.add(r);
            }
        }
        return alertas;
    }
}
