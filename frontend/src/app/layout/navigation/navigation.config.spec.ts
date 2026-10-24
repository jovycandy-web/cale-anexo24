import { describe, expect, it } from 'vitest';
import { NAVIGATION_GROUPS } from './navigation.config';

/** Alcance cerrado de la versión de presentación (Sprint 09). */
describe('navigation.config', () => {
  const groupLabels = NAVIGATION_GROUPS.map((group) => group.label);

  it('expone sólo los cuatro grupos del alcance', () => {
    expect(groupLabels).toEqual(['Catálogos', 'Módulos', 'Reporte de Aduanas', 'Gestión']);
  });

  it('conserva los cuatro catálogos del módulo SAT en orden legacy', () => {
    const catalogos = NAVIGATION_GROUPS.find((group) => group.label === 'Catálogos');
    expect(catalogos?.items.map((item) => item.route)).toEqual([
      '/catalogos/datos-generales',
      '/materiales',
      '/productos',
      '/estructuras',
    ]);
  });

  it('conserva los cuatro módulos del módulo SAT en orden legacy', () => {
    const modulos = NAVIGATION_GROUPS.find((group) => group.label === 'Módulos');
    expect(modulos?.items.map((item) => item.label)).toEqual([
      'Entradas',
      'Materiales utilizados',
      'Salidas',
      'Activo fijo',
    ]);
  });

  it('deep-linkea los cuatro reportes de aduanas al módulo de reportes existente', () => {
    const aduanas = NAVIGATION_GROUPS.find((group) => group.label === 'Reporte de Aduanas');
    expect(aduanas?.items.map((item) => [item.route, item.queryParams?.['tipo']])).toEqual([
      ['/reportes', 'entradas'],
      ['/reportes', 'salidas'],
      ['/reportes', 'saldos'],
      ['/reportes', 'materiales-utilizados'],
    ]);
    expect(aduanas?.items.every((item) => item.permission === 'REPORTES_GENERAR')).toBe(true);
  });

  it('agrupa la gestión administrativa del alcance', () => {
    const gestion = NAVIGATION_GROUPS.find((group) => group.label === 'Gestión');
    expect(gestion?.items.map((item) => item.label)).toEqual([
      'Usuarios',
      'Actividades',
      'Perfiles',
      'Consulta bitácora',
    ]);
    expect(gestion?.items.find((item) => item.label === 'Actividades')?.route).toBe('/actividades');
  });

  it('oculta del menú los módulos fuera del alcance sin borrar sus rutas', () => {
    const routes = NAVIGATION_GROUPS.flatMap((group) => group.items.map((item) => item.route));
    expect(routes.some((route) => route.includes('/operaciones/actas'))).toBe(false);
    expect(routes.some((route) => route.includes('/operaciones/constancias'))).toBe(false);
    expect(routes.some((route) => route.includes('/operaciones/pedimentos'))).toBe(false);
    expect(routes.some((route) => route.includes('/catalogos/importaciones'))).toBe(false);
    expect(routes).not.toContain('/facturacion');
    expect(routes).not.toContain('/catalogos/socios-comerciales');
    expect(routes).not.toContain('/catalogos');
  });
});
