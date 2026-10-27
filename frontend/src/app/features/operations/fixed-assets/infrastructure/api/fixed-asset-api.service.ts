import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ColumnFilterState, serializeColumnFilters } from '@core/table-filters/column-filter.model';
import {
  FixedAssetFilterField,
  FixedAssetSearchCriteria,
} from '@features/operations/fixed-assets/domain/models/fixed-asset.model';
import { FixedAssetPageResponseDto } from './dto/fixed-asset.dto';

/** Adaptador HTTP del contrato V1 de activos fijos. */
@Injectable({ providedIn: 'root' })
export class FixedAssetApiService {
  private readonly http = inject(HttpClient);

  search(criteria: FixedAssetSearchCriteria): Observable<FixedAssetPageResponseDto> {
    let params = new HttpParams()
      .set('pagina', criteria.page)
      .set('tamano', criteria.pageSize);

    if (criteria.from && criteria.to) {
      params = params.set('desde', criteria.from).set('hasta', criteria.to);
    }

    const filters = {
      pedimento: { value: criteria.customsDocument, operator: criteria.operators?.pedimento ?? 'EQ' },
      clavePedimento: { value: criteria.customsCode, operator: criteria.operators?.clavePedimento ?? 'EQ' },
      numeroParte: { value: criteria.partNumber, operator: criteria.operators?.numeroParte ?? 'EQ' },
      descripcion: { value: criteria.description, operator: criteria.operators?.descripcion ?? 'EQ' },
      serie: { value: criteria.serialNumber, operator: criteria.operators?.serie ?? 'EQ' },
      marca: { value: criteria.brand, operator: criteria.operators?.marca ?? 'EQ' },
      modelo: { value: criteria.model, operator: criteria.operators?.modelo ?? 'EQ' },
    } satisfies Record<FixedAssetFilterField, ColumnFilterState>;

    const filterParams: Record<string, string> = serializeColumnFilters(filters);
    for (const [field, value] of Object.entries(filterParams)) {
      params = params.set(field, value);
    }

    return this.http.get<FixedAssetPageResponseDto>('/api/v1/operaciones/activos-fijos', { params });
  }
}
