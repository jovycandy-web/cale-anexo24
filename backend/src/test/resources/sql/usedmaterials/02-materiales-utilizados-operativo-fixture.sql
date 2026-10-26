/*
 * Fixture exclusivo para Testcontainers. No representa datos reales ni se
 * despliega en LIVE. Reproduce los tipos fisicos documentados de
 * dbo.DESCARGA, dbo.PARTIDAS, dbo.IMPORTACIONES, dbo.PSALIDAS y dbo.SALIDAS
 * que consume APP24_Q_MATERIALES_UTILIZADOS_LISTAR.
 */

IF OBJECT_ID('dbo.IMPORTACIONES', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.IMPORTACIONES (
        Ipedimentokey NUMERIC(18,0) NOT NULL CONSTRAINT PK_IMPO PRIMARY KEY,
        Numero_ped VARCHAR(20) NULL,
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
        Fraccion CHAR(12) NULL,
        Descripcion VARCHAR(250) NULL,
        Cantidad NUMERIC(18,4) NULL,
        Unidad CHAR(5) NULL,
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
        Unidad CHAR(5) NULL,
        Desperdicio FLOAT NULL,
        Merma FLOAT NULL,
        Clave VARCHAR(50) NULL,
        Importacion VARCHAR(50) NULL,
        Salida VARCHAR(50) NULL,
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

INSERT INTO dbo.IMPORTACIONES (Ipedimentokey, Numero_ped, Fecha) VALUES
    (1, '5003971', '2025-09-23'),
    (2, '6001720', '2026-05-28');
GO

INSERT INTO dbo.PARTIDAS (Partidakey, Importacionlink, Clave, Descripcion, Fraccion, Cantidad, Unidad) VALUES
    (11, 1, '500017', 'MATERIAL UNO', '17019999', 6000000.0000, 'KG'),
    (12, 1, '500005', 'MATERIAL DOS', '17019999', 126428.0804, 'KG'),
    (13, 2, 'MAT-ORPHAN', 'PARTIDA SIN DESCARGA', '39269003', 10.0000, 'PZA');
GO

INSERT INTO dbo.SALIDAS (SalidaKey, Documento, Cve_pedimento, Fecha) VALUES
    (3001, '190-1562-5001241', 'F4', '2025-10-31'),
    (3002, '190-1562-5001248', 'F4', '2025-11-03');
GO

INSERT INTO dbo.PSALIDAS (Psalidakey, Fraccion, Descripcion, Cantidad, Unidad, Clave, Salidalink) VALUES
    (7001, '17049099', 'PRODUCTO UNO', 576.0000, 'CAJA', '301275', 3001),
    (7002, '17049099', 'PRODUCTO DOS', 92.0000, 'CAJA', '200060', 3001),
    (7003, '17049099', 'PRODUCTO TRES', 5.0000, 'CAJA', '200072', 3002);
GO

INSERT INTO dbo.DESCARGA (Descargakey, Entradalink, Pentradalink, Salidalink, Psalidalink,
                          CantUtil, Unidad, Desperdicio, Merma, Clave, Importacion, Salida, PT) VALUES
    (9001, 1, 11, 3001, 7001, 1140.3102, 'KG', NULL, 6.7741, '500017', '5003971', '190-1562-5001241', '301275'),
    (9002, 1, 11, 3001, 7002, 1088.2952, 'KG', 2.5000, 6.4651, '500017', '5003971', '190-1562-5001241', '200060'),
    (9003, 1, 12, 3002, 7003, 13.7779, 'KG', 0.0000, NULL, '500005', '5003971', '190-1562-5001248', '200072');
GO
