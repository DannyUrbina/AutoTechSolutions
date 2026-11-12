package excepciones;

// Se lanza cuando el efectivo recibido es menor que el total de la factura.
public class PagoInsuficienteException extends Exception {

    public PagoInsuficienteException(double total, double recibido) {
        super(String.format(java.util.Locale.US,
                "Pago insuficiente. Total: L %.2f, recibido: L %.2f, faltan: L %.2f",
                total, recibido, total - recibido));
    }
}
