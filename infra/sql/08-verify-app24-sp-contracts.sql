USE CALE_IMMEX;
GO

SET NOCOUNT ON;

/*
 * Gate read-only de despliegue APP24 en CALE_IMMEX.
 * Verifica objetos, firmas posicionales, columnas que consumen los adapters y
 * dependencias. No crea ni modifica objetos. Requiere ejecutarse contra CALE_IMMEX.
 */
IF DB_NAME() <> 'CALE_IMMEX'
    THROW 51000, 'APP24_SP_CONTRACTS_WRONG_DATABASE: ejecute este gate contra CALE_IMMEX.', 1;

DECLARE @fallos TABLE (detalle NVARCHAR(400));

DECLARE @requeridos TABLE (nombre SYSNAME);
INSERT INTO @requeridos VALUES
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR'),
    ('APP24_Q_PRODUCTOS_LISTAR'),
    ('APP24_Q_MATERIALES_LISTAR'),
    ('APP24_Q_SALDOS_LISTAR'),
    ('APP24_Q_COMPULSA_DETALLE_LISTAR'),
    ('APP24_Q_RECTIFICACIONES_DETALLE_LISTAR'),
    ('APP24_Q_ANEXO30_REVISION_ENTRADAS_LISTAR'),
    ('APP24_Q_ANEXO30_REVISION_FRACCIONES_LISTAR'),
    ('APP24_Q_ANEXO30_REVISION_DESCARGAS_LISTAR'),
    ('APP24_Q_ANEXO30_REVISION_COMPARATIVA_LISTAR'),
    ('APP24_Q_ANEXO30_INVENTARIO_INICIAL_LISTAR'),
    ('APP24_C_ACTA_CARGA_CONFIRMAR'),
    ('APP24_C_AGENTE_CARGA_CONFIRMAR'),
    ('APP24_C_CLIENTE_CARGA_CONFIRMAR'),
    ('APP24_C_CONSTANCIA_CARGA_CONFIRMAR'),
    ('APP24_C_MATERIAL_CARGA_CONFIRMAR'),
    ('APP24_C_PRODUCTO_CARGA_CONFIRMAR'),
    ('APP24_C_PROVEEDOR_CARGA_CONFIRMAR'),
    ('PR_INFORME_SALDOS'),
    ('v_compulsa'),
    ('v_rectificaciones');

INSERT INTO @fallos
SELECT 'FALTA dbo.' + nombre
FROM @requeridos
WHERE OBJECT_ID('dbo.' + nombre) IS NULL;

DECLARE @parametros TABLE (procedimiento SYSNAME, posicion INT, nombre SYSNAME, tipo SYSNAME, largo INT NULL, salida BIT);
INSERT INTO @parametros VALUES
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 1, '@Desde', 'date', NULL, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 2, '@Hasta', 'date', NULL, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 3, '@Pedimento', 'varchar', 20, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 4, '@ClavePedimento', 'varchar', 5, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 5, '@NumeroParte', 'varchar', 50, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 6, '@Descripcion', 'varchar', 250, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 7, '@Serie', 'varchar', 50, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 8, '@Marca', 'varchar', 50, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 9, '@Modelo', 'varchar', 50, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 10, '@Pagina', 'int', NULL, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 11, '@Tamano', 'int', NULL, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 12, '@Total', 'bigint', NULL, 1),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 13, '@PedimentoOp', 'varchar', 10, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 14, '@ClavePedimentoOp', 'varchar', 10, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 15, '@NumeroParteOp', 'varchar', 10, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 16, '@DescripcionOp', 'varchar', 10, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 17, '@SerieOp', 'varchar', 10, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 18, '@MarcaOp', 'varchar', 10, 0),
    ('APP24_Q_ACTIVOS_FIJOS_LISTAR', 19, '@ModeloOp', 'varchar', 10, 0),
    ('APP24_Q_PRODUCTOS_LISTAR', 1, '@Filtro', 'varchar', 250, 0),
    ('APP24_Q_PRODUCTOS_LISTAR', 2, '@Pagina', 'int', NULL, 0),
    ('APP24_Q_PRODUCTOS_LISTAR', 3, '@Tamano', 'int', NULL, 0),
    ('APP24_Q_PRODUCTOS_LISTAR', 4, '@Total', 'bigint', NULL, 1),
    ('APP24_Q_MATERIALES_LISTAR', 1, '@Filtro', 'varchar', 250, 0),
    ('APP24_Q_MATERIALES_LISTAR', 2, '@Pagina', 'int', NULL, 0),
    ('APP24_Q_MATERIALES_LISTAR', 3, '@Tamano', 'int', NULL, 0),
    ('APP24_Q_MATERIALES_LISTAR', 4, '@Total', 'bigint', NULL, 1),
    ('APP24_Q_SALDOS_LISTAR', 1, '@DESDE', 'datetime', NULL, 0),
    ('APP24_Q_SALDOS_LISTAR', 2, '@HASTA', 'datetime', NULL, 0),
    ('APP24_Q_SALDOS_LISTAR', 3, '@documento', 'varchar', 50, 0),
    ('APP24_Q_SALDOS_LISTAR', 4, '@Pagina', 'int', NULL, 0),
    ('APP24_Q_SALDOS_LISTAR', 5, '@Tamano', 'int', NULL, 0),
    ('APP24_Q_SALDOS_LISTAR', 6, '@Total', 'bigint', NULL, 1),
    ('APP24_Q_COMPULSA_DETALLE_LISTAR', 1, '@Filtro', 'varchar', 60, 0),
    ('APP24_Q_COMPULSA_DETALLE_LISTAR', 2, '@Pagina', 'int', NULL, 0),
    ('APP24_Q_COMPULSA_DETALLE_LISTAR', 3, '@Tamano', 'int', NULL, 0),
    ('APP24_Q_COMPULSA_DETALLE_LISTAR', 4, '@Total', 'bigint', NULL, 1),
    ('APP24_Q_RECTIFICACIONES_DETALLE_LISTAR', 1, '@Filtro', 'varchar', 60, 0),
    ('APP24_Q_RECTIFICACIONES_DETALLE_LISTAR', 2, '@Pagina', 'int', NULL, 0),
    ('APP24_Q_RECTIFICACIONES_DETALLE_LISTAR', 3, '@Tamano', 'int', NULL, 0),
    ('APP24_Q_RECTIFICACIONES_DETALLE_LISTAR', 4, '@Total', 'bigint', NULL, 1);

