# Dashboard de Monitoreo y OEE - SuperBrix IA

Este módulo describe la capa de visualización gerencial y operativa de **SuperBrix IA**, diseñada para que jefes de planta, supervisores de producción y líderes de mejora continua analicen en tiempo real las paradas y el rendimiento de las máquinas.

---

## 1. Opciones de Implementación

El sistema admite dos modalidades de dashboard:

### Opción A: Looker Studio (Conexión Directa con Google Sheets)
La forma más ágil y sin mantenimiento de servidor:
1. Abrir [Google Looker Studio](https://lookerstudio.google.com/).
2. Crear un nuevo informe en blanco y seleccionar como conector **Google Sheets**.
3. Escoger la hoja de cálculo de SuperBrix y la pestaña `Eventos_Produccion`.
4. El informe se actualizará automáticamente cada vez que un operario reporte una interrupción desde la app móvil.

### Opción B: Dashboard Web (FastAPI / Streamlit / React)
Consumiendo los endpoints REST expuestos por el backend:
* `GET /api/v1/eventos`: Lista de eventos con filtros por máquina y fecha.
* `GET /salud`: Estado de los servicios de IA y Google Sheets.

---

## 2. Métricas y KPIs Visualizados

El dashboard está estructurado en 4 cuadrantes principales:

```
┌─────────────────────────────────────────────────────────────┐
│                      SUPERBRIX IA                           │
│              MONITOREO DE PARADAS EN TIEMPO REAL            │
├──────────────┬──────────────┬───────────────┬───────────────┤
│ DISPONIBILIDAD│ TIEMPO MUERTO│ TOP PARADA    │ EVENTOS HOY   │
│    87.4%     │    68 min    │ Logística     │      12       │
└──────────────┴──────────────┴───────────────┴───────────────┘
┌─────────────────────────────┬───────────────────────────────┐
│ [Gráfico de Torta]          │ [Gráfico de Barras / Pareto]  │
│ Paradas por Categoría IA    │ Tiempo Muerto por Máquina     │
│ - Espera de Materiales (45%)│ - Fresadora 01: 45 min        │
│ - Falla Técnica (25%)       │ - Torno CNC 02: 15 min        │
│ - Setup (18%)               │ - Rectificadora: 8 min        │
│ - Calidad (12%)             │                               │
├─────────────────────────────┴───────────────────────────────┤
│ [Tabla Detallada con Filtros]                               │
│ Hora | Máquina | OP | Operario | Descripción | Categoría IA │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Guía de Configuración en Looker Studio

1. **Dimensiones**:
   - Dimensión temporal: `Fecha`, `Hora Inicio`
   - Dimensiones de desglose: `Máquina`, `Categoría IA`, `Operario`, `Orden Producción`
2. **Métricas**:
   - `Duración (min)` (Agregación: Suma)
   - `ID Evento` (Agregación: Conteo de registros)
   - `Confianza IA` (Agregación: Promedio)
3. **Filtros interactivos sugeridos**:
   - Selector desplegable por **Máquina** (`Fresadora 01`, `Torno CNC`, etc.).
   - Selector por **Categoría IA**.
   - Selector de rango de fechas.
