USE CALE_IMMEX;
GO

CREATE OR ALTER PROCEDURE dbo.APP24_Q_PRODUCTOS_LISTAR
    @Filtro VARCHAR(250) = NULL,
    @Pagina INT = 1,
    @Tamano INT = 20,
    @Total BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    -- La validación funcional permanece en ListarProductosUseCase. Estas
    -- defensas evitan un OFFSET inválido si el procedimiento se invoca solo.
    IF @Pagina < 1 OR @Tamano < 1 OR @Tamano > 100
    BEGIN
        THROW 50001, 'Parámetros de paginación inválidos.', 1;
    END;

    SET @Filtro = NULLIF(LTRIM(RTRIM(@Filtro)), '');

    -- El filtro es global y del lado del servidor: cubre todas las columnas
    -- de texto expuestas y también el nombre del almacén resuelto.
    SELECT @Total = COUNT_BIG(1)
    FROM dbo.productos AS p
    OUTER APPLY (
        SELECT TOP (1) ALMACEN
        FROM dbo.almacen
        WHERE ALMACENKEY = p.ALMACENKEY
    ) AS a
    WHERE @Filtro IS NULL
       OR p.CVE_PRODUCTO LIKE '%' + @Filtro + '%'
       OR p.CVE_PRODUCTO_CLIENTE LIKE '%' + @Filtro + '%'
       OR p.NOMBRE LIKE '%' + @Filtro + '%'
       OR p.fraccion LIKE '%' + @Filtro + '%'
       OR p.UNIDAD LIKE '%' + @Filtro + '%'
       OR p.UNIDADT LIKE '%' + @Filtro + '%'
       OR p.NICO LIKE '%' + @Filtro + '%'
       OR p.TIPO LIKE '%' + @Filtro + '%'
       OR p.AUXILIAR LIKE '%' + @Filtro + '%'
       OR a.ALMACEN LIKE '%' + @Filtro + '%'
       OR CONVERT(VARCHAR(20), p.ALMACENKEY) LIKE '%' + @Filtro + '%';

    -- Columnas expuestas según SCR-046 §C y docs/03-diseno/mapeo-productos.md:
    -- CVE_PRODUCTO_CLIENTE, NICO, TIPO, AUXILIAR son campos de dbo.productos y
    -- ALMACENKEY se resuelve contra dbo.almacen (read-only, sin DDL nuevo).
    SELECT
        p.PRODUCTOKEY,
        p.CVE_PRODUCTO,
        p.NOMBRE,
        p.fraccion,
        p.UNIDAD,
        p.UNIDADT,
        p.CVE_PRODUCTO_CLIENTE,
        p.NICO,
        p.TIPO,
        p.AUXILIAR,
        p.ALMACENKEY,
        a.ALMACEN
    FROM dbo.productos AS p
    OUTER APPLY (
        SELECT TOP (1) ALMACEN
        FROM dbo.almacen
        WHERE ALMACENKEY = p.ALMACENKEY
    ) AS a
    WHERE @Filtro IS NULL
       OR p.CVE_PRODUCTO LIKE '%' + @Filtro + '%'
       OR p.CVE_PRODUCTO_CLIENTE LIKE '%' + @Filtro + '%'
       OR p.NOMBRE LIKE '%' + @Filtro + '%'
       OR p.fraccion LIKE '%' + @Filtro + '%'
       OR p.UNIDAD LIKE '%' + @Filtro + '%'
       OR p.UNIDADT LIKE '%' + @Filtro + '%'
       OR p.NICO LIKE '%' + @Filtro + '%'
       OR p.TIPO LIKE '%' + @Filtro + '%'
       OR p.AUXILIAR LIKE '%' + @Filtro + '%'
       OR a.ALMACEN LIKE '%' + @Filtro + '%'
       OR CONVERT(VARCHAR(20), p.ALMACENKEY) LIKE '%' + @Filtro + '%'
    ORDER BY p.CVE_PRODUCTO, p.PRODUCTOKEY
    OFFSET (CAST(@Pagina AS BIGINT) - 1) * CAST(@Tamano AS BIGINT) ROWS
    FETCH NEXT @Tamano ROWS ONLY;
END;
GO
