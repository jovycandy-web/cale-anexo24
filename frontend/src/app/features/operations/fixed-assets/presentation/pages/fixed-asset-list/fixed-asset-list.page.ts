import { Component, DestroyRef, ViewChild, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { EMPTY, Observable, Subject, catchError, debounce, filter, firstValueFrom, map, switchMap, timer } from 'rxjs';
import { userFacingApiError } from '@core/http/api-error.util';
import { NotificationService } from '@core/notifications/notification.service';
import { AppAlertComponent } from '@core/ui/app-alert/app-alert.component';
import { CSV_EXPORT_LIMIT_MESSAGE, downloadCsv, loadAllCsvPages, normalizeCsvDate, serializeCsv } from '@core/export/csv-export';
import {
  XLSX_EXPORT_LIMIT_MESSAGE,
  downloadXlsx,
  loadAllXlsxPages,
  serializeXlsx,
} from '@core/export/xlsx-export';
import {
  ColumnFilterState,
  countActiveColumnFilters,
  isSupportedColumnFilterOperator,
  normalizeColumnFilterOperator,
} from '@core/table-filters/column-filter.model';
import { ColumnFilterInputComponent } from '@core/table-filters/column-filter-input.component';
import { SavedCriteria } from '@features/saved-queries/domain/saved-query.model';
import { SavedQueriesControlComponent } from '@features/saved-queries/presentation/saved-queries-control.component';
import {
  formatLocalDateForApi,
  formatOperationDate,
  formatOperationQuantity,
  formatOperationText,
} from '@features/operations/shared/operation-formatters';
import {
  OperationPeriod,
  OperationPeriodFilterComponent,
} from '@features/operations/shared/presentation/operation-period-filter/operation-period-filter.component';
import {
  FixedAsset,
  FixedAssetFilterField,
  FixedAssetFilterOperator,
  FixedAssetPage,
  FixedAssetSearchCriteria,
} from '@features/operations/fixed-assets/domain/models/fixed-asset.model';
import { SearchFixedAssetsUseCase } from '@features/operations/fixed-assets/application/use-cases/search-fixed-assets.use-case';

interface FixedAssetSearchResult {
  requestId: number;
  page: FixedAssetPage;
}

interface DetailField {
  label: string;
  value: string;
}

/** Encabezados compartidos por las exportaciones CSV y XLSX. */
const EXPORT_HEADERS = [
  'Fecha de pago',
  'Fecha de entrada',
  'Pedimento',
  'Clave pedimento',
  'N° parte',
  'Descripción',
  'Fracción',
  'Cantidad',
  'Unidad',
  'Serie',
  'Marca',
  'Modelo',
];

/** Consulta paginada de partidas de importación marcadas como activos fijos. */
@Component({
  imports: [
    AppAlertComponent,
    ColumnFilterInputComponent,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatTableModule,
    OperationPeriodFilterComponent,
    SavedQueriesControlComponent,
  ],
  selector: 'app-fixed-asset-list',
  template: `
    <div class="min-h-full bg-[#f4f7fb] text-slate-800">
      <main class="mx-auto w-full max-w-360 px-5 py-8 sm:px-8">
        <header class="mb-6">
          <p class="mb-1 text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-600">Operaciones</p>
          <h1 class="m-0 text-2xl font-semibold tracking-tight text-slate-900">Activos fijos</h1>
          <p class="mt-1 text-sm text-slate-500">Consulta de partidas de importación marcadas como activos fijos.</p>
        </header>

        <section class="mb-4 rounded-2xl border border-slate-200/80 bg-white p-4 shadow-[0_4px_18px_rgb(15_23_42/4%)]" aria-labelledby="fixed-asset-filters">
          <h2 id="fixed-asset-filters" class="sr-only">Filtros de activos fijos</h2>
          <div class="flex flex-wrap items-center gap-3">
            <app-operation-period-filter #periodFilter class="min-w-0 flex-1" (periodChange)="onPeriodChange($event)" />
            <div class="flex shrink-0 items-center gap-1.5">
              <button
                mat-stroked-button
                type="button"
                class="h-9 rounded-lg! px-3!"
                [attr.aria-expanded]="optionalFiltersExpanded()"
                aria-controls="fixed-asset-optional-filters"
                aria-label="Mostrar filtros por columna"
                (click)="toggleOptionalFilters()"
              >
                <mat-icon class="text-[18px]!" aria-hidden="true">tune</mat-icon>
                <span>Filtros</span>
                @if (activeFilterCount() > 0) {
                  <span class="ml-1 inline-flex min-w-5 items-center justify-center rounded-full bg-blue-100 px-1.5 text-[11px] font-bold text-blue-700">{{ activeFilterCount() }}</span>
                }
              </button>
              <button mat-button type="button" class="h-9 rounded-lg! px-3! text-slate-500!" (click)="clearFilters()">Limpiar</button>
            </div>
          </div>

          @if (hasInvalidDateFilter()) {
            <p class="mt-2 mb-0 text-xs text-amber-700" aria-live="polite">Selecciona ambas fechas para filtrar por periodo.</p>
          }

          @if (optionalFiltersExpanded()) {
            <div id="fixed-asset-optional-filters" class="mt-3 grid grid-cols-1 gap-3 border-t border-slate-100 pt-3 md:grid-cols-2 xl:grid-cols-4">
              <app-column-filter-input
                label="Pedimento"
                fieldName="fixed-asset-customs-document"
                placeholder="Documento de importación"
                [maxlength]="20"
                [state]="columnState('pedimento')"
                (filterChange)="onColumnFilterSearch()"
                (stateChange)="onColumnStateChange('pedimento', $event)"
              />
              <app-column-filter-input
                label="Clave de pedimento"
                fieldName="fixed-asset-customs-code"
                placeholder="Clave"
                [maxlength]="5"
                [state]="columnState('clavePedimento')"
                (filterChange)="onColumnFilterSearch()"
                (stateChange)="onColumnStateChange('clavePedimento', $event)"
              />
              <app-column-filter-input
                label="Número de parte"
                fieldName="fixed-asset-part-number"
                placeholder="Número de parte"
                [maxlength]="50"
                [state]="columnState('numeroParte')"
                (filterChange)="onColumnFilterSearch()"
                (stateChange)="onColumnStateChange('numeroParte', $event)"
              />
              <app-column-filter-input
                label="Descripción"
                fieldName="fixed-asset-description"
                placeholder="Descripción"
                [maxlength]="250"
                [state]="columnState('descripcion')"
                (filterChange)="onColumnFilterSearch()"
                (stateChange)="onColumnStateChange('descripcion', $event)"
              />
              <app-column-filter-input
                label="Serie"
                fieldName="fixed-asset-serial-number"
                placeholder="Número de serie"
                [maxlength]="50"
                [state]="columnState('serie')"
                (filterChange)="onColumnFilterSearch()"
                (stateChange)="onColumnStateChange('serie', $event)"
              />
              <app-column-filter-input
                label="Marca"
                fieldName="fixed-asset-brand"
                placeholder="Marca"
                [maxlength]="50"
                [state]="columnState('marca')"
                (filterChange)="onColumnFilterSearch()"
                (stateChange)="onColumnStateChange('marca', $event)"
              />
              <app-column-filter-input
                label="Modelo"
                fieldName="fixed-asset-model"
                placeholder="Modelo"
                [maxlength]="50"
                [state]="columnState('modelo')"
                (filterChange)="onColumnFilterSearch()"
                (stateChange)="onColumnStateChange('modelo', $event)"
              />
            </div>
          }
        </section>

        <div class="mb-4"><app-saved-queries-control scope="ACTIVOS_FIJOS" [criteria]="savedCriteria()" (applyCriteria)="applySavedCriteria($event)" /></div>

        @if (hasSearched()) {
          <div class="mb-3 flex min-h-9 flex-wrap items-center justify-between gap-3 px-1">
            <span class="text-xs font-medium text-slate-500" aria-live="polite">{{ formatTotal() }} resultados</span>
            <div class="flex flex-wrap items-center gap-2">
              <button mat-stroked-button type="button" class="h-9 rounded-lg! px-3!" (click)="exportCsv()" [disabled]="!canExport()"><mat-icon class="text-[18px]!" aria-hidden="true">download</mat-icon>{{ isExporting() ? 'Exportando...' : 'Exportar CSV' }}</button>
              <button mat-stroked-button type="button" class="h-9 rounded-lg! px-3!" (click)="exportXlsx()" [disabled]="!canExport()"><mat-icon class="text-[18px]!" aria-hidden="true">table_view</mat-icon>{{ isExporting() ? 'Exportando...' : 'Exportar XLSX' }}</button>
              <button mat-button type="button" class="h-9 rounded-lg! px-3! text-slate-600!" (click)="refresh()" [disabled]="!canRefresh()" aria-label="Actualizar consulta de activos fijos">
                <mat-icon class="text-[18px]!" aria-hidden="true">refresh</mat-icon> Actualizar
              </button>
            </div>
          </div>
        }

        @if (isLoading()) {
          <div class="space-y-3 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm" aria-label="Cargando activos fijos" aria-busy="true">
            @for (row of loadingRows; track row) { <div class="h-10 animate-pulse rounded-lg bg-slate-100"></div> }
          </div>
        } @else if (forbidden()) {
          <section class="flex min-h-52 flex-col items-center justify-center rounded-2xl border border-slate-200/80 bg-white px-6 py-10 text-center shadow-[0_4px_18px_rgb(15_23_42/4%)]" aria-label="Permiso insuficiente">
            <mat-icon class="mb-3 h-10 w-10 text-[40px]! text-slate-300" aria-hidden="true">lock</mat-icon>
            <p class="m-0 text-sm font-medium text-slate-600">No cuentas con permiso para consultar activos fijos</p>
            <span class="mt-1 text-xs text-slate-400">Solicita el permiso de consulta de operaciones a un administrador.</span>
          </section>
        } @else if (error()) {
          <section class="rounded-2xl border border-slate-200/80 bg-white p-4 shadow-[0_4px_18px_rgb(15_23_42/4%)]" aria-label="Error de consulta">
            <app-alert kind="error" title="No pudimos cargar los activos fijos" [message]="error()!" actionLabel="Reintentar" (action)="refresh()" />
          </section>
        } @else if (hasInvalidDateFilter()) {
          <section class="flex min-h-52 flex-col items-center justify-center rounded-2xl border border-dashed border-slate-200 bg-white px-6 py-10 text-center" aria-label="Periodo incompleto">
            <mat-icon class="mb-3 h-10 w-10 text-[40px]! text-slate-300" aria-hidden="true">date_range</mat-icon>
            <p class="m-0 text-sm font-medium text-slate-600">Completa el periodo para aplicar el filtro</p>
            <span class="mt-1 text-xs text-slate-400">También puedes limpiar ambas fechas para consultar todos los activos.</span>
          </section>
        } @else {
          <section class="overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-[0_4px_18px_rgb(15_23_42/4%)]" aria-label="Resultados de activos fijos">
            @if (items().length > 0) {
              <div class="overflow-x-auto">
                <table mat-table [dataSource]="items()" class="w-full min-w-275" aria-label="Activos fijos">
                  <ng-container matColumnDef="expand">
                    <th mat-header-cell *matHeaderCellDef class="w-12"><span class="sr-only">Detalle</span></th>
                    <td mat-cell *matCellDef="let item">
                      <button
                        type="button"
                        class="flex h-8 w-8 items-center justify-center rounded-lg text-slate-500 transition hover:bg-slate-100 hover:text-slate-700 focus:ring-4 focus:ring-blue-500/10"
                        [attr.aria-expanded]="expandedRowId() === item.entryLineId"
                        [attr.aria-controls]="expandedRowId() === item.entryLineId ? 'fixed-asset-detail' : null"
                        [attr.aria-label]="'Ver detalle de la partida ' + formatText(item.partNumber)"
                        (click)="toggleDetail(item)"
                      >
                        <mat-icon class="text-[18px]!" aria-hidden="true">{{ expandedRowId() === item.entryLineId ? 'expand_less' : 'expand_more' }}</mat-icon>
                      </button>
                    </td>
                  </ng-container>
                  <ng-container matColumnDef="importDate">
                    <th mat-header-cell *matHeaderCellDef>Fecha de pago</th>
                    <td mat-cell *matCellDef="let item">{{ formatDate(item.importDate) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="entryDate">
                    <th mat-header-cell *matHeaderCellDef>Fecha de entrada</th>
                    <td mat-cell *matCellDef="let item">{{ formatDate(item.entryDate) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="customsDocument">
                    <th mat-header-cell *matHeaderCellDef>Pedimento</th>
                    <td mat-cell *matCellDef="let item" class="font-medium text-slate-700">{{ formatText(item.customsDocument) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="customsCode">
                    <th mat-header-cell *matHeaderCellDef>Clave</th>
                    <td mat-cell *matCellDef="let item">{{ formatText(item.customsCode) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="part">
                    <th mat-header-cell *matHeaderCellDef>N° parte / Descripción</th>
                    <td mat-cell *matCellDef="let item">
                      <div class="font-medium text-slate-700">{{ formatText(item.partNumber) }}</div>
                      <div class="mt-0.5 text-xs text-slate-400">{{ formatText(item.description) }}</div>
                    </td>
                  </ng-container>
                  <ng-container matColumnDef="tariffFraction">
                    <th mat-header-cell *matHeaderCellDef>Fracción</th>
                    <td mat-cell *matCellDef="let item">{{ formatText(item.tariffFraction) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="quantity">
                    <th mat-header-cell *matHeaderCellDef>Cantidad</th>
                    <td mat-cell *matCellDef="let item" class="text-right tabular-nums">{{ formatQuantity(item.quantity) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="unit">
                    <th mat-header-cell *matHeaderCellDef>Unidad</th>
                    <td mat-cell *matCellDef="let item">{{ formatText(item.unit) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="serialNumber">
                    <th mat-header-cell *matHeaderCellDef>Serie</th>
                    <td mat-cell *matCellDef="let item">{{ formatText(item.serialNumber) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="brand">
                    <th mat-header-cell *matHeaderCellDef>Marca</th>
                    <td mat-cell *matCellDef="let item">{{ formatText(item.brand) }}</td>
                  </ng-container>
                  <ng-container matColumnDef="model">
                    <th mat-header-cell *matHeaderCellDef>Modelo</th>
                    <td mat-cell *matCellDef="let item">{{ formatText(item.model) }}</td>
                  </ng-container>
                  <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                  <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
                </table>
              </div>

              @if (expandedItem(); as detalle) {
                <section id="fixed-asset-detail" tabindex="-1" role="region" aria-live="polite" aria-label="Detalle del activo fijo" class="border-t border-slate-200 bg-slate-50/70 px-5 py-4 outline-none focus:ring-4 focus:ring-blue-500/10">
                  <div class="mb-3 flex items-center justify-between gap-3">
                    <h2 class="m-0 text-sm font-semibold text-slate-800">Detalle de la partida {{ formatText(detalle.partNumber) }}</h2>
                    <button mat-button type="button" class="h-8 rounded-lg! px-2! text-slate-500!" (click)="collapseDetail()">Cerrar detalle</button>
                  </div>
                  <dl class="m-0 grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-3">
                    @for (field of detailFields(detalle); track field.label) {
                      <div class="min-w-0">
                        <dt class="text-[11px] font-semibold uppercase tracking-wide text-slate-400">{{ field.label }}</dt>
                        <dd class="m-0 break-words text-sm text-slate-700">{{ field.value }}</dd>
                      </div>
                    }
                  </dl>
                </section>
              }
            } @else {
              <div class="flex min-h-52 flex-col items-center justify-center px-6 py-10 text-center" aria-label="Sin resultados">
                <mat-icon class="mb-3 h-10 w-10 text-[40px]! text-slate-300" aria-hidden="true">search_off</mat-icon>
                <p class="m-0 text-sm font-medium text-slate-600">No se encontraron activos fijos</p>
                <span class="mt-1 text-xs text-slate-400">No hay registros para los filtros seleccionados.</span>
              </div>
            }
            <mat-paginator [length]="totalItems()" [pageIndex]="currentPage - 1" [pageSize]="pageSize" [pageSizeOptions]="pageSizeOptions" (page)="changePage($event)" showFirstLastButtons aria-label="Paginación de activos fijos" />
          </section>
        }
      </main>
    </div>
  `,
})
export class FixedAssetListPage {
  @ViewChild(OperationPeriodFilterComponent) private periodFilter?: OperationPeriodFilterComponent;

  protected readonly displayedColumns = [
    'expand',
    'importDate',
    'entryDate',
    'customsDocument',
    'customsCode',
    'part',
    'tariffFraction',
    'quantity',
    'unit',
    'serialNumber',
    'brand',
    'model',
  ];
  protected readonly pageSizeOptions = [20, 50, 100];
  protected readonly loadingRows = [1, 2, 3, 4, 5];
  protected readonly items = signal<FixedAsset[]>([]);
  protected readonly totalItems = signal(0);
  protected readonly isLoading = signal(false);
  protected readonly isExporting = signal(false);
  protected readonly hasSearched = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly forbidden = signal(false);
  protected readonly optionalFiltersExpanded = signal(false);
  protected readonly expandedRowId = signal<string | number | null>(null);
  protected readonly expandedItem = computed(() =>
    this.items().find((item) => item.entryLineId === this.expandedRowId()) ?? null);

  protected fromDate: Date | null = null;
  protected toDate: Date | null = null;
  protected customsDocument = '';
  protected customsCode = '';
  protected partNumber = '';
  protected description = '';
  protected serialNumber = '';
  protected brand = '';
  protected model = '';
  protected currentPage = 1;
  protected pageSize = 20;

  /** Operadores por columna; la ausencia se interpreta como igualdad. */
  private readonly columnOperators = signal<Partial<Record<FixedAssetFilterField, FixedAssetFilterOperator>>>({});

  private requestSequence = 0;
  private readonly searchTriggers = new Subject<boolean>();
  private readonly destroyRef = inject(DestroyRef);
  private readonly searchFixedAssets = inject(SearchFixedAssetsUseCase);
  private readonly notifications = inject(NotificationService);

  constructor() {
    // Al abrir el detalle se lleva el foco al panel para lectores de pantalla.
    effect(() => {
      if (this.expandedRowId() === null) return;
      queueMicrotask(() => document.getElementById('fixed-asset-detail')?.focus());
    });

    this.searchTriggers
      .pipe(
        debounce((immediate) => timer(immediate ? 0 : 500)),
        map(() => this.buildCriteria()),
        filter(() => this.hasValidDateFilter()),
        switchMap((criteria) => this.executeSearch(criteria)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(({ requestId, page }) => {
        if (requestId !== this.requestSequence) return;
        this.items.set(page.items);
        this.totalItems.set(page.total);
        this.isLoading.set(false);
      });

    this.hasSearched.set(true);
    this.scheduleSearch(true);
  }

  protected onPeriodChange(period: OperationPeriod): void {
    this.fromDate = period.start;
    this.toDate = period.end;

    if (!this.hasValidDateFilter()) {
      this.invalidateResults();
      return;
    }

    this.currentPage = 1;
    this.hasSearched.set(true);
    this.scheduleSearch(true);
  }

  protected onOptionalFilterChange(): void {
    this.optionalFiltersExpanded.set(true);
    if (!this.hasValidDateFilter()) return;

    this.currentPage = 1;
    this.hasSearched.set(true);
    this.scheduleSearch(false);
  }

  protected toggleOptionalFilters(): void {
    this.optionalFiltersExpanded.update((expanded) => !expanded);
  }

  protected activeFilterCount(): number {
    const campos: FixedAssetFilterField[] = ['pedimento', 'clavePedimento', 'numeroParte',
      'descripcion', 'serie', 'marca', 'modelo'];
    return countActiveColumnFilters(Object.fromEntries(campos.map((campo) => [campo, this.columnState(campo)])));
  }

  /** Búsqueda inmediata solicitada desde el filtro compartido (Enter o limpiar). */
  protected onColumnFilterSearch(): void {
    if (!this.hasValidDateFilter()) return;
    this.currentPage = 1;
    this.hasSearched.set(true);
    this.scheduleSearch(true);
  }

  /** Construye el estado que consume un filtro por columna. */
  protected columnState(field: FixedAssetFilterField): ColumnFilterState {
    return {
      value: this.columnValue(field),
      operator: this.columnOperators()[field] ?? 'EQ',
    };
  }

  /** Propaga el cambio de valor u operador del filtro compartido. */
  protected onColumnStateChange(field: FixedAssetFilterField, state: ColumnFilterState): void {
    this.setColumnValue(field, state.value);
    this.columnOperators.update((current) => ({
      ...current,
      [field]: normalizeColumnFilterOperator(state.operator),
    }));
    this.onOptionalFilterChange();
  }

  protected toggleDetail(item: FixedAsset): void {
    this.expandedRowId.update((current) => (current === item.entryLineId ? null : item.entryLineId));
  }

  protected collapseDetail(): void {
    this.expandedRowId.set(null);
  }

  /** Expone todos los campos del contrato en el detalle accesible. */
  protected detailFields(item: FixedAsset): DetailField[] {
    return [
      { label: 'Partida de entrada', value: String(item.entryLineId) },
      { label: 'Importación', value: String(item.importId) },
      { label: 'Pedimento', value: this.formatText(item.customsDocument) },
      { label: 'Clave de pedimento', value: this.formatText(item.customsCode) },
      { label: 'Fecha de pago', value: this.formatDate(item.importDate) },
      { label: 'Fecha de entrada', value: this.formatDate(item.entryDate) },
      { label: 'Número de parte', value: this.formatText(item.partNumber) },
      { label: 'Descripción', value: this.formatText(item.description) },
      { label: 'Fracción', value: this.formatText(item.tariffFraction) },
      { label: 'Cantidad', value: this.formatQuantity(item.quantity) },
      { label: 'Unidad', value: this.formatText(item.unit) },
      { label: 'Número de serie', value: this.formatText(item.serialNumber) },
      { label: 'Marca', value: this.formatText(item.brand) },
      { label: 'Modelo', value: this.formatText(item.model) },
    ];
  }

  protected formatTotal(): string {
    return new Intl.NumberFormat('es-MX').format(this.totalItems());
  }

  protected canRefresh(): boolean {
    return this.hasSearched() && this.hasValidDateFilter();
  }

  protected canExport(): boolean {
    return this.hasSearched() && this.hasValidDateFilter() && !this.isLoading() && !this.isExporting()
      && !this.forbidden() && !this.error() && this.totalItems() > 0;
  }

  protected async exportCsv(): Promise<void> {
    if (!this.canExport()) return;
    const criteria = this.buildCriteria();
    this.isExporting.set(true);
    try {
      const rows = await loadAllCsvPages((page, pageSize) => firstValueFrom(this.searchFixedAssets.execute({ ...criteria, page, pageSize })));
      if (!rows.length) { this.notifications.info('No hay registros para exportar.'); return; }
      const csv = serializeCsv(EXPORT_HEADERS, rows.map(toExportRow));
      downloadCsv('activos-fijos.csv', csv);
    } catch (cause) {
      this.notifications.error(exportErrorMessage(cause, CSV_EXPORT_LIMIT_MESSAGE));
    } finally {
      this.isExporting.set(false);
    }
  }

  protected async exportXlsx(): Promise<void> {
    if (!this.canExport()) return;
    const criteria = this.buildCriteria();
    this.isExporting.set(true);
    try {
      const rows = await loadAllXlsxPages((page, pageSize) => firstValueFrom(this.searchFixedAssets.execute({ ...criteria, page, pageSize })));
      if (!rows.length) { this.notifications.info('No hay registros para exportar.'); return; }
      downloadXlsx('activos-fijos.xlsx', serializeXlsx('Activos fijos', EXPORT_HEADERS, rows.map(toExportRow)));
    } catch (cause) {
      this.notifications.error(exportErrorMessage(cause, XLSX_EXPORT_LIMIT_MESSAGE));
    } finally {
      this.isExporting.set(false);
    }
  }

  protected refresh(): void {
    if (!this.canRefresh()) return;
    this.scheduleSearch(true);
  }

  protected savedCriteria(): SavedCriteria {
    const operators = this.columnOperators();
    return {
      from: formatLocalDateForApi(this.fromDate),
      to: formatLocalDateForApi(this.toDate),
      customsDocument: this.customsDocument,
      customsCode: this.customsCode,
      partNumber: this.partNumber,
      description: this.description,
      serialNumber: this.serialNumber,
      brand: this.brand,
      model: this.model,
      pedimentoOp: operators.pedimento ?? 'EQ',
      clavePedimentoOp: operators.clavePedimento ?? 'EQ',
      numeroParteOp: operators.numeroParte ?? 'EQ',
      descripcionOp: operators.descripcion ?? 'EQ',
      serieOp: operators.serie ?? 'EQ',
      marcaOp: operators.marca ?? 'EQ',
      modeloOp: operators.modelo ?? 'EQ',
    };
  }

  protected applySavedCriteria(criteria: SavedCriteria): void {
    const from = parseNullableStoredDate(criteria['from']); const to = parseNullableStoredDate(criteria['to']);
    const values = ['customsDocument', 'customsCode', 'partNumber', 'description', 'serialNumber', 'brand', 'model'].map((key) => criteria[key]);
    const operators = parseStoredOperators(criteria);
    if (from === undefined || to === undefined || Boolean(from) !== Boolean(to) || (from && to && from.getTime() > to.getTime()) || values.some((value) => typeof value !== 'string') || operators === undefined) { this.notifications.error('Esta consulta guardada ya no es compatible con esta pantalla.'); return; }
    this.requestSequence += 1; this.fromDate = from; this.toDate = to;
    [this.customsDocument, this.customsCode, this.partNumber, this.description, this.serialNumber, this.brand, this.model] = values as string[];
    this.columnOperators.set(operators);
    this.periodFilter?.set({ start: from, end: to }); this.currentPage = 1; this.hasSearched.set(true); this.optionalFiltersExpanded.set(values.some(Boolean)); this.scheduleSearch(true);
  }

  protected clearFilters(): void {
    this.requestSequence += 1;
    this.periodFilter?.clear();
    this.fromDate = null;
    this.toDate = null;
    this.customsDocument = '';
    this.customsCode = '';
    this.partNumber = '';
    this.description = '';
    this.serialNumber = '';
    this.brand = '';
    this.model = '';
    this.columnOperators.set({});
    this.currentPage = 1;
    this.items.set([]);
    this.totalItems.set(0);
    this.error.set(null);
    this.forbidden.set(false);
    this.expandedRowId.set(null);
    this.isLoading.set(false);
    this.optionalFiltersExpanded.set(false);
    this.hasSearched.set(true);
    this.scheduleSearch(true);
  }

  protected changePage(event: PageEvent): void {
    if (!this.hasValidDateFilter()) return;
    this.currentPage = event.pageIndex + 1;
    this.pageSize = event.pageSize;
    this.expandedRowId.set(null);
    this.scheduleSearch(true);
  }

  protected formatDate(value: string | null): string {
    return formatOperationDate(value);
  }

  protected formatQuantity(value: number | string | null): string {
    return formatOperationQuantity(value);
  }

  protected formatText(value: string | null): string {
    return formatOperationText(value);
  }

  protected hasInvalidDateFilter(): boolean {
    return !this.hasValidDateFilter();
  }

  private columnValue(field: FixedAssetFilterField): string {
    switch (field) {
      case 'pedimento': return this.customsDocument;
      case 'clavePedimento': return this.customsCode;
      case 'numeroParte': return this.partNumber;
      case 'descripcion': return this.description;
      case 'serie': return this.serialNumber;
      case 'marca': return this.brand;
      case 'modelo': return this.model;
    }
  }

  private setColumnValue(field: FixedAssetFilterField, value: string): void {
    switch (field) {
      case 'pedimento': this.customsDocument = value; break;
      case 'clavePedimento': this.customsCode = value; break;
      case 'numeroParte': this.partNumber = value; break;
      case 'descripcion': this.description = value; break;
      case 'serie': this.serialNumber = value; break;
      case 'marca': this.brand = value; break;
      case 'modelo': this.model = value; break;
    }
  }

  private hasValidDateFilter(): boolean {
    const hasFrom = this.fromDate !== null;
    const hasTo = this.toDate !== null;

    if (!hasFrom && !hasTo) return true;
    return Boolean(this.fromDate && this.toDate && this.fromDate.getTime() <= this.toDate.getTime());
  }

  private buildCriteria(): FixedAssetSearchCriteria {
    return {
      from: formatLocalDateForApi(this.fromDate),
      to: formatLocalDateForApi(this.toDate),
      customsDocument: this.customsDocument,
      customsCode: this.customsCode,
      partNumber: this.partNumber,
      description: this.description,
      serialNumber: this.serialNumber,
      brand: this.brand,
      model: this.model,
      operators: this.columnOperators(),
      page: this.currentPage,
      pageSize: this.pageSize,
    };
  }

  private scheduleSearch(immediate: boolean): void {
    this.requestSequence += 1;
    this.searchTriggers.next(immediate);
  }

  private executeSearch(criteria: FixedAssetSearchCriteria): Observable<FixedAssetSearchResult> {
    const requestId = ++this.requestSequence;
    this.isLoading.set(true);
    this.error.set(null);
    this.forbidden.set(false);
    this.expandedRowId.set(null);
    this.items.set([]);
    this.totalItems.set(0);

    return this.searchFixedAssets.execute(criteria).pipe(
      map((page) => ({ requestId, page })),
      catchError((error: unknown) => {
        if (requestId === this.requestSequence) {
          this.isLoading.set(false);
          if (error instanceof HttpErrorResponse && error.status === 403) {
            this.forbidden.set(true);
          } else {
            this.error.set(userFacingApiError(error, 'Verifica tu conexión e inténtalo nuevamente.'));
            this.notifications.error('No fue posible consultar los activos fijos.');
          }
        }
        return EMPTY;
      }),
    );
  }

  private invalidateResults(): void {
    this.requestSequence += 1;
    this.items.set([]);
    this.totalItems.set(0);
    this.error.set(null);
    this.forbidden.set(false);
    this.expandedRowId.set(null);
    this.hasSearched.set(false);
    this.isLoading.set(false);
  }
}

function toExportRow(row: FixedAsset): (string | number | null)[] {
  return [
    normalizeCsvDate(row.importDate),
    normalizeCsvDate(row.entryDate),
    row.customsDocument,
    row.customsCode,
    row.partNumber,
    row.description,
    row.tariffFraction,
    row.quantity,
    row.unit,
    row.serialNumber,
    row.brand,
    row.model,
  ];
}

function exportErrorMessage(cause: unknown, limitMessage: string): string {
  return cause instanceof Error && cause.message === limitMessage
    ? limitMessage
    : 'No fue posible exportar los activos fijos.';
}

function parseNullableStoredDate(value: string | number | null | undefined): Date | null | undefined {
  if (value === null) return null;
  if (typeof value !== 'string' || !/^\d{4}-\d{2}-\d{2}$/.test(value)) return undefined;
  const [year, month, day] = value.split('-').map(Number); const result = new Date(year, month - 1, day);
  return result.getFullYear() === year && result.getMonth() === month - 1 && result.getDate() === day ? result : undefined;
}

/** Restaura los operadores guardados; devuelve undefined si alguno no es válido. */
function parseStoredOperators(criteria: SavedCriteria): Partial<Record<FixedAssetFilterField, FixedAssetFilterOperator>> | undefined {
  const campos: FixedAssetFilterField[] = ['pedimento', 'clavePedimento', 'numeroParte', 'descripcion', 'serie', 'marca', 'modelo'];
  const resultado: Partial<Record<FixedAssetFilterField, FixedAssetFilterOperator>> = {};
  for (const campo of campos) {
    const raw = criteria[`${campo}Op`];
    if (raw === null || raw === undefined || raw === '') continue;
    const normalizado = typeof raw === 'string' ? raw.trim().toUpperCase() : raw;
    if (!isSupportedColumnFilterOperator(normalizado)) return undefined;
    resultado[campo] = normalizado;
  }
  return resultado;
}
