package vista;

import controlador.ReporteController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PanelReportes extends PanelBase {

    private ReporteController controlador;
    private CanvasReporte2D graficoIngresos = new CanvasReporte2D("Ingresos por servicio facturado (L)", true);
    private CanvasReporte2D graficoEstados = new CanvasReporte2D("Órdenes por estado", false);

    public PanelReportes(ReporteController controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(crearBoton("Actualizar gráficos", e -> actualizar()));
        add(botones, BorderLayout.NORTH);

        JPanel graficos = new JPanel(new GridLayout(2, 1, 10, 10));
        graficos.add(graficoIngresos);
        graficos.add(graficoEstados);
        add(graficos, BorderLayout.CENTER);
        actualizar();
    }

    @Override
    public void actualizar() {
        graficoIngresos.mostrar(controlador.nombresServicios(), controlador.ingresosPorServicio());
        graficoEstados.mostrar(controlador.nombresEstados(), controlador.ordenesPorEstado());
    }
}
