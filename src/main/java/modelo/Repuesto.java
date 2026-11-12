package modelo;

import excepciones.StockInsuficienteException;

public class Repuesto {

    private String codigo;
    private String nombre;
    private int cantidad;
    private double precioUnitario;
    private int stockMinimo;

    public Repuesto(String codigo, String nombre, int cantidad, double precioUnitario, int stockMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.stockMinimo = stockMinimo;
    }

    // Entrada de inventario.
    public void aumentar(int unidades) {
        cantidad = cantidad + unidades;
    }

    // Salida de inventario: nunca deja la cantidad en negativo.
    public void descontar(int unidades) throws StockInsuficienteException {
        if (unidades > cantidad) {
            throw new StockInsuficienteException("Stock insuficiente del repuesto " + codigo
                    + ": disponible " + cantidad + ", solicitado " + unidades);
        }
        cantidad = cantidad - unidades;
    }

    public boolean estaBajoMinimo() {
        return cantidad <= stockMinimo;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public int getStockMinimo() { return stockMinimo; }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (disponibles: " + cantidad + ")";
    }
}
