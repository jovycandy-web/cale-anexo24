import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { BusinessPartyPage } from '@features/catalogs/business-parties/domain/business-party.model';
import { BusinessPartyApi } from './business-party.api';

describe('BusinessPartyApi', () => {
  function setup() {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    return { api: TestBed.inject(BusinessPartyApi), http: TestBed.inject(HttpTestingController) };
  }

  it('mapea todos los campos confirmados del cliente', () => {
    const { api, http } = setup();
    let response: BusinessPartyPage | undefined;
    api.search('clients', ' centro ', 2, 50).subscribe((page) => { response = page; });

    const request = http.expectOne((req) => req.url === '/api/v1/catalogos/clientes');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('filtro')).toBe('centro');
    expect(request.request.params.get('pagina')).toBe('2');
    expect(request.request.params.get('tamano')).toBe('50');
    request.flush({
      items: [{
        clientekey: 1,
        clave: 'CL1',
        nombre: 'Cliente',
        idfiscal: 'RFC1',
        tipone: '01',
        programa: 'IMMEX',
        calleNumero: 'CALLE 1',
        numeroInterior: 3,
        colonia: 'CENTRO',
        codigo: 'CP1',
        localidad: 'LOCALIDAD',
        municipio: 'MUNICIPIO',
        entidad: 'ENTIDAD',
        pais: 'MEXICO',
        telefono: '555',
        correo: 'a@example.test',
        fax: '556',
        apellidoPaterno: 'PATERNO',
        apellidoMaterno: 'MATERNO',
        calle: 'CALLE',
        referencia: 'REF',
        tipoIdentificador: 'RFC',
        codigoPostal: '21000',
      }],
      total: 1,
      pagina: 2,
      tamano: 50,
    });

    expect(response?.items[0]).toEqual({
      key: 'CL1',
      name: 'Cliente',
      fiscalId: 'RFC1',
      typeNe: '01',
      program: 'IMMEX',
      streetAndNumber: 'CALLE 1',
      interiorNumber: 3,
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
    });
    http.verify();
  });

  it('mapea el almacén resuelto del proveedor sin inventar campos de cliente', () => {
    const { api, http } = setup();
    let response: BusinessPartyPage | undefined;
    api.search('providers', '', 1, 20).subscribe((page) => { response = page; });

    const request = http.expectOne((req) => req.url === '/api/v1/catalogos/proveedores');
    expect(request.request.params.has('filtro')).toBe(false);
    request.flush({
      items: [{ proveedorkey: 9, clave: 'PR9', nombre: 'Proveedor', almacenKey: 2, almacenDescripcion: 'ALMACEN NORTE' }],
      total: 1,
      pagina: 1,
      tamano: 20,
    });

    expect(response?.items[0]).toMatchObject({
      key: 'PR9',
      name: 'Proveedor',
      warehouseKey: 2,
      warehouse: 'ALMACEN NORTE',
    });
    expect(response?.items[0].paternalSurname).toBeUndefined();
    http.verify();
  });

  it('mantiene el mapeo de agentes aduanales', () => {
    const { api, http } = setup();
    let response: BusinessPartyPage | undefined;
    api.search('agents', 'agente', 1, 20).subscribe((page) => { response = page; });

    http.expectOne((req) => req.url === '/api/v1/catalogos/agentes-aduanales').flush({
      items: [{ clave: 'AG1', nombre: 'Agente', rfc: 'RFC-A', patente: '3302', agenciaAduanal: 'AGENCIA' }],
      total: 1,
      pagina: 1,
      tamano: 20,
    });

    expect(response?.items[0]).toEqual({
      key: 'AG1', name: 'Agente', fiscalId: 'RFC-A', patent: '3302', agency: 'AGENCIA',
    });
    http.verify();
  });
});
