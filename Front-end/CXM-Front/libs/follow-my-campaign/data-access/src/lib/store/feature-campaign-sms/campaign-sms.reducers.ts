import { createReducer, on } from '@ngrx/store';
import * as fromAction from './campaign-sms.actions';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';


const uploadingStateInit: { mode: 'idle'|'sending'| 'uploaded'| 'noloading' | 'fail'|string, progression: number, errorName: string|null  } = {
  mode: 'idle',
  errorName: null,
  progression: 0
}

export const campaigSmsFeatureKey = 'campaign-sms-feature';

const campaignSmsInitState = {
  mode: 0, // 0 create, 1 edit
  loading: false,
  batDialog: false,
  // templateId: undefined,
  // campaignId: undefined,
  // template: undefined,
  // campaign: undefined,

  csvData: {
    extension: '', fileId: '', fileName: '', fileSize: 0, fileUrl: '', message: '', originalName: ''
  },
  csvError: {} /* { csvOK: false, matchLength: 0 } */,
  hasHeader: true,
  checkSameNumber: true,
  smsRecord: [],
  navigation: { prev: true, next: false },
  csvFilter: { page: 1, pageSize: 10, total: 0 },
  step: 1,
  lockableForm: false,
  smsFirstRecord: {},
  campaignResponse: {},
  isSMSloading: false,

  // uploading csv
  // sending: false,
  // done: false,
  // progression: 0
  uploadingState: uploadingStateInit,
  metadataResponse: {},
};

