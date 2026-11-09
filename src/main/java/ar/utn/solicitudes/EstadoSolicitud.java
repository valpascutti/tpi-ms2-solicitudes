package ar.utn.solicitudes;

import java.util.Arrays;
import java.util.Optional;

public enum EstadoSolicitud {
    PENDIENTE {
        @Override
        public boolean puedePasarA(EstadoSolicitud nuevo) {
            return nuevo == EN_PROCESO || nuevo == RECHAZADA;
        }
    },
    EN_PROCESO {
        @Override
        public boolean puedePasarA(EstadoSolicitud nuevo) {
            return nuevo == RESUELTA || nuevo == RECHAZADA;
        }
    },
    RESUELTA,
    RECHAZADA;

    public boolean puedePasarA(EstadoSolicitud nuevo) {
        return false;
    }

    public boolean estaCerrado() {
        return this == RESUELTA || this == RECHAZADA;
    }

    public static Optional<EstadoSolicitud> desde(String texto) {
        return Arrays.stream(values())
                .filter(estado -> estado.name().equals(texto))
                .findFirst();
    }
}
