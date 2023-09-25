import {
  AttachmentDetail,
  KeyValue,
  PositionSetting,
  ResourceDetail,
  ResourceResponse, SettingOptionCriteriaType
} from './setting-option-criteria-model';
import {AddressDestination, FlowDocumentAddress, PostalInfo, WatermarkAttribute} from "./watermark.model";

export interface FlowDepositSettingOptionInitialStateModel {
  addResourceType: Array<KeyValue>;
  selectedAddResourceType: string;
  isHideUploadingArea: boolean;
  attachmentPosition: Array<PositionSetting>;
  selectedAttachmentPosition: string;
  selectedResource: string;
  fileName: string;
  fileSize: string;
  fileId: string;
  uploadingFileId: string;
  attributeId: number;
  base64: string;
  temporaryBase64: string;
  resources: ResourceResponse;
  files: Array<File>;
  messages: {
    title: string;
    addType: string;
    choiceOfAttachment: string;
    addBtn: string;
    uploadingInstruction: string;
    formatDocument: string;
    maxPageOfDocument: string;
    position: string;
  };
  resourceDetail: ResourceDetail;
  signatures: Array<AttachmentDetail>;
  attachments: Array<AttachmentDetail>;
  backgrounds: Array<AttachmentDetail>;
  canAddSignature: boolean;
  canAddBackground: boolean;
  canAddAttachment: boolean;
  mode: 'add' | 'edit';
  popupType: SettingOptionCriteriaType;
  isValidSignature: boolean;
  watermark: Array<AttachmentDetail>;
  selectedWatermarkPosition: string;
  watermarkAttribute: WatermarkAttribute;
  canAddWatermark: boolean;
  fetchModeWaterMark: "read" | "edit";
  postalCode:number;
  postalInfo:PostalInfo;
  postalStatus:boolean;
  docUuid:string;
  addressDestination:AddressDestination;
  flowDocumentAddresses: FlowDocumentAddress[];
  response:any,
}
