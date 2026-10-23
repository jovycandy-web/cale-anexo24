USE CALE_IMMEX;
GO
CREATE OR ALTER PROCEDURE dbo.APP24_Q_PROVEEDORES_LISTAR
 @Filtro VARCHAR(250)=NULL,@Pagina INT=1,@Tamano INT=20,@Total BIGINT OUTPUT
AS
BEGIN
 SET NOCOUNT ON;
 IF @Pagina<1 OR @Tamano<1 OR @Tamano>100 THROW 50062,'Parámetros de paginación inválidos.',1;
 SET @Filtro=NULLIF(LTRIM(RTRIM(@Filtro)),'');
 SELECT @Total=COUNT_BIG(1) FROM dbo.Proveedores p
 OUTER APPLY (
  SELECT TOP (1) a.DESCRIPCION
  FROM dbo.almacen a
  WHERE a.ALMACENKEY=p.ALMACENKEY
  ORDER BY a.DESCRIPCION, a.ALMACEN
 ) a
 WHERE @Filtro IS NULL OR p.Clave LIKE '%'+@Filtro+'%' OR p.Nombre LIKE '%'+@Filtro+'%'
   OR p.Idfiscal LIKE '%'+@Filtro+'%' OR p.Tipone LIKE '%'+@Filtro+'%' OR p.Programa LIKE '%'+@Filtro+'%'
   OR p.CalleNumero LIKE '%'+@Filtro+'%' OR p.Colonia LIKE '%'+@Filtro+'%' OR p.Codigo LIKE '%'+@Filtro+'%'
   OR p.Localidad LIKE '%'+@Filtro+'%' OR p.Municipio LIKE '%'+@Filtro+'%' OR p.Entidad LIKE '%'+@Filtro+'%'
   OR p.Pais LIKE '%'+@Filtro+'%' OR p.Telefono LIKE '%'+@Filtro+'%' OR p.Correo LIKE '%'+@Filtro+'%'
   OR p.Fax LIKE '%'+@Filtro+'%' OR a.DESCRIPCION LIKE '%'+@Filtro+'%'
   OR CONVERT(VARCHAR(10),p.callenumerointerior) LIKE '%'+@Filtro+'%';
 SELECT CONVERT(BIGINT,p.proveedorkey) proveedorkey,LTRIM(RTRIM(p.Clave)) clave,LTRIM(RTRIM(p.Nombre)) nombre,LTRIM(RTRIM(p.Idfiscal)) idfiscal,LTRIM(RTRIM(p.Pais)) pais,LTRIM(RTRIM(p.Correo)) correo,
  LTRIM(RTRIM(p.Tipone)) tipone,LTRIM(RTRIM(p.Programa)) programa,LTRIM(RTRIM(p.CalleNumero)) callenumero,CONVERT(INT,p.callenumerointerior) callenumerointerior,
  LTRIM(RTRIM(p.Colonia)) colonia,LTRIM(RTRIM(p.Codigo)) codigo,LTRIM(RTRIM(p.Localidad)) localidad,LTRIM(RTRIM(p.Municipio)) municipio,LTRIM(RTRIM(p.Entidad)) entidad,
  LTRIM(RTRIM(p.Telefono)) telefono,LTRIM(RTRIM(p.Fax)) fax,
  CONVERT(BIGINT,p.ALMACENKEY) almacenkey,LTRIM(RTRIM(a.DESCRIPCION)) almacendescripcion
 FROM dbo.Proveedores p
 OUTER APPLY (
  SELECT TOP (1) a.DESCRIPCION
  FROM dbo.almacen a
  WHERE a.ALMACENKEY=p.ALMACENKEY
  ORDER BY a.DESCRIPCION, a.ALMACEN
 ) a
 WHERE @Filtro IS NULL OR p.Clave LIKE '%'+@Filtro+'%' OR p.Nombre LIKE '%'+@Filtro+'%'
   OR p.Idfiscal LIKE '%'+@Filtro+'%' OR p.Tipone LIKE '%'+@Filtro+'%' OR p.Programa LIKE '%'+@Filtro+'%'
   OR p.CalleNumero LIKE '%'+@Filtro+'%' OR p.Colonia LIKE '%'+@Filtro+'%' OR p.Codigo LIKE '%'+@Filtro+'%'
   OR p.Localidad LIKE '%'+@Filtro+'%' OR p.Municipio LIKE '%'+@Filtro+'%' OR p.Entidad LIKE '%'+@Filtro+'%'
   OR p.Pais LIKE '%'+@Filtro+'%' OR p.Telefono LIKE '%'+@Filtro+'%' OR p.Correo LIKE '%'+@Filtro+'%'
   OR p.Fax LIKE '%'+@Filtro+'%' OR a.DESCRIPCION LIKE '%'+@Filtro+'%'
   OR CONVERT(VARCHAR(10),p.callenumerointerior) LIKE '%'+@Filtro+'%'
 ORDER BY p.Clave OFFSET (CAST(@Pagina AS BIGINT)-1)*CAST(@Tamano AS BIGINT) ROWS FETCH NEXT @Tamano ROWS ONLY;
END;
GO
