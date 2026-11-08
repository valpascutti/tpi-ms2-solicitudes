package ar.utn.solicitudes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SolicitudManager {

    private static final int LARGO_MINIMO_DESCRIPCION = 10;

    public static List<Solicitud> lista = new ArrayList<>();
    public static List<Responsable> responsables = new ArrayList<>();
    private int proximoId = 1;

    public int registrarSolicitud(Estudiante estudiante, String tipo, String prioridad, String descripcion) {
        Optional<TipoSolicitud> tipoSolicitud = TipoSolicitud.desde(tipo);
        Optional<Prioridad> prioridadSolicitud = Prioridad.desde(prioridad);
        String error = validarDatos(estudiante, tipoSolicitud, prioridadSolicitud, descripcion);
        if (error != null) {
            System.out.println("Error: " + error);
            return -1;
        }
        Solicitud solicitud = crearSolicitud(estudiante, tipoSolicitud.get(), prioridadSolicitud.get(), descripcion);
        lista.add(solicitud);
        System.out.println("Solicitud registrada con id " + solicitud.getId());
        return solicitud.getId();
    }

    private String validarDatos(Estudiante estudiante, Optional<TipoSolicitud> tipo, Optional<Prioridad> prioridad, String descripcion) {
        if (estudiante == null || estaVacio(estudiante.getLegajo())) {
            return "legajo vacio";
        }
        if (estaVacio(estudiante.getNombre())) {
            return "nombre vacio";
        }
        if (!esEmailValido(estudiante.getEmail())) {
            return "email invalido";
        }
        if (tipo.isEmpty()) {
            return "tipo invalido";
        }
        if (prioridad.isEmpty()) {
            return "prioridad invalida";
        }
        if (descripcion == null || descripcion.length() < LARGO_MINIMO_DESCRIPCION) {
            return "descripcion muy corta";
        }
        return null;
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isEmpty();
    }

    private boolean esEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
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
        responsables.add(new Responsable(id, nombre, area));
    }

    public Solicitud buscarPorId(int id) {
        for (Solicitud solicitud : lista) {
            if (solicitud.getId() == id) {
                return solicitud;
            }
        }
        return null;
    }

    private Responsable buscarResponsable(int id) {
        for (Responsable responsable : responsables) {
            if (responsable.getId() == id) {
                return responsable;
            }
        }
        return null;
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) {
            System.out.println("No existe la solicitud " + id);
            return false;
        }
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
        Solicitud solicitud = buscarPorId(idSolicitud);
        if (solicitud == null) {
            System.out.println("No existe la solicitud " + idSolicitud);
            return false;
        }
        Responsable responsable = buscarResponsable(idResponsable);
        if (responsable == null) {
            System.out.println("No existe el responsable " + idResponsable);
            return false;
        }
        if (solicitud.estaCerrada()) {
            System.out.println("No se puede asignar una solicitud cerrada");
            return false;
        }
        solicitud.setResponsable(responsable);
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
        List<Solicitud> resultado = new ArrayList<>();
        for (Solicitud solicitud : lista) {
            if (criterio.test(solicitud)) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    public boolean estaVencida(Solicitud solicitud, LocalDate hoy) {
        return solicitud.estaVencida(hoy);
    }

    public String generarReporte() {
        LocalDate hoy = LocalDate.now();
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE SOLICITUDES ===\n");
        reporte.append("Total: ").append(lista.size()).append("\n");
        reporte.append("-- Por estado --\n");
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            agregarConteo(reporte, estado.name(), filtrar(solicitud -> solicitud.getEstado() == estado).size());
        }
        reporte.append("-- Por tipo --\n");
        for (TipoSolicitud tipo : TipoSolicitud.values()) {
            agregarConteo(reporte, tipo.name(), filtrar(solicitud -> solicitud.getTipo() == tipo).size());
        }
        agregarConteo(reporte, "Vencidas", contarVencidas(hoy));
        System.out.println(reporte);
        return reporte.toString();
    }

    private void agregarConteo(StringBuilder reporte, String etiqueta, int cantidad) {
        reporte.append(etiqueta).append(": ").append(cantidad).append("\n");
    }

    private int contarVencidas(LocalDate hoy) {
        return filtrar(solicitud -> solicitud.estaVencida(hoy)).size();
    }
}
