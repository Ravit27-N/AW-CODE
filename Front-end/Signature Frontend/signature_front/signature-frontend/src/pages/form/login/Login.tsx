import KeyLogo from '@/assets/image/keylogo.png';
import VisaLogo from '@/assets/image/visa.png';
import WorldISOLogo from '@/assets/image/worldISO.png';
import {isLogoutKey, refreshTokenKey} from '@/constant/NGContant';

import {Route} from '@/constant/Route';
import {
  M_size,
  colorBlack,
  colorDisable,
  colorWhite,
} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {useLoginMutation} from '@/redux/slides/keycloak/user';
import {Center} from '@/theme';
import {Navigate} from '@/utils/common';
import {pixelToRem} from '@/utils/common/pxToRem';
import Logo from '@assets/background/login/NGLogo.svg';
import bg from '@assets/background/login/bg.svg';
import {NGKey, NGMessage} from '@assets/iconExport/Allicon';
import innovation from '@assets/image/innovation-trust.png';
import {NgSvgBackground} from '@components/ng-background/NGSvgBackground';
import NGInput from '@components/ng-inputField/NGInput';
import NGText from '@components/ng-text/NGText';
import {Backdrop, CircularProgress, Stack, Typography} from '@mui/material';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import FormControlLabel from '@mui/material/FormControlLabel';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import {useSnackbar} from 'notistack';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate} from 'react-router-dom';
import env from '../../../../env.config';

