# 📋 CHANGELOG - BRIX360 (SuperBrix)

Todas las mejoras y rediseños significativos de la plataforma móvil industrial **BRIX360** (`com.superbrix.ia`) están documentados en este archivo.

---

## [2.0.0] - 2026-10-26 — *Rediseño Total y Arquitectura Industrial de Planta*

### 🔒 1. Seguridad y Configuración
- **Eliminación Total de API Keys Hardcodeadas:** La clave de Google Gemini fue erradicada de la app móvil. Ahora la clasificación con IA se delega a un backend seguro (FastAPI) a través de `SuperBrixApiClient`, impidiendo la extracción de credenciales mediante ingeniería inversa del APK.
- **Validación Criptográfica con Hashing SHA-256:**
  - Contraseña de Administrador validada mediante hash SHA-256 (`SecurityUtils.verifyAdminPassword`).
  - PIN de Supervisor para cambio de área validado mediante hash SHA-256 (`SecurityUtils.verifySupervisorPin`).
  - Tolerancia para contraseñas maestras y de transición durante rotación de planta.
- **Endurecimiento de `AndroidManifest.xml`:**
  - `allowBackup="false"` para prevenir extracción de bases de datos mediante depuración USB/ADB.
  - Permisos granulares de producción agregados: `POST_NOTIFICATIONS`, `ACCESS_NETWORK_STATE`, `CAMERA`.
- **Configuración R8 / ProGuard:**
  - `isMinifyEnabled = true` y `isShrinkResources = true` activados en la variante `release`.
  - Archivo `proguard-rules.pro` configurado con reglas para Jetpack Compose, Retrofit 2, OkHttp 3, Gson, WorkManager y AndroidX Biometrics.

---

### 🎨 2. Sistema de Diseño Industrial y Tema Visual
- **Paleta Oficial BRIX360:**
  - Fondos industriales oscuros: Azul petróleo profundo (`#0A192F`), superficies elevadas (`#111F38` y `#1E293B`).
  - Degradado distintivo de marca: Violeta eléctrico (`#7C3AED`) a Cian de alta visibilidad (`#00C2FF`).
  - Acento táctil industrial: Naranja de acción (`#FF8A00`).
  - Estados semánticos de manufactura: Produciendo Verde (`#1DB954`), Interrupción Ámbar (`#F39C12`), Crítico Rojo (`#E63946`).
- **Tipografía Oficial Inter Local:**
  - Archivo de fuente variable `inter_regular.ttf` incorporado localmente en `res/font/`, eliminando dependencias de Google Fonts descargables en plantas sin acceso a internet externo.
  - Tipografía `TimerTextStyle` con dígitos monoespaciados tabulares grandes (36sp) para cronómetros legibles a 1 metro de distancia.
- **Componentes Industriales "Glove-Friendly":**
  - Botones de 56 dp a 64 dp con micro-feedback háptico para interacción fluida con guantes de seguridad (`PrimaryButton`, `SecondaryButton`).
  - `StatusCard` con cronómetro tabular en tiempo real y animación de pulso continuo en estado *Produciendo*.
  - `OperationCard` estructurada en matriz de 2x2 para datos clave: Orden (OP), Máquina, Área y Operación.
  - `EventCard` para línea de tiempo industrial sin emojis, con badges de categoría y duración.
  - Erradicación total de emojis en toda la interfaz de usuario, sustituidos por `Material Icons` profesionales.

---

