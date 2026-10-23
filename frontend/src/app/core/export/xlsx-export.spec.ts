import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import {
  XLSX_EXPORT_LIMIT_MESSAGE,
  XLSX_EXPORT_MAX_ROWS,
  downloadXlsx,
  loadAllXlsxPages,
  serializeXlsx,
} from './xlsx-export';
import { CSV_EXPORT_LIMIT_MESSAGE, CSV_EXPORT_MAX_ROWS } from './csv-export';

const decoder = new TextDecoder();

/**
 * Lector mínimo de ZIP store-only: localiza cabeceras locales y extrae el
 * contenido real de cada entrada sin depender de librerías externas.
 */
function readZipEntries(bytes: Uint8Array): Map<string, string> {
  const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
  const entries = new Map<string, string>();
  let offset = 0;
  while (offset + 4 <= bytes.length && view.getUint32(offset, true) === 0x04034b50) {
    const method = view.getUint16(offset + 8, true);
    expect(method).toBe(0);
    const size = view.getUint32(offset + 18, true);
    const nameLength = view.getUint16(offset + 26, true);
    const extraLength = view.getUint16(offset + 28, true);
    const name = decoder.decode(bytes.subarray(offset + 30, offset + 30 + nameLength));
    const dataStart = offset + 30 + nameLength + extraLength;
    entries.set(name, decoder.decode(bytes.subarray(dataStart, dataStart + size)));
    offset = dataStart + size;
  }
  return entries;
}

function sheetOf(bytes: Uint8Array): string {
  const sheet = readZipEntries(bytes).get('xl/worksheets/sheet1.xml');
  expect(sheet).toBeTruthy();
  return sheet!;
}

/** CRC-32 de referencia, independiente del usado por el exportador. */
function crc32(data: Uint8Array): number {
  let crc = 0xffffffff;
  for (const byte of data) {
    crc ^= byte;
    for (let bit = 0; bit < 8; bit++) {
      crc = crc & 1 ? 0xedb88320 ^ (crc >>> 1) : crc >>> 1;
    }
  }
  return (crc ^ 0xffffffff) >>> 0;
}

/** Recorre el directorio central y devuelve entrada, CRC declarado y datos. */
function readCentralDirectory(bytes: Uint8Array): { name: string; crc: number; data: Uint8Array }[] {
  const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
  const eocd = bytes.length - 22;
  expect(view.getUint32(eocd, true)).toBe(0x06054b50);
  const total = view.getUint16(eocd + 10, true);
  let offset = view.getUint32(eocd + 16, true);
  const entries: { name: string; crc: number; data: Uint8Array }[] = [];
  for (let index = 0; index < total; index++) {
    expect(view.getUint32(offset, true)).toBe(0x02014b50);
    const crc = view.getUint32(offset + 16, true);
    const size = view.getUint32(offset + 20, true);
    const nameLength = view.getUint16(offset + 28, true);
    const extraLength = view.getUint16(offset + 30, true);
    const commentLength = view.getUint16(offset + 32, true);
    const localOffset = view.getUint32(offset + 42, true);
    const name = decoder.decode(bytes.subarray(offset + 46, offset + 46 + nameLength));
    const localNameLength = view.getUint16(localOffset + 26, true);
    const localExtraLength = view.getUint16(localOffset + 28, true);
    const dataStart = localOffset + 30 + localNameLength + localExtraLength;
    entries.push({ name, crc, data: bytes.subarray(dataStart, dataStart + size) });
    offset += 46 + nameLength + extraLength + commentLength;
  }
  return entries;
}

