# Despliegue

## Flujo CI/CD

El workflow `CI` conserva sus gates en GitHub-hosted runners para `main`, `dev`,
`feature/**` y pull requests. Sólo un `push` a `main` que apruebe los jobs
`sp-first-gate`, `backend` y `frontend` puede publicar imágenes en GitHub
Container Registry (GHCR).

El despliegue al VPS usa el runner autoalojado existente y sólo se ejecuta si la
variable del environment `production` `ANEXO24_DEPLOY_ENABLED` tiene exactamente
el valor `true`. Por lo tanto, ningún cambio de `dev`, `feature/**` o un pull
request publica imágenes ni modifica el servidor.

La promoción usa los digests inmutables de:

- `ghcr.io/jovycandy/cale-anexo24-backend`
- `ghcr.io/jovycandy/cale-anexo24-frontend`

El VPS únicamente descarga imágenes y ejecuta Compose; nunca compila Gradle ni
Angular durante el despliegue.

## Preparación única en GitHub

Antes de habilitar el deploy, crear la variable de environment ANEXO24_FRONTEND_PORT con un puerto libre validado en el VPS. No usar 8088: está reservado por phpMyAdmin.

1. Crear el environment `production` y restringirlo a `main`.
2. Guardar allí estos secretos: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`,
   `APP_DB_URL`, `APP_DB_USERNAME`, `APP_DB_PASSWORD`, `JWT_SECRET`,
   `JWT_EXPIRATION_MINUTES` y `APP_CORS_ALLOWED_ORIGINS`.
3. Definir `APP_CORS_ALLOWED_ORIGINS` con el origen exacto
   `https://jovycandy.com.mx`; la SPA usa same-origin bajo `/anexo24`.
4. Configurar revisores requeridos y permitir despliegues únicamente desde
   `main`.
5. Mantener `ANEXO24_DEPLOY_ENABLED=false` hasta completar la preparación de
   red, proxy y secretos en el VPS. Cambiarla a `true` inmediatamente antes de
   fusionar a `main` el primer cambio que se autorice desplegar.

No registrar secretos en archivos versionados ni en la configuración del
runner. El `GITHUB_TOKEN` del job se usa únicamente para leer las imágenes
privadas de GHCR durante el despliegue.

## Publicación web

El frontend escucha sólo en 127.0.0.1:PUERTO_ANEXO24; no queda expuesto directamente a Internet. El proxy HTTPS existente del VPS debe conservar el prefijo al enviar la aplicación al contenedor:

```nginx
location = /anexo24 {
    return 302 /anexo24/login;
}

location ^~ /anexo24/ {
    client_max_body_size 51m;
    proxy_pass http://127.0.0.1:PUERTO_ANEXO24;
    proxy_http_version 1.1;
    proxy_connect_timeout 15s;
    proxy_read_timeout 60s;
    proxy_send_timeout 60s;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Host $host;
    proxy_set_header X-Forwarded-Port 443;
}
```

El workflow instala el manifiesto sin secretos en /opt/apps/anexo24/compose.prod.yml. Las fuentes permanecen en GitHub y en el workspace efímero del runner; el VPS conserva únicamente el manifiesto operativo e imágenes Docker.

La directiva `proxy_pass` no lleva una URI final para preservar `/anexo24`.
Este bloque se incorpora al virtual host HTTPS existente de
`jovycandy.com.mx`; no crea otro sitio ni publica el backend Spring Boot.

## Verificación posterior

1. Confirmar que los dos servicios están en ejecución y el backend está
   saludable.
2. Abrir `https://jovycandy.com.mx/anexo24/login`.
3. Probar login con una cuenta autorizada, una consulta de sólo lectura y la
   bitácora.
4. Registrar digest de ambas imágenes, hora, responsable y resultado.

## Reversión

Conservar los dos digests de la versión anterior. Para revertir, actualizar
`BACKEND_IMAGE` y `FRONTEND_IMAGE` a dichos digests y ejecutar Compose de nuevo;
no se ejecutan cambios estructurales en Módulo C como parte de la reversión.
