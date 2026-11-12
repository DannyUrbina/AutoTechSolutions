package vista;

import excepciones.ClienteNoEncontradoException;
import excepciones.PagoInsuficienteException;
import excepciones.StockInsuficienteException;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

// Clase base de todas las pestañas. Tiene lo que comparten:
// mostrar mensajes con JOptionPane, crear botones y tablas de solo lectura.
public abstract class PanelBase extends JPanel {

    // Cada panel recarga sus datos desde los controladores.
    public abstract void actualizar();

    protected void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "AutoTech Solutions", JOptionPane.INFORMATION_MESSAGE);
    }

    // Muestra el error con un título según el tipo de excepción. La aplicación no se cierra.
    protected void mostrarError(Exception e) {
        String titulo = "Error";
        if (e instanceof StockInsuficienteException) {
            titulo = "Stock insuficiente";
        } else if (e instanceof ClienteNoEncontradoException) {
            titulo = "Cliente no encontrado";
        } else if (e instanceof PagoInsuficienteException) {
            titulo = "Pago insuficiente";
        } else if (e instanceof IllegalArgumentException) {
            titulo = "Datos no válidos";
        } else if (e instanceof IllegalStateException) {
            titulo = "Operación no permitida";
        }
        JOptionPane.showMessageDialog(this, e.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
    }

    protected JButton crearBoton(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        return boton;
    }

    // Modelo de tabla donde las celdas no se pueden editar.
    protected DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}
