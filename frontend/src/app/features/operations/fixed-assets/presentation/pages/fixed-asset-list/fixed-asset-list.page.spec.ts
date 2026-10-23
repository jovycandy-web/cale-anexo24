import { Component, EventEmitter, Output, WritableSignal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { NotificationService } from '@core/notifications/notification.service';
import { SavedCriteria } from '@features/saved-queries/domain/saved-query.model';
import { OperationPeriod, OperationPeriodFilterComponent } from '@features/operations/shared/presentation/operation-period-filter/operation-period-filter.component';
import { ColumnFilterState } from '@core/table-filters/column-filter.model';
import { FixedAsset, FixedAssetSearchCriteria } from '@features/operations/fixed-assets/domain/models/fixed-asset.model';
import { SearchFixedAssetsUseCase } from '@features/operations/fixed-assets/application/use-cases/search-fixed-assets.use-case';
import { FixedAssetListPage } from './fixed-asset-list.page';

let exportedBlob: Blob | undefined;
let downloadedFilename: string | undefined;
let createObjectUrlSpy: ReturnType<typeof vi.spyOn>;
let revokeObjectUrlSpy: ReturnType<typeof vi.spyOn>;
let anchorClickSpy: ReturnType<typeof vi.spyOn>;

function readBlob(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result));
    reader.onerror = () => reject(reader.error);
    reader.readAsText(blob);
  });
}

/** Extrae el XML de la hoja de un XLSX store-only generado por la app. */
function readXlsxSheet(bytes: Uint8Array): string {
  const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
  const decoder = new TextDecoder();
  let offset = 0;
  while (offset + 4 <= bytes.length && view.getUint32(offset, true) === 0x04034b50) {
    const size = view.getUint32(offset + 18, true);
    const nameLength = view.getUint16(offset + 26, true);
    const extraLength = view.getUint16(offset + 28, true);
    const name = decoder.decode(bytes.subarray(offset + 30, offset + 30 + nameLength));
    const dataStart = offset + 30 + nameLength + extraLength;
    if (name === 'xl/worksheets/sheet1.xml') {
      return decoder.decode(bytes.subarray(dataStart, dataStart + size));
    }
    offset = dataStart + size;
  }
  return '';
}

@Component({ selector: 'app-operation-period-filter', standalone: true, template: '' })
class PeriodFilterStub {
  @Output() readonly periodChange = new EventEmitter<OperationPeriod>();
  clear(): void { this.periodChange.emit({ start: null, end: null }); }
  period: OperationPeriod | null = null;
  set(period: OperationPeriod): void { this.period = period; }
}

interface FixedAssetHarness {
  fromDate: Date | null;
  toDate: Date | null;
  customsDocument: string;
  customsCode: string;
  partNumber: string;
  description: string;
  serialNumber: string;
  brand: string;
  model: string;
  currentPage: number;
  pageSize: number;
  items: WritableSignal<FixedAsset[]>;
  totalItems: WritableSignal<number>;
  isLoading: WritableSignal<boolean>;
  hasSearched: WritableSignal<boolean>;
  forbidden: WritableSignal<boolean>;
  error: WritableSignal<string | null>;
  expandedRowId: WritableSignal<string | number | null>;
  applySavedCriteria(criteria: SavedCriteria): void;
  savedCriteria(): SavedCriteria;
  canExport(): boolean;
  exportCsv(): Promise<void>;
  exportXlsx(): Promise<void>;
  toggleDetail(item: FixedAsset): void;
  detailFields(item: FixedAsset): { label: string; value: string }[];
  columnState(field: 'pedimento' | 'clavePedimento' | 'numeroParte' | 'descripcion' | 'serie' | 'marca' | 'modelo'): ColumnFilterState;
  onColumnStateChange(field: 'pedimento' | 'clavePedimento' | 'numeroParte' | 'descripcion' | 'serie' | 'marca' | 'modelo', state: ColumnFilterState): void;
  changePage(event: { pageIndex: number; pageSize: number; length: number }): void;
}

