import { Component, OnInit, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { userFacingApiError } from '@core/http/api-error.util';
import { NotificationService } from '@core/notifications/notification.service';
import { AppAlertComponent } from '@core/ui/app-alert/app-alert.component';
import { AppDetailPanelComponent } from '@core/ui/app-detail-panel/app-detail-panel.component';
import { CSV_EXPORT_LIMIT_MESSAGE, downloadCsv, loadAllCsvPages, serializeCsv } from '@core/export/csv-export';
import { Product } from '@features/catalogs/products/domain/models/product.model';
import { SearchProductsUseCase } from '@features/catalogs/products/application/use-cases/search-products.use-case';

/** Consulta del catálogo de productos terminados. */
@Component({
  imports: [AppAlertComponent, AppDetailPanelComponent, FormsModule, MatButtonModule, MatIconModule, MatInputModule, MatPaginatorModule, MatTableModule],
  selector: 'app-product-list',
  template: `
    <div class="min-h-full bg-[#f4f7fb] text-slate-800">
      <main class="mx-auto w-full max-w-360 px-5 py-8 sm:px-8">
        <header class="mb-7">
          <p class="mb-2 text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-600">Catálogos</p>
          <h1 class="m-0 text-2xl font-semibold tracking-tight text-slate-900">Productos</h1>
          <p class="mt-2 text-sm text-slate-500">Consulta del catálogo de productos terminados.</p>
        </header>

        <section class="mb-6 rounded-2xl border border-slate-200/80 bg-white p-5 shadow-[0_4px_18px_rgb(15_23_42/4%)]" aria-labelledby="product-filters">
          <h2 id="product-filters" class="sr-only">Filtros de productos</h2>
          <form class="flex flex-wrap items-end gap-4" (ngSubmit)="search()">
            <label class="min-w-0 flex-1 sm:min-w-70">
              <span class="mb-1 block text-xs font-medium text-slate-700">Clave, cliente, descripción, fracción, NICO, tipo, unidad, almacén o auxiliar</span>
              <input matInput name="filter" [(ngModel)]="filter" placeholder="Buscar en el catálogo" class="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:bg-white focus:ring-4 focus:ring-blue-500/10" />
            </label>
            <button mat-flat-button color="primary" type="submit" class="h-11 min-w-28 rounded-xl!">Consultar</button>
            <button mat-stroked-button type="button" class="h-11 min-w-24 rounded-xl!" (click)="clearFilter()">Limpiar</button>
          </form>
        </section>

        <div class="mb-4 flex flex-wrap items-center gap-3">
          <button mat-stroked-button type="button" class="h-10 rounded-xl!" (click)="loadProducts()" aria-label="Actualizar catálogo de productos">
            <mat-icon>refresh</mat-icon> Actualizar
          </button>
          <span class="text-xs text-slate-500" aria-live="polite">{{ totalItems() }} registros encontrados</span>
          <button mat-stroked-button type="button" class="h-10 rounded-xl!" (click)="exportCsv()" [disabled]="isLoading() || isExporting() || totalItems() === 0"><mat-icon>download</mat-icon>{{ isExporting() ? 'Exportando...' : 'Exportar CSV' }}</button>
        </div>

        @if (isLoading()) {
          <div class="space-y-3 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm" aria-label="Cargando productos" aria-busy="true">
            @for (row of loadingRows; track row) { <div class="h-10 animate-pulse rounded-lg bg-slate-100"></div> }
          </div>
        } @else if (error()) {
          <app-alert kind="error" title="No pudimos cargar los productos" [message]="error()!" actionLabel="Reintentar" (action)="loadProducts()" />
        } @else {
          <section class="overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-[0_4px_18px_rgb(15_23_42/4%)]" aria-label="Resultados de productos">
            @if (items().length > 0) {
              <div class="overflow-x-auto">
                <table mat-table [dataSource]="items()" multiTemplateDataRows class="w-full min-w-300" aria-label="Catálogo de productos">
                  <ng-container matColumnDef="partNumber">
                    <th mat-header-cell *matHeaderCellDef class="w-32">Clave</th>
                    <td mat-cell *matCellDef="let product" class="font-medium text-slate-700">{{ product.partNumber }}</td>
                  </ng-container>
                  <ng-container matColumnDef="clientCode">
                    <th mat-header-cell *matHeaderCellDef class="w-32">Código cliente</th>
                    <td mat-cell *matCellDef="let product">{{ product.clientCode || '—' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="description">
                    <th mat-header-cell *matHeaderCellDef>Descripción</th>
                    <td mat-cell *matCellDef="let product" class="max-w-90">{{ product.description }}</td>
                  </ng-container>
                  <ng-container matColumnDef="tariffFraction">
                    <th mat-header-cell *matHeaderCellDef class="w-32">Fracción TIGIE</th>
                    <td mat-cell *matCellDef="let product">{{ product.tariffFraction }}</td>
                  </ng-container>
                  <ng-container matColumnDef="nico">
                    <th mat-header-cell *matHeaderCellDef class="w-24">NICO</th>
                    <td mat-cell *matCellDef="let product">{{ product.nico || '—' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="commercialUnit">
                    <th mat-header-cell *matHeaderCellDef class="w-24">UMC</th>
                    <td mat-cell *matCellDef="let product">{{ product.commercialUnit }}</td>
                  </ng-container>
                  <ng-container matColumnDef="tariffUnit">
                    <th mat-header-cell *matHeaderCellDef class="w-28">Unidad tarifa</th>
                    <td mat-cell *matCellDef="let product">{{ product.tariffUnit }}</td>
                  </ng-container>
                  <ng-container matColumnDef="type">
                    <th mat-header-cell *matHeaderCellDef class="w-32">Tipo</th>
                    <td mat-cell *matCellDef="let product">{{ product.type || '—' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="warehouse">
                    <th mat-header-cell *matHeaderCellDef class="w-32">Almacén</th>
                    <td mat-cell *matCellDef="let product">{{ product.warehouse || product.warehouseKey || '—' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="auxiliary">
                    <th mat-header-cell *matHeaderCellDef class="w-28">Auxiliar</th>
                    <td mat-cell *matCellDef="let product">{{ product.auxiliary || '—' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="actions">
                    <th mat-header-cell *matHeaderCellDef class="w-20"><span class="sr-only">Detalle</span></th>
                    <td mat-cell *matCellDef="let product" class="text-right">
                      <button mat-icon-button type="button" [attr.aria-label]="'Ver detalle de ' + product.partNumber" [attr.aria-expanded]="isSelected(product)" (click)="$event.stopPropagation(); selectProduct(product)"><mat-icon>visibility</mat-icon></button>
                    </td>
                  </ng-container>
                  <ng-container matColumnDef="detail">
                    <td mat-cell *matCellDef="let product" [attr.colspan]="displayedColumns.length" class="p-0!">
                      @if (isSelected(product)) {
                        <app-detail-panel [title]="'Detalle de ' + product.partNumber" [subtitle]="product.description || 'Sin descripción'" closeLabel="Cerrar detalle del producto" (closed)="closeDetail()">
                          <dl class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Código cliente</dt><dd class="m-0 text-sm text-slate-800">{{ product.clientCode || '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Fracción TIGIE</dt><dd class="m-0 text-sm text-slate-800">{{ product.tariffFraction || '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">NICO</dt><dd class="m-0 text-sm text-slate-800">{{ product.nico || '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Unidad comercial</dt><dd class="m-0 text-sm text-slate-800">{{ product.commercialUnit || '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Unidad tarifaria</dt><dd class="m-0 text-sm text-slate-800">{{ product.tariffUnit || '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Tipo</dt><dd class="m-0 text-sm text-slate-800">{{ product.type || '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Almacén</dt><dd class="m-0 text-sm text-slate-800">{{ product.warehouse || '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Clave de almacén</dt><dd class="m-0 text-sm text-slate-800">{{ product.warehouseKey ?? '—' }}</dd></div>
                            <div><dt class="text-xs font-medium uppercase tracking-wide text-slate-500">Auxiliar</dt><dd class="m-0 text-sm text-slate-800">{{ product.auxiliary || '—' }}</dd></div>
                          </dl>
                        </app-detail-panel>
                      }
                    </td>
                  </ng-container>
                  <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                  <tr mat-row *matRowDef="let row; columns: displayedColumns" class="cursor-pointer hover:bg-slate-50" [class.bg-blue-50/40]="isSelected(row)" (click)="toggleDetail(row)"></tr>
                  <tr mat-row *matRowDef="let row; columns: ['detail']" [class.hidden]="!isSelected(row)"></tr>
                </table>
              </div>
            } @else {
              <div class="p-4">
                <app-alert kind="info" title="No encontramos productos" message="Prueba con otra búsqueda o limpia el filtro." [actionLabel]="filter ? 'Limpiar búsqueda' : ''" (action)="clearFilter()" />
              </div>
            }
            <mat-paginator [length]="totalItems()" [pageSize]="pageSize" [pageSizeOptions]="[20, 50, 100]" (page)="changePage($event)" showFirstLastButtons aria-label="Paginación de productos" />
          </section>
        }
      </main>
    </div>
  `,
})
export class ProductListPage implements OnInit {
  protected readonly displayedColumns = ['partNumber', 'clientCode', 'description', 'tariffFraction', 'nico', 'commercialUnit', 'tariffUnit', 'type', 'warehouse', 'auxiliary', 'actions'];
  protected readonly items = signal<Product[]>([]);
  protected readonly totalItems = signal(0);
  protected readonly isLoading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly isExporting = signal(false);
  protected readonly selectedProduct = signal<Product | null>(null);
  protected readonly loadingRows = [1, 2, 3, 4, 5];
  protected filter = '';
  protected currentPage = 1;
  protected pageSize = 20;

  private requestSequence = 0;
  private readonly searchProducts = inject(SearchProductsUseCase);
  private readonly notifications = inject(NotificationService);

  ngOnInit(): void {
    this.loadProducts();
  }

  protected search(): void {
    this.currentPage = 1;
    this.loadProducts();
  }

  protected clearFilter(): void {
    this.filter = '';
    this.search();
  }

  protected changePage(event: PageEvent): void {
    this.currentPage = event.pageIndex + 1;
    this.pageSize = event.pageSize;
    this.loadProducts();
  }

  protected selectProduct(product: Product): void {
    this.selectedProduct.set(product);
  }

  /** Alterna el detalle bajo la fila; mantener el contexto evita perder la posición. */
  protected toggleDetail(product: Product): void {
    this.selectedProduct.set(this.isSelected(product) ? null : product);
  }

  protected isSelected(product: Product): boolean {
    return this.selectedProduct()?.id === product.id;
  }

  protected closeDetail(): void {
    this.selectedProduct.set(null);
  }

  protected async exportCsv(): Promise<void> {
    const filter = this.filter;
    this.isExporting.set(true);
    try {
      const rows = await loadAllCsvPages((page, pageSize) => firstValueFrom(this.searchProducts.execute({ filter, page, pageSize })));
      if (!rows.length) { this.notifications.info('No hay registros para exportar.'); return; }
      const csv = serializeCsv(
        ['Clave', 'Código cliente', 'Descripción', 'Fracción TIGIE', 'NICO', 'Unidad comercial', 'Unidad tarifaria', 'Tipo', 'Almacén', 'Auxiliar'],
        rows.map((row) => [row.partNumber, row.clientCode, row.description, row.tariffFraction, row.nico, row.commercialUnit, row.tariffUnit, row.type, row.warehouse, row.auxiliary]),
      );
      downloadCsv('productos.csv', csv);
    } catch (cause) {
      this.notifications.error(cause instanceof Error && cause.message === CSV_EXPORT_LIMIT_MESSAGE ? CSV_EXPORT_LIMIT_MESSAGE : 'No fue posible exportar los productos.');
    } finally { this.isExporting.set(false); }
  }

  protected loadProducts(): void {
    const requestId = ++this.requestSequence;
    this.isLoading.set(true);
    this.error.set(null);
    this.items.set([]);
    this.selectedProduct.set(null);
    this.searchProducts.execute({ filter: this.filter, page: this.currentPage, pageSize: this.pageSize }).subscribe({
      next: (page) => {
        if (requestId !== this.requestSequence) return;
        this.items.set(page.items);
        this.totalItems.set(page.total);
        this.isLoading.set(false);
      },
      error: (error: unknown) => {
        if (requestId !== this.requestSequence) return;
        this.isLoading.set(false);
        this.error.set(userFacingApiError(error, 'Verifica tu conexión e inténtalo nuevamente.'));
        this.notifications.error('No fue posible consultar el catálogo de productos.');
      },
    });
  }
}
