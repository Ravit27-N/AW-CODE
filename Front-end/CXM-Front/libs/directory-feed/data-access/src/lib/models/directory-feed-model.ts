import { DirectoryDataType } from './directory-data-type-enum';
import { DirectoryFiledData } from './directory-filed-data-enum';

export interface DirectoryFeedModel {
  id: number;
  name: string;
  displayName: string;
  createdBy: string;
  ownerId: number;
  feedingBy: string;
  feedingDate: Date;
  createdAt: Date;
  lastModified: Date;
  adminFeedingBy: string;
  adminFeedingDate: Date;
}

export interface DirectoryFeedListResponse {
  contents: DirectoryFeedModel[];
  page: number;
  pageSize: number;
  total: number;
}

export interface DirectoryFeedField {
  directoryId: number;
  directoryName: string;
  fields: FeedField[];
  shareDirectory: boolean;
  [key: string]: any;
}

export interface FeedField {
  id: number;
  fieldOrder: number;
  key: boolean;
  field: string;
  type: DirectoryDataType;
  properties: FieldProperty;
}

export interface FieldProperty{
  data: DirectoryFiledData;
  displayName: string;
  required: boolean;
  type: boolean;
  option: {
    data?: DirectoryFiledData;
    length?: number;
    mask?: string;
  }
}

export interface DirectoryFeedValue {
  id: number;
  lineNumber: string;
  directoryId: string;
  clientId: string;
  values: Array<{
    value: string;
    fieldOrder: string;
    id: number;
    directoryFieldId: number;
  }>;
}

export interface ListDirectoryFeedValue {
  contents: DirectoryFeedValue[];
  page: number;
  pageSize: number;
  total: number;
  isLoad: boolean
}


export interface DirectoryFeedFieldModelForAdd {
  id: number;
  field: string;
  require: boolean;
  key: boolean;
  type: string;
  option: any;
  field_order: number;
}

export interface UploadCsvResultModel {
  fileId?: string;
  fileName?: string;
  size?: number;
}

export interface ImportCsvRequestModel {
  fileId: string;
  removeDuplicate: boolean;
  ignoreHeader: boolean;
}

export interface CheckExistValueParamModel {
  fieldId: number;
  id: number;
  value: string;
}
