export interface DirectoryDefinitionForm {
  id?: number;
  name?: string;
  displayName?: string;
  directoryFields?: DirectoryField[];
  clients?: number [];
  createdBy?: string;
  createdAt?: Date;
  lastModified?: string;
  ownerId?: number;
  hasFeeding?:boolean;
}

export interface DirectoryField {
  id?: number;
  fieldOrder?: number;
  field?: string;
  type?: string;
  key?: boolean;
  canModifyToRequire?: boolean,
  canModifyToKey?: boolean,
  properties?: {
    data?: string;
    displayName?: string;
    required?: boolean;
    option?: {
      length?: string;
      mask?: string;
    };
  };
}
