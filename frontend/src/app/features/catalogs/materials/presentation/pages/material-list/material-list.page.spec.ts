import { WritableSignal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { of } from 'rxjs';
import { NotificationService } from '@core/notifications/notification.service';
import { Material } from '@features/catalogs/materials/domain/models/material.model';
import { MaterialPage, MaterialSearchCriteria } from '@features/catalogs/materials/domain/repositories/material.repository';
import { SearchMaterialsUseCase } from '@features/catalogs/materials/application/use-cases/search-materials.use-case';
import { MaterialListPage } from './material-list.page';

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

interface MaterialHarness {
  filter: string;
  currentPage: number;
  pageSize: number;
  items: WritableSignal<Material[]>;
  totalItems: WritableSignal<number>;
  isLoading: WritableSignal<boolean>;
  displayedColumns: string[];
  exportCsv(): Promise<void>;
}

const MATERIAL: Material = {
  materialKey: 1,
  partNumber: '500005',
  supplierCode: 'CLAV-PR001',
  description: 'AZUCAR REFINADA',
  tariffFraction: '17019999',
  unit: 'KG',
  umc: 'KG',
  materialType: 'MATERIAL PRIMA',
  nationalType: 'MP',
  family: 'ENDULZANTES',
  nico: 'N',
  warehouseKey: 2,
  warehouse: 'ALM02',
};

async function configure(rows: Material[] = [MATERIAL], total = 1) {
  const execute = vi.fn((criteria: MaterialSearchCriteria): MaterialPage =>
    ({ items: rows, total, page: criteria.page, pageSize: criteria.pageSize }));
  const notifications = { success: vi.fn(), info: vi.fn(), error: vi.fn(), warning: vi.fn() };
  TestBed.configureTestingModule({
    imports: [MaterialListPage],
    providers: [
      { provide: SearchMaterialsUseCase, useValue: { execute: (criteria: MaterialSearchCriteria) => of(execute(criteria)) } },
      { provide: NotificationService, useValue: notifications },
    ],
  });
  const fixture = TestBed.createComponent(MaterialListPage);
  fixture.detectChanges();
  await fixture.whenStable();
  const page = fixture.componentInstance as unknown as MaterialHarness;
  return { page, execute, notifications, fixture };
}

describe('MaterialListPage paridad SCR-084', () => {
  beforeEach(() => {
    exportedBlob = undefined;
    downloadedFilename = undefined;
    createObjectUrlSpy = vi.spyOn(URL, 'createObjectURL').mockImplementation((blob) => {
      exportedBlob = blob as Blob;
      return 'blob:csv';
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

  it('consulta el catálogo con el filtro y la página activos', async () => {
    const { page, execute } = await configure();

    expect(execute).toHaveBeenCalledWith({ filter: '', page: 1, pageSize: 20 });
    expect(page.totalItems()).toBe(1);
    expect(page.items()).toEqual([MATERIAL]);
  });

  it('muestra exactamente las cinco columnas legacy en orden', async () => {
    const { page, fixture } = await configure();

    expect(page.displayedColumns).toEqual(['tariffFraction', 'description', 'umc', 'unit', 'partNumber']);
    const encabezados = Array.from(fixture.nativeElement.querySelectorAll('th') as NodeListOf<HTMLElement>)
      .map((th) => th.textContent?.trim());
    expect(encabezados).toEqual(['Fracción', 'Descripción', 'Unidad TIGIE', 'UMC', 'Número parte']);

    const celdas = Array.from(fixture.nativeElement.querySelectorAll('td') as NodeListOf<HTMLElement>)
      .map((td) => td.textContent?.trim());
    expect(celdas).toEqual(['17019999', 'AZUCAR REFINADA', 'KG', 'KG', '500005']);
  });

  it('no conserva columnas ni panel de atributos adicionales', async () => {
    const { fixture } = await configure();
    const texto = (fixture.nativeElement.textContent as string).replace(/\s+/g, ' ');

    expect(fixture.nativeElement.querySelector('app-detail-panel')).toBeNull();
    expect(texto).not.toContain('Clave proveedor');
    expect(texto).not.toContain('NICO');
    expect(texto).not.toContain('TIPOM');
    expect(texto).not.toContain('Familia');
    expect(texto).not.toContain('Ver detalle');
  });

  it('exporta CSV con las cinco columnas funcionales y reutiliza el filtro activo', async () => {
    const { page, execute } = await configure();
    page.filter = 'ENDULZANTES';
    execute.mockClear();

    await page.exportCsv();

    expect(execute).toHaveBeenCalledWith({ filter: 'ENDULZANTES', page: 1, pageSize: 100 });
    expect(downloadedFilename).toBe('materiales.csv');
    const csv = await readBlob(exportedBlob!);
    expect(csv).toContain('Fracción,Descripción,Unidad TIGIE,UMC,Número parte');
    expect(csv).toContain('17019999,AZUCAR REFINADA,KG,KG,500005');
    expect(csv).not.toContain('Clave proveedor');
    expect(csv).not.toContain('warehouseKey');
  });
});
