export type TechnicalId = string | number;

export interface Product {
  id: TechnicalId;
  partNumber: string;
  clientCode: string | null;
  description: string;
  tariffFraction: string;
  commercialUnit: string;
  tariffUnit: string;
  nico: string | null;
  type: string | null;
  auxiliary: string | null;
  warehouseKey: TechnicalId | null;
  warehouse: string | null;
}