const ROW: FixedAsset = {
  entryLineId: 101,
  importId: 202,
  customsDocument: 'DOC-1',
  customsCode: 'A1',
  importDate: '2026-07-19T23:30:00-06:00',
  partNumber: 'PART-1',
  description: 'Equipo de prueba',
  tariffFraction: '1234.56',
  quantity: '0002.5000',
  unit: 'PZA',
  serialNumber: 'SER-1',
  brand: 'Marca',
  model: 'Modelo',
};

async function configure(options: { failing?: HttpErrorResponse } = {}) {
  const execute = options.failing
    ? vi.fn(() => throwError(() => options.failing))
    : vi.fn((criteria: FixedAssetSearchCriteria) => of({ items: [ROW], total: 101, page: criteria.page, pageSize: criteria.pageSize }));
  const notifications = { success: vi.fn(), info: vi.fn(), error: vi.fn(), warning: vi.fn() };
  TestBed.configureTestingModule({
    imports: [FixedAssetListPage],
    providers: [
      { provide: SearchFixedAssetsUseCase, useValue: { execute } },
      { provide: NotificationService, useValue: notifications },
    ],
  });
  TestBed.overrideComponent(FixedAssetListPage, {
    remove: { imports: [OperationPeriodFilterComponent] },
    add: { imports: [PeriodFilterStub] },
  });
  const fixture = TestBed.createComponent(FixedAssetListPage);
  fixture.detectChanges();
  await fixture.whenStable();
  const page = fixture.componentInstance as unknown as FixedAssetHarness;
  return { page, execute, notifications, fixture };
}

