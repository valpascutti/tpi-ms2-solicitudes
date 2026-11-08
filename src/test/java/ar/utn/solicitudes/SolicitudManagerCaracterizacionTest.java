package ar.utn.solicitudes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudManagerCaracterizacionTest {

    private SolicitudManager manager;

    @BeforeEach
    void setUp() {
        SolicitudManager.lista.clear();
        SolicitudManager.responsables.clear();
        manager = new SolicitudManager();
        manager.agregarResponsable(1, "Bedelia Central", "BEDELIA");
    }

    private int registrarValida(String tipo, String prioridad) {
        return manager.registrarSolicitud(new Estudiante("10001", "Estudiante Uno", "uno@mail.com"), tipo, prioridad, "Descripcion de prueba valida");
    }

    @Nested
    @DisplayName("CP01-CP07 Registrar solicitud")
    class Registrar {

        @Test
        @DisplayName("CP01 Solicitud valida queda registrada como PENDIENTE")
        void registraSolicitudValida() {
            int id = registrarValida("CERTIFICADO", "MEDIA");

            Solicitud s = manager.buscarPorId(id);
            assertEquals(1, id);
            assertEquals(EstadoSolicitud.PENDIENTE, s.getEstado());
            assertEquals("10001", s.getEstudiante().getLegajo());
            assertEquals(LocalDate.now(), s.getFechaCreacion());
        }

        @Test
        @DisplayName("CP02 Los ids se asignan en forma incremental")
        void idsIncrementales() {
            assertEquals(1, registrarValida("CERTIFICADO", "MEDIA"));
            assertEquals(2, registrarValida("EXAMEN", "ALTA"));
            assertEquals(3, registrarValida("RECLAMO", "BAJA"));
        }

        @Test
        @DisplayName("CP03 Email invalido se rechaza y no se guarda")
        void emailInvalido() {
            int id = manager.registrarSolicitud(new Estudiante("10001", "Estudiante Uno", "mail-invalido"), "EXAMEN", "ALTA", "Descripcion de prueba valida");

            assertEquals(-1, id);
            assertTrue(SolicitudManager.lista.isEmpty());
        }

        @Test
        @DisplayName("CP04 Tipo inexistente se rechaza")
        void tipoInvalido() {
            assertEquals(-1, registrarValida("BECA", "ALTA"));
        }

        @Test
        @DisplayName("CP05 Prioridad inexistente se rechaza")
        void prioridadInvalida() {
            assertEquals(-1, registrarValida("EXAMEN", "URGENTE"));
        }

        @Test
        @DisplayName("CP06 Descripcion de menos de 10 caracteres se rechaza")
        void descripcionCorta() {
            int id = manager.registrarSolicitud(new Estudiante("10001", "Estudiante Uno", "uno@mail.com"), "EXAMEN", "ALTA", "corta");
            assertEquals(-1, id);
        }

        @Test
        @DisplayName("CP07 Legajo vacio se rechaza")
        void legajoVacio() {
            int id = manager.registrarSolicitud(new Estudiante("", "Estudiante Uno", "uno@mail.com"), "EXAMEN", "ALTA", "Descripcion de prueba valida");
            assertEquals(-1, id);
        }
    }

    @Nested
    @DisplayName("CP08-CP12 Cambiar estado")
    class CambiarEstado {

        @Test
        @DisplayName("CP08 PENDIENTE puede pasar a EN_PROCESO")
        void pendienteAEnProceso() {
            int id = registrarValida("EXAMEN", "ALTA");

            assertTrue(manager.cambiarEstado(id, "EN_PROCESO"));
            assertEquals(EstadoSolicitud.EN_PROCESO, manager.buscarPorId(id).getEstado());
        }

        @Test
        @DisplayName("CP09 PENDIENTE no puede pasar directo a RESUELTA")
        void pendienteAResueltaInvalido() {
            int id = registrarValida("EXAMEN", "ALTA");

            assertFalse(manager.cambiarEstado(id, "RESUELTA"));
            assertEquals(EstadoSolicitud.PENDIENTE, manager.buscarPorId(id).getEstado());
        }

        @Test
        @DisplayName("CP10 EN_PROCESO puede pasar a RESUELTA")
        void enProcesoAResuelta() {
            int id = registrarValida("EXAMEN", "ALTA");
            manager.cambiarEstado(id, "EN_PROCESO");

            assertTrue(manager.cambiarEstado(id, "RESUELTA"));
            assertEquals(EstadoSolicitud.RESUELTA, manager.buscarPorId(id).getEstado());
        }

        @Test
        @DisplayName("CP11 Una solicitud cerrada no cambia de estado")
        void cerradaNoCambia() {
            int id = registrarValida("RECLAMO", "BAJA");
            manager.cambiarEstado(id, "RECHAZADA");

            assertFalse(manager.cambiarEstado(id, "EN_PROCESO"));
            assertEquals(EstadoSolicitud.RECHAZADA, manager.buscarPorId(id).getEstado());
        }

        @Test
        @DisplayName("CP12 Cambiar estado de una solicitud inexistente falla")
        void solicitudInexistente() {
            assertFalse(manager.cambiarEstado(99, "EN_PROCESO"));
        }
    }

    @Nested
    @DisplayName("CP13-CP15 Asignar responsable")
    class AsignarResponsable {

        @Test
        @DisplayName("CP13 Asignar responsable pasa la solicitud a EN_PROCESO")
        void asignaYPasaAEnProceso() {
            int id = registrarValida("CERTIFICADO", "MEDIA");

            assertTrue(manager.asignarResponsable(id, 1));
            Solicitud s = manager.buscarPorId(id);
            assertEquals("Bedelia Central", s.getResponsable().getNombre());
            assertEquals(EstadoSolicitud.EN_PROCESO, s.getEstado());
        }

        @Test
        @DisplayName("CP14 Responsable inexistente falla")
        void responsableInexistente() {
            int id = registrarValida("CERTIFICADO", "MEDIA");

            assertFalse(manager.asignarResponsable(id, 99));
            assertNull(manager.buscarPorId(id).getResponsable());
        }

        @Test
        @DisplayName("CP15 No se asigna responsable a una solicitud cerrada")
        void solicitudCerrada() {
            int id = registrarValida("CERTIFICADO", "MEDIA");
            manager.cambiarEstado(id, "RECHAZADA");

            assertFalse(manager.asignarResponsable(id, 1));
        }
    }

    @Nested
    @DisplayName("CP16-CP18 Buscar y filtrar")
    class Filtrar {

        @Test
        @DisplayName("CP16 Filtrar por estado")
        void porEstado() {
            int id = registrarValida("CERTIFICADO", "MEDIA");
            registrarValida("EXAMEN", "ALTA");
            manager.cambiarEstado(id, "EN_PROCESO");

            assertEquals(1, manager.filtrarPorEstado("PENDIENTE").size());
            assertEquals(1, manager.filtrarPorEstado("EN_PROCESO").size());
            assertEquals(0, manager.filtrarPorEstado("RESUELTA").size());
        }

        @Test
        @DisplayName("CP17 Filtrar por tipo y prioridad")
        void porTipoYPrioridad() {
            registrarValida("EXAMEN", "ALTA");
            registrarValida("EXAMEN", "BAJA");
            registrarValida("RECLAMO", "ALTA");

            assertEquals(2, manager.filtrarPorTipo("EXAMEN").size());
            assertEquals(2, manager.filtrarPorPrioridad("ALTA").size());
        }

        @Test
        @DisplayName("CP18 Buscar un id inexistente devuelve null")
        void buscarInexistente() {
            assertNull(manager.buscarPorId(99));
        }
    }

    @Nested
    @DisplayName("CP19-CP22 Vencimiento segun prioridad")
    class Vencimiento {

        private boolean vencidaA(String prioridad, int dias) {
            int id = registrarValida("EXAMEN", prioridad);
            Solicitud s = manager.buscarPorId(id);
            return manager.estaVencida(s, LocalDate.now().plusDays(dias));
        }

        @Test
        @DisplayName("CP19 ALTA vence despues de 2 dias")
        void alta() {
            assertFalse(vencidaA("ALTA", 2));
            assertTrue(vencidaA("ALTA", 3));
        }

        @Test
        @DisplayName("CP20 MEDIA vence despues de 5 dias")
        void media() {
            assertFalse(vencidaA("MEDIA", 5));
            assertTrue(vencidaA("MEDIA", 6));
        }

        @Test
        @DisplayName("CP21 BAJA vence despues de 10 dias")
        void baja() {
            assertFalse(vencidaA("BAJA", 10));
            assertTrue(vencidaA("BAJA", 11));
        }

        @Test
        @DisplayName("CP22 Una solicitud cerrada nunca esta vencida")
        void cerradaNoVence() {
            int id = registrarValida("EXAMEN", "ALTA");
            manager.cambiarEstado(id, "RECHAZADA");

            assertFalse(manager.estaVencida(manager.buscarPorId(id), LocalDate.now().plusDays(30)));
        }
    }

    @Nested
    @DisplayName("CP23 Reporte")
    class Reporte {

        @Test
        @DisplayName("CP23 El reporte mantiene exactamente el mismo formato y totales")
        void reporteGoldenMaster() {
            manager.agregarResponsable(2, "Coordinacion TUP", "COORDINACION");
            int id1 = manager.registrarSolicitud(new Estudiante("10001", "Estudiante Uno", "uno@mail.com"), "CERTIFICADO", "MEDIA", "Certificado de alumno regular");
            int id2 = manager.registrarSolicitud(new Estudiante("10002", "Estudiante Dos", "dos@mail.com"), "EXAMEN", "ALTA", "Pedido de mesa especial de examen");
            int id3 = manager.registrarSolicitud(new Estudiante("10003", "Estudiante Tres", "tres@mail.com"), "RECLAMO", "BAJA", "Reclamo por nota mal cargada");
            manager.registrarSolicitud(new Estudiante("10004", "Estudiante Cuatro", "mail-invalido"), "EXAMEN", "ALTA", "Pedido con email invalido");
            manager.asignarResponsable(id1, 1);
            manager.cambiarEstado(id1, "RESUELTA");
            manager.asignarResponsable(id2, 2);
            manager.cambiarEstado(id3, "RECHAZADA");

            String esperado = """
                    === REPORTE DE SOLICITUDES ===
                    Total: 3
                    -- Por estado --
                    PENDIENTE: 0
                    EN_PROCESO: 1
                    RESUELTA: 1
                    RECHAZADA: 1
                    -- Por tipo --
                    CERTIFICADO: 1
                    EXAMEN: 1
                    EQUIVALENCIA: 0
                    RECLAMO: 1
                    Vencidas: 0
                    """;

            assertEquals(esperado, manager.generarReporte());
        }
    }
}
