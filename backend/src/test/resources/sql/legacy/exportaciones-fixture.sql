-- Exclusivo de Testcontainers: esquema sintético mínimo leído por PR_INFORME_EXPORTACIONES.
CREATE TABLE dbo.salidas (
    salidakey NUMERIC(18,0) PRIMARY KEY, Aduana CHAR(10), Agente CHAR(10), Documento CHAR(100),
    Fecha DATETIME, FechadePresentacion DATETIME, Tc FLOAT, dta FLOAT, DESCARGA VARCHAR(2),
    PedimentoOriginal VARCHAR(20), Cve_pedimento CHAR(5), TipoDescarga VARCHAR(12),
    CNT NUMERIC(18,4), prev FLOAT, observaciones CHAR(78), FCreacion DATETIME, FactorME FLOAT
);
CREATE TABLE dbo.psalidas (
    psalidakey NUMERIC(18,0) PRIMARY KEY, Salidalink NUMERIC(18,0), codProveedor VARCHAR(20),
    Paisc VARCHAR(10), paisd VARCHAR(10), Clave VARCHAR(50), Descripcion VARCHAR(250),
    Cantidad NUMERIC(18,4), Unidad CHAR(5), Val_pesos NUMERIC(18,4), Val_dolares NUMERIC(18,4),
    Factura CHAR(50), Fecha DATETIME, Fraccion VARCHAR(12), NICO VARCHAR(10), partida FLOAT,
    bloqueado INT, valorcomercial FLOAT, valoragregado FLOAT, descargaDirigida VARCHAR(50),
    COVE VARCHAR(100), usuario VARCHAR(100), FechaModificacion DATETIME, cantidadT FLOAT,
    UnidadT VARCHAR(10), ANEXO CHAR(3), VALORADUANACR NUMERIC(18,4), FCreacion DATETIME,
    PEDIMENTOEXPORTACIONCTM VARCHAR(20)
);
CREATE TABLE dbo.clientes (Clave VARCHAR(20) PRIMARY KEY, Nombre VARCHAR(250), Idfiscal CHAR(20));
CREATE TABLE dbo.PROVEEDORES (CLAVE VARCHAR(20) PRIMARY KEY, NOMBRE VARCHAR(250), IDFISCAL CHAR(20));
CREATE TABLE dbo.dirigido (dirigidokey NUMERIC(18,0) PRIMARY KEY, psalidakey NUMERIC(18,0));

INSERT INTO dbo.clientes VALUES ('C-1', 'Cliente sintético', 'RFC-SINTETICO');
INSERT INTO dbo.PROVEEDORES VALUES ('P-1', 'Proveedor sintético', 'TAX-SINTETICO');
INSERT INTO dbo.salidas (salidakey, Aduana, Agente, Documento, Fecha, FechadePresentacion,
    Tc, dta, Cve_pedimento, CNT, prev, FCreacion, FactorME) VALUES
    (100, '190', '1234', 'DOC-A', '2026-01-10', '2026-01-09', 18.25, 12, 'RT', 3, 4, '2026-01-08', 1.25),
    (200, '190', '1234', 'DOC-B', '2026-02-10', '2026-02-09', NULL, NULL, 'F4', NULL, NULL, '2026-02-08', NULL);
INSERT INTO dbo.psalidas (psalidakey, Salidalink, codProveedor, Paisc, paisd, Clave, Descripcion,
    Cantidad, Unidad, Val_pesos, Val_dolares, Factura, Fecha, Fraccion, NICO, partida,
    bloqueado, valorcomercial, valoragregado, COVE, VALORADUANACR, PEDIMENTOEXPORTACIONCTM) VALUES
    (1001, 100, 'C-1', 'MX', 'US', 'PROD-A', 'Producto sintético', 2, 'PZA', 100.0000, 5.0000,
     'FAC-A', '2026-01-07', '8501', '01', 1, 0, 30.5, 10.25, 'COVE-A', 90, 'CTM-A'),
    (1002, 100, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL,
     NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL),
    (2001, 200, 'P-1', 'CA', 'MX', 'PROD-B', 'Otro producto', 4, 'KG', 80, 4,
     'FAC-B', NULL, '9901', '02', 1, 1, 7, 3, NULL, 75, NULL);
INSERT INTO dbo.dirigido VALUES (1, 1001);
