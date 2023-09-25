export interface CampaignDestinationModel{
  originalName: string,
  fileName: string,
  fileSize?: number;
  totalRecord: number;
  records: any;
  columns: Array<any>;
  columnHeader: string[];
  filePath: string
}
