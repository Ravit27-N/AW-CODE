import { createReducer, on } from '@ngrx/store';
import {AnalyseFlowResponse, AnalysisModel, ModifiedFlowDocumentAddress} from '../../model';
import * as actions from './flow-deposit.action';
import {validateBatchUserCSV} from "@cxm-smartflow/user/data-access";
import {data} from "autoprefixer";

export const flowDepositFeatureKey = 'FLOW_DEPOSIT_FEATURE_KEY';

const initialState = {
  processControlRequest: {},
  processControlResponse: {},
  defaultConfiguration: {},
  analyzeRequest: {},
  analyzeResponse: {},
  treatmentRequest: {},
  treatmentResponse: {},
  switchRequest: {},
  switchResponse: {},
  selectedChannel: '',
  composedFileId: '',
  defaultBase64: '',
  composedBase64: '',
  loadComposedBase64Request: {},
  okDocumentProcessed: {},
  isNavigateFromFlowTraceability: false,
  updateFlowStepLoading: false,
  analyzeContent: [] as AnalysisModel[],
  analyzePagingContent: [] as AnalysisModel[],
  document: {noOk: false, hasKo: false},
  uuid: '',
  summaryDocument: {},
  productionCriteriaOption: {},
  isModelNameChanged: false,
  isConfigChanged: false
};

const processOKDocument = (analyzeResponse: AnalyseFlowResponse) =>
  analyzeResponse?.data?.document?.DOCUMENT
  .filter(x => x.Analyse === 'OK')
  .reduce((prev: any, cur: any) => {
      return { nbDocument: prev.nbDocument + 1, nbPage: prev.nbPage + parseInt(cur.NbPages )};
    }, { nbDocument: 0, nbPage: 0 });

