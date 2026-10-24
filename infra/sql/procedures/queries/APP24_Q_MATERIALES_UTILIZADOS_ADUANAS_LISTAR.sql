-- ============================================
-- REPORTE DE ADUANAS: materiales utilizados (par importacion/exportacion).
-- LEGACY-XXX Reporte de materiales utilizados - consulta read-only.
--
-- Objetivo: reproducir el contrato de 18 columnas que presenta el legacy
--   RptInfMaterialesUA24.aspx, una fila por descarga (DESCARGA.Descargakey).
--
-- Diferencia deliberada con la consulta operativa:
--   dbo.APP24_Q_MATERIALES_UTILIZADOS_LISTAR modela el descargo por material
--   y movimiento (uso operativo). Este procedimiento modela la relacion
--   importacion -> material utilizado -> exportacion con el detalle de ambas
--   partidas. No la reemplaza ni la duplica.
--
-- Cardinalidad: todas las uniones son por clave primaria desde DESCARGA,
--   por lo que una descarga produce exactamente una fila. No se une
--   PRODUCTOMATERIAL: la relacion ya esta registrada en DESCARGA.
--
-- Compatibilidad: CALE_IMMEX compatibility_level = 100. Sin TRY_CONVERT,
--   sin STRING_AGG, sin funciones >= 110. Paginacion por ROW_NUMBER.
-- Consumido por GET /api/v1/reportes/materiales-utilizados-aduanas.
-- ============================================
USE CALE_IMMEX;
GO

