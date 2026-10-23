import { CSV_EXPORT_LIMIT_MESSAGE, CSV_EXPORT_MAX_ROWS, loadAllCsvPages } from './csv-export';

/** Valor aceptado por una celda XLSX: los números conservan su tipo numérico. */
export type XlsxValue = string | number | null | undefined;

/** Límite de filas compartido con la exportación CSV para no degradar memoria. */
export const XLSX_EXPORT_MAX_ROWS = CSV_EXPORT_MAX_ROWS;

/** Mensaje único cuando la consulta filtrada supera el máximo exportable. */
export const XLSX_EXPORT_LIMIT_MESSAGE = CSV_EXPORT_LIMIT_MESSAGE;

/**
 * Carga todas las páginas de una consulta server-side antes de exportar.
 *
 * <p>Reutiliza la paginación de CSV para que el usuario nunca reciba sólo la
 * página visible cuando solicitó el conjunto filtrado completo.</p>
 */
export const loadAllXlsxPages = loadAllCsvPages;

const XML_HEADER = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>';
const MAIN_NAMESPACE = 'http://schemas.openxmlformats.org/spreadsheetml/2006/main';
const RELATIONSHIPS_NAMESPACE = 'http://schemas.openxmlformats.org/package/2006/relationships';
const OFFICE_RELATIONSHIPS_NAMESPACE = 'http://schemas.openxmlformats.org/officeDocument/2006/relationships';

/**
 * Serializa una tabla a un libro XLSX (Office Open XML) de una sola hoja.
 *
 * <p>El ZIP se genera sin compresión y con fecha fija para que la salida sea
 * determinista y verificable en pruebas. Los textos se escriben como
 * {@code inlineStr}: una celda de texto no se evalúa como fórmula, por lo que
 * valores que comienzan con {@code =}, {@code +}, {@code -} o {@code @} quedan
 * inertes sin alterar el dato con apóstrofos. Nunca se emite el elemento
 * {@code <f>}.</p>
 *
 * @param sheetName nombre visible de la hoja; se sanea según las reglas de Excel
 * @param headers   encabezados en el orden en que deben aparecer
 * @param rows      filas con valores alineados por posición con los encabezados
 * @returns bytes del archivo .xlsx
 */
export function serializeXlsx(sheetName: string, headers: string[], rows: XlsxValue[][]): Uint8Array {
  const sheet = buildSheetXml(headers, rows);
  const entries: [string, string][] = [
    ['[Content_Types].xml', buildContentTypesXml()],
    ['_rels/.rels', buildRootRelationshipsXml()],
    ['xl/workbook.xml', buildWorkbookXml(sanitizeSheetName(sheetName))],
    ['xl/_rels/workbook.xml.rels', buildWorkbookRelationshipsXml()],
    ['xl/worksheets/sheet1.xml', sheet],
  ];
  return buildZip(entries);
}

/**
 * Descarga un libro XLSX previamente serializado.
 *
 * @param filename nombre de archivo sugerido, incluida la extensión .xlsx
 * @param content  bytes generados por {@link serializeXlsx}
 */
