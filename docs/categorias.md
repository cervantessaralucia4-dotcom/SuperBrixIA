# Matriz de Categorías de Eventos e Interrupciones - SuperBrix IA

Para estandarizar el registro y permitir análisis de causas raíz en planta, el sistema clasifica automáticamente cada evento en una de las siguientes **6 categorías maestras**:

---

## 1. Producción Activa
* **Definición**: Tiempo en el que la máquina está operando normalmente en ciclo de transformación (corte, fresado, torneado, soldadura o ensamble) agregando valor al producto.
* **Palabras Clave**: `mecanizado`, `corte`, `torneado`, `fresado`, `ciclo iniciado`, `producción`, `operación continua`.
* **Impacto OEE**: Cuenta como tiempo productivo neto (Disponibilidad y Rendimiento positivos).
* **Acción Operativa**: Monitorear cumplimiento de tiempo estándar por pieza.

---

## 2. Alistamiento y Preparación (Setup)
* **Definición**: Actividades de cambio de referencia, montaje y desmontaje de herramientas, centrado de mordazas, cargue de programas CNC o calibración inicial.
* **Palabras Clave**: `setup`, `montaje`, `calibración`, `ajuste de mordaza`, `cambio de herramienta`, `puesta a punto`, `cero máquina`.
* **Impacto OEE**: Pérdida por disponibilidad planificada. Reducción mediante metodología SMED (*Single-Minute Exchange of Die*).
* **Acción Operativa**: Registrar tiempo de inicio y fin para auditar cumplimiento de tiempos estándar de alistamiento.

---

## 3. Espera de Materiales / Logística
* **Definición**: Parada involuntaria debida a la falta de materia prima, insumos de corte, planos, herramental en almacén, montacargas o retiro de viruta/desecho.
* **Palabras Clave**: `broca`, `inserto`, `material`, `esperando insumo`, `falta materia prima`, `almacén`, `montacargas`, `retirar viruta`.
* **Impacto OEE**: Parada no planificada imputable a la cadena de suministro interna.
* **Acción Operativa**: Notificación inmediata al coordinador de logística de planta y almacén de herramentales.

---

## 4. Falla Técnica / Mantenimiento
* **Definición**: Averías mecánicas, eléctricas, hidráulicas, neumáticas o de software en la máquina herramienta.
* **Palabras Clave**: `falla`, `ruido extraño`, `motor recalentado`, `fuga de aceite`, `presión hidráulica`, `husillo trabado`, `alarma CNC`.
* **Impacto OEE**: Parada no planificada crítica. Afecta directamente el MTBF (*Mean Time Between Failures*).
* **Acción Operativa**: Generación automática de solicitud de mantenimiento correctivo y aislamiento preventivo del equipo.

---

## 5. Calidad y Aprobación
* **Definición**: Pausa en la operación para inspección metrológica, medición de cotas críticas, rugosidad, liberación de primera pieza o corrección de defectos superficiales.
* **Palabras Clave**: `calidad`, `medición`, `revisión de cota`, `micrómetro`, `tolerancia geométrica`, `liberación`, `rechazo`, `inspección`.
* **Impacto OEE**: Pérdida de calidad y disponibilidad.
* **Acción Operativa**: Llamado al inspector de control de calidad asignado al área.

---

## 6. Instrucciones / Coordinación
* **Definición**: Interrupciones para recibir aclaraciones del supervisor, revisión de cambios en la ingeniería de producto, reuniones de seguridad (5 minutos) o relevos de turno.
* **Palabras Clave**: `instrucción`, `supervisor`, `duda sobre plano`, `reunión de turno`, `coordinación`, `seguridad industrial`.
* **Impacto OEE**: Pérdida operativa organizativa.
* **Acción Operativa**: Diligenciamiento de notas de ingeniería y aclaración técnica rápida en puesto de trabajo.
