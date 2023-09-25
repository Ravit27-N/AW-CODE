import { createAction, props } from "@ngrx/store";
import { CampaignModel, MetadataResponseModel } from '../../models';
import { HttpErrorResponse } from '@angular/common/http';


export const loadCampaignSms = createAction('[campaign sms / load campaign detail]', props<{ templateId: any, campaignId: any }>());

export const loadCampaignSmsSuccess = createAction('[campaign sms / load campaign detail success]');

export const unloadCampaignSms = createAction('[campaign sms / unload sms form data]');


export const loadCampaignSmsDetail = createAction('[campaign sms / load campaign sms detail]', props<{ campaignId: any }>());
export const loadCampaignSmsDetailSuccess = createAction('[campaign sms / load detail campaign sms detail success]', props<{ campaign: CampaignModel }>());
export const loadCampaignSmsDetailFail = createAction('[campaign sms / load campaign sms detail fail]', props<{ httpError: any }>());


export const loadCampaignSmsTemplate = createAction('[campaign sms / load sms template]', props<{ templateId: any }>());
export const loadCampaignSmsTemplateSuccess = createAction('[campaign sms / load sms template success]', props<{ template: any }>());
export const loadCampaignSmsTemplateFail = createAction('[campaign sms / load sms template fail]', props<{ httpError: any }>());

export const recordSmsDataFromCsv = createAction('[campaign sms / csv record data]', props<{ data: any, filter: { page: number, pageSize: number, total: number } }>());
export const uploadSmsCsvFile = createAction('[campaign sms /upload sms csv]', props<{ file: File }>());
export const uploadSmsCsvFileSuccess = createAction('[campaign sms /upload sms csv success]', props<{ res: any }>());
export const uploadSmsCsvFileFail = createAction('[campaign sms /upload sms csv fail]', props<{ error: any }>());
export const uploadSmsCsvFileNonce = createAction('[campaign sms /upload sms csv nonce]');
export const uploadSmsCsvProgression = createAction('[campaign sms /upload sms csv progression]', props<{ progress: number }>());

export const prepareUploadCsv = createAction('[campaign sms / prepare csv upload]', props<{ file: File, firstrow: any, matchLength: number }>());


export const smsFormChanged = createAction('[campaign sms / form destination value changed]', props<{ hasHeader: boolean, checkSameNumber: boolean  }>());
export const smsParameterFormChanged = createAction('[campaign sms / form parameter value changed]', props<{ campaignName: string, senderName: string }>());
export const smsSendingFormChanged = createAction('[campaign sms / form sendingtime value changed]', props<{ sendingTime: any }>());

export const smsCheckCsvHeaderValue = createAction('[campaign sms / check csv header value]', props<{ header: string[] }>());
export const smsCheckCsvHeaderValueResult = createAction('[campaign sms / check csv header value result]', props<{ csvOK: boolean, matchLength: number, file?: File }>());

export const smsSubmitDestination = createAction('[campaign sms / submit destination]', props<{ isUpdate: boolean }>());
export const smsSubmitDestinationSuccess = createAction('[campaign sms / submit destination success]', props<{ campaign: CampaignModel, isUpdate: boolean }>());
export const smsSubmitDestinationFail = createAction('[campaign sms / submit destination fail]');


export const smsSubmitParameter = createAction('[campaign sms / submit parameter]');
export const smsSubmitParameterSuccess = createAction('[campaign sms / submit parameter success]', props<{ campaign: CampaignModel }>());
export const smsSubmitParameterFail = createAction('[campaing sms / submit parameter fail]');

export const smsSubmitEnvoy = createAction('[campaign sms / submit envoy]', props<{sendingSchedule: string}>());
export const smsSubmitEnvoySuccess = createAction('[campaign sms / sumbit envoy success]');
export const smsSubmitEnvoyFail = createAction('[campaign sms / submit envoy fail ]', props<{ httpErrorResponse: HttpErrorResponse }>());
export const alertSmsSubmitEnvoyFail = createAction('[cxm campaign sms/ alert SMS submit sent fail]');

export const distributionSMSChannelDoesNotConfig = createAction('[FLOW DEPOSIT / distribution channel does not config sms.]');

export const smsInitStep = createAction('[campaign sms / init parameter]', props<{step: number}>());
export const smsValidateStep = createAction('[campaign sms / validate step]', props<{step: number }>());


export const smsAttempToStep = createAction('[campaign sms / attemp to step]', props<{ step: number }>());

export const smsFilterCsvFilterChanged = createAction('[campaign sms / csv filter changed]', props<{ filter: any }>());


export const smsTestSendBat = createAction('[campaing sms / test send bat]', props<{ show: boolean, result?: any }>());
export const smsSubmitTestSendBat = createAction('[campaign sms / submit test send bat]', props<{ recipients: Array<string> }>());
export const smsSubmitTestSendBatSuccess = createAction('[campaign sms / submit test send bat success]', props<{ show: boolean, result?: any, total: number }>());
export const smsSubmitTestSendBatFail = createAction('[campaign sms / submit test send bat fail]', props<{ show: boolean, total: number}>());

export const initLockableSmsForm = createAction('[Campaign sms] / init lockable campaign sms form', props<{isLock: boolean}>());

export const resetSmsCsv = createAction('[Campaign sms / reset csv file]');

export const setSmsUploadingBar = createAction('[sms campaign / file dropped]', props<{ mode: string, progression: number, errorName: string }>());

export const fetchSMSCsv = createAction('[sms campaign / fetch SMS Csv records]');

export const closeLoading = createAction('[sms campaign / close loading]');

export const clearRecord = createAction('[sms campaign / clear sms record]');

export const getSmsMetadata = createAction('[sms campaign / get sms metadata]');
export const getSmsMetadataSuccess = createAction('[sms campaign / get sms metadata success]', props<{ metadataResponse: MetadataResponseModel }>());
export const getSmsMetadataFail = createAction('[sms campaign / get sms metadata fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
