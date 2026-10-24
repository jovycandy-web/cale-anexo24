/*
 * Fixture exclusivo para Testcontainers. No representa datos reales ni se
 * despliega en LIVE. Reproduce los tipos documentados de dbo.SALIDAS y
 * dbo.PSALIDAS que consume APP24_Q_SALIDAS_LISTAR.
 */

IF OBJECT_ID('dbo.SALIDAS', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.SALIDAS (
        SalidaKey NUMERIC(18,0) NOT NULL CONSTRAINT PK_SALIDAS PRIMARY KEY,
        Documento CHAR(60) NULL,
        Cve_pedimento CHAR(5) NULL,
        Fecha DATETIME NULL,
        FechaEntrada DATETIME NULL,
        FechadePresentacion DATETIME NULL
    );
END;

IF OBJECT_ID('dbo.PSALIDAS', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PSALIDAS (
        Psalidakey NUMERIC(18,0) NOT NULL CONSTRAINT PK_PSALIDAS PRIMARY KEY,
        Fraccion CHAR(15) NULL,
        Unidad CHAR(10) NULL,
        Cantidad NUMERIC(18,5) NULL,
        Clave VARCHAR(50) NULL,
        Salidalink FLOAT NULL,
        partida FLOAT NULL
    );
END;
GO

DELETE FROM dbo.PSALIDAS;
DELETE FROM dbo.SALIDAS;
GO

INSERT INTO dbo.SALIDAS (SalidaKey, Documento, Cve_pedimento, Fecha, FechaEntrada, FechadePresentacion) VALUES
    (3001, '190-1562-5001241', 'F4', '2026-03-10', '2026-02-20', '2026-03-01'),
    (3002, '160-3750-6001741', 'A1', '2026-03-12', NULL, NULL),
    (3003, '190-1562-5001241', 'F4', '2025-01-05', '2025-01-02', '2025-01-03');
GO

INSERT INTO dbo.PSALIDAS (Psalidakey, Fraccion, Unidad, Cantidad, Clave, Salidalink, partida) VALUES
    (7001, '17019999', 'KG', 12.34000, '300099', 3001, 1),
    (7002, '39269003', 'PZA', 4.00000, 'MAT-2', 3001, 2),
    (7003, '17019999', 'KG', 1.50000, 'MAT-3', 3002, 1),
    (7004, '17019999', 'KG', 9.00000, 'MAT-4', 3003, 1);
GO
