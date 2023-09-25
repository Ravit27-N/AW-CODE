import {createReducer, on} from '@ngrx/store';
import {FlowDepositSettingOptionInitialStateModel} from '../../model/flow-deposit-setting-option-initial-state-model';
import * as fromActions from './flow-deposit-setting-option.action';
import {FileUtils} from '@cxm-smartflow/shared/utils';
import {
  AddressDestination,
  ResourceResponse,
  SettingOptionCriteriaConstant
} from '@cxm-smartflow/flow-deposit/data-access';
import {UserProfileUtil, UserUtil} from '@cxm-smartflow/shared/data-access/services';
import {EnrichmentMailing} from '@cxm-smartflow/shared/data-access/model';
import {initAnalyzeResponseSuccess} from "./flow-deposit-setting-option.action";

export const flowDepositSettingOptionKey = 'FLOW_DEPOSIT_SETTING_OPTION_KEY';
const initialState: FlowDepositSettingOptionInitialStateModel = {
  addResourceType: [],
  selectedAddResourceType: '1',
  attachmentPosition: [],
  isHideUploadingArea: true,
  selectedAttachmentPosition: '',
  fileSize: '',
  fileName: '',
  fileId: '',
  attributeId: 0,
  base64: '',
  temporaryBase64: '',
  resources: {
    contents: [],
    page: 0,
    pageSize: 0,
    total: 0,
  },
  selectedResource: '',
  files: [],
  messages: {
    title: 'flow.deposit.setting_option_popup_attachment_title',
    addType: 'flow.deposit.setting_option_popup_attachment_type',
    choiceOfAttachment: 'flow.deposit.setting_option_popup_choice_of_attachment',
    addBtn: 'flow.deposit.setting_option_popup_add_button',
    uploadingInstruction: 'flow.deposit.setting_option_popup_uploading_instruction',
    formatDocument: 'flow.deposit.setting_option_popup_uploading_format',
    maxPageOfDocument: 'flow.deposit.productionCriteria.form.label.file_max',
    position: '',
  },
  resourceDetail: {
    base64: '',
    fileId: '',
    fileSize: 0,
    extension: '',
    flowId: '',
    missing: false,
    originalName: '',
    position: '',
    ownerId: 0,
    source: '',
    id: 0,
  },
  signatures: [],
  attachments: [],
  backgrounds: [],
  canAddSignature: true,
  canAddAttachment: true,
  canAddBackground: true,
  mode: 'add',
  uploadingFileId: '',
  popupType: 'Attachment',
  isValidSignature: false,
  watermark: [],
  selectedWatermarkPosition: '1',
  watermarkAttribute: {
    id: 0,
    text: '',
    position: '',
    size: 1,
    rotation: 0,
    color: '',
    flowId: '',
    default: false
  },
  canAddWatermark: false,
  fetchModeWaterMark: "read",
  postalCode: 0,
  postalInfo: {},
  postalStatus:false,
  docUuid:"",
  addressDestination: {},
  flowDocumentAddresses: [],
  response:[]
};

