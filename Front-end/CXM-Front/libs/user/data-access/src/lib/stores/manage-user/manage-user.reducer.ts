import {createReducer, on} from '@ngrx/store';
import * as fromManageUserAction from './manage-user.action';

export const initialState: {
  isSubmit: boolean,
  allprofiles: any[],
  pageSize: number,
  page: number,
  selectReturnAddressLevel: string | number
} = {
  isSubmit: false,
  allprofiles: [],
  pageSize: 15,
  page: 0,
  selectReturnAddressLevel:""
};

export const manageUserKey = 'manage-user-key';

export const manageUserReducer = createReducer(
  initialState,
  on(fromManageUserAction.submitUserForm, (state, props) => ({
    ...state,
    ...props
  })),
  on(fromManageUserAction.unloadFormUser, (state) => ({
    ...initialState
  })),
  on(fromManageUserAction.loadProfileListSuccess, (state, props) => {

    if (props.page != state.page) {
      return {
        ...state,
        page: props.page,
        pageSize: props.pageSize,
        allprofiles: [...state.allprofiles, ...props.allprofiles]
      };
    }

    return {
      ...state,
      ...props
    };
  }),
  on(fromManageUserAction.entryUserForm, (state, props) => ({
    ...state,
    ...props
  })),
  on(fromManageUserAction.getAllProfileByServiceIdSuccess, (state, props) => ({
    ...state,
    allprofiles: props.profiles
  })),
  on(fromManageUserAction.clearProfiles, (state, props) => ({
    ...state,
    allprofiles: []
  })),
  on(fromManageUserAction.switchReturnAddressLevel, (state, props) => {
    return {...state, selectReturnAddressLevel: props.returnAddressLevel};
  }),
);
