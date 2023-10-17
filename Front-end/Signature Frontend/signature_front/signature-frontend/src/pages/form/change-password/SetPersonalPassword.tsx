import React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import {Center, VStack} from '@/theme';
import {useTranslation} from 'react-i18next';
import {Localization} from '@/i18n/lan';
import Box from '@mui/material/Box';
import NGInput from '@components/ng-inputField/NGInput';
import ValidatePersonalPassword from '@/utils/ValidatePersonalPassword';
import {useSelector} from 'react-redux';
import {IAuthentication} from '@/redux/slides/authentication/type';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Logo from '@assets/background/login/NGLogo.svg';
import NGText from '@components/ng-text/NGText';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {NGCorrect, NGFalse, NGKey} from '@assets/iconExport/Allicon';
import bg from '@assets/background/firstLogin/LogoBackground.svg';
import {NgSvgBackground} from '@components/ng-background/NGSvgBackground';
import {NGButton} from '@components/ng-button/NGButton';
import {
  useResetPasswordMutation,
  userSlide,
  useUpdatePasswordMutation,
} from '@/redux/slides/keycloak/user';
import {store} from '@/redux';
import {Stack} from '@mui/material';
import KeyLogo from '@assets/image/keylogo.png';
import VisaLogo from '@assets/image/visa.png';
import WorldISOLogo from '@assets/image/worldISO.png';
import {useAppSelector} from '@/redux/config/hooks';
import {
  AcceptOnlyNumAlphabetAndSomeSpecialCharacter,
  regSpecialCharacter,
} from '@/utils/common/RegSpecialCharacter';
import TooltipSpecialCharacterLayout from '@components/ng-tooltip/TooltipSpecailCharacter';

function SetPersonalPassword() {
  const {t} = useTranslation();
  const {sid} = useSelector<{authentication: IAuthentication}, IAuthentication>(
    state => state.authentication,
  );
  const {theme} = useAppSelector(state => state.enterprise);
  const [password, setPassword] = React.useState('');
  const {state} = ValidatePersonalPassword(password);
  const [updateNewPass] = useUpdatePasswordMutation();
  const [resetPassword] = useResetPasswordMutation();
  const stateTrue =
    state.Character12 &&
    state.Lower &&
    state.Num &&
    state.Special &&
    state.Upper &&
    AcceptOnlyNumAlphabetAndSomeSpecialCharacter(password);

  const updatePassword = async () => {
    try {
      const data = await store
        .dispatch(userSlide.endpoints.getUserById.initiate({userId: sid!}))
        .unwrap();
      const {attributes, id} = data as {
        id: string;
        attributes: {firstLogin: string[]; USER_ID: string[]};
      };
      await resetPassword({
        body: {type: 'password', temporary: false, value: password},
        userId: id,
      }).unwrap();

      await updateNewPass({
        userId: sid!,
        attributes: {...attributes, firstLogin: ['false']},
      }).unwrap();
      window.location.reload();
    } catch (error) {
      return error;
    }
  };
  /** handler special character not allow
   *  handler when length more than 12 characters**/
  const moreCondition = () => {
    if (regSpecialCharacter(password)) {
      return (
        <NGText
          text={t(Localization('form', 'special-character-check'))}
          myStyle={{
            position: 'absolute',
            top: '100%',
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
            top: '100%',
            textAlign: 'start',
            ...StyleConstant.textSmall,
            color: 'red',
            fontSize: '11px',
          }}
        />
      );
    }
  };
  const [check] = React.useState(false);
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
                <Grid
                  item
                  md={12}
                  sm={12}
                  mx={'auto'}
                  xs={12}
                  position={'relative'}>
                  <NGInput
                    size="small"
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
                    state={state}
                    setValue={(e: string) => setPassword(e)}
                    // colorOnfocus={'Primary'}
                    colorOutline={'Primary.main'}
                    Icon={
                      <NGKey
                        sx={{
                          color: password.length ? 'Primary.main' : undefined,
                          mt: '9px',
                          width: '28px',
                        }}
                      />
                    }
                    value={password}
                    type={'password'}
                    textLabel={t(Localization('form', 'password')) ?? ''}
                    nameId={'optional'}
                    placeholder={t(Localization('form', 'password'))}
                  />

                  <> {moreCondition()}</>
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
                  disabled={!stateTrue || password.length !== 12}
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
  );
}

export default SetPersonalPassword;