describe('FixedAssetListPage exportaciones', () => {
  beforeEach(() => {
    exportedBlob = undefined;
    downloadedFilename = undefined;
    createObjectUrlSpy = vi.spyOn(URL, 'createObjectURL').mockImplementation((blob) => {
      exportedBlob = blob as Blob;
      return 'blob:export';
    });
    revokeObjectUrlSpy = vi.spyOn(URL, 'revokeObjectURL').mockImplementation(() => undefined);
    anchorClickSpy = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (this: HTMLAnchorElement) {
      downloadedFilename = this.download;
    });
  });

  afterEach(() => {
    createObjectUrlSpy.mockRestore();
    revokeObjectUrlSpy.mockRestore();
    anchorClickSpy.mockRestore();
  });

  it('permite exportar sin periodo y usa filtros actuales en todas las páginas', async () => {
    const { page, execute } = await configure();
    page.customsDocument = 'DOC-FILTER';
    page.customsCode = 'A1';
    page.partNumber = 'P-FILTER';
    page.description = 'Equipo';
    page.serialNumber = 'SER-FILTER';
    page.brand = 'Marca-FILTER';
    page.model = 'Modelo-FILTER';
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(101);
    page.currentPage = 3;
    page.pageSize = 50;
    const visibleRows = page.items();
    expect(page.fromDate).toBeNull();
    expect(page.toDate).toBeNull();
    expect(page.canExport()).toBe(true);
    execute.mockClear();

    await page.exportCsv();

    expect(execute.mock.calls.map(([criteria]) => [criteria.page, criteria.pageSize])).toEqual([[1, 100], [2, 100]]);
    expect(execute).toHaveBeenCalledWith(expect.objectContaining({ from: null, to: null, customsDocument: 'DOC-FILTER', customsCode: 'A1', partNumber: 'P-FILTER', description: 'Equipo', serialNumber: 'SER-FILTER', brand: 'Marca-FILTER', model: 'Modelo-FILTER' }));
    expect(page.currentPage).toBe(3);
    expect(page.pageSize).toBe(50);
    expect(page.items()).toBe(visibleRows);
    expect(createObjectUrlSpy).toHaveBeenCalledOnce();
    expect(downloadedFilename).toBe('activos-fijos.csv');
    expect(exportedBlob).toBeInstanceOf(Blob);
    expect(exportedBlob?.type).toBe('text/csv;charset=utf-8');
    const csv = await readBlob(exportedBlob!);
    expect(csv).toContain('2026-07-19,DOC-1,A1,PART-1,Equipo de prueba,1234.56,0002.5000,PZA,SER-1,Marca,Modelo');
    expect(csv).not.toContain('entryLineId');
    expect(csv).not.toContain('importId');
  });

  it('exporta XLSX con encabezados, tipos y valores reales manteniendo CSV disponible', async () => {
    const { page, execute } = await configure();
    page.customsDocument = 'DOC-X';
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(101);
    execute.mockClear();

    await page.exportXlsx();

    expect(execute).toHaveBeenCalledWith(expect.objectContaining({ customsDocument: 'DOC-X', page: 1, pageSize: 100 }));
    expect(downloadedFilename).toBe('activos-fijos.xlsx');
    expect(exportedBlob?.type).toBe('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
    const bytes = new Uint8Array(await exportedBlob!.arrayBuffer());
    expect(Array.from(bytes.subarray(0, 4))).toEqual([0x50, 0x4b, 0x03, 0x04]);
    const sheet = readXlsxSheet(bytes);
    expect(sheet).toContain('Fecha importación');
    expect(sheet).toContain('Descripción');
    expect(sheet).toContain('Equipo de prueba');
    expect(sheet).toContain('<c r="G2" t="inlineStr"><is><t xml:space="preserve">0002.5000</t></is></c>');
    expect(sheet).not.toContain('<f');
  });

  it('permite periodo completo, envía ambas fechas y bloquea rango parcial o invertido', async () => {
    const { page, execute } = await configure();
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(101);
    page.fromDate = new Date(2026, 6, 1);
    expect(page.canExport()).toBe(false);
    page.toDate = new Date(2026, 6, 2);
    expect(page.canExport()).toBe(true);
    execute.mockClear();
    await page.exportCsv();
    expect(execute).toHaveBeenCalledWith(expect.objectContaining({ from: '2026-07-01', to: '2026-07-02' }));
    page.fromDate = new Date(2026, 6, 3);
    expect(page.canExport()).toBe(false);
  });

  it('aborta límite sobre 10,000 con una llamada y mantiene tabla', async () => {
    const { page, execute, notifications } = await configure();
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(10_001);
    const visibleRows = page.items();
    execute.mockClear();
    execute.mockImplementation((criteria) => of({ items: [], total: 10_001, page: criteria.page, pageSize: criteria.pageSize }));

    await page.exportCsv();

    expect(execute).toHaveBeenCalledOnce();
    expect(notifications.error).toHaveBeenCalledWith('La exportación supera el máximo de 10,000 registros.');
    expect(page.items()).toBe(visibleRows);
    expect(createObjectUrlSpy).not.toHaveBeenCalled();
  });

  it('aplica filtros sin periodo y rechaza rango parcial sin cambiar estado', async () => {
    const { page, notifications } = await configure();
    page.currentPage = 4;
    page.applySavedCriteria({ from: null, to: null, customsDocument: 'DOC-1', customsCode: 'A1', partNumber: 'P-1', description: 'Equipo', serialNumber: 'SER-1', brand: 'Marca', model: 'Modelo' });
    expect(page.fromDate).toBeNull();
    expect(page.toDate).toBeNull();
    expect(page.customsDocument).toBe('DOC-1');
    expect(page.currentPage).toBe(1);
    const snapshot = { fromDate: page.fromDate, toDate: page.toDate, customsDocument: page.customsDocument, currentPage: page.currentPage };
    page.applySavedCriteria({ from: '2026-07-01', to: null, customsDocument: 'STALE', customsCode: '', partNumber: '', description: '', serialNumber: '', brand: '', model: '' });
    expect({ fromDate: page.fromDate, toDate: page.toDate, customsDocument: page.customsDocument, currentPage: page.currentPage }).toEqual(snapshot);
    expect(notifications.error).toHaveBeenCalledWith('Esta consulta guardada ya no es compatible con esta pantalla.');
  });
});

