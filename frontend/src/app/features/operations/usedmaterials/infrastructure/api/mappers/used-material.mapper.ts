import { UsedMaterial, UsedMaterialPage } from '@features/operations/usedmaterials/domain/models/used-material.model';
import { UsedMaterialPageResponseDto, UsedMaterialResponseDto } from '@features/operations/usedmaterials/infrastructure/api/dto/used-material.dto';

/** Mapea el contrato HTTP de materiales utilizados al dominio frontend. */
export const UsedMaterialMapper = {
  toDomain(dto: UsedMaterialResponseDto): UsedMaterial {
    return {
      dischargeId: dto.descargaId,
      entryId: dto.entradaId,
      entryLineId: dto.partidaEntradaId,
      exitId: dto.salidaId,
      exitLineId: dto.partidaSalidaId,
      entryCustomsDocument: dto.pedimentoEntrada,
      exitCustomsDocument: dto.pedimentoSalida,
      materialCode: dto.materialCode,
      materialDescription: dto.materialDescription,
      productCode: dto.productCode,
      productDescription: dto.productDescription,
      incorporatedQuantity: dto.cantidadIncorporada,
      wasteQuantity: dto.cantidadMerma,
      scrapQuantity: dto.cantidadDesperdicio,
      totalDischargedQuantity: dto.cantidadTotalDescargada,
      unit: dto.unidad,
      date: dto.fecha,
      importPaymentDate: dto.fechaPagoImpo,
      importQuantity: dto.cantidadImpo,
      importTariffFraction: dto.fraccionImpo,
      importPartNumber: dto.numeroParteImpo,
      importUnit: dto.umcImpo,
      exportQuantity: dto.cantidadExpo,
      exportTariffFraction: dto.fraccionExpo,
      exportPartNumber: dto.numeroParteExpo,
      exportUnit: dto.umcExpo,
    };
  },

  toPage(dto: UsedMaterialPageResponseDto): UsedMaterialPage {
    return {
      items: dto.items.map((item) => UsedMaterialMapper.toDomain(item)),
      total: dto.total,
      page: dto.pagina,
      pageSize: dto.tamano,
    };
  },
};