CREATE OR ALTER PROCEDURE dbo.APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR
    @Desde DATE,
    @Hasta DATE,
    @Material VARCHAR(50) = NULL,
    @Producto VARCHAR(50) = NULL,
    @PedimentoImportacion VARCHAR(60) = NULL,
    @PedimentoExportacion VARCHAR(60) = NULL,
    @Pagina INT = 1,
    @Tamano INT = 20,
    @Total BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF @Desde IS NULL OR @Hasta IS NULL OR @Desde > @Hasta
    BEGIN
        THROW 50051, 'El rango de fechas es obligatorio y valido.', 1;
    END;

    IF @Pagina IS NULL OR @Pagina < 1
       OR @Tamano IS NULL OR @Tamano < 1 OR @Tamano > 100
    BEGIN
        THROW 50052, 'Parametros de paginacion invalidos.', 1;
    END;

    SET @Material = NULLIF(LTRIM(RTRIM(@Material)), '');
    SET @Producto = NULLIF(LTRIM(RTRIM(@Producto)), '');
    SET @PedimentoImportacion = NULLIF(LTRIM(RTRIM(@PedimentoImportacion)), '');
    SET @PedimentoExportacion = NULLIF(LTRIM(RTRIM(@PedimentoExportacion)), '');

    DECLARE @HastaExclusivo DATETIME = NULL;
    DECLARE @Desplazamiento BIGINT;
    DECLARE @FilaInicial BIGINT;
    DECLARE @FilaFinal BIGINT;

    /* El periodo se ancla en la fecha de pago del pedimento de exportacion,
       igual que la consulta operativa de materiales utilizados y el reporte
       de salidas, para que el mismo periodo produzca el mismo universo. */
    IF @Hasta < '9999-12-31'
    BEGIN
        SET @HastaExclusivo = DATEADD(DAY, 1, CAST(@Hasta AS DATETIME));
    END;

    SET @Desplazamiento = (CAST(@Pagina AS BIGINT) - 1) * CAST(@Tamano AS BIGINT);
    SET @FilaInicial = @Desplazamiento + 1;
    SET @FilaFinal = @Desplazamiento + CAST(@Tamano AS BIGINT);

    SELECT @Total = COUNT_BIG(*)
    FROM dbo.DESCARGA AS d
    INNER JOIN dbo.PARTIDAS AS pe
        ON CONVERT(NUMERIC(18, 0), CASE
               WHEN d.Pentradalink > -1.0E18
                AND d.Pentradalink < 1.0E18
                AND d.Pentradalink = FLOOR(d.Pentradalink)
               THEN FLOOR(d.Pentradalink)
           END) = pe.Partidakey
    INNER JOIN dbo.IMPORTACIONES AS i
        ON CONVERT(NUMERIC(18, 0), CASE
               WHEN d.Entradalink > -1.0E18
                AND d.Entradalink < 1.0E18
                AND d.Entradalink = FLOOR(d.Entradalink)
               THEN FLOOR(d.Entradalink)
           END) = i.Ipedimentokey
    INNER JOIN dbo.PSALIDAS AS ps
        ON CONVERT(NUMERIC(18, 0), CASE
               WHEN d.Psalidalink > -1.0E18
                AND d.Psalidalink < 1.0E18
                AND d.Psalidalink = FLOOR(d.Psalidalink)
               THEN FLOOR(d.Psalidalink)
           END) = ps.Psalidakey
    INNER JOIN dbo.SALIDAS AS s
        ON CONVERT(NUMERIC(18, 0), CASE
               WHEN d.Salidalink > -1.0E18
                AND d.Salidalink < 1.0E18
                AND d.Salidalink = FLOOR(d.Salidalink)
               THEN FLOOR(d.Salidalink)
           END) = s.SalidaKey
    WHERE s.Fecha >= CAST(@Desde AS DATETIME)
      AND (@HastaExclusivo IS NULL OR s.Fecha < @HastaExclusivo)
      AND (@Material IS NULL OR d.Clave = @Material)
      AND (@Producto IS NULL OR d.PT = @Producto)
      AND (@PedimentoImportacion IS NULL OR LTRIM(RTRIM(i.Numero_ped)) = @PedimentoImportacion)
      AND (@PedimentoExportacion IS NULL OR LTRIM(RTRIM(s.Documento)) = @PedimentoExportacion);

    ;WITH Utilizados AS (
        SELECT
            LTRIM(RTRIM(i.Numero_ped)) AS PEDIMENTO_IMPO,
            i.Fecha AS FECHA_PAGO_IMPO,
            pe.Cantidad AS CANTIDAD_IMPO,
            LTRIM(RTRIM(pe.Fraccion)) AS FRACCION_IMPO,
            LTRIM(RTRIM(pe.Clave)) AS NUMERO_PARTE_IMPO,
            LTRIM(RTRIM(pe.Unidad)) AS UMC_IMPO,
            LTRIM(RTRIM(pe.Descripcion)) AS DESCRIPCION_IMPO,
            LTRIM(RTRIM(s.Documento)) AS PEDIMENTO_EXPO,
            s.Fecha AS FECHA_PAGO_EXPO,
            ps.Cantidad AS CANTIDAD_EXPO,
            LTRIM(RTRIM(ps.Fraccion)) AS FRACCION_EXPO,
            LTRIM(RTRIM(ps.Clave)) AS NUMERO_PARTE_EXPO,
            LTRIM(RTRIM(ps.Unidad)) AS UMC_EXPO,
            LTRIM(RTRIM(ps.Descripcion)) AS DESCRIPCION_EXPO,
            d.CantUtil AS CANTIDAD_UTILIZADA,
            d.Desperdicio AS DESPERDICIO,
            d.Merma AS MERMA,
            LTRIM(RTRIM(d.Unidad)) AS UMC_UTILIZADA,
            ROW_NUMBER() OVER (
                ORDER BY s.Fecha, s.SalidaKey, ps.Psalidakey, d.Descargakey
            ) AS APP24_ROW_NUMBER
        FROM dbo.DESCARGA AS d
        INNER JOIN dbo.PARTIDAS AS pe
            ON CONVERT(NUMERIC(18, 0), CASE
                   WHEN d.Pentradalink > -1.0E18
                    AND d.Pentradalink < 1.0E18
                    AND d.Pentradalink = FLOOR(d.Pentradalink)
                   THEN FLOOR(d.Pentradalink)
               END) = pe.Partidakey
        INNER JOIN dbo.IMPORTACIONES AS i
            ON CONVERT(NUMERIC(18, 0), CASE
                   WHEN d.Entradalink > -1.0E18
                    AND d.Entradalink < 1.0E18
                    AND d.Entradalink = FLOOR(d.Entradalink)
                   THEN FLOOR(d.Entradalink)
               END) = i.Ipedimentokey
        INNER JOIN dbo.PSALIDAS AS ps
            ON CONVERT(NUMERIC(18, 0), CASE
                   WHEN d.Psalidalink > -1.0E18
                    AND d.Psalidalink < 1.0E18
                    AND d.Psalidalink = FLOOR(d.Psalidalink)
                   THEN FLOOR(d.Psalidalink)
               END) = ps.Psalidakey
        INNER JOIN dbo.SALIDAS AS s
            ON CONVERT(NUMERIC(18, 0), CASE
                   WHEN d.Salidalink > -1.0E18
                    AND d.Salidalink < 1.0E18
                    AND d.Salidalink = FLOOR(d.Salidalink)
                   THEN FLOOR(d.Salidalink)
               END) = s.SalidaKey
        WHERE s.Fecha >= CAST(@Desde AS DATETIME)
          AND (@HastaExclusivo IS NULL OR s.Fecha < @HastaExclusivo)
          AND (@Material IS NULL OR d.Clave = @Material)
          AND (@Producto IS NULL OR d.PT = @Producto)
          AND (@PedimentoImportacion IS NULL OR LTRIM(RTRIM(i.Numero_ped)) = @PedimentoImportacion)
          AND (@PedimentoExportacion IS NULL OR LTRIM(RTRIM(s.Documento)) = @PedimentoExportacion)
    )
    SELECT
        PEDIMENTO_IMPO,
        FECHA_PAGO_IMPO,
        CANTIDAD_IMPO,
        FRACCION_IMPO,
        NUMERO_PARTE_IMPO,
        UMC_IMPO,
        DESCRIPCION_IMPO,
        PEDIMENTO_EXPO,
        FECHA_PAGO_EXPO,
        CANTIDAD_EXPO,
        FRACCION_EXPO,
        NUMERO_PARTE_EXPO,
        UMC_EXPO,
        DESCRIPCION_EXPO,
        CANTIDAD_UTILIZADA,
        DESPERDICIO,
        MERMA,
        UMC_UTILIZADA
    FROM Utilizados
    WHERE APP24_ROW_NUMBER BETWEEN @FilaInicial AND @FilaFinal
    ORDER BY APP24_ROW_NUMBER;
END;
GO
