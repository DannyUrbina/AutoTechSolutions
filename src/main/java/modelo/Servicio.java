package modelo;

public class Servicio {

    private String codigo;
    private String descripcion;
    private double precioBase;
    private int tiempoEstimado; // en minutos

    public Servicio(String codigo, String descripcion, double precioBase, int tiempoEstimado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioBase() { return precioBase; }
    public int getTiempoEstimado() { return tiempoEstimado; }

    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}
