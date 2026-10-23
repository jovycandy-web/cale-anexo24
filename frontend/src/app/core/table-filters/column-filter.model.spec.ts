import { describe, expect, it } from 'vitest';
import {
  COLUMN_FILTER_OPERATOR_OPTIONS,
  DEFAULT_COLUMN_FILTER_OPERATOR,
  clearColumnFilterState,
  columnFilterQueryName,
  countActiveColumnFilters,
  createColumnFilterState,
  isColumnFilterActive,
  isSupportedColumnFilterOperator,
  normalizeColumnFilterOperator,
  serializeColumnFilters,
  toColumnFilterQuery,
} from './column-filter.model';

describe('column-filter.model', () => {
  it('expone sólo los cinco operadores que el servidor ejecuta', () => {
    expect(COLUMN_FILTER_OPERATOR_OPTIONS.map((option) => option.value))
      .toEqual(['EQ', 'NEQ', 'CT', 'SW', 'EW']);
    expect(COLUMN_FILTER_OPERATOR_OPTIONS.map((option) => option.label))
      .toEqual(['Igual', 'No igual', 'Contiene', 'Comienza con', 'Termina con']);
    expect(DEFAULT_COLUMN_FILTER_OPERATOR).toBe('EQ');
  });

  it('valida y normaliza operadores tolerando mayúsculas y espacios', () => {
    expect(isSupportedColumnFilterOperator('CT')).toBe(true);
    expect(isSupportedColumnFilterOperator('like')).toBe(false);
    expect(isSupportedColumnFilterOperator(null)).toBe(false);
    expect(normalizeColumnFilterOperator(' sw ')).toBe('SW');
    expect(normalizeColumnFilterOperator('LIKE')).toBe('EQ');
    expect(normalizeColumnFilterOperator(undefined)).toBe('EQ');
  });

  it('serializa valor y operador con el sufijo del contrato', () => {
    expect(columnFilterQueryName('pedimento')).toBe('pedimentoOp');
    expect(toColumnFilterQuery('pedimento', { value: ' 5003971 ', operator: 'CT' }))
      .toEqual({ pedimento: '5003971', pedimentoOp: 'CT' });
    expect(toColumnFilterQuery('pedimento', { value: '   ', operator: 'CT' })).toEqual({});
    expect(toColumnFilterQuery('pedimento', { value: 'X', operator: 'like' as never }))
      .toEqual({ pedimento: 'X', pedimentoOp: 'EQ' });
  });

  it('crea, limpia y cuenta estados activos', () => {
    expect(createColumnFilterState()).toEqual({ value: '', operator: 'EQ' });
    expect(createColumnFilterState('EW')).toEqual({ value: '', operator: 'EW' });
    expect(clearColumnFilterState()).toEqual({ value: '', operator: 'EQ' });
    expect(isColumnFilterActive({ value: '  ', operator: 'EQ' })).toBe(false);
    expect(isColumnFilterActive({ value: 'X', operator: 'EQ' })).toBe(true);
    expect(countActiveColumnFilters({
      pedimento: { value: 'P', operator: 'EQ' },
      marca: { value: '  ', operator: 'CT' },
      modelo: { value: 'M', operator: 'SW' },
    })).toBe(2);
  });

  it('combina varios filtros ignorando los inactivos', () => {
    expect(serializeColumnFilters({
      pedimento: { value: ' P-1 ', operator: 'CT' },
      descripcion: { value: '', operator: 'EQ' },
      serie: { value: 'SN', operator: 'SW' },
    })).toEqual({
      pedimento: 'P-1',
      pedimentoOp: 'CT',
      serie: 'SN',
      serieOp: 'SW',
    });
  });
});
