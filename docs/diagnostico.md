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


## Etapa 2 - Calidad externa vs. calidad interna

### Calidad externa
Desde el punto de vista del usuario, el sistema funciona, esto se comprueba porque los 23 tests pasaron.
Ademas, el sistema rechaza datos invalidos, por ejemplo un email sin @, un tipo de solicitud que no existe (como BECA) o una descripcion de menos de 10 caracteres. Tambien respeta el orden de los estados y no deja modificar una solicitud que ya esta cerrada.

Un punto debil es que cuando hay un error solo se muestra un mensaje por consola, y el metodo devuelve -1 o false. Quien lo llama no puede saber que fue lo que fallo.

En resumen, la calidad externa es buena: el sistema cumple con lo que el usuario necesita.

### Calidad interna
Por dentro, el codigo tiene varios problemas que no se ven desde afuera pero que complican el mantenimiento:

- Legibilidad: se usan nombres poco claros como p, ep, d, aux o cont, y hay metodos muy largos como registrarSolicitud y generarReporte.
- Cohesion: la clase SolicitudManager hace todo. Valida datos, guarda las solicitudes, las busca, cambia estados, arma el reporte e imprime por consola.
- Acoplamiento: las listas de solicitudes y responsables son public static, asi que cualquier clase puede leerlas o modificarlas.
- Extensibilidad: el tipo, la prioridad y el estado son Strings comparados con if/else. Para agregar un valor nuevo hay que tocar varios metodos.
- Testabilidad: como las listas son static, en los tests hay que vaciarlas a mano antes de cada prueba para que no se mezclen los datos.

En resumen, la calidad interna es baja.

### Relacion entre ambas
Las dos calidades no van de la mano. El usuario ve un sistema que funciona, pero cada cambio que se pida va a costar mas de lo que deberia.

Por ejemplo, si la facultad pide agregar el tipo de solicitud BECA, hay que modificar la validacion en registrarSolicitud y el conteo en generarReporte. Si alguien se olvida de uno de los dos, el reporte queda mal y nada avisa del error.

Lo mismo pasa si se quiere agregar un estado nuevo como EN_REVISION: hay que tocar cambiarEstado, asignarResponsable, estaVencida y generarReporte.

Ese costo extra que se paga en cada cambio es la deuda tecnica del sistema. El objetivo del refactoring es mejorar la calidad interna sin que el usuario note ninguna diferencia en el funcionamiento.

## Etapa 3 - Deuda tecnica y code smells

Archivo analizado: SolicitudManager.java (215 lineas)

### Smell 1 - Clase gigante (God Class)
- Ubicacion: SolicitudManager, toda la clase.
- Evidencia: la misma clase valida datos, guarda solicitudes, las busca y filtra, cambia estados, calcula vencimientos, arma el reporte e imprime por consola.
- Impacto: viola el principio de responsabilidad unica (SRP). La clase tiene muchos motivos para cambiar.
- Riesgo: alto. Cualquier cambio, por chico que sea, se hace sobre la misma clase y puede romper otra funcionalidad que no tiene relacion.
- Prioridad: alta.

### Smell 2 - Codigo duplicado
- Ubicacion: busqueda de solicitud por id en las lineas 60-64, 72-76 y 106-110. Los tres metodos filtrarPor... en las lineas 136-164.
- Evidencia: el mismo for que recorre la lista buscando un id esta copiado en buscarPorId, cambiarEstado y asignarResponsable. Los tres filtros son iguales y solo cambia el getter que usan.
- Impacto: viola el principio DRY.
- Riesgo: alto. Si cambia la forma de buscar, hay que corregirlo en tres lugares, y si se olvida uno quedan comportamientos distintos.
- Prioridad: alta.

### Smell 3 - Obsesion por primitivos (Primitive Obsession)
- Ubicacion: los campos tipo, prioridad y estado son String (por ejemplo, linea 46: s.setEstado("PENDIENTE")). El email se valida con contains("@") en la linea 23.
- Evidencia: los valores validos estan escritos como texto en varios metodos.
- Impacto: si alguien escribe "PENDIENTES" o "Alta", el codigo compila igual y el error aparece recien en ejecucion.
- Riesgo: alto. Son bugs silenciosos y dificiles de encontrar.
- Prioridad: alta.

### Smell 4 - Manejo de errores con codigos de retorno
- Ubicacion: registrarSolicitud devuelve -1 en las lineas 17, 21, 25, 29, 33 y 37. cambiarEstado y asignarResponsable devuelven false. buscarPorId devuelve null y tiene un catch vacio en las lineas 65-66.
- Evidencia: el metodo devuelve -1 por seis motivos distintos y el error solo se informa con System.out.println.
- Impacto: va en contra de Clean Code, que recomienda lanzar excepciones en vez de devolver codigos de error. Quien llama al metodo no puede saber que fallo.
- Riesgo: alto. El catch vacio puede esconder un error real sin que nadie se entere.
- Prioridad: alta.

### Smell 5 - Demasiados parametros (Long Parameter List)
- Ubicacion: registrarSolicitud, linea 14.
- Evidencia: recibe seis parametros String: legajo, nombre, email, tipo, prioridad y desc. Ademas, legajo, nombre y email siempre viajan juntos porque son los datos del estudiante.
- Impacto: la firma es dificil de leer y de usar.
- Riesgo: medio. Si se invierten dos parametros, por ejemplo tipo y prioridad, el codigo compila igual y falla recien al ejecutarse.
- Prioridad: media.

### Smell 6 - Metodo largo (Long Method)
- Ubicacion: registrarSolicitud (lineas 14-52) y generarReporte (lineas 180-214).
- Evidencia: registrarSolicitud valida seis datos, crea el estudiante, crea la solicitud, la guarda e imprime, todo en el mismo metodo. generarReporte cuenta por estado, cuenta por tipo, cuenta vencidas, arma el texto y lo imprime.
- Impacto: mezcla varios niveles de abstraccion en un solo lugar. Cuesta entender que hace cada parte.
- Riesgo: medio. Es dificil testear cada parte por separado y es facil romper algo al modificarlo.
- Prioridad: media.

### Smell 7 - Condicionales encadenados (if/else por tipo y estado)
- Ubicacion: cambiarEstado (lineas 82-101), estaVencida (lineas 171-177) y generarReporte (lineas 186-194).
- Evidencia: las reglas de transicion de estados, los plazos de vencimiento y los conteos del reporte estan resueltos con cadenas de if/else que comparan Strings.
- Impacto: viola el principio abierto/cerrado (OCP). Para agregar un tipo, estado o prioridad nuevo hay que modificar codigo que ya funciona en varios metodos.
- Riesgo: medio. Cada vez que se agrega un valor es facil olvidarse de uno de los if y el sistema queda inconsistente.
- Prioridad: media.

### Smell 8 - Envidia de caracteristicas (Feature Envy)
- Ubicacion: estaVencida, lineas 166-178.
- Evidencia: el metodo esta en SolicitudManager pero solo usa datos de Solicitud: getEstado(), getPrioridad() y getFechaCreacion(). Ademas, los plazos 2, 5 y 10 estan escritos directamente en el codigo, sin nombre que explique que son.
- Impacto: la regla de negocio esta en la clase equivocada. Deberia estar en Solicitud, que es la que tiene los datos.
- Riesgo: medio. Si la regla cambia, hay que buscarla en una clase que no es la que uno espera.
- Prioridad: media.