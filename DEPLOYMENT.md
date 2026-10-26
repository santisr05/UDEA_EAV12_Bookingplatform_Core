# Guía de despliegue

Pasos en orden. Cada servicio que crees te da un dato que necesitas en el siguiente paso — ve guardándolos.

## 1. Nombre del repositorio

Sugerido: **`bookingplatform-core`** (coincide con el `artifactId` del `pom.xml`, con el nombre de la imagen Docker y con el `finalName` del JAR — así no hay que renombrar nada al copiar este contenido dentro del repo). Crea el repo en GitHub con `main` como rama por defecto y copia todo el contenido de este `.zip` en su raíz.

## 2. Base de datos — Aiven for MySQL

1. Crea una cuenta en [aiven.io](https://aiven.io) (da créditos de prueba sin pedir tarjeta).
2. Crea un servicio **MySQL** (plan gratuito/Hobbyist).
3. En la pestaña de conexión obtén: `Host`, `Port`, `User`, `Password`, y el nombre de la base (`defaultdb` por defecto).
4. Guarda esos 4 valores — los necesitas en el paso 5.

## 3. Autenticación — Supabase (solo Auth, no la base de datos)

1. Si no tienes un proyecto de Supabase, créalo en [supabase.com](https://supabase.com).
2. En `Settings → API` copia: `Project URL` y la `service_role key` (no la `anon key` — esta app necesita permisos de administrador para crear usuarios).
3. El JWKS para validar tokens está en `https://TU-PROYECTO.supabase.co/auth/v1/.well-known/jwks.json` (se arma solo a partir de `SUPABASE_URL`, no necesitas copiarlo aparte).

## 4. Imagen — Docker Hub

1. Crea una cuenta en [hub.docker.com](https://hub.docker.com) si no tienes.
2. Crea un repositorio público llamado `bookingplatform`.
3. En `Account Settings → Security` genera un **access token** con permisos de lectura y escritura (no uses tu contraseña real).

## 5. Variables y secretos en GitHub

En el repo: `Settings → Secrets and variables → Actions`.

**Secrets** (sensibles, no se ven después de guardarlos):

| Nombre | Valor |
|---|---|
| `DOCKER_USERNAME` | Tu usuario de Docker Hub |
| `DOCKER_PASSWORD` | El access token generado en el paso 4 |
| `SONAR_TOKEN` | Token generado en SonarCloud (paso 7) |
| `RENDER_DEPLOY_HOOK_URL` | El deploy hook del servicio de Render (paso 6, se obtiene después de crearlo) |

**Variables** (no sensibles, visibles):

| Nombre | Valor |
|---|---|
| `RENDER_SERVICE_URL` | La URL pública del servicio en Render (paso 6, también se obtiene después de crearlo) |

## 6. Despliegue — Render

1. Crea una cuenta en [render.com](https://render.com).
2. `New → Web Service → Deploy an existing image from a registry`.
3. Imagen: `docker.io/TU_USUARIO_DOCKERHUB/bookingplatform:latest`.
4. Plan: **Free**. Región: la más cercana a tu base de Aiven.
5. Variables de entorno del servicio (`Environment`):

| Variable | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `cloud` |
| `DB_HOST` | Host de Aiven |
| `DB_PORT` | Puerto de Aiven |
| `DB_NAME` | `defaultdb` (o el nombre que hayas usado) |
| `DB_USERNAME` | Usuario de Aiven |
| `DB_PASSWORD` | Contraseña de Aiven |
| `SUPABASE_URL` | URL de tu proyecto Supabase |
| `SUPABASE_SECRET_KEY` | La `service_role key` de Supabase |
| `APP_BASE_URL` | La URL pública que Render te asigne (puedes dejarla provisional y corregirla después del primer deploy) |
| `ALLOWED_ORIGINS` | `*` para pruebas, o el dominio real del frontend cuando exista |

6. Health Check Path: `/actuator/health/readiness`.
7. Guarda y espera el primer deploy manual (antes de que el pipeline automático exista).
8. Copia la **URL pública** del servicio → variable `RENDER_SERVICE_URL` en GitHub (paso 5).
9. En `Settings → Deploy Hook` copia la URL del hook → secreto `RENDER_DEPLOY_HOOK_URL` en GitHub (paso 5).

## 7. Calidad — SonarCloud + Coveralls

1. En [sonarcloud.io](https://sonarcloud.io), importa el repositorio de GitHub.
2. En `Administration → Analysis Method`, **desactiva el análisis automático** (lo dispara el pipeline, no Sonar).
3. Copia el `project key` y la `organization` y reemplázalos en `pom.xml`:
   ```xml
   <sonar.projectKey>TU_PROJECT_KEY</sonar.projectKey>
   <sonar.organization>TU_ORGANIZATION</sonar.organization>
   ```
4. Genera un token (`My Account → Security`) → secreto `SONAR_TOKEN` en GitHub (paso 5).
5. En [coveralls.io](https://coveralls.io), entra con tu cuenta de GitHub y activa el repositorio (público, no necesita token aparte).

## 8. Primer push

Con todos los secretos y variables cargados:

```bash
git add .
git commit -m "first commit: bookingplatform-core"
git push origin main
```

Esto dispara el pipeline completo: pruebas → integración → Sonar → build → imagen Docker → despliegue en Render.

## Notas sobre los planes gratuitos

- **Aiven free tier**: límite de conexiones concurrentes bajo — por eso el pool de Hikari queda fijo en 5/1 en `application-cloud.yml`.
- **Render free tier**: el servicio se duerme tras ~15 min sin tráfico; la primera petición después de eso tarda cerca de un minuto (por eso el job `deploy` reintenta el smoke test varias veces).
