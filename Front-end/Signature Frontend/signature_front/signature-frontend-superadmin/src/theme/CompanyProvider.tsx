import {defaultColor} from '@/constant/NGContant';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {useGetCompanyThemeQuery} from '@/redux/slides/corporate-admin/corporate-public/corporateSettigPublicSlide';
import {setCompanyProviderTheme} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {light, restTheme} from '@/theme';
import {viewImage} from '@/utils/common/ViewImage';
import certignaLogo from '@assets/background/login/NGLogo.svg';
import {ThemeProvider, createTheme} from '@mui/material';
import {DefaultTheme} from '@mui/styles';
import React, {PropsWithChildren} from 'react';
import {useParams} from 'react-router-dom';

const CompanyProvider = ({children}: PropsWithChildren) => {
  const param = useParams() as {uuid: string};
  const {userToken, role} = useAppSelector(state => state.authentication);
  const [theme, setTheme] = React.useState<Partial<DefaultTheme>>(light);
  const dispatch = useAppDispatch();
  const {currentData: data} = useGetCompanyThemeQuery(
    {
      uuid: param.uuid,
    },
    {skip: !param.uuid},
  );

  React.useEffect(() => {
    if (data) {
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
              main: data[0].mainColor ?? defaultColor,
              contrastText: '#FFFFFF',
            },
            secondary: {
              main: data[0].mainColor ?? defaultColor,
            },
            Primary: {
              main: data[0].mainColor ?? defaultColor,
              contrastText: '#FFFFFF',
            },
            ...restTheme,
          },
        }),
      );
      dispatch(
        setCompanyProviderTheme({
          companyProviderTheme: {
            logo: viewImage(data[0].logo) ?? certignaLogo,
            companyId: data[0].companyId,
            id: data[0].id,
            linkColor: defaultColor,
            mainColor: data[0].mainColor ?? defaultColor,
            secondaryColor: defaultColor,
          },
        }),
      );
    }
  }, [data]);

  return userToken && role ? (
    <ThemeProvider theme={theme}>{children}</ThemeProvider>
  ) : (
    <></>
  );
};

export default CompanyProvider;
