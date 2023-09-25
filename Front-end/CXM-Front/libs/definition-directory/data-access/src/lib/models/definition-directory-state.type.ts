import { DefinitionDirectoryListType } from './definition-directory-list.type';
import { SortDirection } from '@angular/material/sort/sort-direction';
import {
  DirectoryDefinitionForm,
  DirectoryField,
} from './definition-directory-creation.type';

export interface DefinitionDirectoryStateType {
  // List.
  listFilteringCriteria: {
    page: number;
    pageSize: number;
    sortByField: 'name' | 'lastModified' | 'createdAt';
    sortDirection: SortDirection;
  };
  listDefinitionDirectoryResponse: DefinitionDirectoryListType;
  definitionDirectoryForm: DirectoryDefinitionForm;
  formHasChange: boolean;
  definitionDirectoryBeforeModify: DirectoryDefinitionForm;
  definitionDirectoryFormEditor: any;
  oldName: string;
}
