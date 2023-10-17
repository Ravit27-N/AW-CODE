import {CountryCode, ICountry} from '@/components/ng-phone/type';
import NGText from '@/components/ng-text/NGText';
import {UNKOWNERROR} from '@/constant/NGContant';
import {FigmaInput} from '@/constant/style/themFigma/Input';
import {Localization} from '@/i18n/lan';
import {
  useLazyGetDepartmentOrServiceQuery,
  useLazyGetUserAccessQuery,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {
  IGetUsersContent,
  useCreateUserMutation,
  useLazyValidateUserEmailQuery,
} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NGInputField} from '@components/ng-input/NGInputField';
import FlagComponent, {
  NGCountrySelect,
} from '@components/ng-phone/NGCountrySelect';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import {
  Backdrop,
  CircularProgress,
  Grid,
  IconButton,
  MenuItem,
  Stack,
  useTheme,
} from '@mui/material';
import {CorporateAdminForm} from '@pages/super-admin/sidebar-super-admin/customer/utils/create-dialog';
import {t} from 'i18next';
import {useSnackbar} from 'notistack';
import React from 'react';
import {useFormContext} from 'react-hook-form';
import flags from 'react-phone-number-input/flags';
import en from 'react-phone-number-input/locale/en.json';
import 'react-phone-number-input/style.css';
import isEmail from 'validator/lib/isEmail';

