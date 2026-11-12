package controlador;

// Métodos de apoyo para validar lo que el usuario escribe en los formularios.
public class Validaciones {

    // Campo obligatorio: no puede estar vacío.
    public static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" es obligatorio.");
        }
        return valor.trim();
    }

    // Códigos y placas se guardan en mayúsculas para que "abc" y "ABC" sean lo mismo.
    public static String codigo(String valor, String campo) {
        return texto(valor, campo).toUpperCase();
    }

    public static int entero(String valor, String campo) {
        try {
            return Integer.parseInt(texto(valor, campo));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" debe ser un número entero.");
        }
    }

    public static double decimal(String valor, String campo) {
        try {
            return Double.parseDouble(texto(valor, campo).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" debe ser un número.");
        }
    }
}
