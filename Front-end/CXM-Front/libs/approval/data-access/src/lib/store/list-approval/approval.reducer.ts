import { createReducer, on } from '@ngrx/store';
import * as fromActions from './approval.action';
import {
  ListFlowApprovalReposne,
  RemainingShipmentModel,
} from '../../models';

export const featureApprovalKey = 'feature-approval-key';

export interface FilterCriteriaState {
  categories: any[];
  users: any[];
  dates: RemainingShipmentModel;
  isLoaded: boolean
}

const initialState: {
  open: boolean;
  filters: {
    page: number;
    pageSize: number;
    sortDirection?: 'asc' | 'desc';
    filter?: string;
    sortByField: string;
  };
  approveList: ListFlowApprovalReposne;
  criteria: FilterCriteriaState;
} = {
  open: false,
  approveList: {
    contents: [],
    page: 1,
    pageSize: 10,
    total: 0,
  },
  filters: {
    page: 1,
    pageSize: 10,
    filter: '',
    sortByField: 'createdAt',
    sortDirection: 'desc',
  },
  criteria: {
    isLoaded: false,
    categories: [],
    users: [],
    dates: { startDate: '', endDate: '', total: 0 },
  },
};


export const approvalReducer = createReducer(initialState,
  on(fromActions.openApprovalPanel, (state) => ({...state, open: true })),
  on(fromActions.closeApprovalPanel, (state) => ({ ...state, open: false })),
  on(fromActions.setAppprovalPanel, (state, props) => ({ ...state, open: props.active  })),
  on(fromActions.loadFlowAppproveListSuccess, (state, { response }) => {
    return { ...state,
      approveList: {
        contents: response.contents,
        page: response.page, pageSize: response.pageSize,
        total: response.total
      }
    }
  }),
  on(fromActions.unloadApprove, (state) => ({ ...initialState })),
  on(fromActions.loadFlowApproveList, (state, props) => ({ ...state, filters: props.filters })),
  on(fromActions.loadCriteriaFilterSuccess, (state, props) => ({ ...state, criteria: { categories: props.categories, users: props.users, dates: props.dates, isLoaded: true } }))
)
