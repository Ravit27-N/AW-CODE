import {FONT_TYPE} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {
  AcceptOnlyNumAlphabetAndSomeSpecialCharacter,
  Characters_12_Reg,
  IsNumberAndAlphabet,
  LowerReg,
  NumReg,
  SpecialCharacterReg,
  UpperReg,
} from '@/utils/common/RegSpecialCharacter';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NGEyeClose, NGEyeOpen} from '@assets/iconExport/Allicon';
import {
  FormControl,
  IconButton,
  InputAdornment,
  OutlinedInput,
  OutlinedInputProps,
  Select,
  SelectProps,
  Stack,
  Typography,
} from '@mui/material';
import asYouType from 'google-libphonenumber';
import React, {PropsWithChildren} from 'react';
import {
  Control,
  Controller,
  FieldError,
  Path,
  PathValue,
  Validate,
} from 'react-hook-form';
import {useTranslation} from 'react-i18next';
import {isPossiblePhoneNumber} from 'react-phone-number-input';
import isEmail from 'validator/lib/isEmail';
import {CountryCode} from '../ng-phone/type';
import {ITypeInput} from './type';

const phoneUtil = asYouType.PhoneNumberUtil.getInstance();
const PNF = asYouType.PhoneNumberFormat;

type IInput<T extends Record<string, any>> = {
  control: Control<T, any>;
  errorInput?: FieldError;
  length?: number;
  name: Path<T>;
  eMessage?: string;
  defaultShowPassword?: boolean;
  validate?: Validate<PathValue<T, Path<T> & (string | undefined)>, T>;
  type: 'password' | 'text' | Omit<'password' | 'text', string>;
  onAfterChange?: (data: string) => void;
  typeInput: ITypeInput | Omit<ITypeInput, string>;
  countryCode?: CountryCode;
} & OutlinedInputProps &
  SelectProps &
  PropsWithChildren;

