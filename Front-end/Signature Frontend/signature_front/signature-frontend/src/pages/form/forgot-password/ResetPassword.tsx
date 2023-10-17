import {Route} from '@/constant/Route';
import {StyleConstant} from '@/constant/style/StyleConstant';
import bg from '@assets/background/firstLogin/LogoBackground.svg';
import {Localization} from '@/i18n/lan';
import {useResetPasswordMutation} from '@/redux/slides/project-management/user';
import {Center, VStack} from '@/theme';
import ValidatePersonalPassword from '@/utils/ValidatePersonalPassword';
import {Navigate} from '@/utils/common';
import {
  AcceptOnlyNumAlphabetAndSomeSpecialCharacter,
  regSpecialCharacter,
} from '@/utils/common/RegSpecialCharacter';
import {NGButton} from '@components/ng-button/NGButton';
import NGInput from '@components/ng-inputField/NGInput';
import NGText from '@components/ng-text/NGText';
import {Alert, Backdrop, CircularProgress, Grid, Paper} from '@mui/material';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate} from 'react-router-dom';
import {UNKOWNERROR} from '@/constant/NGContant';
import {NgSvgBackground} from '@components/ng-background/NGSvgBackground';
import Logo from '@assets/background/login/NGLogo.svg';
import {NGCorrect, NGFalse, NGKey} from '@assets/iconExport/Allicon';
import KeyLogo from '@assets/image/keylogo.png';
import VisaLogo from '@assets/image/visa.png';
import WorldISOLogo from '@assets/image/worldISO.png';
import {useAppSelector} from '@/redux/config/hooks';
import ResetPasswordActivate from '@pages/protect-route/ResetPasswordActivate';
import TooltipSpecialCharacterLayout from '@components/ng-tooltip/TooltipSpecailCharacter';

