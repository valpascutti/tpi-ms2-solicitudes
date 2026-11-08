package ar.utn.solicitudes;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public class GeneradorReporte {

    public String generar(List<Solicitud> solicitudes, LocalDate hoy) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE SOLICITUDES ===\n");
        reporte.append("Total: ").append(solicitudes.size()).append("\n");
        reporte.append("-- Por estado --\n");
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            agregarConteo(reporte, estado.name(), contar(solicitudes, solicitud -> solicitud.getEstado() == estado));
        }
        reporte.append("-- Por tipo --\n");
        for (TipoSolicitud tipo : TipoSolicitud.values()) {
            agregarConteo(reporte, tipo.name(), contar(solicitudes, solicitud -> solicitud.getTipo() == tipo));
        }
        agregarConteo(reporte, "Vencidas", contar(solicitudes, solicitud -> solicitud.estaVencida(hoy)));
        return reporte.toString();
    }

    private long contar(List<Solicitud> solicitudes, Predicate<Solicitud> criterio) {
        return solicitudes.stream().filter(criterio).count();
    }

    private void agregarConteo(StringBuilder reporte, String etiqueta, long cantidad) {
        reporte.append(etiqueta).append(": ").append(cantidad).append("\n");
    }
}
