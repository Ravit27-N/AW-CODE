import { PrivilegeModel } from '@cxm-smartflow/shared/data-access/model';

interface KeyValue{
  key: string,
  value: string
}

export interface Summary {
  channel: string,
  subChannel?: string,
  pageProcessed: number,
  flowName: string,
  pageCount: number,
  pageError: number
}

interface EntityReponseHandler<T> {
  contents?: T[];
  page?: number;
  pageSize?: number;
  total?: number;
  isLoading?: boolean;
  summary?: Summary;
}

export interface DocumentModel {
  id: string;
  flowTraceabilityId: string;
  document: string;
  batchNumber: string;
  pageNumber: string;
  sheetNumber: string;
  fileSize: string;
  relatedItem: string;
  recipient: string;
  status: string;
  subStatus: string;
  statusLabelReplacement?: string;
  channel: string;
  subChannel: string;
  category?: string;
  dateStatus: string | Date;
  canShowDateStatus?: boolean;
  createdAt: string | Date;
  fileUrl: string;
  createdBy?: string;
  ownerId?: number,
  documentStatus: {
    statusLabel: string;
    status: string;
  },
  documentSubStatus: {
    statusLabel: string;
    status: string;
  },
  documentChannel: {
    statusLabel: string;
    status: string;
  },
  privilege?: PrivilegeModel
}

export type FlowDocumentList =  EntityReponseHandler<DocumentModel>;

export interface FlowDocumentParams{
  sortByField?: string,
  sortDirection?: string,
  filter?: string,
  channel?: string,
  subChannel?: string,
  status?: string,
  subStatus?: string,
  createdAtDateStart?: Date | string,
  createdAtDateEnd?: Date | string,
  statusDateStart?: Date | string,
  statusDateEnd?: Date | string
}

export enum FlowDocumentStatusName {
  scheduled = 'Scheduled' as any,
  completed = 'Completed' as any,
  in_error = 'In error' as any,
  in_progress = 'In progress' as any,
  canceled = 'Canceled' as any
}

export enum FlowDocumentHistoryEventName {
  in_production = 'In production' as any,
  deposited = 'Deposited' as any,
  distributed = 'Distributed' as any,
  refused = 'Refused' as any,
  unclaimed = 'Unclaimed' as any,
  receipt = 'Receipt' as any,
  accepted = 'Accepted' as any,
  certified = 'Certified' as any,
  notified = 'Notified' as any,
  read = 'Read' as any,
  sent = 'Sent' as any,
  in_error = 'In error' as any
}


export interface FlowDocumentElementAssociation{
  id?: number,
  flowDocumentId?: number
  elementName?: string,
  element: KeyValue,
  fileUrl?: string,
  fileId?: string,
  extension?: string
}

export interface CsvSuiviData {
  channels: string[];
  categories: string[];
  fillers: string[];
  status: string; // Modifier le type ici pour qu'il soit de type 'string'
  filter: string;
  startDate: Date | string;
  endDate: Date | string;
  sortByField: string,
  sortDirection: string,
  page: number,
  pageSize: number
}