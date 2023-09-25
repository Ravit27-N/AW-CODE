import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { createReducer, on } from '@ngrx/store';
import { PREDEFINE_PERMISSION_FORM } from '../../models';
import * as manageProfileAction from './manage-profile.action';

export const featureManageProfileFormKey = 'feature-manage-profile-form';

const initailState = {
  // profileId:  undefined,
  perms: [...PREDEFINE_PERMISSION_FORM],
  nameForm: {
    name: '',
    displayName: ''
  },
  keepTotalCount: 0,
  keepOldProfileData: {
    name: '',
    displayName: '',
    privileges: []
  },
  client: {
    module: [],
    form: []
  },
  loadingComplete: false
};

const subfeatureMapper = (func: any, privileges: any) => {
  return Array.from(func).map((x: any) => {
    const found = Array.from(privileges).find((y: any) => y.code === x.code) as any;
    return (found) ? { ...x, checked: true, ...found } : x;
  });

};

export const manageProfileFormReducer = createReducer(initailState,

  on(manageProfileAction.loadProfileForm, (state, props) => ({ ...state, ...initailState, profileId: props.id, client: { form: state.client.form, module: state.client.module } })),
  on(manageProfileAction.unloadProfileForm, (state) => ({ ...initailState, client: { form: state.client.form, module: state.client.module } })),
  on(manageProfileAction.loadProfileFormSuccess, (state, props) => {
    const perms = Array.from(PREDEFINE_PERMISSION_FORM).map((x: any) => {
      const found = Array.from(props.perm).find((y: any) => y.code === x.code) as any;
      return (found) ? { ...x, checked: true, ...found, func: subfeatureMapper(x.func, found.privileges) } : x;
    });

    const { targetPermission } = props;
    const form = UserUtil.aggregateProfileFormBaseOnUser(state.client.form, targetPermission) as any;

    return { ...state,
      nameForm: props.nameForm,
      perms: perms, keepTotalCount:
        props.keepTotalCount || 0,
      keepOldProfileData: props.keepOldProfileData,
      createdBy: props.createdBy,
      ownerId: props.ownerId,
      client: { module: state.client.module, form },
      loadingComplete: true
    };
  }),
  on(manageProfileAction.loadClientModuleSuccess, (state: any, props: any) => {
    const { module, form } = props;
    return { ...state, client: { module, form } }
  }),
  on(manageProfileAction.loadClientModuleFail, (state, props) => ({ ...initailState })),
  on(manageProfileAction.unloadClientModule, (state) => ({ ...initailState })),
  on(manageProfileAction.setLoadingFormComplete, (state: any, props: any) => {
    return { ...state, loadingComplete: true }
  })
);




