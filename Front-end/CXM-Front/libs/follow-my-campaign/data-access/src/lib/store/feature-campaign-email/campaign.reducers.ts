import { createReducer, on } from '@ngrx/store';
import * as fromActions from './campaign.actions';
import {
  appRoute,
  CustomFileModel,
} from '@cxm-smartflow/shared/data-access/model';

export const EmailCampaignFeatureKey = 'feature-campaign-email';

const csvDataInit = {
  csvOK: false,
  fileName: '',
  filePath: '',
  originalName: '',
  fileUrl: '',
  isLoading: false,
  invalidCount: 0,
  count: 0
};

const uploadingStateInit: { mode: 'idle' | 'sending' | 'uploaded' | 'fail' | string, progression: number, errorName: string | null } = {
  mode: 'idle',
  errorName: null,
  progression: 0
};

const initialState = {
  checkSameMail: true,
  hasHeader: true,
  templateId: 0,
  csvAttempt: 0,
  csvData: csvDataInit,
  templateDetails: undefined,
  campaign: undefined,
  csvBlob: undefined,
  showRejectMail: false,
  csvFirstRecode: '',
  isLoading: false,
  isShowSpinner: false,
  emailRecord: [],
  csvFilter: { page: 1, pageSize: 10, total: 0 },
  mode: 0, // 0: create, 1: edit,
  csvError: {} /* { csvOK: false, matchLength: 0 } */,

  // Uploading csv
  // sending: false,
  // done: false,
  // progression: 0,
  // errorName: ''
  uploadingState: uploadingStateInit,
  attachments: [],
  metadataResponse: {},
  parameterFormTemporary: {}
};

