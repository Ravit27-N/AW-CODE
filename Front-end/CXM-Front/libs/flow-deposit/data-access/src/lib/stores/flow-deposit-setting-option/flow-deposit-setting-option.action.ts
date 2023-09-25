import {createAction, props} from '@ngrx/store';
import {
  AttachmentResponse,
  FetchWaterMarkType, FlowDocumentAddress,
  FlowDocumentAddressDto,
  KeyValue,
  PositionSetting,
  PostalInfo,
  ResourceDetail,
  ResourceResponse,
  SettingOptionCriteriaType,
  WatermarkAttribute
} from '@cxm-smartflow/flow-deposit/data-access';
import {HttpErrorResponse} from '@angular/common/http';


export const attachSettingOptionPopup = createAction('[FLOW DEPOSIT SETTING OPTION / attach setting option popup]', props<{ popupType: SettingOptionCriteriaType }>());

// Attachment.
export const setupAttachmentPopup = createAction('[FLOW DEPOSIT SETTING OPTION / setup attachment popup]', props<{ popupType: SettingOptionCriteriaType}>());
export const attachAttachmentPopup = createAction('[FLOW DEPOSIT SETTING OPTION / attach attachment popup]', props<{
  addResourceType: Array<KeyValue>,
  attachmentPosition: Array<PositionSetting>,
  popupType: SettingOptionCriteriaType,
}>());

// Switch add resource types.
export const switchAddResourceType = createAction('[FLOW DEPOSIT SETTING OPTION / switch add resource type]', props<{ selectResourceType: string, popupType: SettingOptionCriteriaType }>());
export const cancelFetchResource = createAction('[FLOW DEPOSIT SETTING OPTION / cancel fetch resource]');

// Fetch resources.
export const fetchResources = createAction('[FLOW DEPOSIT SETTING OPTION / fetch resource]', props<{ popupType: SettingOptionCriteriaType }>());
export const fetchResourcesSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / fetch resource success]', props<{ resources: ResourceResponse }>());
export const fetchResourceFail = createAction('[FLOW DEPOSIT SETTING OPTION / fetch resource fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Switch resource labels.
export const switchResourceLabel = createAction('FLOW DEPOSIT SETTING OPTION / switch resource label', props<{ label: string }>());
export const switchResourceLabelSuccess = createAction('FLOW DEPOSIT SETTING OPTION / switch resource label success', props<{ base64: string }>());
export const switchResourceLabelFail = createAction('FLOW DEPOSIT SETTING OPTION / switch resource label fail', props<{ httpErrorResponse: HttpErrorResponse }>());

// Uploading area.
export const fileUploadChange = createAction('[FLOW DEPOSIT SETTING OPTION / file upload file change]', props<{ files: File[], popupType: SettingOptionCriteriaType }>());
export const fileUploadingInProgress = createAction('[FLOW DEPOSIT SETTING OPTION / file uploading in progress]', props<{ response: any }>());
export const uploadFileSuccessfully = createAction('[FLOW DEPOSIT SETTING OPTION / upload file successfully]', props<{ response: any }>());
export const uploadingFileFail = createAction('[FLOW DEPOSIT SETTING OPTION / upload file fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
export const resetFormUploading = createAction('[FLOW DEPOSIT SETTING OPTION / reset form uploading]');

// Switch position.
export const switchAttachmentPosition = createAction('FLOW DEPOSIT SETTING OPTION / switch attachment position', props<{ position: string }>());
export const clearSettingOptionStates = createAction('[FLOW DEPOSIT SETTING OPTION / clear setting option state]');

// Fetch attachment resource.
export const fetchAddedAttachmentDetail = createAction('[FLOW DEPOSIT SETTING OPTION / fetch added attachment detail]', props<{ attributeId: number, popupType: SettingOptionCriteriaType }>());
export const fetchAddedAttachmentDetailSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / fetch added attachment detail success]', props<{ resourceDetail: ResourceDetail }>());
export const fetchAddedAttachmentDetailFail = createAction('[FLOW DEPOSIT SETTING OPTION / fetch added attachment detail fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
// Updating attachment.
export const updateAttachmentSettingOption = createAction('[FLOW DEPOSIT SETTING OPTION / update attachment setting option]', props<{ popupType: SettingOptionCriteriaType }>());
export const updateAttachmentSettingOptionSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / update attachment setting option success]');
export const updateAttachmentSettingOptionFail = createAction('[FLOW DEPOSIT SETTING OPTION / update attachment setting option fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Get all attachment.
export const getAllAttachmentSettingOption = createAction('[FLOW DEPOSIT SETTING OPTION / get all attachment setting popup]');
export const getAllAttachmentSettingOptionSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / get all attachment setting popup success]', props<{ response: AttachmentResponse }>());
export const getAllAttachmentSettingOptionFail = createAction('[FLOW DEPOSIT SETTING OPTION / get all attachment setting popup fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Delete option attribute.
export const deleteOptionAttribute = createAction('[FLOW DEPOSIT SETTING OPTION / delete option attribute]', props<{ attributeId: number }>());
export const deleteOptionAttributeSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / delete option attribute success]');
export const deleteOptionAttributeFail = createAction('[FLOW DEPOSIT SETTING OPTION / delete option attribute fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Validate portal signature configuration.
export const hasPortalSignatureConfig = createAction('[FLOW DEPOSIT SETTING OPTION / validate portal signature configuration]', props<{ modelName: string }>());
export const hasPortalSignatureConfigSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / validate portal signature configuration success]', props<{ isValidSignature: boolean }>());

export const switchWaterMarkPosition = createAction('FLOW DEPOSIT SETTING OPTION / switch watermark position', props<{ waterMarkPosition: string }>());
export const createWaterMark = createAction('[FLOW DEPOSIT SETTING OPTION / create watermark setting option]', props<{ waterMarkAttribute: WatermarkAttribute }>());
export const createWaterMarkSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / create watermark setting option success]');
export const createWaterMarkFail = createAction('[FLOW DEPOSIT SETTING OPTION / create watermark setting option fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const fetchWatermark = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch watermark in setting option]');
export const fetchWatermarkSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch watermark in setting success]', props<{ waterMarkAttribute: WatermarkAttribute }>());
export const fetchWatermarkFail = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch watermark in setting fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const fetchWatermarkEdit = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch watermark edit in setting option]', props<{ fetchModeWaterMark: FetchWaterMarkType, attributeId: number, popupType: SettingOptionCriteriaType }>());
export const fetchWatermarkEditSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch watermark for edit in setting success]', props<{ waterMarkAttribute: WatermarkAttribute, waterMarkKey: string }>());

