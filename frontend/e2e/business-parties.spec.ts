import { test, expect, type Download, type Page } from '@playwright/test';
import {
  assertNoRuntimeFailures,
  assertNoUnexpectedApiRequests,
  authenticateWithPermissions,
  installRuntimeMonitors,
  installSyntheticApiIsolation,
} from './fixtures';

const client = {
  clientekey: 1,
  clave: 'CL-E2E',
  nombre: 'Cliente Sintético',
  idfiscal: 'RFC-E2E',
  tipone: '01',
  programa: 'IMMEX',
  calleNumero: 'CALLE E2E 100',
  numeroInterior: 5,
  colonia: 'CENTRO',
  codigo: 'CP-E2E',
  localidad: 'LOCALIDAD E2E',
  municipio: 'MUNICIPIO E2E',
  entidad: 'ENTIDAD E2E',
  pais: 'MEXICO',
  telefono: '555 000 0000',
  correo: 'cliente@example.test',
  fax: '555 000 0001',
  apellidoPaterno: 'PATERNO E2E',
  apellidoMaterno: 'MATERNO E2E',
  calle: 'CALLE E2E',
  referencia: 'REFERENCIA E2E',
  tipoIdentificador: 'RFC',
  codigoPostal: '21000',
};

const provider = {
  proveedorkey: 2,
  clave: 'PR-E2E',
  nombre: 'Proveedor Sintético',
  idfiscal: 'RFC-PR-E2E',
  tipone: '02',
  programa: 'PROVEE',
  calleNumero: 'CARRETERA E2E 200',
  numeroInterior: 4,
  colonia: 'INDUSTRIAL',
  codigo: 'CP-PR-E2E',
  localidad: 'LOCALIDAD PR',
  municipio: 'MUNICIPIO PR',
  entidad: 'ENTIDAD PR',
  pais: 'MEXICO',
  telefono: '777 000 0000',
  correo: 'proveedor@example.test',
  fax: '777 000 0001',
  almacenKey: 2,
  almacenDescripcion: 'ALMACEN NORTE',
};

const agent = { clave: 'AG-E2E', nombre: 'Agente Sintético', rfc: 'RFC-AG', patente: '3302', agenciaAduanal: 'AGENCIA E2E' };

test.beforeEach(async ({ page }) => {
  await installSyntheticApiIsolation(page);
});

test.afterEach(({ page }) => {
  assertNoUnexpectedApiRequests(page);
});

async function downloadBytes(download: Download): Promise<number> {
  const stream = await download.createReadStream();
  let total = 0;
  for await (const chunk of stream ?? []) total += Buffer.byteLength(chunk);
  return total;
}

async function mockClients(page: Page, requests: URL[] = [], total = 41): Promise<void> {
  await page.route('**/api/v1/catalogos/clientes**', async (route) => {
    const url = new URL(route.request().url());
    requests.push(url);
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        items: [client],
        total,
        pagina: Number(url.searchParams.get('pagina')),
        tamano: Number(url.searchParams.get('tamano')),
      }),
    });
  });
}

async function mockProviders(page: Page): Promise<void> {
  await page.route('**/api/v1/catalogos/proveedores**', (route) => route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({ items: [provider], total: 1, pagina: 1, tamano: 20 }),
  }));
}

async function mockAgents(page: Page): Promise<void> {
  await page.route('**/api/v1/catalogos/agentes-aduanales**', (route) => route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({ items: [agent], total: 1, pagina: 1, tamano: 20 }),
  }));
}

test('Clientes consulta campos confirmados, filtra, pagina, detalla y exporta CSV', async ({ page }) => {
  const requests: URL[] = [];
  await mockClients(page, requests);
  const monitors = installRuntimeMonitors(page);
  await authenticateWithPermissions(page, ['CATALOGOS_AUX_CONSULTAR']);

  await page.goto('/catalogos/socios-comerciales');
  await expect(page.getByRole('heading', { name: 'Socios comerciales' })).toBeVisible();
  const table = page.getByRole('table', { name: 'Catálogo de Clientes' });
  await expect(table).toContainText('Tipo NE');
  await expect(table).toContainText('01');
  await expect(table).toContainText('555 000 0000 · cliente@example.test');
  await expect(table).toContainText('MUNICIPIO E2E, ENTIDAD E2E');

  await page.getByLabel('Buscar en Clientes').fill('centro');
  await page.getByRole('button', { name: 'Consultar' }).click();
  await expect.poll(() => requests.at(-1)?.searchParams.get('filtro')).toBe('centro');
  expect(requests.at(-1)?.searchParams.get('pagina')).toBe('1');

  await page.locator('mat-paginator[aria-label="Paginación de Clientes"]').getByRole('button', { name: /next page/i }).click();
  await expect.poll(() => requests.at(-1)?.searchParams.get('pagina')).toBe('2');

  await page.getByRole('button', { name: 'Ver detalle de Cliente Sintético' }).click();
  const dialog = page.getByRole('dialog');
  await expect(dialog.getByRole('heading', { name: 'Detalle del cliente' })).toBeVisible();
  await expect(dialog).toContainText('Programa');
  await expect(dialog).toContainText('IMMEX');
  await expect(dialog).toContainText('Código postal');
  await expect(dialog).toContainText('21000');
  await expect(dialog).toContainText('REFERENCIA E2E');
  await dialog.getByRole('button', { name: 'Cerrar detalle' }).click();

  const download = page.waitForEvent('download');
  await page.getByRole('button', { name: 'Exportar CSV' }).click();
  const artifact = await download;
  expect(artifact.suggestedFilename()).toBe('clientes.csv');
  expect(await downloadBytes(artifact)).toBeGreaterThan(0);
  expect(requests.at(-1)?.searchParams.get('pagina')).toBe('1');
  expect(requests.at(-1)?.searchParams.get('tamano')).toBe('100');
  await assertNoRuntimeFailures(monitors);
});

