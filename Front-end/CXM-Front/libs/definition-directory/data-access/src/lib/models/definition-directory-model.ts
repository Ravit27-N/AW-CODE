import { EntityResponseHandler } from '@cxm-smartflow/shared/data-access/model';

export interface DefinitionDirectoryModel {
  id: number;
  name: string;
  displayName: string;
  createdBy: string;
  createdAt: Date;
  lastModified: Date;
}

export interface DefinitionDirectoryListModel {
  contents: DefinitionDirectoryModel[];
  summary: any;
  page: number;
  pageSize: number;
  total: number;
}

export interface DirectoryNavTab {
  link: string,
  name: string,
  active: boolean
}

export interface DirectoryModel {
  id?: number;
  name?: string;
  field?: string;
  fieldOrder?: number;
  displayName?: string;
  dataTypeOfField?: KeyValue;
  type?: boolean;
  isKey?: boolean;
  fieldData?: string;
  fieldType?: string;
  option?: string;
  maximum?: string;
  mask?: string;
}

export interface ClientModel {
  id?: number;
  name?: string;
  active?: boolean;
  canModify?: boolean;
}

export type ClientList = EntityResponseHandler<ClientModel>;

export interface KeyValue {
  key: string;
  value: string;
}

export interface FieldData {
  options: KeyValue;
  key: string;
  value: string;
}

export interface MaskAndTypeValidationResponse {
  mask: boolean;
  type: boolean;
}

// export interface DirectoryField {
//   id?: number;
//   fieldOrder?: number;
//   field?: string;
//   type?: string;
//   key?: boolean;
//   properties?: {
//     data?: string;
//     displayName?: string;
//     required?: boolean;
//     option?: {
//       length?: string;
//       mask?: string;
//     };
//   };
// }
//
// export interface DirectoryDefinitionForm {
//   id?: number;
//   name?: string;
//   displayName?: string;
//   directoryFields?: DirectoryField[];
//   clients?: number [];
//   createdBy?: string;
//   createdAt?: Date;
//   lastModified?: string;
// }

export interface DirectoryPropertyForm{
  dataTypeOfField ?: KeyValue;
  directoryIndex?: number;
  field?: string;
  fieldData?: string;
  fieldOrder?: number;
  fieldType?: string;
  mask?: string;
  maximum?: number;
  option?: string;
}

export enum FieldDataKey {
  SPECIFIC = "directory_field_data_field_specific",
  COMPANY_NAME = "directory_field_data_company_name",
  TELEPHONE = "directory_field_data_telephone",
  EMAIL = "directory_field_data_email",
  ADDRESS_LINE = "directory_field_data_address_line",
  POSTAL_CODE = "directory_field_data_postal_code",
  COMMUNE = "directory_field_data_commune"
}

export interface DefinitionDirectoryPopUpModel{
  id?: number;
  name?: string;
  displayName?: string;
  formType?: string;
  createdBy?: string;
}

export interface UserDetailModel {
  adminUsername: string,
  normalUsername: string,
  admin: boolean
}
