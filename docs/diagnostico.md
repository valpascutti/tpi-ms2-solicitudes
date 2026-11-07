# Diagnostico de calidad - Sistema de Gestion de Solicitudes Academicas

## Etapa 1 - Descripcion del sistema base

### Dominio: 
Gestion de solicitudes academicas de una facultad (mesas de examen, equivalencias, certificados y reclamos)

### Alcance: 
modulo de backen en consola. Registra solicitudes, controla su ciclo de vida, las asigna a un responsable, permite consultarlas y
genera un reporte. No tiene interfaz grafica ni base de datos.

### Actores:

Estudiante: crea la solicitud (legajo, nombre, email, tipo, prioridad, descripcion)
Personal administrativo: Registra la solicitud, la asigna y cambia su estado
Responsable / area: queda asignado a la solicitud y la resuelve o rechaa
Coordinacion: Consulta filtros y el reporte general

### Funcionalidades:
- registrarSolicitud: carga una solicitud nueva, valida los datos y la deja en estado PENDIENTE.
- agregarResponsable: da de alta un responsable con su area.
- buscarPorId: busca una solicitud por su numero de id.
- cambiarEstado: cambia el estado de una solicitud respetando el orden PENDIENTE -> EN_PROCESO -> RESUELTA o RECHAZADA. Una solicitud cerrada no se puede modificar.
- asignarResponsable: asigna un responsable a la solicitud y, si estaba PENDIENTE, la pasa a EN_PROCESO.
- filtrarPorEstado / filtrarPorTipo / filtrarPorPrioridad: devuelven las solicitudes que coinciden con el criterio.
- estaVencida: indica si una solicitud abierta supero el plazo segun su prioridad (ALTA 2 dias, MEDIA 5, BAJA 10).
- generarReporte: muestra el total de solicitudes, cuantas hay por estado y por tipo, y cuantas estan vencidas.

### Tecnologia:
Java 21, Maven, JUnit 5 para los tests, IntelliJ IDEA y Git/GitHub.

### Repositorio:
https://github.com/valpascutti/tpi-ms2-solicitudes

### Evidencia de funcionamiento:
[Captura: Ejecucion_inicial.png - salida del Main con el reporte]
[Captura: tests-antes.png - 23 tests de caracterizacion pasando]