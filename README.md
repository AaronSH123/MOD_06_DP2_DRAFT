# MOD-06 «Reportes, indicadores e IA»

## 1. Qué es

Módulo del Sistema integrado de gestión, monitoreo y analítica de las áreas verdes del campus PUCP (1INF47, Grupo 6). En la interfaz aparece como «Reportes e indicadores». Atiende:

- **RF-REP-002:** cumplimiento del corte de césped.
- **RF-REP-004:** distribución del personal por cuadrilla, actividad, espacio y jornada.
- **RF-REP-006:** atenciones por espacio y sector.
- **RF-IA-001:** análisis con IA de los informes de proveedores tercerizados (con validación humana).

## 2. Stack

- Back-end: Django 5.2 + Django REST Framework + drf-spectacular (OpenAPI).
- Front-end: React + TypeScript con Vite.
- IA: Gemini (Google AI Studio) con el SDK `google-genai`.
- Datos: simulados en `mock-data/` (JSON) hasta que exista la base de datos PostgreSQL/PostGIS.

## 3. Cómo levantarlo (Windows)

Requisitos: Python 3.10 o superior y Node.js 20.19+ o 22.12+.

### Back-end

```powershell
cd backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
copy .env.example .env      # luego edita DJANGO_SECRET_KEY y GEMINI_API_KEY
python manage.py runserver
```

Pruebas: `pytest` (desde `backend/` con el entorno activo).

### Front-end

```powershell
cd frontend
npm install
copy .env.example .env
npm run dev
```

Vite reenvía `/api/v1` a Django en `http://localhost:8000` y `/api/tercerizados` a Spring Boot en `http://localhost:8080`; iniciar el servicio del módulo que se quiera usar.

## 4. Cambio de fuente de datos

Cambia `DATA_SOURCE` en `backend/.env` (`mock` o `db`). Es la única variable que hay que tocar.

## 5. Documentación de la API

`http://localhost:8000/api/docs/` (esquema OpenAPI en `http://localhost:8000/api/schema/`).

## 6. Pendientes

- Autenticación y roles (MOD-01).
- GeoDjango y PostGIS: aplazados porque ninguno de los cuatro requisitos de este corte usa geometrías y en modo `mock` no hay base de datos.
- Modelos de DefinicionIndicador, ReporteGenerado y SugerenciaIA.
- Ampliación de TipoSugerenciaIA.
- Librería de mapas e iconos.
- Exportación (RF-REP-009).

## MOD-05 · Atenciones tercerizadas (Spring Boot)

La implementación de RF-SER-003/004/005/006/011 está en **backend-mod05/**: Java 21, Spring Boot, Spring Security, JDBC, PostgreSQL y Flyway. La app Django anterior de MOD-05 fue retirada; no se migran sus datos ni usuarios. Crear una base nueva para Spring.

- MOD-05: puerto 8080, `/api/tercerizados/`.
- MOD-06: continúa en Django (`backend/`), puerto 8000, `/api/v1/`.
- Frontend: Vite reenvía cada ruta al servicio correspondiente. Los contratos de la API de MOD-05 se conservan, incluidos `/me/`, filtros y nombres descriptivos.

[Instalación, precarga, API, pruebas y TODO de MOD-05](backend-mod05/README.md).
