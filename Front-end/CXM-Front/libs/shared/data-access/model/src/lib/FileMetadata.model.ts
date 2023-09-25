export interface FileMetadataModel{
  idCreator: number;
  uuid: string;
  refFile?: string;
  fileName: string;
  content: string;
  contentType: string;
  hash?: string;
  lifeTime?: number;
  size: number;
  isPublished: boolean;
  fileUrl?: string;
}
