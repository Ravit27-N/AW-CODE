import { createAction, props } from '@ngrx/store';
import { ListFlowApprovalReposne, RemainingShipmentModel } from '../../models';

export const openApprovalPanel = createAction('[cxm approval /  open panel]');
export const closeApprovalPanel = createAction('[cxm approval / close panel]');
export const setAppprovalPanel = createAction('[cxm approval / set panel]', props<{ active: boolean }>());

export const submitApprove = createAction('[cxm approval / submit approve]', props< { flows: any[], comment: string } >());
export const submitRefuse = createAction('[cxm approval / submit refuse', props< { flows: any[], comment: string } >());
export const submitValidateSuccess = createAction('[cxm approval / submit success]',  props<{ response: any, message: string }>());
export const submitValidateFail = createAction('[cxm approval / submit fail]', props<{ httpError: any }>());


export const loadFlowApproveList = createAction('[cxm approval / load approve list]', props<{ filters: any }>());
export const loadFlowAppproveListSuccess = createAction('[cxm approval / load approve list success]', props<{ response: ListFlowApprovalReposne  }>());
export const loadFlowApproveListFail = createAction('[cxm approval / load approve list fail]');

export const filterFlowApproveChanged = createAction('[cxm approval / list filter changed]', props<{ filters: { page?: number, pageSize?: number, filter?: string,
  sortBy?: string, sortDirection?: string, users?: [],  categories: [], end: any, start: any } }>());

export const unloadApprove = createAction('[cxm approval / unload approve state ]');

export const loadCriteriaFilter = createAction('[cxm approval / load filter criteria]');
export const loadCriteriaFilterSuccess = createAction('[cxm approval / load filter criteria success]', props<{ categories: any[], users: any[], dates: RemainingShipmentModel }>());
export const loaCriteriaFail = createAction('[cxm approval / load filter criteria fail]');
