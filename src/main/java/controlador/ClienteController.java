package controlador;

import java.util.ArrayList;
import excepciones.ClienteNoEncontradoException;
import modelo.Cliente;
import modelo.Operacion;
import modelo.Vehiculo;
import repositorio.RepositorioGenerico;

public class ClienteController {

    private RepositorioGenerico<Cliente> clientes;
    private RepositorioGenerico<Vehiculo> vehiculos;
    private HistorialController historial;

    public ClienteController(RepositorioGenerico<Cliente> clientes, RepositorioGenerico<Vehiculo> vehiculos,
            HistorialController historial) {
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.historial = historial;
    }

    public void registrar(String codigo, String nombre, String identidad, String telefono, String direccion) {
        String cod = Validaciones.codigo(codigo, "Código");
        String nom = Validaciones.texto(nombre, "Nombre completo");
        String ide = Validaciones.texto(identidad, "Identidad");
        String tel = Validaciones.texto(telefono, "Teléfono");
        String dir = Validaciones.texto(direccion, "Dirección");
        if (clientes.existe(cod)) {
            throw new IllegalArgumentException("Ya existe un cliente con el código " + cod);
        }
        Cliente c = new Cliente(cod, nom, ide, tel, dir);
        clientes.agregar(cod, c);
        historial.registrar(new Operacion("CLIENTE_REGISTRADO", "Registro del cliente " + cod, c, null, 0));
    }

    public Cliente buscar(String codigo) throws ClienteNoEncontradoException {
        String cod = codigo == null ? "" : codigo.trim().toUpperCase();
        Cliente c = clientes.buscar(cod);
        if (c == null) {
            throw new ClienteNoEncontradoException(cod);
        }
        return c;
    }

    public void modificar(String codigo, String nombre, String identidad, String telefono, String direccion)
            throws ClienteNoEncontradoException {
        Cliente c = buscar(codigo);
        String nom = Validaciones.texto(nombre, "Nombre completo");
        String ide = Validaciones.texto(identidad, "Identidad");
        String tel = Validaciones.texto(telefono, "Teléfono");
        String dir = Validaciones.texto(direccion, "Dirección");
        // Se guarda una copia con los datos anteriores para poder deshacer.
        Cliente copia = new Cliente(c.getCodigo(), c.getNombreCompleto(), c.getIdentidad(), c.getTelefono(), c.getDireccion());
        c.setNombreCompleto(nom);
        c.setIdentidad(ide);
        c.setTelefono(tel);
        c.setDireccion(dir);
        historial.registrar(new Operacion("CLIENTE_MODIFICADO", "Modificación del cliente " + c.getCodigo(), c, copia, 0));
    }

    public void eliminar(String codigo) throws ClienteNoEncontradoException {
        Cliente c = buscar(codigo);
        for (Vehiculo v : vehiculos.listar()) {
            if (v.getPropietario() == c) {
                throw new IllegalStateException("No se puede eliminar al cliente " + c.getCodigo()
                        + " porque tiene vehículos registrados.");
            }
        }
        clientes.eliminar(c.getCodigo());
        historial.registrar(new Operacion("CLIENTE_ELIMINADO", "Eliminación del cliente " + c.getCodigo(), c, null, 0));
    }

    public ArrayList<Cliente> listar() {
        return clientes.listar();
    }
}
