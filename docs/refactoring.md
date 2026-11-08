# Refactoring - Sistema de Gestion de Solicitudes Academicas

Rama: `refactor`. Se hizo un commit por cada refactoring y despues de cada uno se corrieron los 23 tests de caracterizacion.

## Etapa 4 - Aplicacion de SOLID y Clean Code

**SRP (Responsabilidad unica)** - Refactorings 5 y 6
- Problema: SolicitudManager hacia todo (Smell 1).
- Mejora: se separaron ValidadorSolicitud, GeneradorReporte y los repositorios. El Manager solo coordina.

**OCP (Abierto/cerrado)** - Refactoring 4
- Problema: if/else por tipo, prioridad y estado (Smell 7).
- Mejora: enums. Para agregar un tipo nuevo alcanza con sumarlo al enum; la validacion y el reporte lo toman solos.

**LSP (Sustitucion de Liskov)** - Refactoring 5
- Problema: no habia herencia ni interfaces.
- Mejora: cualquier implementacion de RepositorioSolicitudes (memoria, archivo, base de datos) puede reemplazar a otra sin cambiar el Manager.

**ISP (Segregacion de interfaces)** - Refactoring 5
- Mejora: dos interfaces chicas (RepositorioSolicitudes y RepositorioResponsables) en lugar de una general.

**DIP (Inversion de dependencias)** - Refactoring 5
- Problema: listas public static usadas directamente.
- Mejora: el Manager depende de interfaces y las recibe por constructor.

**Clean Code** - Refactorings 1, 2, 4 y 6
- Problema: nombres poco claros (p, ep, aux, cont, d), metodos largos, numeros sueltos, codigos de error y catch vacio (Smells 4 y 6).
- Mejora: nombres descriptivos, metodos cortos, constantes y excepcion propia.

## Etapa 5 - Refactorings aplicados

### Refactoring 1 - Extract Method (Smells 2 y 6)
Commit: `Refactoring 1: Extract Method en SolicitudManager (smells 2 y 6)`

- registrarSolicitud tenia seis if seguidos. Se extrajeron validarDatos, estaVacio, esEmailValido y crearSolicitud.
- El for que buscaba una solicitud por id estaba copiado tres veces. Ahora cambiarEstado y asignarResponsable usan buscarPorId. Se elimino el catch vacio.
- Los tres filtros tenian el mismo for. Ahora usan un unico metodo filtrar que recibe el criterio.
- generarReporte usaba nueve contadores. Se extrajeron agregarConteo y contarVencidas.
- Se renombraron variables: cont -> proximoId, aux -> resultado, desc -> descripcion, s -> solicitud.

Antes:
```java
Solicitud s = null;
for (int i = 0; i < lista.size(); i++) {
    if (lista.get(i).getId() == id) {
        s = lista.get(i);
    }
}
```
Despues:
```java
Solicitud solicitud = buscarPorId(id);
```

### Refactoring 2 - Move Method (Smell 8)
Commit: `Refactoring 2: Move Method estaVencida a Solicitud (smell 8)`

estaVencida estaba en SolicitudManager pero solo usaba datos de Solicitud. Se movio a Solicitud junto con estaCerrada. Los plazos 2, 5 y 10 pasaron a constantes con nombre. El Manager conserva estaVencida(solicitud, hoy) delegando en la solicitud, para no cambiar su interfaz publica.

Antes:
```java
// en SolicitudManager
public boolean estaVencida(Solicitud s, LocalDate hoy) {
    if (s.getEstado().equals("RESUELTA") || s.getEstado().equals("RECHAZADA")) {
        return false;
    }
    long d = ChronoUnit.DAYS.between(s.getFechaCreacion(), hoy);
    if (s.getPrioridad().equals("ALTA")) {
        return d > 2;
    } ...
}
```
Despues:
```java
// en Solicitud
public boolean estaVencida(LocalDate hoy) {
    if (estaCerrada()) {
        return false;
    }
    long diasTranscurridos = ChronoUnit.DAYS.between(fechaCreacion, hoy);
    return diasTranscurridos > prioridad.getDiasDePlazo();
}
```
(La version final usa prioridad.getDiasDePlazo() despues del refactoring 4.)

### Refactoring 3 - Introduce Parameter Object (Smell 5)
Commit: `Refactoring 3: Introduce Parameter Object con Estudiante (smell 5)`

registrarSolicitud recibia seis String. Legajo, nombre y email siempre viajan juntos, asi que se agruparon en la clase Estudiante, que ya existia.

