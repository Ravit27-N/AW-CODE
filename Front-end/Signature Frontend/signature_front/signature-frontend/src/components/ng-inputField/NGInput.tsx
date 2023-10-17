import {Type, TypeSelect} from '@/components/ng-inputField/Type';
import {FONT_TYPE, Participant} from '@/constant/NGContant';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {FigmaInput} from '@/constant/style/themFigma/Input';
import {Localization} from '@/i18n/lan';
import {ISelectEnterPrise} from '@/pages/corporate-admin/Home.corporate';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {setRecipientInfo} from '@/redux/counter/CounterSlice';
import {
  setActiveActorRole,
  updateEnvoiByRole,
} from '@/redux/slides/authentication/authenticationSlide';
import {useUpdateProjectDetailMutation} from '@/redux/slides/project-management/project';
import {Center, HStack} from '@/theme';
import {
  NGEnterprise,
  NGEyeClose,
  NGEyeOpen,
  NGGroupOfPeople,
} from '@assets/iconExport/Allicon';
import NGText from '@components/ng-text/NGText';
import {
  Divider,
  FormControl,
  InputAdornment,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
} from '@mui/material';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import React, {CSSProperties, ReactNode, useEffect, useState} from 'react';
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
  passwordLength = 13,
}: Type) {
  const {t} = useTranslation();
  const [isValid, setIsValid] = useState(false);

  const [dirty, setDirty] = useState(nameId === 'PP');
  const nameIdEmail = t(Localization('form', 'email'));
  const [messageOfTextError, setMessageOfTextError] = useState<string | null>(
    '',
  );
  const [textError, setTextError] = useState<any>('');
  const [TYPE, setTYPE] = useState<'text' | 'password' | string>(type);
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
      // limitLength = 12;
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

export const NGSelect = ({
  name,
  setName,
  data,
  label,
  styleSelect,
}: TypeSelect) => {
  const {optionT} = useAppSelector(state => state.counter);
  const handleChange = (event: SelectChangeEvent) => {
    setName(event.target.value.toString());
  };
  const {t} = useTranslation();
  const [updateProjectDetail] = useUpdateProjectDetailMutation();

  const dispatch = useAppDispatch();

  const handleClickMenuItem = async () => {
    const {selectEnvoiData, activeActorEnvoi, project} =
      store.getState().authentication;
    dispatch(
      updateEnvoiByRole({
        role: activeActorEnvoi?.role!,
        data: {
          id: selectEnvoiData![`${activeActorEnvoi?.role!}`].id,
          name: optionT.docName ?? '',
          title: optionT.title,
          description: optionT.message,
          expired: optionT.opt4,
        },
      }),
    );

    try {
      const data = await updateProjectDetail({
        projectId: Number(project.id)!,
        messageInvitation: optionT.message,
        titleInvitation: optionT.title,
        type: activeActorEnvoi?.role!,
        id: selectEnvoiData![`${activeActorEnvoi?.role!}`].id || null,
      }).unwrap();

      dispatch(
        updateEnvoiByRole({
          role: data.type,
          data: {
            id: data.id,
            name: '',
            title: data.titleInvitation,
            description: data.messageInvitation,
            expired: new Date(data.expiresDate),
          },
        }),
      );
    } catch (e) {
      return e;
    }
  };

  return (
    <FormControl fullWidth>
      <Select
        value={name}
        label={label}
        onChange={handleChange}
        style={{
          minWidth: '200px',
          height: '50px',
          background: 'white',
          ...styleSelect,
        }}>
        {data.map((i: any) => {
          return (
            <MenuItem
              value={i.id.toString()}
              key={i.id.toString()}
              onClick={async () => {
                await handleClickMenuItem();
                dispatch(
                  setActiveActorRole({
                    role: i.role as Participant,
                    id: Number(i.id),
                    signatoryName: i.firstName + ' ' + i.lastName,
                  }),
                );
                dispatch(
                  setRecipientInfo({
                    id: i.id,
                    firstName: i.firstName,
                    email: i.email,
                    name: i.lastName,
                    invitationStatus: i.invitationStatus,
                    phoneNumber: i.phone,
                    projectId: i.projectId,
                    role: i.role,
                    sortOrder: i.sortOrder,
                  }),
                );
              }}
              sx={{justifyContent: 'space-between'}}>
              <Stack direction={'row'} spacing={1} alignItems={'center'}>
                <Avatar
                  sx={{
                    bgcolor: '#EAFAEB',
                    fontSize: 24,
                    width: '35px',
                    height: '35px',
                  }}>
                  <NGText
                    text={
                      i?.firstName?.slice(0, 1).toUpperCase() +
                      i?.lastName?.slice(0, 1).toUpperCase()
                    }
                    myStyle={{color: 'green', fontWeight: 'bold'}}
                  />
                </Avatar>
                <NGText
                  text={
                    i.firstName +
                    ' ' +
                    i.lastName +
                    ' ( ' +
                    t(Localization('upload-signatories', i.role)) +
                    ' )'
                  }
                  myStyle={{
                    ...StyleConstant.textSmall,
                    fontWeight: 500,
                    fontSize: 14,
                  }}
                />
              </Stack>
            </MenuItem>
          );
        })}
      </Select>
    </FormControl>
  );
};

type ISelect = {
  key: string | number;
  value: string;
};

export const NGSelectCorporate = ({
  name,
  setName,
  enterprise,
  label,
  styleSelect,
  group,
  iconData = <NGEnterprise sx={{color: 'Primary.main'}} />,
  iconData2 = <NGGroupOfPeople sx={{color: 'Primary.main'}} />,
}: {
  styleSelect?: CSSProperties;
  name: ISelectEnterPrise;
  setName: React.Dispatch<React.SetStateAction<ISelectEnterPrise>>;
  enterprise: Array<ISelect>;
  group: Array<ISelect>;
  label: string;
  iconData?: ReactNode;
  iconData2?: ReactNode;
}) => {
  const handleChange = (event: SelectChangeEvent) => {
    setName(prev => ({...prev, key: event.target.value}));
  };
  return (
    <FormControl fullWidth>
      <Select
        sx={{
          '&.MuiInputBase-root': {
            color: 'black.main', // set the color of the text
            fieldset: {
              borderColor: '#E9E9E9',
            },
          },
        }}
        size="small"
        value={name.key.toString()}
        label={label}
        onChange={handleChange}
        style={{
          minWidth: '280px',
          background: 'white',
          ...styleSelect,
        }}>
        {enterprise.map(({key, value}) => {
          return (
            <MenuItem
              value={key}
              key={key}
              sx={{justifyContent: 'space-between'}}>
              <Stack direction={'row'} spacing={1} alignItems={'center'}>
                {iconData}
                <NGText text={value} sx={{...FigmaInput.InputTextMediumBold}} />
              </Stack>
            </MenuItem>
          );
        })}
        <Divider sx={{width: '100%'}} />
        {group.length > 0 &&
          group.map(({key, value}) => {
            return (
              <MenuItem
                value={key}
                key={key}
                sx={{justifyContent: 'space-between'}}>
                <Stack direction={'row'} spacing={1} alignItems={'center'}>
                  {iconData2}
                  <NGText
                    text={value}
                    sx={{...FigmaInput.InputTextMediumBold}}
                  />
                </Stack>
              </MenuItem>
            );
          })}
      </Select>
    </FormControl>
  );
};
