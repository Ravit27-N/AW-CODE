import {Type} from '@/components/ng-inputField/Type';
import {FONT_TYPE} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {Center, HStack} from '@/theme';
import {NGEyeClose, NGEyeOpen} from '@assets/iconExport/Allicon';
import NGText from '@components/ng-text/NGText';
import {
  InputAdornment,
  Stack,
} from '@mui/material';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import {HTMLInputTypeAttribute, useEffect, useState} from 'react';
import {useTranslation} from 'react-i18next';
import isEmail from 'validator/lib/isEmail';

function NGInput({
  textLabel,
  textLabelRight,
  setValue,
  value,
  nameId,
  type = 'text',
  placeholder = 'Adresse e-mail',
  state,
  require = false,
  rows = 1,
  limitLength = 200,
  messageError,
  color = 'red',
  Icon,
  colorOnfocus,
  propsInput,
  id = 'id',
  autoFocus = false,
  size = 'small',
  textFieldProps,
    passwordLength=13
}: Type) {
  const {t} = useTranslation();
  const [isValid, setIsValid] = useState(false);

  const [dirty, setDirty] = useState(nameId === 'PP');
  const nameIdEmail = t(Localization('form', 'email'));
  const [messageOfTextError, setMessageOfTextError] = useState<string | null>(
    '',
  );
  const [textError, setTextError] = useState<any>('');
  const [TYPE, setTYPE] = useState<HTMLInputTypeAttribute>(type);
  const handleChange = (event: any) => {
    const val = event.target.value;
    const IsEmail = isEmail(val);
    switch (nameId) {
      case nameIdEmail:
        if (!nameIdEmail) return;
        if (IsEmail) {
          setIsValid(true);
          setTextError('');
        } else if (!val) {
          setIsValid(false);
          setTextError('');
        } else {
          setIsValid(false);
          setTextError(t(messageError!)!);
        }
        break;

      case 'optional':
        if (val.length === limitLength) {
          setIsValid(true);
          setTextError('');
        } else {
          setIsValid(false);
          setTextError(
            t(
              Localization(
                'invitation',
                'Your message exceeds the allowed message size',
              ),
            ) ?? '',
          );
        }
        break;

      case 'PP':
        if (
          state?.Num &&
          state.Special &&
          state.Lower &&
          state.Upper &&
          state.Character12
        ) {
          setIsValid(true);
        } else {
          setIsValid(false);
        }
        break;

      default:
        if (val === nameIdEmail) return;
        if (!val) {
          setIsValid(false);
          setTextError(t(Localization('status', 'required')) ?? '');
          return;
        }
        setIsValid(true);
        setTextError(t(Localization('status', 'success')) ?? '');
        if (val.length > (limitLength ?? 0)) {
          setIsValid(false);
          setTextError(
            t(
              Localization(
                'invitation',
                'Your message exceeds the allowed message size',
              ),
            ) ?? '',
          );
        }
        break;
    }
    setValue(val);
  };
  const message = () => {
    switch (nameId) {
      case 'optional': {
        if (value.length > limitLength) {
          setMessageOfTextError(textError);
        } else {
          setMessageOfTextError('');
        }
        break;
      }
      case nameIdEmail: {
        setMessageOfTextError(textError);
        break;
      }
      default: {
        if (nameId !== 'PP') {
          setMessageOfTextError(textError + '  ' + nameId.toLowerCase());
        } else {
          setMessageOfTextError(textError);
        }
        break;
      }
    }
  };
  useEffect(() => {
    /** close eye when typing **/
    if (type === 'password') {
      setTYPE('password');
    }
    message();
    if (value.length > limitLength) {
      setDirty(true);
    } else setDirty(false);
  }, [value]);
  return (
    <Box width={'100%'}>
      <HStack sx={{justifyContent: 'flex-start'}}>
        <Stack
          direction={'row'}
          justifyContent={'space-between'}
          width={'100%'}>
          <NGText text={textLabel} myStyle={{fontSize: 13, fontWeight: 500}} />
          {textLabelRight}
        </Stack>

        {require && <NGText text={'*'} color={'red'} sx={{ml: 0.4}} />}
      </HStack>
      <TextField
        {...textFieldProps}
        variant={'outlined'}
        color={colorOnfocus}
        sx={{
          mt: '6px',
          '& .MuiOutlinedInput-root': {
            '&:hover fieldset': {
              borderColor: '#E9E9E9',
            },
            '& fieldset': {
              borderColor: value.length !== 0 ? 'Primary.main' : '#E9E9E9',
              borderWidth: '0.1px',
              height: rows > 1 ? rows * 30 - (rows * 30 * 20) / 100 : undefined,
              background: 'inherit',
            },
            '&.Mui-focused fieldset': {
              borderColor: 'Primary.main',
              borderWidth: '0.2px',
            },
          },
          ...propsInput,
        }}
        error={dirty && !isValid}
        multiline={rows !== 1}
        rows={rows}
        // maxRows={rows}
        onBlur={() =>
          nameId === 'optional' && value.length > limitLength
            ? setDirty(true)
            : setDirty(false)
        }
        helperText={
          <NGText
            text={messageOfTextError}
            color={color}
            sx={{fontSize: '14px'}}
          />
        }
        onChange={event => handleChange(event)}
        id={id}
        name={nameId}
        autoComplete={nameId}
        autoFocus={autoFocus}
        placeholder={placeholder}
        size={size}
        fullWidth
        value={value}
        type={TYPE}
        InputLabelProps={{
          shrink: true,
        }}
        inputProps={{
          maxLength: type === 'password' ? passwordLength : limitLength,
        }}
        InputProps={{
          style: {fontSize: 12, fontFamily: FONT_TYPE.POPPINS},
          startAdornment: Icon,
          endAdornment: type === 'password' && (
            <InputAdornment
              sx={{cursor: 'pointer'}}
              position="start"
              onClick={() => {
                if (TYPE === 'text') {
                  setTYPE('password');
                } else setTYPE('text');
              }}>
              {TYPE === 'text' ? (
                <Center sx={{height: '100%'}}>
                  <NGEyeOpen sx={{width: 14, height: 12.83}} />
                </Center>
              ) : (
                <Center sx={{height: '100%'}}>
                  <NGEyeClose sx={{width: 14, height: 12.83}} />
                </Center>
              )}
            </InputAdornment>
          ),
        }}
      />
    </Box>
  );
}

export default NGInput;
