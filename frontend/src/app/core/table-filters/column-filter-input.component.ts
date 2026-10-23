import { ChangeDetectionStrategy, Component, computed, input, model, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {
  COLUMN_FILTER_OPERATOR_OPTIONS,
  ColumnFilterOperator,
  ColumnFilterState,
  clearColumnFilterState,
  isColumnFilterActive,
  normalizeColumnFilterOperator,
} from './column-filter.model';

/**
 * Entrada de filtro por columna con operador explícito.
 *
 * <p>El componente sólo administra estado y emite {@link filterChange}; no ejecuta
 * HTTP. La pantalla consumidora decide cuándo consultar al servidor y conserva
 * el filtro al paginar o exportar.</p>
 */
@Component({
  selector: 'app-column-filter-input',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, MatIconModule, MatInputModule],
  template: `
    <div class="min-w-0">
      <label [for]="fieldName()" class="mb-1 block text-xs font-medium text-slate-700">{{ label() }}</label>
      <div class="flex items-center gap-1">
        <input
          matInput
          [id]="fieldName()"
          type="text"
          [name]="fieldName()"
          [value]="state().value"
          [placeholder]="placeholder()"
          [attr.maxlength]="maxlength()"
          [attr.aria-label]="label()"
          [attr.aria-describedby]="helpId()"
          (input)="onValueChange($event)"
          (keydown.enter)="onEnter($event)"
          class="h-10 w-full rounded-lg border border-slate-200 bg-slate-50 px-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:bg-white focus:ring-4 focus:ring-blue-500/10"
        />
        <select
          [name]="fieldName() + 'Op'"
          [value]="state().operator"
          [attr.aria-label]="'Operador para ' + label()"
          (change)="onOperatorChange($event)"
          class="h-10 shrink-0 rounded-lg border border-slate-200 bg-white px-1 text-xs text-slate-700 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
        >
          @for (option of operators; track option.value) {
            <option [value]="option.value" [title]="option.description">{{ option.label }}</option>
          }
        </select>
        @if (active()) {
          <button
            type="button"
            class="flex h-10 w-9 shrink-0 items-center justify-center rounded-lg text-slate-500 transition hover:bg-slate-100 hover:text-slate-700 focus:ring-4 focus:ring-blue-500/10"
            [attr.aria-label]="'Limpiar filtro ' + label()"
            (click)="onClear()"
          >
            <mat-icon class="text-[18px]!" aria-hidden="true">close</mat-icon>
          </button>
        }
      </div>
      <p [id]="helpId()" class="sr-only">{{ activeOperator().description }}</p>
    </div>
  `,
})
export class ColumnFilterInputComponent {
  /** Etiqueta visible y nombre accesible del filtro. */
  readonly label = input.required<string>();

  /** Identificador estable del campo; se usa en ids y nombres del formulario. */
  readonly fieldName = input.required<string>();

  /** Texto de ayuda del campo. */
  readonly placeholder = input<string>('');

  /** Longitud máxima alineada con el contrato del servidor. */
  readonly maxlength = input<number | null>(null);

  /** Estado editable del filtro. */
  readonly state = model.required<ColumnFilterState>();

  /** Notifica que la pantalla debe consultar o refrescar resultados. */
  readonly filterChange = output<void>();

  protected readonly operators = COLUMN_FILTER_OPERATOR_OPTIONS;
  protected readonly active = computed(() => isColumnFilterActive(this.state()));
  protected readonly helpId = computed(() => `${this.fieldName()}-help`);
  protected readonly activeOperator = computed(() =>
    this.operators.find((option) => option.value === this.state().operator) ?? this.operators[0]);

  protected onValueChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.state.set({ ...this.state(), value });
  }

  protected onOperatorChange(event: Event): void {
    const operator = normalizeColumnFilterOperator((event.target as HTMLSelectElement).value);
    this.state.set({ ...this.state(), operator });
    this.filterChange.emit();
  }

  protected onEnter(event: Event): void {
    event.preventDefault();
    this.filterChange.emit();
  }

  protected onClear(): void {
    this.state.set(clearColumnFilterState());
    this.filterChange.emit();
  }

  /** Expuesto para pruebas y consumidores que necesiten el operador vigente. */
  protected currentOperator(): ColumnFilterOperator {
    return this.state().operator;
  }
}
