import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { BusinessPartyRow } from '@features/catalogs/business-parties/domain/business-party.model';

export interface BusinessPartyDetailData {
  kind: 'clients' | 'providers';
  row: BusinessPartyRow;
}

export interface BusinessPartyDetailField {
  label: string;
  value: string;
}

function text(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === '') return '—';
  return String(value);
}

/** Construye los campos confirmados del detalle según el catálogo. */
export function businessPartyDetailFields(kind: 'clients' | 'providers', row: BusinessPartyRow): BusinessPartyDetailField[] {
  const common: BusinessPartyDetailField[] = [
    { label: 'Clave', value: text(row.key) },
    { label: 'Nombre', value: text(row.name) },
    { label: 'Id fiscal', value: text(row.fiscalId) },
    { label: 'Tipo NE', value: text(row.typeNe) },
    { label: 'Programa', value: text(row.program) },
    { label: 'Calle y número exterior', value: text(row.streetAndNumber) },
    { label: 'No. interior', value: text(row.interiorNumber) },
    { label: 'Colonia', value: text(row.neighborhood) },
    { label: 'Código', value: text(row.code) },
    { label: 'Localidad', value: text(row.locality) },
    { label: 'Municipio', value: text(row.municipality) },
    { label: 'Entidad', value: text(row.state) },
    { label: 'País', value: text(row.country) },
    { label: 'Teléfono', value: text(row.phone) },
    { label: 'Correo', value: text(row.email) },
    { label: 'Fax', value: text(row.fax) },
  ];
  if (kind === 'providers') {
    return [
      { label: 'Almacén', value: text(row.warehouse ?? row.warehouseKey) },
      ...common,
    ];
  }
  return [
    ...common,
    { label: 'Código postal', value: text(row.postalCode) },
    { label: 'Apellido paterno', value: text(row.paternalSurname) },
    { label: 'Apellido materno', value: text(row.maternalSurname) },
    { label: 'Calle', value: text(row.street) },
    { label: 'Referencia', value: text(row.reference) },
    { label: 'Tipo de identificador', value: text(row.idType) },
  ];
}

/** Detalle accesible de un cliente o proveedor confirmado. */
@Component({
  selector: 'app-business-party-detail',
  imports: [MatButtonModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>{{ title }}</h2>
    <mat-dialog-content>
      <dl class="grid grid-cols-1 gap-x-6 gap-y-4 sm:grid-cols-2">
        @for (field of fields; track field.label) {
          <div class="min-w-0">
            <dt class="text-[11px] font-semibold uppercase tracking-wide text-slate-500">{{ field.label }}</dt>
            <dd class="mt-1 break-words text-sm text-slate-900">{{ field.value }}</dd>
          </div>
        }
      </dl>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button type="button" mat-dialog-close aria-label="Cerrar detalle">Cerrar</button>
    </mat-dialog-actions>
  `,
})
export class BusinessPartyDetailDialog {
  protected readonly data = inject<BusinessPartyDetailData>(MAT_DIALOG_DATA);
  protected readonly title = this.data.kind === 'clients' ? 'Detalle del cliente' : 'Detalle del proveedor';
  protected readonly fields = businessPartyDetailFields(this.data.kind, this.data.row);
}
