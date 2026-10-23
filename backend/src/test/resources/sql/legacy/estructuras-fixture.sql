-- Fixture sintético mínimo para ejecutar dbo.PR_INFORME_ESTRUCTURAS en aislamiento.
-- No contiene datos reales: reproduce sólo las columnas que el procedimiento lee.

CREATE TABLE dbo.productos (
    PRODUCTOKEY   BIGINT       NOT NULL PRIMARY KEY,
    CVE_PRODUCTO  VARCHAR(80)  NOT NULL,
    NOMBRE        VARCHAR(250) NULL,
    UNIDAD        CHAR(10)     NULL
);

CREATE TABLE dbo.material (
    clave       VARCHAR(50)  NOT NULL PRIMARY KEY,
    descripcion VARCHAR(250) NULL,
    unidad      CHAR(5)      NULL,
    fraccion    CHAR(10)     NULL
);

CREATE TABLE dbo.estructuras (
    estructurakey BIGINT   NOT NULL PRIMARY KEY,
    productolink  BIGINT   NULL,
    inicio        DATETIME NULL
);

CREATE TABLE dbo.productomaterial (
    PRODMATKEY        NUMERIC(18, 0) NOT NULL PRIMARY KEY,
    CVE_MATERIAL      VARCHAR(50)    NULL,
    CANT_UTILIZADA    NUMERIC(18, 7)  NULL,
    CANT_MERMADA      NUMERIC(18, 7)  NULL,
    CANT_DESPERDICIADA NUMERIC(18, 7) NULL,
    estructuralink    BIGINT         NULL
);

CREATE TABLE dbo.salidas (
    SalidaKey BIGINT   NOT NULL PRIMARY KEY,
    Fecha     DATETIME NULL
);

CREATE TABLE dbo.psalidas (
    PsalidaKey BIGINT      NOT NULL PRIMARY KEY,
    Salidalink BIGINT      NULL,
    Clave      VARCHAR(50) NULL
);

INSERT INTO dbo.productos (PRODUCTOKEY, CVE_PRODUCTO, NOMBRE, UNIDAD) VALUES
    (10, 'P001', 'Producto Uno', 'PZA'),
    (20, 'P002', 'Producto Dos', 'PZA'),
    (30, 'P003', 'Producto Sin Estructura', 'PZA');

INSERT INTO dbo.material (clave, descripcion, unidad, fraccion) VALUES
    ('M001', 'Acero', 'KG', '7208'),
    ('M002', 'Cobre', 'KG', '7403'),
    ('M003', 'Zinc', 'KG', '7901');

INSERT INTO dbo.estructuras (estructurakey, productolink, inicio) VALUES
    (1001, 10, '2020-01-01'),
    (1002, 10, '2021-01-01'),
    (1003, 20, '2019-05-01');

INSERT INTO dbo.productomaterial (PRODMATKEY, CVE_MATERIAL, CANT_UTILIZADA, CANT_MERMADA, CANT_DESPERDICIADA, estructuralink) VALUES
    (100, 'M001', 2.5000000, 0.1000000, 0.2000000, 1001),
    (101, 'M002', 1.0000000, 0.0000000, 0.0000000, 1002),
    (102, 'M001', 4.0000000, 0.5000000, 0.2500000, 1003),
    (103, 'M003', 7.0000000, 0.0000000, 0.0000000, NULL);

INSERT INTO dbo.salidas (SalidaKey, Fecha) VALUES
    (5001, '2020-06-15'),
    (5002, '2020-09-01');

INSERT INTO dbo.psalidas (PsalidaKey, Salidalink, Clave) VALUES
    (1, 5001, 'P001'),
    (2, 5002, 'P001'),
    (3, 5002, 'P002');