export const NGInputField = <T extends Record<string, any>>({
  control,
  eMessage,
  name,
  defaultShowPassword = true, // if `true` default input type is `password` and some browser will autoFill
  validate,
  errorInput,
  type,
  typeInput,
  countryCode,
  children,
  length,
  onAfterChange,
  multiline,
  ...props
}: IInput<T>) => {
  const {t} = useTranslation();
  const [hidePassword, setHidePassword] =
    React.useState<boolean>(defaultShowPassword);

  // use to check weather or not should hide or show button that
  // toggle input type between 'text', 'password'
  const enableShowPasswordButton =
    typeInput.toString().includes('password') ||
    typeInput.toString().includes('ValidationPass') ||
    typeInput.toString().includes('notValidateHaveEye');

  const handleClickShowPassword = React.useCallback(
    () => setHidePassword(e => !e),
    [],
  );
  const formatPhoneNumber = (value: any): unknown => {
    if (typeInput.toString().includes('phone') && value.length > 3) {
      return phoneUtil.format(
        phoneUtil.parseAndKeepRawInput(value, countryCode),
        PNF.INTERNATIONAL,
      );
    }

    return value;
  };

  const onChangePhoneNumber = (value: string) => {
    if (value.startsWith('+') && typeInput.toString().includes('phone')) {
      return value.replace(
        value.slice(
          0,
          phoneUtil
            .parseAndKeepRawInput(value, countryCode)
            .getCountryCode()
            ?.toString().length! + 1,
        ),
        '',
      );
    }
    return value;
  };

  const onPastChange = (e: any) => {
    e.preventDefault();
    return false;
  };
  const ValidateNumber = (e: any) => {
    if (
      typeInput.toString().includes('phone') ||
      typeInput.toString().includes('number') ||
      typeInput.toString().includes('length')
    ) {
      if (!/\d/.test(e.nativeEvent.key)) {
        e.preventDefault();
      }
    } else if (
      typeInput.toString() === 'first-name' ||
      typeInput.toString() === 'last-name'
    ) {
      if (/\d/.test(e.nativeEvent.key)) {
        e.preventDefault();
      }
    }
  };

  const getInputType = () => {
    if (enableShowPasswordButton) {
      return hidePassword ? 'password' : 'text';
    }
    return type;
  };
  return (
    <Stack>
      <Controller
        control={control}
        rules={{
          required: eMessage ?? undefined,
          validate: (value, formValues) => {
            // run custom validate instead if it valid
            if (typeof validate === 'function') {
              return validate(value, formValues);
            }

            if (`${typeInput}`.includes('phone')) {
              return value.length > 0 &&
                !isPossiblePhoneNumber(value, countryCode)
                ? t(Localization('form-error', 'phone-invalid'))!
                : undefined;
            }
            if (typeInput.toString().includes('length')) {
              return value.length < length! || value.length > length!
                ? t(Localization('company-form-error', 'siret-number-invalid'))!
                : undefined;
            }
            if (typeInput.toString().includes('email')) {
              return value.length > 0 && !isEmail(value)
                ? t(Localization('form-error', 'email-invalid'))!
                : undefined;
            }

            if (typeInput.toString().includes('password')) {
              return value.length < 10 ? 'Error' : undefined;
            }
            if (typeInput.toString().includes('ValidationPass')) {
              /** Handle error **/
              const [
                Special,
                SpecialMatch,
                Num,
                Upper,
                Lower,
                Char12,
                MoreThan12,
              ] = [
                {
                  value: !IsNumberAndAlphabet(value),
                  message: t(
                    Localization(
                      'message-error-validation-password',
                      'special',
                    ),
                  ),
                },
                {
                  value:
                    SpecialCharacterReg(value) &&
                    AcceptOnlyNumAlphabetAndSomeSpecialCharacter(value),
                  message: t(
                    Localization(
                      'message-error-validation-password',
                      'special-match',
                    ),
                  ),
                },
                {
                  value: NumReg(value),
                  message: t(
                    Localization('message-error-validation-password', 'num'),
                  ),
                },
                {
                  value: UpperReg(value),
                  message: t(
                    Localization(
                      'message-error-validation-password',
                      'upper-case',
                    ),
                  ),
                },
                {
                  value: LowerReg(value),
                  message: t(
                    Localization(
                      'message-error-validation-password',
                      'lower-case',
                    ),
                  ),
                },
                {
                  value: Characters_12_Reg(value),
                  message: t(
                    Localization(
                      'message-error-validation-password',
                      'character-12',
                    ),
                  ),
                },
                {
                  value: value.length < 13,
                  message: t(Localization('form', 'more-than-12-characters')),
                },
              ];
              /** Process validation and message error **/
              const message: string | undefined = (() => {
                switch (true) {
                  case !MoreThan12.value:
                    return MoreThan12.message;
                  case !Char12.value:
                    return Char12.message;
                  case !Special.value:
                    return Special.message;
                  case !SpecialMatch.value:
                    return SpecialMatch.message;
                  case !Num.value:
                    return Num.message;
                  case !Upper.value:
                    return Upper.message;
                  case !Lower.value:
                    return Lower.message;

                  default:
                    return undefined;
                }
              })();
              return message;
            }

            return undefined;
          },
        }}
        render={({field: {onChange, onBlur, value}}) =>
          typeInput.toString().includes('select') ? (
            <FormControl>
              <Select
                {...props}
                error={!!errorInput}
                size="small"
                value={value}
                onChange={onChange}
                displayEmpty
                sx={{
                  ...(props?.sx ? props.sx : {}),
                  '&.MuiInputBase-root': {
                    color: 'black.main', // set the color of the text
                    fieldset: {
                      borderColor: '#E9E9E9',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: value.length ? 'Primary.main' : '#E9E9E9',
                      borderWidth: '0.2px',
                    },
                    '&.Mui-disabled': {
                      fieldset: {
                        borderColor: '#E9E9E9',
                      },
                    },
                    '&:hover fieldset': {
                      borderColor: '#E9E9E9',
                    },
                    '& .MuiSelect-icon': {
                      color: 'black.main', // set the color of the arrow icon
                    },
                  },
                }}
                inputProps={{'aria-label': 'Without label'}}>
                {children}
              </Select>
            </FormControl>
          ) : (
            <OutlinedInput
              color="secondary"
              error={!!errorInput}
              fullWidth={true}
              multiline={multiline}
              minRows={3}
              {...props}
              sx={{
                '& input::placeholder': {
                  fontSize: pixelToRem(14),
                },
                '&.MuiOutlinedInput-root': {
                  '&.MuiOutlinedInput-notchedOutline': {
                    borderColor: '#E9E9E9',
                  },
                  bgcolor: '#ffffff',
                  fieldset: {
                    borderWidth: '0.2px',
                    borderColor: value.length ? 'Primary.main' : '#E9E9E9',
                  },
                  '& fieldset': {
                    borderColor: '#E9E9E9',
                  },
                  '&:hover fieldset': {
                    borderColor: '#E9E9E9',
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: value.length ? 'Primary.main' : '#E9E9E9',
                    borderWidth: '0.2px',
                  },
                },
                ...(props?.sx ? props.sx : {}),
              }}
              value={formatPhoneNumber(value)}
              onBlur={onBlur}
              inputProps={{
                maxLength: _maxLength(typeInput),
              }}
              onPaste={
                typeInput.toString().includes('phone')
                  ? onPastChange
                  : undefined
              }
              onChange={e => {
                onChange(onChangePhoneNumber(e.target.value));
                if (typeof onAfterChange === 'function') {
                  onAfterChange(onChangePhoneNumber(e.target.value));
                }
              }}
              type={getInputType()}
              onKeyPress={e => {
                ValidateNumber(e);
              }}
              style={{
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 14,
                fontWeight: 400,
              }}
              endAdornment={
                enableShowPasswordButton ? (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                      edge="end">
                      {hidePassword ? (
                        <NGEyeClose sx={{width: 14, height: 12.83}} />
                      ) : (
                        <NGEyeOpen sx={{width: 14, height: 12.83}} />
                      )}
                    </IconButton>
                  </InputAdornment>
                ) : undefined
              }
            />
          )
        }
        name={name}
      />
      {errorInput && (
        <Typography paragraph sx={{color: 'red', fontSize: 12, margin: 0}}>
          {errorInput.message}
        </Typography>
      )}
    </Stack>
  );
};

function _maxLength(typeInput: string | Omit<ITypeInput, string>) {
  if (typeInput.toString().includes('phone')) {
    return 16;
  } else if (
    typeInput.toString().includes('ValidationPass') ||
    typeInput.toString().includes('password') ||
    typeInput.toString().includes('notValidateHaveEye')
  ) {
    return 13;
  } else {
    return undefined;
  }
}
