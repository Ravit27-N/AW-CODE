import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  AnalyseFlowResponse, AnalysisModel,
  DepositFlowStateModel,
  SwitchFlowModel,
  SwitchFlowResponseModel,
  TreatmentFlowModel
} from '../../model';
import { flowDepositFeatureKey } from "./flow-deposit.reducer";

const flowDepositFeature = createFeatureSelector(flowDepositFeatureKey);

export const selectFlowDepositState = createSelector(flowDepositFeature, (state) => state as DepositFlowStateModel);

export const selectProcessControlResponseState = createSelector( flowDepositFeature,(state: any) => state?.processControlResponse);

export const selectProcessControlRequestState = createSelector( flowDepositFeature, (state: any) => state?.processControlRequest);

export const selectAnalyzeResponseState = createSelector(flowDepositFeature,(state: any) => state?.analyzeResponse as AnalyseFlowResponse);

export const selectProductionAnalyzeResponse = createSelector(flowDepositFeature, (state: any) => state?.analyzeResponse?.data?.document?.DOCUMENT?.[0]?.PRODUCTION);

export const selectAnalyzeRequestState = createSelector(flowDepositFeature,(state: any) => state?.analyzeRequest);

export const selectTreatmentResponseState = createSelector(flowDepositFeature,(state: any) => state?.treatmentResponse);

export const selectTreatmentRequestState = createSelector(flowDepositFeature,(state: any) => state?.treatmentRequest as TreatmentFlowModel);

export const selectSwitchRequestState = createSelector(flowDepositFeature,(state: any) => state?.switchRequest as SwitchFlowModel);

export const selectSwitchResponseState = createSelector(flowDepositFeature,(state: any) => state?.switchResponse as SwitchFlowResponseModel);

export const selectChooseChannelState = createSelector(flowDepositFeature, (state: any): string => state?.selectedChannel);

export const selectDefaultBase64FileState = createSelector(flowDepositFeature, (state: any): string => state?.defaultBase64);

export const selectComposedBase64FileState = createSelector(flowDepositFeature, (state: any): string => state?.composedBase64);

export const selectUuid = createSelector(flowDepositFeature, (state: any) => state?.treatmentRequest?.uuid);

export const selectUuidProcessControlRequest = createSelector(flowDepositFeature, (state: any) => state?.processControlRequest?.uuid);

export const selectTreatmentResponse = createSelector(flowDepositFeature, (state: any) => state?.treatmentResponse);

export const selectDocumentProcessingTreatmentResponse = createSelector(flowDepositFeature, (state: any) => state?.treatmentResponse?.documentProcessing);

export const selectDataTreatmentResponse = createSelector(flowDepositFeature, (state: any) => state?.treatmentResponse?.data);

export const selectOKDocumentProcessed = createSelector(flowDepositFeature, (state: any) => state.okDocumentProcessed);

export const selectDocNameAnalzeReponse = createSelector(flowDepositFeature, (state: any) => state?.analyzeResponse?.DOCUMENT?.[0]?.PROCESSING?.DocName);

export const selectIsNavigateFromFlowTraceability = createSelector(flowDepositFeature, (state: any) => state?.isNavigateFromFlowTraceability);

export const selectDepositLoading = createSelector(flowDepositFeature, (state: any) => state.isLoading);

export const selectRecto = createSelector(flowDepositFeature, (state: any) => state?.recto);

export const selectProductionForm = createSelector(flowDepositFeature, (state: any) => state?.productionForm);

export const selectIsCanIdentify = createSelector(flowDepositFeature, (state: any) => state?.isCanIdentify);

export const selectUpdateFlowStepLoading = createSelector(flowDepositFeature, (state: any) => state?.updateFlowStepLoading);
export const selectAnalyzePagingContent = createSelector(flowDepositFeature, (state: any) => state?.analyzePagingContent as AnalysisModel[]);

export const selectAnalyzeContent = createSelector(flowDepositFeature, (state: any) => state?.analyzeContent as AnalysisModel[]);

export const selectFlowDetails = createSelector(flowDepositFeature, (state: any) => state?.flowDetails);

export const selectDocumentIsKO = createSelector(flowDepositFeature, (state: any) => {
  return ({noOk: state?.document?.noOk, hasKo: state?.document?.hasKo})
});

export const selectUUID = createSelector(flowDepositFeature, (state: any) => state?.uuid);

export const selectLimitUploadFileSize = createSelector(flowDepositFeature, (state: any) => state?.limitUploadFileSize);

export const selectSummaryDocument = createSelector(flowDepositFeature, (state: any) => state?.summaryDocument);

// select the composed file id from the state
export const selectComposedFileIdState = createSelector(flowDepositFeature, (state: any) => state?.composedFileId);

export const depositUrlParams = createSelector(flowDepositFeature, (state: any) => ({
  ...state?.navigateParams,
  composedFileId: state?.composedFileId,
  ...state?.productionCriteriaOption,
}));

export const selectDocumentIdentifiable = createSelector(flowDepositFeature, (state: any) => state?.isDocumentIdentifiable);

export const selectDocumentNoOK = createSelector(flowDepositFeature, (state: any) => state?.document?.noOk as boolean);

export const selectFlowBackground = createSelector(flowDepositFeature, (state: any) => state?.treatmentResponse?.backgroundPage);

export const selectFlowAttachment = createSelector(flowDepositFeature, (state: any) => state?.treatmentResponse?.attachments);

export const selectFlowSignature = createSelector(flowDepositFeature, (state: any) => state?.treatmentResponse.signature);

export const selectIsModelNameChanged = createSelector(flowDepositFeature, (state: any) => state?.isModelNameChanged);

export const selectIsConfigChanged = createSelector(flowDepositFeature, (state: any) => state?.isConfigChanged);
export const selectFlowWatermark = createSelector(flowDepositFeature, (state: any) => state?.treatmentResponse.watermark);

export const selectDefaultConfiguration = createSelector(flowDepositFeature, (state: any) => state?.defaultConfiguration || {});
