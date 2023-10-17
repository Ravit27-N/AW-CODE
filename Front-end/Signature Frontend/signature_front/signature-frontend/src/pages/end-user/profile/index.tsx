import {NGInputField} from '@/components/ng-input/NGInputField';
import FlagComponent, {
  NGCountrySelect,
} from '@/components/ng-phone/NGCountrySelect';
import {CountryCode, ICountry} from '@/components/ng-phone/type';
import {UNKOWNERROR} from '@/constant/NGContant';
import {colorDisable} from '@/constant/style/StyleConstant';
import {FigmaBody} from '@/constant/style/themFigma/Body';
import {FigmaCTA} from '@/constant/style/themFigma/CTA';
import {FigmaHeading} from '@/constant/style/themFigma/FigmaHeading';
import {FigmaInput} from '@/constant/style/themFigma/Input';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {useLazyValidateUserEmailQuery} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {
  useChangeEndUserPasswordMutation,
  useLazyGetEndUserProfileQuery,
  useUpdateEndUserProfileMutation,
} from '@/redux/slides/end-user/profileSlide';
import {pixelToRem} from '@/utils/common/pxToRem';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
import {
  NGBusinessBag,
  NGInfo,
  NGKeyTab,
  NGUser,
} from '@assets/iconExport/Allicon';
import {NGButton} from '@components/ng-button/NGButton';
import NGText from '@components/ng-text/NGText';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import {
  Alert,
  Backdrop,
  CircularProgress,
  Divider,
  Grid,
  IconButton,
  Stack,
  outlinedInputClasses,
} from '@mui/material';
import {Box} from '@mui/system';
import {useSnackbar} from 'notistack';
import React from 'react';
import {NonUndefined, useForm} from 'react-hook-form';
import {useTranslation} from 'react-i18next';
import {parsePhoneNumber} from 'react-phone-number-input';
import flags from 'react-phone-number-input/flags';
import en from 'react-phone-number-input/locale/en.json';
import isEmail from 'validator/lib/isEmail';

export type EndUserProfileInformation = {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
};

export type EndUserProfileRoleSection = {
  functional: string;
  department: string;
};

export type EndUserProfilePasswords = {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
};

const defaultInformation: EndUserProfileInformation = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
};

const defaultRoleSection: EndUserProfileRoleSection = {
  functional: '',
  department: '',
};

const defaultPasswords: EndUserProfilePasswords = {
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
};

const defaultCountry: ICountry = {
  callingCode: '33',
  name: 'France',
  code: 'FR',
};

