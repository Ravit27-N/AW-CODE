import { createAction, props } from '@ngrx/store';


export const loadProfileForm = createAction(
  '[manage profile form / load]',
  props<{ id?: string }>()
);


export const loadProfileFormSuccess = createAction(
  '[manage profile form / load success]',
  props<{ perm: any, nameForm: any, keepTotalCount?: number, keepOldProfileData: any, createdBy?: string, targetPermission: any, ownerId?: number }>()
);

export const loadProfileFormFail = createAction(
  '[manage profile form / load fail]'
);

export const unloadProfileForm = createAction(
  '[manage profile form / unload]'
);

export const submitProfile = createAction(
  '[manage profile form / submit]',
  props<{ id?: string, perm: any, name: string, displayName: string, clientId: number }>()
);

export const submitProfileFail = createAction(
  '[manage profile form /  submit fail]'
);
export const End = createAction('[manage profile end flow]');

export const submitProfileSuccess = createAction(
  '[manage profile form / submit success]'
);

export const updateFormName = createAction(
  '[manage profile form / name patch]',
  props<{ form: any }>()
);

export const updatePermission = createAction(
  '[manage profile form / perm update]',
  props<{ perm: any }>()
);

export const validateForm = createAction(
  '[manage profile form / validate]',
  props<{ perm: any, name: string, displayName: string }>()
);

export const validateProfileName = createAction(
  '[manage profile form / validate profile name]',
  props<{ name: string }>()
);


export const loadClientModule = createAction('[mange profile / load client module]', props<{ profileId?: any, clientId?: any, }>());
export const loadClientModuleSuccess = createAction('[mange profile / load client module succss]', props<{ module: any[], form:any, creation: boolean }>());
export const loadClientModuleFail = createAction('[mange profile / load client module fail]');
export const unloadClientModule = createAction('[mange profile / unload client module]');

export const setLoadingFormComplete = createAction('[mange profile / completed load form]');
