export interface ProductResponseDto {
  id: string | number;
  clave: string;
  claveCliente: string | null;
  descripcion: string;
  fraccion: string;
  unidadComercial: string;
  unidadTarifaria: string;
  nico: string | null;
  tipo: string | null;
  auxiliar: string | null;
  almacenKey: string | number | null;
  almacen: string | null;
}

export interface ProductPageResponseDto {
  items: ProductResponseDto[];
  total: number;
  pagina: number;
  tamano: number;
}
