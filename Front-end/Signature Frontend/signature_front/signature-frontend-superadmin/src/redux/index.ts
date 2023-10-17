import authtenticationSlide from '@/redux/slides/authentication/authenticationSlide';
import {corporateSettingSlide} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {corporateUserSlide} from '@/redux/slides/corporate-admin/corporateUserSlide';
import enterpriseSlice from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';

import {
  configureStore,
  PreloadedState,
  combineReducers,
} from '@reduxjs/toolkit';

import {companyAPI} from './slides/company/query/companyRTK';
import {corporateSettingPublicSlide} from './slides/corporate-admin/corporate-public/corporateSettigPublicSlide';
import {userSlide} from './slides/keycloak/user';

import {userManagementSlice} from './slides/project-management/user';
import {corporateAdminSlide} from './slides/super-admin/corporateAdminSlide';

const rootReducer = combineReducers({
  authentication: authtenticationSlide,

  enterprise: enterpriseSlice,

  // From Keycloak
  [companyAPI.reducerPath]: companyAPI.reducer,
  [userSlide.reducerPath]: userSlide.reducer,
  // From Project management

  [userManagementSlice.reducerPath]: userManagementSlice.reducer,

  [corporateSettingSlide.reducerPath]: corporateSettingSlide.reducer,
  [corporateUserSlide.reducerPath]: corporateUserSlide.reducer,
  [corporateSettingPublicSlide.reducerPath]:
    corporateSettingPublicSlide.reducer,

  // end user profile

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

        companyAPI.middleware,
        userSlide.middleware,

        userManagementSlice.middleware,

        corporateSettingSlide.middleware,
        corporateUserSlide.middleware,
        corporateSettingPublicSlide.middleware,

        corporateAdminSlide.middleware,
      ),
    preloadedState,
  });
};

export const store = setupStore();

export type RootState = ReturnType<typeof rootReducer>;
export type AppStore = ReturnType<typeof setupStore>;
export type AppDispatch = AppStore['dispatch'];
