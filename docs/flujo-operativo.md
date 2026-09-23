# Flujo Operativo en Planta - SuperBrix IA

Este documento describe el paso a paso del flujo de trabajo operativo entre el operario en el puesto de mecanizado, el sistema móvil, el backend de IA y el equipo de supervisión.

---

## Diagrama de Secuencia Operativa

```mermaid
sequenceDiagram
    autonumber
    actor Operario as Operario en Máquina
    participant App as App Android (SuperBrix IA)
    participant API as Backend FastAPI
    participant IA as Motor IA (Gemini)
    participant Sheet as Google Sheets / Apps Script
    actor Supervisor as Supervisor / Jefatura

    Note over Operario: Ocurre un evento o parada en máquina
    Operario->>App: Presiona "Reportar Interrupción"
    Operario->>App: Dicta por voz o escribe la novedad
    App->>API: POST /api/v1/ia/clasificar (texto, máquina, OP)
    API->>IA: Prompt de clasificación industrial
    IA-->>API: Categoría, Confianza % y Recomendación
    API-->>App: Resultado del análisis
    App-->>Operario: Muestra pantalla de confirmación con Categoría y Confianza
    Operario->>App: Confirma el reporte ("Registrar Evento")
    App->>API: POST /api/v1/eventos
    API->>Sheet: Webhook POST (inserción en Google Sheets)
    Sheet-->>API: Confirmación (OK)
    API-->>App: Registro exitoso
    Sheet-->>Supervisor: Actualización en tiempo real en Looker Studio
```

---

## Etapas del Proceso

### 1. Detección y Notificación en Máquina
* El operario (ej. Sara en Fresadora 01) detiene el ciclo de mecanizado por una causa ajena o técnica.
* Abre la aplicación **SuperBrix IA** instalada en la tableta o smartphone del puesto de trabajo.

### 2. Captura del Evento
* La pantalla principal muestra la máquina y la Orden de Producción actual (`OP-001`).
* El operario pulsa el botón de interrupción y describe la situación con lenguaje natural habitual:
  > *"Paré la fresadora porque estoy esperando la broca de 1/2 pulgada."*

### 3. Análisis en Tiempo Real por Inteligencia Artificial
* El motor de IA analiza semánticamente el mensaje.
* Determina la categoría más acertada (`Espera de Materiales / Logística`) con un nivel de confianza (`95%`).
* Sugiere una acción preventiva/correctiva para planta.

### 4. Aprobación y Registro
* La app muestra la tarjeta de resultado visual:
  - **Categoría asignada con código de color**.
  - **Porcentaje de certeza**.
  - **Tiempo transcurrido estimado**.
* El operario valida y presiona **Confirmar**.

### 5. Sincronización y Acción de Planta
* El evento queda registrado en la hoja central `Eventos_Produccion`.
* Los supervisores observan el evento en el tablero de control, lo que permite despachar la broca requerida sin demoras innecesarias por traslados a pie.
* Al reanudar la labor, el operario pulsa **"Reanudar Labor"**, cerrando el ciclo de tiempo muerto.
