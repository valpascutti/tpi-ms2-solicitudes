package ar.utn.solicitudes;

import java.util.Optional;

public interface RepositorioResponsables {

    void guardar(Responsable responsable);

    Optional<Responsable> buscarPorId(int id);
}