export const flowDepositFeatureReducer = createReducer(initialState,
  // process control step
  on(actions.launchProcessControl, (state, props) => ({
    ...state,
    processControlRequest: props.request,
    uuid: props.request?.uuid,
    isLoading: false
  })),
  on(actions.launchProcessControlSuccess, (state, props) => ({
    ...state,
    processControlResponse: props.response,
    isLoading: false,
    isDocumentIdentifiable: props.response.data.ModeleName?.trim()?.length > 0
  })),
  on(actions.launchProcessControlFail, (state) => ({ ...state, isLoading: false })),

  // analyse flow step
  on(actions.analyseFlow, (state, props) => ({...state, analyzeRequest: props.request, isLoading: true})),
  on(actions.analyseFlowSuccess, (state, props) => {
    const okDocument = processOKDocument(props.response);
    const content = mappingAnalyzeContent(props.response);
    const koDocs = filterKODocuments(content);
    return {...state,
      composedFileId: props?.response?.data?.composedFileId,
      analyzeResponse: props.response,
      isLoading: false,
      okDocumentProcessed: okDocument,
      productionForm: props?.productionForm,
      analyzeContent: content,
      analyzePagingContent: sortingAnalyzeContent(content),
      document: koDocs,
      summaryDocument: getSummaryDocument(content)
    };
  }),
  on(actions.analyseFlowFail, (state) => ({...state, isLoading: false})),

  // treatment flow step
  on(actions.treatmentFlow, (state, props) => ({ ...state, treatmentRequest: props.request, isLoading: true })),
  on(actions.treatmentFlowSuccess, (state, props) => {
    return {
      ...state,
      composedFileId: props.response?.data?.composedFileId,
      treatmentResponse: {
        ...props.response
      },
      productionForm: props?.productionForm,
      isLoading: false
    };
  }),
  on(actions.treatmentFlowFail, (state) => ({ ...state, isLoading: false })),

  // switch flow step
  on(actions.switchFlow, (state, props) => ({ ...state, switchRequest: props.request, isLoading: true })),
  on(actions.switchFlowSuccess, (state) => ({ ...state, isLoading: false })),
  on(actions.switchFlowFail, (state) => ({ ...state, isLoading: false })),
  on(actions.switchProcessFail, (state) => ({ ...state, isLoading: false })),

  // choosing channel
  on(actions.chooseChannel, (state, props) => ({ ...state, selectedChannel: props.channel, isLoading: false })),
  // store base64 file.
  on(actions.defaultBase64File, (state, props) => ({ ...state, defaultBase64: props.base64, isLoading: false })),
  on(actions.clearDepositFlow, (state) => ({ ...state, ...initialState, isLoading: false })),

  // load composed base64 file.
  on(actions.loadComposedBase64File, (state, props) => ({
    ...state,
    loadComposedBase64Request: props.request,
    isLoading: false
  })),
  on(actions.loadComposedBase64FileSuccess, (state, props) => ({
    ...state,
    composedBase64: props.base64,
    isLoading: false
  })),
  on(actions.loadComposedBase64FileFail, (state) => ({ ...state, isLoading: false })),

  on(actions.flowDepositInitialize, (state, props) => ({ ...state, ...props.data })),
  on(actions.initDefaultBase64PreAnalysis, (state, props) => ({ ...state, defaultBase64: props?.base64 })),
  on(actions.initProcessControlResponse, (state, props) => {

    const content = mappingAnalyzeContent(props?.data);

    return (
      {
        ...state,
        processControlResponse: props.data,
        summaryDocument: getSummaryDocument(content,true),
        isDocumentIdentifiable: props.data.data.ModeleName?.trim()?.length > 0
      });
  }),
  on(actions.initAnalyzeResponse, (state, props) => {
    const content = mappingAnalyzeContent(props?.data);
    const koDocs = filterKODocuments(content);
    return ({
      ...state,
      composedFileId: props?.data?.data?.composedFileId,
      analyzeResponse: props?.data,
      analyzeContent: content,
      analyzePagingContent: sortingAnalyzeContent(content),
      document: koDocs
    })
  }),
  on(actions.initProcessControlRequest, (state, props) => ({...state, processControlRequest: props?.data, uuid: props?.data?.uuid})),
  on(actions.initChooseChannel, (state, props) => ({...state, selectedChannel: props?.channel})),
  on(actions.initOkDocumentProcessed, (state, props) => ({...state, okDocumentProcessed: props?.value})),
  on(actions.initIsNavigateFromFlowTraceability, (state, props) => ({...state, isNavigateFromFlowTraceability: props?.value})),
  on(actions.initProductionForm, (state, props) => ({...state, productionForm: props?.productionForm})),
  on(actions.unloadFlowDepositForm, () => ({...initialState})),
  on(actions.loadReco, (state, props) => ({...state, recto: props.recto})),
  on(actions.initTreatmentResponse, (state, props) => ({
    ...state,
    composedFileId: props?.response?.composedFileId,
    treatmentResponse: {
      ...props.response
    }
  })),
  on(actions.validateIsCanIdentify, (state, props) => ({
    ...state,
    isCanIdentify: props.isCanIdentify
  })),
  on(actions.updateFlowDepositStep, (state) => ({...state, updateFlowStepLoading: true})),
  on(actions.sortedAnalyzeContent, (state, props) => {
    return {
      ...state,
      analyzePagingContent: sortingAnalyzeContent(state.analyzeContent, props.sortField, props.sortDirection, props.pageIndex, props.pageSize)
    };
  }),
  on(actions.loadFlowDetails, (state, props) => ({
    ...state,
    flowDetails: props.flowDetails
  })),
  on(actions.getLimitUploadFileSizeSuccess, (state, props) => ({
    ...state,
    limitUploadFileSize: props.fileSize
  })),
  on(actions.loadDepositParam, (state, props) => ({
    ...state,
    navigateParams: props.navigateParams
  })),
  on(actions.attemptProductCriteriaOption, (state, props) => ({
    ...state,
    productionCriteriaOption: props.productionCriteriaOption
  })),
  on(actions.isModelNameConfigurationChangedSuccess, (state, props) => ({
    ...state,
    isModelNameChanged: props.isModelNameChanged,
  })),
  on(actions.detectPortalConfigurationChanged, (state, props) => ({
    ...state,
    isConfigChanged: props.isConfigChanged,
  })),
  on(actions.initDefaultConfiguration, (state, props) => ({
    ...state,
    defaultConfiguration: props.defaultConfig
  }))
);

