import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { catchError, of } from 'rxjs';
import { userFacingApiError } from '@core/http/api-error.util';
import { AppAlertComponent } from '@core/ui/app-alert/app-alert.component';
import { Activity } from '@features/administration/activities/domain/models/activity.model';
import { ActivityApiService } from '@features/administration/activities/infrastructure/api/activity-api.service';

/** Catálogo read-only de actividades autorizables, equivalente a `catActividades.aspx`. */
@Component({
  imports: [AppAlertComponent, MatFormFieldModule, MatIconModule, MatInputModule, MatTableModule],
  selector: 'app-activity-list',
  template: `
    <div class="mx-auto flex max-w-6xl flex-col gap-6 p-4 sm:p-6">
      <header class="flex flex-col gap-1">
        <h1 class="m-0 text-2xl font-semibold text-slate-900">Actividades</h1>
        <p class="m-0 text-sm text-slate-500">Catálogo de actividades autorizables. La asignación se realiza en Perfiles.</p>
      </header>

      <section class="rounded-2xl border border-slate-200/80 bg-white p-4 sm:p-6" aria-label="Filtro de actividades">
        <mat-form-field appearance="outline" class="w-full sm:w-96">
          <mat-label>Buscar actividad</mat-label>
          <input matInput [value]="filter()" (input)="onFilter($event)" autocomplete="off" placeholder="Clave, nombre, recurso o acción" />
        </mat-form-field>
      </section>

      @if (isLoading()) {
        <section class="rounded-2xl border border-slate-200 bg-white p-6" aria-busy="true" aria-label="Cargando actividades">
          @for (row of loadingRows; track row) { <div class="mb-3 h-10 animate-pulse rounded-lg bg-slate-100"></div> }
        </section>
      } @else if (error()) {
        <app-alert kind="error" title="No pudimos cargar las actividades" [message]="error()!" actionLabel="Reintentar" (action)="load()" />
      } @else {
        <section class="overflow-hidden rounded-2xl border border-slate-200/80 bg-white" aria-label="Resultados de actividades">
          @if (visible().length > 0) {
            <div class="overflow-x-auto">
              <table mat-table [dataSource]="visible()" class="w-full" aria-label="Catálogo de actividades">
                <ng-container matColumnDef="clave">
                  <th mat-header-cell *matHeaderCellDef class="w-40">Clave</th>
                  <td mat-cell *matCellDef="let activity" class="font-medium text-slate-700">{{ activity.clave }}</td>
                </ng-container>
                <ng-container matColumnDef="nombre">
                  <th mat-header-cell *matHeaderCellDef>Nombre</th>
                  <td mat-cell *matCellDef="let activity">{{ activity.nombre }}</td>
                </ng-container>
                <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
              </table>
            </div>
            <p class="m-0 border-t border-slate-100 px-4 py-3 text-xs text-slate-500">{{ visible().length }} de {{ activities().length }} actividades</p>
          } @else {
            <div class="p-6">
              <app-alert
                kind="info"
                [title]="filter() ? 'Sin coincidencias' : 'El catálogo está vacío'"
                [message]="filter() ? 'Prueba con otra búsqueda o limpia el filtro.' : 'La fuente de datos no devolvió actividades.'"
                [actionLabel]="filter() ? 'Limpiar búsqueda' : ''"
                (action)="clearFilter()"
              />
            </div>
          }
        </section>
      }
    </div>
  `,
})
export class ActivityListPage implements OnInit {
  protected readonly displayedColumns = ['clave', 'nombre'];
  protected readonly loadingRows = [1, 2, 3, 4, 5];
  protected readonly activities = signal<Activity[]>([]);
  protected readonly filter = signal('');
  protected readonly isLoading = signal(false);
  protected readonly error = signal<string | null>(null);

  /** Filtro local: el endpoint entrega el catálogo completo (decenas de filas). */
  protected readonly visible = computed(() => {
    const term = this.filter().trim().toLowerCase();
    if (!term) return this.activities();
    return this.activities().filter((activity) =>
      [activity.clave, activity.nombre, activity.recurso, activity.accion]
        .some((value) => (value ?? '').toLowerCase().includes(term)),
    );
  });

  private readonly api = inject(ActivityApiService);
  private readonly destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.api
      .get()
      .pipe(
        catchError((error) => {
          this.error.set(userFacingApiError(error, 'No fue posible consultar las actividades.'));
          return of([] as Activity[]);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((activities) => {
        this.activities.set(activities);
        this.isLoading.set(false);
      });
  }

  protected onFilter(event: Event): void {
    this.filter.set((event.target as HTMLInputElement).value);
  }

  protected clearFilter(): void {
    this.filter.set('');
  }
}
