import { AnalyseFlowModel, AnalyseFlowResponse } from './analyse-flow.model';
import { TreatmentFlowModel, TreatmentFlowResponse } from './treatment-flow.model';
import { ComposedFileRequestModel, DepositedFlowModel } from './deposited-flow.model';
import { SwitchFlowModel, SwitchFlowResponseModel } from './switch-flow.model';


export interface DefaultConfiguration {
  Urgency?: string;
  Color?: string;
  Recto?: string;
  Wrap?: string;
  filigrane?: string;
  Signature?: string;
  FDP?: string;
  PositionFDP?: string;
  PJ1?: string;
  PJ2?: string;
  PJ3?: string;
  PJ4?: string;
  PJ5?: string;
  Workflow?: string;
  FilText?:string;
  FilPosition?:string;
  FilSize?:string;
  FilRotation?:string;
  FilColor?:string;
}

export interface DepositFlowStateModel {
  processControlRequest: DepositedFlowModel;
  processControlResponse: any;
  analyzeRequest: AnalyseFlowModel;
  analyzeResponse: AnalyseFlowResponse;
  treatmentRequest: TreatmentFlowModel;
  treatmentResponse: TreatmentFlowResponse;
  switchRequest: SwitchFlowModel;
  switchResponse: SwitchFlowResponseModel;
  loadComposedBase64Request: ComposedFileRequestModel;
  selectedChannel: string;
  defaultBase64: string;
  composedBase64: string;
  defaultConfiguration: DefaultConfiguration;
}
