import {CountryCode, ICountry} from '@/components/ng-phone/type';
import NGText from '@/components/ng-text/NGText';
import {FigmaInput} from '@/constant/style/themFigma/Input';
import {Localization} from '@/i18n/lan';
import NGDropzoneLogo, {
  LogoFileType,
} from '@components/ng-dropzone/ng-dropzone-logo/NGDropzoneLogo';
import {NGInputField} from '@components/ng-input/NGInputField';
import FlagComponent, {
  NGCountrySelect,
} from '@components/ng-phone/NGCountrySelect';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import {Backdrop, CircularProgress, IconButton, Stack} from '@mui/material';
import {grey} from '@mui/material/colors';
import React from 'react';
import {Control, FieldErrors} from 'react-hook-form';
import {useTranslation} from 'react-i18next';
import flags from 'react-phone-number-input/flags';
import en from 'react-phone-number-input/locale/en.json';
export type ICreateCompanyForm = {
  companyName: string;
  siretNumber: string;
  contactFirstName: string;
  contactLastName: string;
  phoneNumber: string;
  email: string;
  address: string;
  fixNumber: string;
};
export type ICreateCompanyAccount = {
  control: Control<ICreateCompanyForm, any>;
  exist: JSX.Element | null;
  setExist: React.Dispatch<React.SetStateAction<JSX.Element | null>>;
  errors?: FieldErrors<ICreateCompanyForm>;
  country: ICountry;
  fixNumber: ICountry;
  handleClose: () => void;
  handlePhone: (data: string) => void;
  handleFixPhone: (data: string) => void;
  flags: Partial<Record<CountryCode, (props: {title: string}) => JSX.Element>>;
  selectToggle: {
    phoneNumber: boolean;
    fixNumber: boolean;
  };
  setToggle: React.Dispatch<
    React.SetStateAction<{
      phoneNumber: boolean;
      fixNumber: boolean;
    }>
  >;
  logo: LogoFileType;
  setLogo: React.Dispatch<React.SetStateAction<LogoFileType>>;
  addLoading: boolean;
};

