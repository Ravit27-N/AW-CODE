import { ProductionDetailsMetadataModel } from './production-details-metadata.model';

export interface GlobalProductionDetailsModel {
  loading?: boolean;
  metaData: ProductionDetailsMetadataModel[],
  result: any []
}

export interface ProductionDetails {
  loading?: boolean;
  fillerGroupingApplied?: number;
  metaData: ProductionDetailsMetadataModel[],
  data: any [],
  total: any;
}