const CreateCorporateUserForm = ({
  companyId,
  onAddSuccess,
}: {
  companyId: number;
  onAddSuccess?: (data: IGetUsersContent) => void;
}) => {
  const {enqueueSnackbar} = useSnackbar();
  const theme = useTheme();
  /** get company ID from redux **/

  const {
    control,
    formState: {errors},
      getValues,
    setError,
    trigger,
    handleSubmit,
  } = useFormContext<CorporateAdminForm>();
  const [departmentOrServiceTrigger, departmentData] =
    useLazyGetDepartmentOrServiceQuery();
  const [userAccessTrigger, userAccessData] = useLazyGetUserAccessQuery();
  const [validateUserEmailTrigger] = useLazyValidateUserEmailQuery();
  const [country, setCountry] = React.useState<ICountry>({
    callingCode: '33',
    name: 'France',
    code: 'FR',
  });

  const [selectToggle, setToggle] = React.useState({
    phoneNumber: false,
    additionalPhone: false,
  });

  React.useEffect(() => {
    const handleFetchDepartmentOrService = async () => {
      await departmentOrServiceTrigger({
        companyId,
        page: 1,
        pageSize: 15,
      }).unwrap();
    };
    const handleFetchUserAccess = async () => {
      await userAccessTrigger({}).unwrap();
    };

    if (companyId) {
      handleFetchDepartmentOrService().then();
      handleFetchUserAccess().then();
    }
  }, [companyId]);

  const handleClose = () => {
    setToggle({phoneNumber: false, additionalPhone: false});
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

  const [createIGetUsersContent, {isLoading: isCreateLoading}] =
    useCreateUserMutation();

  const onSubmit = async (data: typeof control._defaultValues) => {
    const {
      firstName,
      lastName,
      email,
      phone,
      functional,
      userAccessId,
      businessId,
      password,
    } = data;
    try {
      if (
        firstName &&
        lastName &&
        email &&
        phone &&
        functional &&
        userAccessId &&
        businessId &&
        password
      ) {
        const data = await createIGetUsersContent({
          firstName: firstName.trim(),
          lastName: lastName.trim(),
          email,
          phone: `+${country.callingCode}${phone}`,
          functional: functional.trim(),
          userAccessId,
          businessId,
          password,
          companyId,
        }).unwrap();
        enqueueSnackbar(
          t(
            Localization(
              'super-admin-add-corporate-user',
              'create-successfully',
            ),
            {
              name: `${data.firstName} ${data.lastName}`,
            },
          ),
          {
            variant: 'successSnackbar',
          },
        );
        if (typeof onAddSuccess === 'function') {
          onAddSuccess(data);
        }
      }
    } catch (err) {
      enqueueSnackbar((err as any)?.data?.error?.message ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };
  React.useEffect(() => {
    if (getValues(`phone`)) {
      trigger(`phone`);
    }
  }, [country]);
  return (
    <Stack
      sx={{p: '20px', border: '1px solid #E9E9E9'}}
      gap={'10px'}
      alignItems={'center'}
      width={'100%'}
      justifyContent={'center'}
      bgcolor={'#FAFAFA'}>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={isCreateLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      <form
        id="super_admin_add_corporate_admin_dialog_form"
        onSubmit={handleSubmit(onSubmit)}>
        <Grid container spacing={2}>
          {/** First Name **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization(
                      'super-admin-add-corporate-user',
                      'first-name',
                    ),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
              </Stack>
              <NGInputField<CorporateAdminForm>
                size="small"
                sx={{...FigmaInput.InputTextMedium}}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                errorInput={errors?.firstName}
                typeInput={'name'}
                type={'text'}
                name={`firstName`}
                placeholder={
                  t(
                    Localization(
                      'super-admin-add-corporate-user',
                      'first-name',
                    ),
                  )!
                }
              />
            </Stack>
          </Grid>
          {/** Name **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization('super-admin-add-corporate-user', 'last-name'),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
              </Stack>
              <NGInputField<CorporateAdminForm>
                size="small"
                sx={{...FigmaInput.InputTextMedium}}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                errorInput={errors?.lastName}
                typeInput={'name'}
                type={'text'}
                name={`lastName`}
                placeholder={
                  t(
                    Localization('super-admin-add-corporate-user', 'last-name'),
                  )!
                }
              />
            </Stack>
          </Grid>
          {/** Email **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization('super-admin-add-corporate-user', 'email'),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
              </Stack>
              <NGInputField<CorporateAdminForm>
                size="small"
                sx={{...FigmaInput.InputTextMedium}}
                control={control}
                eMessage={t(Localization('form-error', 'email'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.email}
                typeInput={'email'}
                type={'text'}
                name={`email`}
                onAfterChange={() => trigger('email')}
                placeholder={
                  t(Localization('super-admin-add-corporate-user', 'email'))!
                }
                validate={async value => {
                  const tempValue = value as string;
                  if (isEmail(tempValue)) {
                    const isExisted = await validateUserEmailTrigger({
                      email: tempValue,
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
                  text={t(
                    Localization('super-admin-add-corporate-user', 'password'),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
              </Stack>
              <NGInputField<CorporateAdminForm>
                size="small"
                sx={{...FigmaInput.InputTextMedium}}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.password}
                typeInput={'ValidationPass'}
                type={'text'}
                name={`password`}
                placeholder={
                  t(Localization('super-admin-add-corporate-user', 'password'))!
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
                    Localization(
                      'super-admin-add-corporate-user',
                      'phone-number',
                    ),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
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
                  <NGInputField<CorporateAdminForm>
                    size="small"
                    sx={{...FigmaInput.InputTextMedium}}
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
                        Localization(
                          'super-admin-add-corporate-user',
                          'phone-number',
                        ),
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
                  text={t(
                    Localization('super-admin-add-corporate-user', 'function'),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
              </Stack>
              <NGInputField<CorporateAdminForm>
                size="small"
                sx={{...FigmaInput.InputTextMedium}}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.functional}
                typeInput={'name'}
                type={'text'}
                name={`functional`}
                placeholder={
                  t(Localization('super-admin-add-corporate-user', 'function'))!
                }
              />
            </Stack>
          </Grid>
          {/** Department **/}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization('super-admin-add-corporate-user', 'team'),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
              </Stack>
              <NGInputField<CorporateAdminForm>
                size="small"
                sx={{
                  fontSize: 15,
                  bgcolor: '#ffffff',
                }}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.businessId}
                typeInput={'select'}
                type={'text'}
                name={`businessId`}
                renderValue={(value: unknown): string | JSX.Element => {
                  const newValue = value as string;
                  if (newValue.length <= 0) {
                    // render it as a placeholder
                    return (
                      <NGText
                        text={t(
                          Localization(
                            'super-admin-add-corporate-user',
                            'select-a-team',
                          ),
                        )}
                        myStyle={{
                          ...FigmaInput.InputLabelMedium,
                          fontSize: pixelToRem(14),
                          fontWeight: 'normal',
                          color: theme.palette.grey[400],
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
          {/** User Access */}
          <Grid item xs={6}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(
                    Localization(
                      'super-admin-add-corporate-user',
                      'access-right',
                    ),
                  )}
                  myStyle={{...FigmaInput.InputLabelMedium}}
                />
                <NGText
                  text={'*'}
                  myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                />
              </Stack>
              <NGInputField<CorporateAdminForm>
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
                            'super-admin-add-corporate-user',
                            'select-an-access-right',
                          ),
                        )}
                        myStyle={{
                          ...FigmaInput.InputLabelMedium,
                          fontSize: pixelToRem(14),
                          fontWeight: 'normal',
                          color: theme.palette.grey[400],
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
    </Stack>
  );
};

export default CreateCorporateUserForm;
