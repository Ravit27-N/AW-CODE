export interface ResourceLibraryResponse {
  id: number;
  fileName: string;
  fileId: string;
  label: string;
  type: string;
  fileSize: number;
  pageNumber: number;
  ownerId: number;
  createdAt: Date;
  lastModified: Date;
  createdBy: string;
  lastModifiedBy: string;
}