const ProfileEndUser = () => {
  const {t} = useTranslation();
  const {theme} = useAppSelector(state => state.enterprise);
  const activeColor = theme[0].mainColor;
  const {enqueueSnackbar, closeSnackbar} = useSnackbar();

  const [country, setCountry] = React.useState(defaultCountry);
  const [selectToggle, setToggle] = React.useState({
    phoneNumber: false,
    fixNumber: false,
  });
  // use to check is country callingCode has been change or not,
  // so we can disable or enable submit button.
  const [defaultCountryCallingCode, setDefaultCountryCallingCode] =
    React.useState(defaultCountry.callingCode);

  // slide for validate email
  const [validateUserEmailTrigger] = useLazyValidateUserEmailQuery();

  // slide get profile data
  const [getUserProfileTrigger, {data: userData, isLoading, error, isError}] =
    useLazyGetEndUserProfileQuery();

  // slide update profile information
  const [
    updateEndUserProfile,
    {isLoading: isUpdateLoading, error: updateEndUserProfileError},
  ] = useUpdateEndUserProfileMutation();

  // slide change password
  const [
    changeEndUserPassword,
    {isLoading: isChangeLoading, error: changeEndUserPasswordError},
  ] = useChangeEndUserPasswordMutation();

  // information form
  const {
    control: informationControl,
    setError,
    formState: {errors: informationErrors, dirtyFields: informationDirtyFields},
    handleSubmit: handleSubmitInformation,
    trigger: informationValidationTrigger,
    reset: informationReset,
  } = useForm({defaultValues: defaultInformation});

  // role form
  const {
    control: roleSectionControl,
    formState: {errors: roleSectionControlErrors},
    reset: roleSectionReset,
  } = useForm({defaultValues: defaultRoleSection});

  // change password form
  const {
    control: passwordControl,
    formState: {errors: passwordErrors},
    handleSubmit: handleSubmitPassword,
    reset: passwordReset,
    getValues: getPasswordFieldValues,
    trigger: passwordTrigger,
    watch,
  } = useForm({defaultValues: defaultPasswords});

  const handler_Disable = () => {
    if (
      watch('newPassword') === watch('confirmPassword') &&
      watch('newPassword').length > 0 &&
      watch('confirmPassword').length > 0 &&
      watch('currentPassword').length > 0
    ) {
      if (Object.keys(passwordErrors).length > 0) {
        return true;
      } else {
        return false;
      }
    }
    return true;
  };

  React.useEffect(() => {
    const handleFetchUserProfile = async () => {
      await getUserProfileTrigger(null);
    };
    handleFetchUserProfile();
  }, []);

  React.useEffect(() => {
    const formatPhone = parsePhoneNumber(userData?.phone ?? '');
    informationReset({
      firstName: userData?.firstName ?? '',
      lastName: userData?.lastName ?? '',
      email: userData?.email ?? '',
      phone: formatPhone?.nationalNumber ?? '',
    });
    roleSectionReset({
      functional: userData?.functional ?? '',
      department: userData?.businessUnitInfo?.unitName ?? '',
    });

    if (formatPhone) {
      // * bug: if the country have more than one code (Ex: russia: ["RU", "KZ"]) formatPhone.country = undefined
      setCountry({
        callingCode:
          formatPhone.countryCallingCode ?? defaultCountry.callingCode,
        code: formatPhone.country ?? defaultCountry.code,
        name: en[formatPhone.country ?? defaultCountry.code],
      });

      setDefaultCountryCallingCode(
        formatPhone.countryCallingCode ?? defaultCountry.callingCode,
      );
    }
  }, [userData]);
  React.useEffect(() => {
    if (isError && error) {
      enqueueSnackbar(
        /** temporarily fix, should create util function to handle error and return the error message.*/
        (error as any)?.data?.error?.message ?? UNKOWNERROR,
        {
          variant: 'errorSnackbar',
        },
      );
    }
    return () => {
      // when exit profile page close Snackbar too
      closeSnackbar();
    };
  }, [isError]);
  const handleClose = () => {
    setToggle({phoneNumber: false, fixNumber: false});
  };

  const handlePhone = (data: string) => {
    const res = data.split(' ');
    setCountry({
      name: res[0],
      callingCode: res[1],
      code: res[2] as CountryCode,
    });
    setError('phone', {message: undefined});
  };

  // handle update profile information
  const onSubmitInformation = async (
    data: NonUndefined<typeof informationControl._defaultValues>,
  ) => {
    try {
      const {firstName, lastName, email, phone} = data;
      if (firstName && lastName && email && phone) {
        await updateEndUserProfile({
          firstName,
          lastName,
          email,
          phone: `+${country.callingCode}${phone}`,
          businessId: userData!.businessUnitInfo.id,
        }).unwrap();
      }
    } catch (error) {
      enqueueSnackbar((error as any)?.data?.error?.message ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
      return error;
    }
  };

  // handle change password
  const onSubmitPassword = async (
    data: NonUndefined<typeof passwordControl._defaultValues>,
  ) => {
    try {
      const {currentPassword, newPassword, confirmPassword} = data;
      if (currentPassword && newPassword && confirmPassword) {
        await changeEndUserPassword({
          currentPassword,
          newPassword,
          confirmPassword,
        }).unwrap();
        passwordReset(defaultPasswords);
      }
    } catch (error) {
      // Handle error here Noted:not yet implement
      return error;
    }
  };

  return (
    <Stack width={'100%'} bgcolor={'white'} overflow={'auto'}>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={isLoading || isUpdateLoading || isChangeLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      <Box
        sx={{
          height: pixelToRem(156),
          backgroundImage: `url(${bgLogo})`,
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
          py: 5,
          px: '72px',
          position: 'relative',
        }}>
        <Stack gap={pixelToRem(12)}>
          <NGText
            text={`${userData?.firstName} ${userData?.lastName}`}
            myStyle={{
              ...FigmaHeading.H2,
              textTransform: 'capitalize',
            }}
          />
          <NGText
            text={`${t(Localization('end-user-profile', 'my-profile'))}`}
            iconStart={<NGUser sx={{color: activeColor}} />}
            myStyle={{
              ...FigmaBody.BodyLage,
            }}
          />
        </Stack>
      </Box>
      <Stack
        spacing={5}
        height={`calc(100vh - 190px)`}
        sx={{
          overflow: 'auto',
          p: '40px 72px',
          '&::-webkit-scrollbar': {
            width: '0',
          },
        }}>
        <Grid container>
          <Grid item md={12} lg={8}>
            {/** Information **/}
            <Stack>
              <NGText
                text={`${t(
                  Localization('end-user-profile', 'my-information'),
                )}`}
                iconStart={
                  <NGInfo sx={{color: activeColor, mr: 1}} fontSize="small" />
                }
                myStyle={{
                  ...FigmaHeading.H3,
                }}
              />
              <form onSubmit={handleSubmitInformation(onSubmitInformation)}>
                <Grid container spacing={2} mt={0.5}>
                  {/** Name **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(Localization('end-user-profile', 'name'))}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfileInformation>
                        size="small"
                        sx={{...FigmaInput.InputTextMedium}}
                        control={informationControl}
                        eMessage={t(Localization('form-error', 'name'))!}
                        inputProps={{color: 'red'}}
                        errorInput={informationErrors?.lastName}
                        typeInput={'name'}
                        type={'text'}
                        name={`lastName`}
                        placeholder={
                          t(Localization('end-user-profile', 'name'))!
                        }
                      />
                    </Stack>
                  </Grid>
                  {/** Family Name **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(
                            Localization('end-user-profile', 'first-name'),
                          )}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfileInformation>
                        size="small"
                        sx={{...FigmaInput.InputTextMedium}}
                        control={informationControl}
                        eMessage={t(Localization('form-error', 'name'))!}
                        inputProps={{color: 'red'}}
                        errorInput={informationErrors?.firstName}
                        typeInput={'name'}
                        type={'text'}
                        name={`firstName`}
                        placeholder={
                          t(Localization('end-user-profile', 'first-name'))!
                        }
                      />
                    </Stack>
                  </Grid>
                  {/** Email **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(
                            Localization(
                              'end-user-profile',
                              'professional-e-mail',
                            ),
                          )}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfileInformation>
                        size="small"
                        sx={{...FigmaInput.InputTextMedium}}
                        disabled
                        control={informationControl}
                        eMessage={t(Localization('form-error', 'email'))!}
                        inputProps={{color: 'red'}}
                        errorInput={informationErrors?.email}
                        typeInput={'email'}
                        type={'text'}
                        name={`email`}
                        onAfterChange={() =>
                          informationValidationTrigger('email')
                        }
                        placeholder={
                          t(
                            Localization(
                              'end-user-profile',
                              'professional-e-mail',
                            ),
                          )!
                        }
                        validate={async value => {
                          if (isEmail(value)) {
                            // avoid check user own email
                            if (value === userData?.email) {
                              return undefined;
                            }
                            const isExisted = await validateUserEmailTrigger({
                              email: value,
                            }).unwrap();
                            if (isExisted) {
                              return t(
                                Localization('form-error', 'existed-email'),
                              )!;
                            }
                            return undefined;
                          }

                          return t(
                            Localization('form-error', 'email-invalid'),
                          )!;
                        }}
                      />
                    </Stack>
                  </Grid>
                  {/** Number Phone **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(
                            Localization('end-user-profile', 'phone-number'),
                          )}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <Stack sx={{gap: 1}} direction={'row'}>
                        <NGCountrySelect
                          sx={{
                            display: 'block',
                            width: 0,
                            visibility: 'hidden',
                          }}
                          labels={en}
                          value={country.code}
                          selectToggle={selectToggle.phoneNumber}
                          handleClose={handleClose}
                          selectChange={handlePhone}
                        />
                        <IconButton
                          disableTouchRipple
                          disableFocusRipple
                          sx={{
                            padding: 0,
                            '&:hover': {
                              backgroundColor: 'transparent',
                            },
                          }}
                          onClick={() =>
                            setToggle({
                              ...selectToggle,
                              phoneNumber: !selectToggle.phoneNumber,
                            })
                          }>
                          <FlagComponent
                            flags={flags}
                            country={country.code || 'KH'}
                            countryName={country.name}
                          />
                          <ArrowDropDownIcon />
                        </IconButton>
                        <Stack sx={{width: '100%'}}>
                          <NGInputField<EndUserProfileInformation>
                            size="small"
                            sx={{...FigmaInput.InputTextMedium}}
                            control={informationControl}
                            eMessage={
                              t(Localization('form-error', 'phone-number'))!
                            }
                            inputProps={{color: 'red'}}
                            errorInput={informationErrors?.phone}
                            typeInput={'phone'}
                            countryCode={country.code}
                            type={'text'}
                            name={`phone`}
                            placeholder={
                              t(
                                Localization(
                                  'end-user-profile',
                                  'phone-number',
                                ),
                              )!
                            }
                          />
                        </Stack>
                      </Stack>
                    </Stack>
                  </Grid>
                  {updateEndUserProfileError && (
                    <Alert
                      sx={{
                        bgcolor: 'transparent',
                        width: '100%',
                        justifyContent: 'center',
                      }}
                      severity={'error'}>
                      <NGText
                        text={
                          (updateEndUserProfileError as any)?.data?.error
                            ?.message ?? UNKOWNERROR
                        }
                        myStyle={{fontSize: pixelToRem(12), color: 'error'}}
                      />
                    </Alert>
                  )}
                  {/** Action Button **/}
                  <Grid item xs={12}>
                    <NGButton
                      type={'submit'}
                      btnProps={{
                        disableFocusRipple: true,
                        disableRipple: true,
                        disableTouchRipple: true,
                      }}
                      locationIcon="end"
                      disabled={
                        defaultCountryCallingCode === country.callingCode &&
                        (Object.keys(informationErrors).length > 0 ||
                          Object.keys(informationDirtyFields).length <= 0)
                      }
                      color={['#ffffff', '#ffffff']}
                      variant="contained"
                      fontSize="11px"
                      myStyle={{
                        ...FigmaCTA.CtaSmall,
                        '&.Mui-disabled': {
                          bgcolor: colorDisable,
                        },
                        '&.MuiButtonBase-root': {
                          borderColor: activeColor ?? 'Primary.main',
                        },
                        bgcolor: activeColor ?? 'Primary.main',
                        p: '8px, 16px',
                        height: '36px',
                        borderRadius: '6px',
                        borderColor: '#000000',
                      }}
                      fontWeight="600"
                      title={t(
                        Localization('end-user-profile', 'save-changes'),
                      )}
                    />
                  </Grid>
                </Grid>
              </form>
            </Stack>

            <Divider sx={{width: '100%', my: 5}} />

            {/** Role **/}
            <Stack>
              <NGText
                text={`${t(Localization('end-user-profile', 'role'))}`}
                iconStart={
                  <NGBusinessBag
                    sx={{color: activeColor, mr: 1}}
                    fontSize="small"
                  />
                }
                myStyle={{
                  ...FigmaHeading.H3,
                }}
              />
              <form>
                <Grid container spacing={2} mt={0.5}>
                  {/** Function **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(Localization('end-user-profile', 'function'))}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfileRoleSection>
                        size="small"
                        sx={{
                          ...FigmaInput.InputTextMedium,
                          [`&.${outlinedInputClasses.root}`]: {
                            backgroundColor: 'LightGray.main',
                            border: 'none',
                            [`& .${outlinedInputClasses.disabled}`]: {
                              WebkitTextFillColor: 'Black',
                            },
                          },
                        }}
                        control={roleSectionControl}
                        eMessage={
                          t(Localization('form-error', 'required-field'))!
                        }
                        inputProps={{color: 'red'}}
                        errorInput={roleSectionControlErrors?.functional}
                        typeInput={'name'}
                        type={'text'}
                        disabled
                        name={`functional`}
                        placeholder={
                          t(Localization('end-user-profile', 'function'))!
                        }
                      />
                    </Stack>
                  </Grid>
                  {/** Equip **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(
                            Localization('end-user-profile', 'department'),
                          )}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfileRoleSection>
                        size="small"
                        sx={{
                          ...FigmaInput.InputTextMedium,
                          [`&.${outlinedInputClasses.root}`]: {
                            backgroundColor: 'LightGray.main',
                            border: 'none',
                            [`& .${outlinedInputClasses.disabled}`]: {
                              WebkitTextFillColor: 'Black',
                            },
                          },
                        }}
                        control={roleSectionControl}
                        eMessage={
                          t(Localization('form-error', 'required-field'))!
                        }
                        inputProps={{color: 'red'}}
                        errorInput={roleSectionControlErrors?.department}
                        typeInput={'name'}
                        type={'text'}
                        disabled
                        name={`department`}
                        placeholder={
                          t(Localization('end-user-profile', 'service'))!
                        }
                      />
                    </Stack>
                  </Grid>
                </Grid>
              </form>
            </Stack>

            <Divider sx={{width: '100%', my: 5}} />

            {/** Password **/}
            <Stack>
              <NGText
                text={`${t(Localization('end-user-profile', 'password'))}`}
                iconStart={<NGKeyTab sx={{color: activeColor, mr: 1}} />}
                myStyle={{
                  ...FigmaHeading.H3,
                }}
              />
              <form onSubmit={handleSubmitPassword(onSubmitPassword)}>
                <Grid container spacing={2} mt={0.5}>
                  {/** old password **/}
                  <Grid item xs={12}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(
                            Localization('end-user-profile', 'old-password'),
                          )}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfilePasswords>
                        size="small"
                        sx={{...FigmaInput.InputTextMedium}}
                        control={passwordControl}
                        eMessage={
                          t(Localization('form-error', 'required-field'))!
                        }
                        inputProps={{color: 'red'}}
                        errorInput={passwordErrors?.currentPassword}
                        typeInput={'notValidateHaveEye'}
                        type={'text'}
                        name={`currentPassword`}
                        placeholder={
                          t(Localization('end-user-profile', 'old-password'))!
                        }
                      />
                    </Stack>
                  </Grid>
                  {/** new password **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(
                            Localization('end-user-profile', 'new-password'),
                          )}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfilePasswords>
                        size="small"
                        sx={{...FigmaInput.InputTextMedium}}
                        control={passwordControl}
                        eMessage={
                          t(Localization('form-error', 'required-field'))!
                        }
                        inputProps={{color: 'red'}}
                        errorInput={passwordErrors?.newPassword}
                        typeInput={'ValidationPass'}
                        name={`newPassword`}
                        type={'text'}
                        placeholder={
                          t(Localization('end-user-profile', 'new-password'))!
                        }
                      />
                    </Stack>
                  </Grid>
                  {/** confirm password **/}
                  <Grid item xs={12} md={6}>
                    <Stack width="100%">
                      <Stack direction={'row'}>
                        <NGText
                          text={t(
                            Localization(
                              'end-user-profile',
                              'confirm-new-password',
                            ),
                          )}
                          myStyle={{...FigmaInput.InputLabelMedium, mb: 1}}
                        />
                      </Stack>
                      <NGInputField<EndUserProfilePasswords>
                        size="small"
                        sx={{...FigmaInput.InputTextMedium}}
                        control={passwordControl}
                        eMessage={
                          t(Localization('form-error', 'required-field'))!
                        }
                        inputProps={{color: 'red'}}
                        errorInput={passwordErrors?.confirmPassword}
                        typeInput={'notValidateHaveEye'}
                        name={`confirmPassword`}
                        type={'text'}
                        placeholder={
                          t(
                            Localization(
                              'end-user-profile',
                              'confirm-new-password',
                            ),
                          )!
                        }
                        onAfterChange={() => {
                          passwordTrigger('confirmPassword');
                        }}
                        validate={value => {
                          return value === getPasswordFieldValues('newPassword')
                            ? undefined
                            : t(
                                Localization(
                                  'end-user-profile',
                                  'confirm-new-password-invalid',
                                ),
                              )!;
                        }}
                      />
                    </Stack>
                  </Grid>
                  {/** Password Validation Message **/}
                  <Grid item xs={12}>
                    <NGText
                      myStyle={{
                        ...FigmaBody.BodySmall,
                        color: 'Placeholder.main',
                      }}
                      text={t(
                        Localization('end-user-profile', 'password-validation'),
                      )}
                    />
                  </Grid>
                  {changeEndUserPasswordError && (
                    <Alert
                      sx={{
                        bgcolor: 'transparent',
                        width: '100%',
                        justifyContent: 'center',
                      }}
                      severity={'error'}>
                      <NGText
                        text={
                          (changeEndUserPasswordError as any)?.data?.error
                            ?.message ?? UNKOWNERROR
                        }
                        myStyle={{fontSize: pixelToRem(12), color: 'error'}}
                      />
                    </Alert>
                  )}
                  {/** Action Button **/}
                  <Grid item xs={12}>
                    <NGButton
                      type={'submit'}
                      btnProps={{
                        disableFocusRipple: true,
                        disableRipple: true,
                        disableTouchRipple: true,
                      }}
                      disabled={handler_Disable()}
                      locationIcon="end"
                      color={['#ffffff', '#ffffff']}
                      variant="contained"
                      fontSize="11px"
                      myStyle={{
                        ...FigmaCTA.CtaSmall,
                        '&.Mui-disabled': {
                          bgcolor: colorDisable,
                        },
                        '&.MuiButtonBase-root': {
                          borderColor: activeColor ?? 'Primary.main',
                        },
                        bgcolor: activeColor ?? 'Primary.main',
                        p: '8px, 16px',
                        height: '36px',
                        borderRadius: '6px',
                        borderColor: '#000000',
                      }}
                      fontWeight="600"
                      title={t(
                        Localization('end-user-profile', 'save-changes'),
                      )}
                    />
                  </Grid>
                </Grid>
              </form>
            </Stack>
          </Grid>
          <Grid item md={12} lg={4}></Grid>
        </Grid>
      </Stack>
    </Stack>
  );
};

export default ProfileEndUser;
