CREATE PROCEDURE [dbo].[PR_INFORME_ESTRUCTURAS]
(@PRODUCTO VARCHAR(50),
 @MATERIAL VARCHAR(50), 
 @DESDE    DATETIME,
 @HASTA    DATETIME
)
AS
    BEGIN
        DECLARE @res TABLE
        ([Codigo de Producto]         VARCHAR(80), 
         [Descripcion del Producto]   VARCHAR(250), 
         [Unidad de Medida]           CHAR(10), 
         [Fecha de Inicio Estructura] DATETIME, 
         [Fecha de Fin Estructura]    DATETIME, 
         [Codigo de Materia Prima]    VARCHAR(50), 
         [Cantidad Incorporada]       NUMERIC(18, 7), 
         [Cantidad Mermada]           NUMERIC(18, 7), 
         [Cantidad Desperdiciada]     NUMERIC(18, 7), 
         [Codigo A24 Interno]         NUMERIC(18, 0), 
         [Descripcion de MP]          VARCHAR(250), 
         [Unidad MP]                  CHAR(5), 
         [Fraccion MP]                CHAR(10), 
         [Ultima Salida]              DATETIME, 
         [PRODMATKEY]                 NUMERIC(18, 0), 
         EstructuraKey                BIGINT, 
         productokey                  BIGINT
        );

        --

        DECLARE @temp TABLE
        (estructurakey BIGINT, 
         productokey   BIGINT,
         fechainicio   DATETIME, 
         fechafin      DATETIME
        );

        --
        IF ISNULL(@PRODUCTO, '') = ''
            SET @PRODUCTO = NULL;

        --
        IF ISNULL(@MATERIAL, '') = ''
            SET @MATERIAL = NULL;

        --

        INSERT INTO @res
        ([Codigo de Producto], 
         [Descripcion del Producto], 
         [Unidad de Medida], 
         [Fecha de Inicio Estructura], 
         [Codigo de Materia Prima], 
         [Cantidad Incorporada], 
         [Cantidad Mermada], 
         [Cantidad Desperdiciada], 
         [Codigo A24 Interno], 
         [Descripcion de MP], 
         [Unidad MP],
         [Fraccion MP], 
         [Ultima Salida], 
         [PRODMATKEY], 
         estructurakey, 
         productokey
        )
               SELECT LTRIM(RTRIM(dbo.productos.CVE_PRODUCTO)) AS [Codigo de Producto], 
                      dbo.productos.NOMBRE AS [Descripcion del Producto], 
                      dbo.productos.UNIDAD AS [Unidad de Medida], 
                      dbo.estructuras.inicio AS [Fecha de Inicio Estructura], 
                      LTRIM(RTRIM(dbo.productomaterial.CVE_MATERIAL)) AS [Codigo de Materia Prima], 
                      ROUND(dbo.productomaterial.CANT_UTILIZADA, 8) AS [Cantidad Incorporada], 
                      ROUND(dbo.productomaterial.CANT_MERMADA, 8) AS [Cantidad Mermada], 
                      ROUND(dbo.productomaterial.CANT_DESPERDICIADA, 8) AS [Cantidad Desperdiciada], 
                      dbo.productos.PRODUCTOKEY AS [Codigo A24 Interno], 
                      dbo.material.descripcion AS [Descripcion de MP], 
                      dbo.material.unidad AS [Unidad MP], 
                      dbo.material.fraccion AS [Fraccion MP], 
               (
                   SELECT MAX(dbo.salidas.Fecha) AS Expr1
                   FROM dbo.salidas
                        INNER JOIN dbo.psalidas ON dbo.salidas.SalidaKey = dbo.psalidas.Salidalink
                   WHERE(dbo.psalidas.Clave = dbo.productos.CVE_PRODUCTO)
               ) AS [Ultima Salida], 
                      PRODMATKEY, 
                      dbo.estructuras.estructurakey,
                      dbo.estructuras.productolink
               FROM dbo.material
                    RIGHT OUTER JOIN dbo.productomaterial ON dbo.material.clave = dbo.productomaterial.CVE_MATERIAL
                    LEFT OUTER JOIN dbo.estructuras ON dbo.productomaterial.estructuralink = dbo.estructuras.estructurakey
                    RIGHT OUTER JOIN dbo.productos ON dbo.estructuras.productolink = dbo.productos.PRODUCTOKEY
               WHERE dbo.productos.CVE_PRODUCTO = COALESCE(@PRODUCTO, dbo.productos.CVE_PRODUCTO)
                     AND dbo.productomaterial.CVE_MATERIAL = COALESCE(@MATERIAL, dbo.productomaterial.CVE_MATERIAL);

        --estructurakey = 3920;
        -- fecha fin
        INSERT INTO @temp
        (estructurakey, 
         fechainicio,
         productokey
        )
               SELECT DISTINCT 
                      estructurakey, 
                      [Fecha de Inicio Estructura], 
                      productokey
               FROM @res
               WHERE estructurakey IS NOT NULL;
        UPDATE x
          SET 
              fechafin =
        (
            SELECT TOP 1 [inicio]
            FROM dbo.estructuras e
            WHERE e.[productolink] = x.productokey
                  AND e.[estructurakey] <> x.estructurakey
                  AND e.[inicio] > x.fechainicio
            ORDER BY e.inicio ASC
        )
        FROM @temp x;

        -- resultado
        --    select * from @temp 

        UPDATE x
          SET
              [Fecha de Fin Estructura] = fechafin
        FROM @res x
             INNER JOIN @temp t ON x.estructurakey = t.estructurakey;
        SELECT [Codigo de Producto], 
               [Descripcion del Producto], 
               [Unidad de Medida], 
               [Fecha de Inicio Estructura], 
               [Fecha de Fin Estructura], 
               [Codigo de Materia Prima], 
               [Cantidad Incorporada], 
               [Cantidad Mermada], 
               [Cantidad Desperdiciada], 
               [Codigo A24 Interno],
               [Descripcion de MP], 
               [Unidad MP], 
               [Fraccion MP], 
               [Ultima Salida], 
               [PRODMATKEY], 
               estructurakey
        FROM @res
        ORDER BY [Codigo A24 Interno], 
                 [Codigo de Producto], 
                 [Fecha de Inicio Estructura];
    END
