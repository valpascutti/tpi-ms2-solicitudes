# Code review - Pull Request #1 (refactor -> main)

Revision: Valentina Pascutti. Revision del trabajo de las etapas 4, 5 y 6 en el Pull Request #1:
https://github.com/valpascutti/tpi-ms2-solicitudes/pull/1

Se reviso archivo por archivo en la pestaña Files changed, usando el siguiente checklist.

## Checklist tecnico

### Logica y funcionalidad
- [x] Hace lo mismo que la version inicial: los 23 tests siguen pasando.
- [x] Se siguen rechazando los datos invalidos y las solicitudes cerradas (tests CP03 a CP15).
- [x] El reporte sale igual que antes (CP23 pasa sin cambios).

### Legibilidad
- [x] Los nombres se entienden: ya no estan p, ep, d, aux ni cont.
- [x] Los metodos son cortos y hacen una sola cosa.
- [x] No quedan numeros sueltos sin nombre: los plazos estan en el enum Prioridad.

### Diseño
- [x] Cada clase tiene una sola responsabilidad: validador, repositorios y reporte separados.
- [x] Se puede agregar un tipo o estado nuevo sin tocar varios metodos: alcanza con agregarlo en el enum.
- [x] El Manager depende de interfaces: recibe los repositorios por constructor.
- [x] No hay codigo repetido.
- [ ] Se usan las formas mas actuales de Java: ver observacion 1.

### Manejo de errores
- [x] No hay catch vacios.
- [x] Cuando algo falla se sabe por que: la excepcion trae el motivo.

### Tests
- [x] No se cambio ningun resultado esperado, solo la forma de llamar a los metodos.

### Seguridad y rendimiento
- [x] No hay datos personales reales: son datos inventados.
- [x] No hay recorridos innecesarios.

## Observaciones y feedback

1. SolicitudManager.java, linea 127 (Request changes):
   "Sugerencia (SonarQube java:S6204): desde Java 16 se puede usar Stream.toList() en lugar de collect(Collectors.toList()). Es mas corto y devuelve una lista no modificable, lo que evita que se altere el resultado del filtro desde afuera. Al cambiarlo, tambien se puede eliminar el import de Collectors."

Comentario general: "Buen trabajo con los refactorings. Dejo una sugerencia menor sobre el filtro antes de aprobar."

Mejoras que no frenan el merge:
- Todavia se usa System.out: se mantiene porque el sistema es de consola y esa salida es parte de lo que ve el usuario.
- LocalDate.now() depende de la fecha de la maquina.

## Correcciones realizadas

- Commit 9572f95: se reemplazo collect(Collectors.toList()) por toList() en filtrar y se elimino el import de Collectors. Los 23 tests siguen pasando. Conversacion marcada como resuelta.

## Criterios de aceptacion tecnica

- [x] Los 23 tests de caracterizacion pasan.
- [x] Ningun test cambio su resultado esperado.
- [x] La salida del Main es igual a la de la version inicial.
- [x] Cada refactoring esta en su propio commit, con un mensaje claro.
- [x] No quedan observaciones sin resolver.
- [x] La documentacion (refactoring.md y README) esta actualizada.

Resultado: Pull Request aprobado y mergeado a main con merge commit.
