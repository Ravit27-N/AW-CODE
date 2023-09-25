import { SortDirection } from './flow-traceability.model';

interface KeyValue {
  key: string;
  value: string;
}

export interface FlowTraceabilityFilterCriterialModel {
  channel?: KeyValue[];
  subChannel?: KeyValue[];
  depositMode?: KeyValue[];
  flowStatus?: KeyValue[];
}

export interface FlowTraceabilitySubChannelModel {
  subChannel?: string[];
}

export interface FlowFilterCriteriaParams {
  sortByField?: string;
  sortDirection?: string | SortDirection;
  filter?: string;
  channels?: string[];
  categories?: string[];
  status?: string[];
  depositModes?: string[];
  users?: string[];
  startDate?: Date | string;
  endDate?: Date | string;
  isDisplayLabel?: boolean;
  fillers?: string[];
  searchByFiller?: string;

}

export function validateParams(params: FlowFilterCriteriaParams): boolean {
  if (Object.keys(params).length !== 0) {
    return !!(
      (params.categories && params.categories.length !== 0) ||
      (params.users && params.users.length !== 0) ||
      (params.status && params.status.length !== 0) ||
      (params.channels && params.channels.length !== 0) ||
      (params.depositModes && params.depositModes.length !== 0) ||
      params.startDate ||
      params.endDate
    );
  }
  return false;
}
