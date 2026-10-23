import { describe, expect, it } from 'vitest';
import { BusinessPartyRow } from '@features/catalogs/business-parties/domain/business-party.model';
import { businessPartyDetailFields } from './business-party-detail.dialog';

const row: BusinessPartyRow = {
  key: 'K1',
  name: 'Party',
  fiscalId: 'RFC1',
  typeNe: '01',
  program: 'IMMEX',
  streetAndNumber: 'CALLE 1',
  interiorNumber: null,
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
  warehouse: 'ALMACEN NORTE',
};

describe('businessPartyDetailFields', () => {
  it('muestra los campos confirmados del cliente y los vacíos como guion', () => {
    const fields = businessPartyDetailFields('clients', row);
    expect(fields.map((field) => field.label)).toEqual([
      'Clave', 'Nombre', 'Id fiscal', 'Tipo NE', 'Programa', 'Calle y número exterior', 'No. interior',
      'Colonia', 'Código', 'Localidad', 'Municipio', 'Entidad', 'País', 'Teléfono', 'Correo', 'Fax',
      'Código postal', 'Apellido paterno', 'Apellido materno', 'Calle', 'Referencia', 'Tipo de identificador',
    ]);
    expect(fields.find((field) => field.label === 'No. interior')?.value).toBe('—');
    expect(fields.find((field) => field.label === 'Programa')?.value).toBe('IMMEX');
    expect(fields.some((field) => field.label === 'Almacén')).toBe(false);
  });

  it('muestra el almacén del proveedor sin campos exclusivos de cliente', () => {
    const fields = businessPartyDetailFields('providers', row);
    expect(fields[0]).toEqual({ label: 'Almacén', value: 'ALMACEN NORTE' });
    expect(fields.some((field) => field.label === 'Código postal')).toBe(false);
    expect(fields.some((field) => field.label === 'Apellido paterno')).toBe(false);
  });
});
