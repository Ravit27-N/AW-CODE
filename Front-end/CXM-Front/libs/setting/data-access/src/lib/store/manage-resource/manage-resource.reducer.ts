import { createReducer, on } from '@ngrx/store';
import * as fromActions from './manage-resource.action';
import { ManageResourceInitialState } from '../../model/manage-resource-initial-state';
import { FileUtils, Sort } from '@cxm-smartflow/shared/utils';
import { checkDuplicateLabel, clearAllStatesInResource } from './manage-resource.action';

export const manageResourceReducerKey = 'manage-resource-reducer-key';

const initialResource: ManageResourceInitialState = {
  resources: [],
  form: {
    fileId: '',
    label: '',
    type: '',
    fileName: '',
    pageNumber: 0,
    fileSize: 0,
  },
  listCriteria: {
    filter: '',
    sortDirection: Sort.DESC,
    sortByField: 'createdAt',
    page: 1,
    pageSize: 10,
    length: 0,
    types: []
  },
  isHasFilter: false,
  isSearchBoxError: false,
  resourceCriteria: [],
  messages: {},
  popupForm: {
    label: '',
    resourceType: '',
    fileSize: '',
    fileName: '',
    fileId: '',
    pageNumber: 0,
    fileSizePayload: 0,
    files: [],
    isLabelDuplicate: false,
    isLabelIsChecking: false,
  },
  isCloseModal: false,
  deleteFileId: '',
};

export const manageResourceReducer = createReducer(
  initialResource,
  on(fromActions.fetchResourcesSuccess, (state, props) => {
    const isSearchBoxError = props.resources.total === 0 && state.listCriteria.filter.length > 0;
    return {
      ...state,
      resources: props.resources.contents,
      isSearchBoxError,
      listCriteria: {...state.listCriteria, length: props.resources.total },
    };
  }),
  on(fromActions.searchBoxChange, (state, props) => {
    const criteria: any = {...state.listCriteria, filter: props.filter, page: 1 };
    const isHasFilter = state.listCriteria.types.length > 0 || props.filter.length > 0;
    return { ...state, listCriteria: criteria, isHasFilter };
  }),
  on(fromActions.filterTypeBoxChange, (state, props) => {
    const criteria: any = {...state.listCriteria, types: props.types, page: 1 };
    const isHasFilter = state.listCriteria.filter.length > 0 || props.types.length > 0;
    return { ...state, listCriteria: criteria, isHasFilter };
  }),
  on(fromActions.tableSortChange, (state, props) => {
    const criteria: any = {...state.listCriteria, sortDirection: props.sortDirection, sortByField: props.sortByField };
    return {...state, listCriteria: criteria}
  }),
  on(fromActions.paginationChange, (state, props) => {
    const criteria: any = {...state.listCriteria, page: props.page, pageSize: props.pageSize };
    return {...state, listCriteria: criteria}
  }),
  on(fromActions.fetchListResourceCriteriaSuccess, (state, props) => {
    return {...state, resourceCriteria: props.resourceCriteria };
  }),
  on(fromActions.getTranslationMsgSuccess, (state, props) => {
    return {...state, messages: props.message };
  }),
  on(fromActions.resourcePopupValueChange, (state, props) => {
    const { resourceType, label } = props;
    return {...state, popupForm: {...state.popupForm, resourceType, label, isLabelDuplicate: state.popupForm.isLabelDuplicate && label?.trim().length > 0} };
  }),
  on(fromActions.resetCreationForm, (state) => {
    return {...state, popupForm: initialResource.popupForm, isCloseModal: false };
  }),
  on(fromActions.fileUploadChange, (state, props) => {
    return {...state, popupForm: {...state.popupForm,  files: props.files }};
  }),
  on(fromActions.uploadResourceFileSuccess, (state, props) => {
    const { fileSize, fileId, pageNumber, fileName } = props.resourceLibraryResponse;
    const fs = FileUtils.convertToKB(`${fileSize}B`);

    return {...state, popupForm: {...state.popupForm, fileId, fileSize: FileUtils.getLimitSize(`${fs}KB`), fileSizePayload: fileSize, pageNumber, fileName }, deleteFileId: props.resourceLibraryResponse.fileId };
  }),
  on(fromActions.resetUploadFile, (state) => {
    return {...state, popupForm: {...state.popupForm, fileId: '', fileName: '', pageNumber: 0, fileSize: '', fileSizePayload: 0, files: [] }}
  }),
  on(fromActions.checkDuplicateLabelSuccess, (state, props) => {
    return {...state, popupForm: {...state.popupForm, isLabelDuplicate: props.isLabelDuplicate, isLabelIsChecking: false }}
  }),
  on(fromActions.createResourceSuccess, (state, props) => {
    return {...state, isCloseModal: true }
  }),
  on(fromActions.attemptToDeleteResource, (state, props) => {
    return {...state, deleteFileId: props.fileId }
  }),
  on(fromActions.deleteResourceSuccess, (state, props) => {
    return {...state, deleteFileId: '' }
  }),
  on(fromActions.deleteTemptFileSuccess, (state, props) => {
    return {...state, deleteFileId: '' }
  }),
  on(fromActions.clearAllStatesInResource, (state) => {
    return {...initialResource };
  }),
  on(fromActions.checkDuplicateLabel, (state) => {
    return {...state, popupForm: { ...state.popupForm, isLabelIsChecking: true }}
  }),
  on(fromActions.getTechnicalName, (state, props) => {
    return {...state, clipboardFileId: props.fileId }
  }),
  on(fromActions.copyClipboardTechnicalNameSuccess, (state, props) => {
    return {...state, fileId: props.file }
  })

);
