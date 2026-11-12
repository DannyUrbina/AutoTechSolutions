package excepciones;

// Se lanza cuando se busca o se usa un cliente que no existe.
public class ClienteNoEncontradoException extends Exception {

    public ClienteNoEncontradoException(String codigo) {
        super("No existe ningún cliente con el código: " + codigo);
    }
}