test('Proveedores muestra almacén y Agentes aduanales conserva su consulta', async ({ page }) => {
  await mockClients(page);
  await mockProviders(page);
  await mockAgents(page);
  const monitors = installRuntimeMonitors(page);
  await authenticateWithPermissions(page, ['CATALOGOS_AUX_CONSULTAR']);

  await page.goto('/catalogos/socios-comerciales');
  await page.getByRole('button', { name: 'Proveedores' }).click();
  const providerTable = page.getByRole('table', { name: 'Catálogo de Proveedores' });
  await expect(providerTable).toContainText('ALMACEN NORTE');
  await page.getByRole('button', { name: 'Ver detalle de Proveedor Sintético' }).click();
  const providerDialog = page.getByRole('dialog');
  await expect(providerDialog.getByRole('heading', { name: 'Detalle del proveedor' })).toBeVisible();
  await expect(providerDialog).toContainText('ALMACEN NORTE');
  await providerDialog.getByRole('button', { name: 'Cerrar detalle' }).click();

  await page.getByRole('button', { name: 'Agentes aduanales' }).click();
  const agentTable = page.getByRole('table', { name: 'Catálogo de Agentes aduanales' });
  await expect(agentTable).toContainText('Patente / agencia');
  await expect(agentTable).toContainText('3302 · AGENCIA E2E');
  await expect(page.getByRole('button', { name: /Ver detalle de/ })).toHaveCount(0);
  await assertNoRuntimeFailures(monitors);
});

test('Clientes presenta tarjetas accesibles en pantalla angosta', async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await mockClients(page);
  const monitors = installRuntimeMonitors(page);
  await authenticateWithPermissions(page, ['CATALOGOS_AUX_CONSULTAR']);

  await page.goto('/catalogos/socios-comerciales');
  const cards = page.getByRole('list', { name: 'Resultados de Clientes' });
  await expect(cards).toBeVisible();
  await expect(cards).toContainText('Cliente Sintético');
  await expect(cards).toContainText('Tipo NE: 01');
  await expect(page.getByRole('table', { name: 'Catálogo de Clientes' })).toBeHidden();
  await page.getByRole('button', { name: 'Ver detalle de Cliente Sintético' }).click();
  await expect(page.getByRole('dialog')).toContainText('Detalle del cliente');
  await page.getByRole('button', { name: 'Cerrar detalle' }).click();
  await assertNoRuntimeFailures(monitors);
});

test('Clientes muestra estados vacío y error controlados', async ({ page }) => {
  let mode: 'empty' | 'error' = 'empty';
  await page.route('**/api/v1/catalogos/clientes**', (route) => {
    if (mode === 'error') {
      return route.fulfill({
        status: 503,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'No disponible', correlationId: 'e2e-socios-error' }),
      });
    }
    return route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ items: [], total: 0, pagina: 1, tamano: 20 }),
    });
  });
  const monitors = installRuntimeMonitors(page, { ignoredConsoleErrorFragments: ['503 (Service Unavailable)'] });
  await authenticateWithPermissions(page, ['CATALOGOS_AUX_CONSULTAR']);

  await page.goto('/catalogos/socios-comerciales');
  await expect(page.getByText('Sin resultados')).toBeVisible();
  mode = 'error';
  await page.getByRole('button', { name: 'Consultar' }).click();
  await expect(page.getByText('No pudimos cargar los socios comerciales')).toBeVisible();
  expect(monitors.serverErrors).toContain('GET /api/v1/catalogos/clientes 503');
  await assertNoRuntimeFailures({ ...monitors, serverErrors: [] });
});