export const campaignReducer = createReducer(
  initialState,
  // actions reducers,
  on(fromActions.loadDestinationForm, (state, props) => ({ ...state, templateId: parseInt(props.templateId) })),
  on(fromActions.checkCsvHeaderValueSuccess, (state, props) => {
    let { csvData } = state;
    csvData = { ...csvData, csvOK: props.ok, fileName: '', filePath: '' };
    return { ...state, csvData, csvAttempt: state.csvAttempt + 1 };
  }),
  on(fromActions.uploadCSVFileAction, (state) => ({
    ...state,
    csvData: {
      ...state?.csvData,
      isLoading: true
    },
    // progression: 0, sending: true, done: false, errorName: ''
    uploadingState: { mode: 'sending', progression: 0, errorName: null }
  })),
  on(fromActions.uploadCSVFileActionResponse, (state, props) => {
    let { csvData, uploadingState } = state;

    csvData = {
      ...csvData, fileName: props.res.fileProperties.fileName, filePath: props.res.fileProperties.filePath,
      originalName: props?.res?.fileProperties.originalName,
      fileUrl: props?.res?.fileProperties.fileUrl,
      isLoading: false, invalidCount: props.res.invalidCount, count: props.res.lineCount
    };

    uploadingState = {
      errorName: null, mode: 'uploaded', progression: 100
    };

    return { ...state, csvData, uploadingState, isShowSpinner: true };
  }),
  on(fromActions.uploadCSVProgresssion, (state, props) => ({
    ...state,
    uploadingState: { mode: 'sending', progression: props.progress, errorName: null }
  })),

  on(fromActions.uploadCSVFileActionFail, (state, props) => ({
    ...state,
    uploadingState: { mode: 'fail', progression: 100, errorName: 'some_error_key' },
    isShowSpinner: false
  })),

  on(fromActions.csvFormValueChange, (state, props) => {
    return { ...state, checkSameMail: props.form.checkSameMail, hasHeader: props.form.hasHeader, isCheckHeaderChange: true };
  }),
  on(fromActions.loadTemplateDetailSuccess, (state, props) => ({ ...state, templateDetails: props.templateDetails })),
  on(fromActions.loadEmailCampaignDetailSuccess, (state: any, props: any) => {

    const { csvHasHeader, csvName, csvOriginalName, csvPath, csvRecordCount, removeDuplicate, unsubscribeLink} = props.campaign.details;
    const csvData = { fileUrl: csvPath, fileName: csvName, originalName: csvOriginalName, count: csvRecordCount };

    return {
      ...state,
      campaign: props.campaign, csvData, hasHeader: csvHasHeader, checkSameMail: removeDuplicate, unsubscribeLink, mode: 1,
      csvError: { csvOK: true, matchLength: 0 },
      uploadingState: { errorName: '', progression: 0, mode: 'noloading' },
      attachments: props?.campaign?.attachments
    };
  }),
  on(fromActions.submitDestinationStep, (state) => ({
    ...state,
    isLoading: true
  })),
  on(fromActions.submitDestinationSuccess, (state, props) => ({
    ...state,
    campaign: props.campaign,
    showRejectMail: false,
    isLoading: false,
    mode: 1
  })),
  on(fromActions.createEmailCampaignAfterUploadedFile, (state, props) => ({...state, isShowSpinner: true })),
  on(fromActions.createEmailCampaignAfterUploadedFileSuccess, (state, props) => ({ ...state, campaign: props.campaignResponse, showRejectMail: false, isLoading: false, mode: 1})),
  on(fromActions.submitDestinationFail, (state, props) => ({ ...state, isLoading: false, isShowSpinner: false })),
  on(fromActions.createEmailCampaignAfterUploadedFileFail, (state, props) => ({ ...state, isLoading: false })),

  on(fromActions.submitEmailCampaignParameterStep, (state) => ({ ...state, isLoading: true })),
  on(fromActions.submitEmailCampaignParameterSuccess, (state, props) => ({
    ...state,
    campaign: props?.emailCampaign,
    isLoading: false
  })),
  on(fromActions.submitEmailCampaignParameterFail, (state) => ({ ...state, isLoading: false })),

  on(fromActions.fetchCampaignCsvResponse, (state, props) => ({ ...state, csvBlob: props.csv, showRejectMail: true })),
  on(fromActions.unloadEmailCampaignFormData, () => ({
    ...initialState
  })),
  on(fromActions.keepCsvFirstRecord, (state, props) => ({ ...state, csvFirstRecode: props?.csvFirstRecode })),
  on(fromActions.checkCsvHeaderAfterUploadSuccess, (state, props) => ({ ...state, test: props.value })),

  on(fromActions.submitEmailCampaignSummaryStep, (state) => ({ ...state, isLoading: true })),
  on(fromActions.submitEmailCampaignSummaryStepSuccess, (state) => ({ ...state, isLoading: false })),
  on(fromActions.submitEmailCampaignSummaryStepFail, (state) => ({ ...state, isLoading: false })),

  on(fromActions.sendMailTest, (state) => ({ ...state, isLoading: true })),
  on(fromActions.sendMailTestSuccess, (state) => ({ ...state, isLoading: false })),
  on(fromActions.sendMailTestFail, (state) => ({ ...state, isLoading: false })),


  on(fromActions.recordEmailDataFromCsv, (state, props) => {
    const validRecords = (props.data.filter((e: any) => JSON.parse(e.valid))?.length > 0);

    return {
      ...state,
      emailRecord: props.data,
      csvFilter: props.filter,
      step2Lockable: validRecords,
    }
  }),

  on(fromActions.emailCheckCsvHeaderValueResult, (state, { csvOK, matchLength }) => {
    if (csvOK === false) return {
      ...state,
      csvError: { csvOK, matchLength },
      uploadingState: { errorName: 'csv_error_header', mode: 'fail', progression: 100 }
    };

    return { ...state, csvError: { csvOK, matchLength } };
  }),

  on(fromActions.nonceAction, (state) => ({ ...state, isLoading: false })),
  on(fromActions.resetCampaignCsv, (state) => ({
    ...state,
    uploadingState: { mode: 'idle', errorName: null, progression: 0 },
    csvData: csvDataInit,
    csvError: {}
  })),
  on(fromActions.setUploadingBar, (state, { progression, mode, errorName }) => ({
    ...state,
    uploadingState: { errorName, mode, progression }
  })),
  on(fromActions.getMaxFileSizeUploadSuccess, (state, props) => ({ ...state, limitSize: props.limitSize })),

  // attachments of email campaign parameter form.
  on(fromActions.uploadAttachmentSuccess, (state, props) => ({
    ...state,
    attachments: concatAttachments(props.attachmentResponse, props.oldAttachments)
  })),
  on(fromActions.removeAttachmentSuccess, (state, props) => ({
    ...state,
    attachments: removeAttachmentOnState(props.fileIds, props.attachments)
  })),
  on(fromActions.clearAttachmentInStore, (state) => ({
    ...state,
    attachments: []
  })),
  on(fromActions.initParameterFormTemporary, (state, props) => ({
    ...state,
    parameterFormTemporary: {
      ...props.parameter,
      attachments: props?.attachments
    }
  })),
  on(fromActions.unloadParameterForm, (state) => ({
    ...state,
    parameterFormTemporary: {}
  })),
  on(fromActions.nonceAction, (state) => ({...state, isLoading: false})),
  on(fromActions.resetCampaignCsv, (state ) => ({ ...state, uploadingState: { mode: 'idle', errorName: null, progression: 0 }, csvData: csvDataInit, csvError: { } })),
  on(fromActions.setUploadingBar, (state, { progression, mode, errorName }) => ({ ...state, uploadingState: { errorName, mode, progression } }) ),
  on(fromActions.getMaxFileSizeUploadSuccess, (state, props) => ({ ...state, limitSize: props.limitSize})),
  on(fromActions.setIsHeaderChange, (state, { isCheckHeaderChange }) => ({...state, isCheckHeaderChange })),
  on(fromActions.closeEmailLoading, (state) => ({...state, isShowSpinner: false})),
  on(fromActions.emailFilterCsvFilterChanged, (state) => {
    const isStep2 = location.pathname.includes(appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination);
    return {...state, isShowSpinner: isStep2}
  }),
  on(fromActions.getEmailMetadataSuccess, (state, props) => {
    return { ...state, metadataResponse: props.metadataResponse };
  })
);

export const concatAttachments = (attachmentResponse: CustomFileModel[], oldAttachments: CustomFileModel[]): never[] => {
  return [...oldAttachments, ...attachmentResponse] as never [];
};

export const removeAttachmentOnState = (fileIds: string[], attachments: CustomFileModel[]): never [] => {
  const attachmentTemp = [...attachments];
  fileIds.forEach((fileId: string) => {
    const index = attachments.findIndex(attachment => attachment.fileId === fileId);
    attachmentTemp.splice(index, 1);
  });
  return attachmentTemp as never[];
};
