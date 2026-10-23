import { NgClass } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { AppAlertComponent } from '@core/ui/app-alert/app-alert.component';
import { CSV_EXPORT_LIMIT_MESSAGE, downloadCsv, loadAllCsvPages, serializeCsv } from '@core/export/csv-export';
import { NotificationService } from '@core/notifications/notification.service';
import { userFacingApiError } from '@core/http/api-error.util';
import { BusinessPartyKind, BusinessPartyRow } from '@features/catalogs/business-parties/domain/business-party.model';
import { BusinessPartyApi } from '@features/catalogs/business-parties/infrastructure/business-party.api';
import { BusinessPartyDetailDialog } from '@features/catalogs/business-parties/presentation/dialogs/business-party-detail/business-party-detail.dialog';

/** Consulta agrupada de clientes, proveedores y agentes aduanales. */
@Component({
  selector: 'app-business-parties',
  imports: [AppAlertComponent, FormsModule, MatButtonModule, MatIconModule, MatInputModule, MatPaginatorModule, MatTableModule, NgClass],
  template: `
    <div class="min-h-full bg-[#f4f7fb] text-slate-800"><main class="mx-auto w-full max-w-360 px-5 py-8 sm:px-8">
      <header class="mb-7"><p class="mb-2 text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-600">Catálogos</p>
        <h1 class="m-0 text-2xl font-semibold text-slate-900">Socios comerciales</h1>
        <p class="mt-2 text-sm text-slate-500">Consulta read-only de clientes, proveedores y agentes aduanales con los campos confirmados del maestro legacy.</p></header>
      <section class="mb-6 rounded-2xl border border-slate-200 bg-white p-3 shadow-sm" aria-label="Seleccionar socio comercial">
        <div class="flex flex-wrap gap-2">@for (tab of tabs; track tab.kind) {
          <button mat-stroked-button type="button" [class.bg-blue-50]="selected()===tab.kind" [attr.aria-pressed]="selected()===tab.kind" (click)="select(tab.kind)">{{tab.label}}</button>
        }</div>
      </section>
      <section class="mb-6 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <form class="flex flex-wrap items-end gap-4" (submit)="search()"><label class="min-w-0 flex-1 sm:min-w-70"><span class="mb-1 block text-xs font-medium">Buscar en {{ kindLabel() }}</span>
          <input matInput name="filter" [(ngModel)]="filter" [placeholder]="filterPlaceholder()" [attr.aria-label]="'Buscar en ' + kindLabel()" class="h-11 w-full rounded-xl border border-slate-200 px-4" /></label>
          <button mat-flat-button color="primary" type="submit">Consultar</button><button mat-stroked-button type="button" (click)="clear()">Limpiar</button></form>
      </section>
      <div class="mb-4 flex flex-wrap items-center gap-3">
        <button mat-stroked-button type="button" (click)="exportCsv()" [disabled]="loading() || isExporting() || total() === 0"><mat-icon>download</mat-icon>{{ isExporting() ? 'Exportando...' : 'Exportar CSV' }}</button>
        <span class="text-xs text-slate-500" aria-live="polite">{{ total() }} registros</span>
      </div>
      @if (loading()) { <div class="rounded-2xl border border-slate-200 bg-white p-6" aria-busy="true">Cargando socios comerciales…</div> }
      @else if (error()) { <app-alert kind="error" title="No pudimos cargar los socios comerciales" [message]="error()!" actionLabel="Reintentar" (action)="load()" /> }
      @else { <section class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        @if (rows().length) {
          @if (selected() !== 'agents') {
            <ul class="space-y-3 border-b border-slate-100 p-3 sm:hidden" [attr.aria-label]="'Resultados de ' + kindLabel()">
              @for (row of rows(); track row.key) {
                <li class="rounded-xl border border-slate-200 p-4">
                  <div class="flex items-start justify-between gap-3">
                    <div class="min-w-0">
                      <p class="m-0 truncate text-sm font-semibold text-slate-900">{{ row.name }}</p>
                      <p class="m-0 text-xs text-slate-500">{{ row.key }} · {{ row.fiscalId || '—' }}</p>
                    </div>
                    <button mat-stroked-button type="button" [attr.aria-label]="'Ver detalle de ' + row.name" (click)="openDetail(row)">Detalle</button>
                  </div>
                  <p class="mt-2 text-xs text-slate-600">Tipo NE: {{ row.typeNe || '—' }} · Programa: {{ row.program || '—' }}</p>
                  <p class="mt-1 text-xs text-slate-600">{{ row.phone || '—' }} · {{ row.email || '—' }}</p>
                  <p class="mt-1 text-xs text-slate-600">{{ (row.municipality || row.locality || '—') + ', ' + (row.state || '—') }}</p>
                  @if (selected() === 'providers') { <p class="mt-1 text-xs text-slate-600">Almacén: {{ row.warehouse || '—' }}</p> }
                </li>
              }
            </ul>
          }
          <div class="overflow-x-auto" [ngClass]="{'hidden sm:block': selected() !== 'agents'}">
            <table mat-table [dataSource]="rows()" class="w-full min-w-220" [attr.aria-label]="'Catálogo de ' + kindLabel()">
              <ng-container matColumnDef="key"><th mat-header-cell *matHeaderCellDef>Clave</th><td mat-cell *matCellDef="let row">{{ row.key }}</td></ng-container>
              <ng-container matColumnDef="name"><th mat-header-cell *matHeaderCellDef>Nombre</th><td mat-cell *matCellDef="let row">{{ row.name }}</td></ng-container>
              <ng-container matColumnDef="fiscalId"><th mat-header-cell *matHeaderCellDef>RFC / ID fiscal</th><td mat-cell *matCellDef="let row">{{ row.fiscalId || '—' }}</td></ng-container>
              <ng-container matColumnDef="typeNe"><th mat-header-cell *matHeaderCellDef>Tipo NE</th><td mat-cell *matCellDef="let row">{{ row.typeNe || '—' }}</td></ng-container>
              <ng-container matColumnDef="contact"><th mat-header-cell *matHeaderCellDef>Contacto</th><td mat-cell *matCellDef="let row">{{ (row.phone || '—') + ' · ' + (row.email || '—') }}</td></ng-container>
              <ng-container matColumnDef="location"><th mat-header-cell *matHeaderCellDef>Ubicación</th><td mat-cell *matCellDef="let row">{{ (row.municipality || row.locality || '—') + ', ' + (row.state || '—') }}</td></ng-container>
              <ng-container matColumnDef="warehouse"><th mat-header-cell *matHeaderCellDef>Almacén</th><td mat-cell *matCellDef="let row">{{ row.warehouse || '—' }}</td></ng-container>
              <ng-container matColumnDef="patent"><th mat-header-cell *matHeaderCellDef>Patente / agencia</th><td mat-cell *matCellDef="let row">{{ (row.patent || '—') + ' · ' + (row.agency || '—') }}</td></ng-container>
              <ng-container matColumnDef="actions"><th mat-header-cell *matHeaderCellDef>Detalle</th><td mat-cell *matCellDef="let row"><button mat-stroked-button type="button" [attr.aria-label]="'Ver detalle de ' + row.name" (click)="openDetail(row)">Ver detalle</button></td></ng-container>
              <tr mat-header-row *matHeaderRowDef="columns()"></tr><tr mat-row *matRowDef="let row; columns: columns()"></tr>
            </table>
          </div>
        } @else { <div class="p-5"><app-alert kind="info" title="Sin resultados" message="No hay registros para los filtros actuales." /></div> }
        <mat-paginator [length]="total()" [pageSize]="pageSize" [pageSizeOptions]="[20,50,100]" (page)="changePage($event)" showFirstLastButtons [attr.aria-label]="'Paginación de ' + kindLabel()" />
      </section> }
    </main></div>
  `,
})
export class BusinessPartiesPage implements OnInit {
  protected readonly tabs: { kind: BusinessPartyKind; label: string }[] = [
    { kind: 'clients', label: 'Clientes' },
    { kind: 'providers', label: 'Proveedores' },
    { kind: 'agents', label: 'Agentes aduanales' },
  ];
  protected readonly selected = signal<BusinessPartyKind>('clients');
  protected readonly rows = signal<BusinessPartyRow[]>([]);
  protected readonly total = signal(0);
  protected readonly loading = signal(false);
  protected readonly isExporting = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly columns = computed<string[]>(() => {
    if (this.selected() === 'agents') return ['key', 'name', 'fiscalId', 'patent'];
    const base = ['key', 'name', 'fiscalId', 'typeNe', 'contact', 'location'];
    return this.selected() === 'providers' ? [...base, 'warehouse', 'actions'] : [...base, 'actions'];
  });
  protected readonly kindLabel = computed(() => this.tabs.find((tab) => tab.kind === this.selected())?.label ?? 'socios comerciales');
  protected readonly filterPlaceholder = computed(() =>
    this.selected() === 'agents'
      ? 'Clave, nombre, RFC o patente'
      : 'Clave, nombre, RFC, tipo NE, domicilio, contacto, programa o almacén',
  );
  protected filter = '';
  protected page = 1;
  protected pageSize = 20;
  private request = 0;
  private readonly api = inject(BusinessPartyApi);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  ngOnInit(): void {
    const kind = this.route.snapshot.queryParamMap.get('tipo');
    if (kind === 'clientes') this.selected.set('clients');
    else if (kind === 'proveedores') this.selected.set('providers');
    else if (kind === 'agentes') this.selected.set('agents');
    this.load();
  }

