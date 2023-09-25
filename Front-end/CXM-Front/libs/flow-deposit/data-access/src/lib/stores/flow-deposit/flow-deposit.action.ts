import { createAction, props } from '@ngrx/store';
import {
  AnalyseFlowModel,
  AnalyseFlowResponse,
  ComposedFileRequestModel, DefaultConfiguration,
  DepositedFlowModel, NavigateParam,
  ProductionCriteriaOptionModel,
  SwitchFlowModel,
  TreatmentFlowModel,
  TreatmentFlowResponse
} from '../../model';
import { HttpErrorResponse } from '@angular/common/http';

export enum FlowDepositType {
  launchProcessControl = '[FLOW DEPOSIT/ launch process control]',
  launchProcessControlSuccess = '[FLOW DEPOSIT/ launch process control success]',
  launchProcessControlFail = '[FLOW DEPOSIT/ launch process control fail]',
  analyseFlow = '[FLOW DEPOSIT/ analyse flow]',
  analyseFlowSuccess = '[FLOW DEPOSIT/ analyse flow success]',
  analyseFlowFail = '[FLOW DEPOSIT/ analyse flow fail]',
  chooseChannel = '[FLOW DEPOSIT/ choose channel]',
  defaultBase64File = '[FLOW DEPOSIT/ default base64 file]',
  treatmentFlow = '[FLOW DEPOSIT/ treatment flow]',
  treatmentFlowSuccess = '[FLOW DEPOSIT/ treatment flow success]',
  treatmentFlowFail = '[FLOW DEPOSIT/ treatment flow fail]',
  switchFlow = '[FLOW DEPOSIT/ switch flow]',
  switchFlowSuccess = '[FLOW DEPOSIT/ switch flow success]',
  switchFlowFail = '[FLOW DEPOSIT/ switch flow fail]',
  loadComposedBase64File = '[FLOW DEPOSIT/ load composed base64 file]',
  loadComposedBase64FileSuccess = '[FLOW DEPOSIT/ load composed base64 file success]',
  loadComposedBase64FileFail = '[FLOW DEPOSIT/ load composed base64 file fail]',
  clearDepositFlow = '[FLOW DEPOSIT/ clear deposit flow state]',
}

export const clearDepositFlow = createAction(FlowDepositType.clearDepositFlow);

export const launchProcessControl = createAction(
  FlowDepositType.launchProcessControl,
  props<{
    request: DepositedFlowModel,
    funcKey: string,
    privKey: string
  }>()
);

export const launchProcessControlSuccess = createAction(FlowDepositType.launchProcessControlSuccess,
  props<{
    response: any,
    portalResponse: any
  }>());

export const launchProcessControlFail = createAction(FlowDepositType.launchProcessControlFail);

export const analyseFlow = createAction(
  FlowDepositType.analyseFlow,
  props<{
    request: AnalyseFlowModel
  }>()
);

export const analyseFlowSuccess = createAction(FlowDepositType.analyseFlowSuccess,
  props<{
    response: AnalyseFlowResponse,
    productionForm: any
  }>());

export const analyseFlowFail = createAction(FlowDepositType.analyseFlowFail);

export const chooseChannel = createAction(FlowDepositType.chooseChannel, props<{channel: string}>())

export const defaultBase64File = createAction(FlowDepositType.defaultBase64File, props<{base64: string}>());

export const treatmentFlow = createAction(FlowDepositType.treatmentFlow, props<{request: TreatmentFlowModel}>());

export const treatmentFlowSuccess = createAction(FlowDepositType.treatmentFlowSuccess,props<{response: TreatmentFlowResponse, productionForm: any}>());

export const treatmentFlowFail = createAction(FlowDepositType.treatmentFlowFail, props<{ httpError: any } >());

export const completeFlowWithUnloadConfig = createAction('[FLOW DEPOSIT / process attem without config]');
export const distributionChannelDoesNotConfig = createAction('[FLOW DEPOSIT / distribution channel does not config.]');
export const completeFlowWithMissingResource = createAction('[FLOW DEPOSIT / process attem with missing resources]')

export const proccessSwithFlow = createAction('[FLOW DEPOSIT / process attemp to switch flow]', props<{ request: SwitchFlowModel }>());

export const switchFlow = createAction(FlowDepositType.switchFlow, props<{request: SwitchFlowModel}>());

export const switchFlowSuccess = createAction(FlowDepositType.switchFlowSuccess);

export const switchFlowFail = createAction(FlowDepositType.switchFlowFail);

export const loadComposedBase64File = createAction(FlowDepositType.loadComposedBase64File, props<{request: ComposedFileRequestModel, funcKey: string, privKey: string}>());

export const loadComposedBase64FileSuccess = createAction(FlowDepositType.loadComposedBase64FileSuccess,props<{base64: string}>());

