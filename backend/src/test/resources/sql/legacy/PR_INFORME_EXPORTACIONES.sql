--

CREATE PROCEDURE [dbo].[PR_INFORME_EXPORTACIONES]
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
        ([DIRIGIDO]                VARCHAR(8),
         [Aduana]                  CHAR(10),
         [Patente]                 CHAR(10),
         [Pedimento]               CHAR(100),
         [Fecha de Pago]           DATETIME,
         [Fecha de presentacion]   DATETIME,
         [Codigo de Cliente]       VARCHAR(20),
         [Nombre Cliente]          VARCHAR(250),
         [RFC / Tax Cliente]       CHAR(20),
         [Pais Comprador]          VARCHAR(10),
         [Pais Destino]            VARCHAR(10),
         [TC]                      FLOAT,
         [DTA]                     FLOAT,
         [Codigo de Producto]      VARCHAR(50),
         [Descripcion]             VARCHAR(250),
         [Cantidad]                NUMERIC(18, 4),
         [Unidad]                  CHAR(5),
         [Valor Pesos]             NUMERIC(18, 4),
         [Valor Dolares]           NUMERIC(18, 4),
         [Numero de Factura]       CHAR(50),
         [Fecha de Factura]        DATETIME,
         [Fraccion]                VARCHAR(22),
         [Secuencia pedimento]     FLOAT,
         [Pedimento Bloqueado]     INT,
         [DESCARGA]                VARCHAR(2),
         [PedimentoOriginal]       VARCHAR(20),
         [Cve_pedimento]           CHAR(5),
         [Valor Comercial Maquila] FLOAT,
         [Valor Agregado Maquila]  FLOAT,
         [TipoDescarga]            VARCHAR(12),
         [descargaDirigida]        VARCHAR(50),
         [CNT]                     NUMERIC(18, 4),
         [prev]                    FLOAT,
         [COVE]                    VARCHAR(100),
         [psalidakey]              NUMERIC(18, 0),
         [salidakey]               NUMERIC(18, 0),
         [Usuario Modifico]        VARCHAR(100),
         [Fecha de Modificacion]   DATETIME,
         [cantidadT]               FLOAT,
         [UnidadT]                 VARCHAR(10),
         [observaciones]           CHAR(78),
         [ANEXO]                   CHAR(3),
         [Valor Aduana]            NUMERIC(18, 4),
         [FechaCreacionSalida]     DATETIME,
         [FechaCreacionPartida]    DATETIME,
         pu                        NUMERIC(18, 3),
         FME                       NUMERIC(18, 5),
         NICO                      VARCHAR(10),
		 PedimentoCTM              VARCHAR(20)
        );

        -- inserto el resultado

        INSERT INTO @res
        ([DIRIGIDO],
         [Aduana],
         [Patente],
         [Pedimento],
         [Fecha de Pago],
         [Fecha de presentacion],
         [Codigo de Cliente],
         [Nombre Cliente],
         [RFC / Tax Cliente],
         [Pais Comprador],
         [Pais Destino],
         [TC],
         [DTA],
         [Codigo de Producto],
         [Descripcion],
         [Cantidad],
         [Unidad],
         [Valor Pesos],
         [Valor Dolares],
         [Numero de Factura],
         [Fecha de Factura],
         [Fraccion],
         [Secuencia pedimento],
         [Pedimento Bloqueado],
         [DESCARGA],
         [PedimentoOriginal],
         [Cve_pedimento],
         [Valor Comercial Maquila],
         [Valor Agregado Maquila],
         [TipoDescarga],
         [descargaDirigida],
         [CNT],
         [prev],
         [COVE],
         [psalidakey],
         [salidakey],
         [Usuario Modifico],
         [Fecha de Modificacion],
         [cantidadT],
         [UnidadT],
         [observaciones],
         [ANEXO],
         [Valor Aduana],
         [FechaCreacionSalida],
         [FechaCreacionPartida],
         PU,
         FME,
         NICO,
	 PedimentoCTM
        )
               SELECT CASE ISNULL(
               (
                   SELECT TOP 1 dirigidokey
                   FROM dirigido
                   WHERE dirigido.psalidakey = psalidas.psalidakey
               ), -1)
                          WHEN-1
                          THEN ''
                          ELSE 'DIRIGIDO'
                      END DIRIGIDO,
                      dbo.salidas.Aduana,
                      dbo.salidas.Agente AS Patente,
                      dbo.salidas.Documento AS Pedimento,
                      dbo.salidas.Fecha AS [Fecha de Pago],
                      dbo.salidas.FechadePresentacion,
                      ISNULL(dbo.pSALIDAS.codProveedor, '-') AS [Codigo de Cliente],
                      CASE
                          WHEN CVE_PEDIMENTO IN('F4', 'F5', 'A3')
                          THEN
               (
                   SELECT TOP 1 NOMBRE
                   FROM PROVEEDORES P
                   WHERE P.CLAVE = pSALIDAS.codProveedor
               )
                          ELSE ISNULL(dbo.clientes.Nombre, '-')
                      END AS [Nombre Cliente],
                      CASE
                          WHEN CVE_PEDIMENTO IN('F4', 'F5', 'A3')
                          THEN
               (
                   SELECT TOP 1 IDFISCAL
                   FROM PROVEEDORES P
                   WHERE P.CLAVE = pSALIDAS.codProveedor
               )
                          ELSE ISNULL(dbo.clientes.Idfiscal, '-')
                      END AS [RFC / Tax Cliente],
                      ISNULL(dbo.psalidas.Paisc, '-') AS [Pais Comprador],
                      ISNULL(dbo.psalidas.paisd, '-') AS [Pais Destino],
                      ISNULL(dbo.salidas.Tc, 0) AS TC,
                      ISNULL(dbo.salidas.dta, 0) AS DTA,
                      ISNULL(dbo.psalidas.Clave, '-') AS [Codigo de Producto],
                      ISNULL(dbo.psalidas.Descripcion, '-') AS Descripcion,
                      ISNULL(dbo.psalidas.Cantidad, 0) AS Cantidad,
                      dbo.psalidas.Unidad,
                      ISNULL(dbo.psalidas.Val_pesos, 0) AS [Valor Pesos],
                      ISNULL(dbo.psalidas.Val_dolares, 0) AS [Valor Dolares],
                      ISNULL(dbo.psalidas.Factura, '-') AS [Numero de Factura],
                      ISNULL(dbo.psalidas.Fecha, dbo.salidas.Fecha) AS [Fecha de Factura],
                      ISNULL(dbo.psalidas.Fraccion, '-') + ISNULL(psalidas.NICO, '') AS Fraccion,
                      ISNULL(dbo.psalidas.partida, 0) AS [Secuencia pedimento],
                      dbo.psalidas.bloqueado AS [Pedimento Bloqueado],
                      dbo.salidas.DESCARGA,
                      dbo.salidas.PedimentoOriginal,
                      dbo.salidas.Cve_pedimento,
                      ISNULL(dbo.psalidas.valorcomercial, 0) AS [Valor Comercial Maquila],
                      ISNULL(dbo.psalidas.valoragregado, 0) AS [Valor Agregado Maquila],
                      dbo.salidas.TipoDescarga,
                      dbo.psalidas.descargaDirigida,
                      dbo.salidas.CNT,
                      dbo.salidas.prev,
                      ISNULL(dbo.psalidas.COVE, '') COVE,
                      psalidakey,
                      salidakey,
                      psalidas.usuario [Usuario Modifico],
                      psalidas.FechaModificacion [Fecha de Modificacion],
                      psalidas.cantidadT,
                      psalidas.UnidadT,
                      salidas.observaciones,
                      PSALIDAS.ANEXO,
                      PSALIDAS.VALORADUANACR [Valor Aduana],
                      salidas.FCreacion FechaCreacionSalida,
                      PSALIDAS.FCreacion FechaCreacionPartida,
                      ISNULL(dbo.psalidas.Val_pesos, 0) / (CASE psalidas.cantidad
                                                               WHEN 0
                                                               THEN NULL
                                                               ELSE psalidas.cantidad
                                                           END),
                      ISNULL(salidas.[FactorME], 0),
                      PSALIDAS.NICO,
		      PSALIDAS.PEDIMENTOEXPORTACIONCTM
               FROM dbo.psalidas
                    INNER JOIN dbo.salidas ON dbo.psalidas.Salidalink = dbo.salidas.SalidaKey
                    LEFT OUTER JOIN dbo.clientes ON dbo.pSALIDAS.codProveedor = dbo.clientes.Clave
               WHERE SALIDAS.FECHA BETWEEN COALESCE(@DESDE, SALIDAS.FECHA) AND COALESCE(@HASTA, salidas.fecha)
                     AND salidas.documento = COALESCE(@documento, salidas.documento);

        -- RESULTADO

        SELECT [DIRIGIDO],
               [Aduana],
               [Patente],
               [Pedimento],
               [Fecha de Pago],
               [Fecha de presentacion],
               [Codigo de Cliente],
               [Nombre Cliente],
               [RFC / Tax Cliente],
               [Pais Comprador],
               [Pais Destino],
               [TC],
               [DTA],
               [Codigo de Producto],
               [Descripcion],
               [Cantidad],
               [Unidad],
               [Valor Pesos],
               [Valor Dolares],
               [Numero de Factura],
               [Fecha de Factura],
               [Fraccion],
               [Secuencia pedimento],
               [Pedimento Bloqueado],
               [DESCARGA],
               [PedimentoOriginal],
               [Cve_pedimento],
               [Valor Comercial Maquila],
               [Valor Agregado Maquila],
               [TipoDescarga],
               [descargaDirigida],
               [CNT],
               [prev],
               [COVE],
               [psalidakey],
               [salidakey],
               [Usuario Modifico],
               [Fecha de Modificacion],
               [cantidadT],
               [UnidadT],
               [observaciones],
               [ANEXO],
               [Valor Aduana],
               [FechaCreacionSalida],
               [FechaCreacionPartida],
               PU,
               FME,
               NICO,
	       PedimentoCTM
        FROM @res
        ORDER BY [Fecha de Pago],
                 [Aduana],
                 [Patente],
                 [Pedimento],
                 [Secuencia pedimento];
    END
