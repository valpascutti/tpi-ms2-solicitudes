package ar.utn.solicitudes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SolicitudManager {

    public static List<Solicitud> lista = new ArrayList<>();
    public static List<Responsable> responsables = new ArrayList<>();
    private int cont = 1;

    public int registrarSolicitud(String legajo, String nombre, String email, String tipo, String prioridad, String desc) {
        if (legajo == null || legajo.isEmpty()) {
            System.out.println("Error: legajo vacio");
            return -1;
        }
        if (nombre == null || nombre.isEmpty()) {
            System.out.println("Error: nombre vacio");
            return -1;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("Error: email invalido");
            return -1;
        }
        if (tipo == null || (!tipo.equals("CERTIFICADO") && !tipo.equals("EXAMEN") && !tipo.equals("EQUIVALENCIA") && !tipo.equals("RECLAMO"))) {
            System.out.println("Error: tipo invalido");
            return -1;
        }
        if (prioridad == null || (!prioridad.equals("BAJA") && !prioridad.equals("MEDIA") && !prioridad.equals("ALTA"))) {
            System.out.println("Error: prioridad invalida");
            return -1;
        }
        if (desc == null || desc.length() < 10) {
            System.out.println("Error: descripcion muy corta");
            return -1;
        }
        Estudiante e = new Estudiante(legajo, nombre, email);
        Solicitud s = new Solicitud();
        s.setId(cont);
        s.setEstudiante(e);
        s.setTipo(tipo);
        s.setPrioridad(prioridad);
        s.setDescripcion(desc);
        s.setEstado("PENDIENTE");
        s.setFechaCreacion(LocalDate.now());
        lista.add(s);
        cont++;
        System.out.println("Solicitud registrada con id " + s.getId());
        return s.getId();
    }

    public void agregarResponsable(int id, String nombre, String area) {
        responsables.add(new Responsable(id, nombre, area));
    }

    public Solicitud buscarPorId(int id) {
        try {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == id) {
                    return lista.get(i);
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        Solicitud s = null;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                s = lista.get(i);
            }
        }
        if (s == null) {
            System.out.println("No existe la solicitud " + id);
            return false;
        }
        String actual = s.getEstado();
        if (actual.equals("PENDIENTE")) {
            if (nuevoEstado.equals("EN_PROCESO") || nuevoEstado.equals("RECHAZADA")) {
                s.setEstado(nuevoEstado);
                return true;
            } else {
                System.out.println("Transicion invalida");
                return false;
            }
        } else if (actual.equals("EN_PROCESO")) {
            if (nuevoEstado.equals("RESUELTA") || nuevoEstado.equals("RECHAZADA")) {
                s.setEstado(nuevoEstado);
                return true;
            } else {
                System.out.println("Transicion invalida");
                return false;
            }
        } else {
            System.out.println("La solicitud ya esta cerrada");
            return false;
        }
    }

    public boolean asignarResponsable(int idSolicitud, int idResponsable) {
        Solicitud s = null;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == idSolicitud) {
                s = lista.get(i);
            }
        }
        if (s == null) {
            System.out.println("No existe la solicitud " + idSolicitud);
            return false;
        }
        Responsable r = null;
        for (int i = 0; i < responsables.size(); i++) {
            if (responsables.get(i).getId() == idResponsable) {
                r = responsables.get(i);
            }
        }
        if (r == null) {
            System.out.println("No existe el responsable " + idResponsable);
            return false;
        }
        if (s.getEstado().equals("RESUELTA") || s.getEstado().equals("RECHAZADA")) {
            System.out.println("No se puede asignar una solicitud cerrada");
            return false;
        }
        s.setResponsable(r);
        if (s.getEstado().equals("PENDIENTE")) {
            s.setEstado("EN_PROCESO");
        }
        return true;
    }

    public List<Solicitud> filtrarPorEstado(String estado) {
        List<Solicitud> aux = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getEstado().equals(estado)) {
                aux.add(lista.get(i));
            }
        }
        return aux;
    }

    public List<Solicitud> filtrarPorTipo(String tipo) {
        List<Solicitud> aux = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getTipo().equals(tipo)) {
                aux.add(lista.get(i));
            }
        }
        return aux;
    }

    public List<Solicitud> filtrarPorPrioridad(String prioridad) {
        List<Solicitud> aux = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getPrioridad().equals(prioridad)) {
                aux.add(lista.get(i));
            }
        }
        return aux;
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
        int p = 0, ep = 0, r = 0, rech = 0;
        int cert = 0, ex = 0, eq = 0, rec = 0;
        int venc = 0;
        for (int i = 0; i < lista.size(); i++) {
            Solicitud s = lista.get(i);
            if (s.getEstado().equals("PENDIENTE")) p++;
            else if (s.getEstado().equals("EN_PROCESO")) ep++;
            else if (s.getEstado().equals("RESUELTA")) r++;
            else if (s.getEstado().equals("RECHAZADA")) rech++;

            if (s.getTipo().equals("CERTIFICADO")) cert++;
            else if (s.getTipo().equals("EXAMEN")) ex++;
            else if (s.getTipo().equals("EQUIVALENCIA")) eq++;
            else if (s.getTipo().equals("RECLAMO")) rec++;

            if (estaVencida(s, LocalDate.now())) venc++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE SOLICITUDES ===\n");
        sb.append("Total: ").append(lista.size()).append("\n");
        sb.append("-- Por estado --\n");
        sb.append("PENDIENTE: ").append(p).append("\n");
        sb.append("EN_PROCESO: ").append(ep).append("\n");
        sb.append("RESUELTA: ").append(r).append("\n");
        sb.append("RECHAZADA: ").append(rech).append("\n");
        sb.append("-- Por tipo --\n");
        sb.append("CERTIFICADO: ").append(cert).append("\n");
        sb.append("EXAMEN: ").append(ex).append("\n");
        sb.append("EQUIVALENCIA: ").append(eq).append("\n");
        sb.append("RECLAMO: ").append(rec).append("\n");
        sb.append("Vencidas: ").append(venc).append("\n");
        System.out.println(sb.toString());
        return sb.toString();
    }
}
