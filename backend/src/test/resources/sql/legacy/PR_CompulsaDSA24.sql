-- PROCESO DE COMPULSA 2020

CREATE PROCEDURE [dbo].[PR_CompulsaDSA24]
(@DESDE           DATETIME,
 @HASTA           DATETIME,
 @PEDIMENTOARMADO VARCHAR(50),
 @TIPO            VARCHAR(10)
)
AS
    BEGIN
        DECLARE @T_CompulsaDSA24 TABLE
        ([COMPULSA_DSA24_KEY]      [BIGINT] IDENTITY(1, 1) NOT NULL,
         [TIPOOPERACION]           [VARCHAR](50) NULL,
         [TIPOPEDIMENTO]           [VARCHAR](50) NULL,
         [PEDIMENTOARMADO]         [VARCHAR](50) NULL,
         [FECHA]                   [DATE] NULL,
         [DS_FECHA_ORIGINAL]       [DATE] NULL,
         [A24_FECHA]               [DATE] NULL,
         [STATUS_FECHA]            [VARCHAR](10) NULL,
         [CLAVEDOCUMENTO]          [VARCHAR](10) NULL,
         [A24_CLAVE_PEDIMENTO]     [VARCHAR](50) NULL,
         [STATUS_CLAVE_PEDIMENTO]  [VARCHAR](10) NULL,
         [DS_TIPOPEDIMENTO]        [VARCHAR](50) NULL,
         [SECUENCIA]               [NUMERIC](18, 0) NULL,
         [DS]                      [VARCHAR](2) NULL,
         [A24]                     [VARCHAR](2) NULL,
         [DS_FRACCION]             [VARCHAR](10) NULL,
         [A24_FRACCION]            [VARCHAR](50) NULL,
         [STATUS_FRACCION]         [VARCHAR](10) NULL,
         [DS_VALORDOLARES]         [NUMERIC](18, 2) NULL,
         [A24_VALORDOLARES]        [NUMERIC](18, 2) NULL,
         [STATUS_VALORDOLARES]     [VARCHAR](10) NULL,
         [DS_VALORCOMERCIAL]       [NUMERIC](18, 0) NULL,
         [A24_VALORCOMERCIAL]      [NUMERIC](18, 0) NULL,
         [STATUS_VALORCOMERCIAL]   [VARCHAR](10) NULL,
         [DS_VALORADUANA]          [NUMERIC](18, 0) NULL,
         [A24_VALORADUANA]         [NUMERIC](18, 0) NULL,
         [STATUS_VALORADUANA]      [VARCHAR](10) NULL,
         [DS_CANTIDAD]             [NUMERIC](18, 3) NULL,
         [A24_FACTOR_UNIDAD]       [NUMERIC](18, 4) NULL,
         [A24_CANTIDAD_CONVERTIDA] [NUMERIC](18, 3) NULL,
         [A24_CANTIDAD]            [NUMERIC](18, 3) NULL,
         [STATUS_CANTIDAD]         [VARCHAR](10) NULL,
         [DS_UNIDAD]               [VARCHAR](10) NULL,
         [A24_UNIDAD]              [VARCHAR](50) NULL,
         [A24_UNIDAD_FACTOR_CONV]  [VARCHAR](50) NULL,
         [STATUS_UNIDAD]           [VARCHAR](10) NULL,
         [DS_CANTIDADT]            [NUMERIC](18, 4) NULL,
         [A24_CANTIDADT]           [NUMERIC](18, 4) NULL,
         [STATUS_CANTIDADT]        [VARCHAR](10) NULL,
         [DS_UNIDADT]              [VARCHAR](10) NULL,
         [A24_UNIDADT]             [VARCHAR](50) NULL,
         [STATUS_UNIDADT]          [VARCHAR](10) NULL,
         [DS_DTA]                  [NUMERIC](18, 4) NULL,
         [A24_DTA]                 [NUMERIC](18, 4) NULL,
         [STATUS_DTA]              [VARCHAR](10) NULL,
         [DS_CNT]                  [NUMERIC](18, 4) NULL,
         [A24_CNT]                 [NUMERIC](18, 4) NULL,
         [STATUS_CNT]              [VARCHAR](10) NULL,
         [DS_PREV]                 [NUMERIC](18, 4) NULL,
         [A24_PREV]                [NUMERIC](18, 4) NULL,
         [STATUS_PREV]             [VARCHAR](10) NULL,
         [DS_IVA]                  [NUMERIC](18, 4) NULL,
         [A24_IVA]                 [NUMERIC](18, 4) NULL,
         [STATUS_IVA]              [VARCHAR](10) NULL,
         [DS_IGIE]                 [NUMERIC](18, 4) NULL,
         [A24_IGIE]                [NUMERIC](18, 4) NULL,
         [STATUS_IGIE]             [VARCHAR](10) NULL,
         [A24_PAISOD]              [VARCHAR](20),
         [DS_PAISOD]               [VARCHAR](20),
         [STATUS_PAISOD]           [VARCHAR](10) NULL,
         [A24_PAISCV]              [VARCHAR](20),
         [DS_PAISCV]               [VARCHAR](20),
         [STATUS_PAISCV]           [VARCHAR](10) NULL,
         [DS_IVA_FP21/22]          [NUMERIC](18, 4) NULL,
         [A24_IVA_FP21/22]         [NUMERIC](18, 4) NULL,
         [STATUS_IVA_FP21/22]      [VARCHAR](10) NULL
        );

        --INSERTA DATA STAGE

        INSERT INTO @T_CompulsaDSA24
        ([TIPOPEDIMENTO],
         [TIPOOPERACION],
         [PEDIMENTOARMADO],
         [FECHA],
         [DS_FECHA_ORIGINAL],
         [CLAVEDOCUMENTO],
         [DS_TIPOPEDIMENTO],
         [SECUENCIA],
         [DS],
         [DS_FRACCION],
         [DS_VALORDOLARES],
         [DS_VALORCOMERCIAL],
         [DS_VALORADUANA],
         [DS_CANTIDAD],
         [DS_UNIDAD],
         [DS_CANTIDADT],
         [DS_UNIDADT],
         [DS_DTA],
         [DS_CNT],
         [DS_PREV],
         [DS_IVA],
         [DS_IGIE],
         [DS_PAISOD],
         [DS_PAISCV],
         [DS_IVA_FP21/22]
        )
               SELECT CASE
                          WHEN DS.TIPOPEDIMENTO = 1
                          THEN 'ORIGINAL'
                          ELSE 'RECTIFICACION'
                      END,
                      CASE
                          WHEN DS.TIPOOPERACION = 1
                          THEN 'IMPORTACION'
                          ELSE 'EXPORTACION'
                      END,
                      ds.pedimentoarmado,
                      CAST(ds.fecha AS DATETIME),
                      DBO.FECHAORIGINAL(PEDIMENTOARMADO),
                      ds.clavedocumento,
                      ds.tipopedimento,
                      ds.secuenciafraccion,
                      'SI',
                      ds.fraccion,
                      ds.valordolares,
                      ds.valorcomercial,
                      ds.valoraduana,
                      ds.cantidadumcomercial,
                      ds.unidadmedidacomercial,
                      ds.cantidadumtarifa,
                      ds.unidadmedidatarifa,
               (
                   SELECT SUM(IMPORTEPAGO)
                   FROM [dbo].[CONRIBUCIONES_GENERALES_DS] CGDS
                   WHERE CGDS.PEDIMENTOARMADO = DS.PEDIMENTOARMADO
                         AND CLAVECONTRIBUCION = '1'
               ),
               (
                   SELECT SUM(IMPORTEPAGO)
                   FROM [dbo].[CONRIBUCIONES_GENERALES_DS] CGDS
                   WHERE CGDS.PEDIMENTOARMADO = DS.PEDIMENTOARMADO
                         AND CLAVECONTRIBUCION = '21'
               ),
               (
                   SELECT SUM(IMPORTEPAGO)
                   FROM [dbo].[CONRIBUCIONES_GENERALES_DS] CGDS
                   WHERE CGDS.PEDIMENTOARMADO = DS.PEDIMENTOARMADO
                         AND CLAVECONTRIBUCION = '15'
               ),
               ( -- todo el iva
                   SELECT SUM(CAST(IMPORTEPAGO AS NUMERIC(18, 4)))
                   FROM [dbo].[CONRIBUCIONES_PARTIDA_DS] CPDS
                   WHERE CPDS.PEDIMENTOARMADO = DS.PEDIMENTOARMADO
                         AND CPDS.SecuenciaFraccion = DS.SECUENCIAFRACCION
                         AND ClaveContribucion = '3'
               ), --6
               (
                   SELECT SUM(CAST(IMPORTEPAGO AS NUMERIC(18, 4)))
                   FROM [dbo].[CONRIBUCIONES_PARTIDA_DS] CPDS
                   WHERE CPDS.PEDIMENTOARMADO = DS.PEDIMENTOARMADO
                         AND CPDS.SecuenciaFraccion = DS.SECUENCIAFRACCION
                         AND ClaveContribucion = '6'
               ),
                      ds.PaisOrigenDestino,
                      ds.PaisCompradorVendedor,
               ( -- solo el iva de la certificacion
                   SELECT SUM(CAST(IMPORTEPAGO AS NUMERIC(18, 4)))
                   FROM [dbo].[CONRIBUCIONES_PARTIDA_DS] CPDS
                   WHERE CPDS.PEDIMENTOARMADO = DS.PEDIMENTOARMADO
                         AND CPDS.SecuenciaFraccion = DS.SECUENCIAFRACCION
                         AND ClaveContribucion = '3'
                         AND [FormaPago] IN('21', '22')
               )
               FROM v_partidas ds
               WHERE CAST(ds.FECHA AS DATE) BETWEEN @DESDE AND @HASTA;

        --INSERTA IMPORTACIONES

        INSERT INTO @T_CompulsaDSA24
        ([PEDIMENTOARMADO],
         [FECHA],
         [SECUENCIA]
        )
               SELECT LTRIM(RTRIM(im.numero_ped)),
                      CAST(im.fecha AS DATETIME),
                      p.partida
               FROM importaciones im
                    INNER JOIN partidas p ON im.ipedimentokey = p.importacionlink
               WHERE im.cve_pedimento NOT IN('CT', 'FACT')
                    AND im.FECHA BETWEEN @DESDE AND @HASTA
                    AND NOT EXISTS
               (
                   SELECT pedimentoarmado
                   FROM @T_CompulsaDSA24 t
                   WHERE t.PEDIMENTOARMADO = im.numero_ped
               );

        --INSERTA SALIDAS

        INSERT INTO @T_CompulsaDSA24
        ([PEDIMENTOARMADO],
         [FECHA],
         [SECUENCIA]
        )
               SELECT LTRIM(RTRIM(s.documento)),
                      CAST(s.fecha AS DATETIME),
                      ps.partida
               FROM salidas s
                    INNER JOIN psalidas ps ON s.salidakey = ps.salidalink
               WHERE s.cve_pedimento NOT IN('CT', 'FACT')
                    AND s.FECHA BETWEEN @DESDE AND @HASTA
                    AND NOT EXISTS
               (
                   SELECT pedimentoarmado
                   FROM @T_CompulsaDSA24 t
                   WHERE t.PEDIMENTOARMADO = s.documento
               );

        --UPDATES

        UPDATE @T_CompulsaDSA24
          SET
              [DS] = 'NO'
        WHERE [DS] <> 'SI'
              OR [DS] IS NULL;
        UPDATE @T_CompulsaDSA24
          SET
              [DS_UNIDAD] = [dbo].[VALIDUNIT]([DS_UNIDAD]);
        UPDATE @T_CompulsaDSA24
          SET
              [DS_UNIDADT] = [dbo].[VALIDUNIT]([DS_UNIDADT]);
        UPDATE T
          SET
              T.[A24] = 'SI',
              T.[A24_FRACCION] = DBO.FRACCIONES(T.PEDIMENTOARMADO, T.SECUENCIA, 'F'),
              T.[A24_VALORDOLARES] =
        (
            SELECT SUM(p.val_dolares)
            FROM importaciones im
                 INNER JOIN partidas p ON im.ipedimentokey = p.importacionlink
            WHERE im.numero_ped = T.PEDIMENTOARMADO
                  AND p.partida = T.SECUENCIA
        ),
              T.[A24_VALORCOMERCIAL] =
        (
            SELECT SUM(p.val_comercial)
            FROM importaciones im
                 INNER JOIN partidas p ON im.ipedimentokey = p.importacionlink
            WHERE im.numero_ped = T.PEDIMENTOARMADO
                  AND p.partida = T.SECUENCIA
        ),
              T.[A24_VALORADUANA] =
        (
            SELECT SUM(p.val_aduanal)
            FROM importaciones im
                 INNER JOIN partidas p ON im.ipedimentokey = p.importacionlink
            WHERE im.numero_ped = T.PEDIMENTOARMADO
                  AND p.partida = T.SECUENCIA
        ),

              -- la cantidad es la de las partidas sin conversiones
              T.[A24_CANTIDAD] =
        (
            SELECT SUM(P.CANTIDAD)
            FROM IMPORTACIONES I,
                 PARTIDAS P
            WHERE I.IPEDIMENTOKEY = P.IMPORTACIONLINK
                  AND NUMERO_PED = T.PEDIMENTOARMADO
                  AND P.PARTIDA = T.SECUENCIA
        ),

              -- NO SE QUE HACE FRACCIONES

              T.[A24_UNIDAD] = DBO.FRACCIONES(T.PEDIMENTOARMADO, T.SECUENCIA, 'UC'),
              T.[A24_CANTIDADT] =
        (
            SELECT SUM(p.cantidadt)
            FROM importaciones im
                 INNER JOIN partidas p ON im.ipedimentokey = p.importacionlink
            WHERE im.numero_ped = T.PEDIMENTOARMADO
                  AND p.partida = T.SECUENCIA
        ),
              T.[A24_UNIDADT] = DBO.FRACCIONES(T.PEDIMENTOARMADO, T.SECUENCIA, 'UT'),
              T.A24_DTA = IM.DTA,
              T.A24_CNT = IM.CNT,
              T.A24_PREV = IM.prevalidacion,
              T.A24_IVA =
        ( -- todo el iva
            SELECT SUM(MONTOIVA)
            FROM IMPORTACIONES I,
                 PARTIDAS PA
            WHERE I.IPEDIMENTOKEY = PA.IMPORTACIONLINK
                  AND I.NUMERO_PED = T.PEDIMENTOARMADO
                  AND PA.PARTIDA = T.SECUENCIA
        ),
              T.A24_IGIE =
        (
            SELECT SUM(Montoigi)
            FROM IMPORTACIONES I,
                 PARTIDAS PA
            WHERE I.IPEDIMENTOKEY = PA.IMPORTACIONLINK
                  AND I.NUMERO_PED = T.PEDIMENTOARMADO
                  AND PA.PARTIDA = T.SECUENCIA
        ),
              [A24_CLAVE_PEDIMENTO] = IM.CVE_PEDIMENTO,
              [A24_FECHA] = IM.FECHA,
              [A24_FACTOR_UNIDAD] = [DBO].[FACTOR](P.UNIDAD, [DBO].[VALIDUNIT](DS_UNIDAD), P.[CLAVE]),
              [A24_UNIDAD_FACTOR_CONV] = [DBO].[VALIDUNIT](DS_UNIDAD),
              [A24_PAISOD] =
        (
            SELECT TOP 1 PA.Origen
            FROM IMPORTACIONES I,
                 PARTIDAS PA
            WHERE I.IPEDIMENTOKEY = PA.IMPORTACIONLINK
                  AND I.NUMERO_PED = T.PEDIMENTOARMADO
                  AND PA.PARTIDA = T.SECUENCIA
        ),
              [A24_PAISCV] =
        (
            SELECT TOP 1 PA.paisvendedor
            FROM IMPORTACIONES I,
                 PARTIDAS PA
            WHERE I.IPEDIMENTOKEY = PA.IMPORTACIONLINK
                  AND I.NUMERO_PED = T.PEDIMENTOARMADO
                  AND PA.PARTIDA = T.SECUENCIA
        ),
              [A24_IVA_FP21/22] =
        ( -- solo el iva de la certificacion
            SELECT SUM(PA.Montoiva)
            FROM IMPORTACIONES I,
                 PARTIDAS PA
            WHERE I.IPEDIMENTOKEY = PA.IMPORTACIONLINK
                  AND I.NUMERO_PED = T.PEDIMENTOARMADO
                  AND PA.PARTIDA = T.SECUENCIA
                  AND PA.Fpiva IN('21', '22')
        )
        FROM @T_CompulsaDSA24 T, IMPORTACIONES IM
                                 INNER JOIN PARTIDAS P ON IM.IPEDIMENTOKEY = P.IMPORTACIONLINK
        WHERE IM.NUMERO_PED = T.PEDIMENTOARMADO
              AND P.PARTIDA = T.SECUENCIA;
        UPDATE T
          SET
              T.[A24] = 'SI',
              T.[A24_FRACCION] = DBO.FRACCIONES(T.PEDIMENTOARMADO, T.SECUENCIA, 'F'),
              T.[A24_VALORDOLARES] =
        (
            SELECT SUM(ps.val_dolares)
            FROM salidas s
                 INNER JOIN psalidas ps ON s.salidakey = ps.salidalink
            WHERE s.documento = T.PEDIMENTOARMADO
                  AND ps.partida = T.SECUENCIA
        ),
              T.[A24_VALORCOMERCIAL] =
        (
            SELECT SUM(ps.val_pesos)
            FROM salidas s
                 INNER JOIN psalidas ps ON s.salidakey = ps.salidalink
            WHERE s.documento = T.PEDIMENTOARMADO
                  AND ps.partida = T.SECUENCIA
        ),
              T.[A24_VALORADUANA] =
        (
            SELECT SUM(ps.valoraduanacr)
            FROM salidas s
                 INNER JOIN psalidas ps ON s.salidakey = ps.salidalink
            WHERE s.documento = T.PEDIMENTOARMADO
                  AND ps.partida = T.SECUENCIA
        ),
              T.[A24_CANTIDAD] =
        (
            SELECT SUM(ps.cantidad)
            FROM salidas s
                 INNER JOIN psalidas ps ON s.salidakey = ps.salidalink
            WHERE s.documento = T.PEDIMENTOARMADO
                  AND ps.partida = T.SECUENCIA
        ),
              T.[A24_UNIDAD] = DBO.FRACCIONES(T.PEDIMENTOARMADO, T.SECUENCIA, 'UC'),
              T.[A24_CANTIDADT] =
        (
            SELECT SUM(ps.cantidadt)
            FROM salidas s
                 INNER JOIN psalidas ps ON s.salidakey = ps.salidalink
            WHERE s.documento = T.PEDIMENTOARMADO
                  AND ps.partida = T.SECUENCIA
        ),
              T.[A24_UNIDADT] = DBO.FRACCIONES(T.PEDIMENTOARMADO, T.SECUENCIA, 'UT'),
              T.A24_DTA = S.DTA,
              T.A24_CNT = S.CNT,
              T.A24_PREV = S.prev,
              T.A24_IVA =
        ( --todo el iva
            SELECT SUM(MONTOIVA)
            FROM SALIDAS SA,
                 PSALIDAS PS
            WHERE SA.SALIDAKEY = PS.SALIDALINK
                  AND Sa.DOCUMENTO = T.PEDIMENTOARMADO
                  AND PS.PARTIDA = T.SECUENCIA
        ),
              T.A24_IGIE =
        (
            SELECT SUM(montoigi)
            FROM SALIDAS SA,
                 PSALIDAS PS
            WHERE SA.SALIDAKEY = PS.SALIDALINK
                  AND SA.DOCUMENTO = T.PEDIMENTOARMADO
                  AND PS.PARTIDA = T.SECUENCIA
        ),
              [A24_CLAVE_PEDIMENTO] = S.CVE_PEDIMENTO,
              [A24_FECHA] = S.FECHA,
              [A24_FACTOR_UNIDAD] = CASE
                                        WHEN S.[Cve_pedimento] IN('A3', 'F4', 'F5')
                                        THEN [DBO].[FACTOR](PS.UNIDAD, [DBO].[VALIDUNIT](DS_UNIDAD), PS.[CLAVE])
                                        ELSE DBO.[FACTORPT](PS.UNIDAD, [DBO].[VALIDUNIT](DS_UNIDAD), PS.[CLAVE])
                                    END,
              [A24_UNIDAD_FACTOR_CONV] = [DBO].[VALIDUNIT](DS_UNIDAD),
              [A24_PAISOD] =
        (
            SELECT TOP 1 PS.paisd
            FROM SALIDAS SA,
                 PSALIDAS PS
            WHERE SA.SALIDAKEY = PS.SALIDALINK
                  AND SA.DOCUMENTO = T.PEDIMENTOARMADO
                  AND PS.PARTIDA = T.SECUENCIA
        ),
              [A24_PAISCV] =
        (
            SELECT TOP 1 PS.paisc
            FROM SALIDAS SA,
                 PSALIDAS PS
            WHERE SA.SALIDAKEY = PS.SALIDALINK
                  AND SA.DOCUMENTO = T.PEDIMENTOARMADO
                  AND PS.PARTIDA = T.SECUENCIA
        ),
              [A24_IVA_FP21/22] =
        ( -- solo el iva de la certificacion
            SELECT SUM(ps.montoiva)
            FROM SALIDAS SA,
                 PSALIDAS PS
            WHERE SA.SALIDAKEY = PS.SALIDALINK
                  AND Sa.DOCUMENTO = T.PEDIMENTOARMADO
                  AND PS.PARTIDA = T.SECUENCIA
                  AND PS.FPIVA IN('21', '22')
        )
        FROM @T_CompulsaDSA24 T, SALIDAS S
                                 INNER JOIN PSALIDAS PS ON S.SALIDAKEY = PS.SALIDALINK
        WHERE S.DOCUMENTO = T.PEDIMENTOARMADO
              AND PS.PARTIDA = T.SECUENCIA;

        --

        UPDATE @T_CompulsaDSA24
          SET
              [A24_CANTIDAD_CONVERTIDA] = [A24_CANTIDAD] / CASE
                                                               WHEN ISNULL([A24_FACTOR_UNIDAD], 0) = 0
                                                               THEN 1
                                                               ELSE [A24_FACTOR_UNIDAD]
                                                           END;
        UPDATE @T_CompulsaDSA24
          SET
              [A24] = 'NO'
        WHERE [A24] <> 'SI'
              OR [A24] IS NULL;
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_FECHA] = (CASE
                                    WHEN ISNULL([DS_FECHA_ORIGINAL], [FECHA]) = ISNULL([A24_FECHA], CAST(GETDATE() AS DATE))
                                    THEN 'OK'
                                    ELSE 'ERROR'
                                END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_CLAVE_PEDIMENTO] = (CASE
                                              WHEN ISNULL([CLAVEDOCUMENTO], '') = ISNULL([A24_CLAVE_PEDIMENTO], '')
                                              THEN 'OK'
                                              ELSE 'ERROR'
                                          END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_FRACCION] = (CASE
                                       WHEN LTRIM(RTRIM(DS_FRACCION)) = LTRIM(RTRIM(A24_FRACCION))
                                       THEN 'OK'
                                       ELSE 'ERROR'
                                   END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_VALORDOLARES] = (CASE
                                           WHEN(ISNULL(DS_VALORDOLARES, 0) - ISNULL(A24_VALORDOLARES, 0)) = 0
                                           THEN 'OK'
                                           ELSE 'ERROR'
                                       END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_VALORCOMERCIAL] = (CASE
                                             WHEN(ISNULL(DS_VALORCOMERCIAL, 0) - ISNULL(A24_VALORCOMERCIAL, 0)) = 0
                                             THEN 'OK'
                                             ELSE 'ERROR'
                                         END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_VALORADUANA] = (CASE
                                          WHEN(ISNULL(DS_VALORADUANA, 0) - ISNULL(A24_VALORADUANA, 0)) = 0
                                          THEN 'OK'
                                          ELSE 'ERROR'
                                      END);

        -- CAMBIE EL PROCESO PARA QUE COMPARE CON LA UNIDAD CONVERTIDA
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_CANTIDAD] = (CASE
                                       WHEN(ISNULL(DS_CANTIDAD, 0) - ISNULL(A24_CANTIDAD_CONVERTIDA, 0)) = 0
                                       THEN 'OK'
                                       ELSE 'ERROR'
                                   END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_UNIDAD] = (CASE
                                     WHEN ISNULL([A24_FACTOR_UNIDAD], 0) = 0
                                          AND DS_UNIDAD <> A24_UNIDAD
                                     THEN 'ERROR'
                                     ELSE 'OK'
                                 END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_CANTIDADT] = (CASE
                                        WHEN(ISNULL(DS_CANTIDADT, 0) - ISNULL(A24_CANTIDADT, 0)) = 0
                                        THEN 'OK'
                                        ELSE 'ERROR'
                                    END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_UNIDADT] = (CASE
                                      WHEN DS_UNIDADT = A24_UNIDADT
                                      THEN 'OK'
                                      ELSE 'ERROR'
                                  END);
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_DTA] = CASE
                                 WHEN ABS(ISNULL(DS_DTA, 0) - ISNULL(A24_DTA, 0)) <= 1
                                 THEN 'OK'
                                 ELSE 'ERROR'
                             END;
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_CNT] = CASE
                                 WHEN ABS(ISNULL(DS_CNT, 0) - ISNULL(A24_CNT, 0)) <= 1
                                 THEN 'OK'
                                 ELSE 'ERROR'
                             END;
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_PREV] = CASE
                                  WHEN ABS(ISNULL(DS_PREV, 0) - ISNULL(A24_PREV, 0)) <= 1
                                  THEN 'OK'
                                  ELSE 'ERROR'
                              END;
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_IVA] = CASE
                                 WHEN ABS(ISNULL(DS_IVA, 0) - ISNULL(A24_IVA, 0)) <= 1
                                 THEN 'OK'
                                 ELSE 'ERROR'
                             END;
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_IGIE] = CASE
                                  WHEN ABS(ISNULL(DS_IGIE, 0) - ISNULL(A24_IGIE, 0)) <= 1
                                  THEN 'OK'
                                  ELSE 'ERROR'
                              END;
        UPDATE @T_CompulsaDSA24
          SET
              [STATUS_PAISOD] = CASE
                                    WHEN [A24_PAISOD] = [DS_PAISOD]
                                    THEN 'OK'
                                    ELSE 'ERROR'
                                END,
              [STATUS_PAISCV] = CASE
                                    WHEN [A24_PAISCV] = [DS_PAISCV]
                                    THEN 'OK'
                                    ELSE 'ERROR'
                                END,
              [STATUS_IVA_FP21/22] = CASE
                                         WHEN ABS(ISNULL([DS_IVA_FP21/22], 0) - ISNULL([A24_IVA_FP21/22], 0)) <= 1
                                         THEN 'OK'
                                         ELSE 'ERROR'
                                     END;

        -- FIN DE REVISION DE ERRORES

        SELECT [COMPULSA_DSA24_KEY],
               [PEDIMENTOARMADO],
               [FECHA],
               [DS_FECHA_ORIGINAL],
               [A24_FECHA],
               [STATUS_FECHA],
               [CLAVEDOCUMENTO],
               [A24_CLAVE_PEDIMENTO],
               [STATUS_CLAVE_PEDIMENTO],
               [DS_TIPOPEDIMENTO],
               [SECUENCIA],
               [DS],
               [A24],
               [DS_FRACCION],
               [A24_FRACCION],
               [STATUS_FRACCION],
               [DS_VALORDOLARES],
               [A24_VALORDOLARES],
               [STATUS_VALORDOLARES],
               [DS_VALORCOMERCIAL],
               [A24_VALORCOMERCIAL],
               [STATUS_VALORCOMERCIAL],
               [DS_VALORADUANA],
               [A24_VALORADUANA],
               [STATUS_VALORADUANA],
               [DS_CANTIDAD],
               [A24_FACTOR_UNIDAD],
               [A24_CANTIDAD_CONVERTIDA],
               [A24_CANTIDAD],
               [STATUS_CANTIDAD],
               [DS_UNIDAD],
               [A24_UNIDAD],
               [A24_UNIDAD_FACTOR_CONV],
               [STATUS_UNIDAD],
               [DS_CANTIDADT],
               [A24_CANTIDADT],
               [STATUS_CANTIDADT],
               [DS_UNIDADT],
               [A24_UNIDADT],
               [STATUS_UNIDADT],
               [DS_DTA],
               [A24_DTA],
               [STATUS_DTA],
               [DS_CNT],
               [A24_CNT],
               [STATUS_CNT],
               [DS_PREV],
               [A24_PREV],
               [STATUS_PREV],
               [DS_IVA],
               [A24_IVA],
               [STATUS_IVA],
               [DS_IVA_FP21/22],
               [A24_IVA_FP21/22],
               [STATUS_IVA_FP21/22],
               [DS_IGIE],
               [A24_IGIE],
               [STATUS_IGIE],
               [A24_PAISOD],
               [DS_PAISOD],
               [STATUS_PAISOD],
               [A24_PAISCV],
               [DS_PAISCV],
               [STATUS_PAISCV]
        FROM @T_CompulsaDSA24
        ORDER BY FECHA,
                 PEDIMENTOARMADO;

    END