export const campaignSmsReducer = createReducer(campaignSmsInitState,
  on(fromAction.loadCampaignSms, (state, props) => {
    const { campaignId, templateId } = props;
    const { campaignId: updateCampaignId } = state as any;
    const mode = (campaignId !== undefined || updateCampaignId !== undefined) ? 1 : 0;

    if(mode == 1) return { ...state, loading: true, mode, csvError: { csvOK: true }, uploadingState: { errorName: '', mode:  'noloading', progression: 0 }};

    return { ...state, loading: true, mode};
  }),
  on(fromAction.loadCampaignSmsSuccess, (state) => ({ ...state, loading: false })),
  on(fromAction.unloadCampaignSms, () => ({ ...campaignSmsInitState })),


  on(fromAction.loadCampaignSmsTemplate, (state) => ({ ...state, loading: true })),
  on(fromAction.loadCampaignSmsTemplateSuccess, (state, props) => ({
    ...state,
    loading: false,
    template: props.template
  })),
  on(fromAction.loadCampaignSmsTemplateFail, (state) => ({ ...state, loading: false })),

  on(fromAction.loadCampaignSmsDetail, (state) => ({ ...state, loading: true })),
  on(fromAction.loadCampaignSmsDetailSuccess, (state: any, props: any) => {
    const { campaign } = props;
    const {
      csvHasHeader,
      csvName,
      csvOriginalName,
      csvPath,
      csvRecordCount,
      removeDuplicate
    } = props?.campaign?.details;
    const csvData = { fileUrl: csvPath, fileName: csvName, originalName: csvOriginalName, count: csvRecordCount };

    return {
      ...state,
      loading: false,
      campaign,
      hasHeader: csvHasHeader,
      checkSameNumber: removeDuplicate,
      csvData,
      campaignId: campaign?.id,
      templateId: campaign?.templateId,
      mode: 1
    };
  }),
  on(fromAction.loadCampaignSmsDetailFail, (state) => ({ ...state, loading: false })),


  on(fromAction.recordSmsDataFromCsv, (state, props) => {
    const record = (state.smsRecord || []).filter((e: any) => JSON.parse(e.valid));
    const data = (props.data || []).filter((e: any) => JSON.parse(e.valid));
    const step2Lockable = (record.length > 0 || data.length > 0);
    return {
      ...state,
      smsRecord: props.data,
      step2Lockable,
      csvFilter: { page: props.filter.page, pageSize: props.filter.pageSize, total: props.filter.total }
    };
  }),
  on(fromAction.uploadSmsCsvFile, (state, props) => ({ ...state, loading: true, uploadingState: { progression: 0, mode: 'sending', errorName: null } })),
  on(fromAction.uploadSmsCsvFileSuccess, (state, props) => {

    const csvdata = { ...props.res.fileProperties, invalidCount: props.res.invalidCount, count: props.res.lineCount };

    let { navigation } = state;
    navigation = { ...navigation, next: csvdata.count > 0 && csvdata.count != csvdata.invalidCount };

    return { ...state, csvData: csvdata, loading: false, uploadingState: { progression: 100, mode: 'uploaded', errorName: null }, navigation, isSMSloading: true };
  }),
  on(fromAction.uploadSmsCsvProgression, (state, props) => ({ ...state, uploadingState: { progression: props.progress, mode: 'sending', errorName: 'some_error_key' } })),
  on(fromAction.uploadSmsCsvFileFail, (state, props) => ({ ...state, uploadingState: { progression: 100, mode: 'fail', errorName: null, isSMSloading: false }  })),

  on(fromAction.smsFormChanged, (state, { checkSameNumber, hasHeader }) => ({ ...state, checkSameNumber, hasHeader })),
  on(fromAction.smsParameterFormChanged, (state, { campaignName, senderName }) => {
    let { campaign } = state as any;
    campaign = { ...campaign, senderName, campaignName };
    const navigation = (!!campaignName && !!senderName) ? { prev: true, next: true } : { prev: true, next: false };
    return { ...state, campaign, navigation };
  }),
  on(fromAction.smsSendingFormChanged, (state, { sendingTime }) => {
    let { campaign } = state as any;
    campaign = { ...campaign, sendingSchedule: sendingTime };
    return { ...state, campaign };
  }),

  on(fromAction.smsCheckCsvHeaderValueResult, (state, { csvOK, matchLength }) => {
    const { campaign, step, uploadingState } = state as any;
    let navigation = csvOK === true ? { prev: true, next: true } : { prev: true, next: false };
    navigation = { ...navigation, prev: !(campaign && step === 2) };

    // const smsuploading = csvOK === false ? { sending: false, progression: 0, done: false  } : {};
    const us = csvOK === false ?  { ...uploadingState, errorName: 'csv_error_header', mode: 'fail', progression: 100   } : {...uploadingState};

    return { ...state, csvError: { csvOK, matchLength }, navigation, uploadingState: us };
  }),


  on(fromAction.smsSubmitDestination, (state) => ({ ...state, loading: true })),
  on(fromAction.smsSubmitDestinationFail, (state) => ({ ...state, loading: false })),

  on(fromAction.smsSubmitDestinationSuccess, (state, { campaign }) => {
    return { ...state, loading: false, campaign, campaignId: campaign.id, mode: 1 };
  }),

  on(fromAction.smsSubmitParameter, (state) => ({ ...state, loading: true })),
  on(fromAction.smsSubmitParameterSuccess, (state) => ({ ...state, loading: false })),
  on(fromAction.smsSubmitParameterFail, (state) => ({ ...state, loading: false })),

  on(fromAction.smsSubmitEnvoy, (state) => ({ ...state, loading: true })),
  on(fromAction.smsSubmitEnvoySuccess, (state) => ({ ...state, loading: false })),
  on(fromAction.smsSubmitEnvoyFail, (state) => ({ ...state, loading: false })),

  on(fromAction.smsValidateStep, (state, { step }) => {
    if (step === 3) {
      const { campaign } = state as any;
      const navigation = (!!campaign?.campaignName && !!campaign?.senderName) ? {
        prev: true,
        next: true
      } : { prev: true, next: false };
      return { ...state, navigation };
    } else if (step === 2) {
      const { csvError, campaign, mode, smsRecord } = state as any;
      let navigation = (csvError && csvError?.csvOK) ? { prev: true, next: true } : { prev: true, next: false };
      navigation = { ...navigation, prev: !campaign };
      const step2Lockable = (smsRecord.filter((e: any) => JSON.parse(e.valid)).length > 0);
      return { ...state, navigation, step2Lockable };
    } else if (step === 4) {
      const { campaign } = state as any;
      const navigation = (campaign && campaign.sendingSchedule) ? { prev: true, next: true } : {
        prev: true,
        next: false
      };
      return { ...state, navigation };
    }
    return { ...state };
  }),

  on(fromAction.smsTestSendBat, (state, props) => ({ ...state, batDialog: props.show })),
  on(fromAction.smsSubmitTestSendBat, (state) => ({ ...state, loading: true })),
  on(fromAction.smsSubmitTestSendBatSuccess, (state, props) => ({ ...state, loading: false, batDialog: false })),
  on(fromAction.smsSubmitTestSendBatFail, (state, props) => ({ ...state, loading: false, batDialog: false })),

  on(fromAction.smsInitStep, (state, props) => ({ ...state, step: props.step })),
  on(fromAction.initLockableSmsForm, (state, props) => ({ ...state, lockableForm: props.isLock })),

  on(fromAction.resetSmsCsv, (state) =>  ({ ...state, uploadingState: { mode: 'idle', errorName: null, progression: 0 }, csvData: { extension: '', fileId: '', fileName: '', fileSize: 0, fileUrl: '', message: '', originalName: '' }, csvError: { } })),
  on(fromAction.setSmsUploadingBar, (state, { progression, mode }) => ({ ...state, uploadingState: { errorName: null, mode, progression } }) ),

  on(fromAction.loadCampaignSmsDetailFail, (state) => ({
    ...state,
    lockableForm: false
  })),
  on(fromAction.loadCampaignSmsTemplateFail, (state) => ({
    ...state,
    lockableForm: false
  })),
  on(fromAction.smsFilterCsvFilterChanged, (state) => {
    const isStep2 = location.pathname.includes(appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination);

    return {
      ...state,
      isSMSloading: isStep2,
    }
  }),
  on(fromAction.closeLoading, (state) => ({
    ...state,
    isSMSloading: false,
  })),
  on(fromAction.clearRecord, (state) => ({
    ...state,
    smsRecord: []
  })),
  on(fromAction.getSmsMetadataSuccess, (state, props) => {
    return { ...state, metadataResponse: props.metadataResponse };
  })
)
