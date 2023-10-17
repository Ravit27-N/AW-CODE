import {COLOR_THEME_ARRAY, defaultColor, i18nKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {useAppDispatch} from '@/redux/config/hooks';
import {
  IGetCompanyTheme,
  corporateSettingPublicSlide,
  useGetCompanyThemeQuery,
} from '@/redux/slides/corporate-admin/corporate-public/corporateSettigPublicSlide';
import {setCorporateThemeSetting} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {light, restTheme} from '@/theme';
import Certigna_Logo from '@assets/background/login/NGLogo.svg';
import {ThemeProvider} from '@emotion/react';
import {Theme, createTheme} from '@mui/material';
import React, {PropsWithChildren, useEffect} from 'react';
import {useSearchParams} from 'react-router-dom';

export const getDefaultLan = (defaultLan: string | null) => {
  return (
    [null, 'en-US'].indexOf(defaultLan) > -1 &&
    localStorage.setItem(i18nKey, 'fr')
  );
};

// App config provider
const UnAuthorize = ({children}: PropsWithChildren) => {
  const [search] = useSearchParams();
  const companyId = search.get('company')!;
  const {data, isSuccess, error, isUninitialized} = useGetCompanyThemeQuery(
    {
      uuid: companyId,
    },
    {skip: !companyId},
  );
  const dispatch = useAppDispatch();
  const [theme, setTheme] = React.useState<Theme>(light);
  const [isReady, setIsReady] = React.useState(false);
  const checkDefaultLang = localStorage.getItem(i18nKey);

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
          ...restTheme,
        },
      }),
    );
    dispatch(
      setCorporateThemeSetting({
        reduxTheme: {
          logo: Certigna_Logo,
          companyId: '',
          id: '',
          linkColor: defaultColor,
          mainColor: defaultColor,
          secondaryColor: defaultColor,
        },
      }),
    );
  };
  useEffect(() => {
    if (isUninitialized || error) {
      setIsReady(true);
      setThemeDefault();
    }
  }, [isUninitialized, error]);

  const getColor = (
    data: IGetCompanyTheme[],
    key: keyof IGetCompanyTheme,
  ): string => {
    return data[0] ? (data[0][key] as string) : defaultColor;
  };

  useEffect(() => {
    if (isSuccess) {
      localStorage.setItem('company', companyId ?? '');
      getDefaultLan(checkDefaultLang);
      const readyData = async () => {
        try {
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
                ...restTheme,
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
          setIsReady(true);
        } catch (error) {
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
                Black: {
                  main: '#000000',
                  dark: '#A9A9A9',
                },
                black: {
                  main: '#000000',
                  dark: '#A9A9A9',
                },
                DarkGrey: {
                  main: '#676767',
                },
                Grey: {
                  main: '#BABABA',
                },
                LightGray: {
                  main: '#E9E9E9',
                },
                White: {
                  main: '#FFFFFF',
                },
                OffWhite: {
                  main: '#F0F2F5',
                },
                Danger: {
                  // main: '#CE0500',
                  main: COLOR_THEME_ARRAY[3],
                },
                Success: {
                  main: '#197B4A',
                },
                Info: {
                  main: '#0065E0',
                },
                Warning: {
                  main: '#D14900',
                },
                Dark: {
                  main: '#000000',
                },
                Light: {
                  main: '#525050',
                },
                Placeholder: {
                  main: '#767676',
                },
                ColorDisabled: {
                  main: '#CE0500',
                },
                ColorHover: {
                  main: 'rgba(0,0,0,0.20)',
                },
                Color10: {
                  main: 'rgba(255,255,255,0.98)',
                },
                Color35: {
                  main: 'rgba(255,255,255,0.93)',
                },
                Color50: {
                  main: 'rgba(255,255,255,0.90)',
                },
                Color100: {
                  main: 'rgba(255,255,255,0.80)',
                },
                Color250: {
                  main: 'rgba(255,255,255,0.50)',
                },
                Color950: {
                  main: 'rgba(0,0,0,0.90)',
                },
                BlueGray: {
                  main: '#EDF2FA',
                },
                //================================================
                boxModel: {
                  main: '#E83977',
                },
                blue: {main: '#0065E0', dark: '#0065E0', light: '#EDF4FD'},
                info: {
                  main: '#D6056A',
                  light: '#F7CDE1',
                },

                bg: {
                  main: '#F0F2F5',
                },

                Text2: {
                  main: '#71717A',
                  dark: '#F0F2F5',
                },
                buttonNewProject: {
                  main: '#E83977',
                  dark: '#121232',
                  light: '#29A8FF',
                  contrastText: '#FFFFFF',
                },
                buttonWhite: {
                  main: '#FFFFFF',
                  dark: '#ccc',
                  light: '#29A8FF',
                  contrastText: '#FFFFFF',
                },
                buttonLogin: {
                  main: '#121232',
                  dark: '#121232',
                  light: '#29A8FF',
                  contrastText: '#FFFFFF',
                },
                whiteColor: {
                  main: '#FFFFFF',
                  dark: 'rgba(255, 255, 255, 0.98);',
                  light: 'rgba(255, 255, 255, 0.99);',
                },
              },
            }),
          );
          dispatch(
            setCorporateThemeSetting({
              reduxTheme: {
                logo: Certigna_Logo,
                companyId: data[0]?.companyId as unknown as string,
                id: data[0]?.id as unknown as string,
                linkColor: getColor(data, 'linkColor'),
                mainColor: getColor(data, 'mainColor'),
                secondaryColor: getColor(data, 'secondaryColor'),
              },
            }),
          );
          setIsReady(true);
          return error;
        }
      };
      readyData().then(r => r);
    }
  }, [isSuccess]);

  return isReady ? (
    <ThemeProvider theme={theme}>{children}</ThemeProvider>
  ) : (
    <>loading...</>
  );
};

export default UnAuthorize;
