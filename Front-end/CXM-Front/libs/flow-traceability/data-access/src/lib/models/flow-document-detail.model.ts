import { FlowHistoryModel } from './flow-history.model';

interface ProductCriterial {
  pageBackground?: string,
  envelope?: string,
  archiving?: string,
  addition?: string,
  color?: string,
  impression?: string,
  postage?: string,
  postalPickup?: string
}

export interface FlowDocumentDetailModel{
  id?: number,
  document?: string,
  batchNumber?: number,
  pageNumber?: number,
  sheetNumber?: number ,
  fileSize?: number,
  channel?: string,
  flowTraceabilityId?: number,
  dateStatus?: Date,
  fileUrl?: string;
  fileId?: string;
  documentChannel?: DocumentChannel;
  details?: {
    id?: number,
    flowName?: string,
    address?: string,
    email?: string,
    campaignName?: string,
    telephone?: string,
    reference?: string,
    fillers: string[],
    docName?: string;
    productCriteria?: ProductCriterial
  },
  flowHistories?: FlowHistoryModel []
}

export interface DocumentChannel {
  status?: string;
  statusLabel?: string;
}
