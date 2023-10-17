import {Route} from '@/constant/Route';
import {store} from '@/redux';
import {
  refreshToken,
  storeUserCompanyInfo,
} from '@/redux/slides/authentication/authenticationSlide';
import {jwtDecode} from '@/redux/slides/keycloak/common';
import {router} from '@/router';
import {light} from '@/theme';
import {Navigate} from '@/utils/common';
import {UserRoleEnum} from '@/utils/request/interface/User.interface';
import {ThemeProvider} from '@mui/material';
import axios from 'axios';
import React, {PropsWithChildren} from 'react';
import {useLocation} from 'react-router-dom';
import env from '../../env.config';

import {i18nKey, refreshTokenKey} from '@/constant/NGContant';
import {getDefaultLan} from './UnAuthorize';

// App config provider
const AppProvider = ({children}: PropsWithChildren) => {
  const location = useLocation();
  const [loading, setLoading] = React.useState(true);
  const checkDefaultLang = localStorage.getItem(i18nKey);

  const checkDirect = () => {
    switch (store.getState().authentication.role) {
      case UserRoleEnum.COPERATE_ADMIN: 
        return router.navigate(Navigate(Route.HOME_CORPORATE));
      case UserRoleEnum.ENDUSER:
        return router.navigate(Navigate(Route.HOME_ENDUSER))
      case UserRoleEnum.SUPERADMIN: 
        return router.navigate(Navigate(Route.HOME_SUPER))
      default :
        return router.navigate(Navigate(Route.LOGIN)); // return login page when invalid role
    }
  };
  
  

  React.useEffect(() => {
    const routeSwitching = async () => {
      const token = store.getState().authentication.userToken;
      if (token) {
        const {exp, firstLogin} = jwtDecode(token) as any;
        if (exp * 1000 < Date.now()) {
          return await pageRefresh();
        }
        if (
          !firstLogin &&
          [Route.CHANGE_PASSWORD].indexOf(location.pathname) > -1
        ) {
          await checkDirect();
        }
        if ([Route.LOGIN, Route.ROOT].indexOf(location.pathname) > -1) {
          await checkDirect();
        }
      }
    };
    !loading && routeSwitching().then(r=>r).catch(e=>e);
  }, [location.pathname, loading]);

  React.useEffect(() => {
    const handleFetch = async () => {
      await pageRefresh().then(() => {
        setLoading(false);
      });
    };
    getDefaultLan(checkDefaultLang);
    handleFetch().then(r => r).catch(e=>e);
  }, []);

  return !loading ? (
    <ThemeProvider theme={light}>{children}</ThemeProvider>
  ) : (
    <>loading...</>
  );
};

export default AppProvider;

export const pageRefresh = async () => {
  const refreshTokenLocal = localStorage.getItem(refreshTokenKey)!;
  let storeRole: null | string = null;
  if (!refreshTokenLocal) {
    return router.navigate(Navigate(Route.LOGIN));
  }
  const {exp} = jwtDecode(refreshTokenLocal) as any;
  if (exp * 1000 < Date.now()) {
    localStorage.removeItem(refreshTokenKey);
    return router.navigate(Navigate(Route.LOGIN));
  }

  try {
    const res = await axios.post(
      `${env.VITE_BASE_URL_KEYCLOAK}/realms/${env.VITE_REALM}/protocol/openid-connect/token`,
      {
        refresh_token: refreshTokenLocal,
        client_id: env.VITE_CLIENT_ID,
        client_secret: env.VITE_CLIENT_SECRET,
        grant_type: env.VITE_GRANT_TYPE_FRONT,
      },
      {headers: {'Content-Type': 'application/x-www-form-urlencoded'}},
    );

    const {refresh_token, access_token} = res.data;
    const {realm_access, name, firstLogin, sub, C_UUID, USER_COMPANY} =
      jwtDecode(access_token) as any;
    const {roles} = realm_access as {roles: any[]};
    roles.forEach(role => {
      if ([UserRoleEnum.SUPERADMIN].indexOf(role) > -1) {
        storeRole = role;
      }
    });
    store.dispatch(
      refreshToken({
        value: {
          token: access_token,
          name,
          role: storeRole,
          sid: sub,
        },
      }),
    );
    store.dispatch(
      storeUserCompanyInfo({
        value: {
          C_UUID,
          USER_COMPANY,
        },
      }),
    );
    localStorage.setItem(refreshTokenKey, refresh_token);
    if ([true, undefined, null, 'true'].indexOf(firstLogin) > -1) {
      return router.navigate(Navigate(Route.CHANGE_PASSWORD));
    }
    return res;
  } catch (error) {
    localStorage.removeItem(refreshTokenKey);
    return await router.navigate(Navigate(Route.LOGIN));
  }
};