export function downloadXlsx(filename: string, content: Uint8Array): void {
  const buffer = content.buffer.slice(content.byteOffset, content.byteOffset + content.byteLength) as ArrayBuffer;
  const blob = new Blob([buffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  // Se revoca en el siguiente ciclo para que Firefox/Safari consuman el blob.
  setTimeout(() => URL.revokeObjectURL(url), 0);
}

/** Aplica las restricciones de nombre de hoja de Excel. */
function sanitizeSheetName(value: string): string {
  const cleaned = (value ?? '').replace(/[[\]:*?/\\]/g, ' ').trim().slice(0, 31).trim();
  return cleaned || 'Datos';
}

function buildSheetXml(headers: string[], rows: XlsxValue[][]): string {
  const allRows = [headers as XlsxValue[], ...rows];
  const rowXml = allRows
    .map((row, rowIndex) => {
      const cells = row
        .slice(0, headers.length)
        .map((value, columnIndex) => buildCellXml(columnIndex, rowIndex, value))
        .join('');
      return `<row r="${rowIndex + 1}">${cells}</row>`;
    })
    .join('');
  return XML_HEADER
    + `<worksheet xmlns="${MAIN_NAMESPACE}"><sheetData>${rowXml}</sheetData></worksheet>`;
}

function buildCellXml(columnIndex: number, rowIndex: number, value: XlsxValue): string {
  if (value === null || value === undefined || value === '') return '';
  const reference = `${columnName(columnIndex)}${rowIndex + 1}`;
  if (typeof value === 'number' && Number.isFinite(value)) {
    return `<c r="${reference}"><v>${value}</v></c>`;
  }
  return `<c r="${reference}" t="inlineStr"><is><t xml:space="preserve">${escapeXml(String(value))}</t></is></c>`;
}

/** Convierte el índice base 0 en letras de columna (A, B, ..., AA). */
function columnName(index: number): string {
  let remaining = index;
  let name = '';
  do {
    name = String.fromCharCode(65 + (remaining % 26)) + name;
    remaining = Math.floor(remaining / 26) - 1;
  } while (remaining >= 0);
  return name;
}

const XML_ESCAPES: Record<string, string> = {
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;',
};

/**
 * Escapa XML y descarta caracteres de control no permitidos por el formato.
 * No se usa una expresión regular con controles para evitar falsos positivos
 * de análisis estático y mantener explícitas las excepciones válidas.
 */
function escapeXml(value: string): string {
  let result = '';
  for (const char of value) {
    const code = char.codePointAt(0) ?? 0;
    const permitido = code === 0x09 || code === 0x0a || code === 0x0d
      || (code >= 0x20 && code <= 0xd7ff)
      || (code >= 0xe000 && code <= 0xfffd)
      || code >= 0x10000;
    if (!permitido) continue;
    result += XML_ESCAPES[char] ?? char;
  }
  return result;
}

function buildContentTypesXml(): string {
  return XML_HEADER
    + '<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">'
    + '<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>'
    + '<Default Extension="xml" ContentType="application/xml"/>'
    + '<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>'
    + '<Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>'
    + '</Types>';
}

function buildRootRelationshipsXml(): string {
  return XML_HEADER
    + `<Relationships xmlns="${RELATIONSHIPS_NAMESPACE}">`
    + `<Relationship Id="rId1" Type="${OFFICE_RELATIONSHIPS_NAMESPACE}/officeDocument" Target="xl/workbook.xml"/>`
    + '</Relationships>';
}

function buildWorkbookXml(sheetName: string): string {
  return XML_HEADER
    + `<workbook xmlns="${MAIN_NAMESPACE}" xmlns:r="${OFFICE_RELATIONSHIPS_NAMESPACE}">`
    + `<sheets><sheet name="${escapeXml(sheetName)}" sheetId="1" r:id="rId1"/></sheets>`
    + '</workbook>';
}

function buildWorkbookRelationshipsXml(): string {
  return XML_HEADER
    + `<Relationships xmlns="${RELATIONSHIPS_NAMESPACE}">`
    + `<Relationship Id="rId1" Type="${OFFICE_RELATIONSHIPS_NAMESPACE}/worksheet" Target="worksheets/sheet1.xml"/>`
    + '</Relationships>';
}

/** Entrada de un ZIP sin compresión. */
interface ZipEntry {
  name: string;
  data: Uint8Array;
  crc: number;
}

function buildZip(entries: [string, string][]): Uint8Array {
  const encoder = new TextEncoder();
  const prepared: ZipEntry[] = entries.map(([name, content]) => {
    const data = encoder.encode(content);
    return { name, data, crc: crc32(data) };
  });

  let totalSize = 22;
  for (const entry of prepared) {
    totalSize += 30 + encoder.encode(entry.name).length + entry.data.length;
    totalSize += 46 + encoder.encode(entry.name).length;
  }

  const output = new Uint8Array(totalSize);
  const view = new DataView(output.buffer);
  let offset = 0;
  const centralDirectory: number[] = [];

  for (const entry of prepared) {
    const nameBytes = encoder.encode(entry.name);
    const localOffset = offset;

    view.setUint32(offset, 0x04034b50, true);
    view.setUint16(offset + 4, 20, true);
    view.setUint16(offset + 6, 0x0800, true);
    view.setUint16(offset + 8, 0, true);
    view.setUint16(offset + 10, 0, true);
    view.setUint16(offset + 12, 0x0021, true);
    view.setUint32(offset + 14, entry.crc, true);
    view.setUint32(offset + 18, entry.data.length, true);
    view.setUint32(offset + 22, entry.data.length, true);
    view.setUint16(offset + 26, nameBytes.length, true);
    view.setUint16(offset + 28, 0, true);
    output.set(nameBytes, offset + 30);
    output.set(entry.data, offset + 30 + nameBytes.length);
    offset += 30 + nameBytes.length + entry.data.length;

    centralDirectory.push(localOffset);
  }

  const centralStart = offset;
  prepared.forEach((entry, index) => {
    const nameBytes = encoder.encode(entry.name);
    view.setUint32(offset, 0x02014b50, true);
    view.setUint16(offset + 4, 20, true);
    view.setUint16(offset + 6, 20, true);
    view.setUint16(offset + 8, 0x0800, true);
    view.setUint16(offset + 10, 0, true);
    view.setUint16(offset + 12, 0, true);
    view.setUint16(offset + 14, 0x0021, true);
    view.setUint32(offset + 16, entry.crc, true);
    view.setUint32(offset + 20, entry.data.length, true);
    view.setUint32(offset + 24, entry.data.length, true);
    view.setUint16(offset + 28, nameBytes.length, true);
    view.setUint16(offset + 30, 0, true);
    view.setUint16(offset + 32, 0, true);
    view.setUint16(offset + 34, 0, true);
    view.setUint16(offset + 36, 0, true);
    view.setUint32(offset + 38, 0, true);
    view.setUint32(offset + 42, centralDirectory[index], true);
    output.set(nameBytes, offset + 46);
    offset += 46 + nameBytes.length;
  });

  const centralSize = offset - centralStart;
  view.setUint32(offset, 0x06054b50, true);
  view.setUint16(offset + 4, 0, true);
  view.setUint16(offset + 6, 0, true);
  view.setUint16(offset + 8, prepared.length, true);
  view.setUint16(offset + 10, prepared.length, true);
  view.setUint32(offset + 12, centralSize, true);
  view.setUint32(offset + 16, centralStart, true);
  view.setUint16(offset + 20, 0, true);

  return output;
}

let crcTable: Uint32Array | null = null;

/** Calcula CRC-32 (polinomio 0xEDB88320) para las entradas del ZIP. */
function crc32(data: Uint8Array): number {
  if (!crcTable) {
    crcTable = new Uint32Array(256);
    for (let index = 0; index < 256; index++) {
      let value = index;
      for (let bit = 0; bit < 8; bit++) {
        value = value & 1 ? 0xEDB88320 ^ (value >>> 1) : value >>> 1;
      }
      crcTable[index] = value >>> 0;
    }
  }
  let crc = 0xFFFFFFFF;
  for (const byte of data) {
    crc = crcTable[(crc ^ byte) & 0xFF] ^ (crc >>> 8);
  }
  return (crc ^ 0xFFFFFFFF) >>> 0;
}
