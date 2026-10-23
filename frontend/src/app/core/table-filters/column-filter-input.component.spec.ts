import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ColumnFilterInputComponent } from './column-filter-input.component';
import { createColumnFilterState } from './column-filter.model';

function configure(state = createColumnFilterState()): ReturnType<typeof TestBed.createComponent<ColumnFilterInputComponent>> {
  const fixture = TestBed.createComponent(ColumnFilterInputComponent);
  fixture.componentRef.setInput('label', 'Pedimento');
  fixture.componentRef.setInput('fieldName', 'pedimento');
  fixture.componentRef.setInput('placeholder', 'Documento de importación');
  fixture.componentRef.setInput('maxlength', 20);
  fixture.componentRef.setInput('state', state);
  fixture.detectChanges();
  return fixture;
}

function inputOf(fixture: ReturnType<typeof configure>): HTMLInputElement {
  return fixture.nativeElement.querySelector('input') as HTMLInputElement;
}

function selectOf(fixture: ReturnType<typeof configure>): HTMLSelectElement {
  return fixture.nativeElement.querySelector('select') as HTMLSelectElement;
}

describe('ColumnFilterInputComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [ColumnFilterInputComponent] }).compileComponents();
  });

  it('renderiza etiqueta, entrada y los cinco operadores soportados', () => {
    const fixture = configure();
    expect(fixture.nativeElement.querySelector('label')?.textContent).toContain('Pedimento');
    expect(inputOf(fixture).getAttribute('maxlength')).toBe('20');
    expect(inputOf(fixture).getAttribute('placeholder')).toBe('Documento de importación');
    expect([...selectOf(fixture).options].map((option) => option.value))
      .toEqual(['EQ', 'NEQ', 'CT', 'SW', 'EW']);
  });

  it('expone etiquetas accesibles y ayuda del operador activo', () => {
    const fixture = configure();
    expect(inputOf(fixture).getAttribute('aria-label')).toBe('Pedimento');
    expect(selectOf(fixture).getAttribute('aria-label')).toBe('Operador para Pedimento');
    const helpId = inputOf(fixture).getAttribute('aria-describedby');
    expect(helpId).toBe('pedimento-help');
    expect(fixture.nativeElement.querySelector(`#${helpId}`)?.textContent)
      .toContain('coincidir exactamente');
  });

  it('emite búsqueda al presionar Enter y actualiza el valor', () => {
    const fixture = configure();
    const emitted = vi.fn();
    fixture.componentInstance.filterChange.subscribe(emitted);

    const input = inputOf(fixture);
    input.value = '5003971';
    input.dispatchEvent(new Event('input'));
    input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }));

    expect(fixture.componentInstance.state()).toEqual({ value: '5003971', operator: 'EQ' });
    expect(emitted).toHaveBeenCalledOnce();
  });

  it('emite búsqueda al cambiar el operador', () => {
    const fixture = configure();
    const emitted = vi.fn();
    fixture.componentInstance.filterChange.subscribe(emitted);

    const select = selectOf(fixture);
    select.value = 'CT';
    select.dispatchEvent(new Event('change'));

    expect(fixture.componentInstance.state().operator).toBe('CT');
    expect(emitted).toHaveBeenCalledOnce();
  });

  it('limpia el filtro y emite búsqueda sin borrar el operador del contrato', () => {
    const fixture = configure({ value: 'P-1', operator: 'SW' });
    const emitted = vi.fn();
    fixture.componentInstance.filterChange.subscribe(emitted);

    const clearButton = fixture.nativeElement.querySelector('button[aria-label="Limpiar filtro Pedimento"]');
    expect(clearButton).toBeTruthy();
    clearButton.click();

    expect(fixture.componentInstance.state()).toEqual({ value: '', operator: 'EQ' });
    expect(emitted).toHaveBeenCalledOnce();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('button[aria-label="Limpiar filtro Pedimento"]')).toBeNull();
  });

  it('no muestra el botón limpiar cuando el filtro está vacío', () => {
    const fixture = configure();
    expect(fixture.nativeElement.querySelector('button[aria-label="Limpiar filtro Pedimento"]')).toBeNull();
  });
});
