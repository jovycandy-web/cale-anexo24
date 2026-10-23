import { readFile } from 'node:fs/promises';
import { test, expect, type Page } from '@playwright/test';
import {
  assertNoRuntimeFailures,
  assertNoUnexpectedApiRequests,
  authenticateWithPermissions,
  installRuntimeMonitors,
  installSyntheticApiIsolation,
} from './fixtures';

const FIXED_ASSET_ROW = {
  partidaEntradaId: '101',
  importacionId: '202',
  pedimento: 'DOC-1',
  clavePedimento: 'A1',
  fechaImportacion: '2026-07-19T00:00:00',
  numeroParte: 'PART-1',
  descripcion: 'Equipo de prueba',
  fraccion: '1234.56',
  cantidad: '0002.5000',
  unidad: 'PZA',
  numeroSerie: 'SER-1',
  marca: 'Marca',
  modelo: 'Modelo',
};

test.beforeEach(async ({ page }) => {
  await installSyntheticApiIsolation(page);
});

test.afterEach(async ({ page }) => {
  assertNoUnexpectedApiRequests(page);
});

async function mockSavedQueries(page: Page): Promise<void> {
  await page.route('**/api/v1/consultas-guardadas**', (route) =>
    route.fulfill({ status: 200, contentType: 'application/json', body: '[]' }));
}

function pageBody(url: URL): string {
  const pagina = Number(url.searchParams.get('pagina') ?? '1');
  return JSON.stringify({
    items: [{ ...FIXED_ASSET_ROW, numeroParte: pagina === 2 ? 'PART-2' : 'PART-1' }],
    total: 21,
    pagina,
    tamano: Number(url.searchParams.get('tamano') ?? '20'),
  });
}

test('Activos fijos mantiene el operador server-side al paginar y abre el detalle sin llamadas extra', async ({ page }) => {
  const requests: URL[] = [];
  await mockSavedQueries(page);
  await page.route('**/api/v1/operaciones/activos-fijos**', async (route) => {
    requests.push(new URL(route.request().url()));
    await route.fulfill({ status: 200, contentType: 'application/json', body: pageBody(new URL(route.request().url())) });
  });
  const monitors = installRuntimeMonitors(page);
  await authenticateWithPermissions(page, ['OPERACIONES_CONSULTAR']);

  await page.goto('/operaciones/activos-fijos');
  await expect(page.getByRole('heading', { name: 'Activos fijos', exact: true })).toBeVisible();

  await page.getByRole('button', { name: 'Mostrar filtros por columna' }).click();
  const description = page.getByLabel('Descripción', { exact: true });
  await description.fill('Equipo');
  await page.getByLabel('Operador para Descripción').selectOption('CT');

  await expect.poll(() => requests.some((url) => url.searchParams.get('descripcionOp') === 'CT')).toBe(true);
  const filtered = requests.find((url) => url.searchParams.get('descripcionOp') === 'CT')!;
  expect(filtered.searchParams.get('descripcion')).toBe('Equipo');

  const callsBeforeDetail = requests.length;
  await page.getByRole('button', { name: 'Ver detalle de la partida PART-1' }).click();
  const detail = page.locator('#fixed-asset-detail');
  await expect(detail).toBeVisible();
  await expect(detail).toContainText('Importación');
  await expect(detail).toContainText('0002.5000');
  expect(requests.length).toBe(callsBeforeDetail);

  await page.getByRole('button', { name: /next page/i }).click();
  await expect.poll(() => requests.some((url) => url.searchParams.get('pagina') === '2')).toBe(true);
  const secondPage = requests.find((url) => url.searchParams.get('pagina') === '2')!;
  expect(secondPage.searchParams.get('descripcion')).toBe('Equipo');
  expect(secondPage.searchParams.get('descripcionOp')).toBe('CT');
  await expect(detail).toBeHidden();

  await assertNoRuntimeFailures(monitors);
});

test('Activos fijos descarga un XLSX con contenido real y conserva CSV', async ({ page }) => {
  await mockSavedQueries(page);
  await page.route('**/api/v1/operaciones/activos-fijos**', (route) =>
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ items: [FIXED_ASSET_ROW], total: 1, pagina: 1, tamano: 20 }),
    }));
  const monitors = installRuntimeMonitors(page);
  await authenticateWithPermissions(page, ['OPERACIONES_CONSULTAR']);

  await page.goto('/operaciones/activos-fijos');
  await expect(page.getByRole('button', { name: 'Exportar XLSX' })).toBeEnabled();
  await expect(page.getByRole('button', { name: 'Exportar CSV' })).toBeEnabled();

  const [xlsx] = await Promise.all([
    page.waitForEvent('download'),
    page.getByRole('button', { name: 'Exportar XLSX' }).click(),
  ]);
  expect(xlsx.suggestedFilename()).toBe('activos-fijos.xlsx');
  const xlsxPath = await xlsx.path();
  const xlsxBytes = await readFile(xlsxPath!);
  expect(xlsxBytes.subarray(0, 2).toString('latin1')).toBe('PK');
  const xlsxText = xlsxBytes.toString('utf8');
  expect(xlsxText).toContain('Equipo de prueba');
  expect(xlsxText).toContain('Descripción');

  const [csv] = await Promise.all([
    page.waitForEvent('download'),
    page.getByRole('button', { name: 'Exportar CSV' }).click(),
  ]);
  expect(csv.suggestedFilename()).toBe('activos-fijos.csv');

  await assertNoRuntimeFailures(monitors);
});

test('Activos fijos distingue el permiso insuficiente devuelto por la API', async ({ page }) => {
  await mockSavedQueries(page);
  await page.route('**/api/v1/operaciones/activos-fijos**', (route) =>
    route.fulfill({
      status: 403,
      contentType: 'application/json',
      body: JSON.stringify({ code: 'ACCESO_DENEGADO', message: 'Permiso insuficiente' }),
    }));
  const monitors = installRuntimeMonitors(page, { ignoredConsoleErrorFragments: ['403'] });
  await authenticateWithPermissions(page, ['OPERACIONES_CONSULTAR']);

  await page.goto('/operaciones/activos-fijos');
  await expect(page.getByText('No cuentas con permiso para consultar activos fijos')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Reintentar' })).toHaveCount(0);

  await assertNoRuntimeFailures({ ...monitors, serverErrors: [] });
});
