import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Resuelve rutas API absolutas bajo la base pública de la SPA.
 *
 * <p>Los servicios conservan contratos relativos al dominio ({@code /api/...}).
 * En producción la SPA vive bajo {@code /anexo24/}, por lo que la solicitud se
 * reescribe a {@code /anexo24/api/...}. En desarrollo, con base {@code /}, la
 * ruta resultante no cambia.</p>
 */
export const apiPrefixInterceptor: HttpInterceptorFn = (request, next) => {
  const url = resolveApiUrl(request.url, document.baseURI);
  return next(url === request.url ? request : request.clone({ url }));
};

/**
 * Resuelve una ruta API absoluta contra la base pública de la aplicación.
 *
 * @param url ruta solicitada por un servicio HTTP
 * @param baseUri URI base expuesta por el documento
 * @return ruta con el prefijo público de la aplicación cuando corresponde
 */
export function resolveApiUrl(url: string, baseUri: string): string {
  if (!url.startsWith('/api/')) return url;

  const resolved = new URL(url.slice(1), baseUri);
  return `${resolved.pathname}${resolved.search}${resolved.hash}`;
}
