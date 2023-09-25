import { createAction, props } from '@ngrx/store';

export const initailizeClientModule = createAction(
  '[cxm client / init client]'
);
export const destroyClientModule = createAction(
  '[cxm client / destroy client]'
);

export const loadClientList = createAction(
  '[cxm client / load client list]',
  props<{ filters: any }>()
);
export const loadClientListSuccess = createAction(
  '[cxm client / load client list success]',
  props<{ clients: any[]; pagination: any }>()
);
export const loadClientListFail = createAction(
  '[cxm client / load client list fail]',
  props<{ httpError: any }>()
);

export const attempToDeleteClient = createAction(
  '[cxm client / attempt to delete client]',
  props<{ id: number }>()
);

export const deleteClient = createAction(
  '[cxm client / delete client]',
  props<{ id: number }>()
);
export const deleteClientSuccess = createAction(
  '[cxm client / delete client success]'
);
export const deleteClientFail = createAction(
  '[cxm client / delete client fail]'
);

export const filterChanged = createAction(
  '[cxm client / filter changed]',
  props<{
    page?: number;
    pageSize?: number;
    sortByField?: string;
    sortDirection?: 'asc' | 'desc' | string;
  }>()
);