### 📱 3. Rediseño Completo de las 12 Pantallas
1. **`SplashScreen`:** Presentación sobria con cubo tridimensional animado (fade + scale), eslogan oficial *"Tu aliado en la planta"* y versión de compilación.
2. **`LoginScreen`:** Selector rápido de operario con buscador, autenticación biométrica nativa (huella/rostro), fallback de PIN y acceso administrativo discreto.
3. **`HomeScreen`:** Panel principal con Header de planta, StatusCard en vivo, matriz de operación activa, botón de acción dinámico de 64 dp y confirmación de cierre de labor.
4. **`SeleccionOperacionScreen`:** Stepper numerado (Paso 1: OP, Paso 2: Área, Paso 3: Operación, Paso 4: Máquina) con diálogo de PIN de supervisor validado criptográficamente.
5. **`ReporteNovedadScreen`:** Reconocimiento de voz guiado con animación pulsante, chips de acceso rápido a novedades comunes, adjunto de fotografía con preview y botón fijo inferior.
6. **`AnalizandoScreen`:** Animación industrial del cubo BRIX360 (sin íconos informales), temporizador de timeout de 10 segundos con alternativas de reintento o guardado local offline.
7. **`ResultadoReporteScreen`:** Banner de alerta roja inmediata ante novedades de Salud y Seguridad, chip interactivo de corrección de categoría y barra visual de certeza IA.
8. **`HistorialScreen`:** Métricas OEE (Órdenes, Producción Activa, Tiempo Muerto), filtros de categoría por chips y exportación de bitácora a formato CSV mediante `Intent` nativo.
9. **`DetalleEventoScreen`:** Auditoría y trazabilidad completa del evento (cronología, estado de sincronización, foto adjunta) con función de anulación bajo motivo obligatorio.
10. **`PerfilScreen`:** Ficha técnica del operario (código, área, centro de trabajo), acceso a configuración y confirmación de cierre de sesión con purga del stack de navegación.
11. **`ConfiguracionScreen`:** Monitor de red en tiempo real (WiFi / Ethernet de planta), persistencia real en DataStore (tema oscuro, alertas, dictado), contador de cola pendiente y botón de sincronización forzada.
12. **`AdminScreen`:** Administración de catálogo de operarios (alta, modificación y eliminación con validación de código único y área de manufactura).

---

### 💾 4. Capa de Datos, Almacenamiento Local y Sincronización Offline-First
- **Motor de Almacenamiento SQLite Local (`BrixDatabaseHelper`):**
  - Tablas normalizadas `eventos` y `operarios` con esquema nativo compatible desde Android 7.0 hasta Android 16 sin fricción de versiones de plugins.
  - Columnas de auditoría: `sync_status` (`PENDIENTE`, `ENVIADO`, `ERROR`), `categoria_corregida`, `categoria_original_ia`, `anulado`, `motivo_anulacion`.
- **Sincronización en Segundo Plano con WorkManager (`SyncWorker`):**
  - `SyncWorker` programa ejecuciones periódicas automáticas cada 15 minutos condicionadas a red (`NetworkType.CONNECTED`).
  - Sincronización reactiva inmediata cada vez que se guarda o actualiza un registro.
  - Dualidad de sincronización: intenta transmitir al backend FastAPI y a Google Sheets (Apps Script).
- **Capa Repository:**
  - `EventoRepository`: Encapsula persistencia local, estado pendiente y encolado de sincronización.
  - `OperarioRepository`: Gestiona el catálogo de operarios garantizando consistencia.
- **Notificaciones Industriales Push (`PlantNotificationHelper`):**
  - Canal de alta prioridad `superbrix_plant_alerts` para disparar alarmas inmediatas ante eventos de *Salud y Seguridad (Accidentes)*.

---

### 🧠 5. Clasificador Heurístico de Contingencia (`FallbackClassifier`)
- **Prioridad Absoluta a Salud y Seguridad:** Evalúa primero accidentes, lesiones humanas, derrames químicos o incendios antes que cualquier falla mecánica o interrupción general.
- **Normalización Fonética Robusta:** Normalización NFD para remoción de tildes (á/é/í/ó/ú -> a/e/i/o/u), eliminación de puntuación y coincidencia de palabras completas con expresiones regulares escapadas.
- **Soporte de las 8 Categorías Oficiales:**
  1. *Salud y Seguridad (Accidentes)*
  2. *Alistamiento y Preparación (Setup)*
  3. *Calidad y Aprobación*
  4. *Falla Técnica / Mantenimiento*
  5. *Espera de Materiales / Logística*
  6. *Instrucciones / Coordinación*
  7. *Producción Activa*
  8. *Otros / Novedad Excepcional*

---

### 🧪 6. Pruebas Unitarias Automatizadas
- **`FallbackClassifierTest`:** 10 pruebas unitarias con cobertura exhaustiva de normalización, prioridad de salud/seguridad, expresiones compuestas y fallback predeterminado.
- **`SecurityUtilsTest`:** Pruebas unitarias de cálculo de hash SHA-256 estándar y validación estricta de contraseñas y PINs válidos e inválidos.
- **Estado de Pruebas:** 14/14 pruebas superadas con éxito (`BUILD SUCCESSFUL in 18s`).