export default function Login() {
  const {t} = useTranslation();
  const {theme} = useAppSelector(state => state.enterprise);
  const [check, setCheck] = React.useState(false);
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const navigate = useNavigate();
  const [messageError, setMessageError] = React.useState('');
  const [login, {isLoading}] = useLoginMutation();
  const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);
  const {enqueueSnackbar, closeSnackbar} = useSnackbar();

  // Handle login
  const loginProcess = async (e: any) => {
    e.preventDefault();
    try {
      const data = await login({
        password,
        username: email,
        client_secret: env.VITE_CLIENT_SECRET,
      }).unwrap();

      const {refresh_token} = data;
      localStorage.setItem(refreshTokenKey, refresh_token);
      window.location.reload();
    } catch (error) {
      setMessageError(
        t(
          Localization(
            'status',
            'your-email-address-and/or-password-is-not-correct',
          ),
        )!,
      );
    }
  };
  React.useEffect(() => {
    setMessageError('');
  }, [password, email]);

  React.useEffect(() => {
    const handleWindowResize = () => {
      setWindowWidth(window.innerWidth);
    };
    window.addEventListener('resize', handleWindowResize);
    return () => {
      window.removeEventListener('resize', handleWindowResize);
    };
  }, []);
  React.useEffect(() => {
    const initialValue = document.body.style.scale;
    document.body.style.scale =
      windowWidth >= 1000 && windowWidth <= 1200 ? '80%' : '100%';
    return () => {
      document.body.style.scale = initialValue;
    };
  }, [windowWidth]);

  /* handler when something error with backend */
  React.useEffect(() => {
    if (localStorage.getItem(isLogoutKey) == 'true') {
      enqueueSnackbar(t(Localization('title', 'you-have-been-disconnected')), {
        variant: 'infoSnackbar',
      });
      localStorage.setItem(isLogoutKey, 'false');
    }
    return () => closeSnackbar();
  }, []);

  return (
    <Grid container component="main" sx={{height: '100vh'}}>
      <CssBaseline />
      <Grid
        item
        xs={12}
        sm={12}
        md={5}
        sx={{
          position: 'relative',
        }}
        overflow={'hidden'}
        component={Paper}
        elevation={6}
        square>
        <Center height={'85%'}>
          <Box
            sx={{
              justifyContent: 'center',
              width: {xs: '90%', sm: '80%', md: '80%', lg: '80%', xl: '55%'},
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'flex-start',
            }}>
            <Grid
              container
              md={12}
              sm={12}
              rowSpacing={'10%'}
              item
              width={'100%'}>
              <Grid item md={12} sm={12}>
                <img
                  src={theme[0].logo ?? Logo}
                  style={{height: '48px', maxWidth: '205px'}}
                  alt={'Logo'}
                />
              </Grid>
              <Grid item md={12} sm={12} rowSpacing={2}>
                <Stack>
                  <NGText
                    myStyle={{
                      fontSize: 14,
                      fontWeight: 400,
                      color: 'black.main',
                    }}
                    text={t(Localization('title', 'welcome to signature'))}
                  />
                  <NGText
                    text={
                      t(Localization('title', 'log in to your')) +
                      ' ' +
                      t(Localization('title', 'signature space'))
                    }
                    myStyle={{
                      fontSize: [20, 22, 22, 22, 31],
                      fontWeight: 600,
                      color: 'black.main',
                    }}
                  />
                </Stack>
              </Grid>
            </Grid>
            <Box mt={'5%'} width={'100%'}>
              <form onSubmit={loginProcess}>
                <Grid
                  container
                  md={12}
                  sm={12}
                  xs={12}
                  rowSpacing={2}
                  item
                  mb={1}>
                  <Grid item md={12} sm={12} mx={'auto'} xs={12}>
                    <NGInput
                      id="email"
                      colorOnfocus={'primary'}
                      colorOutline={'Primary.main'}
                      Icon={
                        <NGMessage
                          sx={{
                            color: email === '' ? '#000000' : 'Primary.main',
                            mt: pixelToRem(11),
                          }}
                        />
                      }
                      setValue={setEmail}
                      value={email}
                      nameId={'optional'}
                      placeholder={t(Localization('form', 'email-placeholder'))}
                      type={'text'}
                      textLabel={t(Localization('form', 'e-mail-address'))}
                    />
                  </Grid>
                  <Grid item md={12} sm={12} mx={'auto'} xs={12}>
                    <NGInput
                      passwordLength={200}
                      id="password"
                      colorOnfocus={'info'}
                      colorOutline={'info.main'}
                      Icon={
                        <NGKey
                          sx={{
                            color: password === '' ? '#000000' : 'Primary.main',
                            mt: pixelToRem(11),
                          }}
                        />
                      }
                      setValue={setPassword}
                      value={password}
                      nameId={'optional'}
                      placeholder={t(
                        Localization('form', 'password-placeholder'),
                      )}
                      textLabel={t(Localization('form', 'password'))}
                      textLabelRight={
                        <NGText
                          text={t(Localization('form', 'forget-password'))}
                          myStyle={{
                            fontWeight: 600,
                            fontSize: pixelToRem(11),
                            cursor: 'pointer',
                            color: 'black.main',
                          }}
                          onClick={() => {
                            navigate(Navigate(Route.FORGOT_PASSWORD));
                          }}
                        />
                      }
                      type={'password'}
                    />
                  </Grid>
                </Grid>
                {messageError !== '' && (
                  // <Text sx={{color: 'red'}}>{messageError}</Text>
                  <NGText
                    myStyle={{color: 'red', fontSize: 12}}
                    aria-label="login-page-error-message-popup"
                    text={messageError}
                  />
                )}
                <Box display={'flex'} flexDirection={'column'}>
                  <FormControlLabel
                    disabled={!(email !== '' && password !== '')}
                    sx={{mt: 2}}
                    control={
                      <Checkbox
                        value="remember"
                        color="primary"
                        checked={check}
                        onChange={e => {
                          setCheck(e.target.checked);
                        }}
                      />
                    }
                    label={
                      <>
                        <NGText
                          text={t(Localization('title', 'I accept the'))}
                          myStyle={{
                            opacity: check ? 1 : 0.5,
                            fontSize: [10, 12, 11],
                            fontWeight: 400,
                            LineHeight: 16,
                          }}
                        />
                        <a
                          href={
                            'documents/CGU-Certigna_Sign-Niveau_Simple_V0.1.pdf'
                          }
                          target="_blank"
                          rel="noopener noreferrer"
                          style={{textDecoration: 'none'}}>
                          <NGText
                            text={t(
                              Localization(
                                'title',
                                'Terms and Conditions of Use',
                              ),
                            )}
                            myStyle={{
                              opacity: check ? 1 : 0.5,
                              color: 'Primary.main',
                              textDecoration: 'underline',
                              fontSize: [10, 12, 11],
                              fontWeight: 400,
                              LineHeight: 16,
                            }}
                          />
                        </a>
                      </>
                    }
                  />
                  <Button
                    variant={'contained'}
                    type="submit"
                    color={'primary'}
                    disabled={!check}
                    sx={{
                      mt: 3,
                      textTransform: 'none',
                      mb: 2,
                      py: [1, 2],
                      borderRadius: '6px',
                      width: ['100%', '100%'],
                      fontWeight: 'bold',
                      fontSize: M_size.h3,
                      '&.MuiButton-contained': {
                        fontWeight: 600,
                        textTransform: 'capitalize',
                      },
                      '&.Mui-disabled': {
                        bgcolor: colorDisable,
                        color: colorWhite,
                      },
                      '&:hover': {
                        bgcolor: colorBlack,
                      },
                    }}>
                    <NGText
                      text={t(Localization('form', 'login'))}
                      myStyle={{
                        fontSize: 13,
                        fontWeight: 600,
                        LineHeight: 24,
                        color: 'white',
                      }}
                    />
                  </Button>

                  <Stack alignItems={'center'}>
                    <Stack
                      sx={{position: 'fixed', bottom: '8%'}}
                      direction={'row'}
                      width={'340px'}
                      justifyContent={'space-between'}
                      alignItems={'center'}>
                      <img
                        src={KeyLogo}
                        width={'30px'}
                        height={'40px'}
                        alt={'KeyLogo'}
                      />
                      <img
                        src={VisaLogo}
                        width={'120px'}
                        height={'50px'}
                        alt={'VisaLogo'}
                      />
                      <img
                        src={WorldISOLogo}
                        width={'40px'}
                        height={'40px'}
                        alt={'WorldISOLogo'}
                      />
                    </Stack>
                  </Stack>
                </Box>
              </form>
            </Box>
          </Box>
        </Center>

        <Stack
          sx={{
            position: 'absolute',
            alignItems: 'end',
            bottom: 20,
            right: 30,
          }}>
          <img src={innovation} width="130px" />
          {/* <Typography
            sx={{
              fontWeight: 700,
              textAlign: 'end',
            }}>
            Innovation
            <Typography
              component="span"
              sx={{
                color: '#4C4CFF',
                fontWeight: 600,
              }}>
              &
            </Typography>
            trust
          </Typography> */}
          <Typography
            sx={{
              fontWeight: 600,
              textAlign: 'end',
              color: '#2D2D99',
            }}>
            © {import.meta.env.VITE_SIGNATURE_VERSION} (
            {import.meta.env.VITE_SIGNATURE_PUBLISH_DATE})
          </Typography>
        </Stack>
      </Grid>
      <Grid
        item
        xs={false}
        sm={false}
        md={7}
        sx={{
          backgroundColor: '#FAFBFE',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          position: 'relative',
        }}>
        <NgSvgBackground resource={bg} />
      </Grid>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={isLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Grid>
  );
}
