package controlador;

import java.util.ArrayList;
import excepciones.ClienteNoEncontradoException;
import modelo.Cliente;
import modelo.Operacion;
import modelo.Vehiculo;
import repositorio.RepositorioGenerico;

public class VehiculoController {

    private RepositorioGenerico<Vehiculo> vehiculos;
    private RepositorioGenerico<Cliente> clientes;
    private HistorialController historial;

    public VehiculoController(RepositorioGenerico<Vehiculo> vehiculos, RepositorioGenerico<Cliente> clientes,
            HistorialController historial) {
        this.vehiculos = vehiculos;
        this.clientes = clientes;
        this.historial = historial;
    }

    public void registrar(String placa, String marca, String modelo, String anio, String color,
            String codigoCliente) throws ClienteNoEncontradoException {
        String pla = Validaciones.codigo(placa, "Placa");
        String mar = Validaciones.texto(marca, "Marca");
        String mod = Validaciones.texto(modelo, "Modelo");
        int an = Validaciones.entero(anio, "Año");
        String col = Validaciones.texto(color, "Color");
        // El vehículo solo se puede asociar a un cliente que exista.
        String codCliente = codigoCliente == null ? "" : codigoCliente.trim().toUpperCase();
        Cliente propietario = clientes.buscar(codCliente);
        if (propietario == null) {
            throw new ClienteNoEncontradoException(codCliente);
        }
        if (vehiculos.existe(pla)) {
            throw new IllegalArgumentException("Ya existe un vehículo con la placa " + pla);
        }
        Vehiculo v = new Vehiculo(pla, mar, mod, an, col, propietario);
        vehiculos.agregar(pla, v);
        historial.registrar(new Operacion("VEHICULO_REGISTRADO", "Registro del vehículo " + pla, v, null, 0));
    }

    // Devuelve null si no existe ningún vehículo con esa placa.
    public Vehiculo buscarPorPlaca(String placa) {
        String pla = placa == null ? "" : placa.trim().toUpperCase();
        return vehiculos.buscar(pla);
    }

    public ArrayList<Vehiculo> listarPorCliente(String codigoCliente) throws ClienteNoEncontradoException {
        String cod = codigoCliente == null ? "" : codigoCliente.trim().toUpperCase();
        Cliente c = clientes.buscar(cod);
        if (c == null) {
            throw new ClienteNoEncontradoException(cod);
        }
        ArrayList<Vehiculo> resultado = new ArrayList<>();
        for (Vehiculo v : vehiculos.listar()) {
            if (v.getPropietario() == c) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public ArrayList<Vehiculo> listar() {
        return vehiculos.listar();
    }
}