export const flowDepositSettingOptionReducer = createReducer(
  initialState,
  on(fromActions.attachAttachmentPopup, (state, props) => {
    const isAddedMode = state.mode === 'add';
    const selectedAttachmentPosition = isAddedMode && props ? props.attachmentPosition[0]?.key : state.selectedAttachmentPosition;

    // add resource Type.
    let selectedAddResourceType = state.selectedAddResourceType;
    if (isAddedMode) {
      selectedAddResourceType = props.addResourceType.length === 1 ? props.addResourceType[0]?.key : '1';
    }

    const isHideUploadingArea = selectedAddResourceType === '1';
    return {
      ...state,
      addResourceType: props.addResourceType,
      attachmentPosition: props.attachmentPosition,
      selectedAttachmentPosition,
      selectedAddResourceType,
      isHideUploadingArea
    };
  }),
  on(fromActions.switchAddResourceType, (state, props) => {
    const isHideUploadingArea = props.selectResourceType !== '2';
    const temporaryBase64 = state.base64;
    const base64 = state.temporaryBase64;

    return {...state, isHideUploadingArea, selectedAddResourceType: props.selectResourceType, temporaryBase64, base64};
  }),
  on(fromActions.resetFormUploading, (state) => {
    return {...state, fileName: '', fileSize: '', base64: ''};
  }),
  on(fromActions.fetchResourcesSuccess, (state, props) => {
    return {...state, resources: props.resources, mode: 'edit'};
  }),
  on(fromActions.fileUploadChange, (state, props) => {
    return {...state, files: props.files, popupType: props.popupType};
  }),
  on(fromActions.uploadFileSuccessfully, (state, props) => {
    const {fileSize, fileId, originalName, base64} = props.response;
    const fs = FileUtils.convertToKB(`${fileSize}B`);
    return {
      ...state,
      fileSize: FileUtils.getLimitSize(`${fs}KB`),
      fileName: originalName,
      fileId,
      base64,
      uploadingFileId: fileId
    };
  }),
  on(fromActions.switchResourceLabel, (state, props) => {
    const fileId = state.resources.contents.find(item => item.label === props.label)?.fileId || '';
    return {...state, selectedResource: props.label, fileId};
  }),
  on(fromActions.switchResourceLabelSuccess, (state, props) => {
    return {...state, base64: props.base64};
  }),
  on(fromActions.switchResourceLabelFail, (state) => {
    return {...state, base64: ''};
  }),
  on(fromActions.switchAttachmentPosition, (state, props) => {
    return {...state, selectedAttachmentPosition: props.position};
  }),
  on(fromActions.clearSettingOptionStates, (state) => {
    return {
      ...initialState,
      signatures: state.signatures,
      backgrounds: state.backgrounds,
      attachments: state.attachments,
      canAddSignature: state.canAddSignature,
      canAddAttachment: state.canAddAttachment,
      canAddBackground: state.canAddBackground,
      fileId: state.fileId,
      isValidSignature: state.isValidSignature,
      watermarkAttribute: state.watermarkAttribute,
      canAddWatermark: state.canAddWatermark
    };
  }),
  on(fromActions.fetchAddedAttachmentDetailSuccess, (state, props) => {
    const {position, base64, source, originalName, id, fileSize, fileId} = props.resourceDetail;
    const selectedAddResourceType = source === 'Library' ? '1' : '2';
    let selectedAttachmentPosition = '';
    if (state.popupType === 'Background') {
      selectedAttachmentPosition = SettingOptionCriteriaConstant.BACKGROUND_POSITION.find(e => position === e.val)?.key || '';
    } else if (state.popupType === 'Attachment') {
      selectedAttachmentPosition = SettingOptionCriteriaConstant.ATTACHMENT_POSITION.find(e => position === e.val)?.key || '';
    }

    const fs = FileUtils.convertToKB(`${fileSize}B`);
    const displayFileSize = `${FileUtils.getLimitSize(`${fs}KB`)}`;
    const displayFileName = source === 'Library' ? '' : originalName;
    let resources: ResourceResponse = state.resources;

    if (state.mode === 'edit') {

      const canUsingLibrary = UserProfileUtil.getInstance().canVisibility({
        func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
        priv: EnrichmentMailing.USE_RESOURCE_IN_LIBRARY,
        ownerId: UserUtil.getOwnerId(),
        checkAdmin: false,
      });

      if (!canUsingLibrary && source === 'Library') {
        resources = {
          contents: [
            {
              createdAt: new Date(),
              createdBy: '',
              fileId: '',
              fileName: originalName,
              fileSize: 0,
              id: 0,
              label: originalName,
              lastModified: new Date(),
              lastModifiedBy: '',
              ownerId: UserUtil.getOwnerId(),
              pageNumber: 0,
              type: 'Library',
            }
          ],
          page: 1,
          total: 1,
          pageSize: 1,
        }
      }
    }

    const isHideUploadingArea = source === 'Library';

    return {
      ...state,
      resourceDetail: props.resourceDetail,
      base64,
      selectedAttachmentPosition,
      selectedAddResourceType,
      selectedResource: source === 'Library' ? originalName : '',
      attachmentId: id,
      isHideUploadingArea,
      resources,
      fileName: displayFileName,
      fileSize: displayFileSize,
      fileId: source === 'Library' ? fileId : state.fileId,
      uploadingFileId: source === 'Library' ? state.uploadingFileId : fileId
    };
  }),
  on(fromActions.getAllAttachmentSettingOptionSuccess, (state, props) => {
    const {Background, Attachment, Signature} = props.response;
    const canAddAttachment = Attachment.length < 5;

    // Check can add background.
    const hasAllPages = props.response.Background.some(item => item.position === 'ALL_PAGES');
    const hasAllPageOptions = props.response.Background.length === 3;
    const canAddBackground = !hasAllPages && !hasAllPageOptions;
    const canAddSignature = props.response.Signature.length === 0;

    return {
      ...state,
      signatures: Signature,
      attachments: Attachment,
      backgrounds: Background,
      canAddSignature,
      canAddAttachment,
      canAddBackground,
    };
  }),
  on(fromActions.fetchAddedAttachmentDetail, (state, props) => {
    return {
      ...state,
      mode: 'edit',
      attributeId: props.attributeId,
      messages: {...state.messages, addBtn: 'flow.deposit.setting_option_popup_modify_button'},
      popupType: props.popupType,
    };
  }),
  on(fromActions.attachSettingOptionPopup, (state, props) => {
    let messages: any = {
      title: '',
      addType: '',
      choiceOfAttachment: '',
      addBtn: '',
      uploadingInstruction: '',
      formatDocument: '',
      maxPageOfDocument: '',
      position: '',
      textSize:'',
      textWaterMark:'',
      textRotation:'',
      textColor:''
    };

    switch (props.popupType) {
      case 'Attachment': {
        messages = {
          title: 'flow.deposit.setting_option_popup_attachment_title',
          addType: 'flow.deposit.setting_option_popup_attachment_type',
          choiceOfAttachment: 'flow.deposit.setting_option_popup_choice_of_attachment',
          addBtn: state.mode === 'add' ? 'flow.deposit.setting_option_popup_add_button' : 'flow.deposit.setting_option_popup_modify_button',
          uploadingInstruction: 'flow.deposit.setting_option_popup_uploading_instruction',
          formatDocument: 'flow.deposit.setting_option_popup_uploading_format',
          maxPageOfDocument: '',
          positon: 'flow.deposit.setting_option_popup_position',
          textSize:'',
          textWaterMark:'',
          textRotation:'',
          textColor:''
        };

        break;
      }

      case 'Background': {
        messages = {
          title: 'flow.deposit.productionCriteria.form.label.attachwatermark',
          addType: 'flow.deposit.productionCriteria.form.label.watermarktype',
          choiceOfAttachment: 'flow.deposit.productionCriteria.form.label.choosepage',
          addBtn: state.mode === 'add' ? 'flow.deposit.setting_option_popup_add_button' : 'flow.deposit.setting_option_popup_modify_button',
          uploadingInstruction: 'flow.deposit.productionCriteria.form.label.upload_inst',
          formatDocument: 'flow.deposit.productionCriteria.form.label.file_limt',
          maxPageOfDocument: 'flow.deposit.productionCriteria.form.label.file_max',
          position: 'flow.deposit.setting_option_popup_position',
          textSize:'',
          textWaterMark:'',
          textRotation:'',
          textColor:''
        };

        break;
      }

      case 'Signature': {
        messages = {
          title: 'flow.deposit.setting_option_popup_signature_title',
          addType: 'flow.deposit.setting_option_popup_signature_type',
          choiceOfAttachment: 'flow.deposit.setting_option_popup_choice_of_signature',
          addBtn: state.mode === 'add' ? 'flow.deposit.setting_option_popup_add_button' : 'flow.deposit.setting_option_popup_modify_button',
          uploadingInstruction: 'flow.deposit.setting_option_popup_uploading_signature_instruction',
          formatDocument: 'flow.deposit.setting_option_popup_uploading_signature_format',
          maxPageOfDocument: '',
          position: '',
          textSize: '',
          textWaterMark:'',
          textRotation:'',
          textColor:''
        };

        break;
      }

      case 'Watermark': {
        messages = {
          title: 'flow.deposit.setting_option_popup_watermark_title',
          addType: '',
          choiceOfAttachment: '',
          addBtn: state.mode === 'add' ? 'flow.deposit.setting_option_popup_add_button' : 'flow.deposit.setting_option_popup_modify_button',
          uploadingInstruction: '',
          formatDocument: '',
          maxPageOfDocument: '',
          position: 'flow.deposit.setting_option_popup_position',
          textSize: 'flow.deposit.setting_option_popup_text_size',
          textWaterMark: 'flow.deposit.setting_option_popup_text_water_mark',
          textRotation: 'flow.deposit.setting_option_popup_text_rotation',
          textColor: 'flow.deposit.setting_option_popup_text_color'
        };
        break;
      }
    }

    return {...state, popupType: props.popupType, messages};
  }),
  on(fromActions.hasPortalSignatureConfigSuccess, (state, props) => ({
    ...state,
    isValidSignature: props.isValidSignature,
  })),
  on(fromActions.switchWaterMarkPosition, (state, props) => {
    return {...state, selectedWatermarkPosition: props.waterMarkPosition};
  }),
  on(fromActions.createWaterMark, (state, props) => {
    return {...state, watermarkAttribute: props.waterMarkAttribute};
  }),
  on(fromActions.fetchWatermark, (state, props) => {
    return {...state};
  }),
  on(fromActions.fetchWatermarkEdit, (state, props) => {
    return {
      ...state,
      mode: 'edit',
      attributeId: props.attributeId,
      messages: {...state.messages, addBtn: 'flow.deposit.setting_option_popup_modify_button'},
      popupType: props.popupType,
    };
  }),
  on(fromActions.fetchWatermarkSuccess, (state, props) => {
    const canAddWatermark = !props?.waterMarkAttribute;
    return {
      ...state, canAddWatermark: canAddWatermark, watermarkAttribute: props.waterMarkAttribute
    };
  }),
  on(fromActions.fetchWatermarkFail, (state, props) => {
    return {
      ...state, canAddWatermark: true
    };
  }),
  on(fromActions.updateWatermark, (state, props) => {
    return {...state, waterMarkAttribute: props.waterMarkAttribute};
  }),
  on(fromActions.fetchWatermarkEditSuccess, (state, props) => {
    return {...state, selectedWatermarkPosition: props.waterMarkKey};
  }),
  on(fromActions.deleteWatermarkSuccess, (state) => {
    return {...state, watermarkAttribute: initialState.watermarkAttribute};
  }),
  on(fromActions.clearAllStateInSettingOption, () => {
    return { ...initialState };
  }),
  on(fromActions.fetchFlowDocumentAddress, (state, props) => {
    return {
      ...state,
      docUuid: props.docUuid
    };
  }),
  on(fromActions.fetchFlowDocumentAddressSuccess, (state, props) => {
    const flowDocumentAddress = props?.flowDocumentAddressResponse || [];
    const addressDestination: AddressDestination = {};
    if (flowDocumentAddress.length > 0) {
      flowDocumentAddress.forEach(state => {
        switch (state.addressLineNumber) {
          case 1:
            addressDestination.Line1 = state.address || "";
            break;
          case 2:
            addressDestination.Line2 = state.address || "";
            break;
          case 3:
            addressDestination.Line3 = state.address || "";
            break;
          case 4:
            addressDestination.Line4 = state.address || "";
            break;
          case 5:
            addressDestination.Line5 = state.address || "";
            break;
          case 6:
            addressDestination.Line6 = state.address || "";
            break;
          case 7:
            addressDestination.Line7 = state.address || "";
            break;
          default:
            break;
        }
      })
    }
    return {
      ...state,
      addressDestination: addressDestination
    };
  }),
  on(fromActions.updateFlowDocumentAddress, (state, props) => {
    return {...state, flowDocumentAddresses: props.flowDocumentAddresses};
  }),
  on(fromActions.initAnalyzeResponseSuccess, (state, props) => {
    return {
      ...state,
      response: props.response
    };
  }),
);

