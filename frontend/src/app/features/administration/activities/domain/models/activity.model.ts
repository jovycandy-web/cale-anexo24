/** Actividad del catálogo técnico de permisos (read-only). */
export interface Activity {
  id: number;
  clave: string;
  nombre: string;
  recurso: string | null;
  accion: string | null;
}
