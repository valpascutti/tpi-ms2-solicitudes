package ar.utn.solicitudes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SolicitudManager {

    private final RepositorioSolicitudes repositorioSolicitudes;
    private final RepositorioResponsables repositorioResponsables;
    private final GeneradorReporte generadorReporte;
    private final ValidadorSolicitud validador = new ValidadorSolicitud();
    private int proximoId = 1;

    public SolicitudManager() {
        this(new RepositorioSolicitudesEnMemoria(), new RepositorioResponsablesEnMemoria(), new GeneradorReporte());
    }

    public SolicitudManager(RepositorioSolicitudes repositorioSolicitudes,
                            RepositorioResponsables repositorioResponsables,
                            GeneradorReporte generadorReporte) {
        this.repositorioSolicitudes = repositorioSolicitudes;
        this.repositorioResponsables = repositorioResponsables;
        this.generadorReporte = generadorReporte;
    }

    public int registrarSolicitud(Estudiante estudiante, String tipo, String prioridad, String descripcion) {
        try {
            validador.validarEstudiante(estudiante);
            TipoSolicitud tipoSolicitud = validador.validarTipo(tipo);
            Prioridad prioridadSolicitud = validador.validarPrioridad(prioridad);
            validador.validarDescripcion(descripcion);
            Solicitud solicitud = crearSolicitud(estudiante, tipoSolicitud, prioridadSolicitud, descripcion);
            repositorioSolicitudes.guardar(solicitud);
            System.out.println("Solicitud registrada con id " + solicitud.getId());
            return solicitud.getId();
        } catch (SolicitudInvalidaException error) {
            System.out.println("Error: " + error.getMessage());
            return -1;
        }
    }

    private Solicitud crearSolicitud(Estudiante estudiante, TipoSolicitud tipo, Prioridad prioridad, String descripcion) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(proximoId++);
        solicitud.setEstudiante(estudiante);
        solicitud.setTipo(tipo);
        solicitud.setPrioridad(prioridad);
        solicitud.setDescripcion(descripcion);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaCreacion(LocalDate.now());
        return solicitud;
    }

    public void agregarResponsable(int id, String nombre, String area) {
        repositorioResponsables.guardar(new Responsable(id, nombre, area));
    }

    public Solicitud buscarPorId(int id) {
        return repositorioSolicitudes.buscarPorId(id).orElse(null);
    }

    public List<Solicitud> listarSolicitudes() {
        return repositorioSolicitudes.listarTodas();
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        Optional<Solicitud> encontrada = repositorioSolicitudes.buscarPorId(id);
        if (encontrada.isEmpty()) {
            System.out.println("No existe la solicitud " + id);
            return false;
        }
        Solicitud solicitud = encontrada.get();
        if (solicitud.estaCerrada()) {
            System.out.println("La solicitud ya esta cerrada");
            return false;
        }
        Optional<EstadoSolicitud> estadoDestino = EstadoSolicitud.desde(nuevoEstado);
        if (estadoDestino.isEmpty() || !solicitud.getEstado().puedePasarA(estadoDestino.get())) {
            System.out.println("Transicion invalida");
            return false;
        }
        solicitud.setEstado(estadoDestino.get());
        return true;
    }

    public boolean asignarResponsable(int idSolicitud, int idResponsable) {
        Optional<Solicitud> encontrada = repositorioSolicitudes.buscarPorId(idSolicitud);
        if (encontrada.isEmpty()) {
            System.out.println("No existe la solicitud " + idSolicitud);
            return false;
        }
        Optional<Responsable> responsable = repositorioResponsables.buscarPorId(idResponsable);
        if (responsable.isEmpty()) {
            System.out.println("No existe el responsable " + idResponsable);
            return false;
        }
        Solicitud solicitud = encontrada.get();
        if (solicitud.estaCerrada()) {
            System.out.println("No se puede asignar una solicitud cerrada");
            return false;
        }
        solicitud.setResponsable(responsable.get());
        if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
        }
        return true;
    }

    public List<Solicitud> filtrarPorEstado(String estado) {
        return filtrar(solicitud -> solicitud.getEstado().name().equals(estado));
    }

    public List<Solicitud> filtrarPorTipo(String tipo) {
        return filtrar(solicitud -> solicitud.getTipo().name().equals(tipo));
    }

    public List<Solicitud> filtrarPorPrioridad(String prioridad) {
        return filtrar(solicitud -> solicitud.getPrioridad().name().equals(prioridad));
    }

    private List<Solicitud> filtrar(Predicate<Solicitud> criterio) {
        return repositorioSolicitudes.listarTodas().stream()
                .filter(criterio)
                .toList();
    }

    public boolean estaVencida(Solicitud solicitud, LocalDate hoy) {
        return solicitud.estaVencida(hoy);
    }

    public String generarReporte() {
        String reporte = generadorReporte.generar(repositorioSolicitudes.listarTodas(), LocalDate.now());
        System.out.println(reporte);
        return reporte;
    }
}
