import { createAction, props } from '@ngrx/store';

export const loadFeedForm = createAction('[directory feed form] / load', props<{ directoryId: string }>());

export const loadFeedFormSuccess = createAction('[directory feed form / load success]', props<{ fields: any, columns: any, directoryId: string, directoryName: string }>());

export const loadFeedFormFail = createAction('[directory feed form ]', props<{ error: any }>());

export const unloadFeedForm = createAction('[directory feed form/unload]');

export const loadFeedData = createAction('[directory feed form/load data]', props<{ directoryId: string, page?: string, pageSize?: string }>());

export const loadFeedDataSuccess = createAction('[directory feed form/load data success]', props<{  content: Array<any>, page: number, pageSize: number, totoal: number }>());

export const loadFeedDataFail = createAction('[directory feed form/load data fail]', props<{ error: any }>());

export const selectRowFeed = createAction('[directory feed form/ select]', props<{ row: any }>());

export const createRow = createAction('[directory feed form]/ add');

export const removeSelectedRow = createAction('[directory feed form/ remove selected]', props<{ row: any }>());

export const removeAllRow = createAction('[directory feed form/remove all]');

export const changeCellValue = createAction('[directory feed form/ change cell]', props<{ row: any, order: number, value: string}>())

export const exportDirectorySchema = createAction('[directory feed form/ schemas export]');

export const importDirectoryData = createAction('[directory feed form/ data import]', props<{ json?: Array<any>, form?: any , filename: string}>());

export const exportDirectoryData = createAction('[directory feed form/ data export]');

export const attempToSubmitFeedDirectory = createAction('[directory feed form/ attemp submit]');

export const submitFeedDirectory = createAction('[directory feed form/ submit]');

export const attempToUploadCsv = createAction('[directory feed form/ attemp csv upload]', props<{ schemas: any, form: any, filename: string }>());

export const uploadCsv = createAction('[directory feed form/ csv upload]', props<{ schemas: any, form: any, filename: string }>());

export const uploadCsvError = createAction('[directory feed form / csv upload error]', props<{error: any, filename:string }>() );

export const filterChanged = createAction('[directory feed form/ filter changed]', props<{ page: any, pageSize: any }>());
