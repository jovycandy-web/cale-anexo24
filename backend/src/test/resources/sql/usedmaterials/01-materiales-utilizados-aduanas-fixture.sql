/*
 * Fixture exclusivo para Testcontainers. No representa datos reales ni se
 * despliega en LIVE. Reproduce los tipos documentados de dbo.DESCARGA,
 * dbo.PARTIDAS, dbo.IMPORTACIONES, dbo.PSALIDAS y dbo.SALIDAS que consume
 * APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR.
 */

IF OBJECT_ID('dbo.IMPORTACIONES', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.IMPORTACIONES (
        Ipedimentokey NUMERIC(18,0) NOT NULL CONSTRAINT PK_IMPO PRIMARY KEY,
        Numero_ped VARCHAR(60) NULL,
        Fecha DATETIME NULL
    );
END;

IF OBJECT_ID('dbo.PARTIDAS', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PARTIDAS (
        Partidakey NUMERIC(18,0) NOT NULL CONSTRAINT PK_PART PRIMARY KEY,
        Importacionlink NUMERIC(18,0) NULL,
        Clave VARCHAR(50) NULL,
        Descripcion VARCHAR(250) NULL,
        Fraccion VARCHAR(15) NULL,
        Cantidad NUMERIC(18,4) NULL,
        Unidad VARCHAR(10) NULL
    );
END;

IF OBJECT_ID('dbo.SALIDAS', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.SALIDAS (
        SalidaKey NUMERIC(18,0) NOT NULL CONSTRAINT PK_SAL PRIMARY KEY,
        Documento CHAR(60) NULL,
        Cve_pedimento CHAR(5) NULL,
        Fecha DATETIME NULL
    );
END;

IF OBJECT_ID('dbo.PSALIDAS', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PSALIDAS (
        Psalidakey NUMERIC(18,0) NOT NULL CONSTRAINT PK_PSAL PRIMARY KEY,
        Fraccion CHAR(15) NULL,
        Descripcion VARCHAR(250) NULL,
        Cantidad NUMERIC(18,5) NULL,
        Unidad CHAR(10) NULL,
        Clave VARCHAR(50) NULL,
        Salidalink FLOAT NULL
    );
END;

IF OBJECT_ID('dbo.DESCARGA', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.DESCARGA (
        Descargakey BIGINT NOT NULL CONSTRAINT PK_DESC PRIMARY KEY,
        Entradalink FLOAT NULL,
        Pentradalink FLOAT NULL,
        Salidalink FLOAT NULL,
        Psalidalink FLOAT NULL,
        CantUtil FLOAT NULL,
        Unidad CHAR(10) NULL,
        Desperdicio FLOAT NULL,
        Merma FLOAT NULL,
        Clave VARCHAR(50) NULL,
        PT VARCHAR(50) NULL
    );
END;
GO

DELETE FROM dbo.DESCARGA;
DELETE FROM dbo.PSALIDAS;
DELETE FROM dbo.SALIDAS;
DELETE FROM dbo.PARTIDAS;
DELETE FROM dbo.IMPORTACIONES;
GO

-- Importación con dos partidas
INSERT INTO dbo.IMPORTACIONES (Ipedimentokey, Numero_ped, Fecha) VALUES
    (1, '5003971', '2025-09-23'),
    (2, '6001720', '2026-05-28');
GO

INSERT INTO dbo.PARTIDAS (Partidakey, Importacionlink, Clave, Descripcion, Fraccion, Cantidad, Unidad) VALUES
    (11, 1, '500017', 'AZUCAR ESTANDAR', '17019999', 6000000.0000, 'KG'),
    (12, 1, '500005', 'AZUCAR REFINADA', '17019999', 126428.0804, 'KG'),
    (13, 2, 'MAT-ORPHAN', 'PARTIDA SIN DESCARGA', '39269003', 10.0000, 'PZA');
GO

-- Exportación con dos partidas
INSERT INTO dbo.SALIDAS (SalidaKey, Documento, Cve_pedimento, Fecha) VALUES
    (3001, '190-1562-5001241', 'F4', '2025-10-31'),
    (3002, '190-1562-5001248', 'F4', '2025-11-03');
GO

INSERT INTO dbo.PSALIDAS (Psalidakey, Fraccion, Descripcion, Cantidad, Unidad, Clave, Salidalink) VALUES
    (7001, '17049099', 'RINGS JOVY', 576.00000, 'CAJA', '301275', 3001),
    (7002, '17049099', 'LIMONAZO', 92.00000, 'CAJA', '200060', 3001),
    (7003, '17049099', 'REVOLCADITAS', 5.00000, 'CAJA', '200072', 3002);
GO

-- Dos descargas sobre la misma partida de importación, con merma/desperdicio distintos
INSERT INTO dbo.DESCARGA (Descargakey, Entradalink, Pentradalink, Salidalink, Psalidalink,
                          CantUtil, Unidad, Desperdicio, Merma, Clave, PT) VALUES
    (9001, 1, 11, 3001, 7001, 1140.3102, 'KG', NULL, 6.7741, '500017', '301275'),
    (9002, 1, 11, 3001, 7002, 1088.2952, 'KG', 2.5000, 6.4651, '500017', '200060'),
    (9003, 1, 12, 3002, 7003, 13.7779, 'KG', NULL, 0.0000, '500005', '200072');
GO
