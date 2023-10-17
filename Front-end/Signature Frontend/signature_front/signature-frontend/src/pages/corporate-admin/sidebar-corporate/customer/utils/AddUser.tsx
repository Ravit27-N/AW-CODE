import {NGPlusIcon} from '@/assets/Icon';
import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {NGInputField} from '@/components/ng-input/NGInputField';
import NGText from '@/components/ng-text/NGText';
import {UNKOWNERROR} from '@/constant/NGContant';

import {colorDisable} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {
  useLazyGetDepartmentOrServiceQuery,
  useLazyGetUserAccessQuery,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {
  useCreateUserMutation,
  useLazyValidateUserEmailQuery,
} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {splitUserCompany} from '@/utils/common/String';
import {pixelToRem} from '@/utils/common/pxToRem';
import FlagComponent, {
  NGCountrySelect,
} from '@components/ng-phone/NGCountrySelect';
import {CountryCode, ICountry} from '@components/ng-phone/type';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import CloseIcon from '@mui/icons-material/Close';
import {
  Alert,
  Backdrop,
  CircularProgress,
  IconButton,
  MenuItem,
  Stack,
} from '@mui/material';
import Grid from '@mui/material/Grid';
import {SerializedError} from '@reduxjs/toolkit';
import {FetchBaseQueryError} from '@reduxjs/toolkit/query';
import {t} from 'i18next';
import React from 'react';
import {
  FormProvider,
  NonUndefined,
  useForm,
  useFormContext,
} from 'react-hook-form';
import flags from 'react-phone-number-input/flags';
import en from 'react-phone-number-input/locale/en.json';
import isEmail from 'validator/lib/isEmail';

type IAddUser = {
  open: boolean;
  onClose: () => void;
  onAddSuccess?: () => void;
};

export type ICreateCorporateUserForm = {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  functional: string;
  department: string;
  userAccessId: string;
  password: string;
};

const defaultValues: ICreateCorporateUserForm = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  functional: '',
  department: '',
  userAccessId: '',
  password: '',
};

export const AddUser = ({open, onClose, onAddSuccess}: IAddUser) => {
  const methods = useForm({
    // mode: 'onChange',
    defaultValues,
  });
  const handleClose = () => {
    methods.reset();
    onClose();
  };
  return (
    <FormProvider {...methods}>
      <NGDialog
        open={open}
        sx={{
          '& .MuiPaper-root': {
            boxSizing: 'border-box',
            borderRadius: '16px',
          },
        }}
        sxProp={{
          titleSx: {
            borderRadius: '28px',
            p: '20px',
          },
          contentsSx: {
            p: 0,
          },
        }}
        titleDialog={<AddUserTitle />}
        contentDialog={
          <AddUserContent onClose={handleClose} onAddSuccess={onAddSuccess} />
        }
        actionDialog={<AddUserAction onClose={handleClose} />}
      />
    </FormProvider>
  );
};

const AddUserTitle = (): JSX.Element => {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  return (
    <Stack gap={'12px'} alignItems={'center'} direction={'row'}>
      <NGPlusIcon
        sx={{
          height: '20px',
          mt: '-1px',
          color: activeColor,
        }}
      />
      <NGText
        myStyle={{width: '517px', height: '28px'}}
        text={t(Localization('enterprise-dashboard', 'add-a-user'))}
        fontSize={'18px'}
        fontWeight="600"
      />
    </Stack>
  );
};

