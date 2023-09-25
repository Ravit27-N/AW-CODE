import {ModifiedFlowDocumentAddress, FlowResponseHandler} from "./flow-response-handler";

export interface AnalyseFlowModel {
  uuid: string;
  modelName: string;
  flowType: string;
  fileId: string;
  idCreator: string;
  channel: string;
  subChannel?: string;
}

export interface Address {
  [key: string]: string
}

export interface Production {
  Confirmation?: any;
  Validation?: any;
  Archiving?: string;
  GED?: any;
  RecoveryMode?: any;
  RecoveryTime?: any;
  Urgency: string;
  Color: string;
  Recto: string;
  Wrap?: string;
  AddressPage?: any;
  Signature?: any;
  Watermark?: any;
  FDP?: any;
  PJs?: any;
  Datas?: any;
  ZipCode?: string;
  CountryCode?: any;
}

export interface Processing {
  DocUUID?: any;
  ServerName?: any;
  DocName: string;
  Size?: any;
  CreationDate?: any;
  Filler1: string;
  Filler2: string;
  Filler3: string;
  Filler4: string;
  Filler5: string;
}

export interface Document {
  DocUUID: string;
  UUID?: any;
  Offset: string;
  NbPages: string;
  Analyse: string;
  Filiere: string;
  'Sous-filere'?: any;
  RecipientID: string;
  EmailRecipient: string;
  EmailObject?: any;
  ADDRESS: Address;
  PRODUCTION: Production;
  PROCESSING: Processing;
  Analysis?: string;
  Channel?: string;
}

export interface DocumentDetails {
  NbDocuments: string;
  NbPages: string;
  NbDocumentsKO: string;
  DOCUMENT: Document[];
}

export interface Data {
  document: DocumentDetails;
  composedFileId: string;
  modifiedFlowDocumentAddress?: ModifiedFlowDocumentAddress[];
}

export type AnalyseFlowResponse = FlowResponseHandler<Data>;
