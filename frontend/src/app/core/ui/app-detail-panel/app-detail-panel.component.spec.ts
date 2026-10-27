import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { AppDetailPanelComponent } from './app-detail-panel.component';

@Component({
  imports: [AppDetailPanelComponent],
  template: `
    <app-detail-panel title="Detalle de A-1" subtitle="Registro uno" closeLabel="Cerrar detalle de A-1" (closed)="cerrado = cerrado + 1">
      <p>Contenido proyectado</p>
    </app-detail-panel>
    <app-detail-panel title="Detalle de A-2" (closed)="cerrado = cerrado + 1">
      <p>Segundo contenido</p>
    </app-detail-panel>
  `,
})
class HostComponent {
  cerrado = 0;
}

describe('AppDetailPanelComponent', () => {
  function render() {
    const fixture = TestBed.createComponent(HostComponent);
    fixture.detectChanges();
    return fixture;
  }

  it('muestra título, subtítulo y contenido proyectado en una región accesible', () => {
    const fixture = render();
    const elemento: HTMLElement = fixture.nativeElement;
    const regiones = elemento.querySelectorAll('section');

    expect(regiones).toHaveLength(2);
    expect(elemento.textContent).toContain('Detalle de A-1');
    expect(elemento.textContent).toContain('Registro uno');
    expect(elemento.textContent).toContain('Contenido proyectado');
    expect(regiones[0].getAttribute('aria-labelledby')).toBe(regiones[0].querySelector('h2')?.id);
    expect(regiones[0].getAttribute('aria-labelledby')).not.toBe(regiones[1].getAttribute('aria-labelledby'));
  });

  it('emite el cierre al usar el botón con su etiqueta accesible', () => {
    const fixture = render();
    const boton = fixture.nativeElement.querySelector('button[aria-label="Cerrar detalle de A-1"]') as HTMLButtonElement;

    boton.click();
    fixture.detectChanges();

    expect(fixture.componentInstance.cerrado).toBe(1);
  });

  it('usa la etiqueta de cierre por defecto cuando no se configura', () => {
    const fixture = render();
    const boton = fixture.nativeElement.querySelectorAll('button[aria-label="Cerrar detalle"]');

    expect(boton).toHaveLength(1);
  });
});
