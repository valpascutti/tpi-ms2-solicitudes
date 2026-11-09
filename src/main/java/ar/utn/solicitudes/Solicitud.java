package ar.utn.solicitudes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Solicitud {


    private int id;
    private Estudiante estudiante;
    private TipoSolicitud tipo;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
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

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
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
        return diasTranscurridos > prioridad.getDiasDePlazo();
    }

    public boolean estaCerrada() {
        return estado.estaCerrado();
    }
}
