import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ManageDefinitionDirectoryReducerKey } from './manage-directory.reducer';
import {DefinitionDirectoryStateType} from "@cxm-smartflow/definition-directory/data-access";

const ManageDefinitionDirectorySelectorState = createFeatureSelector<DefinitionDirectoryStateType>(ManageDefinitionDirectoryReducerKey);

export const selectDefinitionDirectoryAllStates = createSelector(ManageDefinitionDirectorySelectorState, state => state);


// List directories.
export const selectDefinitionDirectoryTableDatasource = createSelector(
  ManageDefinitionDirectorySelectorState,
  (state) => state.listDefinitionDirectoryResponse
);
export const selectDefinitionDirectoryTableCriteria = createSelector(ManageDefinitionDirectorySelectorState, state => state.listFilteringCriteria);

// Form Directory.
export const selectDefinitionDirectoryFormHasChanged = createSelector(
  ManageDefinitionDirectorySelectorState,
  (state) => state.formHasChange
);

export const selectDefinitionDirectoryForm = createSelector(
  ManageDefinitionDirectorySelectorState,
  (state) => state.definitionDirectoryForm
);

export const selectDefinitionDirectoryFormEditor = createSelector(
  ManageDefinitionDirectorySelectorState,
  (state) => {
    if (Object.keys(state.definitionDirectoryFormEditor).length === 0) {
      return JSON.parse(localStorage.getItem('definitionDirectoryFormEditor') || '{}');
    }

    return state.definitionDirectoryFormEditor;
  }
);

export const selectDefinitionDirectoryOldName = createSelector(
  ManageDefinitionDirectorySelectorState,
  (state) => state.oldName
);
