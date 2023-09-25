export declare type SettingOptionCriteriaType =
  | 'Attachment'
  | 'Background'
  | 'Signature'
  | 'Watermark';

export interface KeyValue {
  key: string;
  value: string;
}

export interface PositionSetting {
  mode: number;
  key: string;
  value: string;
  val: string;
}

export interface ResourceOptionSetting {
  createdAt: Date;
  createdBy: string;
  fileId: string;
  fileName: string;
  fileSize: number;
  id: number;
  label: string;
  lastModified: Date;
  lastModifiedBy: string;
  ownerId: number;
  pageNumber: number;
  type: string;
}

export interface ResourceResponse {
  page: number;
  pageSize: number;
  total: number;
  contents: Array<ResourceOptionSetting>;
}

export interface ResourceDetail {
  base64: string;
  extension: string;
  fileId: string;
  fileSize: number;
  flowId: string;
  id: number;
  missing: boolean;
  originalName: string;
  ownerId: number;
  position: string;
  source: string;
}

export interface AttachmentDetail {
  id: number;
  fileId: string;
  originalName: string;
  fileSize: number;
  extension: string;
  numberOfPages: number;
  base64: string;
  default: boolean;
  flowId: string;
  source: string;
  position: string;
  ownerId: number;
  missing: boolean;
  deletable: boolean;
  modifiable: boolean;
  selected?: boolean;
}

export interface AttachmentResponse {
  Signature: Array<AttachmentDetail>;
  Background: Array<AttachmentDetail>;
  Attachment: Array<AttachmentDetail>;
}

export declare type FetchWaterMarkType =
  | 'read'
  | 'edit';

export interface ColorPicker {
  hex: string;
  value: string;
}
