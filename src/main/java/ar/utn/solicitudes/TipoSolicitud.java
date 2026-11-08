package ar.utn.solicitudes;

import java.util.Arrays;
import java.util.Optional;

public enum TipoSolicitud {
    CERTIFICADO,
    EXAMEN,
    EQUIVALENCIA,
    RECLAMO;

    public static Optional<TipoSolicitud> desde(String texto) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.name().equals(texto))
                .findFirst();
    }
}
