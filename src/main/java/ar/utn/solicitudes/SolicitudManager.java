package ar.utn.solicitudes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SolicitudManager {

    public static List<Solicitud> lista = new ArrayList<>();
    public static List<Responsable> responsables = new ArrayList<>();
    private int proximoId = 1;

    public int registrarSolicitud(String legajo, String nombre, String email, String tipo, String prioridad, String descripcion) {
        String error = validarDatos(legajo, nombre, email, tipo, prioridad, descripcion);
        if (error != null) {
            System.out.println("Error: " + error);
            return -1;
        }
        Solicitud solicitud = crearSolicitud(new Estudiante(legajo, nombre, email), tipo, prioridad, descripcion);
        lista.add(solicitud);
        System.out.println("Solicitud registrada con id " + solicitud.getId());
        return solicitud.getId();
    }

    private String validarDatos(String legajo, String nombre, String email, String tipo, String prioridad, String descripcion) {
        if (estaVacio(legajo)) {
            return "legajo vacio";
        }
        if (estaVacio(nombre)) {
            return "nombre vacio";
        }
        if (!esEmailValido(email)) {
            return "email invalido";
        }
        if (!esTipoValido(tipo)) {
            return "tipo invalido";
        }
        if (!esPrioridadValida(prioridad)) {
            return "prioridad invalida";
        }
        if (descripcion == null || descripcion.length() < 10) {
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

    private boolean esTipoValido(String tipo) {
        return "CERTIFICADO".equals(tipo) || "EXAMEN".equals(tipo) || "EQUIVALENCIA".equals(tipo) || "RECLAMO".equals(tipo);
    }

    private boolean esPrioridadValida(String prioridad) {
        return "BAJA".equals(prioridad) || "MEDIA".equals(prioridad) || "ALTA".equals(prioridad);
    }

    private Solicitud crearSolicitud(Estudiante estudiante, String tipo, String prioridad, String descripcion) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(proximoId++);
        solicitud.setEstudiante(estudiante);
        solicitud.setTipo(tipo);
        solicitud.setPrioridad(prioridad);
        solicitud.setDescripcion(descripcion);
        solicitud.setEstado("PENDIENTE");
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
        if (estaCerrada(solicitud)) {
            System.out.println("La solicitud ya esta cerrada");
            return false;
        }
        if (!esTransicionValida(solicitud.getEstado(), nuevoEstado)) {
            System.out.println("Transicion invalida");
            return false;
        }
        solicitud.setEstado(nuevoEstado);
        return true;
    }

    private boolean esTransicionValida(String estadoActual, String nuevoEstado) {
        if (estadoActual.equals("PENDIENTE")) {
            return nuevoEstado.equals("EN_PROCESO") || nuevoEstado.equals("RECHAZADA");
        }
        if (estadoActual.equals("EN_PROCESO")) {
            return nuevoEstado.equals("RESUELTA") || nuevoEstado.equals("RECHAZADA");
        }
        return false;
    }

    private boolean estaCerrada(Solicitud solicitud) {
        return solicitud.getEstado().equals("RESUELTA") || solicitud.getEstado().equals("RECHAZADA");
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
        if (estaCerrada(solicitud)) {
            System.out.println("No se puede asignar una solicitud cerrada");
            return false;
        }
        solicitud.setResponsable(responsable);
        if (solicitud.getEstado().equals("PENDIENTE")) {
            solicitud.setEstado("EN_PROCESO");
        }
        return true;
    }

    public List<Solicitud> filtrarPorEstado(String estado) {
        return filtrar(solicitud -> solicitud.getEstado().equals(estado));
    }

    public List<Solicitud> filtrarPorTipo(String tipo) {
        return filtrar(solicitud -> solicitud.getTipo().equals(tipo));
    }

    public List<Solicitud> filtrarPorPrioridad(String prioridad) {
        return filtrar(solicitud -> solicitud.getPrioridad().equals(prioridad));
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

    public boolean estaVencida(Solicitud s, LocalDate hoy) {
        if (s.getEstado().equals("RESUELTA") || s.getEstado().equals("RECHAZADA")) {
            return false;
        }
        long d = ChronoUnit.DAYS.between(s.getFechaCreacion(), hoy);
        if (s.getPrioridad().equals("ALTA")) {
            return d > 2;
        } else if (s.getPrioridad().equals("MEDIA")) {
            return d > 5;
        } else {
            return d > 10;
        }
    }

    public String generarReporte() {
        LocalDate hoy = LocalDate.now();
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE SOLICITUDES ===\n");
        reporte.append("Total: ").append(lista.size()).append("\n");
        reporte.append("-- Por estado --\n");
        agregarConteo(reporte, "PENDIENTE", filtrarPorEstado("PENDIENTE").size());
        agregarConteo(reporte, "EN_PROCESO", filtrarPorEstado("EN_PROCESO").size());
        agregarConteo(reporte, "RESUELTA", filtrarPorEstado("RESUELTA").size());
        agregarConteo(reporte, "RECHAZADA", filtrarPorEstado("RECHAZADA").size());
        reporte.append("-- Por tipo --\n");
        agregarConteo(reporte, "CERTIFICADO", filtrarPorTipo("CERTIFICADO").size());
        agregarConteo(reporte, "EXAMEN", filtrarPorTipo("EXAMEN").size());
        agregarConteo(reporte, "EQUIVALENCIA", filtrarPorTipo("EQUIVALENCIA").size());
        agregarConteo(reporte, "RECLAMO", filtrarPorTipo("RECLAMO").size());
        agregarConteo(reporte, "Vencidas", contarVencidas(hoy));
        System.out.println(reporte);
        return reporte.toString();
    }

    private void agregarConteo(StringBuilder reporte, String etiqueta, int cantidad) {
        reporte.append(etiqueta).append(": ").append(cantidad).append("\n");
    }

    private int contarVencidas(LocalDate hoy) {
        return filtrar(solicitud -> estaVencida(solicitud, hoy)).size();
    }
}
