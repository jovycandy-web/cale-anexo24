import { Product } from '@features/catalogs/products/domain/models/product.model';
import { ProductPage } from '@features/catalogs/products/domain/repositories/product.repository';
import { ProductPageResponseDto, ProductResponseDto } from '@features/catalogs/products/infrastructure/dto/product-response.dto';

export const ProductMapper = {
  toDomain(dto: ProductResponseDto): Product {
    return {
      id: dto.id,
      partNumber: dto.clave,
      clientCode: dto.claveCliente ?? null,
      description: dto.descripcion,
      tariffFraction: dto.fraccion,
      commercialUnit: dto.unidadComercial,
      tariffUnit: dto.unidadTarifaria,
      nico: dto.nico ?? null,
      type: dto.tipo ?? null,
      auxiliary: dto.auxiliar ?? null,
      warehouseKey: dto.almacenKey ?? null,
      warehouse: dto.almacen ?? null,
    };
  },
  toPage(dto: ProductPageResponseDto): ProductPage {
    return {
      items: dto.items.map((item) => ProductMapper.toDomain(item)),
      total: dto.total,
      page: dto.pagina,
      pageSize: dto.tamano,
    };
  },
};
