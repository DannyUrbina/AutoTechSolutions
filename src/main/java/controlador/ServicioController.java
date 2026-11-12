package controlador;

import java.util.ArrayList;
import modelo.Operacion;
import modelo.Servicio;
import repositorio.RepositorioGenerico;

public class ServicioController {

    private RepositorioGenerico<Servicio> servicios;
    private HistorialController historial;

    public ServicioController(RepositorioGenerico<Servicio> servicios, HistorialController historial) {
        this.servicios = servicios;
        this.historial = historial;
        cargarServiciosIniciales();
    }

    // Servicios iniciales pedidos en el examen. Los precios y tiempos son valores de ejemplo.
    private void cargarServiciosIniciales() {
        agregarInicial("SRV-001", "Cambio de aceite", 600.00, 30);
        agregarInicial("SRV-002", "Revisión de frenos", 450.00, 45);
        agregarInicial("SRV-003", "Diagnóstico electrónico", 800.00, 60);
        agregarInicial("SRV-004", "Alineamiento", 500.00, 60);
        agregarInicial("SRV-005", "Mantenimiento general", 1500.00, 120);
    }

    private void agregarInicial(String codigo, String descripcion, double precio, int tiempo) {
        servicios.agregar(codigo, new Servicio(codigo, descripcion, precio, tiempo));
    }

    public void registrar(String codigo, String descripcion, String precio, String tiempo) {
        String cod = Validaciones.codigo(codigo, "Código");
        String des = Validaciones.texto(descripcion, "Descripción");
        double pre = Validaciones.decimal(precio, "Precio base");
        int tie = Validaciones.entero(tiempo, "Tiempo estimado");
        if (pre <= 0) {
            throw new IllegalArgumentException("El precio base debe ser positivo (mayor que cero).");
        }
        if (tie <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor que cero.");
        }
        if (servicios.existe(cod)) {
            throw new IllegalArgumentException("Ya existe un servicio con el código " + cod);
        }
        Servicio s = new Servicio(cod, des, pre, tie);
        servicios.agregar(cod, s);
        historial.registrar(new Operacion("SERVICIO_REGISTRADO", "Registro del servicio " + cod, s, null, 0));
    }

    public ArrayList<Servicio> listar() {
        return servicios.listar();
    }
}
