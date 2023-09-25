import {
  DirectoryDefinitionForm,
  DirectoryField,
  FieldData,
  FieldDataKey
} from '@cxm-smartflow/definition-directory/data-access';
import { KeyValue } from '@cxm-smartflow/flow-traceability/data-access';
import { DIRECTORIES_FORM } from './directory-constant';

/**
 * Method used get directory fields from local storage.
 * @return object of {@link DirectoryField}
 */
export const getDirectoryFieldFromLocalStorage: () => any = () => JSON.parse(<string> localStorage.getItem(DIRECTORIES_FORM));

/**
 * Method used to check directory filed have empty or not.
 * @param directoryDefinitionForm
 * @return value of {@link boolean}
 */
export const isDirectoryFieldsHaveEmpty: (directoryDefinitionForm: DirectoryDefinitionForm) => boolean = (directoryDefinitionForm: DirectoryDefinitionForm) => {
  let isNull = 0;
  directoryDefinitionForm?.directoryFields?.filter(directory => directory?.field?.trim()?.length === 0 ? isNull++ : 0);
  return isNull > 0;
};

/**
 * Method used to check directory field not have key or not.
 * @param directoryDefinitionForm
 * @return value of {@link boolean}
 */
export const isDirectoryFieldsNoneKey: (directoryDefinitionForm: DirectoryDefinitionForm) => boolean = (directoryDefinitionForm: DirectoryDefinitionForm) => {
  return directoryDefinitionForm?.directoryFields?.filter(item => item?.key === true)?.length === 0;
}

/**
 * Method used to check directory filed have duplicate or not.
 * @param directoryDefinitionForm
 * @return value of {@link boolean}
 */
export const isDirectoryFieldsDuplicate: (directoryDefinitionForm: DirectoryDefinitionForm) => boolean = (directoryDefinitionForm: DirectoryDefinitionForm) => {
  const fields: any = directoryDefinitionForm?.directoryFields?.map(directory => directory?.field?.trim());
  return fields?.some((item: string, idx: any) => {
    return fields?.indexOf(item?.trim()) != idx;
  }) || false;
};

/**
 * Method used to get field data.
 * @param field
 * return value of {@link string}
 */
export const getFieldData = (field?: string): string => {
  return  Object.assign(FieldDataKey)[Object.keys(FieldDataKey)
    .map(j => toCapitalize(j.replace('_', ' ')
      .toLowerCase())).filter(s => s === field)[0]
    ?.toUpperCase()?.replace(' ', '_')] || FieldDataKey.SPECIFIC
}

export const toCapitalize = (value: string) => {
  return value.charAt(0).toUpperCase() + value.slice(1);
};

/**
 * Method used to get directory fields after sorted.
 * @param directory
 * return list of {@link DirectoryField}
 */
export const getDirectoryFieldsSorted = (directory: DirectoryDefinitionForm): DirectoryField[] => {
  return [...(directory?.directoryFields || [])]?.sort((a: any, b: any) => (a?.fieldOrder > b?.fieldOrder) ? 1 : -1);
}

/**
 * Method used to get option value.
 * @param value
 * @return value of {@link string}
 */
export const getOption = (value?: string): string => {
  return value ? 'directory_field_option_mask' : 'directory_field_option_length';
}

/**
 * Method used to get filed type value.
 * @param required
 * @return value of {@link string}
 */
export const getFieldType = (required?: boolean): string => {
  return required ? 'Mandatory' : 'Optional';
}

/**
 * Method used to get object of {@link KeyValue}
 * @param dataTypes
 * @param type
 * @return object of {@link KeyValue}
 */
export const getDataTypeOfField = (dataTypes: KeyValue[], type?: string): KeyValue => {
  return dataTypes?.filter(item => item.value === type)[0];
}

/**
 * Method used to get field data.
 * @param fieldData
 * @param fieldDataKey
 * @return value of {@link string}
 */
export const getData = (fieldData: FieldData[], fieldDataKey?: string) => {
  return fieldData?.filter(item => item.key === fieldDataKey)[0]?.value;
}

