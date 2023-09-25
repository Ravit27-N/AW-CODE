export interface SwitchFlowModel {
  uuid: string,
  composedFileId: string,
  validation: boolean
}

export interface SwitchProduction {
  urgency: string;
  color: string;
  recto: string;
  wrap: string;
}

export interface SwitchFlowResponseModel {
  filename: string;
  nbDocuments: number;
  nbPages: number;
  nbFailed: number;
  productions: SwitchProduction[];
}
