/*
 * Fixture exclusivo para Testcontainers. No representa datos reales ni se
 * despliega en LIVE. Reproduce los tipos documentados de dbo.Partidas y
 * dbo.Importaciones en docs/03-diseno/mapeo-activos-fijos.md.
 */

IF OBJECT_ID('dbo.Importaciones', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Importaciones (
        Ipedimentokey NUMERIC(18,0) NOT NULL CONSTRAINT PK_Importaciones PRIMARY KEY,
        Numero_ped VARCHAR(20) NULL,
        Cve_pedimento VARCHAR(5) NULL,
        Fecha DATETIME NULL,
        Fecha_ad DATETIME NULL
    );
END;

IF OBJECT_ID('dbo.Partidas', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Partidas (
        Partidakey NUMERIC(18,0) NOT NULL CONSTRAINT PK_partidas PRIMARY KEY,
        Importacionlink NUMERIC(18,0) NULL,
        Clave VARCHAR(50) NULL,
        Descripcion VARCHAR(250) NULL,
        Fraccion VARCHAR(15) NULL,
        Cantidad NUMERIC(18,4) NULL,
        Unidad VARCHAR(10) NULL,
        marca VARCHAR(50) NULL,
        modelo VARCHAR(50) NULL,
        serie VARCHAR(50) NULL,
        EsActivo VARCHAR(2) NULL,
        PFECHAIMPO DATETIME NULL,
        FECHAIMPO DATETIME NULL,
        Saldo NUMERIC(18,4) NULL
    );
END;
