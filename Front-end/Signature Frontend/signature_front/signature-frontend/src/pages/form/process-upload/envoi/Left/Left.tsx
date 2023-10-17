import {
  AutoReminder,
  EnumAutoReminder,
  EnumChannelOptions,
  KeySignatureLevel,
} from '@constant/NGContant';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {setOption} from '@/redux/counter/CounterSlice';
import {Center} from '@/theme';
import {NGAlert, NGFileTitle, NGSetting} from '@assets/Icon';
import {
  NGMessagePlus,
  NGMessages,
  NGPhone,
  NGWatch,
} from '@assets/iconExport/Allicon';
import NGInput from '@components/ng-inputField/NGInput';
import NGText from '@components/ng-text/NGText';
import {Divider, Stack} from '@mui/material';
import Grid from '@mui/material/Grid';
import {Box} from '@mui/system';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {AntSwitch} from '@pages/form/process-upload/edit-pdf/other/common';
import React, {useEffect} from 'react';
import {useTranslation} from 'react-i18next';
import {useDispatch} from 'react-redux';
import {
  Box_Input,
  StyleMainTitle,
  StyleSecondText,
  Text_Input,
} from '../function';

import {TypeProps} from '../function/type';
import {StyleConstant} from '@constant/style/StyleConstant';
import AdvanceSignature from '@pages/form/process-upload/envoi/Left/components/AdvanceSignature';
import {setSignatureLevels} from '@/redux/slides/authentication/authenticationSlide';
import {RemainOption, RemainOptionToNumber} from '@/utils/common/covert';

interface Type {
  setGetDate: React.Dispatch<React.SetStateAction<Date>>;
  getDate: Date;
}

function DatePickerValue({setGetDate, getDate}: Type) {
  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Stack
        direction={'row'}
        alignItems={'center'}
        border={1}
        borderColor={'Primary.main'}
        width={'100%'}
        borderRadius={2}
        px={2}>
        <NGWatch
          fontSize="small"
          sx={{
            color: 'Primary.main',
            mr: 0.5,
            '&.myDatePicker fieldset.MuiOutlinedInput-notchedOutline': {
              borderColor: 'Primary.main',
            },
          }}
        />
        <input
          id="winterStartFrom:"
          value={
            new Date(getDate)
              .toLocaleDateString('zh-Hans-CN', {
                month: '2-digit',
                day: '2-digit',
                year: 'numeric',
              })
              .replace(/\//g, '-') +
            'T' +
            new Date(getDate).toLocaleTimeString('en-US', {
              hourCycle: 'h23',
              timeZone: 'Europe/Paris',
            })
          }
          type={'datetime-local'}
          // pattern="\d{4}-\d{1}-\d{1}"
          style={{
            paddingTop: 10,
            paddingBottom: 10,
            width: '100%',
            outline: 'none',
            border: 'none',
            fontFamily: 'Poppins',
          }}
          onChange={event => {
            if (getDate.toString() === 'Invalid Date') {
              return setGetDate(new Date());
            }
            setGetDate(new Date(event.target.value));
          }}
        />
      </Stack>
    </LocalizationProvider>
  );
}

