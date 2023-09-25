import { EntityResponseHandler } from '@cxm-smartflow/shared/data-access/model';
import { PrivilegeModel } from '@cxm-smartflow/shared/data-access/model';
interface StatusTranslation{
  statusLabel?: string,
  status?: string
}

export interface FlowDepositModel {
  id?: number,
  fileId?: string,
  composedFileId?: string,
  step?: number,
  flowName?: string,
  user?: string,
  fullName?: string,
  dateStatus?: Date,
  createdAt?: Date,
  flowStatus?: StatusTranslation,
  statusColorClass?: string,
  flowChannel?: StatusTranslation,
  flowSubChannel?: StatusTranslation,
  flowDepositMode?: StatusTranslation,
  category?: string,
  validated?: boolean,
  createdBy?: string,
  ownerId?: number,
  privilege?: PrivilegeModel
  lastModified: Date
}

export type FlowDepositList = EntityResponseHandler<FlowDepositModel>;