Antes:
```java
public int registrarSolicitud(String legajo, String nombre, String email, String tipo, String prioridad, String desc)
```
Despues:
```java
public int registrarSolicitud(Estudiante estudiante, String tipo, String prioridad, String descripcion)
```

### Refactoring 4 - Replace Type Code with Enum (Smells 3 y 7)
Commit: `Refactoring 4: Replace Type Code with Enum (smells 3 y 7)`

Tipo, prioridad y estado pasaron de String a los enums TipoSolicitud, Prioridad y EstadoSolicitud.
- Prioridad guarda sus dias de plazo (ALTA 2, MEDIA 5, BAJA 10).
- Cada EstadoSolicitud sabe a que estados puede pasar (metodo puedePasarA). Se reemplazo el if/else de cambiarEstado por polimorfismo.
- El reporte recorre los valores de los enums en lugar de usar contadores fijos.
- El Manager sigue recibiendo String en sus metodos publicos y los convierte a enum en la entrada. Adentro del sistema todo queda tipado.

Antes:
```java
if (actual.equals("PENDIENTE")) {
    if (nuevoEstado.equals("EN_PROCESO") || nuevoEstado.equals("RECHAZADA")) {
        ...
} else if (actual.equals("EN_PROCESO")) {
    ...
```
Despues:
```java
if (estadoDestino.isEmpty() || !solicitud.getEstado().puedePasarA(estadoDestino.get())) {
    System.out.println("Transicion invalida");
    return false;
}
```

### Refactoring 5 - Extract Class (Smell 1)
Commit: `Refactoring 5: Extract Class de repositorios y reporte (smell 1)`

Se dividio SolicitudManager:
- RepositorioSolicitudes y RepositorioResponsables (interfaces) con sus implementaciones en memoria. Las listas dejaron de ser public static.
- GeneradorReporte arma el texto del reporte.
- El Manager recibe estas piezas por constructor. Se agrego un constructor sin parametros que usa las implementaciones en memoria.

Antes:
```java
public static List<Solicitud> lista = new ArrayList<>();
public static List<Responsable> responsables = new ArrayList<>();
```
Despues:
```java
private final RepositorioSolicitudes repositorioSolicitudes;
private final RepositorioResponsables repositorioResponsables;
private final GeneradorReporte generadorReporte;
```

### Refactoring 6 - Excepcion en lugar de codigos de error (Smell 4)
Commit: `Refactoring 6: excepcion propia para validaciones (smell 4)`

Las validaciones pasaron a ValidadorSolicitud, que lanza SolicitudInvalidaException con el motivo del error. registrarSolicitud la captura y sigue devolviendo -1 para no cambiar el comportamiento externo.

Antes:
```java
if (email == null || !email.contains("@") || !email.contains(".")) {
    System.out.println("Error: email invalido");
    return -1;
}
```
Despues:
```java
try {
    validador.validarEstudiante(estudiante);
    TipoSolicitud tipoSolicitud = validador.validarTipo(tipo);
    ...
} catch (SolicitudInvalidaException error) {
    System.out.println("Error: " + error.getMessage());
    return -1;
}
```

## Etapa 6 - Pruebas y verificacion

Se usaron los 23 tests de caracterizacion (CP01 a CP23) que se escribieron sobre la version inicial. Se corrieron antes de empezar y despues de cada refactoring.

| Momento                              | Resultado      | Evidencia            |
|--------------------------------------|----------------|----------------------|
| Version inicial (tag estado-inicial) | 23/23 en verde | tests-antes.png      |
| Despues de cada refactoring (1 a 6)  | 23/23 en verde | historial de commits |
| Version final                        | 23/23 en verde | tests-despues.png    |

Ademas se comparo la salida del Main antes y despues: es identica (mismos mensajes y mismo reporte).

### Cambios en los tests
Los valores esperados no se modificaron. Solo cambio la forma de llamar al codigo cuando cambio su interfaz:
- Refactoring 3: las llamadas a registrarSolicitud usan `new Estudiante(...)`.
- Refactoring 4: las seis comparaciones de estado usan `EstadoSolicitud.PENDIENTE` en lugar del texto "PENDIENTE".
- Refactoring 5: el setUp ya no vacia listas estaticas (cada test crea un Manager nuevo) y CP03 usa `listarSolicitudes()`.

## Pendientes
- SonarQube sugiere reemplazar System.out por un logger. No se cambio porque el sistema es de consola y la salida es parte del comportamiento esperado.
- SonarQube sugiere pasar un Clock a LocalDate.now() para no depender de la fecha de la maquina.
