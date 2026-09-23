/**
 * ==============================================================================
 * SUPERBRIX-IA : Google Apps Script
 * Endpoint de sincronización y persistencia para paradas y eventos de producción.
 * ==============================================================================
 */

const NOMBRE_HOJA = "SuperBrixIA - Eventos Operativos";

/**
 * Handler HTTP POST: Recibe datos enviados por la app móvil o el backend FastAPI
 * y los añade como una nueva fila en la hoja de cálculo.
 */
function doPost(e) {
  try {
    const ss = SpreadsheetApp.getActiveSpreadsheet();
    let sheet = ss.getSheetByName(NOMBRE_HOJA);
    
    if (!sheet) {
      sheet = inicializarHoja(ss);
    }
    
    const postData = JSON.parse(e.postData.contents);
    
    const id = postData.id || "EVT-" + Utilities.getUuid().substring(0, 6).toUpperCase();
    const fecha = postData.fecha || "";
    const horaInicio = postData.hora_inicio || "";
    const horaFin = postData.hora_fin || "";
    const operario = postData.operario_id || "";
    const ordenProduccion = postData.orden_id || "";
    const maquina = postData.maquina_id || "";
    const tipoEvento = postData.tipo_evento || "";
    const mensajeOriginal = postData.mensaje || "";
    const categoria = postData.categoria || "";
    const descripcion = postData.descripcion || "";
    const confianzaIA = postData.confianza_ia || "";
    const duracionMinutos = postData.duracion_minutos || "";
    const estado = postData.estado || "";

    const fila = [
      id,
      fecha,
      horaInicio,
      horaFin,
      operario,
      ordenProduccion,
      maquina,
      tipoEvento,
      mensajeOriginal,
      categoria,
      descripcion,
      confianzaIA,
      duracionMinutos,
      estado
    ];

    sheet.appendRow(fila);
    const lastRow = sheet.getLastRow();

    // Aplicar color de acento según la categoría
    colorearFilaPorCategoria(sheet, lastRow, categoria);

    return ContentService.createTextOutput(JSON.stringify({
      status: "success",
      message: "Evento registrado en Google Sheets",
      id: id,
      fila: lastRow
    })).setMimeType(ContentService.MimeType.JSON);

  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({
      status: "error",
      message: error.toString()
    })).setMimeType(ContentService.MimeType.JSON);
  }
}

/**
 * Handler HTTP GET: Permite consultar los últimos registros en formato JSON
 */
function doGet(e) {
  try {
    const ss = SpreadsheetApp.getActiveSpreadsheet();
    const sheet = ss.getSheetByName(NOMBRE_HOJA);
    
    if (!sheet) {
      return ContentService.createTextOutput(JSON.stringify({
        status: "empty",
        data: []
      })).setMimeType(ContentService.MimeType.JSON);
    }

    const data = sheet.getDataRange().getValues();
    const headers = data[0];
    const rows = data.slice(1);
    
    const result = rows.map(r => {
      let obj = {};
      headers.forEach((h, i) => {
        obj[h] = r[i];
      });
      return obj;
    });

    return ContentService.createTextOutput(JSON.stringify({
      status: "success",
      total: result.length,
      data: result
    })).setMimeType(ContentService.MimeType.JSON);

  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({
      status: "error",
      message: error.toString()
    })).setMimeType(ContentService.MimeType.JSON);
  }
}

/**
 * Crea y configura la estructura de columnas y formatos iniciales
 */
function inicializarHoja(spreadsheet) {
  const ss = spreadsheet || SpreadsheetApp.getActiveSpreadsheet();
  let sheet = ss.getSheetByName(NOMBRE_HOJA);
  
  if (!sheet) {
    sheet = ss.insertSheet(NOMBRE_HOJA);
  }

  const cabeceras = [
    "ID Evento",
    "Fecha",
    "Hora Inicio",
    "Hora Fin",
    "Operario",
    "Orden",
    "Máquina",
    "Tipo Evento",
    "Mensaje Original",
    "Categoría IA",
    "Descripción",
    "Confianza IA",
    "Duración Minutos",
    "Estado"
  ];

  sheet.getRange(1, 1, 1, cabeceras.length).setValues([cabeceras]);
  
  // Estilo cabecera corporativo SuperBrix
  const headerRange = sheet.getRange(1, 1, 1, cabeceras.length);
  headerRange.setBackground("#0F172A"); // Azul oscuro
  headerRange.setFontColor("#FFFFFF");
  headerRange.setFontWeight("bold");
  headerRange.setHorizontalAlignment("center");
  sheet.setFrozenRows(1);

  // Autoajuste de columnas
  for (let i = 1; i <= cabeceras.length; i++) {
    sheet.autoResizeColumn(i);
  }

  return sheet;
}

/**
 * Aplica color a la celda de Categoría para facilitar la lectura visual en planta
 */
function colorearFilaPorCategoria(sheet, rowIndex, categoria) {
  const catCell = sheet.getRange(rowIndex, 10);
  
  switch(categoria) {
    case "Producción Activa":
      catCell.setBackground("#DCFCE7").setFontColor("#166534"); // Verde claro
      break;
    case "Alistamiento y Preparación (Setup)":
      catCell.setBackground("#E0E7FF").setFontColor("#3730A3"); // Azul índigo
      break;
    case "Espera de Materiales / Logística":
      catCell.setBackground("#FEF3C7").setFontColor("#92400E"); // Ámbar
      break;
    case "Falla Técnica / Mantenimiento":
      catCell.setBackground("#FEE2E2").setFontColor("#991B1B"); // Rojo claro
      break;
    case "Calidad y Aprobación":
      catCell.setBackground("#F3E8FF").setFontColor("#6B21A8"); // Morado
      break;
    case "Instrucciones / Coordinación":
      catCell.setBackground("#E0F2FE").setFontColor("#075985"); // Celeste
      break;
    default:
      catCell.setBackground("#F3F4F6").setFontColor("#1F2937");
  }
}
