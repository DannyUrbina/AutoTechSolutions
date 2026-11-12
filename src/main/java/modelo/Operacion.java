package modelo;

// Una operación guardada en el historial (Stack) para poder deshacerla.
public class Operacion {

    private String tipo;        // por ejemplo: "CLIENTE_REGISTRADO"
    private String descripcion; // texto que se muestra al usuario
    private Object objeto;      // objeto principal afectado
    private Object extra;       // dato adicional (copia anterior, servicio, detalle...)
    private int cantidad;       // cantidad afectada (si aplica)

    public Operacion(String tipo, String descripcion, Object objeto, Object extra, int cantidad) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.objeto = objeto;
        this.extra = extra;
        this.cantidad = cantidad;
    }

    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Object getObjeto() { return objeto; }
    public Object getExtra() { return extra; }
    public int getCantidad() { return cantidad; }
}
