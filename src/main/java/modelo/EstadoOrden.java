package modelo;

public enum EstadoOrden {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private String texto;

    EstadoOrden(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
