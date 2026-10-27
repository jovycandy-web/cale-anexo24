--
CREATE PROCEDURE [dbo].[PR_INFORME_SALDOS]
(@DESDE     DATETIME,
 @HASTA     DATETIME,
 @documento VARCHAR(50)
)
AS
    BEGIN
        IF ISNULL(@documento, '') = ''
            SET @documento = NULL;
            ELSE
            BEGIN
                SET @DESDE = NULL;
                SET @HASTA = NULL;
        END;

        -- DECLARO RESULTADO

        DECLARE @res TABLE
        ([Documento]               CHAR(20),
         [Fecha de Pago]           DATETIME,
         [Clave Pedimento]         CHAR(5),
         [Tipo de Operacion]       CHAR(25),
         [tc]                      FLOAT,
         [Clave]                   VARCHAR(50),
         [Descripcion]             VARCHAR(250),
         [Fraccion]                VARCHAR(15),
         [Cant. Importado]         NUMERIC(18, 4),
         [Unidad]                  VARCHAR(10),
         [Saldo]                   NUMERIC(18, 4),
         [Valor Aduanal de Saldo]  NUMERIC(38, 6),
         [Valor dolares del saldo] NUMERIC(38, 6),
         [Pais origen]             varchar(10),
         [Temporalidad(Meses)]     INT,
         [Categoria]               CHAR(5),
         [Fecha de Vencimiento]    DATETIME,
         [PedimentoOriginal]       VARCHAR(19),
         [Descarga]                VARCHAR(2),
         [lote]                    VARCHAR(2),
         [Complemento 1]           VARCHAR(50),
         [Complemento 2]           VARCHAR(50),
         [Complemento 3]           VARCHAR(50),
         [Desperdiciado]           NUMERIC(38, 4),
         [Saldodesperdicio]        NUMERIC(38, 4),
         [COVE]                    VARCHAR(100),
         [Factura]                 CHAR(50),
         [Tipo Material]           VARCHAR(150),
         [pu_vad]                  NUMERIC(38, 10),
         [pu_vdo]                  NUMERIC(38, 10),
         [val_aduanal]             NUMERIC(18, 4),
         [val_dolares]             NUMERIC(18, 4),
         [saldo en UMT]            FLOAT,
         [unidadt]                 CHAR(5),
         [valor en pesos]          FLOAT,
         [Saldo en valor pesos]    FLOAT,
         NICO                      VARCHAR(10)
        );

        -- inserto el resultado

        INSERT INTO @res
        ([Documento],
         [Fecha de Pago],
         [Clave Pedimento],
         [Tipo de Operacion],
         [tc],
         [Clave],
         [Descripcion],
         [Fraccion],
         [Cant. Importado],
         [Unidad],
         [Saldo],
         [Valor Aduanal de Saldo],
         [Valor dolares del saldo],
         [Temporalidad(Meses)],
         [Categoria],
         [Fecha de Vencimiento],
         [PedimentoOriginal],
         [Descarga],
         [lote],
         [Complemento 1],
         [Complemento 2],
         [Complemento 3],
         [Desperdiciado],
         [Saldodesperdicio],
         [COVE],
         [Factura],
         [Tipo Material],
         [pu_vad],
         [pu_vdo],
         [val_aduanal],
         [val_dolares],
         [saldo en UMT],
         [unidadt],
         [valor en pesos],
         [Saldo en valor pesos],
         NICO,
         [Pais origen]
        )
               SELECT dbo.Importaciones.Numero_ped AS Documento,
                      dbo.Importaciones.Fecha AS [Fecha de Pago],
                      dbo.Importaciones.Cve_pedimento AS [Clave Pedimento],
                      dbo.Importaciones.TipoOper AS [Tipo de Operacion],
                      dbo.Importaciones.tc,
                      dbo.partidas.Clave,
                      dbo.partidas.Descripcion,
                      dbo.partidas.Fraccion,
                      dbo.partidas.Cantidad AS [Cant. Importado],
                      dbo.partidas.Unidad,
                      ROUND(dbo.partidas.Saldo, 3) AS Saldo,
                      ROUND(dbo.partidas.Val_aduanal / dbo.partidas.Cantidad * dbo.partidas.Saldo, 3) AS [Valor Aduanal de Saldo],
                      ROUND(dbo.partidas.Val_dolares / dbo.partidas.Cantidad * dbo.partidas.Saldo, 3) AS [Valor dolares del saldo],
                      ISNULL(dbo.categorias.meses, 0) AS [Temporalidad(Meses)],
                      dbo.partidas.Categoria,
                      DATEADD(month, ISNULL(dbo.categorias.meses, 0), dbo.Importaciones.Fecha) AS [Fecha de Vencimiento],
                      dbo.Importaciones.PedimentoOriginal,
                      dbo.Importaciones.Descarga,
                      CASE(SELECT COUNT(*)
                           FROM productomaterial
                           WHERE productomaterial.CVE_MATERIAL = partidas.Clave)
                          WHEN 0
                          THEN 'NO'
                          ELSE 'SI'
                      END [lote],
                      ISNULL(partidas.complemento1, '') AS [Complemento 1],
                      ISNULL(partidas.complemento2, '') AS [Complemento 2],
                      ISNULL(partidas.complemento3, '') AS [Complemento 3],
                      ISNULL(
               (
                   SELECT SUM(desperdicio)
                   FROM descarga
                   WHERE pentradalink = partidakey
               ), 0) Desperdiciado,
                      ISNULL(
               (
                   SELECT SUM(saldodesperdicio)
                   FROM descarga
                   WHERE pentradalink = partidakey
               ), 0) Saldodesperdicio,
                      ISNULL(dbo.partidas.cove, '') COVE,
                      ISNULL(dbo.partidas.factura, '') Factura,
                      dbo.BUSCATIPOM(dbo.partidas.clave) [Tipo Material],
                      dbo.partidas.val_aduanal / cantidad pu_vad,
                      dbo.partidas.val_dolares / cantidad pu_vdo,
                      dbo.partidas.val_aduanal,
                      dbo.partidas.val_dolares,
                      (dbo.partidas.saldo * dbo.partidas.cantidadt) / dbo.partidas.cantidad [saldo en UMT],
                      dbo.partidas.unidadt,
                      dbo.partidas.val_comercial [valor en pesos],
                      (dbo.partidas.val_comercial / cantidad) * PARTIDAS.SALDO [Saldo en valor pesos],
                      PARTIDAS.NICO,
                      partidas.origen
               FROM dbo.Importaciones
                    INNER JOIN dbo.partidas ON dbo.Importaciones.Ipedimentokey = dbo.partidas.Importacionlink
                    LEFT OUTER JOIN dbo.categorias ON dbo.partidas.Categoria = dbo.categorias.categoria
               WHERE(dbo.partidas.Categoria <> 'NO')
                    AND (dbo.partidas.Cantidad > 0)
                    AND (ROUND(dbo.partidas.Saldo, 3) > 0
                         OR ISNULL(
               (
                   SELECT SUM(saldodesperdicio)
                   FROM descarga
                   WHERE pentradalink = partidakey
               ), 0) > 0)
                    AND (dbo.Importaciones.Descarga = 'SI')
                    AND Importaciones.Cve_pedimento <> 'AF'
                    AND IMPORTACIONES.Fecha BETWEEN COALESCE(@desde, importaciones.fecha) AND COALESCE(@hasta, importaciones.fecha)
                    AND importaciones.Numero_ped = COALESCE(@documento, importaciones.Numero_ped);

        -- RESULTADO

        SELECT [Documento],
               [Fecha de Pago],
               [Clave Pedimento],
               [Tipo de Operacion],
               [tc],
               [Clave],
               [Descripcion],
               [Fraccion],
               [Cant. Importado],
               [Unidad],
               [Saldo],
               [Valor Aduanal de Saldo],
               [Valor dolares del saldo],
               [Pais origen],
               [Temporalidad(Meses)],
               [Categoria],
               [Fecha de Vencimiento],
               [PedimentoOriginal],
               [Descarga],
               [lote],
               [Complemento 1],
               [Complemento 2],
               [Complemento 3],
               [Desperdiciado],
               [Saldodesperdicio],
               [COVE],
               [Factura],
               [Tipo Material],
               [pu_vad],
               [pu_vdo],
               [val_aduanal],
               [val_dolares],
               [saldo en UMT],
               [unidadt],
               [valor en pesos],
               [Saldo en valor pesos],
               NICO
        FROM @res
        ORDER BY [Fecha de pago],
                 Documento;
    END
