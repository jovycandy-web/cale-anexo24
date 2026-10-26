import { WritableSignal } from '@angular/core';
import { Component, EventEmitter, Output } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { describe, expect, it, vi } from 'vitest';
import { of } from 'rxjs';
import { NotificationService } from '@core/notifications/notification.service';
import { StructureLine } from '@features/catalogs/structures/domain/models/structure-line.model';
import { SearchStructuresUseCase } from '@features/catalogs/structures/application/use-cases/search-structures.use-case';
import { OperationPeriodFilterComponent } from '@features/operations/shared/presentation/operation-period-filter/operation-period-filter.component';
import { StructureListPage } from './structure-list.page';

@Component({ selector: 'app-operation-period-filter', standalone: true, template: '' })
class PeriodFilterStub {
  @Output() readonly periodChange = new EventEmitter<{ start: Date | null; end: Date | null }>();
  clear(): void { this.periodChange.emit({ start: null, end: null }); }
  set(): void { /* no-op */ }
}

interface StructureHarness {
  items: WritableSignal<StructureLine[]>;
  totalItems: WritableSignal<number>;
  isLoading: WritableSignal<boolean>;
  displayedColumns: string[];
  currentPage: number;
}

const LINEA: StructureLine = {
  structureId: 1,
  productId: 2,
  productCode: '300770',
  productDescription: 'ACHAMOYADOS WORMS REVOLCADO',
  productUnit: 'PZA',
  startDate: '2018-08-01T00:00:00-05:00',
  endDate: null,
  productMaterialId: 3,
  materialCode: '500017',
  materialDescription: 'ACIDO LACTICO',
  materialUnit: 'KG',
  materialTariffFraction: '17019999',
  incorporatedQuantity: 0.057,
  wasteQuantity: 0,
  scrapQuantity: 0,
};

async function configure() {
  const execute = vi.fn(() => of({ items: [LINEA], total: 1, page: 1, pageSize: 20 }));
  TestBed.configureTestingModule({
    imports: [StructureListPage],
    providers: [
      { provide: SearchStructuresUseCase, useValue: { execute } },
      { provide: NotificationService, useValue: { success: vi.fn(), info: vi.fn(), error: vi.fn(), warning: vi.fn() } },
    ],
  });
  TestBed.overrideComponent(StructureListPage, {
    remove: { imports: [OperationPeriodFilterComponent] },
    add: { imports: [PeriodFilterStub] },
  });
  const fixture = TestBed.createComponent(StructureListPage);
  fixture.detectChanges();
  await fixture.whenStable();
  const page = fixture.componentInstance as unknown as StructureHarness;
  page.isLoading.set(false);
  page.items.set([LINEA]);
  page.totalItems.set(1);
  fixture.detectChanges();
  return { page, fixture, execute };
}

describe('StructureListPage paridad SCR-006', () => {
  it('muestra exactamente las ocho columnas legacy en orden', async () => {
    const { page, fixture } = await configure();

    expect(page.displayedColumns).toEqual([
      'productCode',
      'startDate',
      'materialCode',
      'productDescription',
      'materialUnit',
      'incorporatedQuantity',
      'scrapQuantity',
      'wasteQuantity',
    ]);

    const encabezados = Array.from(fixture.nativeElement.querySelectorAll('th') as NodeListOf<HTMLElement>)
      .map((th) => th.textContent?.trim());
    expect(encabezados).toEqual([
      'Producto',
      'Fecha vigencia',
      'Material',
      'Descripción',
      'Unidad',
      'Incorporada',
      'Desperdicio',
      'Merma',
    ]);
  });

  it('no expone la fracción del material ni columnas adicionales', async () => {
    const { fixture } = await configure();
    const texto = (fixture.nativeElement.textContent as string).replace(/\s+/g, ' ');

    expect(texto).not.toContain('Fracción material');
    expect(texto).toContain('500017');
    expect(texto).toContain('ACHAMOYADOS WORMS REVOLCADO');
  });

  it('filtra por periodo de vigencia y lo envía a la consulta', async () => {
    const { page, execute, fixture } = await configure();
    execute.mockClear();

    expect(fixture.nativeElement.querySelector('app-operation-period-filter')).not.toBeNull();
    const stub = fixture.debugElement.query(
      (element) => element.name === 'app-operation-period-filter');
    stub.componentInstance.periodChange.emit({ start: new Date(2025, 0, 1), end: new Date(2025, 11, 31) });

    expect(execute).toHaveBeenCalledWith(expect.objectContaining({ from: '2025-01-01', to: '2025-12-31' }));
    expect(page.currentPage).toBe(1);
  });
});