function Left({props}: TypeProps) {
  /** select redux slide **/
  const {optionT} = useAppSelector(state => state.counter);
  const {activeActorEnvoi, selectEnvoiData, signatureLevels} = useAppSelector(
    state => state.authentication,
  );
  /** dispatch **/
  const dispatch = useDispatch();
  /** length of message in mail **/
  const limitMessage = 60;
  /** Date of exp **/
  const [getDate, setGetDate] = React.useState(new Date());
  /** state **/
  const {t} = useTranslation();
  /** setting general **/
  const [title, setTitle] = React.useState('');
  const [docName, setDocName] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [companyOrUsername] = React.useState<boolean>(true);
  /** Way of Remainder **/
  // const defaultRemainder = signatureLevels.channelReminder!.toUpperCase();
  // console.log(defaultRemainder);
  const [emailOrPhoneOrSms, setEmailOrPhoneOrSms] = React.useState<1 | 2 | 3>(
    RemainOptionToNumber(signatureLevels.channelReminder!)!,
  );
  /** Option of Remainder **/
  const [remain, setRemain] = React.useState<AutoReminder>(1);
  /** active on the option remainder **/
  const [IsActive, setIsActive] = React.useState(false);
  const handleChange = (event: any) => {
    setIsActive(event.target.checked);
  };

  /** take the value to Remainder in settings when emailOrPhoneOrSms was change **/
  useEffect(() => {
    dispatch(
      setSignatureLevels({
        ...signatureLevels,
        remainderSelected: RemainOption(emailOrPhoneOrSms),
      }),
    );
  }, [emailOrPhoneOrSms]);
  /** set title description **/
  useEffect(() => {
    if (activeActorEnvoi) {
      setTitle(selectEnvoiData![activeActorEnvoi.role].title || '');
      setMessage(selectEnvoiData![activeActorEnvoi.role].description || '');
    }
  }, [activeActorEnvoi]);
  /** set value to general setting **/
  useEffect(() => {
    dispatch(
      setOption({
        // opt1: companyOrUsername ? 'AllWeb' : username,
        opt1: 'Company',
        opt2: emailOrPhoneOrSms,
        opt3: IsActive ? remain : null,
        opt4: getDate,
        title,
        message,
        docName: docName ?? '', // project Name
      }),
    );
  }, [
    emailOrPhoneOrSms,
    companyOrUsername,
    remain,
    getDate,
    title,
    message,
    docName,
    IsActive,
  ]);
  /** Data for way of remainder**/
  const DataRemainder = [
    /** sms **/
    {
      id: 'sms',
      title: 'SMS seulement',
      checked: emailOrPhoneOrSms == EnumChannelOptions.SMS,
      onClick: () => setEmailOrPhoneOrSms(EnumChannelOptions.SMS),
      icon: <NGPhone sx={{fontSize: '25px', color: 'blue.dark'}} />,
    },
    /** email **/
    {
      id: 'email',
      title: 'E-mail seulement',
      checked: emailOrPhoneOrSms == EnumChannelOptions.EMAIL,
      onClick: () => setEmailOrPhoneOrSms(EnumChannelOptions.EMAIL),
      icon: <NGMessages sx={{fontSize: '20px', color: 'blue.dark'}} />,
    },
    /** sms_email **/
    {
      id: 'sms_email',
      title: 'E-mail & SMS',
      checked: emailOrPhoneOrSms == EnumChannelOptions.SMS_EMAIL,
      onClick: () => setEmailOrPhoneOrSms(EnumChannelOptions.SMS_EMAIL),
      icon: (
        <NGMessagePlus sx={{fontSize: '25px', color: 'blue.dark', ml: '5px'}} />
      ),
    },
  ];
  /** Data for way of option remainder**/
  const OptionRemainder = [
    /** 1 timer per day **/
    {
      id: '1TPD',
      title: t(Localization('invitation', `1 time per day`)),
      checked: remain == EnumAutoReminder.ONE_TIME_PER_DAY,
      onClick: () => {
        IsActive && setRemain(EnumAutoReminder.ONE_TIME_PER_DAY);
      },
    },
    /** every 2 days **/
    {
      id: 'E2D',
      title: t(Localization('invitation', `Every 2 days`)),
      checked: remain == EnumAutoReminder.EVERY_TWO_DAYS,
      onClick: () => {
        IsActive && setRemain(EnumAutoReminder.EVERY_TWO_DAYS);
      },
    },
    /** 1 time per week  **/
    {
      id: '1TPW',
      title: t(Localization('invitation', `1 time per week`)),
      checked: remain == EnumAutoReminder.ONE_TIME_PER_WEEK,
      onClick: () => {
        IsActive && setRemain(EnumAutoReminder.ONE_TIME_PER_WEEK);
      },
    },
    /** every 2 weeks  **/
    {
      id: 'E2W',
      title: t(Localization('invitation', `Every 2 weeks`)),
      checked: remain == EnumAutoReminder.EVERY_TWO_WEEKS,
      onClick: () => {
        IsActive && setRemain(EnumAutoReminder.EVERY_TWO_WEEKS);
      },
    },
  ];
  return (
    <Box width={'100%'} sx={{...props, height: '93vh'}}>
      <Stack height={'100%'} width={'100%'}>
        <Box sx={{px: 2}}>
          <NGText
            text={t(Localization('invitation', 'Personalize the invitation'))}
            myStyle={{
              ...StyleMainTitle,
            }}
            iconStart={<NGFileTitle />}
          />
        </Box>
        <Divider sx={{width: '100%'}} />
        <Stack p={2} spacing={2}>
          <NGText
            text={t(Localization('invitation', 'information'))}
            myStyle={{
              fontWeight: 600,
              fontSize: 18,
            }}
            iconStart={<NGAlert sx={{color: 'Primary.main'}} />}
          />
          <Text_Input
            text={Localization('invitation', 'name-project')}
            input={
              <NGInput
                colorOnfocus={'info'}
                autoFocus={true}
                textLabel={
                  <NGText
                    text={t(
                      Localization('invitation', 'visible-to-the-participants'),
                    )}
                    myStyle={{
                      ...StyleSecondText,
                    }}
                    color={'grey'}
                  />
                }
                type={'text'}
                placeholder={''}
                nameId={'optional'}
                setValue={setDocName}
                value={docName}
              />
            }
          />
          <Text_Input
            text={Localization('invitation', 'Title of the invitation')}
            input={
              <NGInput
                colorOnfocus={'info'}
                textLabel={
                  <NGText
                    text={t(
                      Localization('invitation', 'title-of-the-invitation'),
                    )}
                    myStyle={{
                      ...StyleSecondText,
                    }}
                    color={'grey'}
                  />
                }
                type={'text'}
                placeholder={t(
                  Localization(
                    'invitation',
                    'Change the title of the invitation email',
                  ),
                )}
                nameId={'optional'}
                setValue={setTitle}
                value={title}
              />
            }
          />
          <Text_Input
            text={Localization('invitation', 'Personalized message')}
            input={
              <NGInput
                rows={4}
                colorOnfocus={'info'}
                limitLength={limitMessage}
                textLabel={
                  <NGText
                    text={t(Localization('invitation', 'personalized-message'))}
                    myStyle={{
                      ...StyleSecondText,
                    }}
                    color={'grey'}
                  />
                }
                type={'text'}
                placeholder={t(
                  Localization('invitation', 'add-personalized-message'),
                )}
                nameId={'optional'}
                setValue={setMessage}
                value={message}
              />
            }
          />
          {/** General Form **/}
          <Divider sx={{width: '100%', mt: 2}} />
          <NGText
            text={t(Localization('text', 'Parameters'))}
            myStyle={{
              fontWeight: 600,
              fontSize: 18,
              ml: '5px',
            }}
            iconStart={<NGSetting sx={{color: 'Primary.main'}} />}
          />
          <Divider sx={{width: '100%', mt: 2}} />
          <Text_Input
            isInput={false}
            haveUnderline={false}
            text={Localization('invitation', 'Expiration date')}
            secondText={
              <NGText
                text={t(
                  Localization(
                    'invitation',
                    `Recipients will have access to the file until this date`,
                  ),
                )}
                myStyle={{
                  ...StyleSecondText,
                }}
                color={'grey'}
              />
            }
            box={
              <Box width={'100%'}>
                <DatePickerValue getDate={getDate} setGetDate={setGetDate} />

                {optionT.checkDate && (
                  <NGText
                    text={t(
                      Localization(
                        'invitation',
                        `You must select a date in the future`,
                      ),
                    )}
                    myStyle={{color: 'red', fontSize: '12px'}}
                  />
                )}
              </Box>
            }
          />
          <Stack sx={{width: '100%'}}>
            <Text_Input
              isInput={false}
              text={Localization('invitation', 'Recall channel')}
              secondText={
                <NGText
                  text={t(
                    Localization(
                      'invitation',
                      `The recipients will receive the reminders by this means`,
                    ),
                  )}
                  myStyle={{
                    ...StyleSecondText,
                  }}
                  color={'grey'}
                />
              }
              box={
                /** Remainder Option **/
                <Grid container width={'100%'} spacing={1}>
                  {DataRemainder.map(item => (
                    <Grid item md={6} key={item.id}>
                      <Box_Input
                        isDisable={
                          signatureLevels.channelReminder === 'sms_email'
                            ? false
                            : signatureLevels.channelReminder !== item.id
                        }
                        padding={2}
                        icon={item.icon}
                        title={item.title}
                        checked={item.checked}
                        onClick={item.onClick}
                      />
                    </Grid>
                  ))}
                </Grid>
              }
            />
          </Stack>
          <Center sx={{width: '100%'}} spacing={1}>
            <NGText
              text={t(Localization('invitation', 'Automatic reminders'))}
              myStyle={{
                ...StyleConstant.textBold,
                fontSize: 'large',
                fontWeight: 'bold',
                height: '100%',
              }}
              iconEnd={
                <AntSwitch
                  overridecolor={'green'}
                  onChange={handleChange}
                  sx={{ml: 2}}
                />
              }
            />
            <NGText
              text={t(
                Localization(
                  'invitation',
                  'reminders-will-be-sent-to-recipients-who-have-not-signed-the-document',
                ),
              )}
              myStyle={{
                ...StyleSecondText,
              }}
              color={'grey'}
            />
            <Grid container width={'100%'} spacing={1}>
              {/** time of remainder **/}
              {OptionRemainder.map(item => (
                <Grid item md={6} key={item.id}>
                  <Box_Input
                    isDisable={!IsActive}
                    haveIcon={false}
                    text={item.title}
                    checked={item.checked}
                    onClick={item.onClick}
                  />
                </Grid>
              ))}
            </Grid>
          </Center>
          {/** Form Advance **/}
          {signatureLevels.signatureLevel === KeySignatureLevel.ADVANCE && (
            <AdvanceSignature />
          )}
        </Stack>
      </Stack>
    </Box>
  );
}

export default Left;
