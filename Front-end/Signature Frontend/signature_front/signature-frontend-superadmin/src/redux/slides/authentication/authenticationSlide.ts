import {Participant, tokenRoleName} from '@/constant/NGContant';
import {createSlice} from '@reduxjs/toolkit';
import {IAction, IAuthentication, IProjectDetailActions} from './type';

const initialState: IAuthentication = {
  user: {
    username: '',
  },
  files: [],
  tempFiles: [],
  signatories: [],
  approvals: [],
  viewers: [],
  recipients: [],
  role: null,
  isLoading: true,
  isSignout: false,
  userToken: null,
  sid: null,
  C_UUID: null,
  USER_COMPANY: null,
  invitation: {
    inviter: null ?? 'Cristina Games',
    receiver: null ?? 'You',
    timeDelivery: null ?? Date.now(),
    documents: null ?? 2,
    projectName: null ?? 'Crédit immobilier ~ Paris Xvit',
  },
  annotations: [],
  project: {
    id: null,
    name: null,
    orderSign: false,
    orderApprove: false,
    template: null,
  },
  selectEnvoiData: null,
  activeActorEnvoi: null,
  projectDetailActions: {
    'modified-date': false,
  },
  createModel: null,
};
/***
export const refreshTokenAsync = () => {
  return async (dispatch: any) => {
    const value = JSON.parse(localStorage.getItem(tokenRoleName)!);
    if (!value) {
      return;
    } else {
      dispatch(refreshToken({value}));
    }
  };
};
*/
export const authSlice = createSlice({
  name: 'authentication',
  initialState,
  reducers: {
    projectDetailAction: (
      state,
      {payload}: {payload: {[k in IProjectDetailActions]: boolean}},
    ) => {
      state.projectDetailActions = payload;
    },
    /*** Set active signatory */
    setActiveActorRole: (
      state,
      action: {
        payload: {
          id: number;
          signatoryName: string;
          role:
            | Participant.Approval
            | Participant.Signatory
            | Participant.Receipt
            | Participant.Viewer;
        };
      },
    ) => {
      const {id, role, signatoryName} = action.payload;
      return {
        ...state,
        activeActorEnvoi: {
          id,
          role,
          signatoryName,
        },
      };
    },

    /*** Update envoi data */
    updateEnvoiByRole: (
      state,
      action: {
        payload: {
          role:
            | Participant.Approval
            | Participant.Signatory
            | Participant.Receipt
            | Participant.Viewer;
          data: {
            id: number | null;
            name: string;
            title: string;
            description: string;
            expired: Date;
          };
        };
      },
    ) => {
      const {data, role} = action.payload;
      state.selectEnvoiData = {...state.selectEnvoiData, [role]: data};
    },

    /*** Sign out clear token **/
    signOut: state => {
      localStorage.removeItem(tokenRoleName);
      return {...state, userToken: null, role: null};
    },

    /*** Refresh store token **/
    refreshToken: (state, action: IAction) => {
      const {token, role, name, sid} = action.payload.value;
      return {
        ...state,
        userToken: token,
        role: role as string,
        user: {username: name},
        sid,
      };
    },

    /***
     * it's used to store C UUID corporateID, CompanyInfo
     * */
    storeUserCompanyInfo: (state, action: IAction) => {
      const {C_UUID, USER_COMPANY} = action.payload.value;
      return {
        ...state,
        C_UUID,
        USER_COMPANY,
      };
    },
  },
});

export const {
  signOut,
  refreshToken,
  storeUserCompanyInfo,
  updateEnvoiByRole,
  setActiveActorRole,
  projectDetailAction,
} = authSlice.actions;
export default authSlice.reducer;
