package ar.utn.solicitudes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioResponsablesEnMemoria implements RepositorioResponsables {

    private final List<Responsable> responsables = new ArrayList<>();

    @Override
    public void guardar(Responsable responsable) {
        responsables.add(responsable);
    }

    @Override
    public Optional<Responsable> buscarPorId(int id) {
        return responsables.stream()
                .filter(responsable -> responsable.getId() == id)
                .findFirst();
    }
}
