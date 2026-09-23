# Estructura y Esquema de Google Sheets - SuperBrix IA

Este documento especifica el modelo de datos tabular utilizado para la persistencia, auditoría y análisis analítico de los eventos de producción reportados a través de la aplicación móvil y el backend de **SuperBrix IA**.

---

## 1. Hoja Principal: `Eventos_Produccion`

Cada registro representa un evento operativo (inicio de ciclo de mecanizado, interrupción, parada programada o ajuste de calidad).

### Diccionario de Columnas

| Columna | Nombre de Cabecera | Tipo de Dato | Ejemplo | Descripción |
| :---: | :--- | :--- | :--- | :--- |
| **A** | `ID Evento` | Texto (Código) | `EVT-1002` | Identificador único del evento. |
| **B** | `Fecha` | Fecha (`YYYY-MM-DD`) | `2026-09-22` | Fecha en la que ocurrió el evento. |
| **C** | `Hora Inicio` | Hora (`hh:mm a`) | `10:32 AM` | Hora en la que se detuvo la máquina o inició la labor. |
| **D** | `Hora Fin` | Hora (`hh:mm a`) | `10:47 AM` | Hora en que finalizó la parada o labor (opcional). |
| **E** | `Operario` | Texto | `Sara` | Nombre o carnet del operario a cargo. |
| **F** | `Orden Producción` | Texto | `OP-001` | Código de la orden de producción activa. |
| **G** | `Máquina` | Texto | `Fresadora 01` | Nombre o código del equipo industrial. |
| **H** | `Tipo Evento` | Texto | `Interrupción` | Tipo: *Inicio de labor*, *Interrupción*, *Fin de labor*. |
| **I** | `Descripción` | Texto Largo | `Paré la fresadora por falta de broca 1/2.` | Texto ingresado por el operario (voz o teclado). |
| **J** | `Categoría IA` | Texto (Validado) | `Espera de Materiales / Logística` | Una de las 6 categorías asignadas por el modelo de IA. |
| **K** | `Duración (min)` | Entero | `15` | Tiempo total transcurrido de la parada en minutos. |
| **L** | `Confianza IA` | Porcentaje | `95%` | Certeza calculada por el modelo de inferencia. |
| **M** | `Timestamp Registro` | Fecha/Hora (`ISO`) | `2026-09-22 10:32:15` | Momento exacto de inserción en la base de datos. |

---

## 2. Validación de Datos (Reglas para Google Sheets)

Para asegurar la integridad de los datos si se edita manualmente en la hoja:
* **Columna J (`Categoría IA`)**: Lista desplegable con los siguientes valores exactos:
  1. `Producción Activa`
  2. `Alistamiento y Preparación (Setup)`
  3. `Espera de Materiales / Logística`
  4. `Falla Técnica / Mantenimiento`
  5. `Calidad y Aprobación`
  6. `Instrucciones / Coordinación`
* **Columna K (`Duración (min)`)**: Número mayor o igual a 0.

---

## 3. Hoja Secundaria: `Dashboard_KPIs`

Hoja de métricas calculadas automáticamente mediante fórmulas de Google Sheets:

### Fórmulas Clave
1. **Total de Tiempo Muerto (minutos)**:
   ```excel
   =SUMIF(Eventos_Produccion!J:J, "<>Producción Activa", Eventos_Produccion!K:K)
   ```
2. **Minutos perdidos por Logística / Espera de Materiales**:
   ```excel
   =SUMIF(Eventos_Produccion!J:J, "Espera de Materiales / Logística", Eventos_Produccion!K:K)
   ```
3. **Minutos perdidos por Fallas Técnicas**:
   ```excel
   =SUMIF(Eventos_Produccion!J:J, "Falla Técnica / Mantenimiento", Eventos_Produccion!K:K)
   ```
4. **Disponibilidad Operativa estimada**:
   ```excel
   =1 - (SUMIF(Eventos_Produccion!J:J, "<>Producción Activa", Eventos_Produccion!K:K) / 480)
   ```
   *(Asumiendo jornada estándar de 8 horas = 480 minutos).*

---

## 4. Instrucciones para Publicar el Webhook (Apps Script)

1. En la hoja de cálculo, ve a **Extensiones > Apps Script**.
2. Pega el código de `google/apps-script/Code.gs`.
3. Haz clic en **Implementar > Nueva implementación**.
4. Selecciona tipo **Aplicación web**.
5. Configura:
   - **Ejecutar como**: *Yo (tu cuenta de Google)*.
   - **Quién tiene acceso**: *Cualquier usuario* (permite llamadas POST desde el backend o la app).
6. Copia la URL generada (`https://script.google.com/macros/s/.../exec`) y agrégala a `GOOGLE_APPS_SCRIPT_URL` en tu archivo `backend/.env`.
