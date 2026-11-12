package hilos;

import controlador.OrdenController;
import excepciones.StockInsuficienteException;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

// Simula el procesamiento de una orden en un hilo aparte (Runnable).
// - run() NO se ejecuta en el EDT, por eso Thread.sleep() no congela la ventana.
// - Los componentes Swing (barra y etiqueta) solo se modifican con SwingUtilities.invokeLater,
//   que ejecuta el código en el EDT.
// Dura 50 pasos x 120 ms = 6 segundos (el examen pide al menos 5).
public class ProcesoServicioHilo implements Runnable {

    private OrdenController controlador;
    private String numeroOrden;
    private JProgressBar barra;
    private JLabel etiqueta;
    private Component ventana;
    private Runnable alTerminar;

    public ProcesoServicioHilo(OrdenController controlador, String numeroOrden, JProgressBar barra,
            JLabel etiqueta, Component ventana, Runnable alTerminar) {
        this.controlador = controlador;
        this.numeroOrden = numeroOrden;
        this.barra = barra;
        this.etiqueta = etiqueta;
        this.ventana = ventana;
        this.alTerminar = alTerminar;
    }

    @Override
    public void run() {
        try {
            for (int paso = 1; paso <= 50; paso++) {
                Thread.sleep(120);
                final int porcentaje = paso * 2;
                SwingUtilities.invokeLater(() -> {
                    barra.setValue(porcentaje);
                    etiqueta.setText("Procesando orden " + numeroOrden + "... " + porcentaje + " %");
                });
            }
        } catch (InterruptedException e) {
            // Si se interrumpe, la orden vuelve a Pendiente.
            SwingUtilities.invokeLater(() -> {
                controlador.revertirProcesamiento(numeroOrden);
                etiqueta.setText("Procesamiento interrumpido.");
                alTerminar.run();
            });
            return;
        }

        // Al terminar, la orden se finaliza y se descuenta el inventario (también en el EDT).
        SwingUtilities.invokeLater(() -> {
            try {
                controlador.finalizarOrden(numeroOrden);
                etiqueta.setText("Orden " + numeroOrden + " finalizada.");
                alTerminar.run();
                JOptionPane.showMessageDialog(ventana, "La orden " + numeroOrden
                        + " fue finalizada y el inventario se actualizó.", "Procesamiento completado",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (StockInsuficienteException e) {
                barra.setValue(0);
                etiqueta.setText("No se pudo finalizar la orden " + numeroOrden + ".");
                alTerminar.run();
                JOptionPane.showMessageDialog(ventana, e.getMessage(), "Stock insuficiente",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
