package ar.utn.solicitudes;

public class Main {

    public static void main(String[] args) {
        SolicitudManager m = new SolicitudManager();

        m.agregarResponsable(1, "Bedelia Central", "BEDELIA");
        m.agregarResponsable(2, "Coordinacion TUP", "COORDINACION");

        int id1 = m.registrarSolicitud("10001", "Estudiante Uno", "uno@mail.com", "CERTIFICADO", "MEDIA", "Certificado de alumno regular");
        int id2 = m.registrarSolicitud("10002", "Estudiante Dos", "dos@mail.com", "EXAMEN", "ALTA", "Pedido de mesa especial de examen");
        int id3 = m.registrarSolicitud("10003", "Estudiante Tres", "tres@mail.com", "RECLAMO", "BAJA", "Reclamo por nota mal cargada");
        m.registrarSolicitud("10004", "Estudiante Cuatro", "mail-invalido", "EXAMEN", "ALTA", "Pedido con email invalido");

        m.asignarResponsable(id1, 1);
        m.cambiarEstado(id1, "RESUELTA");
        m.asignarResponsable(id2, 2);
        m.cambiarEstado(id3, "RECHAZADA");
        m.cambiarEstado(id3, "EN_PROCESO");

        System.out.println("Pendientes: " + m.filtrarPorEstado("PENDIENTE").size());
        System.out.println("Examenes: " + m.filtrarPorTipo("EXAMEN").size());

        m.generarReporte();
    }
}
