USE CALE_IMMEX;
GO

CREATE OR ALTER PROCEDURE dbo.APP24_Q_MATERIALES_LISTAR
    @Filtro VARCHAR(250) = NULL,
    @Pagina INT = 1,
    @Tamano INT = 20,
    @Total BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF @Pagina IS NULL OR @Pagina < 1
       OR @Tamano IS NULL OR @Tamano < 1 OR @Tamano > 100
    BEGIN
        THROW 50051, 'Parámetros de paginación inválidos.', 1;
    END;

    SET @Filtro = NULLIF(LTRIM(RTRIM(@Filtro)), '');

    -- El filtro es global y del lado del servidor: cubre las columnas de
    -- texto expuestas y el nombre del almacén resuelto.
    SELECT @Total = COUNT_BIG(1)
    FROM dbo.material AS m
    OUTER APPLY (
        SELECT TOP (1) ALMACEN
        FROM dbo.almacen
        WHERE ALMACENKEY = m.ALMACENKEY
    ) AS a
    WHERE @Filtro IS NULL
       OR m.clave LIKE '%' + @Filtro + '%'
       OR m.claveProveedor LIKE '%' + @Filtro + '%'
       OR m.descripcion LIKE '%' + @Filtro + '%'
       OR m.fraccion LIKE '%' + @Filtro + '%'
       OR m.unidad LIKE '%' + @Filtro + '%'
       OR m.unidadt LIKE '%' + @Filtro + '%'
       OR m.tipomaterial LIKE '%' + @Filtro + '%'
       OR m.tipo LIKE '%' + @Filtro + '%'
       OR m.TIPOM LIKE '%' + @Filtro + '%'
       OR m.FAMILIA LIKE '%' + @Filtro + '%'
       OR m.NICO LIKE '%' + @Filtro + '%'
       OR a.ALMACEN LIKE '%' + @Filtro + '%'
       OR CONVERT(VARCHAR(20), m.ALMACENKEY) LIKE '%' + @Filtro + '%';

    -- Columnas expuestas según SCR-084 §C. Verificado por metadata read-only
    -- contra CALE_IMMEX: CLAVEPROVEEDOR, TIPOM, FAMILIA, NICO y ALMACENKEY
    -- existen en dbo.material; el nombre de almacén se resuelve sin DDL nuevo.
    SELECT
        m.materialkey,
        m.clave,
        m.descripcion,
        m.fraccion,
        m.unidad,
        m.unidadt,
        m.tipomaterial,
        m.tipo,
        m.FactorUM,
        m.IGIE,
        m.claveProveedor,
        m.TIPOM,
        m.FAMILIA,
        m.NICO,
        m.ALMACENKEY,
        a.ALMACEN
    FROM dbo.material AS m
    OUTER APPLY (
        SELECT TOP (1) ALMACEN
        FROM dbo.almacen
        WHERE ALMACENKEY = m.ALMACENKEY
    ) AS a
    WHERE @Filtro IS NULL
       OR m.clave LIKE '%' + @Filtro + '%'
       OR m.claveProveedor LIKE '%' + @Filtro + '%'
       OR m.descripcion LIKE '%' + @Filtro + '%'
       OR m.fraccion LIKE '%' + @Filtro + '%'
       OR m.unidad LIKE '%' + @Filtro + '%'
       OR m.unidadt LIKE '%' + @Filtro + '%'
       OR m.tipomaterial LIKE '%' + @Filtro + '%'
       OR m.tipo LIKE '%' + @Filtro + '%'
       OR m.TIPOM LIKE '%' + @Filtro + '%'
       OR m.FAMILIA LIKE '%' + @Filtro + '%'
       OR m.NICO LIKE '%' + @Filtro + '%'
       OR a.ALMACEN LIKE '%' + @Filtro + '%'
       OR CONVERT(VARCHAR(20), m.ALMACENKEY) LIKE '%' + @Filtro + '%'
    ORDER BY m.clave, m.materialkey
    OFFSET (CAST(@Pagina AS BIGINT) - 1) * CAST(@Tamano AS BIGINT) ROWS
    FETCH NEXT @Tamano ROWS ONLY;
END;
GO
