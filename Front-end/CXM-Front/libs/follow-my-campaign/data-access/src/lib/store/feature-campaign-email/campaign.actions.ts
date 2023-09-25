import { createAction, props } from '@ngrx/store';
import { CampaignModel, MetadataResponseModel } from '../../models';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomFileModel } from '@cxm-smartflow/shared/data-access/model';

export const submitEmailCampaignParameterStep = createAction('[email campaign]/ submit email campaign of email campaign parameter step', props<{ parameter: any}>());

export const submitEmailCampaignParameterSuccess = createAction('[email campaign]/ show success message', props<{ emailCampaign: any }>());

export const submitEmailCampaignParameterFail = createAction('[email campaign]/ show failed message', props<{ httpErrorResponse: HttpErrorResponse }>());
export const alertSubmitEmailCampaignParameterFail = createAction('[email campaign / alert sent email campaign parameter fail]');
export const distributionEmailChannelDoesNotConfig = createAction('[FLOW DEPOSIT / distribution channel does not config email.]');

export const previousEmailCampaignParameterStep = createAction('[email campaign]/ previous page in email campaign parameter step', props<any>());
export const previousEmailCampaignEnvoiStep = createAction('[email campaign]/ previous page in email campaign envoi step', props<any>());

export const checkCsvHeaderValue = createAction('[campaign / check csv header]', props<{ values: string[] }>());

export const checkCsvHeaderValueSuccess = createAction('[campaign / check csv header succeess]', props<{ ok: boolean }>());

export const checkCsvHeaderAfterUpload = createAction('[campaign / check csv header after upload]', props<{values: string[]}>());

export const checkCsvHeaderAfterUploadSuccess = createAction('[campaign / check csv header success after upload success]', props<{value: any}>());

export const checkCsvHeaderAfterUploadFail = createAction('[campaign / check csv header fail after upload success]');

export const keepCsvFirstRecord = createAction('[campaign email / keep csv first record]', props<{csvFirstRecode: string}>());

export const loadDestinationForm = createAction('[campaign email / load]', props<{ templateId: string }>());

export const unloadDestinationForm = createAction('[campaign email / unload]');


export const prepareEmailCsvUpload = createAction('[campaign email / prepare csv file]', props<{ file: File, matchLength: number,firstrow: any  }>());

export const emailCheckCsvHeaderValueResult = createAction('[campaign email / check csv header result]', props<{ csvOK: boolean, matchLength: number, file?: File }>())

export const uploadCSVFileAction = createAction('[campaign email / csv upload]', props<{ file: File }>());

export const uploadCSVFileActionResponse = createAction('[campaign email / csv upload resposne]', props<{ res: any }>());

export const uploadCSVFileActionFail = createAction('[campaign email / csv upload fail]', props<{ error: any }>());

export const uploadCSVProgresssion = createAction('[campaign email] / csv progresson', props<{ progress: number }>());

export const emailFilterCsvFilterChanged = createAction('[campaign email / csv filter changed]', props<{ filter: { page: number, pageSize: number, sortByField?: string, sortDirection?: string } }>());

export const recordEmailDataFromCsv = createAction('[campaign email / record csv data]', props<{ data: any, filter: { page: number, pageSize: number, total: number } }>());

export const fetchCampaignCsv = createAction('[campaign email / get campaign csv]');

export const fetchCampaignCsvResponse = createAction('[campaign email / get campaign csv response]', props<{ csv: any }>());

export const nonceAction = createAction('[campaign email / csv upload nonce response]'); // just for handle empty case

export const submitDestinationStep = createAction('[email campaign] / submit email campaign of destination step', props<{emailCampaign: CampaignModel, editMode: boolean}>());

export const csvFormValueChange = createAction('[campaign email / csv form value update]', props<{ form: any }>())

export const submitDestinationSuccess = createAction('[email campaign] / submit email campaign of destination step success', props<{ campaign: any }>());

export const submitDestinationFail = createAction('[email campaign] / submit email campaign of destination step fail', props<any>());

export const loadTemplateDetail = createAction('[email campaign] / load template detail', props<{templateId: number}>());

export const loadTemplateDetailSuccess = createAction('[email campaign] / load template detail success', props<{templateDetails: any}>());

export const loadTemplateDetailFail = createAction('[email campaign] / load template detail fail', props<{ error: any }>());

export const parameterFormValueChange = createAction('[email campaign / parameter form value change]');

export const loadEmailCampaignDetail = createAction('[email campaign / load email campaign detail]', props<{campaignId: number}>());

export const loadEmailCampaignDetailSuccess = createAction('[email campaign / load email campaign detail sucess]', props<{ campaign: CampaignModel }>());

export const preloadEmailCampaign = createAction('[email campaign / preload campaign]', props<{ campaignId: any }>());

