import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Activity } from '@features/administration/activities/domain/models/activity.model';

/** Consulta HTTP read-only del catálogo de actividades. */
@Injectable({ providedIn: 'root' })
export class ActivityApiService {
  private readonly http = inject(HttpClient);

  get(): Observable<Activity[]> {
    return this.http.get<Activity[]>('/api/v1/administracion/actividades');
  }
}
