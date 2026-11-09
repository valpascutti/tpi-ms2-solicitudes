package ar.utn.solicitudes;

import java.util.Arrays;
import java.util.Optional;

public enum Prioridad {
    ALTA(2),
    MEDIA(5),
    BAJA(10);

    private final int diasDePlazo;

    Prioridad(int diasDePlazo) {
        this.diasDePlazo = diasDePlazo;
    }

    public int getDiasDePlazo() {
        return diasDePlazo;
    }

    public static Optional<Prioridad> desde(String texto) {
        return Arrays.stream(values())
                .filter(prioridad -> prioridad.name().equals(texto))
                .findFirst();
    }
}
