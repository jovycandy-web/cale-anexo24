import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { Activity } from '@features/administration/activities/domain/models/activity.model';
import { ActivityListPage } from './activity-list.page';

const URL_ACTIVIDADES = '/api/v1/administracion/actividades';

const ACTIVIDADES: Activity[] = [
  { id: 1, clave: 'CACT', nombre: 'ACCESO A CATALOGO DE ACTIVIDADES', recurso: 'actividades', accion: 'GET' },
  { id: 2, clave: 'CUSR1', nombre: 'AGREGAR USUARIO', recurso: 'usuarios', accion: 'POST' },
  { id: 3, clave: 'CPRF', nombre: 'ACCESO A CATALOGO PERFILES', recurso: 'perfiles', accion: 'GET' },
];

describe('ActivityListPage', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ActivityListPage],
      providers: [provideNoopAnimations(), provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('renderiza clave y nombre del catálogo', () => {
    const fixture = TestBed.createComponent(ActivityListPage);
    fixture.detectChanges();
    http.expectOne(URL_ACTIVIDADES).flush(ACTIVIDADES);
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent as string;
    expect(text).toContain('CACT');
    expect(text).toContain('ACCESO A CATALOGO DE ACTIVIDADES');
    expect(text).toContain('3 de 3 actividades');
  });

  it('filtra en cliente por clave, nombre o recurso', () => {
    const fixture = TestBed.createComponent(ActivityListPage);
    fixture.detectChanges();
    http.expectOne(URL_ACTIVIDADES).flush(ACTIVIDADES);
    fixture.detectChanges();

    const instance = fixture.componentInstance as unknown as { onFilter(event: Event): void };
    instance.onFilter({ target: { value: 'usuarios' } } as unknown as Event);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('1 de 3 actividades');
    expect(fixture.nativeElement.textContent).toContain('AGREGAR USUARIO');
    expect(fixture.nativeElement.textContent).not.toContain('ACCESO A CATALOGO PERFILES');
  });

  it('expone estado vacío controlado cuando el filtro no coincide', () => {
    const fixture = TestBed.createComponent(ActivityListPage);
    fixture.detectChanges();
    http.expectOne(URL_ACTIVIDADES).flush(ACTIVIDADES);
    fixture.detectChanges();

    const instance = fixture.componentInstance as unknown as { onFilter(event: Event): void };
    instance.onFilter({ target: { value: 'ZZZ_INEXISTENTE' } } as unknown as Event);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Sin coincidencias');
  });

  it('muestra error controlado sin romper la pantalla', () => {
    const fixture = TestBed.createComponent(ActivityListPage);
    fixture.detectChanges();
    http.expectOne(URL_ACTIVIDADES).flush({ message: 'fallo' }, { status: 503, statusText: 'Service Unavailable' });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No pudimos cargar las actividades');
  });
});
