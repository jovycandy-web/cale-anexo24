import { Component, OnInit, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { Material } from '@features/catalogs/materials/domain/models/material.model';
import { SearchMaterialsUseCase } from '@features/catalogs/materials/application/use-cases/search-materials.use-case';
import { NotificationService } from '@core/notifications/notification.service';
import { userFacingApiError } from '@core/http/api-error.util';
import { AppAlertComponent } from '@core/ui/app-alert/app-alert.component';
import { CSV_EXPORT_LIMIT_MESSAGE, downloadCsv, loadAllCsvPages, serializeCsv } from '@core/export/csv-export';

/**
 * Consulta del catálogo de materiales (RF-010).
 */
@Component({
  imports: [AppAlertComponent, FormsModule, MatButtonModule, MatIconModule, MatInputModule, MatPaginatorModule, MatTableModule],
  selector: 'app-materiales',
  template: `
    <div class="min-h-full bg-[#f4f7fb] text-slate-800">
      <main class="mx-auto w-full max-w-360 px-5 py-8 sm:px-8">
        <div class="mb-7 flex flex-wrap items-end justify-between gap-3">
          <div>
            <p class="mb-2 text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-600">Catálogos</p>
            <h1 class="m-0 text-2xl font-semibold tracking-tight text-slate-900">Materiales</h1>
            <p class="mt-2 text-sm text-slate-500">Consulta y administra el catálogo de materiales.</p>
          </div>
        </div>

        <section class="mb-6 rounded-2xl border border-slate-200/80 bg-white p-5 shadow-[0_4px_18px_rgb(15_23_42/4%)]" aria-labelledby="filtros-materiales">
          <h2 id="filtros-materiales" class="sr-only">Filtros de materiales</h2>
          <form (submit)="search()" class="flex flex-wrap items-end gap-4">
            <label class="min-w-0 flex-1 sm:min-w-70">
              <span class="mb-1 block text-xs font-medium text-slate-700">Fracción, descripción, unidad o número de parte</span>
              <input
                matInput
                name="filter"
                [(ngModel)]="filter"
                (ngModelChange)="onFilterChange()"
                placeholder="Buscar en el catálogo"
                class="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:bg-white focus:ring-4 focus:ring-blue-500/10"
              />
            </label>
            <button mat-stroked-button type="button" class="h-11 min-w-24 rounded-xl!" (click)="clearFilter()">Limpiar</button>
          </form>
        </section>

        <div class="mb-4 flex flex-wrap items-center gap-3">
          <button mat-stroked-button type="button" class="h-10 rounded-xl!" (click)="loadMaterials()">
            <mat-icon>refresh</mat-icon>
            Actualizar
          </button>
          <span class="text-xs text-slate-500" aria-live="polite">{{ totalItems() }} registros encontrados</span>
          <button mat-stroked-button type="button" class="h-10 rounded-xl!" (click)="exportCsv()" [disabled]="isLoading() || isExporting() || totalItems() === 0"><mat-icon>download</mat-icon>{{ isExporting() ? 'Exportando...' : 'Exportar CSV' }}</button>
        </div>

        @if (isLoading()) {
          <div class="space-y-3 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm" aria-label="Cargando materiales" aria-busy="true">
            @for (row of loadingRows; track row) {
              <div class="h-10 animate-pulse rounded-lg bg-slate-100"></div>
            }
          </div>
        } @else if (error()) {
          <app-alert kind="error" title="No pudimos cargar los materiales" [message]="error()!" actionLabel="Reintentar" (action)="loadMaterials()" />
        } @else {
          <div class="overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-[0_4px_18px_rgb(15_23_42/4%)]">
            @if (items().length > 0) {
              <div class="overflow-x-auto">
                <table mat-table [dataSource]="items()" class="w-full" aria-label="Catálogo de materiales">
                  <ng-container matColumnDef="tariffFraction">
                    <th mat-header-cell *matHeaderCellDef class="w-32">Fracción</th>
                    <td mat-cell *matCellDef="let m">{{ m.tariffFraction || '-' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="description">
                    <th mat-header-cell *matHeaderCellDef>Descripción</th>
                    <td mat-cell *matCellDef="let m" class="max-w-90">{{ m.description }}</td>
                  </ng-container>
                  <ng-container matColumnDef="umc">
                    <th mat-header-cell *matHeaderCellDef class="w-28">Unidad TIGIE</th>
                    <td mat-cell *matCellDef="let m">{{ m.umc || '-' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="unit">
                    <th mat-header-cell *matHeaderCellDef class="w-28">UMC</th>
                    <td mat-cell *matCellDef="let m">{{ m.unit || '-' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="partNumber">
                    <th mat-header-cell *matHeaderCellDef class="w-32">Número parte</th>
                    <td mat-cell *matCellDef="let m" class="font-medium text-slate-700">{{ m.partNumber }}</td>
                  </ng-container>
                  <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                  <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
                </table>
              </div>
            } @else {
              <div class="p-4">
                <app-alert
                  kind="info"
                  title="No encontramos materiales"
                  message="Prueba con otra búsqueda o limpia los filtros."
                  [actionLabel]="filter ? 'Limpiar búsqueda' : ''"
                  (action)="clearFilter()"
                />
              </div>
            }
            <mat-paginator
              [length]="totalItems()"
              [pageIndex]="currentPage - 1"
              [pageSize]="pageSize"
              [pageSizeOptions]="[20, 50, 100]"
              (page)="changePage($event)"
              showFirstLastButtons
              aria-label="Paginación de materiales"
            ></mat-paginator>
          </div>
        }
      </main>
    </div>
  `,
})
export class MaterialListPage implements OnInit {
  private static readonly FILTER_DEBOUNCE_MS = 400;
  protected displayedColumns = ['tariffFraction', 'description', 'umc', 'unit', 'partNumber'];
  protected items = signal<Material[]>([]);
  protected totalItems = signal(0);
  protected isLoading = signal(false);
  protected error = signal<string | null>(null);
  protected readonly isExporting = signal(false);
  protected readonly loadingRows = [1, 2, 3, 4, 5];
  protected filter = '';
  protected currentPage = 1;
  protected pageSize = 20;

  private requestSequence = 0;
  private filterDebounce?: ReturnType<typeof setTimeout>;
  private readonly searchMaterials = inject(SearchMaterialsUseCase);
  private readonly notifications = inject(NotificationService);

  ngOnInit(): void {
    this.loadMaterials();
  }

  protected search(): void {
    this.cancelScheduledFilter();
    this.currentPage = 1;
    this.loadMaterials();
  }

  /** Programa una consulta al terminar de escribir, sin requerir un botón. */
  protected onFilterChange(): void {
    this.currentPage = 1;
    this.cancelScheduledFilter();
    this.filterDebounce = setTimeout(() => this.loadMaterials(), MaterialListPage.FILTER_DEBOUNCE_MS);
  }

  protected clearFilter(): void {
    this.filter = '';
    this.search();
  }

  private cancelScheduledFilter(): void {
    if (this.filterDebounce !== undefined) clearTimeout(this.filterDebounce);
    this.filterDebounce = undefined;
  }

  protected changePage(event: PageEvent): void {
    this.currentPage = event.pageIndex + 1;
    this.pageSize = event.pageSize;
    this.loadMaterials();
  }

  protected async exportCsv(): Promise<void> {
    const filter = this.filter;
    this.isExporting.set(true);
    try {
      const rows = await loadAllCsvPages((page, pageSize) => firstValueFrom(this.searchMaterials.execute({ filter, page, pageSize })));
      if (!rows.length) { this.notifications.info('No hay registros para exportar.'); return; }
      const csv = serializeCsv(
        ['Fracción', 'Descripción', 'Unidad TIGIE', 'UMC', 'Número parte'],
        rows.map((row) => [row.tariffFraction, row.description, row.umc, row.unit, row.partNumber]),
      );
      downloadCsv('materiales.csv', csv);
    } catch (cause) {
      this.notifications.error(cause instanceof Error && cause.message === CSV_EXPORT_LIMIT_MESSAGE ? CSV_EXPORT_LIMIT_MESSAGE : 'No fue posible exportar los materiales.');
    } finally { this.isExporting.set(false); }
  }

  protected loadMaterials(): void {
    const requestId = ++this.requestSequence;
    this.isLoading.set(true);
    this.error.set(null);
    this.items.set([]);
    this.searchMaterials.execute({ filter: this.filter, page: this.currentPage, pageSize: this.pageSize }).subscribe({
      next: (resp) => {
        if (requestId !== this.requestSequence) return;
        this.items.set(resp.items);
        this.totalItems.set(resp.total);
        this.isLoading.set(false);
      },
      error: (error: unknown) => {
        if (requestId !== this.requestSequence) return;
        this.isLoading.set(false);
        this.error.set(userFacingApiError(error, 'Verifica tu conexión e inténtalo nuevamente.'));
        this.notifications.error('No fue posible consultar el catálogo de materiales.');
      },
    });
  }
}
