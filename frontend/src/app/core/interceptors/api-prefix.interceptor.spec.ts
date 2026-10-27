import { describe, expect, it } from 'vitest';
import { resolveApiUrl } from './api-prefix.interceptor';

describe('resolveApiUrl', () => {
  it('agrega el prefijo público al llamar la API desde una SPA en subruta', () => {
    expect(resolveApiUrl('/api/v1/auth/login', 'https://jovycandy.com.mx/anexo24/'))
      .toBe('/anexo24/api/v1/auth/login');
  });

  it('conserva parámetros de consulta y rutas ajenas a la API', () => {
    expect(resolveApiUrl('/api/v1/reportes?periodo=2026', 'https://jovycandy.com.mx/anexo24/'))
      .toBe('/anexo24/api/v1/reportes?periodo=2026');
    expect(resolveApiUrl('https://fonts.googleapis.com/css2', 'https://jovycandy.com.mx/anexo24/'))
      .toBe('https://fonts.googleapis.com/css2');
  });

  it('no modifica el comportamiento de desarrollo servido desde la raíz', () => {
    expect(resolveApiUrl('/api/v1/auth/login', 'http://localhost:4200/'))
      .toBe('/api/v1/auth/login');
  });
});
