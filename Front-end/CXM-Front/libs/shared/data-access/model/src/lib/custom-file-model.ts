export interface CustomFileModel extends File{
  id?: number;
  fileId?: string;
  filename?: string;
  contentType?: string;
  fileSize?: string;
  hash?: string;
}
