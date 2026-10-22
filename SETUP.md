# Cómo correr el proyecto en local (macOS y Windows)

El sistema tiene tres piezas. Cada una se ejecuta en su propia terminal:

| Pieza | Carpeta | Puerto | Para qué |
|---|---|---|---|
| Base de datos PostgreSQL | — | 5432 | Datos de MOD-05 |
| Backend MOD-05 y MOD-06 (Spring Boot) | `backend/` | 8000 | Servicios tercerizados (`/api/tercerizados/`) y reportes, indicadores e IA (`/api/v1/`) |
| Frontend (Vite + React) | `frontend/` | 5173 | Interfaz. Reenvía cada ruta de la API a su backend |

Los pasos 1 y 2 de cada sistema operativo se hacen **una sola vez**. Para el día a día, ve a [Uso diario](#uso-diario).

---

## Requisitos

| Herramienta | Versión | Para |
|---|---|---|
| JDK | **21** (exactamente 21; Java 8 u otro no sirve) | MOD-05 |
| PostgreSQL | 14 o posterior | MOD-05 |
| Node.js | 20.19+ o 22.12+ | Frontend |

**No hace falta instalar Maven.** `backend/mvnw` (macOS) y `mvnw.cmd` (Windows) descargan Maven 3.9.11 la primera vez en `~/.m2/wrapper` y verifican su SHA-512.

---

## macOS (Apple Silicon o Intel)

### 1. Instalar herramientas

Con [Homebrew](https://brew.sh):

```sh
brew install openjdk@21 postgresql@16 node python@3.12
```

**Registrar Java 21 en macOS.** Homebrew no lo registra; sin este paso, `java_home` puede devolver otro Java, por ejemplo un plugin viejo de Java 8:

```sh
sudo ln -sfn "$(brew --prefix openjdk@21)/libexec/openjdk.jdk" /Library/Java/JavaVirtualMachines/openjdk-21.jdk
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zprofile
source ~/.zprofile
/usr/libexec/java_home -V     # debe listar una línea con 21
```

### 2. Crear la base de datos

```sh
brew services start postgresql@16
export PATH="$(brew --prefix postgresql@16)/bin:$PATH"   # si no encuentra createuser
createuser -P areas_verdes            # pide una contraseña: anótala
createdb -O areas_verdes areas_verdes_mod05
```

Si prefieres Docker, en lugar de lo anterior:

```sh
docker run -d --name pg-mod05 -p 5432:5432 \
  -e POSTGRES_USER=areas_verdes -e POSTGRES_PASSWORD=tu_clave \
  -e POSTGRES_DB=areas_verdes_mod05 postgres:16
```

No hay que crear tablas: Flyway las crea en el primer arranque de Spring.

### 3. Backend MOD-05 (Spring Boot)

```sh
cd backend
cp .env.example .env
open -e .env
```

Completa estas dos variables, sin comillas:

- `MOD05_DB_PASSWORD=`: la contraseña del paso 2.
- `MOD05_DEMO_PASSWORD=`: la clave de los usuarios de prueba, de al menos 12 caracteres.

Primer arranque, que además precarga catálogos y usuarios de prueba:

```sh
./mvnw -version          # debe decir "Java version: 21…"
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.arguments=--mod05.seed=true
```

El backend está listo cuando `http://localhost:8000/api/docs` abre Swagger.

### 4. MOD-06

Va en el mismo back-end del paso 3. Con `DATA_SOURCE=mock` funciona tal cual; `GEMINI_API_KEY` solo hace falta para la IA.

### 5. Frontend

```sh
cd frontend
npm install
cp .env.example .env
npm run dev
```

---

## Windows 10/11 (PowerShell)

### 1. Instalar herramientas

Con `winget`, en PowerShell:

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
winget install PostgreSQL.PostgreSQL.16
winget install OpenJS.NodeJS.LTS
winget install Python.Python.3.12
```

Sin `winget`, usa los instaladores de adoptium.net (Temurin 21, Windows x64 `.msi`), postgresql.org, nodejs.org y python.org. Durante la instalación de PostgreSQL te pide una contraseña para el superusuario `postgres`: anótala.

**Configurar `JAVA_HOME`.** Abre una PowerShell nueva y ejecuta:

```powershell
$jdk = (Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -Filter "jdk-21*")[0].FullName
setx JAVA_HOME "$jdk"
```

Cierra y vuelve a abrir PowerShell, y comprueba:

```powershell
echo $env:JAVA_HOME     # debe terminar en jdk-21…
java -version           # debe decir 21
```

**Permitir los scripts del entorno de Python.** Hazlo una vez:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

### 2. Crear la base de datos

El servicio de PostgreSQL queda iniciado tras instalarlo. Ajusta `16` si instalaste otra versión:

```powershell
$pg = "C:\Program Files\PostgreSQL\16\bin"
& "$pg\psql.exe" -U postgres -c "CREATE ROLE areas_verdes LOGIN PASSWORD 'tu_clave';"
& "$pg\createdb.exe" -U postgres -O areas_verdes areas_verdes_mod05
```

Ambos comandos piden la contraseña de `postgres`. Cambia `tu_clave` por la que quieras y anótala.

### 3. Backend MOD-05 (Spring Boot)

```powershell
cd backend
copy .env.example .env
notepad .env
```

Completa `MOD05_DB_PASSWORD=` (la contraseña de `areas_verdes`) y `MOD05_DEMO_PASSWORD=` (al menos 12 caracteres), sin comillas.

Primer arranque, con precarga:

```powershell
.\mvnw.cmd -version
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev" "-Dspring-boot.run.arguments=--mod05.seed=true"
```

En PowerShell, los argumentos `-D…` van entre comillas. Si `mvnw.cmd` falla, instala Maven 3.9 (maven.apache.org), agrégalo al `PATH` y usa `mvn` en lugar de `.\mvnw.cmd`.

### 4. MOD-06

Va en el mismo back-end del paso 3. Con `DATA_SOURCE=mock` funciona tal cual; `GEMINI_API_KEY` solo hace falta para la IA.

### 5. Frontend

```powershell
cd frontend
npm install
copy .env.example .env
npm run dev
```

---

## Uso diario

Con todo instalado, abre una terminal por pieza y ejecuta lo siguiente.

| Pieza | macOS | Windows |
|---|---|---|
| PostgreSQL | `brew services start postgresql@16` (o `docker start pg-mod05`) | Ya corre como servicio de Windows |
| Backend (MOD-05 y MOD-06) | `cd backend && ./mvnw spring-boot:run` | `cd backend; .\mvnw.cmd spring-boot:run` |
| Frontend | `cd frontend && npm run dev` | `cd frontend; npm run dev` |

Repetir la precarga (`--mod05.seed=true`) no rompe nada, pero no hace falta.

### Ingresar

- **MOD-05:** `http://localhost:5173/servicios-tercerizados/ingreso`, con `mod05_jefe`, `mod05_ingeniero` o `mod05_practicante` y la clave de `MOD05_DEMO_PASSWORD`. Para cambiar de rol, usa "Cerrar sesión".
- **MOD-06:** `http://localhost:5173/reportes-indicadores/reportes`.

### Pruebas

```sh
cd frontend && npm test && npm run lint && npm run build
cd backend && ./mvnw test                 # MOD-05 y MOD-06; necesita una base terminada en _test; ver backend/README-MOD05.md
```

---

## Problemas frecuentes

| Síntoma | Causa y solución |
|---|---|
| `The JAVA_HOME environment variable is not defined correctly` | `JAVA_HOME` apunta a una carpeta que no existe. Vuelve a configurarlo como en el paso 1. |
| `class file version 61.0 … only recognizes class file versions up to 52.0` | Maven está usando Java 8. Revisa `./mvnw -version`: debe decir 21. En macOS, registra el JDK de Homebrew (paso 1). |
| `java -version` dice 21 pero `java_home -v 21` devuelve otra ruta | El JDK de Homebrew no está registrado en macOS. Usa el comando `sudo ln -sfn …` del paso 1. |
| `Connection refused` o `password authentication failed` al arrancar Spring | PostgreSQL no está corriendo, o `MOD05_DB_PASSWORD` no coincide. |
| El ingreso de MOD-05 dice credenciales incorrectas | Hay credenciales viejas en el navegador, o la clave no es la de `MOD05_DEMO_PASSWORD`. Cierra sesión o usa una ventana privada. |
| Reportes (MOD-06) no carga | El back-end no está levantado en el puerto 8000. |
| `Activate.ps1 cannot be loaded` (Windows) | Ejecuta `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`. |
| Puerto ocupado (8000 o 5173) | Cierra la otra instancia, o cambia `PORT` en `backend/.env`. Si cambias un puerto, ajusta también el proxy en `frontend/vite.config.ts`. |

Los archivos `.env` y `private-media/`, donde se guardan los documentos subidos, están en `.gitignore`: las contraseñas no se suben al repositorio.
