/**
 * Operadores de filtro por columna que el servidor ejecuta realmente.
 *
 * <p>Sólo se modelan comparaciones directas sobre una columna. No existen
 * rangos, expresiones regulares ni listas porque el contrato HTTP/SP de
 * Activos fijos no las soporta; simularlas en el cliente produciría resultados
 * parciales sobre la página visible.</p>
 */
export type ColumnFilterOperator = 'EQ' | 'NEQ' | 'CT' | 'SW' | 'EW';

/** Opción presentable de un operador soportado. */
export interface ColumnFilterOperatorOption {
  value: ColumnFilterOperator;
  label: string;
  shortLabel: string;
  description: string;
}

/** Catálogo único de operadores, reutilizable por cualquier pantalla. */
export const COLUMN_FILTER_OPERATOR_OPTIONS: readonly ColumnFilterOperatorOption[] = [
  {
    value: 'EQ',
    label: 'Igual',
    shortLabel: '=',
    description: 'La columna debe coincidir exactamente con el valor.',
  },
  {
    value: 'NEQ',
    label: 'No igual',
    shortLabel: '≠',
    description: 'La columna no debe coincidir con el valor.',
  },
  {
    value: 'CT',
    label: 'Contiene',
    shortLabel: '∋',
    description: 'La columna contiene el valor en cualquier posición.',
  },
  {
    value: 'SW',
    label: 'Comienza con',
    shortLabel: '^',
    description: 'La columna comienza con el valor indicado.',
  },
  {
    value: 'EW',
    label: 'Termina con',
    shortLabel: '$',
    description: 'La columna termina con el valor indicado.',
  },
];

/** Operador por omisión: preserva el comportamiento histórico de igualdad. */
export const DEFAULT_COLUMN_FILTER_OPERATOR: ColumnFilterOperator = 'EQ';

/** Estado editable de un filtro de columna. */
export interface ColumnFilterState {
  value: string;
  operator: ColumnFilterOperator;
}

/**
 * Indica si un valor corresponde a un operador soportado.
 *
 * @param raw valor recibido desde almacenamiento o consultas guardadas
 * @returns `true` cuando el operador es válido
 */
export function isSupportedColumnFilterOperator(raw: unknown): raw is ColumnFilterOperator {
  return typeof raw === 'string'
    && COLUMN_FILTER_OPERATOR_OPTIONS.some((option) => option.value === raw);
}

/**
 * Normaliza un operador tolerando mayúsculas y espacios.
 *
 * @param raw operador recibido; valores desconocidos caen en el valor por omisión
 * @returns operador soportado
 */
export function normalizeColumnFilterOperator(raw: unknown): ColumnFilterOperator {
  if (typeof raw !== 'string') return DEFAULT_COLUMN_FILTER_OPERATOR;
  const candidato = raw.trim().toUpperCase();
  return isSupportedColumnFilterOperator(candidato)
    ? candidato
    : DEFAULT_COLUMN_FILTER_OPERATOR;
}

/**
 * Nombre del parámetro HTTP que transporta el operador de una columna.
 *
 * @param field nombre de la columna en el contrato HTTP
 * @returns nombre del parámetro con sufijo `Op`
 */
export function columnFilterQueryName(field: string): string {
  return `${field}Op`;
}

/**
 * Serializa un filtro a parámetros de consulta.
 *
 * @param field nombre de la columna en el contrato HTTP
 * @param state estado del filtro
 * @returns objeto vacío si no hay valor; en caso contrario valor y operador
 */
export function toColumnFilterQuery(
  field: string,
  state: ColumnFilterState,
): Record<string, string> {
  const value = state?.value?.trim() ?? '';
  if (!value) return {};
  return {
    [field]: value,
    [columnFilterQueryName(field)]: normalizeColumnFilterOperator(state.operator),
  };
}

/**
 * Crea un estado de filtro vacío con el operador indicado.
 *
 * @param operator operador inicial; por omisión igualdad
 * @returns estado vacío
 */
export function createColumnFilterState(
  operator: ColumnFilterOperator = DEFAULT_COLUMN_FILTER_OPERATOR,
): ColumnFilterState {
  return { value: '', operator: normalizeColumnFilterOperator(operator) };
}

/**
 * Restablece un filtro a su estado vacío.
 *
 * @returns estado vacío con operador por omisión
 */
export function clearColumnFilterState(): ColumnFilterState {
  return createColumnFilterState();
}

/**
 * Indica si el filtro aporta un valor efectivo.
 *
 * @param state estado del filtro
 * @returns `true` cuando existe contenido distinto de espacios
 */
export function isColumnFilterActive(state: ColumnFilterState | null | undefined): boolean {
  return Boolean(state?.value?.trim());
}

/**
 * Cuenta los filtros con valor efectivo.
 *
 * @param states mapa de filtros por columna
 * @returns cantidad de filtros activos
 */
export function countActiveColumnFilters(
  states: Readonly<Record<string, ColumnFilterState>>,
): number {
  return Object.values(states).filter((state) => isColumnFilterActive(state)).length;
}

/**
 * Combina varios filtros en un único mapa de parámetros HTTP.
 *
 * @param fields filtros por columna
 * @returns parámetros de consulta sin filtros vacíos
 */
export function serializeColumnFilters(
  fields: Readonly<Record<string, ColumnFilterState>>,
): Record<string, string> {
  return Object.entries(fields).reduce<Record<string, string>>((params, [field, state]) => {
    return { ...params, ...toColumnFilterQuery(field, state) };
  }, {});
}