  protected select(kind: BusinessPartyKind): void {
    const changed = kind !== this.selected();
    if (changed) {
      this.selected.set(kind);
      this.filter = '';
      this.page = 1;
    }
    const tipo = kind === 'clients' ? 'clientes' : kind === 'providers' ? 'proveedores' : 'agentes';
    void this.router.navigate([], { relativeTo: this.route, queryParams: { tipo }, queryParamsHandling: 'merge', replaceUrl: true });
    if (changed) this.load();
  }

  protected openDetail(row: BusinessPartyRow): void {
    const kind = this.selected();
    if (kind === 'agents') return;
    this.dialog.open(BusinessPartyDetailDialog, {
      data: { kind, row },
      width: 'min(560px, 92vw)',
      maxHeight: '85vh',
      autoFocus: 'first-tabbable',
    });
  }

  protected async exportCsv(): Promise<void> {
    const kind = this.selected();
    const filter = this.filter;
    this.isExporting.set(true);
    try {
      const rows = await loadAllCsvPages((page, pageSize) => firstValueFrom(this.api.search(kind, filter, page, pageSize)));
      if (!rows.length) {
        this.notifications.info('No hay registros para exportar.');
        return;
      }
      if (kind === 'agents') {
        downloadCsv('agentes-aduanales.csv', serializeCsv(
          ['Clave', 'Nombre', 'RFC', 'Patente', 'Agencia aduanal'],
          rows.map((row) => [row.key, row.name, row.fiscalId, row.patent, row.agency]),
        ));
        return;
      }
      if (kind === 'clients') {
        downloadCsv('clientes.csv', serializeCsv(CLIENT_CSV_HEADERS, rows.map(clientCsvValues)));
        return;
      }
      downloadCsv('proveedores.csv', serializeCsv(PROVIDER_CSV_HEADERS, rows.map(providerCsvValues)));
    } catch (cause) {
      this.notifications.error(cause instanceof Error && cause.message === CSV_EXPORT_LIMIT_MESSAGE
        ? CSV_EXPORT_LIMIT_MESSAGE
        : 'No fue posible exportar los socios comerciales.');
    } finally {
      this.isExporting.set(false);
    }
  }

