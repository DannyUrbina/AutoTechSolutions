package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import modelo.*;
import repositorio.RepositorioGenerico;

// Prepara los datos reales de los dos gráficos.
public class ReporteController {

    private RepositorioGenerico<Servicio> servicios;
    private RepositorioGenerico<Factura> facturas;
    private RepositorioGenerico<OrdenTrabajo> ordenes;

    public ReporteController(RepositorioGenerico<Servicio> servicios, RepositorioGenerico<Factura> facturas,
            RepositorioGenerico<OrdenTrabajo> ordenes) {
        this.servicios = servicios;
        this.facturas = facturas;
        this.ordenes = ordenes;
    }

    // Gráfico 1: nombres de los servicios.
    public String[] nombresServicios() {
        ArrayList<Servicio> lista = servicios.listar();
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            nombres[i] = lista.get(i).getDescripcion();
        }
        return nombres;
    }

    // Gráfico 1: ingreso de cada servicio sumando las facturas generadas.
    public double[] ingresosPorServicio() {
        HashMap<String, Double> ingresos = new HashMap<>();
        for (Factura f : facturas.listar()) {
            for (Servicio s : f.getOrden().getServicios()) {
                double actual = 0;
                if (ingresos.containsKey(s.getCodigo())) {
                    actual = ingresos.get(s.getCodigo());
                }
                ingresos.put(s.getCodigo(), actual + s.getPrecioBase());
            }
        }
        ArrayList<Servicio> lista = servicios.listar();
        double[] valores = new double[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            if (ingresos.containsKey(lista.get(i).getCodigo())) {
                valores[i] = ingresos.get(lista.get(i).getCodigo());
            }
        }
        return valores;
    }

    // Gráfico 2: nombres de los estados.
    public String[] nombresEstados() {
        EstadoOrden[] estados = EstadoOrden.values();
        String[] nombres = new String[estados.length];
        for (int i = 0; i < estados.length; i++) {
            nombres[i] = estados[i].toString();
        }
        return nombres;
    }

    // Gráfico 2: cantidad de órdenes de cada estado.
    public double[] ordenesPorEstado() {
        EstadoOrden[] estados = EstadoOrden.values();
        double[] cantidades = new double[estados.length];
        for (OrdenTrabajo o : ordenes.listar()) {
            for (int i = 0; i < estados.length; i++) {
                if (o.getEstado() == estados[i]) {
                    cantidades[i]++;
                }
            }
        }
        return cantidades;
    }
}