describe('serializeXlsx', () => {
  it('genera un ZIP válido con las entradas mínimas de Office Open XML', () => {
    const bytes = serializeXlsx('Activos fijos', ['Pedimento'], [['PED-1']]);
    expect(Array.from(bytes.subarray(0, 4))).toEqual([0x50, 0x4b, 0x03, 0x04]);
    const entries = readZipEntries(bytes);
    expect([...entries.keys()]).toEqual([
      '[Content_Types].xml',
      '_rels/.rels',
      'xl/workbook.xml',
      'xl/_rels/workbook.xml.rels',
      'xl/worksheets/sheet1.xml',
    ]);
    expect(entries.get('[Content_Types].xml')).toContain('spreadsheetml.sheet.main+xml');
    expect(entries.get('xl/workbook.xml')).toContain('name="Activos fijos"');
  });

  it('respeta encabezados, orden de columnas y valores de fila', () => {
    const bytes = serializeXlsx('Datos', ['Fecha', 'Pedimento', 'Cantidad'], [
      ['2026-07-19', 'PED-1', 2.5],
      ['2026-07-20', 'PED-2', 10],
    ]);
    const sheet = sheetOf(bytes);
    expect(sheet).toContain('<row r="1"><c r="A1" t="inlineStr"><is><t xml:space="preserve">Fecha</t></is></c>'
      + '<c r="B1" t="inlineStr"><is><t xml:space="preserve">Pedimento</t></is></c>'
      + '<c r="C1" t="inlineStr"><is><t xml:space="preserve">Cantidad</t></is></c></row>');
    expect(sheet).toContain('<c r="C2"><v>2.5</v></c>');
    expect(sheet).toContain('<c r="C3"><v>10</v></c>');
    expect(sheet.indexOf('Fecha')).toBeLessThan(sheet.indexOf('Pedimento'));
  });

  it('conserva tipo numérico y serializa textos como inlineStr', () => {
    const sheet = sheetOf(serializeXlsx('Hoja', ['Número', 'Texto'], [[1234.5, 'ABC']]));
    expect(sheet).toContain('<c r="A2"><v>1234.5</v></c>');
    expect(sheet).toContain('<c r="B2" t="inlineStr"><is><t xml:space="preserve">ABC</t></is></c>');
  });

  it('escapa caracteres XML sin romper el documento', () => {
    const sheet = sheetOf(serializeXlsx('Hoja', ['Descripción'], [['A & B < C > "D"']]));
    expect(sheet).toContain('A &amp; B &lt; C &gt; &quot;D&quot;');
    expect(sheet).not.toContain('A & B < C');
  });

  it('neutraliza valores con forma de fórmula al escribirlos como texto', () => {
    const values = ['=1+1', '+SUMA(A1)', '-1+1', '@cmd', '=HYPERLINK("http://x")'];
    const sheet = sheetOf(serializeXlsx('Hoja', ['Valor'], values.map((value) => [value])));
    expect(sheet).not.toContain('<f');
    for (const value of values) {
      expect(sheet).toContain(value.replace(/&/g, '&amp;').replace(/"/g, '&quot;'));
    }
    expect(sheet).toContain('t="inlineStr"');
  });

  it('declara en el directorio central los CRC reales de cada entrada', () => {
    const bytes = serializeXlsx('Hoja', ['A', 'B'], [[1, 'Texto'], [null, '&<>"']]);
    const entries = readCentralDirectory(bytes);
    expect(entries.map((entry) => entry.name)).toEqual([
      '[Content_Types].xml',
      '_rels/.rels',
      'xl/workbook.xml',
      'xl/_rels/workbook.xml.rels',
      'xl/worksheets/sheet1.xml',
    ]);
    for (const entry of entries) {
      expect(crc32(entry.data)).toBe(entry.crc);
      expect(entry.data.length).toBeGreaterThan(0);
    }
  });

  it('omite celdas nulas, indefinidas o vacías', () => {
    const sheet = sheetOf(serializeXlsx('Hoja', ['A', 'B', 'C'], [[null, undefined, '']]));
    expect(sheet).toContain('<row r="2"></row>');
  });

  it('sanea el nombre de la hoja y usa Datos cuando queda vacío', () => {
    const largo = readZipEntries(serializeXlsx('H'.repeat(40), ['A'], [['x']]))
      .get('xl/workbook.xml');
    expect(largo).toContain(`name="${'H'.repeat(31)}"`);

    const invalido = readZipEntries(serializeXlsx('a[b]:c*d?e/f\\g', ['A'], [['x']]))
      .get('xl/workbook.xml');
    expect(invalido).toContain('name="a b  c d e f g"');

    const vacio = readZipEntries(serializeXlsx('   ', ['A'], [['x']])).get('xl/workbook.xml');
    expect(vacio).toContain('name="Datos"');
  });
});

describe('downloadXlsx', () => {
  let capturedBlob: Blob | undefined;
  let capturedFilename: string | undefined;
  let createObjectUrlSpy: ReturnType<typeof vi.spyOn>;
  let revokeObjectUrlSpy: ReturnType<typeof vi.spyOn>;
  let anchorClickSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(() => {
    capturedBlob = undefined;
    capturedFilename = undefined;
    createObjectUrlSpy = vi.spyOn(URL, 'createObjectURL').mockImplementation((blob) => {
      capturedBlob = blob as Blob;
      return 'blob:xlsx';
    });
    revokeObjectUrlSpy = vi.spyOn(URL, 'revokeObjectURL').mockImplementation(() => undefined);
    anchorClickSpy = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (this: HTMLAnchorElement) {
      capturedFilename = this.download;
    });
  });

  afterEach(() => {
    createObjectUrlSpy.mockRestore();
    revokeObjectUrlSpy.mockRestore();
    anchorClickSpy.mockRestore();
  });

  it('descarga con el MIME de XLSX, el nombre solicitado y los bytes íntegros', async () => {
    const content = serializeXlsx('Activos fijos', ['Pedimento'], [['PED-1']]);
    downloadXlsx('activos-fijos.xlsx', content);
    await new Promise((resolve) => setTimeout(resolve, 0));

    expect(createObjectUrlSpy).toHaveBeenCalledOnce();
    expect(revokeObjectUrlSpy).toHaveBeenCalledWith('blob:xlsx');
    expect(anchorClickSpy).toHaveBeenCalledOnce();
    expect(capturedFilename).toBe('activos-fijos.xlsx');
    expect(capturedBlob?.type).toBe('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
    const downloaded = new Uint8Array(await capturedBlob!.arrayBuffer());
    expect(Array.from(downloaded)).toEqual(Array.from(content));
  });
});

describe('límites y paginación de exportación', () => {
  it('reutiliza el máximo y el mensaje de CSV', () => {
    expect(XLSX_EXPORT_MAX_ROWS).toBe(CSV_EXPORT_MAX_ROWS);
    expect(XLSX_EXPORT_LIMIT_MESSAGE).toBe(CSV_EXPORT_LIMIT_MESSAGE);
  });

  it('carga todas las páginas filtradas y no sólo la visible', async () => {
    const fetchPage = vi.fn((page: number, pageSize: number) => Promise.resolve({
      items: Array.from({ length: page === 3 ? 50 : pageSize }, (_, index) => ({ id: (page - 1) * pageSize + index })),
      total: 250,
    }));
    const rows = await loadAllXlsxPages(fetchPage);
    expect(fetchPage.mock.calls).toEqual([[1, 100], [2, 100], [3, 100]]);
    expect(rows).toHaveLength(250);
  });

  it('aborta cuando el total supera el máximo exportable', async () => {
    const fetchPage = vi.fn(() => Promise.resolve({ items: [], total: XLSX_EXPORT_MAX_ROWS + 1 }));
    await expect(loadAllXlsxPages(fetchPage)).rejects.toThrow(XLSX_EXPORT_LIMIT_MESSAGE);
    expect(fetchPage).toHaveBeenCalledOnce();
  });
});
