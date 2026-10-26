# bookingplatform-core

Backend de la Plataforma de Reservas de Servicios — CodeFactory UdeA (cursos de Bases de Datos y Arquitectura de Software).

Construido desde cero siguiendo la arquitectura ya definida en el proyecto original (monolito modular, arquitectura limpia), pero con **MySQL en Aiven** como motor de base de datos en lugar de PostgreSQL/Supabase. Supabase se mantiene únicamente como proveedor de autenticación (Supabase Auth / GoTrue).

## Alcance (16 HU, Sprint 1 + Sprint 2)

**Sprint 1 — Identidad y Catálogo**
HU-001, HU-021, HU-002, HU-022, HU-003, HU-004, HU-009, HU-005

**Sprint 2 — Motor de reservas**
HU-006, HU-007, HU-008, HU-010, HU-011, HU-012, HU-013, HU-014

## Módulos

```
identity    -> HU-001, HU-002, HU-003
auth        -> HU-021, HU-022 (RBAC)
catalog     -> HU-004, HU-005, HU-009 (tipos de recurso)
scheduling  -> HU-006, HU-007, HU-008, HU-009 (instancias de recurso), HU-010, HU-011
booking     -> HU-012, HU-013, HU-014
shared      -> errores, seguridad, observabilidad (transversal)
```

Cada módulo sigue 4 capas: `domain` (puro, sin frameworks) → `application` (casos de uso) → `infrastructure` (JPA, Supabase) → `api` (REST). La regla de dependencia hacia adentro se verifica con ArchUnit en `ArchitectureTest`.

## Decisión de arquitectura clave: NN-03 en MySQL

PostgreSQL resuelve "evitar doble agendamiento" con `EXCLUDE USING gist` sobre un rango de tiempo continuo. MySQL no tiene equivalente directo, así que el tiempo se **discretiza** según la granularidad de cada agenda (15/30/60 min) y cada unidad discreta que ocupa una reserva se guarda como una fila en `resource_slot_lock`, con `UNIQUE(resource_id, slot_start)`. InnoDB rechaza, de forma atómica, una segunda inserción para el mismo recurso en el mismo instante — la misma garantía, expresada de otra forma. Ver `ResourceSlotLockConcurrencyIT` para la prueba que lo demuestra.

## Cómo correrlo localmente

```bash
docker compose up -d          # levanta MySQL local
export DB_HOST=localhost DB_PORT=3306 DB_NAME=bookingplatform DB_USERNAME=root DB_PASSWORD=root
export SUPABASE_URL=https://tu-proyecto.supabase.co
export SUPABASE_SECRET_KEY=tu-service-role-key
mvn spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Pruebas

```bash
mvn test                               # unitarias + cobertura JaCoCo
mvn failsafe:integration-test failsafe:verify   # integracion (requiere Docker)
```

## Despliegue

Ver `DEPLOYMENT.md` para la guía paso a paso de Aiven, Docker Hub, Render y los secretos de GitHub.

## Limitaciones conocidas de este primer corte

- `NextAvailableSlotQuery` (la "próxima franja disponible" en HU-005) devuelve vacío por ahora; falta conectar la búsqueda del catálogo con el cálculo real de disponibilidad.
- La verificación de "reservas futuras confirmadas" para una agenda se aproxima por sede (`location_id`), no por agenda exacta, porque `Booking` no referencia `schedule_id` directamente.
- No se corrió `mvn verify` contra un entorno real antes de esta entrega: el sandbox de generación no tiene salida a Maven Central. La primera verificación real es el job `tests` de este mismo pipeline.
- La tabla `resource_maintenance_window` (HU-009, "recurso en mantenimiento no genera disponibilidad") existe en la migración `V3__scheduling.sql`, pero todavía no está conectada al `AvailabilityCalculator` ni tiene su propio repositorio/caso de uso — queda como el siguiente punto a completar en ese módulo.
