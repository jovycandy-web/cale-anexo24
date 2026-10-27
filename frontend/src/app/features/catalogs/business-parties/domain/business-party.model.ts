export type BusinessPartyKind = 'clients' | 'providers' | 'agents';

/** Fila normalizada de cliente, proveedor o agente aduanal. */
export interface BusinessPartyRow {
  key: string;
  name: string;
  fiscalId?: string;
  typeNe?: string;
  program?: string;
  streetAndNumber?: string;
  interiorNumber?: number | null;
  neighborhood?: string;
  code?: string;
  locality?: string;
  municipality?: string;
  state?: string;
  country?: string;
  phone?: string;
  email?: string;
  fax?: string;
  paternalSurname?: string;
  maternalSurname?: string;
  street?: string;
  reference?: string;
  idType?: string;
  postalCode?: string;
  warehouseKey?: number | null;
  warehouse?: string;
  patent?: string;
  agency?: string;
}

export interface BusinessPartyPage {
  items: BusinessPartyRow[];
  total: number;
  pagina: number;
  tamano: number;
}

export interface ApiPage<T> {
  items: T[];
  total: number;
  pagina: number;
  tamano: number;
}

export interface ClientDto {
  clientekey?: number;
  clave: string;
  nombre: string;
  idfiscal?: string;
  tipone?: string;
  programa?: string;
  calleNumero?: string;
  numeroInterior?: number | null;
  colonia?: string;
  codigo?: string;
  localidad?: string;
  municipio?: string;
  entidad?: string;
  pais?: string;
  telefono?: string;
  correo?: string;
  fax?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  calle?: string;
  referencia?: string;
  tipoIdentificador?: string;
  codigoPostal?: string;
}

export interface ProviderDto {
  proveedorkey?: number;
  clave: string;
  nombre: string;
  idfiscal?: string;
  tipone?: string;
  programa?: string;
  calleNumero?: string;
  numeroInterior?: number | null;
  colonia?: string;
  codigo?: string;
  localidad?: string;
  municipio?: string;
  entidad?: string;
  pais?: string;
  telefono?: string;
  correo?: string;
  fax?: string;
  almacenKey?: number | null;
  almacenDescripcion?: string;
}

export interface AgentDto {
  clave: string;
  nombre: string;
  rfc?: string;
  patente?: string;
  agenciaAduanal?: string;
}
