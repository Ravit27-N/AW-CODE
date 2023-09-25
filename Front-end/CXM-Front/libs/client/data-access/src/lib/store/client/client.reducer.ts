import { createReducer, on } from '@ngrx/store';
import * as fromActions from './client.actions';

export const clientFeatureKey = 'feature-parameter-client-key';

const initialState: {
  clients: Array<any>,
  pagination: { page: number, pageSize: number, total: 0 },
  filters: { sortByField: string, sortDirection: 'asc'|'desc'|string }
} = {
  clients: [],
  pagination: { page: 1, pageSize: 10, total: 0 },
  filters: { sortByField: "lastModified", sortDirection: 'desc' }
}

export const filterHistory = 'filterHistory';


export const reducers = createReducer(initialState,
  on(fromActions.destroyClientModule, (state) => ({ ...initialState })),
  on(fromActions.initailizeClientModule, (state) => ({ ...state })),
  on(fromActions.loadClientListSuccess, (state, props) => ({ ...state, clients: props.clients, pagination: props.pagination }))
)
