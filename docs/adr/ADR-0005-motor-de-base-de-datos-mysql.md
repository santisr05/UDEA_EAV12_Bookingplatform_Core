# ADR-0005: Motor de base de datos — MySQL en Aiven en lugar de PostgreSQL/Supabase

## Estado
Aceptado

## Contexto
El proyecto original (Sprint 1) definio su modelo fisico completo sobre PostgreSQL,
usando `EXCLUDE USING gist` sobre un rango de tiempo (`tstzrange`) en
`booking_resource` para resolver NN-03 (evitar doble agendamiento) a nivel de
base de datos. Al reconstruir el backend desde cero bajo una nueva
organizacion de sprints, se eligio MySQL en Aiven como motor de base de
datos, replicando el patron ya usado en el Laboratorio 2 del curso de
Arquitectura de Software.

## Decision
Usar MySQL 8 (Aiven, plan gratuito) para toda la persistencia de negocio.
Supabase se mantiene exclusivamente como proveedor de autenticacion
(Supabase Auth / GoTrue), sin relacion fisica con la base de datos de
negocio: el `user_id` que devuelve Supabase se guarda como un simple
`CHAR(36)` en MySQL, sin clave foranea entre motores (imposible entre
motores distintos), tratado como referencia logica.

Como MySQL no tiene un equivalente directo al `EXCLUDE` de PostgreSQL, NN-03
se resuelve discretizando el tiempo segun la granularidad de cada agenda
(15/30/60 minutos) y usando una restriccion `UNIQUE(resource_id, slot_start)`
sobre una tabla `resource_slot_lock`: cada unidad discreta que ocupa una
reserva es una fila propia, e InnoDB rechaza de forma atomica una segunda
insercion para el mismo recurso en el mismo instante, incluso bajo
transacciones concurrentes.

## Alternativas consideradas
- **Mantener PostgreSQL/Supabase**: descartada por decision explicita del
  equipo de alinear el stack de este proyecto con el que ya se domina del
  Laboratorio 2 (MySQL en Aiven), para reducir el numero de herramientas
  distintas que el equipo debe operar en paralelo durante el semestre.
- **MySQL con verificacion de solapes solo a nivel de aplicacion** (sin
  restriccion de base de datos): descartada porque reintroduce exactamente
  el riesgo de condicion de carrera que NN-03 busca eliminar; dos
  transacciones concurrentes podrian leer "disponible" antes de que
  cualquiera de las dos escriba.

## Consecuencias
- Se gana consistencia con el stack ya usado en otro curso del mismo
  semestre, a costa de una garantia de concurrencia mas artesanal
  (discretizacion por granularidad) en lugar de una funcion nativa del
  motor para rangos continuos.
- La discretizacion ata la finura de la proteccion a la granularidad de la
  agenda: una agenda de 60 minutos no detecta un solape de 30 minutos
  dentro del mismo slot de una manera tan precisa como lo haria un rango
  continuo — en la practica no es un problema porque las reservas siempre
  se alinean a la granularidad de su propia agenda.
- `countFutureConfirmedBookingsForSchedule` (usado por HU-006/HU-007) queda
  aproximado por sede en lugar de por agenda exacta, porque `Booking` no
  referencia `schedule_id` directamente; se documenta como limitacion
  conocida en el README.
