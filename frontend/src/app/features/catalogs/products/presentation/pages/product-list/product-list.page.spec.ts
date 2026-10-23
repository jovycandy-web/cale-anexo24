import { WritableSignal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { of } from 'rxjs';
import { NotificationService } from '@core/notifications/notification.service';
import { Product } from '@features/catalogs/products/domain/models/product.model';
import { ProductPage, ProductSearchCriteria } from '@features/catalogs/products/domain/repositories/product.repository';
import { SearchProductsUseCase } from '@features/catalogs/products/application/use-cases/search-products.use-case';
import { ProductListPage } from './product-list.page';

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

interface ProductHarness {
  filter: string;
  currentPage: number;
  pageSize: number;
  items: WritableSignal<Product[]>;
  totalItems: WritableSignal<number>;
  isLoading: WritableSignal<boolean>;
  selectedProduct: WritableSignal<Product | null>;
  selectProduct(product: Product): void;
  closeDetail(): void;
  exportCsv(): Promise<void>;
}

const PRODUCTO: Product = {
  id: 12,
  partNumber: 'P0122',
  clientCode: 'CLI-P0122',
  description: 'JOVI TROPICAL POWDER NARANJA',
  tariffFraction: '17049099',
  commercialUnit: 'PZA',
  tariffUnit: 'PZA',
  nico: '8504',
  type: 'TERMINADO',
  auxiliary: 'AUX-1',
  warehouseKey: 77,
  warehouse: 'PRINCIPAL',
};

async function configure(rows: Product[] = [PRODUCTO], total = 1) {
  const execute = vi.fn((criteria: ProductSearchCriteria): ProductPage =>
    ({ items: rows, total, page: criteria.page, pageSize: criteria.pageSize }));
  const notifications = { success: vi.fn(), info: vi.fn(), error: vi.fn(), warning: vi.fn() };
  TestBed.configureTestingModule({
    imports: [ProductListPage],
    providers: [
      { provide: SearchProductsUseCase, useValue: { execute: (criteria: ProductSearchCriteria) => of(execute(criteria)) } },
      { provide: NotificationService, useValue: notifications },
    ],
  });
  const fixture = TestBed.createComponent(ProductListPage);
  fixture.detectChanges();
  await fixture.whenStable();
  const page = fixture.componentInstance as unknown as ProductHarness;
  return { page, execute, notifications, fixture };
}

describe('ProductListPage paridad SCR-046', () => {
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
    expect(page.items()).toEqual([PRODUCTO]);
    expect(page.items()[0].clientCode).toBe('CLI-P0122');
    expect(page.items()[0].nico).toBe('8504');
    expect(page.items()[0].type).toBe('TERMINADO');
    expect(page.items()[0].warehouse).toBe('PRINCIPAL');
    expect(page.items()[0].auxiliary).toBe('AUX-1');
  });

  it('abre y cierra el detalle del producto seleccionado', async () => {
    const { page, fixture } = await configure();
    page.selectProduct(PRODUCTO);
    fixture.detectChanges();

    expect(page.selectedProduct()).toEqual(PRODUCTO);
    expect(fixture.nativeElement.textContent).toContain('Detalle de P0122');
    expect(fixture.nativeElement.textContent).toContain('CLI-P0122');

    page.closeDetail();
    fixture.detectChanges();
    expect(page.selectedProduct()).toBeNull();
  });

  it('exporta CSV con todas las columnas visibles y reutiliza el filtro activo', async () => {
    const { page, execute } = await configure();
    page.filter = 'NARANJA';
    execute.mockClear();

    await page.exportCsv();

    expect(execute).toHaveBeenCalledWith({ filter: 'NARANJA', page: 1, pageSize: 100 });
    expect(downloadedFilename).toBe('productos.csv');
    const csv = await readBlob(exportedBlob!);
    expect(csv).toContain('Clave,Código cliente,Descripción,Fracción TIGIE,NICO,Unidad comercial,Unidad tarifaria,Tipo,Almacén,Auxiliar');
    expect(csv).toContain('P0122,CLI-P0122,JOVI TROPICAL POWDER NARANJA,17049099,8504,PZA,PZA,TERMINADO,PRINCIPAL,AUX-1');
    expect(csv).not.toContain('warehouseKey');
  });
});
