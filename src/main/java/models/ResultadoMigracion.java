package main.java.models;

public class ResultadoMigracion {

    String tablaMigrada;
    long tiempoTranscurrido;
    int recibidos;
    int insertados;
    int errores;
    EstadoMigracion estadoMigracion;

    /**
     * Constructor con errores para supervisar migracion completa
     * @param tablaMigrada
     * @param tiempoTranscurrido
     * @param recibidos
     * @param insertados
     * @param errores
     * @param estadoMigracion
     */
    public ResultadoMigracion(String tablaMigrada, long tiempoTranscurrido, int recibidos, int insertados, int errores, EstadoMigracion estadoMigracion) {
        this.tablaMigrada = tablaMigrada;
        this.tiempoTranscurrido = tiempoTranscurrido;
        this.recibidos = recibidos;
        this.insertados = insertados;
        this.errores = errores;
        this.estadoMigracion = estadoMigracion;
    }

    public String getTablaMigrada() {
        return tablaMigrada;
    }

    public void setTablaMigrada(String tablaMigrada) {
        this.tablaMigrada = tablaMigrada;
    }

    public long getTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public void setTiempoTranscurrido(long tiempoTranscurrido) {
        this.tiempoTranscurrido = tiempoTranscurrido;
    }

    public int getRecibidos() {
        return recibidos;
    }

    public void setRecibidos(int recibidos) {
        this.recibidos = recibidos;
    }

    public int getInsertados() {
        return insertados;
    }

    public void setInsertados(int insertados) {
        this.insertados = insertados;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public EstadoMigracion getEstadoMigracion() {
        return estadoMigracion;
    }

    public void setEstadoMigracion(EstadoMigracion estadoMigracion) {
        this.estadoMigracion = estadoMigracion;
    }

    @Override
    public String toString() {
        return "[MIGRACION] " + tablaMigrada + " > " + estadoMigracion + "(" +
                insertados + "/" + recibidos + " insertados, "+
                errores + " errores) en " + tiempoTranscurrido + " milisegundos.";
    }
}
