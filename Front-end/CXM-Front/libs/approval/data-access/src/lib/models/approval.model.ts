

export interface FlowApproval {
  campaignFilename: string,
  campaignName: string,
  channel: string,
  composedId: string,
  createdAt: string,
  createdBy: string,
  ownerId: number,
  dateStatus: string,
  depositDate: string,
  depositMode: string,
  depositType: string,
  fileId: string,
  fileUrl: string,
  flowChannel: any,
  flowDepositMode: any,
  flowStatus: any,
  flowSubChannel: any,
  fullName: string,
  id: number,
  service: string,
  status: string,
  subChannel: string,
  totalDocument: number,
  totalRemainingValidationDocument: number
}

export interface ListFlowApprovalReposne {
  contents: Array<any>,
  page: number,
  pageSize: number,
  total: number
}
