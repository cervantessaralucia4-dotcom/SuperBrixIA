# SUPERBRIX-IA 🚀⚙️

Sistema integral para el monitoreo, clasificación con Inteligencia Artificial (Google Gemini) y análisis de tiempos muertos y paradas de producción en planta industrial.

---

## 📁 Estructura del Proyecto

```text
SUPERBRIX-IA/
│
├── android/                        # Aplicación móvil en Kotlin / Jetpack Compose
│   ├── app/
│   ├── gradle/
│   ├── build.gradle.kts
│   └── settings.gradle.kts
│
├── backend/                        # API REST en FastAPI con motor de IA y Google Sheets
│   │
│   ├── app/
│   │   ├── __init__.py
│   │   │
│   │   ├── main.py                 # Punto de entrada de la aplicación FastAPI
│   │   │
│   │   ├── routes/                 # Controladores y endpoints REST
│   │   │   ├── __init__.py
│   │   │   ├── eventos.py          # Gestión y registro de eventos/paradas
│   │   │   ├── ia.py               # Inferencia de IA para descripciones de paradas
│   │   │   └── salud.py            # Endpoint de diagnóstico y salud del servicio
│   │   │
│   │   ├── services/               # Lógica de negocio e integraciones externas
│   │   │   ├── __init__.py
│   │   │   ├── ia_service.py       # Integración con Google Gemini + clasificador de respaldo
│   │   │   └── sheets_service.py   # Persistencia y webhook hacia Google Sheets
│   │   │
│   │   ├── models/                 # Modelos de datos y esquemas Pydantic
│   │   │   ├── __init__.py
│   │   │   └── evento.py           # Esquemas de eventos, categorías y clasificación
│   │   │
│   │   └── config/                 # Configuración y variables de entorno
│   │       ├── __init__.py
│   │       └── settings.py
│   │
│   ├── .env                        # Variables de entorno (API keys, URLs de webhook)
│   ├── .gitignore                  # Reglas de exclusión de Git para Python
│   └── requirements.txt            # Dependencias del backend (FastAPI, Uvicorn, etc.)
│
├── google/                         # Integración con Google Workspace
│   ├── apps-script/
│   │   └── Code.gs                 # Script de Google Apps Script para recibir Webhooks
│   │
│   └── sheets/
│       └── estructura.md           # Diccionario de datos y diseño de la hoja de cálculo
│
├── dashboard/                      # Capa analítica y visualización de KPIs
│   └── README.md                   # Guía de configuración para Looker Studio y dashboards
│
├── docs/                           # Documentación técnica y operativa
│   ├── arquitectura.md             # Diagramas de arquitectura y topología de sistemas
│   ├── categorias.md               # Catálogo de las 6 categorías maestras de paradas
│   └── flujo-operativo.md          # Manual del flujo de captura y sincronización en planta
│
└── README.md                       # Documento principal del repositorio
```

---

## 🛠️ Tecnologías Utilizadas

* **Móvil (Android)**: Kotlin, Jetpack Compose, Material Design 3, Coroutines, StateFlow, Navigation Compose.
* **Backend**: Python 3.10+, FastAPI, Pydantic v2, Uvicorn, HTTPX.
* **Inteligencia Artificial**: Google Gemini API (`gemini-1.5-flash` / `gemini-2.5-flash`) con respaldo heurístico en planta.
* **Persistencia y Datos**: Google Sheets API & Google Apps Script Webhooks.
* **Visualización / BI**: Google Looker Studio.

---

## 🚀 Guía de Inicio Rápido

### 1. Backend (FastAPI)

```bash
# Entrar a la carpeta del backend
cd backend

# Crear y activar entorno virtual
python -m venv .venv
# En Windows (PowerShell):
.venv\Scripts\Activate.ps1
# En Linux/macOS:
source .venv/bin/activate

# Instalar dependencias
pip install -r requirements.txt

# Configurar variables de entorno en backend/.env (opcional para pruebas locales)
# Ejecutar el servidor de desarrollo
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```
La documentación interactiva Swagger estará disponible en: `http://localhost:8000/docs`

### 2. Aplicación Móvil (Android)

1. Abre **Android Studio**.
2. Selecciona **Open** y navega a la carpeta `android/` del proyecto.
3. Espera la sincronización de Gradle (`Sync Project with Gradle Files`).
4. Conecta tu dispositivo Android o inicia un emulador.
5. Ejecuta la aplicación (**Run 'app'** o `Shift + F10`).

---

## 📚 Documentación Adicional

* [Arquitectura del Sistema](docs/arquitectura.md)
* [Matriz de Categorías Industriales](docs/categorias.md)
* [Flujo Operativo en Planta](docs/flujo-operativo.md)
* [Estructura de Google Sheets](google/sheets/estructura.md)
* [Dashboard y KPIs de OEE](dashboard/README.md)
