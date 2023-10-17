import {identitySlide} from '@/redux/slides/advance-signature/identity/identity';
import authtenticationSlide from '@/redux/slides/authentication/authenticationSlide';
import {corporateSettingSlide} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {corporateUserSlide} from '@/redux/slides/corporate-admin/corporateUserSlide';
import enterpriseSlice from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {endUserProfileSlide} from '@/redux/slides/end-user/profileSlide';
import {processControlNotificationSlide} from '@/redux/slides/process-control';
import {
  PreloadedState,
  combineReducers,
  configureStore,
} from '@reduxjs/toolkit';
import counterReducer from './counter/CounterSlice';
import {companyAPI} from './slides/company/query/companyRTK';
import {corporateSettingPublicSlide} from './slides/corporate-admin/corporate-public/corporateSettigPublicSlide';
import {userSlide} from './slides/keycloak/user';
import {processControlSlide} from './slides/process-control/internal/processControlSlide';
import {participantSlide} from './slides/process-control/participant';
import {templateSlice} from './slides/profile/template/templateSlide';
import {projectSlide} from './slides/project-management/project';
import {signatorySlice} from './slides/project-management/signatory';
import {userManagementSlice} from './slides/project-management/user';
import {corporateAdminSlide} from './slides/super-admin/corporateAdminSlide';

const rootReducer = combineReducers({
  counter: counterReducer,
  authentication: authtenticationSlide,

  enterprise: enterpriseSlice,
  // Form identity
  [identitySlide.reducerPath]: identitySlide.reducer,
  // From profile
  [templateSlice.reducerPath]: templateSlice.reducer,
  // From Keycloak
  [companyAPI.reducerPath]: companyAPI.reducer,
  [userSlide.reducerPath]: userSlide.reducer,
  // From Project management
  [projectSlide.reducerPath]: projectSlide.reducer,
  [signatorySlice.reducerPath]: signatorySlice.reducer,
  [userManagementSlice.reducerPath]: userManagementSlice.reducer,
  [participantSlide.reducerPath]: participantSlide.reducer,
  [processControlNotificationSlide.reducerPath]:
    processControlNotificationSlide.reducer,
  [corporateSettingSlide.reducerPath]: corporateSettingSlide.reducer,
  [corporateUserSlide.reducerPath]: corporateUserSlide.reducer,
  [corporateSettingPublicSlide.reducerPath]:
    corporateSettingPublicSlide.reducer,
  [processControlSlide.reducerPath]: processControlSlide.reducer,
  // end user profile
  [endUserProfileSlide.reducerPath]: endUserProfileSlide.reducer,
  // super admin
  [corporateAdminSlide.reducerPath]: corporateAdminSlide.reducer,
});

export const setupStore = (preloadedState?: PreloadedState<RootState>) => {
  return configureStore({
    reducer: rootReducer,
    middleware: getDefaultMiddleware =>
      // adding the api middleware enables caching, invalidation, polling and other features of `rtk-query`
      getDefaultMiddleware({serializableCheck: false}).concat(
        // logger,
        processControlSlide.middleware,
        companyAPI.middleware,
        userSlide.middleware,
        projectSlide.middleware,
        participantSlide.middleware,
        processControlNotificationSlide.middleware,
        signatorySlice.middleware,
        userManagementSlice.middleware,
        templateSlice.middleware,
        corporateSettingSlide.middleware,
        corporateUserSlide.middleware,
        corporateSettingPublicSlide.middleware,
        endUserProfileSlide.middleware,
        corporateAdminSlide.middleware,
        identitySlide.middleware,
      ),
    preloadedState,
  });
};

export const store = setupStore();

export type RootState = ReturnType<typeof rootReducer>;
export type AppStore = ReturnType<typeof setupStore>;
export type AppDispatch = AppStore['dispatch'];
