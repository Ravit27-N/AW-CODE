import { createAction, props } from '@ngrx/store';
import {
  DefinitionDirectoryListType,
  DefinitionDirectoryResponseType
} from '../../models/definition-directory-list.type';
import { SortDirection } from '@angular/material/sort/sort-direction';
import { HttpErrorResponse } from '@angular/common/http';
import { DirectoryDefinitionForm, DirectoryField, FormMode } from '../../models';

// List directories.
export const fetchDirectoryDefinition = createAction(
  '[cxm-directory / fetch directory definition]',
  props<{
    page: number;
    pageSize: number;
    sortByField: 'name' | 'lastModified' | 'createdAt';
    sortDirection: SortDirection;
  }>()
);

export const fetchDirectoryDefinitionSuccess = createAction(
  '[cxm-directory / fetch directory definition success]',
  props<{ listDefinitionDirectoryResponse: DefinitionDirectoryListType }>()
);

export const fetchDirectoryDefinitionFail = createAction(
  '[cxm-directory / fetch directory definition fail]',
  props<{ httpErrorResponse: HttpErrorResponse }>()
);

// Delete a directory.
export const deleteDirectory = createAction(
  '[cxm-directory / delete a directory]',
  props<{ id: number }>()
);

export const deleteDirectorySuccess = createAction(
  '[cxm-directory / delete a directory success]'
);

export const deleteDirectoryFail = createAction(
  '[cxm-directory / delete a directory fail]',
  props<{ httpErrorResponse: HttpErrorResponse }>()
);

export const adjustFormStep1 = createAction(
  '[cxm-directory / adjust form definition directory form step 1]',
  props<{ formStepOne: { name: string; displayName: string } }>()
);

export const adjustDirectoryFields = createAction(
  '[cxm-directory / adjust form fields]',
  props<{ directoryFields: DirectoryField[] }>()
);

export const addClientId = createAction(
  '[cxm-directory / adjust form by adding client id]',
  props<{ clientId: number }>()
);

export const removeClientId = createAction(
  '[cxm-directory / adjust form by remove client id]',
  props<{ clientId: number }>()
);

export const getDefinitionDirectoryDetail = createAction(
  '[cxm-directory / get definition directory detail]',
  props<{
    id: number;
  }>()
);

export const getDefinitionDirectoryDetailSuccess = createAction(
  '[cxm-directory / get definition directory detail success]',
  props<{
    directoryDefinitionForm: DirectoryDefinitionForm,
  }>()
);

export const getDefinitionDirectoryDetailFail = createAction(
  '[cxm-directory / get definition directory detail fails]',
  props<{
    httpErrorResponse: HttpErrorResponse;
  }>()
);

export const submitForm = createAction(
  '[cxm-directory / create directory]',
  props<{
    formType: FormMode;
  }>()
);
export const createDirectorySuccess = createAction(
  '[cxm-directory / create directory success]'
);
export const createDirectoryFail = createAction(
  '[cxm-directory / create directory fails]',
  props<{
    httpErrorResponse: HttpErrorResponse;
  }>()
);

export const modifyDirectorySuccess = createAction(
  '[cxm-directory / modify directory success]'
);
export const modifyDirectoryFail = createAction(
  '[cxm-directory / modify directory fails]',
  props<{
    httpErrorResponse: HttpErrorResponse;
  }>()
);

export const validateFormChange = createAction(
  '[cxm-directory / validate form change]'
);
export const updateFormChange = createAction(
  '[cxm-directory / update form change]',
  props<{ hasChange: boolean }>()
);
export const unloadDefinitionDirectoryForm = createAction(
  '[cxm-directory / unload definition directory]'
);

export const setupHistoryForm = createAction(
  '[cxm-directory / setup directory form]',
  props<{ definitionDirectoryForm: DirectoryDefinitionForm }>(),
);

export const adjustDefinitionDirectoryFormEditor = createAction('' +
  '[cxm-directory / adjust definition directory form]',
  props<{ definitionDirectoryFormEditor: any }>(),
);
