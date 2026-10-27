export interface NavigationItem {
  label: string;
  icon: string;
  route: string;
  /** Query params del enlace (destinos deep-link al módulo de reportes). */
  queryParams?: Record<string, string>;
  permission?: string;
  anyOfPermissions?: string[];
}

export interface NavigationGroup {
  label: string;
  icon: string;
  items: NavigationItem[];
}

/** Accesos directos, sin submenú: una sola entrada que abre el módulo completo. */
export const NAVIGATION_LINKS: NavigationItem[] = [
  { label: 'Reportes', icon: 'fact_check', route: '/reportes', permission: 'REPORTES_GENERAR' },
];

/** Metadata única de navegación; sólo contiene rutas funcionales existentes. */
export const NAVIGATION_GROUPS: NavigationGroup[] = [
  { label: 'Catálogos', icon: 'inventory_2', items: [
    { label: 'Datos generales', icon: 'business', route: '/catalogos/datos-generales', permission: 'CATALOGOS_AUX_CONSULTAR' },
    { label: 'Materiales', icon: 'inventory_2', route: '/materiales', permission: 'MATERIALES_CONSULTAR' },
    { label: 'Productos', icon: 'category', route: '/productos', permission: 'PRODUCTOS_CONSULTAR' },
    { label: 'Estructuras', icon: 'account_tree', route: '/estructuras', permission: 'ESTRUCTURAS_CONSULTAR' },
  ]},
  { label: 'Módulos', icon: 'swap_horiz', items: [
    { label: 'Entradas', icon: 'move_to_inbox', route: '/operaciones/entradas', permission: 'OPERACIONES_CONSULTAR' },
    { label: 'Materiales utilizados', icon: 'layers', route: '/operaciones/materiales-utilizados', permission: 'OPERACIONES_CONSULTAR' },
    { label: 'Salidas', icon: 'outbox', route: '/operaciones/salidas', permission: 'OPERACIONES_CONSULTAR' },
    { label: 'Activo fijo', icon: 'precision_manufacturing', route: '/operaciones/activos-fijos', permission: 'OPERACIONES_CONSULTAR' },
  ]},
  { label: 'Gestión', icon: 'admin_panel_settings', items: [
    { label: 'Usuarios', icon: 'group', route: '/usuarios', permission: 'USUARIOS_ADMINISTRAR' },
    { label: 'Actividades', icon: 'checklist', route: '/actividades', permission: 'PERFILES_ADMINISTRAR' },
    { label: 'Perfiles', icon: 'admin_panel_settings', route: '/perfiles', permission: 'PERFILES_ADMINISTRAR' },
    { label: 'Consulta bitácora', icon: 'manage_search', route: '/bitacora', permission: 'BITACORA_CONSULTAR' },
  ]},
];
