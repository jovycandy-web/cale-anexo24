import { test, expect } from '@playwright/test';

const storageState = process.env.E2E_STORAGE_STATE;

test.skip(!storageState, 'Requiere E2E_STORAGE_STATE con una sesión Angular autorizada; no se versionan credenciales.');

test.use({ storageState: storageState as string });

test('Productos: filtro real, columnas y estado vacío', async ({ page }) => {
  await page.goto('/productos');
  await expect(page.getByRole('heading', { name: 'Productos', exact: true })).toBeVisible();
  await expect(page.locator('table thead th')).toHaveCount(11);

  const search = page.getByPlaceholder('Buscar en el catálogo');
  await search.fill('LIMONAZO');
  await page.getByRole('button', { name: 'Consultar', exact: true }).click();
  await expect(page.getByText('LIMONAZO (EXP 24/10 PZAS POLVO)')).toBeVisible();
  await expect(page.getByText(/1\s*[–-]\s*1 of 1/)).toBeVisible();

  await search.fill('ZZZZ-NO-EXISTE');
  await page.getByRole('button', { name: 'Consultar', exact: true }).click();
  await expect(page.getByText('No encontramos productos')).toBeVisible();
});

test('Estructuras legacy: responde sin 5xx y conserva su contrato visible', async ({ page }) => {
  const serverErrors: string[] = [];
  page.on('response', (response) => {
    if (response.url().includes('/api/') && response.status() >= 500) serverErrors.push(response.url());
  });

  await page.goto('/reportes');
  await page.getByRole('button', { name: 'Estructuras (legacy)' }).click();
  await expect(page.getByText('El periodo del procedimiento no filtra resultados.')).toBeVisible();
  await page.getByRole('button', { name: 'Generar' }).click();
  await expect(page.getByText('No pudimos generar el reporte')).toHaveCount(0);
  expect(serverErrors).toEqual([]);
});