const AddUserContent = ({
  onClose,
  onAddSuccess,
}: Omit<IAddUser, 'open'>): JSX.Element => {
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const [country, setCountry] = React.useState<ICountry>({
    callingCode: '33',
    name: 'France',
    code: 'FR',
  });

  const [
    createCorporateUser,
    {isLoading: isCreateLoading, error: createError},
  ] = useCreateUserMutation({});

  const [selectToggle, setToggle] = React.useState({
    phoneNumber: false,
    fixNumber: false,
  });

  const [trigger, departmentData] = useLazyGetDepartmentOrServiceQuery();
  const [userAccessTrigger, userAccessData] = useLazyGetUserAccessQuery();
  const [validateUserEmailTrigger] = useLazyValidateUserEmailQuery();

  React.useEffect(() => {
    const handleFetchDepartmentOrService = async () => {
      await trigger({
        companyId: company.companyId,
        page: 1,
        pageSize: 15,
      }).unwrap();
    };
    const handleFetchUserAccess = async () => {
      await userAccessTrigger({
        pageSize: 30,
      }).unwrap();
    };

    if (company.companyId) {
      handleFetchDepartmentOrService().then();
      handleFetchUserAccess().then();
    }
  }, [company.companyId]);

  const {
    control,
    formState: {errors},
    getValues,
    trigger: hookFromValidationTrigger,
    setError,
    handleSubmit,
  } = useFormContext<ICreateCorporateUserForm>();
  // handle create user
  const onSubmit = async (
    data: NonUndefined<typeof control._defaultValues>,
  ) => {
    try {
      const {
        firstName,
        lastName,
        email,
        phone,
        functional,
        department,
        userAccessId,
        password,
      } = data;
      if (
        firstName &&
        lastName &&
        email &&
        phone &&
        functional &&
        department &&
        userAccessId &&
        password
      ) {
        await createCorporateUser({
          firstName: firstName.trim(),
          lastName: lastName.trim(),
          email,
          phone: `+${country.callingCode}${phone}`,
          businessId: department,
          functional: functional.trim(),
          userAccessId,
          password,
          active: true,
        }).unwrap();

        if (typeof onAddSuccess === 'function') {
          onAddSuccess();
        }
        onClose();
      }
    } catch (error) {
      // Handle error here Noted:not yet implement
      return error;
    }
  };
  React.useEffect(() => {
    if (getValues(`phone`)) {
      hookFromValidationTrigger(`phone`);
    }
  }, [country]);

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

  // handle get error string from different error object
  const formatErrorString = (
    error: FetchBaseQueryError | SerializedError | undefined,
  ) => {
    if (error) {
      if ('status' in error) {
        if (typeof error.status === 'string') {
          return error.status;
        } else {
          return (error as any)?.data?.error?.message ?? UNKOWNERROR;
        }
      } else {
        return error.message;
      }
    }
    return 'INTERNAL_ERROR';
  };

  return (
    <Stack
      sx={{p: '20px', border: '1px solid #E9E9E9'}}
      gap={'10px'}
      alignItems={'center'}
      width={'100%'}
      justifyContent={'center'}
      bgcolor={'#FAFAFA'}>
      <form
        id="add_corporate_user_dialog_form"
        onSubmit={handleSubmit(onSubmit)}>
        <IconButton
          onClick={onClose}
          disableTouchRipple
          disableFocusRipple
          disableRipple
          sx={{
            position: 'absolute',
            top: theme => theme.spacing(1.5),
            right: theme => theme.spacing(1.5),
          }}
          aria-label="delete">
          <CloseIcon color={'primary'} />
        </IconButton>
        <Grid container spacing={2}>
          {/**  First Name*/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization('corporate-form-add-user', 'first-name'),
                  )}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />
                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<ICreateCorporateUserForm>
                size="small"
                sx={{
                  fontSize: 15,
                }}
                control={control}
                eMessage={
                  t(
                    Localization(
                      'upload-signatories-error',
                      'first-name-error',
                    ),
                  )!
                }
                inputProps={{color: 'red'}}
                errorInput={errors?.firstName}
                typeInput={'name'}
                type={'text'}
                name={`firstName`}
                placeholder={
                  t(Localization('corporate-form-add-user', 'first-name'))!
                }
              />
            </Stack>
          </Grid>
          {/** Name **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(Localization('corporate-form-add-user', 'last-name'))}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<ICreateCorporateUserForm>
                size="small"
                sx={{
                  fontSize: 15,
                }}
                control={control}
                eMessage={t(Localization('form-error', 'name'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.lastName}
                typeInput={'name'}
                type={'text'}
                name={`lastName`}
                placeholder={
                  t(Localization('corporate-form-add-user', 'last-name'))!
                }
              />
            </Stack>
          </Grid>
          {/** Email **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(Localization('corporate-form-add-user', 'email'))}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<ICreateCorporateUserForm>
                size="small"
                sx={{
                  fontSize: 15,
                }}
                control={control}
                eMessage={t(Localization('form-error', 'email'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.email}
                typeInput={'email'}
                type={'text'}
                name={`email`}
                onAfterChange={() => hookFromValidationTrigger('email')}
                placeholder={
                  t(Localization('corporate-form-add-user', 'email'))!
                }
                validate={async value => {
                  if (isEmail(value)) {
                    const isExisted = await validateUserEmailTrigger({
                      email: value,
                    }).unwrap();
                    if (isExisted) {
                      return t(Localization('form-error', 'existed-email'))!;
                    }
                    return undefined;
                  }

                  return t(Localization('form-error', 'email-invalid'))!;
                }}
              />
            </Stack>
          </Grid>
          {/** Password **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(Localization('corporate-form-add-user', 'password'))}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<ICreateCorporateUserForm>
                size="small"
                sx={{
                  fontSize: 15,
                }}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.password}
                typeInput={'ValidationPass'}
                name={`password`}
                type={'text'}
                placeholder={
                  t(Localization('corporate-form-add-user', 'password'))!
                }
              />
            </Stack>
          </Grid>
          {/** Number Phone **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization('corporate-form-add-user', 'phone-number'),
                  )}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <Stack sx={{gap: 1}} direction={'row'}>
                <NGCountrySelect
                  sx={{display: 'block', width: 0, visibility: 'hidden'}}
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
                  <NGInputField<ICreateCorporateUserForm>
                    size="small"
                    sx={{
                      fontSize: 15,
                    }}
                    control={control}
                    eMessage={t(Localization('form-error', 'phone-number'))!}
                    inputProps={{color: 'red'}}
                    errorInput={errors?.phone}
                    typeInput={'phone'}
                    countryCode={country.code}
                    type={'text'}
                    name={`phone`}
                    placeholder={
                      t(
                        Localization('corporate-form-add-user', 'phone-number'),
                      )!
                    }
                  />
                </Stack>
              </Stack>
            </Stack>
          </Grid>
          {/** Function **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(Localization('corporate-form-add-user', 'function'))}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<ICreateCorporateUserForm>
                size="small"
                sx={{
                  fontSize: 15,
                }}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.functional}
                typeInput={'name'}
                type={'text'}
                name={`functional`}
                placeholder={
                  t(Localization('corporate-form-add-user', 'function'))!
                }
              />
            </Stack>
          </Grid>
          {/** Equip **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(Localization('corporate-form-add-user', 'team'))}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<ICreateCorporateUserForm>
                size="small"
                sx={{
                  fontSize: 15,
                  bgcolor: '#ffffff',
                }}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.department}
                typeInput={'select'}
                type={'text'}
                name={`department`}
                renderValue={(value: unknown): string | JSX.Element => {
                  const newValue = value as string;
                  if (newValue.length <= 0) {
                    // render it as a placeholder
                    return (
                      <NGText
                        text={t(
                          Localization(
                            'corporate-form-add-user',
                            'select-a-team',
                          ),
                        )}
                        myStyle={{
                          fontSize: pixelToRem(12),
                          color: 'Placeholder.main',
                        }}
                      />
                    );
                  }

                  // get user by the selected id
                  let tempDepartment = null;
                  if (departmentData.data) {
                    tempDepartment = departmentData.data.contents.find(
                      department => {
                        return department.id.toString() === newValue.toString();
                      },
                    );
                  }

                  return (
                    <NGText
                      text={tempDepartment ? tempDepartment.unitName : ''}
                      myStyle={{fontSize: pixelToRem(12)}}
                    />
                  );
                }}>
                {departmentData.data &&
                departmentData.data.contents.length > 0 ? (
                  departmentData.data.contents.map(item => (
                    <MenuItem key={item.id} value={item.id}>
                      <NGText
                        text={item.unitName}
                        myStyle={{fontSize: pixelToRem(12)}}
                      />
                    </MenuItem>
                  ))
                ) : (
                  <MenuItem disabled value="noResult">
                    <NGText
                      text={'No Result'}
                      myStyle={{fontSize: pixelToRem(12)}}
                    />
                  </MenuItem>
                )}
              </NGInputField>
            </Stack>
          </Grid>
          {/** Dorit's */}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization('corporate-form-add-user', 'access-right'),
                  )}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<ICreateCorporateUserForm>
                size="small"
                sx={{
                  fontSize: 15,
                  bgcolor: '#ffffff',
                }}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.userAccessId}
                typeInput={'select'}
                type={'text'}
                name={`userAccessId`}
                renderValue={(value: unknown): string | JSX.Element => {
                  const newValue = value as string;
                  if (newValue.length <= 0) {
                    // render it as a placeholder
                    return (
                      <NGText
                        text={t(
                          Localization(
                            'corporate-form-add-user',
                            'select-an-access-right',
                          ),
                        )}
                        myStyle={{
                          fontSize: pixelToRem(12),
                          color: 'Placeholder.main',
                        }}
                      />
                    );
                  }

                  // get user access by the selected id
                  let tempUserAccess = null;
                  if (userAccessData.data) {
                    tempUserAccess = userAccessData.data.contents.find(
                      userAccess => {
                        return userAccess.id.toString() === newValue.toString();
                      },
                    );
                  }

                  return (
                    <NGText
                      text={tempUserAccess?.name}
                      myStyle={{fontSize: pixelToRem(12)}}
                    />
                  );
                }}>
                {userAccessData.data &&
                userAccessData.data?.contents.length > 0 ? (
                  userAccessData.data?.contents.map(item => (
                    <MenuItem key={item.id} value={item.id}>
                      <NGText
                        text={item.name}
                        myStyle={{fontSize: pixelToRem(12)}}
                      />
                    </MenuItem>
                  ))
                ) : (
                  <MenuItem disabled value="noResult">
                    <NGText
                      text={'No Result'}
                      myStyle={{fontSize: pixelToRem(12)}}
                    />
                  </MenuItem>
                )}
              </NGInputField>
            </Stack>
          </Grid>
        </Grid>
      </form>
      {createError && (
        <Alert
          sx={{bgcolor: 'transparent', width: '100%', justifyContent: 'center'}}
          severity={'error'}>
          <NGText
            text={formatErrorString(createError)}
            myStyle={{fontSize: pixelToRem(12), color: 'error'}}
          />
        </Alert>
      )}
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={isCreateLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

const AddUserAction = ({onClose}: {onClose: () => void}): JSX.Element => {
  const {
    formState: {errors, dirtyFields},
  } = useFormContext<ICreateCorporateUserForm>();
  const activeColor = store.getState().enterprise.theme[0].mainColor;

  return (
    <Stack
      gap={'10px'}
      width={'100%'}
      height={'64px'}
      justifyContent={'center'}>
      <Stack direction={'row'} justifyContent={'flex-end'} gap={'10px'}>
        <NGButton
          onClick={onClose}
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          locationIcon="start"
          color={['#ffffff', '#000000']}
          variant="outlined"
          fontSize="11px"
          myStyle={{
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            width: '77px',
            height: '36px',
            borderRadius: '6px',
            border: '1px solid #000000',
            '&:hover': {
              borderColor: activeColor ?? 'info.main',
            },
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('upload-document', 'cancel'))}
        />
        <NGButton
          type={'submit'}
          form="add_corporate_user_dialog_form"
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          icon={
            <NGPlusIcon
              sx={{
                width: '18px',
                height: '20px',
                color: 'White.main',
              }}
            />
          }
          disabled={
            Object.keys(errors).length > 0 ||
            Object.keys(dirtyFields).length !==
              Object.keys(defaultValues).length
          }
          locationIcon="end"
          color={['#ffffff', '#ffffff']}
          variant="contained"
          fontSize="11px"
          myStyle={{
            '&.Mui-disabled': {
              bgcolor: colorDisable,
            },
            '&.MuiButtonBase-root': {
              borderColor: activeColor ?? 'Primary.main',
            },
            bgcolor: activeColor ?? 'Primary.main',
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            height: '36px',
            borderRadius: '6px',
            borderColor: '#000000',
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('enterprise-services', 'add'))}
        />
      </Stack>
    </Stack>
  );
};
