import { createAction, createFeatureSelector, createReducer, createSelector, on, props } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';


export const FORGOT_PASSWORD_FEATURE_KEY = 'forgot_password_key';

export const requestChangePassword = createAction('[forgot password / request change]', props<{ email: string }>());

export const requesetResetPassword = createAction('[forgot password /  request request passoword]', props<{ password: string }>());

export const requestResetPasswordFail = createAction('[forgot password / request reset password fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

export const initialForgotPassword = createAction('[forgot password] / init', props<{ mode: number, token?: string, nonExpired?: boolean }>());

export const errorForgotResult = createAction('[forgot password] / error', props<{ error: any }>());

export const forgotPasswordSuccess = createAction('[forgot password] / success', props<{ successMode: number }>());

export const unloadForgotForm = createAction('[forgot password /  unload]');

export const validateToken = createAction('[auth / validate token]', props<{ token: string }>());

export const validateTokenSuccess = createAction('[auth / validate token success]', props<{ isExpired: boolean, token: string }>());

export const validateTokenFail = createAction('[auth / validate token fail]', props<{ httpErrorResponse: HttpErrorResponse }>())

export const selectForgotState = createSelector(createFeatureSelector(FORGOT_PASSWORD_FEATURE_KEY), state => state);

export const clearErrorForgotState = createAction('[auth/ clear error forgot pw]');




const initialData = {
  mode: 0, // 0: request change, 1: request reset
  token: '',
  error: null,
  successMode: 0,
}

export const reducer = createReducer(initialData,

  // on(requestChangePassword, (state, props) => ({ ...state, }))
  on(forgotPasswordSuccess, (state, { successMode }) => ({ ...state, successMode  })),
  on(initialForgotPassword, (state, { mode,token, nonExpired }) => ({ ...state, mode, nonExpired: nonExpired ?? false, token: token ?? ''  })),
  on(unloadForgotForm, (state) => ({ ...initialData })),
  on(errorForgotResult, (state, props) => {
    const { error } = props;
    if(error.status === 404) {
      return { ...state, error  };
     } else if(error.status === 400) {
      return { ...state, error  };
     }

    return { ...state,  }
  }),
  on(clearErrorForgotState, (state) => ({ ...state, error: null }))

)