const CreateCompanyAccount = ({
  control,
  errors,
  country,
  fixNumber,
  setToggle,
  selectToggle,
  handleClose,
  handlePhone,
  handleFixPhone,
  logo,
  setLogo,
  exist,
  addLoading,
}: ICreateCompanyAccount) => {
  const {t} = useTranslation();

  return (
    <Stack
      sx={{
        width: '100%',
        padding: 2,
        borderColor: grey[400],
        gap: 2,
      }}>
      {exist}
      {/* Name */}
      <Stack sx={{flex: 1, gap: 1}}>
        <Stack direction={'row'}>
          <NGText
            myStyle={{...FigmaInput.InputLabelMedium}}
            text={t(Localization('form', 'name'))}
          />
          <NGText
            myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
            text={'*'}
          />
        </Stack>
        <NGInputField<ICreateCompanyForm>
          size="small"
          sx={{...FigmaInput.InputTextMedium}}
          control={control}
          eMessage={`${t(Localization('form-error', 'company-name'))!}`}
          errorInput={errors?.companyName}
          typeInput={'name'}
          type={'text'}
          name={'companyName'}
          placeholder={t(Localization('company-form', 'company-name'))!}
        />
      </Stack>
      {/* Siret number */}
      <Stack sx={{flex: 1, gap: 1}}>
        <Stack direction={'row'}>
          <NGText
            myStyle={{...FigmaInput.InputLabelMedium}}
            text={t(Localization('company-form', 'siret-number'))}
          />
          <NGText
            myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
            text={'*'}
          />
        </Stack>
        <NGInputField<ICreateCompanyForm>
          size="small"
          sx={{...FigmaInput.InputTextMedium}}
          control={control}
          length={14}
          eMessage={`${t(Localization('form-error', 'siret-number'))!}`}
          errorInput={errors?.siretNumber}
          typeInput={'numlength'}
          type={'text'}
          name={'siretNumber'}
          placeholder={t(Localization('company-form', 'siret-number'))!}
        />
      </Stack>
      {/* Email */}
      <Stack sx={{flex: 1, gap: 1}}>
        <Stack direction={'row'}>
          <NGText
            myStyle={{...FigmaInput.InputLabelMedium}}
            text={t(Localization('form', 'email'))}
          />
        </Stack>

        <NGInputField<ICreateCompanyForm>
          size="small"
          sx={{...FigmaInput.InputTextMedium}}
          control={control}
          errorInput={errors?.email}
          typeInput={'email'}
          type={'text'}
          name={'email'}
          placeholder={t(Localization('form', 'email'))!}
        />
      </Stack>
      {/* Address */}
      <Stack sx={{flex: 1, gap: 1}}>
        <NGText
          myStyle={{...FigmaInput.InputLabelMedium}}
          text={t(Localization('company-form', 'address'))}
        />
        <NGInputField<ICreateCompanyForm>
          size="small"
          sx={{...FigmaInput.InputTextMedium}}
          control={control}
          errorInput={errors?.address}
          typeInput={'name'}
          type={'text'}
          name={'address'}
          placeholder={t(Localization('company-form', 'address'))!}
        />
      </Stack>
      {/* Contact name */}
      {/* <Stack sx={{flex: 1, gap: 1}}>
        <Typography>
          {t(Localization('company-form', 'contact-name'))}
        </Typography>
        <InputField<ICreateCompanyForm>
          size="small"
          sx={{fontSize: 15, minWidth: '15rem'}}
          control={control}
          errorInput={errors?.contactName}
          eMessage={`${t(
            Localization('company-form-error', 'contact-last-name'),
          )!}`}
          typeInput={'name'}
          type={'text'}
          name={'companyName'}
          placeholder={t(Localization('company-form', 'contact-name'))!}
        />
      </Stack> */}
      {/* Phone number */}
      <Stack direction={'row'}>
        <NGText
          myStyle={{...FigmaInput.InputLabelMedium}}
          text={t(Localization('form', 'phone-number'))}
        />
        <NGText
          myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
          text={'*'}
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
          <NGInputField
            size="small"
            sx={{...FigmaInput.InputTextMedium}}
            control={control}
            eMessage={`${t(Localization('form-error', 'phone-number'))!}`}
            countryCode={country.code}
            errorInput={errors?.phoneNumber}
            typeInput={'phone'}
            type={'text'}
            name="phoneNumber"
            placeholder={t(Localization('form', 'phone-number'))!}
          />
        </Stack>
      </Stack>
      <Stack sx={{flex: 1, gap: 1}}>
        <Stack direction={'row'} sx={{width: '100%'}} spacing={2}>
          <Stack sx={{width: '100%'}}>
            <Stack direction={'row'}>
              <NGText
                myStyle={{...FigmaInput.InputLabelMedium}}
                text={`${t(
                  Localization('upload-signatories', 'first-name'),
                )} contact`}
              />
              <NGText
                myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                text={'*'}
              />
            </Stack>
            <NGInputField<ICreateCompanyForm>
              size="small"
              sx={{...FigmaInput.InputTextMedium}}
              control={control}
              eMessage={`${t(
                Localization('company-form-error', 'contact-first-name'),
              )!}`}
              errorInput={errors?.contactFirstName}
              typeInput={'name'}
              type={'text'}
              name={'contactFirstName'}
              placeholder={`${t(
                Localization('upload-signatories', 'first-name'),
              )!} contact`}
            />
          </Stack>
          <Stack sx={{width: '100%'}}>
            <Stack direction={'row'}>
              <NGText
                myStyle={{...FigmaInput.InputLabelMedium}}
                text={t(Localization('upload-signatories', 'last-name'))}
              />
              <NGText
                myStyle={{...FigmaInput.InputLabelMedium, color: 'red'}}
                text={'*'}
              />
            </Stack>

            <NGInputField<ICreateCompanyForm>
              size="small"
              sx={{...FigmaInput.InputTextMedium}}
              control={control}
              eMessage={`${t(
                Localization('company-form-error', 'contact-last-name'),
              )!}`}
              errorInput={errors?.contactLastName}
              typeInput={'name'}
              type={'text'}
              name={'contactLastName'}
              placeholder={t(Localization('upload-signatories', 'last-name'))!}
            />
          </Stack>
        </Stack>
      </Stack>
      {/* Fix Number */}
      <Stack direction={'row'}>
        <NGText
          myStyle={{...FigmaInput.InputLabelMedium}}
          text={t(Localization('company-form', 'fix-number'))}
        />
      </Stack>
      <Stack sx={{gap: 1}} direction={'row'}>
        <NGCountrySelect
          sx={{display: 'block', width: 0, visibility: 'hidden'}}
          labels={en}
          value={fixNumber.code}
          selectToggle={selectToggle.fixNumber}
          handleClose={handleClose}
          selectChange={handleFixPhone}
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
              fixNumber: !selectToggle.fixNumber,
            })
          }>
          <FlagComponent
            flags={flags}
            country={fixNumber.code || 'KH'}
            countryName={fixNumber.name}
          />
          <ArrowDropDownIcon />
        </IconButton>
        <Stack sx={{width: '100%'}}>
          <NGInputField<ICreateCompanyForm>
            size="small"
            sx={{...FigmaInput.InputTextMedium}}
            countryCode={fixNumber.code}
            control={control}
            errorInput={errors?.fixNumber}
            typeInput={'phone'}
            type={'text'}
            name={'fixNumber'}
            placeholder={t(Localization('company-form', 'fix-number'))!}
          />
        </Stack>
      </Stack>
      <Stack sx={{flex: 1, gap: 1}}>
        <NGText myStyle={{...FigmaInput.InputLabelMediumBold}} text={'Logo'} />
        <NGDropzoneLogo logo={logo} setLogo={setLogo} />
      </Stack>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={addLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

export default CreateCompanyAccount;
