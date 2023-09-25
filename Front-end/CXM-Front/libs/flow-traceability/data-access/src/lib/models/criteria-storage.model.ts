import { FlowFilterCriteriaParams } from './flow-traceability-filter-criterial.model';

export interface CriteriaStorage {
  page?: number;
  pageSize?: number;
  usersSelected?: string[];
  dateRangeSelected?: CriteriaDateRange;

  criteriaParams?: FlowFilterCriteriaParams;
}

export enum CriteriaStorageKey {
  FLOW_DOCUMENT = 'documentCriteria',
  FLOW_DOCUMENT_SHIPMENT = 'documentShipmentCriteria',
  FLOW_TRACEABILITY = 'flowCriteria',
}

export interface CriteriaDateRange {
  start?: string | Date;
  end?: string | Date;
  isDisplayLabel?: boolean;
}
