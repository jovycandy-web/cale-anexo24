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
  selectedMaterial: WritableSignal<Material | null>;
  selectMaterial(material: Material): void;
  toggleDetail(material: Material): void;
  isSelected(material: Material): boolean;
  closeDetail(): void;
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

  it('consulta el catálogo con el filtro y la página activos y expone los campos ampliados', async () => {
    const { page, execute } = await configure();

    expect(execute).toHaveBeenCalledWith({ filter: '', page: 1, pageSize: 20 });
    expect(page.totalItems()).toBe(1);
    expect(page.items()).toEqual([MATERIAL]);
    expect(page.items()[0].supplierCode).toBe('CLAV-PR001');
    expect(page.items()[0].nico).toBe('N');
    expect(page.items()[0].nationalType).toBe('MP');
    expect(page.items()[0].family).toBe('ENDULZANTES');
    expect(page.items()[0].warehouse).toBe('ALM02');
  });

  it('expande el detalle bajo la fila seleccionada y permite cerrarlo', async () => {
    const { page, fixture } = await configure();
    page.toggleDetail(MATERIAL);
    fixture.detectChanges();

    expect(page.selectedMaterial()).toEqual(MATERIAL);
    expect(page.isSelected(MATERIAL)).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('Detalle de 500005');
    expect(fixture.nativeElement.textContent).toContain('CLAV-PR001');
    expect(fixture.nativeElement.querySelector('table')?.textContent).toContain('Detalle de 500005');

    page.toggleDetail(MATERIAL);
    fixture.detectChanges();
    expect(page.selectedMaterial()).toBeNull();
    expect(page.isSelected(MATERIAL)).toBe(false);

    page.selectMaterial(MATERIAL);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Detalle de 500005');

    page.closeDetail();
    fixture.detectChanges();
    expect(page.selectedMaterial()).toBeNull();
  });

  it('exporta CSV con todas las columnas visibles y reutiliza el filtro activo', async () => {
    const { page, execute } = await configure();
    page.filter = 'ENDULZANTES';
    execute.mockClear();

    await page.exportCsv();

    expect(execute).toHaveBeenCalledWith({ filter: 'ENDULZANTES', page: 1, pageSize: 100 });
    expect(downloadedFilename).toBe('materiales.csv');
    const csv = await readBlob(exportedBlob!);
    expect(csv).toContain('Clave MP,Clave proveedor,Descripción comercial,Fracción,NICO,UMC,Unidad TIGIE,TIPOM,Tipo de material,Familia,Almacén');
    expect(csv).toContain('500005,CLAV-PR001,AZUCAR REFINADA,17019999,N,KG,KG,MP,MATERIAL PRIMA,ENDULZANTES,ALM02');
    expect(csv).not.toContain('warehouseKey');
  });
});
