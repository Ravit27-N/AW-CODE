import { createAction, props } from '@ngrx/store';
import {FlowDocumentList, FlowFilterCriteriaParams, getDateRangeLast7Days,CsvSuiviData} from '../../models';
export const loadDocumentTraceabilityList = createAction(
  '[flow document traceability list/ load]',
  props<{
    flowTraceabilityId?: number;
    page: number;
    pageSize: number;
    params?: FlowFilterCriteriaParams;
    isLoading?: boolean;
  }>()
);

export const loadDocumentTraceabilityListSuccess = createAction(
  '[flow document traceability list/ load success]',
  props<{
    flowTraceabilityId?: number,
    response: FlowDocumentList,
    isLoading?: boolean,
    params?: FlowFilterCriteriaParams
  }>()
);

export const loadDocumentTraceabilityListFail = createAction(
  '[flow document traceability list/ load fail]'
);

export const unloadDocumentTraceabilityList = createAction(
  '[flow document traceability list/ unload]'
);

export const documentTraceabilityListFilterChangeAction = createAction(
  '[flow document traceability list/ filter changed]',
  props<{
    flowTraceabilityId: number,
    page ?: number,
    pageSize ?: number,
    params?: FlowFilterCriteriaParams,
    isLoading?: boolean
  }>()
);

const dateRange = getDateRangeLast7Days(true);
export const defaultValue = {
  page: 1,
  pageSize: 10,
  flowTraceabilityId: 0,
  params: {
    ...dateRange,
    sortByField: 'dateStatus',
    sortDirection: 'desc',
    channels: [],
  } as FlowFilterCriteriaParams,
};


export const defaultFilterOnViewShipment = {
  page: 1,
  pageSize: 10,
  params: {
    sortByField: 'dateStatus',
    sortDirection: 'desc',
    channels: [],
  } as FlowFilterCriteriaParams,
};

export const downloadDocDocumentTraceabilityAction = createAction(
  '[flow document traceability/download document]',
  props<{flow: any, _type: string}>()
)

export const openDocumentTraceabilityHistoryDialog = createAction(
'[flow document traceability/open document history dialog]',
props<{ flow : any}>()
)

export const navigateToDetailAction = createAction(
  '[flow document traceability/navigate doc detail]',
  props<{ flow: any }>()
);

export const navigateToPreviousUrl = createAction(
  '[flow document traceability] / navigate to list flow traceability',
  props<{ isBackToFlow?: boolean }>()
);
export const exportSuivi = createAction('[flow document traceability / export  suivi ]', props<{ services: { data: CsvSuiviData }[] }>());
export const exportSuiviSuccess = createAction('[flow document traceability / export suivi success] Export suivi Success',props<{ response: any }>());

export const exportSuiviFailure = createAction('[flow document traceability / export suivi fail] Export Suivi Failure',props<{ error: any }>());