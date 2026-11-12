package modelo;

// Un repuesto usado en una orden y la cantidad que se usó.
public class DetalleRepuesto {

    private Repuesto repuesto;
    private int cantidad;

    public DetalleRepuesto(Repuesto repuesto, int cantidad) {
        this.repuesto = repuesto;
        this.cantidad = cantidad;
    }

    public Repuesto getRepuesto() { return repuesto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getSubtotal() {
        return repuesto.getPrecioUnitario() * cantidad;
    }
}
