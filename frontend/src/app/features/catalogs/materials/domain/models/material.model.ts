export interface Material {
  materialKey: number;
  partNumber: string;
  supplierCode: string | null;
  description: string;
  tariffFraction: string;
  unit: string;
  umc: string;
  materialType: string;
  nationalType: string | null;
  family: string | null;
  nico: string | null;
  warehouseKey: number | null;
  warehouse: string | null;
}