INSERT INTO @fallos
SELECT 'PARAMETRO ' + e.procedimiento + ' pos ' + CAST(e.posicion AS VARCHAR(2)) + ' esperado ' + e.nombre
FROM @parametros e
WHERE OBJECT_ID('dbo.' + e.procedimiento, 'P') IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys.parameters p
      WHERE p.object_id = OBJECT_ID('dbo.' + e.procedimiento)
        AND p.parameter_id = e.posicion
        AND p.name = e.nombre
        AND TYPE_NAME(p.user_type_id) = e.tipo
        AND (e.largo IS NULL OR p.max_length = e.largo)
        AND p.is_output = e.salida
  );

DECLARE @columnas TABLE (procedimiento SYSNAME, columna SYSNAME);
INSERT INTO @columnas VALUES
    ('APP24_Q_PRODUCTOS_LISTAR', 'PRODUCTOKEY'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'CVE_PRODUCTO'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'CVE_PRODUCTO_CLIENTE'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'NOMBRE'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'fraccion'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'UNIDAD'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'UNIDADT'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'NICO'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'TIPO'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'AUXILIAR'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'ALMACENKEY'),
    ('APP24_Q_PRODUCTOS_LISTAR', 'ALMACEN'),
    ('APP24_Q_MATERIALES_LISTAR', 'materialkey'),
    ('APP24_Q_MATERIALES_LISTAR', 'clave'),
    ('APP24_Q_MATERIALES_LISTAR', 'descripcion'),
    ('APP24_Q_MATERIALES_LISTAR', 'fraccion'),
    ('APP24_Q_MATERIALES_LISTAR', 'unidad'),
    ('APP24_Q_MATERIALES_LISTAR', 'unidadt'),
    ('APP24_Q_MATERIALES_LISTAR', 'tipomaterial'),
    ('APP24_Q_MATERIALES_LISTAR', 'tipo'),
    ('APP24_Q_MATERIALES_LISTAR', 'FactorUM'),
    ('APP24_Q_MATERIALES_LISTAR', 'IGIE'),
    ('APP24_Q_MATERIALES_LISTAR', 'claveProveedor'),
    ('APP24_Q_MATERIALES_LISTAR', 'TIPOM'),
    ('APP24_Q_MATERIALES_LISTAR', 'FAMILIA'),
    ('APP24_Q_MATERIALES_LISTAR', 'NICO'),
    ('APP24_Q_MATERIALES_LISTAR', 'ALMACENKEY'),
    ('APP24_Q_MATERIALES_LISTAR', 'ALMACEN');

INSERT INTO @fallos
SELECT 'COLUMNA ' + c.procedimiento + '.' + c.columna
FROM @columnas c
WHERE OBJECT_ID('dbo.' + c.procedimiento, 'P') IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys.dm_exec_describe_first_result_set_for_object(OBJECT_ID('dbo.' + c.procedimiento), 0) r
      WHERE LOWER(r.name) = LOWER(c.columna)
  );

IF EXISTS (SELECT 1 FROM @fallos)
BEGIN
    SELECT detalle FROM @fallos ORDER BY detalle;
    THROW 51000, 'APP24_SP_CONTRACTS_FAILED: despliegue de procedimientos incompleto o desalineado.', 1;
END

PRINT 'APP24_SP_CONTRACTS|PASS';
