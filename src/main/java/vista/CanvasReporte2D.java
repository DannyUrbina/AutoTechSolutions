package vista;

import java.awt.*;
import javax.swing.JPanel;

// Gráfico de barras dibujado a mano con Java 2D.
// Todo se dibuja en paintComponent() usando Graphics2D, con los datos que recibe en mostrar().
public class CanvasReporte2D extends JPanel {

    private String titulo;
    private String[] etiquetas = new String[0];
    private double[] valores = new double[0];
    private boolean esDinero;
    private Color[] colores = {new Color(46, 134, 193), new Color(40, 180, 99), new Color(243, 156, 18),
        new Color(192, 57, 43), new Color(142, 68, 173), new Color(22, 160, 133)};

    public CanvasReporte2D(String titulo, boolean esDinero) {
        this.titulo = titulo;
        this.esDinero = esDinero;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 280));
    }

    // Recibe los datos reales y vuelve a dibujar.
    public void mostrar(String[] etiquetas, double[] valores) {
        this.etiquetas = etiquetas;
        this.valores = valores;
        repaint();
    }

    private String texto(double valor) {
        if (esDinero) {
            return Formato.dinero(valor);
        }
        return String.valueOf((int) valor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        // Título
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.setColor(new Color(31, 78, 121));
        FontMetrics fmTitulo = g2.getFontMetrics();
        g2.drawString(titulo, (ancho - fmTitulo.stringWidth(titulo)) / 2, 25);

        // Área del gráfico
        int izquierda = 90;
        int derecha = 30;
        int arriba = 45;
        int abajo = 50;
        int areaAncho = ancho - izquierda - derecha;
        int areaAlto = alto - arriba - abajo;
        if (areaAncho < 50 || areaAlto < 50) {
            return;
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        // Valor máximo para escalar las barras
        double maximo = 0;
        for (double v : valores) {
            if (v > maximo) {
                maximo = v;
            }
        }
        if (maximo == 0) {
            maximo = 1;
        }

        // Cantidad de líneas guía. En el gráfico de cantidades el eje usa números enteros.
        int lineas = 4;
        if (!esDinero) {
            if (maximo <= 4) {
                lineas = (int) Math.ceil(maximo);
                maximo = lineas;
            } else {
                maximo = Math.ceil(maximo / 4) * 4;
            }
        }

        // Líneas guía y números del eje Y
        for (int i = 0; i <= lineas; i++) {
            int y = arriba + areaAlto - (areaAlto * i / lineas);
            g2.setColor(new Color(225, 225, 225));
            g2.drawLine(izquierda, y, izquierda + areaAncho, y);
            g2.setColor(Color.DARK_GRAY);
            String numero = texto(maximo * i / lineas);
            g2.drawString(numero, izquierda - 8 - fm.stringWidth(numero), y + 4);
        }

        // Ejes
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(izquierda, arriba, izquierda, arriba + areaAlto);
        g2.drawLine(izquierda, arriba + areaAlto, izquierda + areaAncho, arriba + areaAlto);

        // Barras
        int cantidad = valores.length;
        if (cantidad == 0) {
            g2.drawString("Sin datos", izquierda + 20, arriba + areaAlto / 2);
            return;
        }
        int espacio = areaAncho / cantidad;
        int anchoBarra = Math.min(80, espacio * 6 / 10);
        for (int i = 0; i < cantidad; i++) {
            int altoBarra = (int) (areaAlto * valores[i] / maximo);
            int x = izquierda + i * espacio + (espacio - anchoBarra) / 2;
            int y = arriba + areaAlto - altoBarra;

            g2.setColor(colores[i % colores.length]);
            g2.fillRect(x, y, anchoBarra, altoBarra);

            // Cantidad encima de la barra
            g2.setColor(Color.BLACK);
            String valorTexto = texto(valores[i]);
            g2.drawString(valorTexto, x + (anchoBarra - fm.stringWidth(valorTexto)) / 2, y - 5);

            // Etiqueta debajo de la barra (se recorta si es muy larga)
            String etiqueta = etiquetas[i];
            while (etiqueta.length() > 3 && fm.stringWidth(etiqueta) > espacio - 4) {
                etiqueta = etiqueta.substring(0, etiqueta.length() - 1);
            }
            g2.drawString(etiqueta, izquierda + i * espacio + (espacio - fm.stringWidth(etiqueta)) / 2,
                    arriba + areaAlto + 18);
        }
    }
}
