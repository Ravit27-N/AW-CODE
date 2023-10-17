import {defaultColor, i18nKey} from '@/constant/NGContant';
import CustomSnackbarProvider from '@/pages/protect-route/provider/CustomSnackbarProvider';
import {store} from '@/redux';
import {useAppDispatch} from '@/redux/config/hooks';
import {
  IGetCompanyTheme,
  corporateSettingPublicSlide,
  useGetCompanyThemeQuery,
} from '@/redux/slides/corporate-admin/corporate-public/corporateSettigPublicSlide';
import {setCorporateThemeSetting} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {light, resetTheme} from '@/theme';
import Certigna_Logo from '@assets/background/login/NGLogo.svg';
import {Theme, ThemeProvider, createTheme} from '@mui/material';
import React, {PropsWithChildren, useEffect} from 'react';
import {Outlet, useParams, useSearchParams} from 'react-router-dom';

// App config provider
const UnAuthorize = ({children}: PropsWithChildren) => {
  const [search] = useSearchParams();
  const {id} = useParams() as {id: string};
  const {data, isSuccess, error, isUninitialized} = useGetCompanyThemeQuery(
    {
      uuid: id,
    },
    {
      skip: !id,
    },
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
    if (error) {
      setThemeDefault();
      setIsReady(true);
    }
  }, [error]);

  useEffect(() => {
    if (isUninitialized) {
      setIsReady(true);
      setThemeDefault();
    }
  }, [isUninitialized]);

  const getColor = (data: IGetCompanyTheme[], key: keyof IGetCompanyTheme) => {
    return data[0] && data[0][key] ? (data[0][key] as string) : defaultColor;
  };

  useEffect(() => {
    if (isSuccess) {
      localStorage.setItem('company', search.get('company') ?? '');
      if ([null, 'en-US'].indexOf(checkDefaultLang) > -1) {
        localStorage.setItem(i18nKey, 'fr');
      }
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
    <ThemeProvider theme={theme}>
      <CustomSnackbarProvider>
        <Outlet />
      </CustomSnackbarProvider>
    </ThemeProvider>
  ) : (
    <>loading...</>
  );
};

export default UnAuthorize;