export const updateWatermark = createAction('[FLOW DEPOSIT SETTING OPTION / update watermark in setting option]', props<{ waterMarkAttribute: WatermarkAttribute }>());
export const updateWatermarkSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / update watermark in setting success]');
export const updateWatermarkFail = createAction('[FLOW DEPOSIT SETTING OPTION / update watermark in setting fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const deleteWatermark = createAction('[FLOW DEPOSIT SETTING OPTION / Delete watermark in setting option]');
export const deleteWatermarkSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / Delete watermark in setting success]');
export const deleteWatermarkFail = createAction('[FLOW DEPOSIT SETTING OPTION / Delete watermark in setting fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const clearAllStateInSettingOption = createAction('FLOW DEPOSIT OPTION / clear all stAte setting option');

export const fetchFlowDocumentAddress = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch flow document address]',props<{ docUuid: string }>());
export const fetchFlowDocumentAddressSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch flow document address success]', props<{ flowDocumentAddressResponse: FlowDocumentAddress[] }>());
export const fetchFlowDocumentAddressFail = createAction('[FLOW DEPOSIT SETTING OPTION / Fetch flow document address fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const updateFlowDocumentAddress = createAction('[FLOW DEPOSIT SETTING OPTION / Update flow document address]', props<{flowDocumentAddresses: FlowDocumentAddress[] }>());
export const updateFlowDocumentAddressSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / Update flow document address success]');
export const updateFlowDocumentAddressFail = createAction('[FLOW DEPOSIT SETTING OPTION / Update flow document address fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const initAnalyzeResponseStepThree = createAction('[FLOW DEPOSIT SETTING OPTION / Flow document address init step 3]');
export const initAnalyzeResponseSuccess = createAction('[FLOW DEPOSIT SETTING OPTION / Flow document address init step 3 success]', props<{ response: any }>());
export const initAnalyzeResponseFail = createAction('[FLOW DEPOSIT SETTING OPTION / Flow document address init step 3 fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
