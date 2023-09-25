export interface CommunicationInteractiveResponse {
  contents: Array<{
    id: number,
    name: string,
    displayName: string,
    customer: string,
    division: string,
    department: string
  }>,
  page: number,
  pageSize: number,
  total: number
}
