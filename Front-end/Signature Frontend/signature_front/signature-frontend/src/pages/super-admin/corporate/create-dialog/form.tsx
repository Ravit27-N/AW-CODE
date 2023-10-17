import React from 'react';
import {t} from 'i18next';
import {Localization} from '@/i18n/lan';
import {useFormContext} from 'react-hook-form';
import 'react-phone-number-input/style.css';
import flags from 'react-phone-number-input/flags';
import en from 'react-phone-number-input/locale/en.json';
import {NGInputField} from '@components/ng-input/NGInputField';
import {CountryCode, ICountry} from '@/components/ng-phone/type';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import FlagComponent, {
  NGCountrySelect,
} from '@components/ng-phone/NGCountrySelect';
import {
  Backdrop,
  CircularProgress,
  Grid,
  IconButton,
  Stack,
} from '@mui/material';
import {ICorporateRegister} from '@pages/super-admin/corporate/create-dialog';
import NGText from '@/components/ng-text/NGText';
import {FigmaInput} from '@/constant/style/themFigma/Input';
import {useLazyValidateUserEmailQuery} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {
  CorporateAdmin,
  useCreateUserMutation,
} from '@/redux/slides/super-admin/corporateAdminSlide';
import {useSnackbar} from 'notistack';
import {UNKOWNERROR} from '@/constant/NGContant';
import isEmail from 'validator/lib/isEmail';

const CreateCorporateUserForm = ({
  companyId,
  onAddSuccess,
}: {
  companyId: number;
  onAddSuccess?: (data: CorporateAdmin) => void;
}) => {
  const {enqueueSnackbar} = useSnackbar();

  const {
    control,
    formState: {errors},
    trigger,
    handleSubmit,
  } = useFormContext<ICorporateRegister>();
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
  };

  const [createCorporateAdmin, {isLoading: isCreateLoading}] =
    useCreateUserMutation();

  const onSubmit = async (data: typeof control._defaultValues) => {
    const {firstName, lastName, email, phone, functional = '', password} = data;
    try {
      if (firstName && lastName && email && phone && password) {
        const data = await createCorporateAdmin({
          firstName: firstName.trim(),
          lastName: lastName.trim(),
          email,
          phone: `+${country.callingCode}${phone}`,
          functional: functional.trim(),
          password,
          companyId,
        }).unwrap();
        enqueueSnackbar('Create Corporate Admin Successfully', {
          variant: 'successSnackbar',
        });
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
              <NGInputField<ICorporateRegister>
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
              <NGInputField<ICorporateRegister>
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
              <NGInputField<ICorporateRegister>
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
              <NGInputField<ICorporateRegister>
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
                  <NGInputField<ICorporateRegister>
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
              </Stack>
              <NGInputField<ICorporateRegister>
                size="small"
                sx={{...FigmaInput.InputTextMedium}}
                control={control}
                inputProps={{color: 'red'}}
                typeInput={'name'}
                type={'text'}
                name={`functional`}
                placeholder={
                  t(Localization('super-admin-add-corporate-user', 'function'))!
                }
              />
            </Stack>
          </Grid>
        </Grid>
      </form>
    </Stack>
  );
};

export default CreateCorporateUserForm;
