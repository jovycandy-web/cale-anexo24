import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

let siguienteIdPanel = 0;

/**
 * Panel de detalle reutilizable para filas expandibles de una tabla.
 *
 * <p>Se coloca dentro de la celda de detalle de la fila seleccionada para que la
 * información aparezca justo debajo de ella, sin perder la posición de lectura.
 * El contenido se proyecta, de modo que cada pantalla decide sus campos.</p>
 */
@Component({
  imports: [MatButtonModule, MatIconModule],
  selector: 'app-detail-panel',
  template: `
    <section class="block border-t border-slate-200 bg-slate-50 p-5" [attr.aria-labelledby]="panelId">
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 [id]="panelId" class="m-0 text-base font-semibold text-slate-900">{{ title }}</h2>
          @if (subtitle) {
            <p class="mt-1 mb-0 text-sm text-slate-600">{{ subtitle }}</p>
          }
        </div>
        <button mat-icon-button type="button" [attr.aria-label]="closeLabel" (click)="closed.emit()">
          <mat-icon aria-hidden="true">close</mat-icon>
        </button>
      </div>
      <div class="mt-4">
        <ng-content></ng-content>
      </div>
    </section>
  `,
})
export class AppDetailPanelComponent {
  /** Título del panel; debe identificar el registro abierto. */
  @Input({ required: true }) title = '';
  /** Texto secundario opcional bajo el título. */
  @Input() subtitle = '';
  /** Etiqueta accesible del botón de cierre. */
  @Input() closeLabel = 'Cerrar detalle';
  /** Se emite al solicitar el cierre del panel. */
  @Output() readonly closed = new EventEmitter<void>();

  /** Identificador único para asociar el título con la región accesible. */
  protected readonly panelId = `detail-panel-${siguienteIdPanel++}`;
}
