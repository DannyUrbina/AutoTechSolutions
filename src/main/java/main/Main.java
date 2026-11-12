package main;

import javax.swing.SwingUtilities;
import vista.VentanaPrincipal;

public class Main {

    public static void main(String[] args) {
        // Las ventanas Swing se crean en el hilo de eventos (EDT).
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
