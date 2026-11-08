package ar.utn.solicitudes;

import java.util.List;
import java.util.Optional;

public interface RepositorioSolicitudes {

    void guardar(Solicitud solicitud);

    Optional<Solicitud> buscarPorId(int id);

    List<Solicitud> listarTodas();
}
