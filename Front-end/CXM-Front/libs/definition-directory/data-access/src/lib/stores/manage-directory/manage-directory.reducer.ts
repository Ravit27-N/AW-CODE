import { createReducer, on } from '@ngrx/store';
import * as fromActions$ from './manage-directory.action';
import { DefinitionDirectoryStateType } from "../../models";

export const ManageDefinitionDirectoryReducerKey =
  'manage-definition-directory-reducer-key';

const initialManageDirectoryState: DefinitionDirectoryStateType = {
  // List.
  listFilteringCriteria: {
    page: 1,
    pageSize: 10,
    sortByField: 'lastModified',
    sortDirection: 'desc',
  },
  listDefinitionDirectoryResponse: {
    contents: [],
    page: 1,
    pageSize: 10,
    total: 0
  },
  definitionDirectoryBeforeModify: {
    id: 0,
    name: '',
    displayName: '',
    clients: [],
    directoryFields: [],
    createdAt: undefined,
    createdBy: undefined,
    lastModified: undefined,
  },
  definitionDirectoryForm: {
    id: 0,
    name: '',
    displayName: '',
    clients: [],
    directoryFields: [],
    createdAt: undefined,
    createdBy: undefined,
    lastModified: undefined,
    hasFeeding: false
  },
  formHasChange: false,
  definitionDirectoryFormEditor: {},
  oldName: '',
};

export const ManageDefinitionDirectoryReducer = createReducer(
  initialManageDirectoryState,
  on(fromActions$.fetchDirectoryDefinition, (state, props) => {
    return { ...state, listFilteringCriteria: { ...props } };
  }),
  on(fromActions$.fetchDirectoryDefinitionSuccess, (state, props) => {
    return { ...state, listDefinitionDirectoryResponse: props.listDefinitionDirectoryResponse };
  }),
  on(fromActions$.adjustFormStep1, (state, props) => {
    return {
      ...state,
      definitionDirectoryForm:
        {
          ...state.definitionDirectoryForm,
          name: props.formStepOne.name,
          displayName: props.formStepOne.displayName
        }
    };
  }),
  on(fromActions$.updateFormChange, (state, props) => {
    return { ...state, formHasChange: props.hasChange };
  }),
  on(fromActions$.adjustDirectoryFields, (state, props) => {
    return {
      ...state,
      definitionDirectoryForm:
        {
          ...state.definitionDirectoryForm,
          directoryFields: props.directoryFields,
        }
    };
  }),
  on(fromActions$.addClientId, (state, props) => {
    return {
      ...state,
      definitionDirectoryForm:
        {
          ...state.definitionDirectoryForm,
          clients: [...state.definitionDirectoryForm.clients || [], props.clientId],
        }
    };
  }),
  on(fromActions$.removeClientId, (state, props) => {
    return {
      ...state,
      definitionDirectoryForm:
        {
          ...state.definitionDirectoryForm,
          clients: [...state.definitionDirectoryForm.clients || []].filter(item => item !== props.clientId),
        }
    };
  }),
  on(fromActions$.submitForm, (state, props) => {
    return { ...state, formHasChange: false };
  }),
  on(fromActions$.unloadDefinitionDirectoryForm, (state, props) => {
    return { ...initialManageDirectoryState };
  }),
  on(fromActions$.getDefinitionDirectoryDetailSuccess, (state, props) => {
    const definitionDirectoryForm = JSON.parse(localStorage.getItem('definitionDirectoryForm') || '{}');
    if (Object.keys(definitionDirectoryForm).length > 0) {
      return { ...state, definitionDirectoryBeforeModify: props.directoryDefinitionForm, definitionDirectoryForm: definitionDirectoryForm.definitionDirectoryForm, oldName: props.directoryDefinitionForm.name || '' };
    }

    return { ...state, definitionDirectoryForm: props.directoryDefinitionForm, definitionDirectoryBeforeModify: props.directoryDefinitionForm, oldName: props.directoryDefinitionForm.name || '' };
  }),
  on(fromActions$.setupHistoryForm, (state, props) => {
    return { ...state, definitionDirectoryForm: props.definitionDirectoryForm };
  }),
  on(fromActions$.adjustDefinitionDirectoryFormEditor, (state, props) => {
    localStorage.setItem('definitionDirectoryFormEditor', JSON.stringify(props.definitionDirectoryFormEditor));
    return { ...state, definitionDirectoryFormEditor: props.definitionDirectoryFormEditor };
  }),
);
