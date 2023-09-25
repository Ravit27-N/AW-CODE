export interface ResourceResponse {
  id: number;
  fileName: string;
  fileId: string;
  label: string;
  type: string;
  fileSize: string;
  pageNumber: number;
  ownerId: number;
  createdAt: Date;
  lastModified: Date;
  createdBy: string;
  lastModifiedBy: string;
  canDelete?: boolean;
  canView?: boolean;
}

export interface ResourceResponseList {
  contents: ResourceResponse[],
  page: number,
  pageSize: number,
  total: number,
}
