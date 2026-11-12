package modelo;

public class Cliente {

    private String codigo;
    private String nombreCompleto;
    private String identidad;
    private String telefono;
    private String direccion;

    public Cliente(String codigo, String nombreCompleto, String identidad, String telefono, String direccion) {
        this.codigo = codigo;
        this.nombreCompleto = nombreCompleto;
        this.identidad = identidad;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getCodigo() { return codigo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getIdentidad() { return identidad; }
    public void setIdentidad(String identidad) { this.identidad = identidad; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return codigo + " - " + nombreCompleto;
    }
}
