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
  displayedColumns: string[];
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

  it('consulta el catálogo con el filtro y la página activos', async () => {
    const { page, execute } = await configure();

    expect(execute).toHaveBeenCalledWith({ filter: '', page: 1, pageSize: 20 });
    expect(page.totalItems()).toBe(1);
    expect(page.items()).toEqual([PRODUCTO]);
  });

  it('muestra exactamente las cinco columnas legacy en orden', async () => {
    const { page, fixture } = await configure();

    expect(page.displayedColumns).toEqual(['tariffFraction', 'description', 'tariffUnit', 'commercialUnit', 'partNumber']);
    const encabezados = Array.from(fixture.nativeElement.querySelectorAll('th') as NodeListOf<HTMLElement>)
      .map((th) => th.textContent?.trim());
    expect(encabezados).toEqual(['Fracción', 'Descripción', 'Unidad TIGIE', 'UMC', 'Número parte']);

    const celdas = Array.from(fixture.nativeElement.querySelectorAll('td') as NodeListOf<HTMLElement>)
      .map((td) => td.textContent?.trim());
    expect(celdas).toEqual(['17049099', 'JOVI TROPICAL POWDER NARANJA', 'PZA', 'PZA', 'P0122']);
  });

  it('no conserva columnas ni panel de atributos adicionales', async () => {
    const { fixture } = await configure();
    const texto = (fixture.nativeElement.textContent as string).replace(/\s+/g, ' ');

    expect(fixture.nativeElement.querySelector('app-detail-panel')).toBeNull();
    expect(texto).not.toContain('Código cliente');
    expect(texto).not.toContain('NICO');
    expect(texto).not.toContain('Auxiliar');
    expect(texto).not.toContain('Almacén');
    expect(texto).not.toContain('Ver detalle');
  });

  it('pagina y vuelve a consultar con el tamaño solicitado', async () => {
    const { page, execute } = await configure();
    execute.mockClear();
    (page as unknown as { changePage(event: { pageIndex: number; pageSize: number }): void })
      .changePage({ pageIndex: 1, pageSize: 50 });

    expect(execute).toHaveBeenCalledWith({ filter: '', page: 2, pageSize: 50 });
  });

  it('exporta CSV con las cinco columnas funcionales y reutiliza el filtro activo', async () => {
    const { page, execute } = await configure();
    page.filter = 'NARANJA';
    execute.mockClear();

    await page.exportCsv();

    expect(execute).toHaveBeenCalledWith({ filter: 'NARANJA', page: 1, pageSize: 100 });
    expect(downloadedFilename).toBe('productos.csv');
    const csv = await readBlob(exportedBlob!);
    expect(csv).toContain('Fracción,Descripción,Unidad TIGIE,UMC,Número parte');
    expect(csv).toContain('17049099,JOVI TROPICAL POWDER NARANJA,PZA,PZA,P0122');
    expect(csv).not.toContain('Código cliente');
    expect(csv).not.toContain('NICO');
    expect(csv).not.toContain('warehouseKey');
  });
});
