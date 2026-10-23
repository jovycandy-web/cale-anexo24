USE CALE_IMMEX;
GO
CREATE OR ALTER PROCEDURE dbo.APP24_Q_CLIENTES_LISTAR
 @Filtro VARCHAR(250)=NULL,@Pagina INT=1,@Tamano INT=20,@Total BIGINT OUTPUT
AS
BEGIN
 SET NOCOUNT ON;
 IF @Pagina<1 OR @Tamano<1 OR @Tamano>100 THROW 50061,'Parámetros de paginación inválidos.',1;
 SET @Filtro=NULLIF(LTRIM(RTRIM(@Filtro)),'');
 SELECT @Total=COUNT_BIG(1) FROM dbo.clientes c
 WHERE @Filtro IS NULL OR c.Clave LIKE '%'+@Filtro+'%' OR c.Nombre LIKE '%'+@Filtro+'%'
   OR c.Idfiscal LIKE '%'+@Filtro+'%' OR c.Tipone LIKE '%'+@Filtro+'%' OR c.Programa LIKE '%'+@Filtro+'%'
   OR c.Callenumero LIKE '%'+@Filtro+'%' OR c.Colonia LIKE '%'+@Filtro+'%' OR c.Codigo LIKE '%'+@Filtro+'%'
   OR c.Localidad LIKE '%'+@Filtro+'%' OR c.Municipio LIKE '%'+@Filtro+'%' OR c.Entidad LIKE '%'+@Filtro+'%'
   OR c.Pais LIKE '%'+@Filtro+'%' OR c.Telefono LIKE '%'+@Filtro+'%' OR c.Correo LIKE '%'+@Filtro+'%'
   OR c.Fax LIKE '%'+@Filtro+'%' OR c.ApellidoPaterno LIKE '%'+@Filtro+'%' OR c.ApellidoMaterno LIKE '%'+@Filtro+'%'
   OR c.calle LIKE '%'+@Filtro+'%' OR c.referencia LIKE '%'+@Filtro+'%' OR c.tipoidentificador LIKE '%'+@Filtro+'%'
   OR c.codigopostal LIKE '%'+@Filtro+'%' OR CONVERT(VARCHAR(10),c.callenumerointerior) LIKE '%'+@Filtro+'%';
 SELECT CONVERT(BIGINT,clientekey) clientekey,LTRIM(RTRIM(Clave)) clave,LTRIM(RTRIM(Nombre)) nombre,LTRIM(RTRIM(Idfiscal)) idfiscal,LTRIM(RTRIM(Pais)) pais,LTRIM(RTRIM(Correo)) correo,
  LTRIM(RTRIM(Tipone)) tipone,LTRIM(RTRIM(Programa)) programa,LTRIM(RTRIM(Callenumero)) callenumero,CONVERT(INT,callenumerointerior) callenumerointerior,
  LTRIM(RTRIM(Colonia)) colonia,LTRIM(RTRIM(Codigo)) codigo,LTRIM(RTRIM(Localidad)) localidad,LTRIM(RTRIM(Municipio)) municipio,LTRIM(RTRIM(Entidad)) entidad,
  LTRIM(RTRIM(Telefono)) telefono,LTRIM(RTRIM(Fax)) fax,LTRIM(RTRIM(ApellidoPaterno)) apellidopaterno,LTRIM(RTRIM(ApellidoMaterno)) apellidomaterno,
  LTRIM(RTRIM(calle)) calle,LTRIM(RTRIM(referencia)) referencia,LTRIM(RTRIM(tipoidentificador)) tipoidentificador,LTRIM(RTRIM(codigopostal)) codigopostal
 FROM dbo.clientes c
 WHERE @Filtro IS NULL OR c.Clave LIKE '%'+@Filtro+'%' OR c.Nombre LIKE '%'+@Filtro+'%'
   OR c.Idfiscal LIKE '%'+@Filtro+'%' OR c.Tipone LIKE '%'+@Filtro+'%' OR c.Programa LIKE '%'+@Filtro+'%'
   OR c.Callenumero LIKE '%'+@Filtro+'%' OR c.Colonia LIKE '%'+@Filtro+'%' OR c.Codigo LIKE '%'+@Filtro+'%'
   OR c.Localidad LIKE '%'+@Filtro+'%' OR c.Municipio LIKE '%'+@Filtro+'%' OR c.Entidad LIKE '%'+@Filtro+'%'
   OR c.Pais LIKE '%'+@Filtro+'%' OR c.Telefono LIKE '%'+@Filtro+'%' OR c.Correo LIKE '%'+@Filtro+'%'
   OR c.Fax LIKE '%'+@Filtro+'%' OR c.ApellidoPaterno LIKE '%'+@Filtro+'%' OR c.ApellidoMaterno LIKE '%'+@Filtro+'%'
   OR c.calle LIKE '%'+@Filtro+'%' OR c.referencia LIKE '%'+@Filtro+'%' OR c.tipoidentificador LIKE '%'+@Filtro+'%'
   OR c.codigopostal LIKE '%'+@Filtro+'%' OR CONVERT(VARCHAR(10),c.callenumerointerior) LIKE '%'+@Filtro+'%'
 ORDER BY c.Clave OFFSET (CAST(@Pagina AS BIGINT)-1)*CAST(@Tamano AS BIGINT) ROWS FETCH NEXT @Tamano ROWS ONLY;
END;
GO
