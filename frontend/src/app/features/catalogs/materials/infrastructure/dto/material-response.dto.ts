export interface MaterialResponseDto {
  materialkey: number;
  clave: string;
  descripcion: string;
  fraccion: string;
  unidad: string;
  unidadt: string;
  tipomaterial: string;
  claveProveedor: string | null;
  tipom: string | null;
  familia: string | null;
  nico: string | null;
  almacenKey: number | null;
  almacen: string | null;
}

export interface MaterialPageResponseDto {
  items: MaterialResponseDto[];
  total: number;
  pagina: number;
  tamano: number;
}
