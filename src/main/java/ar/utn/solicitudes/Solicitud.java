package ar.utn.solicitudes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Solicitud {

    private static final int DIAS_PLAZO_ALTA = 2;
    private static final int DIAS_PLAZO_MEDIA = 5;
    private static final int DIAS_PLAZO_BAJA = 10;

    private int id;
    private Estudiante estudiante;
    private String tipo;
    private String prioridad;
    private String estado;
    private String descripcion;
    private Responsable responsable;
    private LocalDate fechaCreacion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean estaVencida(LocalDate hoy) {
        if (estaCerrada()) {
            return false;
        }
        long diasTranscurridos = ChronoUnit.DAYS.between(fechaCreacion, hoy);
        return diasTranscurridos > diasDePlazo();
    }

    public boolean estaCerrada() {
        return estado.equals("RESUELTA") || estado.equals("RECHAZADA");
    }

    private int diasDePlazo() {
        if (prioridad.equals("ALTA")) {
            return DIAS_PLAZO_ALTA;
        }
        if (prioridad.equals("MEDIA")) {
            return DIAS_PLAZO_MEDIA;
        }
        return DIAS_PLAZO_BAJA;
    }
}