describe('FixedAssetListPage filtros por columna, detalle y permisos', () => {
  beforeEach(() => {
    exportedBlob = undefined;
    downloadedFilename = undefined;
    createObjectUrlSpy = vi.spyOn(URL, 'createObjectURL').mockImplementation((blob) => {
      exportedBlob = blob as Blob;
      return 'blob:export';
    });
    revokeObjectUrlSpy = vi.spyOn(URL, 'revokeObjectURL').mockImplementation(() => undefined);
    anchorClickSpy = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (this: HTMLAnchorElement) {
      downloadedFilename = this.download;
    });
  });

  afterEach(() => {
    createObjectUrlSpy.mockRestore();
    revokeObjectUrlSpy.mockRestore();
    anchorClickSpy.mockRestore();
  });

  it('expone el detalle con los trece campos sin solicitar datos adicionales', async () => {
    const { page, execute } = await configure();
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(101);
    execute.mockClear();

    page.toggleDetail(ROW);

    expect(page.expandedRowId()).toBe(ROW.entryLineId);
    const fields = page.detailFields(ROW);
    expect(fields).toHaveLength(13);
    expect(fields.map((field) => field.label)).toContain('Importación');
    expect(fields.find((field) => field.label === 'Cantidad')?.value).toBe('0002.5000');
    expect(execute).not.toHaveBeenCalled();

    page.toggleDetail(ROW);
    expect(page.expandedRowId()).toBeNull();
    expect(execute).not.toHaveBeenCalled();
  });

  it('conserva el operador por columna al paginar y cierra el detalle', async () => {
    const { page, execute } = await configure();
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(101);
    page.onColumnStateChange('descripcion', { value: ' EQUIPO ', operator: 'CT' });
    page.onColumnStateChange('pedimento', { value: 'DOC-9', operator: 'SW' });
    expect(page.columnState('descripcion')).toEqual({ value: ' EQUIPO ', operator: 'CT' });
    page.toggleDetail(ROW);
    execute.mockClear();

    page.changePage({ pageIndex: 1, pageSize: 20, length: 101 });

    expect(page.currentPage).toBe(2);
    expect(page.expandedRowId()).toBeNull();
    page.isLoading.set(false);
    page.totalItems.set(101);
    await page.exportCsv();
    expect(execute).toHaveBeenCalledWith(expect.objectContaining({
      page: 1,
      operators: { descripcion: 'CT', pedimento: 'SW' },
      description: ' EQUIPO ',
    }));
  });

  it('muestra un estado específico de permiso insuficiente ante 403', async () => {
    const { page, notifications } = await configure({
      failing: new HttpErrorResponse({ status: 403, statusText: 'Forbidden' }),
    });

    await vi.waitFor(() => expect(page.forbidden()).toBe(true));

    expect(page.isLoading()).toBe(false);
    expect(page.error()).toBeNull();
    expect(notifications.error).not.toHaveBeenCalledWith('No fue posible consultar los activos fijos.');
  });

  it('guarda y reaplica los operadores por columna sin degradarlos a igualdad', async () => {
    const { page, execute } = await configure();
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(1);
    page.onColumnStateChange('descripcion', { value: 'EQUIPO', operator: 'CT' });
    page.onColumnStateChange('marca', { value: 'ACME', operator: 'SW' });

    const saved = page.savedCriteria();
    expect(saved['descripcionOp']).toBe('CT');
    expect(saved['marcaOp']).toBe('SW');
    expect(saved['serieOp']).toBe('EQ');

    page.applySavedCriteria(saved);
    expect(page.columnState('descripcion')).toEqual({ value: 'EQUIPO', operator: 'CT' });
    expect(page.columnState('marca')).toEqual({ value: 'ACME', operator: 'SW' });

    execute.mockClear();
    await page.exportCsv();
    expect(execute).toHaveBeenCalledWith(expect.objectContaining({
      operators: expect.objectContaining({ descripcion: 'CT', marca: 'SW' }),
    }));
  });

  it('rechaza una consulta guardada con operador fuera del contrato', async () => {
    const { page, notifications } = await configure();
    page.applySavedCriteria({
      from: null, to: null, customsDocument: '', customsCode: '', partNumber: '',
      description: 'EQUIPO', serialNumber: '', brand: '', model: '', descripcionOp: 'LIKE',
    });

    expect(notifications.error).toHaveBeenCalledWith('Esta consulta guardada ya no es compatible con esta pantalla.');
    expect(page.columnState('descripcion').operator).toBe('EQ');
  });

  it('bloquea la exportación cuando la consulta terminó en error', async () => {
    const { page } = await configure();
    page.isLoading.set(false);
    page.hasSearched.set(true);
    page.totalItems.set(5);
    page.error.set('fallo de red');
    expect(page.canExport()).toBe(false);

    page.error.set(null);
    page.forbidden.set(true);
    expect(page.canExport()).toBe(false);
  });
});
