import { EntityResponseHandler, PrivilegeModel } from '@cxm-smartflow/shared/data-access/model';

export interface FlowTraceabilityModel {
  id?: number,
  depositDate?: Date,
  depositMode?: string,
  flowName?: string,
  createdBy?: string,
  ownerId: number,
  service?: string
  channel?: string
  subChannel?: string,
  category?: string,
  status?: string
  dateStatus?: Date
  createdAt?: Date | string;
  updatedAt?: Date;
  lastModified?: Date,
  lastModifiedBy?: string,
  fullName?: string,
  fileId?: string;
  flowStatus?: {
    statusLabel?: string,
    statusLabelReplacement?: string,
    status?: string
  },
  fileUrl?: string,
  flowChannel?: {
    statusLabel?: string,
    status?: string
  };
  flowSubChannel?: {
    statusLabel?: string,
    status?: string
  };
  flowDepositMode?: {
    statusLabel?: string,
    status?: string
  }
  privilege?: PrivilegeModel
}

export type FlowTraceabilityList = EntityResponseHandler<FlowTraceabilityModel>;

export interface FlowTraceabilityParams {
  sortByField?: string,
  sortDirection?: string,
  filter?: string,
  channel?: string,
  subChannel?: string,
  status?: string,
  depositMode?: string
  createdBy?: string
  service?: string,
  startDate?: Date | string;
  endDate?: Date | string;
  ownerId?: number;
}

export type FlowConfirmMessage = {
  confirmMessage: ConfirmMessage;
  information: any;
};

export type ConfirmMessage = {
  title: string;
  message: string;
  cancelButton: string;
  confirmButton: string;
  paragraph?: string;
};

export type SortDirection = 'asc' | 'desc';