export function mappingAnalyzeContent(analyzeResponse: AnalyseFlowResponse) {
  const firstAddress = (addressObject: any) => {
    return  Object.values(addressObject).filter(line => !!line)[0] as string;
  }
  const data: AnalysisModel[] = [];
  const modifiedFlowDocumentAddress = analyzeResponse.data.modifiedFlowDocumentAddress || [];
  if(analyzeResponse?.data?.document?.DOCUMENT !== undefined) {
    analyzeResponse?.data?.document?.DOCUMENT?.forEach((item, index) => {
      let modify=false;
      let address= "";
      let resultAnalysis= "";
      for (const addressModify of modifiedFlowDocumentAddress) {
        if (item.DocUUID == addressModify.docId) {
          modify = addressModify.modified || false;
          address = addressModify.address || "";
          resultAnalysis = "ok";
        }
      }

      if (resultAnalysis !== "ok") {
        resultAnalysis = item?.Analyse || item?.Analysis || "";
      }

      data.push({
        document: ++index,
        resultAnalysis: resultAnalysis,
        numberOfPages: item.NbPages,
        reception: firstAddress(item.ADDRESS) || "",
        channel: item?.Filiere || item?.Channel,
        docUuid: item?.DocUUID,
        addresses: item?.ADDRESS,
        modify: modify,
        address: address,
        numberOfSet: item?.Offset
      })
    })
  }
  return data;
}

export function sortingAnalyzeContent(content: AnalysisModel[], sortField?: string, sortDirection?: string, pageIndex= 1, pageSize = 10): AnalysisModel[] {
  let data = [...content];
  if (!sortField || sortDirection === '') {
    return paging(data, pageIndex, pageSize) || [];
  }

  data = data.sort((a, b) => {
    const isAsc = sortDirection === 'asc';
    switch (sortField) {
      case 'document':
        return compare(a?.document || '', b?.document || '', isAsc);
      case 'reception':
        return compare(a?.reception || '', b?.reception || '', isAsc);
      case 'resultAnalysis':
        return compare(a?.resultAnalysis || '', b?.resultAnalysis || '', isAsc);
      case 'nbPage':
        return compare(a?.numberOfPages || 0, b?.numberOfPages || 0, isAsc);
      default:
        return 0;
    }
  });
  return paging(data, pageIndex, pageSize) || [];
}

function paging(content: AnalysisModel[], pageIndex: number, pageSize:number): AnalysisModel[] {
  const data = [...content];
  pageIndex--;
  const lowValue = pageSize * pageIndex;
  const highValue = lowValue + pageSize;
  return data.slice(lowValue, highValue);
}

function compare(a: number | string, b: number | string, isAsc: boolean) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}

function filterKODocuments(content: AnalysisModel[]): { noOk: boolean, hasKo: boolean } {
  const data = [...content];
  const koDocs = data.filter(item => item.resultAnalysis?.toLowerCase() === 'ko');
  if (koDocs.length !== 0) {
    if (koDocs.length === data.length) {
      return {noOk: true, hasKo: false}
    }
    return {noOk: false, hasKo: true}
  }
  return {noOk: false, hasKo: false}
}

function getSummaryDocument(documents: any, isFromFinalize?: boolean): { KO: number, OK: number } {
  let KO, OK;

  if (isFromFinalize) {
    OK = documents.filter((e: any) =>e?.resultAnalysis?.toUpperCase() === 'OK').length || 0;
    KO = documents.filter((e: any) =>e?.resultAnalysis?.toUpperCase() === 'KO').length || 0;
  } else {
    // Validate from following step.
    OK = documents.filter((e: any) =>e?.resultAnalysis?.toUpperCase() === 'OK').length || 0;
    KO = documents.filter((e: any) =>e?.resultAnalysis?.toUpperCase() === 'KO').length || 0;
  }

  return {KO,OK};
}
