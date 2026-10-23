import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of } from 'rxjs';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { NotificationService } from '@core/notifications/notification.service';
import { BusinessPartyKind } from '@features/catalogs/business-parties/domain/business-party.model';
import { BusinessPartyApi } from '@features/catalogs/business-parties/infrastructure/business-party.api';
import { BusinessPartyDetailDialog } from '@features/catalogs/business-parties/presentation/dialogs/business-party-detail/business-party-detail.dialog';
import { BusinessPartiesPage } from './business-parties.page';

const row = {
  key: 'K1',
  name: 'Party',
  fiscalId: 'RFC1',
  typeNe: '01',
  program: 'IMMEX',
  streetAndNumber: 'CALLE 1',
  interiorNumber: 3,
  neighborhood: 'CENTRO',
  code: 'CP1',
  locality: 'LOCALIDAD',
  municipality: 'MUNICIPIO',
  state: 'ENTIDAD',
  country: 'MEXICO',
  phone: '555',
  email: 'a@example.test',
  fax: '556',
  paternalSurname: 'PATERNO',
  maternalSurname: 'MATERNO',
  street: 'CALLE',
  reference: 'REF',
  idType: 'RFC',
  postalCode: '21000',
  warehouseKey: 2,
  warehouse: 'ALMACEN',
  patent: 'P1',
  agency: 'Agency',
};
const kinds: { query: string; kind: BusinessPartyKind }[] = [
  { query: 'clientes', kind: 'clients' },
  { query: 'proveedores', kind: 'providers' },
  { query: 'agentes', kind: 'agents' },
];

describe('BusinessPartiesPage', () => {
  afterEach(() => vi.unstubAllGlobals());

  function createPage(tipo: string | null = null) {
    const api = { search: vi.fn(() => of({ items: [row], total: 1, pagina: 1, tamano: 20 })) };
    const dialog = { open: vi.fn() };
    const route = { snapshot: { queryParamMap: convertToParamMap(tipo === null ? {} : { tipo }) } };
    const router = { navigate: vi.fn() };
    TestBed.configureTestingModule({
      imports: [BusinessPartiesPage],
      providers: [
        { provide: BusinessPartyApi, useValue: api },
        { provide: MatDialog, useValue: dialog },
        { provide: ActivatedRoute, useValue: route },
        { provide: Router, useValue: router },
        { provide: NotificationService, useValue: { info: vi.fn(), error: vi.fn() } },
      ],
    });
    const fixture = TestBed.createComponent(BusinessPartiesPage);
    fixture.detectChanges();
    return { fixture, api, dialog, router };
  }

  it.each(kinds)('selecciona query tipo=$query', ({ query, kind }) => {
    const { fixture, api } = createPage(query);
    const page = fixture.componentInstance as unknown as { selected(): BusinessPartyKind };
    expect(page.selected()).toBe(kind);
    expect(api.search).toHaveBeenCalledWith(kind, '', 1, 20);
  });

  it('usa clientes como fallback para tipo inválido', () => {
    const { fixture, api } = createPage('desconocido');
    expect((fixture.componentInstance as unknown as { selected(): BusinessPartyKind }).selected()).toBe('clients');
    expect(api.search).toHaveBeenCalledWith('clients', '', 1, 20);
  });

  it('sincroniza tipo preservando query params ajenos', () => {
    const { fixture, router } = createPage();
    const page = fixture.componentInstance as unknown as { select(kind: BusinessPartyKind): void };
    page.select('agents');
    expect(router.navigate).toHaveBeenCalledWith([], {
      relativeTo: expect.anything(),
      queryParams: { tipo: 'agentes' },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  });

  it.each([
    { kind: 'clients' as const, expected: ['key', 'name', 'fiscalId', 'typeNe', 'contact', 'location', 'actions'] },
    { kind: 'providers' as const, expected: ['key', 'name', 'fiscalId', 'typeNe', 'contact', 'location', 'warehouse', 'actions'] },
    { kind: 'agents' as const, expected: ['key', 'name', 'fiscalId', 'patent'] },
  ])('expone columnas confirmadas para $kind', ({ kind, expected }) => {
    const { fixture } = createPage();
    const page = fixture.componentInstance as unknown as { selected: { set(value: BusinessPartyKind): void }; columns(): string[] };
    page.selected.set(kind);
    expect(page.columns()).toEqual(expected);
  });

  it.each(['clients', 'providers'] as const)('abre detalle accesible para %s', (kind) => {
    const { fixture, dialog } = createPage();
    const page = fixture.componentInstance as unknown as {
      selected: { set(value: BusinessPartyKind): void };
      openDetail(value: typeof row): void;
    };
    page.selected.set(kind);
    page.openDetail(row);
    expect(dialog.open).toHaveBeenCalledWith(BusinessPartyDetailDialog, expect.objectContaining({
      data: { kind, row },
      autoFocus: 'first-tabbable',
    }));
  });

  it('no abre detalle para agentes aduanales', () => {
    const { fixture, dialog } = createPage('agentes');
    const page = fixture.componentInstance as unknown as { openDetail(value: typeof row): void };
    page.openDetail(row);
    expect(dialog.open).not.toHaveBeenCalled();
  });

  it.each([
    { kind: 'clients' as const, filename: 'clientes.csv' },
    { kind: 'providers' as const, filename: 'proveedores.csv' },
    { kind: 'agents' as const, filename: 'agentes-aduanales.csv' },
  ])('exporta $kind con filtro y page size 100', async ({ kind, filename }) => {
    const createObjectURL = vi.fn(() => 'blob:csv');
    vi.stubGlobal('URL', { createObjectURL, revokeObjectURL: vi.fn() });
    const downloaded: string[] = [];
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (this: HTMLAnchorElement) {
      downloaded.push(this.download);
    });
    const { fixture, api } = createPage();
    const page = fixture.componentInstance as unknown as {
      selected: { set(value: BusinessPartyKind): void };
      filter: string;
      page: number;
      pageSize: number;
      rows(): unknown[];
      exportCsv(): Promise<void>;
    };
    page.selected.set(kind);
    page.filter = ' keep ';
    page.page = 3;
    page.pageSize = 50;
    await page.exportCsv();
    expect(api.search).toHaveBeenLastCalledWith(kind, ' keep ', 1, 100);
    expect(downloaded).toEqual([filename]);
    expect(page.page).toBe(3);
    expect(page.pageSize).toBe(50);
    expect(page.rows()).toEqual([row]);
  });

  it('exporta los campos confirmados de clientes en CSV', async () => {
    let blob: Blob | undefined;
    vi.stubGlobal('URL', {
      createObjectURL: vi.fn((value: Blob) => {
        blob = value;
        return 'blob:csv';
      }),
      revokeObjectURL: vi.fn(),
    });
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
    const { fixture } = createPage();
    const page = fixture.componentInstance as unknown as { exportCsv(): Promise<void> };
    await page.exportCsv();
    expect(blob).toBeDefined();
    const content = await blob!.text();
    expect(content).toContain('Programa');
    expect(content).toContain('Código postal');
    expect(content).toContain('Tipo de identificador');
    expect(content).toContain('21000');
  });
});
