package ar.utn.solicitudes;

public class ValidadorSolicitud {

    private static final int LARGO_MINIMO_DESCRIPCION = 10;

    public void validarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estaVacio(estudiante.getLegajo())) {
            throw new SolicitudInvalidaException("legajo vacio");
        }
        if (estaVacio(estudiante.getNombre())) {
            throw new SolicitudInvalidaException("nombre vacio");
        }
        if (!esEmailValido(estudiante.getEmail())) {
            throw new SolicitudInvalidaException("email invalido");
        }
    }

    public TipoSolicitud validarTipo(String tipo) {
        return TipoSolicitud.desde(tipo)
                .orElseThrow(() -> new SolicitudInvalidaException("tipo invalido"));
    }

    public Prioridad validarPrioridad(String prioridad) {
        return Prioridad.desde(prioridad)
                .orElseThrow(() -> new SolicitudInvalidaException("prioridad invalida"));
    }

    public void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.length() < LARGO_MINIMO_DESCRIPCION) {
            throw new SolicitudInvalidaException("descripcion muy corta");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isEmpty();
    }

    private boolean esEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
