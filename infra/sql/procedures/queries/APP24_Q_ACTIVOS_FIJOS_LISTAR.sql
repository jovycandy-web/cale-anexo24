USE CALE_IMMEX;
GO

CREATE OR ALTER PROCEDURE dbo.APP24_Q_ACTIVOS_FIJOS_LISTAR
    @Desde DATE = NULL,
    @Hasta DATE = NULL,
    @Pedimento VARCHAR(20) = NULL,
    @ClavePedimento VARCHAR(5) = NULL,
    @NumeroParte VARCHAR(50) = NULL,
    @Descripcion VARCHAR(250) = NULL,
    @Serie VARCHAR(50) = NULL,
    @Marca VARCHAR(50) = NULL,
    @Modelo VARCHAR(50) = NULL,
    @Pagina INT = 1,
    @Tamano INT = 20,
    @Total BIGINT OUTPUT,
    @PedimentoOp VARCHAR(10) = NULL,
    @ClavePedimentoOp VARCHAR(10) = NULL,
    @NumeroParteOp VARCHAR(10) = NULL,
    @DescripcionOp VARCHAR(10) = NULL,
    @SerieOp VARCHAR(10) = NULL,
    @MarcaOp VARCHAR(10) = NULL,
    @ModeloOp VARCHAR(10) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    /* @Total conserva la posición 12 del contrato desplegado en LIVE y los
       parámetros de operador se agregan al final (13..19). NULL o vacío
       equivale a EQ, por lo que un cliente previo que envía 12 argumentos
       mantiene exactamente el mismo resultado. */

    IF (@Desde IS NULL AND @Hasta IS NOT NULL)
       OR (@Desde IS NOT NULL AND @Hasta IS NULL)
       OR (@Desde IS NOT NULL AND @Hasta IS NOT NULL AND @Desde > @Hasta)
    BEGIN
        THROW 50041, 'Los parámetros desde y hasta deben informarse juntos y formar un rango válido.', 1;
    END;

    IF @Pagina IS NULL OR @Pagina < 1
       OR @Tamano IS NULL OR @Tamano < 1 OR @Tamano > 100
    BEGIN
        THROW 50042, 'Parámetros de paginación inválidos.', 1;
    END;

    SET @Pedimento = NULLIF(LTRIM(RTRIM(@Pedimento)), '');
    SET @ClavePedimento = NULLIF(LTRIM(RTRIM(@ClavePedimento)), '');
    SET @NumeroParte = NULLIF(LTRIM(RTRIM(@NumeroParte)), '');
    SET @Descripcion = NULLIF(LTRIM(RTRIM(@Descripcion)), '');
    SET @Serie = NULLIF(LTRIM(RTRIM(@Serie)), '');
    SET @Marca = NULLIF(LTRIM(RTRIM(@Marca)), '');
    SET @Modelo = NULLIF(LTRIM(RTRIM(@Modelo)), '');

    SET @PedimentoOp = UPPER(LTRIM(RTRIM(ISNULL(@PedimentoOp, 'EQ'))));
    SET @ClavePedimentoOp = UPPER(LTRIM(RTRIM(ISNULL(@ClavePedimentoOp, 'EQ'))));
    SET @NumeroParteOp = UPPER(LTRIM(RTRIM(ISNULL(@NumeroParteOp, 'EQ'))));
    SET @DescripcionOp = UPPER(LTRIM(RTRIM(ISNULL(@DescripcionOp, 'EQ'))));
    SET @SerieOp = UPPER(LTRIM(RTRIM(ISNULL(@SerieOp, 'EQ'))));
    SET @MarcaOp = UPPER(LTRIM(RTRIM(ISNULL(@MarcaOp, 'EQ'))));
    SET @ModeloOp = UPPER(LTRIM(RTRIM(ISNULL(@ModeloOp, 'EQ'))));

    IF @PedimentoOp NOT IN ('EQ', 'NEQ', 'CT', 'SW', 'EW')
       OR @ClavePedimentoOp NOT IN ('EQ', 'NEQ', 'CT', 'SW', 'EW')
       OR @NumeroParteOp NOT IN ('EQ', 'NEQ', 'CT', 'SW', 'EW')
       OR @DescripcionOp NOT IN ('EQ', 'NEQ', 'CT', 'SW', 'EW')
       OR @SerieOp NOT IN ('EQ', 'NEQ', 'CT', 'SW', 'EW')
       OR @MarcaOp NOT IN ('EQ', 'NEQ', 'CT', 'SW', 'EW')
       OR @ModeloOp NOT IN ('EQ', 'NEQ', 'CT', 'SW', 'EW')
    BEGIN
        THROW 50043, 'Operador de filtro no válido. Valores permitidos: EQ, NEQ, CT, SW, EW.', 1;
    END;

    /* Patrones escapados: el carácter de escape es la barra invertida, por lo
       que %, _ y [ se comparan de forma literal en CT/SW/EW. */
    DECLARE @PedimentoPatron VARCHAR(100) = NULL;
    DECLARE @ClavePedimentoPatron VARCHAR(100) = NULL;
    DECLARE @NumeroPartePatron VARCHAR(200) = NULL;
    DECLARE @DescripcionPatron VARCHAR(800) = NULL;
    DECLARE @SeriePatron VARCHAR(200) = NULL;
    DECLARE @MarcaPatron VARCHAR(200) = NULL;
    DECLARE @ModeloPatron VARCHAR(200) = NULL;

    IF @Pedimento IS NOT NULL AND @PedimentoOp IN ('CT', 'SW', 'EW')
        SET @PedimentoPatron = REPLACE(REPLACE(REPLACE(REPLACE(@Pedimento, '\', '\\'), '%', '\%'), '_', '\_'), '[', '\[');
    IF @ClavePedimento IS NOT NULL AND @ClavePedimentoOp IN ('CT', 'SW', 'EW')
        SET @ClavePedimentoPatron = REPLACE(REPLACE(REPLACE(REPLACE(@ClavePedimento, '\', '\\'), '%', '\%'), '_', '\_'), '[', '\[');
    IF @NumeroParte IS NOT NULL AND @NumeroParteOp IN ('CT', 'SW', 'EW')
        SET @NumeroPartePatron = REPLACE(REPLACE(REPLACE(REPLACE(@NumeroParte, '\', '\\'), '%', '\%'), '_', '\_'), '[', '\[');
    IF @Descripcion IS NOT NULL AND @DescripcionOp IN ('CT', 'SW', 'EW')
        SET @DescripcionPatron = REPLACE(REPLACE(REPLACE(REPLACE(@Descripcion, '\', '\\'), '%', '\%'), '_', '\_'), '[', '\[');
    IF @Serie IS NOT NULL AND @SerieOp IN ('CT', 'SW', 'EW')
        SET @SeriePatron = REPLACE(REPLACE(REPLACE(REPLACE(@Serie, '\', '\\'), '%', '\%'), '_', '\_'), '[', '\[');
    IF @Marca IS NOT NULL AND @MarcaOp IN ('CT', 'SW', 'EW')
        SET @MarcaPatron = REPLACE(REPLACE(REPLACE(REPLACE(@Marca, '\', '\\'), '%', '\%'), '_', '\_'), '[', '\[');
    IF @Modelo IS NOT NULL AND @ModeloOp IN ('CT', 'SW', 'EW')
        SET @ModeloPatron = REPLACE(REPLACE(REPLACE(REPLACE(@Modelo, '\', '\\'), '%', '\%'), '_', '\_'), '[', '\[');

    DECLARE @HastaExclusivo DATETIME = NULL;
    DECLARE @Desplazamiento BIGINT;
    DECLARE @FilaInicial BIGINT;
    DECLARE @FilaFinal BIGINT;

    IF @Hasta IS NOT NULL AND @Hasta < '9999-12-31'
    BEGIN
        SET @HastaExclusivo = DATEADD(DAY, 1, CAST(@Hasta AS DATETIME));
    END;

    SET @Desplazamiento = (CAST(@Pagina AS BIGINT) - 1) * CAST(@Tamano AS BIGINT);
    SET @FilaInicial = @Desplazamiento + 1;
    SET @FilaFinal = @Desplazamiento + CAST(@Tamano AS BIGINT);

    SELECT @Total = COUNT_BIG(*)
    FROM dbo.Partidas AS p
    INNER JOIN dbo.Importaciones AS i
        ON i.Ipedimentokey = p.Importacionlink
    WHERE UPPER(LTRIM(RTRIM(p.EsActivo))) = 'S'
      AND (
          @Desde IS NULL
          OR (
              i.Fecha >= CAST(@Desde AS DATETIME)
              AND (
                  @Hasta = '9999-12-31'
                  OR i.Fecha < @HastaExclusivo
              )
          )
      )
      AND (@Pedimento IS NULL OR (
              (@PedimentoOp = 'EQ' AND LTRIM(RTRIM(i.Numero_ped)) = @Pedimento)
              OR (@PedimentoOp = 'NEQ' AND (LTRIM(RTRIM(i.Numero_ped)) <> @Pedimento OR i.Numero_ped IS NULL))
              OR (@PedimentoOp = 'CT' AND LTRIM(RTRIM(i.Numero_ped)) LIKE '%' + @PedimentoPatron + '%' ESCAPE '\')
              OR (@PedimentoOp = 'SW' AND LTRIM(RTRIM(i.Numero_ped)) LIKE @PedimentoPatron + '%' ESCAPE '\')
              OR (@PedimentoOp = 'EW' AND LTRIM(RTRIM(i.Numero_ped)) LIKE '%' + @PedimentoPatron ESCAPE '\')
          ))
      AND (@ClavePedimento IS NULL OR (
              (@ClavePedimentoOp = 'EQ' AND LTRIM(RTRIM(i.Cve_pedimento)) = @ClavePedimento)
              OR (@ClavePedimentoOp = 'NEQ' AND (LTRIM(RTRIM(i.Cve_pedimento)) <> @ClavePedimento OR i.Cve_pedimento IS NULL))
              OR (@ClavePedimentoOp = 'CT' AND LTRIM(RTRIM(i.Cve_pedimento)) LIKE '%' + @ClavePedimentoPatron + '%' ESCAPE '\')
              OR (@ClavePedimentoOp = 'SW' AND LTRIM(RTRIM(i.Cve_pedimento)) LIKE @ClavePedimentoPatron + '%' ESCAPE '\')
              OR (@ClavePedimentoOp = 'EW' AND LTRIM(RTRIM(i.Cve_pedimento)) LIKE '%' + @ClavePedimentoPatron ESCAPE '\')
          ))
      AND (@NumeroParte IS NULL OR (
              (@NumeroParteOp = 'EQ' AND LTRIM(RTRIM(p.Clave)) = @NumeroParte)
              OR (@NumeroParteOp = 'NEQ' AND (LTRIM(RTRIM(p.Clave)) <> @NumeroParte OR p.Clave IS NULL))
              OR (@NumeroParteOp = 'CT' AND LTRIM(RTRIM(p.Clave)) LIKE '%' + @NumeroPartePatron + '%' ESCAPE '\')
              OR (@NumeroParteOp = 'SW' AND LTRIM(RTRIM(p.Clave)) LIKE @NumeroPartePatron + '%' ESCAPE '\')
              OR (@NumeroParteOp = 'EW' AND LTRIM(RTRIM(p.Clave)) LIKE '%' + @NumeroPartePatron ESCAPE '\')
          ))
      AND (@Descripcion IS NULL OR (
              (@DescripcionOp = 'EQ' AND LTRIM(RTRIM(p.Descripcion)) = @Descripcion)
              OR (@DescripcionOp = 'NEQ' AND (LTRIM(RTRIM(p.Descripcion)) <> @Descripcion OR p.Descripcion IS NULL))
              OR (@DescripcionOp = 'CT' AND LTRIM(RTRIM(p.Descripcion)) LIKE '%' + @DescripcionPatron + '%' ESCAPE '\')
              OR (@DescripcionOp = 'SW' AND LTRIM(RTRIM(p.Descripcion)) LIKE @DescripcionPatron + '%' ESCAPE '\')
              OR (@DescripcionOp = 'EW' AND LTRIM(RTRIM(p.Descripcion)) LIKE '%' + @DescripcionPatron ESCAPE '\')
          ))
      AND (@Serie IS NULL OR (
              (@SerieOp = 'EQ' AND LTRIM(RTRIM(p.serie)) = @Serie)
              OR (@SerieOp = 'NEQ' AND (LTRIM(RTRIM(p.serie)) <> @Serie OR p.serie IS NULL))
              OR (@SerieOp = 'CT' AND LTRIM(RTRIM(p.serie)) LIKE '%' + @SeriePatron + '%' ESCAPE '\')
              OR (@SerieOp = 'SW' AND LTRIM(RTRIM(p.serie)) LIKE @SeriePatron + '%' ESCAPE '\')
              OR (@SerieOp = 'EW' AND LTRIM(RTRIM(p.serie)) LIKE '%' + @SeriePatron ESCAPE '\')
          ))
      AND (@Marca IS NULL OR (
              (@MarcaOp = 'EQ' AND LTRIM(RTRIM(p.marca)) = @Marca)
              OR (@MarcaOp = 'NEQ' AND (LTRIM(RTRIM(p.marca)) <> @Marca OR p.marca IS NULL))
              OR (@MarcaOp = 'CT' AND LTRIM(RTRIM(p.marca)) LIKE '%' + @MarcaPatron + '%' ESCAPE '\')
              OR (@MarcaOp = 'SW' AND LTRIM(RTRIM(p.marca)) LIKE @MarcaPatron + '%' ESCAPE '\')
              OR (@MarcaOp = 'EW' AND LTRIM(RTRIM(p.marca)) LIKE '%' + @MarcaPatron ESCAPE '\')
          ))
      AND (@Modelo IS NULL OR (
              (@ModeloOp = 'EQ' AND LTRIM(RTRIM(p.modelo)) = @Modelo)
              OR (@ModeloOp = 'NEQ' AND (LTRIM(RTRIM(p.modelo)) <> @Modelo OR p.modelo IS NULL))
              OR (@ModeloOp = 'CT' AND LTRIM(RTRIM(p.modelo)) LIKE '%' + @ModeloPatron + '%' ESCAPE '\')
              OR (@ModeloOp = 'SW' AND LTRIM(RTRIM(p.modelo)) LIKE @ModeloPatron + '%' ESCAPE '\')
              OR (@ModeloOp = 'EW' AND LTRIM(RTRIM(p.modelo)) LIKE '%' + @ModeloPatron ESCAPE '\')
          ));

    ;WITH ActivosFijos AS (
        SELECT
            p.Partidakey AS PARTIDA_ENTRADA_ID,
            i.Ipedimentokey AS IMPORTACION_ID,
            LTRIM(RTRIM(i.Numero_ped)) AS PEDIMENTO,
            LTRIM(RTRIM(i.Cve_pedimento)) AS CLAVE_PEDIMENTO,
            i.Fecha AS FECHA_IMPORTACION,
            LTRIM(RTRIM(p.Clave)) AS NUMERO_PARTE,
            LTRIM(RTRIM(p.Descripcion)) AS DESCRIPCION,
            LTRIM(RTRIM(p.Fraccion)) AS FRACCION,
            p.Cantidad AS CANTIDAD,
            LTRIM(RTRIM(p.Unidad)) AS UNIDAD,
            LTRIM(RTRIM(p.serie)) AS NUMERO_SERIE,
            LTRIM(RTRIM(p.marca)) AS MARCA,
            LTRIM(RTRIM(p.modelo)) AS MODELO,
            ROW_NUMBER() OVER (
                ORDER BY i.Fecha DESC, i.Ipedimentokey DESC, p.Partidakey DESC
            ) AS APP24_ROW_NUMBER
        FROM dbo.Partidas AS p
        INNER JOIN dbo.Importaciones AS i
            ON i.Ipedimentokey = p.Importacionlink
        WHERE UPPER(LTRIM(RTRIM(p.EsActivo))) = 'S'
          AND (
              @Desde IS NULL
              OR (
                  i.Fecha >= CAST(@Desde AS DATETIME)
                  AND (
                      @Hasta = '9999-12-31'
                      OR i.Fecha < @HastaExclusivo
                  )
              )
          )
          AND (@Pedimento IS NULL OR (
                  (@PedimentoOp = 'EQ' AND LTRIM(RTRIM(i.Numero_ped)) = @Pedimento)
                  OR (@PedimentoOp = 'NEQ' AND (LTRIM(RTRIM(i.Numero_ped)) <> @Pedimento OR i.Numero_ped IS NULL))
                  OR (@PedimentoOp = 'CT' AND LTRIM(RTRIM(i.Numero_ped)) LIKE '%' + @PedimentoPatron + '%' ESCAPE '\')
                  OR (@PedimentoOp = 'SW' AND LTRIM(RTRIM(i.Numero_ped)) LIKE @PedimentoPatron + '%' ESCAPE '\')
                  OR (@PedimentoOp = 'EW' AND LTRIM(RTRIM(i.Numero_ped)) LIKE '%' + @PedimentoPatron ESCAPE '\')
              ))
          AND (@ClavePedimento IS NULL OR (
                  (@ClavePedimentoOp = 'EQ' AND LTRIM(RTRIM(i.Cve_pedimento)) = @ClavePedimento)
                  OR (@ClavePedimentoOp = 'NEQ' AND (LTRIM(RTRIM(i.Cve_pedimento)) <> @ClavePedimento OR i.Cve_pedimento IS NULL))
                  OR (@ClavePedimentoOp = 'CT' AND LTRIM(RTRIM(i.Cve_pedimento)) LIKE '%' + @ClavePedimentoPatron + '%' ESCAPE '\')
                  OR (@ClavePedimentoOp = 'SW' AND LTRIM(RTRIM(i.Cve_pedimento)) LIKE @ClavePedimentoPatron + '%' ESCAPE '\')
                  OR (@ClavePedimentoOp = 'EW' AND LTRIM(RTRIM(i.Cve_pedimento)) LIKE '%' + @ClavePedimentoPatron ESCAPE '\')
              ))
          AND (@NumeroParte IS NULL OR (
                  (@NumeroParteOp = 'EQ' AND LTRIM(RTRIM(p.Clave)) = @NumeroParte)
                  OR (@NumeroParteOp = 'NEQ' AND (LTRIM(RTRIM(p.Clave)) <> @NumeroParte OR p.Clave IS NULL))
                  OR (@NumeroParteOp = 'CT' AND LTRIM(RTRIM(p.Clave)) LIKE '%' + @NumeroPartePatron + '%' ESCAPE '\')
                  OR (@NumeroParteOp = 'SW' AND LTRIM(RTRIM(p.Clave)) LIKE @NumeroPartePatron + '%' ESCAPE '\')
                  OR (@NumeroParteOp = 'EW' AND LTRIM(RTRIM(p.Clave)) LIKE '%' + @NumeroPartePatron ESCAPE '\')
              ))
          AND (@Descripcion IS NULL OR (
                  (@DescripcionOp = 'EQ' AND LTRIM(RTRIM(p.Descripcion)) = @Descripcion)
                  OR (@DescripcionOp = 'NEQ' AND (LTRIM(RTRIM(p.Descripcion)) <> @Descripcion OR p.Descripcion IS NULL))
                  OR (@DescripcionOp = 'CT' AND LTRIM(RTRIM(p.Descripcion)) LIKE '%' + @DescripcionPatron + '%' ESCAPE '\')
                  OR (@DescripcionOp = 'SW' AND LTRIM(RTRIM(p.Descripcion)) LIKE @DescripcionPatron + '%' ESCAPE '\')
                  OR (@DescripcionOp = 'EW' AND LTRIM(RTRIM(p.Descripcion)) LIKE '%' + @DescripcionPatron ESCAPE '\')
              ))
          AND (@Serie IS NULL OR (
                  (@SerieOp = 'EQ' AND LTRIM(RTRIM(p.serie)) = @Serie)
                  OR (@SerieOp = 'NEQ' AND (LTRIM(RTRIM(p.serie)) <> @Serie OR p.serie IS NULL))
                  OR (@SerieOp = 'CT' AND LTRIM(RTRIM(p.serie)) LIKE '%' + @SeriePatron + '%' ESCAPE '\')
                  OR (@SerieOp = 'SW' AND LTRIM(RTRIM(p.serie)) LIKE @SeriePatron + '%' ESCAPE '\')
                  OR (@SerieOp = 'EW' AND LTRIM(RTRIM(p.serie)) LIKE '%' + @SeriePatron ESCAPE '\')
              ))
          AND (@Marca IS NULL OR (
                  (@MarcaOp = 'EQ' AND LTRIM(RTRIM(p.marca)) = @Marca)
                  OR (@MarcaOp = 'NEQ' AND (LTRIM(RTRIM(p.marca)) <> @Marca OR p.marca IS NULL))
                  OR (@MarcaOp = 'CT' AND LTRIM(RTRIM(p.marca)) LIKE '%' + @MarcaPatron + '%' ESCAPE '\')
                  OR (@MarcaOp = 'SW' AND LTRIM(RTRIM(p.marca)) LIKE @MarcaPatron + '%' ESCAPE '\')
                  OR (@MarcaOp = 'EW' AND LTRIM(RTRIM(p.marca)) LIKE '%' + @MarcaPatron ESCAPE '\')
              ))
          AND (@Modelo IS NULL OR (
                  (@ModeloOp = 'EQ' AND LTRIM(RTRIM(p.modelo)) = @Modelo)
                  OR (@ModeloOp = 'NEQ' AND (LTRIM(RTRIM(p.modelo)) <> @Modelo OR p.modelo IS NULL))
                  OR (@ModeloOp = 'CT' AND LTRIM(RTRIM(p.modelo)) LIKE '%' + @ModeloPatron + '%' ESCAPE '\')
                  OR (@ModeloOp = 'SW' AND LTRIM(RTRIM(p.modelo)) LIKE @ModeloPatron + '%' ESCAPE '\')
                  OR (@ModeloOp = 'EW' AND LTRIM(RTRIM(p.modelo)) LIKE '%' + @ModeloPatron ESCAPE '\')
              ))
    )
    SELECT
        PARTIDA_ENTRADA_ID,
        IMPORTACION_ID,
        PEDIMENTO,
        CLAVE_PEDIMENTO,
        FECHA_IMPORTACION,
        NUMERO_PARTE,
        DESCRIPCION,
        FRACCION,
        CANTIDAD,
        UNIDAD,
        NUMERO_SERIE,
        MARCA,
        MODELO
    FROM ActivosFijos
    WHERE APP24_ROW_NUMBER BETWEEN @FilaInicial AND @FilaFinal
    ORDER BY APP24_ROW_NUMBER;
END;
GO
