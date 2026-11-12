package excepciones;

// Se lanza cuando se pide más repuestos de los que hay en inventario.
public class StockInsuficienteException extends Exception {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
