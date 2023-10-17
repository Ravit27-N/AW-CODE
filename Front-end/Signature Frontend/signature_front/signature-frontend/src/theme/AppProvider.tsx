import {Route} from '@/constant/Route';
import {store} from '@/redux';
import {useAppDispatch} from '@/redux/config/hooks';
import {
  refreshToken,
  storeUserCompanyInfo,
} from '@/redux/slides/authentication/authenticationSlide';
import {jwtDecode} from '@/redux/slides/keycloak/common';
import {router} from '@/router';
import {light, resetTheme} from '@/theme';
import {Navigate} from '@/utils/common';
import {UserRoleEnum} from '@/utils/request/interface/User.interface';
import certignaLogo from '@assets/background/login/NGLogo.svg';
import {Theme, ThemeProvider, createTheme} from '@mui/material';
import axios from 'axios';
import React, {PropsWithChildren, useEffect} from 'react';
import {useLocation, useSearchParams} from 'react-router-dom';
import env from '../../env.config';

import {defaultColor, i18nKey, refreshTokenKey} from '@/constant/NGContant';
import {
  IGetCompanyTheme,
  corporateSettingPublicSlide,
  useGetCompanyThemeQuery,
} from '@/redux/slides/corporate-admin/corporate-public/corporateSettigPublicSlide';
import {corporateSettingSlide} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {
  setCorporateSetting,
  setCorporateThemeSetting,
} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {formatKeycloakAPI} from '@/utils/common/ApiFacade';
import CustomSnackbarProvider from '@/pages/protect-route/provider/CustomSnackbarProvider';

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
      `${formatKeycloakAPI}/realms/${env.VITE_REALM}/protocol/openid-connect/token`,
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
      if (
        [
          UserRoleEnum.ENDUSER,
          UserRoleEnum.SUPERADMIN,
          UserRoleEnum.COPERATE_ADMIN,
        ].indexOf(role) > -1
      ) {
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

// App config provider
const AppProvider = ({children}: PropsWithChildren) => {
  const [search] = useSearchParams();
  const {data, isSuccess, error} = useGetCompanyThemeQuery(
    {
      uuid: search.get('company')!,
    },
    {skip: !search.get('company')},
  );
  const location = useLocation();
  const dispatch = useAppDispatch();
  const [theme, setTheme] = React.useState<Theme>(light);
  const [isReady, setIsReady] = React.useState(false);
  const [loading, setLoading] = React.useState(true);
  const checkDefaultLang = localStorage.getItem(i18nKey);

  const checkDirect = () => {
    return store.getState().authentication.role === UserRoleEnum.COPERATE_ADMIN
      ? router.navigate(Navigate(Route.HOME_CORPORATE))
      : store.getState().authentication.role === UserRoleEnum.ENDUSER
      ? router.navigate(Navigate(Route.HOME_ENDUSER))
      : store.getState().authentication.role === UserRoleEnum.SUPERADMIN &&
        router.navigate(Navigate(Route.HOME_SUPER));
  };

  const setThemeDefault = () => {
    setTheme(
      createTheme({
        breakpoints: {
          values: {
            xs: 0,
            sm: 600,
            md: 900,
            lg: 1200,
            xl: 1441,
            xxl: 2300,
          },
        },
        typography: {
          fontFamily: 'Poppins',
        },
        palette: {
          mode: 'light',
          primary: {
            main: defaultColor,
            contrastText: '#FFFFFF',
          },
          secondary: {
            main: defaultColor,
          },
          Primary: {
            main: defaultColor,
            contrastText: '#FFFFFF',
          },
          ...resetTheme,
        },
      }),
    );
    dispatch(
      setCorporateThemeSetting({
        reduxTheme: {
          logo: certignaLogo,
          companyId: '',
          id: '',
          linkColor: defaultColor,
          mainColor: defaultColor,
          secondaryColor: defaultColor,
        },
      }),
    );
  };

  React.useEffect(() => {
    if (error) {
      setThemeDefault();
      setIsReady(true);
    }
  }, [error]);

  const getColor = (data: IGetCompanyTheme[], key: keyof IGetCompanyTheme) => {
    return data[0] ? (data[0][key] as string) : defaultColor;
  };

  React.useEffect(() => {
    const routeSwitching = async () => {
      if (store.getState().authentication.userToken) {
        const {exp, firstLogin} = jwtDecode(
          store.getState().authentication!.userToken!,
        ) as any;
        if (exp * 1000 < Date.now()) {
          return await pageRefresh();
        }
        if (
          !firstLogin &&
          [Route.CHANGE_PASSWORD].indexOf(location.pathname) > -1
        ) {
          checkDirect();
        }
        if ([Route.LOGIN, Route.ROOT].indexOf(location.pathname) > -1) {
          checkDirect();
        }
      }
    };
    !loading && routeSwitching();
  }, [location.pathname, loading]);

  useEffect(() => {
    if (isSuccess) {
      const readyData = async () => {
        try {
          if ([Route.LOGIN, Route.ROOT].indexOf(location.pathname) > -1) {
            if (data[0].logo !== null) {
              const res = await store
                .dispatch(
                  corporateSettingPublicSlide.endpoints.viewFileInCorporate.initiate(
                    {
                      fileName: data[0].logo,
                    },
                  ),
                )
                .unwrap();

              setTheme(
                createTheme({
                  breakpoints: {
                    values: {
                      xs: 0,
                      sm: 600,
                      md: 900,
                      lg: 1200,
                      xl: 1441,
                      xxl: 2300,
                    },
                  },
                  typography: {
                    fontFamily: 'Poppins',
                  },
                  palette: {
                    mode: 'light',
                    primary: {
                      main: getColor(data, 'mainColor'),
                      contrastText: '#FFFFFF',
                    },
                    secondary: {
                      main: getColor(data, 'secondaryColor'),
                    },
                    Primary: {
                      main: getColor(data, 'mainColor'),
                      contrastText: '#FFFFFF',
                    },
                    ...resetTheme,
                  },
                }),
              );
              dispatch(
                setCorporateThemeSetting({
                  reduxTheme: {
                    logo: `data:application/pdf;base64,${res}`,
                    companyId: data[0].companyId as unknown as string,
                    id: data[0].id as unknown as string,
                    linkColor: getColor(data, 'linkColor'),
                    mainColor: getColor(data, 'mainColor'),
                    secondaryColor: getColor(data, 'secondaryColor'),
                  },
                }),
              );
            }
          }

          setIsReady(true);
        } catch (error) {
          setIsReady(true);
          return error;
        }
      };
      readyData().then(r => r);
    } else {
      setThemeDefault();
      setIsReady(true);
    }
  }, [isSuccess]);

  React.useEffect(() => {
    const fetchSettingTheme = async () => {
      try {
        const data = await store
          .dispatch(
            corporateSettingSlide.endpoints.getCorporateSetting.initiate({}),
          )
          .unwrap();
        const theme = {
          companyId: data.theme[0].companyId as unknown as string,
          id: data.theme[0].id as unknown as string,
          linkColor: data.theme[0].linkColor,
          mainColor: data.theme[0].mainColor,
          secondaryColor: data.theme[0].secondaryColor,
        };
        setTheme(
          createTheme({
            breakpoints: {
              values: {
                xs: 0,
                sm: 600,
                md: 900,
                lg: 1200,
                xl: 1441,
                xxl: 2300,
              },
            },
            typography: {
              fontFamily: 'Poppins',
            },
            palette: {
              mode: 'light',
              primary: {
                main: data.theme[0].mainColor ?? defaultColor,
                contrastText: '#FFFFFF',
              },
              secondary: {
                main: data.theme[0].secondaryColor ?? defaultColor,
              },
              Primary: {
                main: data.theme[0].mainColor ?? defaultColor,
                contrastText: '#FFFFFF',
              },
              ...resetTheme,
            },
          }),
        );
        dispatch(
          setCorporateThemeSetting({
            reduxTheme: {
              logo: certignaLogo,
              ...theme,
            },
          }),
        );
        if (data.theme[0].logo) {
          const res = await store
            .dispatch(
              corporateSettingPublicSlide.endpoints.viewFileInCorporate.initiate(
                {
                  fileName: data.theme[0].logo,
                },
              ),
            )
            .unwrap();

          dispatch(setCorporateSetting({reduxCorporateSetting: data}));
          dispatch(
            setCorporateThemeSetting({
              reduxTheme: {
                logo: `data:application/pdf;base64,${res}`,
                ...theme,
              },
            }),
          );
        }
      } catch (error) {
        setIsReady(true);
        return error;
      }
    };
    if (store.getState().authentication.userToken) {
      fetchSettingTheme().then(r => r);
    }
  }, [store.getState().authentication.userToken]);

  React.useEffect(() => {
    localStorage.setItem('company', search.get('company') ?? 'certigna');
    const handleFetch = async () => {
      await pageRefresh().then(() => {
        setLoading(false);
      });
    };
    if ([null, 'en-US'].indexOf(checkDefaultLang) > -1) {
      localStorage.setItem(i18nKey, 'fr');
    }
    handleFetch().then(r => r);
  }, []);

  return isReady ? (
    <ThemeProvider theme={theme}>
      <CustomSnackbarProvider>{children}</CustomSnackbarProvider>
    </ThemeProvider>
  ) : (
    <>loading...</>
  );
};

export default AppProvider;
