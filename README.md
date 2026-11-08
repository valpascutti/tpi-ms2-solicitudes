# Sistema de Gestion de Solicitudes Academicas

Trabajo Practico Integrador de Metodologia de Sistemas II (Tecnicatura Universitaria en Programacion a Distancia - UTN).

El objetivo no es agregar funcionalidad, sino mejorar la calidad interna de un sistema existente: diagnosticar code smells y deuda tecnica, aplicar SOLID y Clean Code, refactorizar sin cambiar el comportamiento externo y hacer un code review.

## Integrantes

- Valentina Pascutti
- Angela Irina Ramos

## Descripcion

Modulo de consola que gestiona las solicitudes que los estudiantes presentan en una facultad (certificados, mesas de examen, equivalencias y reclamos). No tiene interfaz grafica ni base de datos: los datos se guardan en memoria.

## Funcionalidades

- Registrar una solicitud validando los datos del estudiante, el tipo, la prioridad y la descripcion.
- Dar de alta responsables y asignarlos a una solicitud.
- Cambiar el estado respetando el ciclo PENDIENTE -> EN_PROCESO -> RESUELTA o RECHAZADA. Una solicitud cerrada no se puede modificar.
- Buscar por id y filtrar por estado, tipo o prioridad.
- Saber si una solicitud esta vencida segun su prioridad (ALTA 2 dias, MEDIA 5, BAJA 10).
- Generar un reporte con totales por estado, por tipo y vencidas.

## Tecnologias

- Java 21
- Maven
- JUnit 5
- Git y GitHub

## Instalacion y uso

Requisitos: JDK 21 y Maven (o un IDE como IntelliJ IDEA o VS Code con Extension Pack for Java).

```
git clone https://github.com/valpascutti/tpi-ms2-solicitudes.git
cd tpi-ms2-solicitudes
```

Correr los tests:
```
mvn test
```

Ejecutar el programa de ejemplo (clase `ar.utn.solicitudes.Main`): desde el IDE, con el boton Run sobre `Main.java`.

En VS Code o IntelliJ tambien se pueden correr los tests desde el panel de tests del IDE.

## Estructura

```
src/main/java/ar/utn/solicitudes/   codigo del sistema
src/test/java/ar/utn/solicitudes/   tests de caracterizacion (CP01 a CP23)
docs/                               diagnostico, refactoring, code review y capturas
```

## Documentacion

- [docs/diagnostico.md](docs/diagnostico.md): etapas 1, 2 y 3 (sistema base, calidad externa e interna, code smells).
- [docs/refactoring.md](docs/refactoring.md): etapas 4, 5 y 6 (SOLID y Clean Code, refactorings, pruebas).
- [docs/code-review.md](docs/code-review.md): etapa 7 (checklist y revision).
- Etapa 8 (conclusiones grupales e individuales): en el informe PDF.

## Tags

- `estado-inicial`: version base con los problemas a diagnosticar.
- `etapa-diagnostico`, `etapa-refactoring`, `etapa-pruebas`, `etapa-docs`: hitos de cada etapa.
- `estado-refactorizado`: version final.