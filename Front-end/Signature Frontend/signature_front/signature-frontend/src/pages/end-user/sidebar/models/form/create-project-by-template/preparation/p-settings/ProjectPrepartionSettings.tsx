import {
  NGDateSelectIcon,
  NGEmail,
  NGEmailNSms,
  NGMinus,
  NGPlus,
  NGSetting,
  NGSms,
} from '@/assets/Icon';
import {NGInputField} from '@/components/ng-input/NGInputField';
import {AutoReminder, ChannelOptions, FONT_TYPE} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {AntSwitch} from '@/pages/form/process-upload/edit-pdf/other/common';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  IconButton,
  InputAdornment,
  MenuItem,
  OutlinedInput,
  Radio,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {DesktopDatePicker, LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import {IPayloadParameters} from '@pages/end-user/sidebar/models/form/create-project-by-template/CreateProject';
import {
  getAutoReminder,
  getNotificationService,
} from '@pages/end-user/sidebar/models/form/create-project-by-template/ProjectEnvoi';
import dayjs, {Dayjs} from 'dayjs';
import {t} from 'i18next';
import React from 'react';
import {
  Control,
  Controller,
  FieldErrors,
  UseFormGetValues,
  UseFormSetValue,
} from 'react-hook-form';

type IModelDetail = {
  control: Control<IPayloadParameters, any>;
  errors: FieldErrors<IPayloadParameters>;
  setValue: UseFormSetValue<IPayloadParameters>;
  getValues: UseFormGetValues<IPayloadParameters>;
  setOptionReminder: React.Dispatch<React.SetStateAction<boolean>>;
};

const periodDay = [
  {
    key: 1,
    name: 'day',
  },
];

const ProjectPrepartionSettings = (props: IModelDetail) => {
  const {control, errors, setValue, setOptionReminder, getValues} = props;
  const {storeModel} = store.getState().authentication;
  const isReminder = Number(storeModel?.templateMessage.sendReminder) === 0;
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

  React.useEffect(() => {
    if (storeModel) {
      const {templateMessage, notificationService} = storeModel;
      const getNotificationService = (
        c: 'sms_email' | 'email' | 'sms',
      ): number => {
        const channel = {
          sms_email: 1,
          email: 2,
          sms: 1,
        };

        return channel[c];
      };

      if (templateMessage) {
        const {titleInvitation, messageInvitation, sendReminder, expiration} =
          templateMessage;
        setValue('purpose', titleInvitation);
        setValue('message', messageInvitation);
        // setValue('period', 0);
        setValue('dateExpired', dayjs().add(expiration, 'd'));
        setModifiedDate(dayjs().add(expiration, 'd'));
        setValue(
          'channel',
          getNotificationService(notificationService) as ChannelOptions,
        );
        setChannelReminder(
          getNotificationService(notificationService) as ChannelOptions,
        );
        if (sendReminder) {
          setToggleReminder(false);
          setValue('autoReminder', sendReminder);
          setAutoReminder(sendReminder);
        }
      }
    }
  }, [storeModel]);

  React.useEffect(() => {
    setValue('dateExpired', date);
  }, [date]);

  React.useEffect(() => {
    setValue('channel', channelReminder);
  }, [channelReminder]);

  React.useEffect(() => {
    setValue('autoReminder', autoReminder);
  }, [autoReminder]);

  React.useEffect(() => {
    setOptionReminder(toggleReminder);
  }, [toggleReminder]);

  return (
    <Stack
      sx={{
        width: '326px',
        overflow: 'hidden',
        py: '20px',
        px: '20px',
        borderLeft: '1px solid #E9E9E9',
      }}
      height="calc(100vh - 60px)">
      <Stack gap="30px">
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
                      width: '286px',
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
          <Stack gap="16px">
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
                    disabled={true}
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
                          borderColor: '#E9E9E9',
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
                          disabled={true}
                          onClick={() =>
                            value > 0 ? onChange(Number(value) - 1) : value
                          }
                          aria-label="toggle password visibility"
                          edge="start">
                          <NGMinus
                            sx={{fontSize: '12px', color: '#00000050'}}
                          />
                        </IconButton>
                      </InputAdornment>
                    }
                    endAdornment={
                      <InputAdornment position="end">
                        <IconButton
                          disabled={true}
                          onClick={() => onChange(Number(value) + 1)}
                          aria-label="toggle password visibility"
                          edge="end">
                          <NGPlus sx={{fontSize: '11px', color: '#00000050'}} />
                        </IconButton>
                      </InputAdornment>
                    }
                  />
                )}
                name="period"
              />

              <NGInputField<IPayloadParameters>
                size="small"
                disabled={true}
                sx={{
                  fontSize: 15,
                  width: '133px',
                  '& .MuiInputBase-root': {
                    fieldset: {
                      borderColor: '#E9E9E9',
                    },
                  },
                  '& .MuiSvgIcon-root': {
                    fill: '#00000050',
                  },
                }}
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
                      {t(Localization('models-corporate', item.name as any))}
                    </Typography>
                  </MenuItem>
                ))}
              </NGInputField>
            </Stack>
          </Stack>

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
                  overridecolor={'green'}
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
  );
};

export default ProjectPrepartionSettings;

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
      width="286px"
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
