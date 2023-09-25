import { createAction, props } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { ResourceCriteriaResponse } from '../../model/resource-criteria.response';
import { ResourceResponseList } from '../../model';
import { ResourceLibraryResponse } from '../../model/resource-library.response';

// Fetch resources actions.
export const fetchResources = createAction('[ cxm-setting / fetch resource]');
export const fetchResourcesSuccess = createAction('[ cxm-setting / fetch resource success]', props<{ resources: ResourceResponseList }>());
export const fetchResourcesFail = createAction('[ cxm-setting / fetch resource fails]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Fetch resource type criteria.
export const fetchListResourceCriteria = createAction('[cxm-setting / fetch list resource criteria]');
export const fetchListResourceCriteriaSuccess = createAction('[cxm-setting / fetch list resource criteria success]', props<{ resourceCriteria: ResourceCriteriaResponse[] }>());
export const fetchListResourceCriteriaFail = createAction('[cxm-setting / fetch list resource criteria fail]', props<{ httpErrorResponse: HttpErrorResponse }>());


// Manage all actions in the list of resource.
export const filterTypeBoxChange = createAction('[cxm-setting / filter by types change]', props<{ types: string[]}>());
export const searchBoxChange = createAction('[cxm-setting / filter by search box change]', props<{ filter: string}>());
export const tableSortChange = createAction('[cxm-setting / table sort change]', props<{ sortDirection: string, sortByField: string}>());
export const paginationChange = createAction('[cxm-setting / pagination change]', props<{ page: number, pageSize: number }>());

// Get translation massage.
export const getTranslationMsg = createAction('[cxm-setting / get translation message]');
export const getTranslationMsgSuccess = createAction('[cxm-setting / get translation message success]', props<{ message: any }>());

// Manage creating resource.
export const fileUploadChange = createAction('[cxm-setting / validate resource form change]', props<{ files: File[], resourceType: string}>());
export const resourcePopupValueChange = createAction('[cxm-setting / resource popup value change]', props<{ label: string, resourceType: string}>());
export const resetCreationForm = createAction('[cxm-setting / reset creation form]');
export const resetUploadFile = createAction('[cxm-setting / reset upload file]');
export const validateCreateResourceForm = createAction('[cxm-setting / validate create resource form]', props<{ formType: 'create' }>());
export const cancelResourceForm = createAction('[cxm-setting / cancel resource form]');
export const createResource = createAction('[cxm-setting / create resource]');
export const createResourceSuccess = createAction('[cxm-setting / create resource success]');
export const createResourceFail = createAction('[cxm-setting / create resource fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Upload resource file.
export const uploadResourceFile = createAction('[cxm-setting / upload resource file]', props<{resourceType: string}>());
export const cancelUploadResourceFile = createAction('[cxm-setting / cancel upload resource file]');
export const uploadResourceFileInProgress = createAction('[cxm-setting / upload resource file in progress]', props<{ response: any }>());
export const uploadResourceFileSuccess = createAction('[cxm-setting / upload resource file success]', props<{ resourceLibraryResponse: ResourceLibraryResponse }>());
export const uploadResourceFileFail = createAction('[cxm-setting / upload resource file fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Check duplicated label.
export const checkDuplicateLabel = createAction('[cxm-setting / check duplicated label]', props<{ name: string, resourceType: string }>());
export const checkDuplicateLabelSuccess = createAction('[cxm-setting / check duplicated label success]', props<{ isLabelDuplicate: boolean }>());
export const checkDuplicateLabelFails = createAction('[cxm-setting / check duplicated label fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Manage deletion.
export const deleteResourceOrTemptFile = createAction('[cxm-setting / delete resource or temporary file]', props<{ deleteType: 'temp' | 'resource' }>());
export const deleteResourceSuccess = createAction('[cxm-setting / delete resource file success]');
export const deleteTemptFileSuccess = createAction('[cxm-setting / delete temporary file success]');
export const cancelDeleteTemptFile = createAction('[cxm-setting / cancel delete temporary file success]');
export const deleteResourceOrTemptFileFail = createAction('[cxm-setting / delete resource or temporary file fail]', props<{ httpErrorResponse: HttpErrorResponse }>());


// Manage confirm delete resource.
export const attemptToDeleteResource = createAction('[cxm-setting / attempt to delete resource]', props<{ fileId: string  }>());

// Manage download file.
export const downloadResourceFile = createAction('[cxm-setting / download resource file]', props<{ fileId: string, fileName: string }>());
export const downloadResourceFileSuccess = createAction('[cxm-setting / download resource file success]', props<{ byteCode: any, fileName: string }>());
export const downloadResourceFileFail = createAction('[cxm-setting / download resource file fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Clear state.
export const clearAllStatesInResource = createAction('[cxm-setting / clear all state in resource]');

export const getTechnicalName = createAction('[cxm-setting / get technical name]', props<{ fileId: string }>());
export const copyClipboardTechnicalNameSuccess = createAction('[cxm-setting / copy clipboard technical name success]', props<{ file: any }>());
export const getTechnicalNameFail = createAction('[cxm-setting / get Technical name fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
