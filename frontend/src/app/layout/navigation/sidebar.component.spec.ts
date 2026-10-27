import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '@core/auth/auth.service';
import { SidebarComponent } from './sidebar.component';

describe('SidebarComponent', () => {
  const auth = {
    hasPermission: vi.fn(),
    initials: vi.fn(() => 'OP'),
    userName: vi.fn(() => 'Operador'),
  };

  beforeEach(() => {
    auth.hasPermission.mockReset();
    TestBed.configureTestingModule({
      imports: [SidebarComponent],
      providers: [provideNoopAnimations(), provideRouter([]), { provide: AuthService, useValue: auth }],
    });
  });

  it('oculta estructuralmente Usuarios sin el permiso administrativo', () => {
    auth.hasPermission.mockReturnValue(false);
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('Gestión');
    expect(fixture.nativeElement.textContent).not.toContain('Usuarios');
  });

  it('muestra Usuarios pero no Perfiles con autoridad de usuarios solamente', () => {
    auth.hasPermission.mockImplementation((permission: string) => permission === 'USUARIOS_ADMINISTRAR');
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();

    const links = fixture.nativeElement.querySelectorAll('a') as NodeListOf<HTMLAnchorElement>;
    const link = Array.from(links).find((element) => element.textContent?.includes('Usuarios'));
    expect(link?.getAttribute('ng-reflect-router-link') ?? link?.getAttribute('href')).toContain('/usuarios');
    expect(fixture.nativeElement.textContent).not.toContain('Perfiles');
  });

  it('no concede acceso a Perfiles con ACTIVIDADES_ADMINISTRAR solamente', () => {
    auth.hasPermission.mockImplementation((permission: string) => permission === 'ACTIVIDADES_ADMINISTRAR');
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain('Perfiles');
  });

  it('muestra un único acceso a reportes sólo con REPORTES_GENERAR', () => {
    auth.hasPermission.mockImplementation((permission: string) => permission === 'REPORTES_GENERAR');
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();

    const links = Array.from(fixture.nativeElement.querySelectorAll('a') as NodeListOf<HTMLAnchorElement>);
    const reportes = links.filter((element) => element.getAttribute('href') === '/reportes');
    expect(reportes).toHaveLength(1);
    expect(reportes[0].textContent).toContain('Reportes');
  });

  it('mantiene fuera del menú la carga de facturación aunque exista el permiso', () => {
    auth.hasPermission.mockReturnValue(true);
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain('Carga de facturación');
    expect(fixture.nativeElement.textContent).not.toContain('Importaciones de catálogos');
  });

  it('colapsa y expande un grupo sin renderizar encabezados vacíos', () => {
    auth.hasPermission.mockImplementation((permission: string) => permission === 'MATERIALES_CONSULTAR');
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();
    const group = Array.from(fixture.nativeElement.querySelectorAll('button') as NodeListOf<HTMLButtonElement>)
      .find((button) => button.textContent?.includes('Catálogos'))!;
    expect(fixture.nativeElement.textContent).toContain('Materiales');
    group.click(); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain('Materiales');
    expect(fixture.nativeElement.textContent).not.toContain('Gestión');
  });

  it('usa mini rail con menú para grupos al estar contraído', () => {
    auth.hasPermission.mockImplementation((permission: string) => permission === 'MATERIALES_CONSULTAR');
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.componentRef.setInput('collapsed', true);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.rail-group')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('.app-sidebar')?.classList).toContain('sidebar-compact');
  });

  it('emite cierre al navegar en móvil', () => {
    auth.hasPermission.mockReturnValue(false);
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.componentRef.setInput('isMobile', true);
    const emit = vi.spyOn(fixture.componentInstance.mobileClosed, 'emit');
    fixture.detectChanges();
    (fixture.componentInstance as unknown as { navigate(): void }).navigate();
    expect(emit).toHaveBeenCalledOnce();
  });

  it('muestra Perfiles sólo con PERFILES_ADMINISTRAR', () => {
    auth.hasPermission.mockImplementation((permission: string) => permission === 'PERFILES_ADMINISTRAR');
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();
    const links = fixture.nativeElement.querySelectorAll('a') as NodeListOf<HTMLAnchorElement>;
    const link = Array.from(links).find((element) => element.textContent?.includes('Perfiles'));
    expect(link?.getAttribute('ng-reflect-router-link') ?? link?.getAttribute('href')).toContain('/perfiles');
    expect(fixture.nativeElement.textContent).not.toContain('Usuarios');
  });
});
