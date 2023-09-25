export interface ApprovalDocModel {
  id: number,
  channel: string,
  depositDate: Date | string,
  flowName: string,
  fullName: string,
  subChannel: string,
  createdBy: string,
  ownerId: number,
  docName: string,
  fileId: string
}

export interface ApproveDocResponse {
  contents: Array<ApprovalDocModel>,
  page: number, pageSize: number, total: number
}
