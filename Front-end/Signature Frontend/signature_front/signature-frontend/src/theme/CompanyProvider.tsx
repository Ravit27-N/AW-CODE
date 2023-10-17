import {COLOR_THEME_ARRAY, defaultColor} from '@/constant/NGContant';
import {light} from '@/theme';
import {ThemeProvider, createTheme} from '@mui/material';
import {DefaultTheme} from '@mui/styles';
import React, {PropsWithChildren} from 'react';

const CompanyProvider = ({children}: PropsWithChildren) => {
  const [theme, setTheme] = React.useState<Partial<DefaultTheme>>(light);

  React.useMemo(() => {
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
            main: defaultColor,
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
  }, []);

  return <ThemeProvider theme={theme}>{children}</ThemeProvider>;
};

export default CompanyProvider;
