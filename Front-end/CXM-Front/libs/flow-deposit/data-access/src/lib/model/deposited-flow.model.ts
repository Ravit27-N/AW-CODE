export interface DepositedFlowModel {
  customer: string;
  depositType: string;
  connector?: string;
  fileName: string;
  extension: string;
  size: number;
  depositDate: Date;
  fileId: string;
  flowType: string;
  uuid: string;
  serverName: string;
  idCreator: string;
  serviceId: string;
  serviceName: string;
  userName: string;
}

export interface ComposedFileRequestModel {
  fileId: string;
  docId?: string;
}

export interface NavigateParam {
  fileId: string,
  composedFileId?: string,
  step: number,
  validation: boolean,
  createdBy?: string,
  ownerId?: number
}
