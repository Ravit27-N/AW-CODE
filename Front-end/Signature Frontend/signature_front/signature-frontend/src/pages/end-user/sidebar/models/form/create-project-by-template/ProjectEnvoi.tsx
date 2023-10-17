import {
  NGDateSelectIcon,
  NGEmail,
  NGEmailNSms,
  NGFileTitle,
  NGSetting,
  NGSms,
} from '@/assets/Icon';
import {NGInputField} from '@/components/ng-input/NGInputField';
import {
  AutoReminder,
  ChannelOptions,
  FONT_TYPE,
  Participant,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {AntSwitch} from '@/pages/form/process-upload/edit-pdf/other/common';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  setActiveActorRole,
  updateEnvoiByRole,
} from '@/redux/slides/authentication/authenticationSlide';
import {TemplateInterface} from '@/redux/slides/profile/template/templateSlide';
import {useUpdateProjectDetailMutation} from '@/redux/slides/project-management/project';
import {DateFrench} from '@/utils/common';
import {HandleException} from '@/utils/common/HandleException';
import {
  getFirstNameAndLastName,
  getNameByFirstIndex,
} from '@/utils/common/HandlerFirstName_LastName';
import certignaLogo from '@assets/background/login/NGLogo.svg';
import {NGInfo} from '@assets/iconExport/Allicon';
import NGGroupAvatar from '@components/ng-group-avatar/NGGroupAvatar';
import NGText from '@components/ng-text/NGText';
import {
  Button,
  Divider,
  IconButton,
  MenuItem,
  Radio,
  Select,
  SelectChangeEvent,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {DesktopDatePicker, LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import {IPayloadParameters} from '@pages/end-user/sidebar/models/form/create-model/CreateModel';
import dayjs, {Dayjs} from 'dayjs';
import {t} from 'i18next';
import {enqueueSnackbar} from 'notistack';
import React from 'react';
import {
  Control,
  FieldErrors,
  UseFormGetValues,
  UseFormSetValue,
  UseFormWatch,
} from 'react-hook-form';

type IModelDetail = {
  control: Control<IPayloadParameters & {projectName: string}, any>;
  errors: FieldErrors<IPayloadParameters & {projectName: string}>;
  setValue: UseFormSetValue<IPayloadParameters & {projectName: string}>;
  getValues: UseFormGetValues<IPayloadParameters & {projectName: string}>;
  watch: UseFormWatch<IPayloadParameters & {projectName: string}>;
};

export const getNotificationService = (
  c: 'sms_email' | 'email' | 'sms',
): ChannelOptions => {
  const channel = {
    sms_email: 1,
    email: 2,
    sms: 3,
  };

  return channel[c] as ChannelOptions;
};

export const getAutoReminder = (
  r: TemplateInterface['templateMessage'],
): AutoReminder => {
  let result = 1 as AutoReminder;
  if (r) {
    if (Number(r.sendReminder) !== 0) {
      result = r.sendReminder!;
    }
  }

  return result;
};

const getExpireDate = (d: TemplateInterface['templateMessage']) => {
  let result: string | Date = new Date();
  if (d) {
    result = dayjs().add(d.expiration, 'd').toISOString();
  }

  return result;
};

const ProjectEnvoi = (props: IModelDetail) => {
  const {control, errors, setValue, getValues, watch} = props;
  const dispatch = useAppDispatch();
  const {
    storeModel,
    activeActorEnvoi,
    signatories,
    approvals,
    recipients,
    selectEnvoiData,
    viewers,
  } = store.getState().authentication;
  const participants = [
    ...signatories,
    ...approvals,
    ...recipients,
    ...viewers,
  ];
  const isReminder = Number(storeModel?.templateMessage.sendReminder) === 0;
  const [preview, setPreview] = React.useState({
    projectName: '',
    purpose: '',
    message: '',
  });
  const [select, setSelect] = React.useState(
    participants.find(item => item.id === activeActorEnvoi?.id)!.id,
  );
  const [updateProjectDetail] = useUpdateProjectDetailMutation();
  const colorModified = storeModel?.notificationService ? '#BFBFBF' : '#0065E0';
  const [channelReminder, setChannelReminder] = React.useState<ChannelOptions>(
    storeModel ? getNotificationService(storeModel.notificationService) : 1,
  );
  const [autoReminder, setAutoReminder] = React.useState<AutoReminder>(
    storeModel ? getAutoReminder(storeModel.templateMessage) : 1,
  );
  const [toggleReminder, setToggleReminder] = React.useState<boolean>(
    storeModel ? !getAutoReminder(storeModel.templateMessage) : true,
  );
  const [date, setModifiedDate] = React.useState<Dayjs | null>(
    getValues('dateExpired'),
  );
  const getReminderFixed =
    storeModel!.templateMessage && !!storeModel!.templateMessage.sendReminder;
  const handleChangeValue = (value: Dayjs | null) => {
    setModifiedDate(value);
  };

  /** handle select change & call request endpoint */
  const handleClickMenuItem = async () => {
    const {selectEnvoiData, activeActorEnvoi, project} =
      store.getState().authentication;
    try {
      const data = await updateProjectDetail({
        projectId: Number(project.id)!,
        messageInvitation: preview.message,
        titleInvitation: preview.purpose,
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
    } catch (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  /** handleChange select participant */
  const handleChangeSelect = async (e: SelectChangeEvent) => {
    setSelect(Number(e.target.value));
  };

  /** set value fill-form to from store-model state  */
  React.useEffect(() => {
    if (storeModel) {
      const {templateMessage, notificationService} = storeModel;

      if (templateMessage) {
        const {titleInvitation, messageInvitation, sendReminder, expiration} =
          templateMessage;
        const {title, description} = selectEnvoiData![activeActorEnvoi?.role!];

        setValue('purpose', title !== '' ? title : titleInvitation);
        setValue(
          'message',
          description !== '' ? description : messageInvitation,
        );
        setValue('dateExpired', dayjs().add(expiration, 'd'));
        setModifiedDate(dayjs().add(expiration, 'd'));
        setValue('channel', getNotificationService(notificationService));
        setChannelReminder(getNotificationService(notificationService));
        if (sendReminder) {
          setToggleReminder(false);
          setValue('autoReminder', sendReminder);
          setAutoReminder(sendReminder);
        }
      }
    }
  }, [storeModel]);

  /** change state base on participants select changed */
  React.useEffect(() => {
    if (storeModel) {
      const {templateMessage} = storeModel;
      if (templateMessage) {
        const {title, description} = selectEnvoiData![activeActorEnvoi?.role!];
        const {titleInvitation, messageInvitation} = templateMessage;
        setValue('purpose', title !== '' ? title : titleInvitation);
        setValue(
          'message',
          description !== '' ? description : messageInvitation,
        );
      }
    }
  }, [activeActorEnvoi]);

  /** set value expired date */
  React.useEffect(() => {
    setValue('dateExpired', date);
  }, [date]);

  /** set value channel reminder */
  React.useEffect(() => {
    setValue('channel', channelReminder);
  }, [channelReminder]);

  /** set value auto reminder */
  React.useEffect(() => {
    setValue('autoReminder', autoReminder);
  }, [autoReminder]);

  /** set value while fill form field purpose, message, projectName*/
  React.useEffect(() => {
    setPreview(prev => ({
      ...prev,
      projectName: getValues('projectName'),
      purpose: getValues('purpose'),
      message: getValues('message'),
    }));
  }, [watch('purpose'), watch('message'), watch('projectName')]);
  // store expire date
  const expireDate = storeModel
    ? getExpireDate(storeModel.templateMessage)
    : new Date();

  const time = new Date(expireDate).toLocaleTimeString('en-US', {
    timeZone: 'Europe/Paris',
  });

  const DATE = () => {
    return new Date(expireDate);
  };
  const {month, Day, day, year} = DateFrench(DATE());

  return (
    <Stack direction="row">
      {/* left side fill form */}
      <Stack sx={{width: '620px'}} p="12px 40px">
        <Stack gap="30px">
          <Stack gap="14px">
            <Stack direction="row" gap="6px" alignItems="center">
              <NGFileTitle
                color="primary"
                sx={{
                  width: '20px',
                  mt: '2px',
                }}
              />
              <Typography
                sx={{
                  fontSize: '14px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('invitation', 'Personalize the invitation'))}
              </Typography>
            </Stack>
            <Stack direction="row" gap="6px" alignItems="center">
              <NGInfo
                color="primary"
                sx={{
                  width: '20px',
                }}
              />
              <Typography
                sx={{
                  fontSize: '18px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('invitation', 'information'))}
              </Typography>
            </Stack>
            <Typography
              sx={{
                fontSize: '12px',
                fontFamily: 'Poppins',
              }}>
              {t(Localization('models-corporate', 'parameter-template'))}
            </Typography>
          </Stack>

          <Stack gap="16px">
            <Stack gap="4px">
              <Typography
                sx={{
                  fontSize: '14px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('invitation', 'name-project'))}
              </Typography>
              <Typography
                sx={{
                  fontSize: '12px',
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('invitation', 'visible-to-the-participants'))}
              </Typography>
            </Stack>
            <NGInputField<IPayloadParameters & {projectName: string}>
              size="small"
              sx={{fontSize: 15, minWidth: '15rem'}}
              control={control}
              eMessage={'...'}
              errorInput={errors.purpose ?? undefined}
              typeInput={'name'}
              type={'text'}
              name={'projectName'}
              placeholder=""
              style={{fontFamily: FONT_TYPE.POPPINS}}
            />
          </Stack>

          <Stack gap="16px">
            <Stack gap="4px">
              <Typography
                sx={{
                  fontSize: '14px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'purpose-invitation'))}
              </Typography>
              <Typography
                sx={{
                  fontSize: '12px',
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'subject-invitation'))}
              </Typography>
            </Stack>
            <NGInputField<IPayloadParameters & {projectName: string}>
              size="small"
              sx={{fontSize: 15, minWidth: '15rem'}}
              control={control}
              eMessage={'...'}
              errorInput={errors.purpose ?? undefined}
              typeInput={'name'}
              type={'text'}
              name={'purpose'}
              placeholder={
                t(Localization('models-corporate', 'personal-object'))!
              }
              style={{fontFamily: FONT_TYPE.POPPINS}}
            />
          </Stack>

          <Stack gap="16px">
            <Stack gap="4px">
              <Typography
                sx={{
                  fontSize: '14px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'personal-message'))}
              </Typography>
              <Typography
                sx={{
                  fontSize: '12px',
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'message-recipients'))}
              </Typography>
            </Stack>
            <NGInputField<IPayloadParameters & {projectName: string}>
              multiline
              minRows={5}
              maxRows={5}
              size="small"
              sx={{fontSize: 15, minWidth: '15rem'}}
              control={control}
              eMessage={'...'}
              errorInput={errors.message ?? undefined}
              typeInput={'name'}
              type={'text'}
              name={'message'}
              placeholder={
                t(Localization('models-corporate', 'your-per-message'))!
              }
              style={{fontFamily: FONT_TYPE.POPPINS}}
            />
          </Stack>

          <Stack direction="row" gap="6px" alignItems="center">
            <NGSetting
              color="primary"
              sx={{
                width: '20px',
              }}
            />
            <Typography
              sx={{
                fontSize: '18px',
                fontWeight: 600,
                fontFamily: 'Poppins',
              }}>
              {t(Localization('models-corporate', 'parameter'))}
            </Typography>
          </Stack>

          <Stack gap="28px">
            {/* Expired date */}
            <Stack gap="16px">
              <Stack gap="4px">
                <Typography
                  sx={{
                    fontSize: '14px',
                    fontWeight: 600,
                    fontFamily: 'Poppins',
                  }}>
                  {t(Localization('models-corporate', 'expired-date'))}
                </Typography>
                <Typography
                  sx={{
                    fontSize: '12px',
                    fontFamily: 'Poppins',
                  }}>
                  {t(Localization('models-corporate', 'period-recipient-sign'))}
                </Typography>
              </Stack>
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DesktopDatePicker
                  disabled={true}
                  minDate={dayjs(Date.now()).startOf('d')}
                  value={date}
                  inputFormat="MM/DD/YYYY"
                  onChange={handleChangeValue}
                  components={{
                    OpenPickerIcon: NGDateSelectIcon,
                  }}
                  renderInput={(params: any) => (
                    <TextField
                      size="small"
                      sx={{
                        width: '400px',
                        '& .MuiInputBase-root': {
                          borderRadius: '6px',
                          fieldset: {
                            borderColor: 'Primary.main',
                          },
                          '&.Mui-focused fieldset': {
                            borderColor: 'Primary.main',
                            borderWidth: '0.2px',
                          },
                          '&:hover fieldset': {
                            borderColor: '#E9E9E9',
                          },
                          '& .MuiSelect-icon': {
                            color: 'black.main', // set the color of the arrow icon
                          },
                        },
                      }}
                      {...params}
                    />
                  )}
                />
              </LocalizationProvider>
            </Stack>

            {/* Reflection period */}
            {/* <Stack gap="16px">
                <Stack gap="4px">
                  <Typography
                    sx={{
                      fontSize: '14px',
                      fontWeight: 600,
                      fontFamily: 'Poppins',
                    }}>
                    {t(Localization('models-corporate', 'reflection-period'))}
                  </Typography>
                  <Typography
                    sx={{
                      fontSize: '12px',
                      fontFamily: 'Poppins',
                    }}>
                    {t(Localization('models-corporate', 'reflection-email'))}
                  </Typography>
                </Stack>
                <Stack direction="row" gap="16px">
                  <Controller
                    control={control}
                    render={({field: {onChange, onBlur, value}}) => (
                      <OutlinedInput
                        type="tel"
                        size="small"
                        color="secondary"
                        error={!!errors}
                        fullWidth={true}
                        inputProps={{
                          sx: {
                            textAlign: 'center',
                          },
                        }}
                        sx={{
                          width: '132px',
                          '& input::placeholder': {
                            fontSize: pixelToRem(14),
                          },
                          '&.MuiOutlinedInput-root': {
                            '& .MuiOutlinedInput-notchedOutline': {
                              borderColor:
                                value > 0 ? 'Primary.main' : '#E9E9E9',
                              borderWidth: '0.3px',
                            },

                            '& fieldset': {
                              borderColor: '#000000',
                            },
                            '&:hover fieldset': {
                              borderColor: '#E9E9E9',
                            },
                            '&.Mui-focused fieldset': {
                              borderColor: '#E9E9E9',
                              borderWidth: '0.2px',
                            },
                          },
                        }}
                        value={Number(value.toString())}
                        onBlur={onBlur}
                        onPaste={e => e.preventDefault()}
                        onChange={onChange}
                        onKeyPress={e => {
                          if (!/\d/.test(e.nativeEvent.key)) {
                            e.preventDefault();
                          }
                        }}
                        style={{
                          fontFamily: FONT_TYPE.POPPINS,
                          fontSize: 14,
                          fontWeight: 400,
                        }}
                        startAdornment={
                          <InputAdornment position="start">
                            <IconButton
                              onClick={() =>
                                value > 0 ? onChange(Number(value) - 1) : value
                              }
                              aria-label="toggle password visibility"
                              edge="start">
                              <NGMinus
                                sx={{fontSize: '12px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                        endAdornment={
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() => onChange(Number(value) + 1)}
                              aria-label="toggle password visibility"
                              edge="end">
                              <NGPlus
                                sx={{fontSize: '11px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                      />
                    )}
                    name="period"
                  />

                  <NGInputField<IPayloadParameters>
                    size="small"
                    sx={{fontSize: 15, width: '248px'}}
                    control={control}
                    typeInput={'select'}
                    type={'text'}
                    name={'day'}>
                    {periodDay.map(item => (
                      <MenuItem value={item.key} key={item.key}>
                        <Typography
                          sx={{
                            fontSize: '14px',
                            fontFamily: 'Poppins',
                          }}>
                          {item.name}
                        </Typography>
                      </MenuItem>
                    ))}
                  </NGInputField>
                </Stack>
              </Stack> */}

            {/* Channel reminder */}
            <Stack gap="16px">
              <Stack gap="4px">
                <Typography
                  sx={{
                    fontSize: '14px',
                    fontWeight: 600,
                    fontFamily: 'Poppins',
                  }}>
                  {t(Localization('models-corporate', 'channel'))}
                </Typography>
                <Typography
                  sx={{
                    fontSize: '12px',
                    fontFamily: 'Poppins',
                  }}>
                  {t(Localization('models-corporate', 'reminder'))}
                </Typography>
              </Stack>
              <Stack direction="row" gap="16px" flexWrap="wrap">
                {/* E-mail & SMS */}
                <IconButton
                  disabled={!!storeModel?.notificationService}
                  onClick={() => setChannelReminder(1)}
                  sx={{color: '#000000', p: 0}}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple>
                  <ChannelReminder
                    text={'E-mail & SMS'}
                    icon={
                      <Stack
                        sx={{
                          bgcolor: '#0065E010',
                          borderRadius: '50%',
                          p: '10px',
                        }}>
                        <NGEmailNSms
                          sx={{
                            color: colorModified,
                          }}
                        />
                      </Stack>
                    }
                    active={channelReminder === 1}
                    notificationService={storeModel?.notificationService}
                  />
                </IconButton>

                {/* Email only */}
                <IconButton
                  disabled={!!storeModel?.notificationService}
                  onClick={() => setChannelReminder(2)}
                  sx={{color: '#000000', p: 0}}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple>
                  <ChannelReminder
                    text={t(Localization('models-corporate', 'email-only'))}
                    icon={
                      <Stack
                        sx={{
                          bgcolor: '#0065E010',
                          borderRadius: '50%',
                          p: '10px',
                        }}>
                        <NGEmail
                          sx={{
                            color: colorModified,
                          }}
                        />
                      </Stack>
                    }
                    active={channelReminder === 2}
                  />
                </IconButton>

                {/* SMS only */}
                <IconButton
                  disabled={!!storeModel?.notificationService}
                  onClick={() => setChannelReminder(3)}
                  sx={{color: '#000000', p: 0}}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple>
                  <ChannelReminder
                    text={t(Localization('models-corporate', 'sms-only'))}
                    icon={
                      <Stack
                        sx={{
                          bgcolor: '#0065E010',
                          borderRadius: '50%',
                          p: '10px',
                        }}>
                        <NGSms
                          sx={{
                            color: colorModified,
                          }}
                        />
                      </Stack>
                    }
                    active={channelReminder === 3}
                  />
                </IconButton>
              </Stack>
            </Stack>

            {/* Auto reminder */}
            <Stack gap="16px">
              <Stack gap="4px">
                <Stack direction="row" gap="8px" alignItems="center">
                  <Typography
                    sx={{
                      fontSize: '14px',
                      fontWeight: 600,
                      fontFamily: 'Poppins',
                    }}>
                    {t(Localization('models-corporate', 'auto-reminder'))}
                  </Typography>
                  <AntSwitch
                    checked={!toggleReminder && !isReminder}
                    overridecolor="green"
                    onClick={() => {
                      if (isReminder) {
                        return;
                      }
                      if (!storeModel!.templateMessage) {
                        return setToggleReminder(!toggleReminder);
                      }

                      if (storeModel!.templateMessage) {
                        if (!storeModel!.templateMessage.sendReminder) {
                          return setToggleReminder(!toggleReminder);
                        }
                      }

                      return;
                    }}
                  />
                </Stack>
                <Typography
                  sx={{
                    fontSize: '12px',
                    fontFamily: 'Poppins',
                  }}>
                  {t(Localization('models-corporate', 'reminder-sign-doc'))}
                </Typography>
              </Stack>

              <Stack direction="row" gap="16px" flexWrap="wrap">
                {/* 1 time a day */}
                <IconButton
                  disabled={!toggleReminder || isReminder}
                  onClick={() => !isReminder && setAutoReminder(1)}
                  sx={{color: '#000000', p: 0}}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple>
                  <ChannelReminder
                    reminderFixed={getReminderFixed}
                    reminder={true}
                    text={t(Localization('models-corporate', '1-time-a-day'))}
                    height="44px"
                    disabled={toggleReminder}
                    active={autoReminder === 1}
                  />
                </IconButton>

                {/* Every 2 days */}
                <IconButton
                  disabled={!toggleReminder || isReminder}
                  onClick={() => !isReminder && setAutoReminder(2)}
                  sx={{color: '#000000', p: 0}}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple>
                  <ChannelReminder
                    reminderFixed={getReminderFixed}
                    reminder={true}
                    disabled={toggleReminder}
                    text={t(Localization('models-corporate', 'every-2-day'))}
                    height="44px"
                    active={autoReminder === 2}
                  />
                </IconButton>

                {/* every week */}
                <IconButton
                  disabled={!toggleReminder || isReminder}
                  onClick={() => !isReminder && setAutoReminder(3)}
                  sx={{color: '#000000', p: 0}}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple>
                  <ChannelReminder
                    reminderFixed={getReminderFixed}
                    reminder={true}
                    disabled={toggleReminder}
                    text={t(Localization('models-corporate', 'every-week'))}
                    height="44px"
                    active={autoReminder === 3}
                  />
                </IconButton>

                {/* every 2 weeks */}
                <IconButton
                  disabled={!toggleReminder || isReminder}
                  onClick={() => !isReminder && setAutoReminder(4)}
                  sx={{color: '#000000', p: 0}}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple>
                  <ChannelReminder
                    reminderFixed={getReminderFixed}
                    reminder={true}
                    disabled={toggleReminder}
                    text={t(Localization('models-corporate', 'every-2-week'))}
                    height="44px"
                    active={autoReminder === 4}
                  />
                </IconButton>
              </Stack>
            </Stack>
          </Stack>
        </Stack>
      </Stack>

      {/*  right side preview */}
      <Stack
        sx={{width: 'calc(100% - 620px)', px: '80px'}}
        p="12px 40px"
        alignItems="center"
        gap="24px">
        {/*  participant select */}
        <Stack direction="row" alignItems="center" gap="12px">
          <NGText
            text={t(Localization('invitation', 'Preview as'))}
            sx={{
              fontWeight: 500,
              fontSize: '13px',
            }}
          />

          <Select
            size="small"
            labelId="demo-simple-select-label"
            id="demo-simple-select"
            value={select!.toString()}
            sx={{
              width: 'auto',
            }}
            onChange={handleChangeSelect}>
            {participants.map(item => (
              <MenuItem
                value={item.id}
                key={item.id}
                onClick={async () => {
                  await handleClickMenuItem();
                  dispatch(
                    setActiveActorRole({
                      role: item.role as Participant,
                      id: Number(item.id),
                      signatoryName: getFirstNameAndLastName(
                        `${item.firstName} ${item.lastName}`,
                      ),
                      email: item.email,
                    }),
                  );
                }}>
                <Stack direction="row" alignItems="center" gap="10px">
                  <NGGroupAvatar
                    character={[
                      getNameByFirstIndex(`${item.firstName} ${item.lastName}`),
                    ]}
                  />
                  <Typography
                    sx={{
                      fontSize: '14px',
                      fontWeight: 500,
                    }}>
                    {getFirstNameAndLastName(
                      `${item.firstName} ${item.lastName}`,
                    )}
                  </Typography>

                  <Typography
                    sx={{
                      fontSize: '14px',
                      fontWeight: 500,
                    }}>
                    {`( ${item.role} )`}
                  </Typography>
                </Stack>
              </MenuItem>
            ))}
          </Select>
        </Stack>

        {/* preview block */}
        <Stack
          bgcolor="white"
          gap="24px"
          p={'24px 32px'}
          width="660px"
          border={`1px solid #E9E9E9`}>
          {/* preview participant header */}
          <Stack pb="8px">
            <PreviewHeader
              title={t(Localization('invitation', 'from'))}
              options={{
                name: 'Company',
                email: 'signature@certigna.com',
              }}
            />
            <PreviewHeader
              title={t(Localization('invitation', 'for'))}
              options={
                activeActorEnvoi
                  ? {
                      name: activeActorEnvoi.signatoryName,
                      email: activeActorEnvoi.email!,
                    }
                  : {
                      name: '',
                      email: '',
                    }
              }
            />
            <PreviewHeader
              title={t(Localization('invitation', 'object'))}
              name={preview.purpose}
            />
          </Stack>

          {/* divider */}
          <Divider sx={{borderBottomWidth: 1}} />

          <Stack p="32px 0px" gap="24px" alignItems="center">
            {/* logo */}
            <img
              src={certignaLogo}
              style={{
                height: 32,
                width: 'auto',
              }}
            />
            <Stack gap="8px" alignItems="center" textAlign="center">
              {/* signatory name */}
              <Typography
                sx={{
                  fontSize: '16px',
                  fontWeight: 600,
                }}>
                {t(Localization('text', 'hello')) +
                  ' ' +
                  `${
                    activeActorEnvoi
                      ? activeActorEnvoi.signatoryName.split(' ')[0]
                      : ''
                  }`}
                ,
              </Typography>

              {/* preview message */}
              <Stack>
                <Typography
                  sx={{
                    fontSize: '16px',
                    inlineSize: '500px',
                    overflowWrap: 'break-word',
                  }}>
                  {preview.message}
                </Typography>
                <Typography
                  sx={{
                    fontSize: '16px',
                  }}>
                  {`"` + preview.projectName + `"`}
                </Typography>
              </Stack>
            </Stack>

            {/* btn access file */}
            <Button
              variant="contained"
              sx={{
                width: '162px',
                fontSize: '13px',
                height: '48px',
                textTransform: 'none',
                boxShadow: 0,
              }}>
              {t(Localization('invitation', 'access-the-file'))}
            </Button>

            <Stack alignItems="center">
              <Typography fontSize="12px">
                {t(
                  Localization(
                    'invitation',
                    'To view and sign the documents, click on the button below',
                  ),
                )}
                :
              </Typography>
              <Typography
                sx={{
                  fontSize: '12px',
                  fontWeight: 600,
                }}>{`
                ${t(Localization('invitation', 'You have until'))}
                ${Day}
                ${day}
                ${month}
                ${year}
                ${t(Localization('text', 'at'))}
                ${time}
                ${t(Localization('invitation', 'to sign the documents'))}
                `}</Typography>
            </Stack>

            <Typography
              sx={{
                fontSize: '12px',
              }}>
              {t(Localization('invitation', 'sincerely'))}
            </Typography>
          </Stack>
        </Stack>
      </Stack>
    </Stack>
  );
};

type IPreviewHeader = {
  title: string;
  name?: string;
  options?: {
    name: string;
    email: string;
  };
};

const PreviewHeader = (props: IPreviewHeader) => {
  const {title, name, options} = props;
  return (
    <Stack direction="row" alignItems="center" gap="4px">
      <Typography>{title} : </Typography>
      {name && (
        <Typography sx={{fontSize: '14px', fontWeight: 600}}>{name}</Typography>
      )}
      {options && (
        <Stack direction="row">
          <Typography sx={{fontSize: '14px', fontWeight: 600}}>
            {options.name}
          </Typography>
          <Typography
            sx={{fontSize: '14px'}}>{`<${options.email}>`}</Typography>
        </Stack>
      )}
    </Stack>
  );
};

export default ProjectEnvoi;

type IChannelReminder = {
  text: string;
  active: boolean;
  height?: string;
  icon?: React.ReactNode;
  disabled?: boolean;
  notificationService?: string;
  sendReminder?: AutoReminder;
  reminder?: boolean;
  reminderFixed?: boolean;
};

export const ChannelReminder = (props: IChannelReminder) => {
  const {text, active, height = '64px', icon, reminder, reminderFixed} = props;
  const {storeModel} = useAppSelector(state => state.authentication);
  const isReminder = Number(storeModel?.templateMessage.sendReminder) === 0;

  const getColor = () => {
    let color = '#E9E9E9';
    if (active) {
      color = '#BFBFBF';
    }

    return color;
  };

  return (
    <Stack
      width="400px"
      height={height}
      gap="12px"
      direction="row"
      alignItems="center"
      justifyContent="start"
      border={`0.5px solid`}
      borderColor={`${
        (active && isReminder) || (reminderFixed && active)
          ? '#BFBFBF'
          : reminder && active
          ? 'Primary.main'
          : getColor()
      }`}
      borderRadius="6px">
      <Radio
        disabled={!reminder || (reminderFixed && active) || isReminder}
        checked={active}
        sx={{
          height: '30px',
        }}
        value={1}
        name="radio-buttons"
        inputProps={{'aria-label': 'A'}}
      />
      {icon}
      <Typography
        sx={{
          fontWeight: 300,
          fontSize: '12px',
          fontFamily: 'Poppins',
        }}>
        {text}
      </Typography>
    </Stack>
  );
};