export const loadComposedBase64FileFail = createAction(FlowDepositType.loadComposedBase64FileFail);

export const documentAnalyseResultMessage = createAction('[FLOW DEPOSIT / document analyse result msg]');

export const updateFlowDepositStep = createAction('[Flow deposit] / update flow document step', props<{uuid: string, step: number, composedFileId: string, validation: boolean}>());


export const unloadFlowDepositForm = createAction('[Flow deposit / upload flow deposit]');

export const flowDepositInitialize = createAction('[Flow deposit] load initialize data', props<{data: any}>());

export const initDefaultBase64PreAnalysis = createAction('[Flow deposit] / load init default base64 (PreAnalysis)', props<{base64: any}>());

export const initProcessControlResponse = createAction('[Flow deposit] / load init process control response object', props<{data: any}>());

export const initAnalyzeResponse = createAction('[Flow deposit] / load init analyze response object', props<{data: any}>());

export const initProcessControlRequest = createAction('[Flow deposit] / load init processControlRequest', props<{data: any}>());

export const initChooseChannel = createAction('[FloW deposit] / load init choose channel', props<{channel: any}>());

export const initOkDocumentProcessed = createAction('[Flow deposit] / load init okDocumentProcessed', props<{value: any}>());

export const initIsNavigateFromFlowTraceability = createAction('[Flow deposit / load init isNavigateFromFlowTraceabililty object]', props<{value: boolean}>())

export const initProductionForm = createAction('[Flow deposit / init production from]', props<{productionForm: any}>());

export const initTreatmentResponse = createAction('[Flow deposit / init treatment reponse]', props<{response: any}>());

export const loadReco = createAction('[Flow deposit / load reco]', props<{ recto: string }>());

export const updateToFinalizeStatus = createAction('[Flow deposit / update to finalize status]', props<{uuid: string, step: number, composedFileId?: string, validation: boolean}>());

export const validateIsCanIdentify = createAction('[Flow deposit / validate is can identify]', props<{isCanIdentify: boolean}>());

export const sortedAnalyzeContent = createAction('[Flow deposit / sorted analyze content]', props<{sortField?: string, sortDirection?: string, pageIndex?: number, pageSize?: number}>());

export const loadFlowDetails = createAction('[Flow deposit / load flow details]', props<{ flowDetails: any }>());

// Cancel flow.
export const cancelFlowDeposit = createAction('[Flow deposit] / cancel deposit flow', props<{ uuid: string, ownerId: number}>());
export const cancelFlowDepositSuccess = createAction('[Flow deposit] / cancel deposit flow success');
export const cancelFlowDepositFails = createAction('[Flow deposit] / cancel deposit flow fails', props<{ httpErrorResponse: HttpErrorResponse }>());

// Get file upload limit size.
export const getLimitUploadFileSize = createAction('[Flow deposit] / get limit upload file size');
export const getLimitUploadFileSizeSuccess = createAction('[Flow deposit] / get limit upload file size success', props<{ fileSize: string }>());
export const getLimitUploadFileSizeFails = createAction('[Flow deposit] / get limit upload file size fails' , props<{ httpErrorResponse: HttpErrorResponse }>());

// Load all URL query params.
export const loadDepositParam = createAction('[Flow deposit] / load deposit param', props<{ navigateParams: NavigateParam }>());
export const attemptProductCriteriaOption = createAction('[Flow deposit / Attempt production criteria option]', props<{ productionCriteriaOption: ProductionCriteriaOptionModel }>());

// Update status to finalize.
export const updateStatusToFinalize = createAction('[Flow deposit] / Update status to finalize', props<{ fileId: string, step: number, validation: boolean, composedFileId?: string }>());
export const updateStatusToFinalizeSuccess = createAction('[Flow deposit] / Update status to finalize success');

export const switchProcessFail = createAction('[Flow deposit / processed switch flow fail ]', props<{ httpError: any }>())
export const NonceAction = createAction('[Flow deposit / nonce actions ]')
export const missingSignatureConfiguration = createAction('[FLOW DEPOSIT / missing signature configuration]');

export const isModelNameConfigurationChanged = createAction('[FLOW DEPOSIT / validate model name configuration changed]', props<{ modelName: string }>());
export const isModelNameConfigurationChangedSuccess = createAction('[FLOW DEPOSIT / validate model name configuration changed success]', props<{ isModelNameChanged: boolean }>());

export const detectPortalConfigurationChanged = createAction('[FLOW DEPOSIT / detect portal configuration changed]', props<{ isConfigChanged: boolean }>());

export const initDefaultConfiguration = createAction('[Flow DEPOSIT / initialized default configuration]', props<{defaultConfig: DefaultConfiguration}>());
