import {Route} from '@/constant/Route';
import {
  colorBlack,
  colorDisable,
  colorWhite,
} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {useForgetPasswordMutation} from '@/redux/slides/project-management/user';
import {Navigate} from '@/utils/common';
import {NGButton} from '@components/ng-button/NGButton';
import NGInput from '@components/ng-inputField/NGInput';
import NGText from '@components/ng-text/NGText';
import {Alert, Stack} from '@mui/material';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import CssBaseline from '@mui/material/CssBaseline';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate} from 'react-router-dom';
import isEmail from 'validator/lib/isEmail';
import {UNKOWNERROR} from '@/constant/NGContant';
import Logo from '@assets/background/login/NGLogo.svg';
import {useAppSelector} from '@/redux/config/hooks';
import {FigmaCTA} from '@constant/style/themFigma/CTA';
import {NGArrowLeft, NGMessage} from '@assets/iconExport/ExportIcon';
import {FigmaHeading} from '@constant/style/themFigma/FigmaHeading';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NgSvgBackground} from '@components/ng-background/NGSvgBackground';
import bg from '@assets/background/login/bg.svg';

function ForgotPassword() {
  const [loadingSubmit, setLoadingSubmit] = React.useState(false);
  const {t} = useTranslation();
  const [email, setEmail] = React.useState('');
  const navigate = useNavigate();
  const [message, setMessage] = React.useState('');
  const [check, setCheck] = React.useState(false);
  const [forgetPassword, {isLoading}] = useForgetPasswordMutation();
  const {theme} = useAppSelector(state => state.enterprise);
  const sentToEmail = async () => {
    setCheck(true);
    setLoadingSubmit(true);
    try {
      await forgetPassword({email})
        .unwrap()
        .then(() => {
          setMessage('Email sent sucessfully');
          setEmail('');
          return setLoadingSubmit(false);
        })
        .catch(err => {
          if (err) {
            const {
              data: {
                error: {message},
              },
            } = err;
            setMessage(message);
            return setLoadingSubmit(false);
          }
        });
    } catch (e) {
      setMessage(UNKOWNERROR);
      setLoadingSubmit(false);
    }
  };

  const checkMessage = (message: any) => {
    if (message.toLowerCase().includes('unable to find')) {
      return {message: <Alert severity="error">{message}</Alert>};
    } else if (message.toLowerCase().includes('sucessfully')) {
      return {
        message: (
          <Alert severity="success">
            {message.replace('sucessfully', 'successfully')}
          </Alert>
        ),
      };
    } else {
      return {
        message: <Alert severity="error">{UNKOWNERROR}</Alert>,
      };
    }
  };
  return (
    <Grid container component="main" sx={{height: '100vh'}}>
      <CssBaseline />
      <Grid
        item
        xs={12}
        sm={12}
        md={5}
        overflow={'hidden'}
        component={Paper}
        elevation={6}
        square>
        <Stack
          height={'55%'}
          width={'100%'}
          mt={10}
          ml={'10%'}
          justifyContent={'center'}>
          <Box width={['80%', '60%', '40%']}>
            <Stack width={'100%'} spacing={'80px'}>
              <img
                src={theme[0].logo ?? Logo}
                style={{height: '48px', maxWidth: '205px'}}
                alt={'Logo'}
              />
              <Stack spacing={'5%'}>
                <Stack
                  direction={'row'}
                  onClick={() => {
                    navigate(Navigate(Route.LOGIN));
                  }}>
                  <NGArrowLeft sx={{mr: 1, color: 'Primary.main'}} />
                  <NGText
                    sx={{
                      ...FigmaCTA.CtaMedium,
                    }}
                    text={t(Localization('form', 'Back to the connection'))}
                  />
                </Stack>
                <Stack spacing={2}>
                  <NGText
                    text={t(Localization('form', 'forget-password'))}
                    myStyle={{
                      ...FigmaHeading.H1,
                      width: '416px',
                    }}
                  />
                  <Stack width="416px" display={isLoading ? 'none' : 'flex'}>
                    {message.length > 0 && checkMessage(message)!.message}
                  </Stack>
                  <NGText
                    text={t(
                      Localization('form', 'text-bottom-forgot-password'),
                    )}
                    myStyle={{
                      ...FigmaBody.BodyMedium,
                      width: '416px',
                      color: 'Light.main',
                    }}
                  />
                </Stack>

                <Stack width={'100%'} spacing={3} py={'25px'}>
                  <NGInput
                    propsInput={{width: '416px'}}
                    require={false}
                    textLabel={t(Localization('form', 'email'))}
                    nameId={t(Localization('form', 'email'))}
                    placeholder={t(Localization('form', 'email'))}
                    value={email}
                    setValue={setEmail}
                    Icon={
                      <NGMessage
                        sx={{
                          color: email === '' ? '#000000' : 'Primary.main',
                          mt: pixelToRem(11),
                        }}
                      />
                    }
                    messageError={
                      check
                        ? Localization(
                            'form',
                            'The format of the email is incorrect',
                          )
                        : ''
                    }
                  />

                  <NGButton
                    myStyle={{
                      height: '48px',
                      width: '416px',
                      borderRadius: '6px',
                      mt: '32px',
                      px: '16px',
                      py: '12px',
                      '&.MuiButton-contained': {
                        fontWeight: 600,
                        textTransform: 'none',
                      },
                      '&.Mui-disabled': {
                        bgcolor: colorDisable,
                        color: colorWhite,
                      },
                      '&:hover': {
                        bgcolor: colorBlack,
                      },
                    }}
                    disabled={!isEmail(email) || loadingSubmit}
                    locationIcon="end"
                    icon={
                      loadingSubmit && (
                        <CircularProgress
                          sx={{color: 'White.main'}}
                          size={'1.5rem'}
                        />
                      )
                    }
                    title={
                      !loadingSubmit && t(Localization('form', 'Send-link'))
                    }
                    textSx={{
                      ...FigmaCTA.CtaMedium,
                      color: 'White.main',
                    }}
                    variant={'contained'}
                    bgColor={'primary'}
                    fontWeight={'bold'}
                    color={['Text2.main', 'Text2.dark']}
                    onClick={sentToEmail}
                  />
                </Stack>
              </Stack>
            </Stack>
          </Box>
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
    </Grid>
  );
}

export default ForgotPassword;
