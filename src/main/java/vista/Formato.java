package vista;

import java.util.Locale;

// Da formato de dinero a los números (ejemplo: L 1,532.00).
public class Formato {

    public static String dinero(double valor) {
        return String.format(Locale.US, "L %,.2f", valor);
    }
}
