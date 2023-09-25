export interface DirectoryFeedForm {
  inserted?: InsertedDirectoryFeed[];
  updated?: UpdatedDirectoryFeed[];
  deleted?: number[];
}

export interface InsertedDirectoryFeed {
  lineNumber: number;
  data: FieldDetail[];
}

export interface FieldDetail {
  id?: number;
  fieldId: number;
  value: string;
}

export interface UpdatedDirectoryFeed {
  lineNumber: number;
  data: FieldDetail[];
}

export interface CellObject {
  value: string;
  fieldOrder: number | string;
  id: number;
  directoryFieldId: number;
}

export interface CellDetails {
  lineNumber: string;
  data: CellObject;
}

export interface DebugMessage {
  directory_key?: string;
  field_name?: string;
  field_data_type?: string;
  field_value?: string;
}

export enum ErrorType {
  REQUIRED,
  MASK,
  MAX,
  DATA_TYPE,
  DUPLICATE
}
