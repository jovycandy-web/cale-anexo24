--

CREATE PROCEDURE [dbo].[PR_INFORME_IMPORTACIONES]
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
        ([Aduana]                            CHAR(10),
         [Patente]                           CHAR(10),
         [Documento]                         CHAR(20),
         [Fecha de pago]                     DATETIME,
         [Tipo de operacion]                 CHAR(25),
         [Codigo de proveedor]               CHAR(150),
         [Clave de Pedimento]                CHAR(5),
         [Nombre Proveedor]                  VARCHAR(250),
         [RFC/TAX ID]                        CHAR(20),
         [tc]                                FLOAT,
         [IVA]                               FLOAT,
         [DTA]                               FLOAT,
         [Prevalidacion]                     FLOAT,
         [Numero de parte]                   VARCHAR(50),
         [Descripcion mercancia]             VARCHAR(250),
         [Fraccion Arancelaria]              VARCHAR(20),
         [Cantidad importada comercial]      NUMERIC(18, 4),
         [Unidad de medida comercial]        VARCHAR(10),
         [Valor en aduana]                   NUMERIC(18, 4),
         [Valor en dolares]                  NUMERIC(18, 4),
         [Pais Origen]                       VARCHAR(50),
         [Tasa IGI]                          FLOAT,
         [Saldo en Unidad Comercial]         NUMERIC(18, 4),
         [Categoria]                         CHAR(5),
         [Cantidad en Unidad Tarifa]         FLOAT,
         [Unidad de medida tarifa]           CHAR(5),
         [Valor comercial]                   FLOAT,
         [Numero de Factura]                 CHAR(50),
         [Secuencia del pedimento]           INT,
         [Monto IGI]                         FLOAT,
         [Forma de pago IGI]                 FLOAT,
         [Monto IVA]                         FLOAT,
         [Forma de pago IVA]                 FLOAT,
         [Tasa Cuota Compensatoria]          FLOAT,
         [Monto Cuota Compensatoria]         FLOAT,
         [Forma de pago Cuota Compensatoria] FLOAT,
         [Tasa IVA]                          FLOAT,
         [Fecha de Factura]                  DATETIME,
         [Pais Vendedor]                     VARCHAR(50),
         [DESCARGA]                          VARCHAR(2),
         [PedimentoOriginal]                 VARCHAR(19),
         [Lote]                              VARCHAR(50),
         [Complemento 1]                     VARCHAR(50),
         [Complemento 2]                     VARCHAR(50),
         [Complemento 3]                     VARCHAR(50),
         [CNT]                               NUMERIC(18, 4),
         [pu]                                FLOAT,
         [Saldo en pesos valorcomercial]     FLOAT,
         [Saldo en pesos de IVA]             FLOAT,
         [COVE]                              VARCHAR(100),
         [partidakey]                        NUMERIC(18, 0),
         [ipedimentokey]                     NUMERIC(18, 0),
         [Usuario Modifico]                  VARCHAR(100),
         [Fecha Modificacion]                DATETIME,
         [Aplica a 303]                      VARCHAR(2),
         [observaciones]                     CHAR(150),
         [Tipo Material]                     VARCHAR(150),
         [SEGUROS]                           NUMERIC(1, 1),
         [FLETES]                            NUMERIC(1, 1),
         [EMBALAJES]                         NUMERIC(1, 1),
         [OTROS]                             NUMERIC(1, 1),
         [INCOTERM]                          VARCHAR(1),
         [FACTORINCREMENTABLES]              FLOAT,
         [IDENTIFICADORPARTIDA]              VARCHAR(1),
         [FechaCreacionImportacion]          DATETIME,
         [FechaCreacionPartida]              DATETIME,
         [Fecha de entrada]                  DATETIME,
         [NICO]                              VARCHAR(10),
         [marca]                             VARCHAR(150),
         [modelo]                            VARCHAR(150),
         [serie]                             VARCHAR(150),
         [FactorME]                          FLOAT,
         [ESTADO]                            VARCHAR(50),
         [DIAS RESTANTES]                    INT,
         [TEMPORALIDAD EN MESES]             INT,
         [FECHA VENCIMIENTO]                 DATETIME
        );

        -- inserto el resultado

        INSERT INTO @res
        ([Aduana],
         [Patente],
         [Documento],
         [Fecha de pago],
         [Tipo de operacion],
         [Codigo de proveedor],
         [Clave de Pedimento],
         [Nombre Proveedor],
         [RFC/TAX ID],
         [tc],
         [IVA],
         [DTA],
         [Prevalidacion],
         [Numero de parte],
         [Descripcion mercancia],
         [Fraccion Arancelaria],
         [Cantidad importada comercial],
         [Unidad de medida comercial],
         [Valor en aduana],
         [Valor en dolares],
         [Pais Origen],
         [Tasa IGI],
         [Saldo en Unidad Comercial],
         [Categoria],
         [Cantidad en Unidad Tarifa],
         [Unidad de medida tarifa],
         [Valor comercial],
         [Numero de Factura],
         [Secuencia del pedimento],
         [Monto IGI],
         [Forma de pago IGI],
         [Monto IVA],
         [Forma de pago IVA],
         [Tasa Cuota Compensatoria],
         [Monto Cuota Compensatoria],
         [Forma de pago Cuota Compensatoria],
         [Tasa IVA],
         [Fecha de Factura],
         [Pais Vendedor],
         [DESCARGA],
         [PedimentoOriginal],
         [Lote],
         [Complemento 1],
         [Complemento 2],
         [Complemento 3],
         [CNT],
         [pu],
         [Saldo en pesos valorcomercial],
         [Saldo en pesos de IVA],
         [COVE],
         [partidakey],
         [ipedimentokey],
         [Usuario Modifico],
         [Fecha Modificacion],
         [Aplica a 303],
         [observaciones],
         [Tipo Material],
         [SEGUROS],
         [FLETES],
         [EMBALAJES],
         [OTROS],
         [INCOTERM],
         [FACTORINCREMENTABLES],
         [IDENTIFICADORPARTIDA],
         [FechaCreacionImportacion],
         [FechaCreacionPartida],
         [Fecha de entrada],
         [NICO],
         [marca],
         [modelo],
         [serie],
         [FactorME],
         [ESTADO]
        )
               SELECT dbo.Importaciones.Aduana,
                      dbo.Importaciones.Patente,
                      dbo.Importaciones.Numero_ped AS Documento,
                      dbo.Importaciones.Fecha AS [Fecha de pago],
                      dbo.Importaciones.TipoOper AS [Tipo de operacion],
                      substring(ISNULL(dbo.PARTIDAS.Proveedor, '-'),1,100) AS [Codigo de proveedor],
                      dbo.Importaciones.Cve_pedimento AS [Clave de Pedimento],
                      ISNULL(dbo.Proveedores.Nombre, '-') AS [Nombre Proveedor],
                      ISNULL(dbo.Proveedores.Idfiscal, '-') AS [RFC/TAX ID],
                      dbo.Importaciones.tc,
                      ISNULL(dbo.Importaciones.iva, 0) AS IVA,
                      ISNULL(dbo.Importaciones.dta, 0) AS DTA,
                      ISNULL(dbo.Importaciones.prevalidacion, 0) AS Prevalidacion,
                      dbo.partidas.Clave AS [Numero de parte],
                      dbo.partidas.Descripcion AS [Descripcion mercancia],
                      dbo.partidas.Fraccion AS [Fraccion Arancelaria],
                      dbo.partidas.Cantidad AS [Cantidad importada comercial],
                      dbo.partidas.Unidad AS [Unidad de medida comercial],
                      ROUND(ISNULL(dbo.partidas.Val_aduanal, 0), 3) AS [Valor en aduana],
                      ROUND(ISNULL(dbo.partidas.Val_dolares, 0), 3) AS [Valor en dolares],
                      ISNULL(dbo.partidas.Origen, '-') AS [Pais Origen],
                      ROUND(ISNULL(dbo.partidas.Arancel, 0), 3) AS [Tasa IGI],
                      ROUND(ISNULL(dbo.partidas.Saldo, 0), 3) AS [Saldo en Unidad Comercial],
                      dbo.partidas.Categoria,
                      ROUND(ISNULL(dbo.partidas.CantidadT, 0), 3) AS [Cantidad en Unidad Tarifa],
                      dbo.partidas.UnidadT AS [Unidad de medida tarifa],
                      ROUND(ISNULL(dbo.partidas.Val_comercial, 0), 3) AS [Valor comercial],
                      ISNULL(dbo.partidas.Factura, '-') AS [Numero de Factura],
                      ROUND(ISNULL(dbo.partidas.partida, 0), 0) AS [Secuencia del pedimento],
                      ROUND(ISNULL(dbo.partidas.Montoigi, 0), 3) AS [Monto IGI],
                      ROUND(ISNULL(dbo.partidas.Fpigi, 0), 0) AS [Forma de pago IGI],
                      ROUND(ISNULL(dbo.partidas.Montoiva, 0), 3) AS [Monto IVA],
                      ROUND(ISNULL(dbo.partidas.Fpiva, 0), 0) AS [Forma de pago IVA],
                      ROUND(ISNULL(dbo.partidas.TasaCCompen, 0), 3) AS [Tasa Cuota Compensatoria],
                      ROUND(ISNULL(dbo.partidas.MontoCCompen, 0), 3) AS [Monto Cuota Compensatoria],
                      ROUND(ISNULL(dbo.partidas.FpCCompen, 0), 0) AS [Forma de pago Cuota Compensatoria],
                      ROUND(ISNULL(dbo.partidas.Tasaiva, 0), 3) AS [Tasa IVA],
                      dbo.partidas.FechaFactura AS [Fecha de Factura],
                      ISNULL(dbo.partidas.paisvendedor, '-') AS [Pais Vendedor],
                      dbo.Importaciones.DESCARGA,
                      dbo.Importaciones.PedimentoOriginal,
                      ISNULL(dbo.partidas.Lote, '') AS Lote,
                      ISNULL(dbo.partidas.complemento1, '') AS [Complemento 1],
                      ISNULL(dbo.partidas.complemento2, '') AS [Complemento 2],
                      ISNULL(dbo.partidas.complemento3, '') AS [Complemento 3],
                      dbo.Importaciones.CNT,
                      ROUND((dbo.partidas.val_comercial / dbo.partidas.cantidad), 2) pu,
                      ROUND(((dbo.partidas.val_comercial / dbo.partidas.cantidad) * saldo), 2) [Saldo en pesos valorcomercial],
                      ROUND(((dbo.partidas.val_comercial / dbo.partidas.cantidad) * saldo) * .16, 2) [Saldo en pesos de IVA],
                      ISNULL(dbo.partidas.COVE, '') COVE,
                      partidakey,
                      ipedimentokey,
                      partidas.usuario [Usuario Modifico],
                      partidas.fechamodificacion [Fecha Modificacion],
                      CASE
                          WHEN ISNULL(partidas.aplicaa, 0) = 0
                          THEN 'NO'
                          ELSE 'si'
                      END [Aplica a 303],
                      importaciones.observaciones,
                      dbo.BUSCATIPOM(dbo.partidas.clave) [Tipo Material],
                      0.0 SEGUROS,
                      0.0 FLETES,
                      0.0 EMBALAJES,
                      0.0 OTROS,
                      '' INCOTERM,
                      partidas.FacInc FACTORINCREMENTABLES,
                      '' IDENTIFICADORPARTIDA,
                      dbo.Importaciones.FCreacion FechaCreacionImportacion,
                      dbo.partidas.FCreacion FechaCreacionPartida,
                      dbo.importaciones.fecha_ad [Fecha de entrada],
                      dbo.partidas.NICO,
                      dbo.partidas.marca,
                      dbo.partidas.modelo,
                      dbo.partidas.serie,
                      dbo.importaciones.[FactorME],
                      '' ESTADO
               FROM dbo.Importaciones
                    INNER JOIN dbo.partidas ON dbo.Importaciones.Ipedimentokey = dbo.partidas.Importacionlink
                    LEFT OUTER JOIN dbo.Proveedores ON dbo.PARTIDAS.Proveedor = dbo.Proveedores.Clave
               WHERE IMPORTACIONES.Fecha BETWEEN COALESCE(@desde, importaciones.fecha) AND COALESCE(@hasta, importaciones.fecha)
                     AND importaciones.Numero_ped = COALESCE(@documento, importaciones.Numero_ped);

        -- update estado

        UPDATE X
          SET
              [TEMPORALIDAD EN MESES] = ISNULL(
        (
            SELECT TOP 1 meses
            FROM categorias
            WHERE categoria = X.CATEGORIA
        ), 0)
        FROM @RES X;

        --

        UPDATE X
          SET
              [FECHA VENCIMIENTO] = DATEADD(month, ISNULL([TEMPORALIDAD EN MESES], 0), [Fecha de pago])
        FROM @RES x;

        --

        UPDATE X
          SET
              [DIAS RESTANTES] = DATEDIFF(day, GETDATE(), [FECHA VENCIMIENTO])
        FROM @RES x;

        -- ACTIVOS FIJOS

        UPDATE X
          SET
              [ESTADO] = 'NA'
        FROM @RES X
        WHERE X.[ESTADO] = ''
              AND X.[Clave de Pedimento] = 'AF';

        -- SALDO EN CERO

        UPDATE X
          SET
              [ESTADO] = 'SIN SALDO ACTIVO'
        FROM @RES X
        WHERE X.[ESTADO] = ''
              AND X.[Saldo en Unidad Comercial] <= 0;

        -- SALDO VIVO
        UPDATE X
          SET
              [ESTADO] = 'SALDO ACTIVO'
        FROM @RES X
        WHERE X.[ESTADO] = ''
              AND X.[Saldo en Unidad Comercial] > 0
              AND X.[DIAS RESTANTES] > 0;

        -- SALDO VENCIDO

        UPDATE X
          SET
              [ESTADO] = 'SALDO VENCIDO'
        FROM @RES X
        WHERE X.[ESTADO] = ''
              AND X.[Saldo en Unidad Comercial] > 0
              AND X.[DIAS RESTANTES] <= 0;

        -- RESULTADO

        SELECT [Aduana],
               [Patente],
               [Documento],
               [Fecha de pago],
               [Tipo de operacion],
               [Codigo de proveedor],
               [Clave de Pedimento],
               [Nombre Proveedor],
               [RFC/TAX ID],
               [tc],
               [IVA],
               [DTA],
               [Prevalidacion],
               [Numero de parte],
               [Descripcion mercancia],
               [Fraccion Arancelaria],
               [Cantidad importada comercial],
               [Unidad de medida comercial],
               [Valor en aduana],
               [Valor en dolares],
               [Pais Origen],
               [Tasa IGI],
               [Saldo en Unidad Comercial],
               [Categoria],
               [Cantidad en Unidad Tarifa],
               [Unidad de medida tarifa],
               [Valor comercial],
               [Numero de Factura],
               [Secuencia del pedimento],
               [Monto IGI],
               [Forma de pago IGI],
               [Monto IVA],
               [Forma de pago IVA],
               [Tasa Cuota Compensatoria],
               [Monto Cuota Compensatoria],
               [Forma de pago Cuota Compensatoria],
               [Tasa IVA],
               [Fecha de Factura],
               [Pais Vendedor],
               [DESCARGA],
               [PedimentoOriginal],
               [Lote],
               [Complemento 1],
               [Complemento 2],
               [Complemento 3],
               [CNT],
               [pu],
               [Saldo en pesos valorcomercial],
               [Saldo en pesos de IVA],
               [COVE],
               [partidakey],
               [ipedimentokey],
               [Usuario Modifico],
               [Fecha Modificacion],
               [Aplica a 303],
               [observaciones],
               [Tipo Material],
               [SEGUROS],
               [FLETES],
               [EMBALAJES],
               [OTROS],
               [INCOTERM],
               [FACTORINCREMENTABLES],
               [IDENTIFICADORPARTIDA],
               [FechaCreacionImportacion],
               [FechaCreacionPartida],
               [Fecha de entrada],
               [NICO],
               [marca],
               [modelo],
               [serie],
               [FactorME],
               [ESTADO],
               [DIAS RESTANTES],
               [TEMPORALIDAD EN MESES],
               [FECHA VENCIMIENTO]
        FROM @res
        ORDER BY [Fecha de pago],
                 Documento,
                 [Secuencia del pedimento];
    END
