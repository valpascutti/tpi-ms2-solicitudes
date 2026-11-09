package ar.utn.solicitudes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioSolicitudesEnMemoria implements RepositorioSolicitudes {

    private final List<Solicitud> solicitudes = new ArrayList<>();

    @Override
    public void guardar(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }

    @Override
    public Optional<Solicitud> buscarPorId(int id) {
        return solicitudes.stream()
                .filter(solicitud -> solicitud.getId() == id)
                .findFirst();
    }

    @Override
    public List<Solicitud> listarTodas() {
        return new ArrayList<>(solicitudes);
    }
}