function ResetPassword() {
  const [resetPassword] = useResetPasswordMutation();
  const {t} = useTranslation();
  const [resetToken, setResetToken] = React.useState('');
  const [isError, setIsError] = React.useState(true);
  const [password, setPassword] = React.useState('');
  const [rePassword, setRePassword] = React.useState('');
  const {state} = ValidatePersonalPassword(password);
  const {theme} = useAppSelector(state => state.enterprise);
  const [check] = React.useState(false);
  const navigate = useNavigate();
  const stateTrue =
    state.Character12 &&
    state.Lower &&
    state.Num &&
    state.Special &&
    state.Upper &&
    AcceptOnlyNumAlphabetAndSomeSpecialCharacter(password);
  const [message, setMessage] = React.useState('');
  const [loadingSubmit, setLoadingSubmit] = React.useState(false);
  const checkMessage = (message: any) => {
    if (message.toLowerCase().includes('error')) {
      return {message: <Alert severity="error">{message}</Alert>};
    } else if (message.toLowerCase().includes('reset token')) {
      return {message: <Alert severity="error">{`Token expired.`}</Alert>};
    } else if (message.toLowerCase().includes('successfully')) {
      return {message: <Alert severity="success">{message}</Alert>};
    } else {
      return {
        message: <Alert severity="error">{UNKOWNERROR}</Alert>,
      };
    }
  };
  const updatePassword = async () => {
    setLoadingSubmit(true);
    try {
      await resetPassword({
        newPassword: password,
        confirmPassword: rePassword,
        resetToken,
      })
        .unwrap()
        .then(() => {
          setPassword('');
          setRePassword('');
          setLoadingSubmit(false);
          setMessage('Password reset successfully.');
          return navigate(Navigate(Route.PASSWORD_MODIFY));
        })
        .catch(err => {
          if (err) {
            const {
              data: {
                error: {message},
              },
            } = err;
            setPassword('');
            setRePassword('');
            setMessage(message);
            return setLoadingSubmit(false);
          }
        });
    } catch (e) {
      setMessage(UNKOWNERROR);
      setLoadingSubmit(false);
    }
  };
  /** handler special character not allow
   *  handler when length more than 12 characters
   *  handler when password and re-password isn't the same **/
  const moreCondition = () => {
    if (regSpecialCharacter(password)) {
      return (
        <NGText
          text={t(Localization('form', 'special-character-check'))}
          myStyle={{
            position: 'absolute',
            top: '30%',
            textAlign: 'start',
            ...StyleConstant.textSmall,
            color: 'red',
            fontSize: '11px',
          }}
        />
      );
    }
    if (password.length > 12 && stateTrue) {
      return (
        <NGText
          text={t(Localization('form', 'more-than-12-characters'))}
          myStyle={{
            position: 'absolute',
            top: '30%',
            textAlign: 'start',
            ...StyleConstant.textSmall,
            color: 'red',
            fontSize: '11px',
          }}
        />
      );
    }
    if (
      password !== rePassword &&
      stateTrue &&
      rePassword.length === 12 &&
      password.length === 12
    ) {
      return (
        <NGText
          text={t(Localization('form', 'password-re-password-must-same'))}
          myStyle={{
            position: 'absolute',
            top: '30%',
            textAlign: 'start',
            ...StyleConstant.textSmall,
            color: 'red',
            fontSize: '11px',
          }}
        />
      );
    }
  };
  /** just function for handler spacing when have error  **/
  const conditionSpacing = () => {
    return (
      regSpecialCharacter(password) ||
      (password.length > 12 && stateTrue) ||
      (password !== rePassword &&
        stateTrue &&
        rePassword.length === 12 &&
        password.length === 12)
    );
  };
  checkMessage(message);
  return (
    <>
      <CssBaseline />
      {isError ? (
        <ResetPasswordActivate
          setResetToken={setResetToken}
          setIsError={setIsError}
        />
      ) : (
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
            <Center height={'100%'}>
              <Box
                sx={{
                  justifyContent: 'center',
                  mx: 4,
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'flex-start',
                  ml: '5%',
                }}>
                <Grid container md={12} sm={12} rowSpacing={'10%'} item>
                  <Grid item md={12} sm={12}>
                    <img
                      src={theme[0].logo ?? Logo}
                      style={{height: '48px', width: 'auto'}}
                      alt={'Logo'}
                    />
                  </Grid>
                  <Grid item md={12} sm={12} rowSpacing={'8px'}>
                    <NGText
                      sx={{
                        ...StyleConstant.textSmall,
                        fontWeight: 500,
                        color: '#000000',
                        fontSize: '14px',
                      }}
                      text={t(Localization('form', 'new password'))}
                    />
                    <NGText
                      text={''}
                      dataMulti={[
                        t(Localization('form', 'set-your-personal')),
                        t(Localization('form', 'personal-password')),
                      ]}
                      multiLine={true}
                      rows={2}
                      propsChildMulti={{
                        ...StyleConstant.textBold,
                        fontSize: '31px',
                        color: '#000000',
                        fontWeight: 600,
                      }}
                    />
                  </Grid>
                </Grid>
                <Box mt={'40px'} width={'100%'}>
                  <Grid
                    container
                    md={12}
                    sm={12}
                    xs={12}
                    rowSpacing={2}
                    item
                    mb={1}>
                    <Grid item md={12} sm={12} mx={'auto'} xs={12}>
                      <Stack
                        spacing={conditionSpacing() ? 3 : 1}
                        position={'relative'}>
                        <NGInput
                          require={false}
                          state={state}
                          setValue={(e: string) => setPassword(e)}
                          type="password"
                          value={password}
                          textLabel={
                            t(Localization('form', 'Choose a new password')) ??
                            ''
                          }
                          nameId={'PP'}
                          placeholder={t(
                            Localization('form', 'Choose a new password'),
                          )}
                          size="small"
                          limitLength={13}
                          propsInput={{
                            mb: '5px',
                            width: '100%',
                            '& fieldset': {
                              borderRadius: '6px',
                              alignItems: 'center',
                              justifyContent: 'center',
                              height: '40px',
                            },
                          }}
                          // colorOnfocus={'Primary'}
                          colorOutline={'Primary.main'}
                          Icon={
                            <NGKey
                              sx={{
                                color: password.length
                                  ? 'Primary.main'
                                  : undefined,
                                mt: '9px',
                                width: '28px',
                              }}
                            />
                          }
                        />
                        <NGInput
                          colorOnfocus={'info'}
                          colorOutline={'info.main'}
                          require={false}
                          state={state}
                          setValue={(e: string) => setRePassword(e)}
                          type="password"
                          value={rePassword}
                          textLabel={
                            t(
                              Localization(
                                'form',
                                'Re-enter the chosen password',
                              ),
                            ) ?? ''
                          }
                          nameId={'optional'}
                          placeholder={t(
                            Localization(
                              'form',
                              'Re-enter the chosen password',
                            ),
                          )}
                        />
                        {/** handler different password and re-password **/}
                        <>{moreCondition()}</>
                      </Stack>
                    </Grid>
                    <VStack spacing={1}>
                      <NGText
                        text={t(Localization('form', 'your-password'))}
                        myStyle={{
                          mt: '20px',
                          opacity: check ? 1 : 0.5,
                          color: 'black.main',
                          fontSize: '12px',
                          fontWeight: 600,
                        }}
                      />
                      <NGText
                        disable={!state.Upper}
                        iconStart={
                          state.Upper ? (
                            <NGCorrect
                              sx={{
                                color: 'green',
                                fontSize: '10px',
                                mr: 0.7,
                              }}
                            />
                          ) : (
                            <NGFalse
                              sx={{
                                fontSize: '10px',
                                opacity: check ? 1 : 0.5,
                                mr: 0.7,
                              }}
                            />
                          )
                        }
                        text={t(Localization('text', '1 capital letter '))}
                        myStyle={{
                          opacity: check ? 1 : 0.5,
                          color: 'black.main',
                          fontSize: '12px',
                        }}
                      />

                      <NGText
                        disable={!state.Lower}
                        iconStart={
                          state.Lower ? (
                            <NGCorrect
                              sx={{
                                color: 'green',
                                fontSize: '10px',
                                mr: 0.7,
                              }}
                            />
                          ) : (
                            <NGFalse
                              sx={{
                                fontSize: '10px',
                                mr: 0.7,
                                opacity: check ? 1 : 0.5,
                              }}
                            />
                          )
                        }
                        text={t(Localization('text', '1 lower case letter'))}
                        myStyle={{
                          opacity: check ? 1 : 0.5,
                          color: 'black.main',
                          fontSize: '12px',
                        }}
                      />
                      <NGText
                        disable={!state.Num}
                        iconStart={
                          state.Num ? (
                            <NGCorrect
                              sx={{
                                color: 'green',
                                fontSize: '10px',
                                mr: 0.7,
                              }}
                            />
                          ) : (
                            <NGFalse
                              sx={{
                                fontSize: '10px',
                                mr: 0.7,
                                opacity: check ? 1 : 0.5,
                              }}
                            />
                          )
                        }
                        text={t(Localization('text', '1 number'))}
                        myStyle={{
                          opacity: check ? 1 : 0.5,
                          color: 'black.main',
                          fontSize: '12px',
                        }}
                      />
                      {/** tooltip special characters **/}
                      <TooltipSpecialCharacterLayout>
                        <NGText
                          disable={!state.Special}
                          iconStart={
                            state.Special ? (
                              <NGCorrect
                                sx={{
                                  color: 'green',
                                  fontSize: '10px',
                                  mr: 0.7,
                                }}
                              />
                            ) : (
                              <NGFalse
                                sx={{
                                  opacity: check ? 1 : 0.5,
                                  fontSize: '10px',
                                  mr: 0.7,
                                }}
                              />
                            )
                          }
                          text={t(Localization('text', '1 special character'))}
                          myStyle={{
                            opacity: check ? 1 : 0.5,
                            color: 'black.main',
                            fontSize: '12px',
                          }}
                        />
                      </TooltipSpecialCharacterLayout>

                      <NGText
                        disable={!state.Character12}
                        iconStart={
                          state.Character12 ? (
                            <NGCorrect
                              sx={{
                                color: 'green',
                                fontSize: '10px',
                                mr: 0.7,
                              }}
                            />
                          ) : (
                            <NGFalse
                              sx={{
                                opacity: check ? 1 : 0.5,
                                fontSize: '10px',
                                mr: 0.7,
                              }}
                            />
                          )
                        }
                        text={t(Localization('form', 'least-12-characters'))}
                        myStyle={{
                          opacity: check ? 1 : 0.5,
                          fontSize: '12px',
                          color: 'black.main',
                        }}
                      />
                    </VStack>
                  </Grid>
                  <Stack sx={{justifyContent: 'space-between', height: '25vh'}}>
                    <NGButton
                      title={
                        <NGText
                          text={t(
                            Localization('form', 'confirm-your-new-password'),
                          )}
                          myStyle={{
                            fontWeight: 600,
                            color: '#ffffff',
                            fontSize: '13px',
                          }}
                        />
                      }
                      icon={<></>}
                      // bgColor={'Primary'}
                      myStyle={{
                        '&.MuiButtonBase-root.Mui-disabled': {
                          bgcolor: '#71717A',
                        },
                        mt: '32px',
                        textTransform: 'none',
                        width: '100%',
                        p: '12px 16px',
                        height: '48px',
                        bgcolor: 'Primary.main',
                      }}
                      onClick={updatePassword}
                      disabled={
                        !(password === rePassword && stateTrue) ||
                        loadingSubmit ||
                        !(password.length === 12 && rePassword.length === 12)
                      }
                    />
                    <Stack
                      sx={{width: '100%'}}
                      direction={'row'}
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
              </Box>
            </Center>
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
      )}
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={loadingSubmit}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </>
  );
}

export default ResetPassword;