  protected search(): void {
    this.page = 1;
    this.load();
  }

  protected clear(): void {
    this.filter = '';
    this.search();
  }

  protected changePage(event: PageEvent): void {
    this.page = event.pageIndex + 1;
    this.pageSize = event.pageSize;
    this.load();
  }

  protected load(): void {
    const id = ++this.request;
    this.loading.set(true);
    this.error.set(null);
    this.api.search(this.selected(), this.filter, this.page, this.pageSize).subscribe({
      next: (response) => {
        if (id !== this.request) return;
        this.rows.set(response.items);
        this.total.set(response.total);
        this.loading.set(false);
      },
      error: (cause) => {
        if (id !== this.request) return;
        this.loading.set(false);
        this.error.set(userFacingApiError(cause, 'Verifica tu conexión e inténtalo nuevamente.'));
      },
    });
  }
}

const CLIENT_CSV_HEADERS = [
  'Clave', 'Nombre', 'Id fiscal', 'Tipo NE', 'Programa', 'Calle y número exterior', 'No. interior',
  'Colonia', 'Código', 'Código postal', 'Localidad', 'Municipio', 'Entidad', 'País', 'Teléfono',
  'Correo', 'Fax', 'Apellido paterno', 'Apellido materno', 'Calle', 'Referencia', 'Tipo de identificador',
];

const PROVIDER_CSV_HEADERS = [
  'Almacén', 'Clave', 'Nombre', 'Id fiscal', 'Tipo NE', 'Programa', 'Calle y número exterior',
  'No. interior', 'Colonia', 'Código', 'Localidad', 'Municipio', 'Entidad', 'País', 'Teléfono', 'Correo', 'Fax',
];

function clientCsvValues(row: BusinessPartyRow) {
  return [
    row.key, row.name, row.fiscalId, row.typeNe, row.program, row.streetAndNumber, row.interiorNumber,
    row.neighborhood, row.code, row.postalCode, row.locality, row.municipality, row.state, row.country,
    row.phone, row.email, row.fax, row.paternalSurname, row.maternalSurname, row.street, row.reference, row.idType,
  ];
}

function providerCsvValues(row: BusinessPartyRow) {
  return [
    row.warehouse, row.key, row.name, row.fiscalId, row.typeNe, row.program, row.streetAndNumber,
    row.interiorNumber, row.neighborhood, row.code, row.locality, row.municipality, row.state, row.country,
    row.phone, row.email, row.fax,
  ];
}