export const loadEmailCampaignDetailFail = createAction('[email campaign / load email campaign detail fail]', props<{ error: any }>());

export const submitEmailCampaignSummaryStep = createAction('[email campaign]/ submit email campaign of email campaign summary step', props<{ summary: any }>());

export const submitEmailCampaignSummaryStepSuccess = createAction('[email campaign]/ show success message of email campaign summary step', props<{ emailCampaign: any }>());

export const submitEmailCampaignSummaryStepFail = createAction('[email campaign]/ show failed message of email campaign summary step');

export const sendMailTest = createAction('[email campaign / send mail test]', props<{recipientAddress: string[]}>());

export const sendMailTestSuccess = createAction('[email campaign / send mail test success]', props<{total: number}>());

export const sendMailTestFail = createAction('[email campaign / send mail test fail]', props<{total: number}>());

export const unloadEmailCampaignFormData = createAction('[email campaign] / unload email campaign form data');

export const alertEmailCampaignDetailError = createAction('[email campaign / alert email campaign]', props<{ httpError: HttpErrorResponse }>());

export const resetCampaignCsv = createAction('[email campaign / reset campaign csv]');

export const setUploadingBar = createAction('[email campaign / file dropped]', props<{ mode: string, progression: number, errorName: string }>());

// To maximum file size of uploading file
export const getMaxFileSizeUpload = createAction('[email campaign / get maximum file size upload]');

export const getMaxFileSizeUploadSuccess = createAction('[email campaign / get maximum file size upload success]', props<{limitSize: string}>());

export const getMaxFileSizeUploadFailed = createAction('[email campaign / get maximum file size upload failed]');
// Attachments of email campaign parameter form.

export const uploadAttachments = createAction('[email campaign /  parameter form] / upload attachments', props<{ formData: FormData }>());

export const uploadAttachmentsAction = createAction('[email campaign / parameter form] / sending attachments');

export const uploadAttachmentsProgression = createAction('[email campaign / parameter form] / sending attachments', props<{progress: number}>());

export const uploadAttachmentSuccess = createAction('[email campaign / parameter form] / upload attachment success', props<{ attachmentResponse: any, oldAttachments: any }>());

export const uploadAttachmentFail = createAction('[email campaign / parameter form] / upload attachment fail', props<{ error: any }>());

export const removeAttachment = createAction('[email campaign / parameter form] / remove attachment', props<{fileIds: string[]}>());

export const removeAttachmentOnTemporary = createAction('[email campaign / parameter form] / remove attachment on temporary', props<{fileIds: string[]}>())

export const removeAttachmentSuccess = createAction('[email campaign / parameter form] / remove attachment success', props<{fileIds: string[], attachments: CustomFileModel[]}>());

export const removeAttachmentFail = createAction('[email campaign / parameter form] / remove attachment fail', props<{error?: any}>());

export const clearAttachmentInStore = createAction('email campaign / parameter form / clear attachments from store');

export const removeAttachmentUploadedWhenLeavePage = createAction('[email campaign / parameter form] / remove attachment that has uploaded when leave page');

export const updateCampaignParameterWhenLeavePage = createAction('[email campaign / parameter form] / update campaign parameter form when leave page');

export const updateCampaignParameterWhenLeavePageSuccess = createAction('[email campaign / parameter form] / leave page after update campaign success');

export const updateCampaignParameterWhenLeavePageFail = createAction('[email campaign / parameter form] / leave page after update campaign fail');

export const leavePageNoNeedRemoveAttachment = createAction('[email campaing / parameter form] / leave page no need remove attachments');

export const initParameterFormTemporary = createAction('[email campaign / parameter form] / init parameter form temporary', props<{parameter?: any, attachments?: CustomFileModel[]}>());

export const unloadParameterForm = createAction('[email campaign / parameter form] / unload data of parameter form temporary');
// Create email campaign.
export const createEmailCampaignAfterUploadedFile = createAction('[create email campaign after uploaded file.]');

export const createEmailCampaignAfterUploadedFileSuccess = createAction('[create email campaign after uploaded file success.]', props<{ campaignResponse: any }>());

export const createEmailCampaignAfterUploadedFileFail = createAction('[create email campaign after uploaded file fail.]');

export const setIsHeaderChange = createAction('[cxm campaign / set is header change]', props<{ isCheckHeaderChange: boolean}> ());

export const closeEmailLoading = createAction('[cxm campaign / close loading]');

// Get email metadata.
export const getEmailMetadata = createAction('[cxm campaign / get email metadata');
export const getEmailMetadataSuccess = createAction('[cxm campaign / get email metadata success', props<{ metadataResponse: MetadataResponseModel }>());
export const getEmailMetadataFail = createAction('[cxm campaign / get email metadata fail', props<{ httpErrorResponse: HttpErrorResponse }>());
