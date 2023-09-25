import {Production} from "./analyse-flow.model";
import {FlowResponseHandler} from "./flow-response-handler";

export interface TreatmentFlowModel {
  uuid: string;
  composedFileId: string;
  idCreator: string;
  production?: Production;
}

export interface DocumentProcessing {
  DocId?: string;
  IdDoc?: string;
  DocName: string;
  Size: string;
  CreationDate: string;
  Filler1?: string;
  Filler2?: string;
  Filler3?: string;
  Filler4?: string;
  Filler5?: string;
}

export interface TreatmentData {
  documentProcessing: DocumentProcessing[];
  composedFileId: string;
}

export type TreatmentFlowResponse = FlowResponseHandler<TreatmentData>;

