export interface DefinitionDirectoryListType {
  contents: DefinitionDirectoryResponseType[];
  page: number;
  pageSize: number;
  total: number;
}


export interface DefinitionDirectoryResponseType {
  id: number;
  name: string;
  displayName: string;
  directoryFields: DefinitionDirectoryFieldResponseType[];
  clients: number[];
  createdBy: string;
  ownerId: number;
  createdAt: Date;
  lastModified: Date;
  isDirectoryShared: boolean;
}


export interface DefinitionDirectoryFieldResponseType {
  id: number;
  fieldOrder: number;
  field: string;
  type: 'String' | 'Number' | 'Integer' | 'Mandatory' | 'Boolean';
  key: boolean;
  properties: DefinitionDirectoryFieldPropertiesType;
}


export interface DefinitionDirectoryFieldPropertiesType {
  data: string;
  displayName: string;
  required: boolean;
  option: Map<string, string>;
}
