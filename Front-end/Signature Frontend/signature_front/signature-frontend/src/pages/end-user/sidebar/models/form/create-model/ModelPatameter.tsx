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
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  Card,
  CardContent,
  IconButton,
  InputAdornment,
  MenuItem,
  OutlinedInput,
  Radio,
  Stack,
  Typography,
} from '@mui/material';
import {Dayjs} from 'dayjs';
import {t} from 'i18next';
import React from 'react';
import {
  Control,
  Controller,
  FieldErrors,
  UseFormGetValues,
  UseFormSetValue,
} from 'react-hook-form';
import {IPayloadParameters} from './CreateModel';

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

const ModelParameter = (props: IModelDetail) => {
  const {control, errors, setValue, setOptionReminder, getValues} = props;
  const {createModel} = store.getState().authentication;
  const [channelReminder, setChannelReminder] =
    React.useState<ChannelOptions>(1);
  const [autoReminder, setAutoReminder] = React.useState<AutoReminder>(1);
  const [toggleReminder, setToggleReminder] = React.useState<boolean>(true);
  const [date, setModifiedDate] = React.useState<Dayjs | null>(
    getValues('dateExpired'),
  );
  const handleChangeValue = (value: Dayjs | null) => {
    setModifiedDate(value);
  };

  React.useEffect(() => {
    if (createModel) {
      const {templateMessage, notificationService} = createModel;
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
        const {titleInvitation, messageInvitation, sendReminder} =
          templateMessage;
        setValue('purpose', titleInvitation);
        setValue('message', messageInvitation);
        // setValue('dateExpired', dayjs(expireDate.split('T')[0]));
        // setModifiedDate(dayjs(expireDate.split('T')[0]));
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
  }, [createModel]);

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
    <Stack p="60px 270px">
      <Card sx={{p: '40px', width: '100%'}}>
        <CardContent
          sx={{
            p: 0,
          }}>
          <Stack gap="30px">
            <Stack gap="14px">
              <Stack direction="row" gap="6px">
                <NGSetting color="primary" />
                <Typography
                  sx={{
                    fontSize: '18px',
                    fontWeight: 600,
                    fontFamily: 'Poppins',
                  }}>
                  {t(Localization('models-corporate', 'parameter'))}
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
              <NGInputField<IPayloadParameters>
                size="small"
                sx={{fontSize: 15, minWidth: '15rem'}}
                control={control}
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
              <NGInputField<IPayloadParameters>
                multiline
                minRows={5}
                maxRows={5}
                size="small"
                sx={{fontSize: 15, minWidth: '15rem'}}
                control={control}
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

            <Stack direction="row" gap="6px">
              <NGDateSelectIcon color="primary" sx={{mt: '3.2px'}} />
              <Typography
                sx={{
                  fontSize: '18px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'manage-date'))}
              </Typography>
            </Stack>

            <Stack gap="28px">
              {/* Expired date */}
              {/* <Stack gap="16px">
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
                    {t(
                      Localization(
                        'models-corporate',
                        'expired-date-description',
                      ),
                    )}
                  </Typography>
                </Stack>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <DesktopDatePicker
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
              </Stack> */}

              {/* Expired day */}
              <Stack gap="16px">
                <Stack gap="4px">
                  <Typography
                    sx={{
                      fontSize: '14px',
                      fontWeight: 600,
                      fontFamily: 'Poppins',
                    }}>
                    {t(Localization('models-corporate', 'expired-day'))}
                  </Typography>
                  <Typography
                    sx={{
                      fontSize: '12px',
                      fontFamily: 'Poppins',
                    }}>
                    {t(
                      Localization(
                        'models-corporate',
                        'expired-date-description',
                      ),
                    )}
                  </Typography>
                </Stack>
                <Stack gap="8px">
                  <Stack direction="row" gap="16px">
                    <Controller
                      rules={{
                        validate: (value: any): any => {
                          if (value < 1) {
                            return t(
                              Localization(
                                'models-corporate',
                                'at-least-one-day-expiration',
                              ),
                            );
                          }
                        },
                      }}
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
                          value={Number(value)}
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
                                  value > 0
                                    ? onChange(Number(value) - 1)
                                    : value
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
                      name="dayExpired"
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
                            {t(
                              Localization(
                                'models-corporate',
                                item.name as any,
                              ),
                            )}
                          </Typography>
                        </MenuItem>
                      ))}
                    </NGInputField>
                  </Stack>
                  {errors.dayExpired && (
                    <Typography
                      color="red"
                      sx={{
                        fontSize: '12px',
                      }}>
                      {errors.dayExpired.message}
                    </Typography>
                  )}
                </Stack>
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
                <Stack gap="8px">
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
                                  value > 0
                                    ? onChange(Number(value) - 1)
                                    : value
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
                            {t(
                              Localization(
                                'models-corporate',
                                item.name as any,
                              ),
                            )}
                          </Typography>
                        </MenuItem>
                      ))}
                    </NGInputField>
                  </Stack>
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
                              color: '#0065E0',
                            }}
                          />
                        </Stack>
                      }
                      active={channelReminder === 1}
                    />
                  </IconButton>

                  {/* Email only */}
                  <IconButton
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
                              color: '#0065E0',
                            }}
                          />
                        </Stack>
                      }
                      active={channelReminder === 2}
                    />
                  </IconButton>

                  {/* SMS only */}
                  <IconButton
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
                              color: '#0065E0',
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
                      checked={!toggleReminder}
                      overridecolor="green"
                      onClick={() => setToggleReminder(!toggleReminder)}
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
                    disabled={toggleReminder}
                    onClick={() => setAutoReminder(1)}
                    sx={{color: '#000000', p: 0}}
                    disableFocusRipple
                    disableTouchRipple
                    disableRipple>
                    <ChannelReminder
                      text={t(Localization('models-corporate', '1-time-a-day'))}
                      height="44px"
                      disabled={toggleReminder}
                      active={autoReminder === 1}
                    />
                  </IconButton>

                  {/* Every 2 days */}
                  <IconButton
                    disabled={toggleReminder}
                    onClick={() => setAutoReminder(2)}
                    sx={{color: '#000000', p: 0}}
                    disableFocusRipple
                    disableTouchRipple
                    disableRipple>
                    <ChannelReminder
                      disabled={toggleReminder}
                      text={t(Localization('models-corporate', 'every-2-day'))}
                      height="44px"
                      active={autoReminder === 2}
                    />
                  </IconButton>

                  {/* every week */}
                  <IconButton
                    disabled={toggleReminder}
                    onClick={() => setAutoReminder(3)}
                    sx={{color: '#000000', p: 0}}
                    disableFocusRipple
                    disableTouchRipple
                    disableRipple>
                    <ChannelReminder
                      disabled={toggleReminder}
                      text={t(Localization('models-corporate', 'every-week'))}
                      height="44px"
                      active={autoReminder === 3}
                    />
                  </IconButton>

                  {/* every 2 weeks */}
                  <IconButton
                    disabled={toggleReminder}
                    onClick={() => setAutoReminder(4)}
                    sx={{color: '#000000', p: 0}}
                    disableFocusRipple
                    disableTouchRipple
                    disableRipple>
                    <ChannelReminder
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
        </CardContent>
      </Card>
    </Stack>
  );
};

export default ModelParameter;

type IChannelReminder = {
  text: string;
  active: boolean;
  height?: string;
  icon?: React.ReactNode;
  disabled?: boolean;
};

export const ChannelReminder = (props: IChannelReminder) => {
  const {text, active, height = '64px', icon, disabled} = props;

  return (
    <Stack
      width="400px"
      height={height}
      gap="12px"
      direction="row"
      alignItems="center"
      justifyContent="start"
      border={`0.5px solid ${
        active ? store.getState().enterprise.theme[0].mainColor : '#E9E9E9'
      }`}
      borderRadius="6px">
      <Radio
        disabled={disabled}
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
