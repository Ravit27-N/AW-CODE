import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {Center, VStack} from '@/theme';
import Logo from '@assets/background/login/NGLogo.svg';
import {NGButton} from '@components/ng-button/NGButton';
import {NGSelect} from '@components/ng-inputField/NGInput';
import {default as MyText, default as NGText} from '@components/ng-text/NGText';
import {Divider, Stack, Typography} from '@mui/material';
import {Box} from '@mui/system';
import {EmailHeader} from '@pages/form/process-upload/envoi/function';
import React, {useEffect} from 'react';
import {useTranslation} from 'react-i18next';
import {TypeProps} from './function/type';
import {DateFrench} from '@/utils/common';

function Right({props}: TypeProps) {
  const {t} = useTranslation();
  const {signatories, approvals, recipients, viewers} = useAppSelector(
    state => state.authentication,
  );
  const {theme} = useAppSelector(state => state.enterprise);
  const activeActor = store.getState().authentication.activeActorEnvoi;
  const {user} = useAppSelector(state => state.authentication);
  const {optionT} = useAppSelector(state => state.counter);
  const [name, setName] = React.useState<string>('');
  useEffect(() => {
    if (signatories) {
      setName(`${activeActor?.id}`);
    }
  }, []);

  const actor =
    signatories.find(item => item.id === Number(activeActor?.id)) ??
    approvals.find(item => item.id === Number(activeActor?.id)) ??
    recipients.find(item => item.id === Number(activeActor?.id)) ??
    viewers.find(item => item.id === Number(activeActor?.id));
  const date = new Date(optionT.opt4).toLocaleDateString('en-US', {
    month: '2-digit',
    day: '2-digit',
    year: 'numeric',
  });
  const time = new Date(optionT.opt4).toLocaleTimeString('en-US', {
    // hourCycle: 'h23',
    timeZone: 'Europe/Paris',
  });

  /** validate date before using DateFrench **/
  const DATE = () => {
    if (date === 'Invalid Date') {
      return new Date();
    }
    return new Date(date);
  };
  const {month, Day, day, year} = DateFrench(DATE());
  return (
    <Center
      width={'100%'}
      height={'auto'}
      sx={{...props, height: 'auto', mt: '3%'}}>
      <Center width={'80%'}>
        <Stack direction={'row'} width={'80%'} mb={3} alignItems={'center'}>
          <NGText
            text={t(Localization('invitation', 'Preview as'))}
            myStyle={{
              ...StyleConstant.textSmall,
              width: '50%',
              fontSize: 13,
              fontWeight: 500,
              color: 'black.main',
            }}
          />
          <NGSelect
            label={''}
            name={name}
            setName={setName}
            data={[...signatories, ...approvals, ...recipients, ...viewers]}
          />
        </Stack>
        <Center
          width={'100%'}
          sx={{
            backgroundColor: 'white',
            border: 2,
            borderColor: 'bg.main',
            borderRadius: 1,
            p: 4,
          }}>
          <Stack sx={{alignSelf: 'flex-start'}} spacing={0.5}>
            <EmailHeader
              option={t(Localization('invitation', 'from'))}
              position={optionT.opt1}
              email={`<signature@certigna.com>`}
            />

            {actor && (
              <EmailHeader
                option={t(Localization('invitation', 'for'))}
                position={actor.firstName + ' ' + actor.lastName}
                email={`<${actor.email}>`}
              />
            )}

            <EmailHeader
              option={t(Localization('invitation', 'object'))}
              position={
                optionT.title === ''
                  ? t(Localization('invitation', 'invitation-to-sign')) +
                    ' ' +
                    user.username
                  : optionT.title
              }
              email={''}
            />
          </Stack>
          <Divider sx={{width: '100%', mt: 2}} />

          <Stack alignItems={'center'} justifyContent={'center'}>
            <Center spacing={1.2} sx={{py: 5}}>
              <img
                src={theme[0].logo ?? Logo}
                style={{height: '32px', maxWidth: '137px'}}
                alt={'Logo'}
              />
              <NGText
                text={
                  t(Localization('text', 'hello')) +
                  ' ' +
                  activeActor?.signatoryName +
                  ','
                }
                myStyle={{
                  ...StyleConstant.textBold,
                  fontSize: 16,
                  fontWeight: 600,
                }}
              />

              <NGText
                text={
                  optionT.message === ''
                    ? t(
                        Localization(
                          'invitation',
                          'you have been invited to sign the documents in the file',
                        ),
                      )
                    : optionT.message
                }
                myStyle={{
                  ...StyleConstant.textSmall,
                  fontSize: 16,
                  color: 'black.main',
                  fontWeight: 400,
                }}
              />
              <NGText
                text={`"` + optionT.docName + `"`}
                myStyle={{
                  ...StyleConstant.textSmall,
                  fontSize: 16,
                  color: 'Primary.main',
                  fontWeight: 400,
                }}
              />

              <Box py={1.5}>
                <NGButton
                  icon={<></>}
                  title={t(Localization('invitation', 'Access the file'))}
                  size={'large'}
                  bgColor={'primary'}
                />
              </Box>
              <NGText
                text={t(
                  Localization(
                    'invitation',
                    'To view and sign the documents, click on the button below',
                  ),
                )}
                myStyle={{
                  ...StyleConstant.textSmall,
                  color: 'black.main',
                  fontSize: 12,
                  fontWeight: 400,
                  textAlign: 'center',
                }}
              />
              <NGText
                text={`
                ${t(Localization('invitation', 'You have until'))} 
                ${Day}  
                ${day}
                ${month}
                 ${year}
                ${t(Localization('text', 'at'))}
                ${time}
                ${t(Localization('invitation', 'to sign the documents'))}
                `}
                myStyle={{
                  ...StyleConstant.textBold,
                  fontSize: 12,
                  fontWeight: 600,
                  textAlign: 'center',
                }}
              />
              <Box>
                <VStack
                  sx={{
                    width: '100%',
                    alignItems: 'center',
                    mt: 2,
                  }}>
                  <MyText
                    text={t(Localization('invitation', 'sincerely'))}
                    myStyle={{
                      ...StyleConstant.textSmall,
                      fontSize: 12,
                      color: '#000000',
                      fontWeight: 400,
                    }}
                  />
                  <Typography
                    sx={{
                      ...StyleConstant.textSmall,
                      fontSize: 12,
                      color: '#000000',
                      fontWeight: 400,
                    }}>
                    {t(Localization('invitation', 'all-date-fix-at'))}{' '}
                    <b>UTC+02:00</b>, Europe/Paris
                  </Typography>
                </VStack>
              </Box>
            </Center>
          </Stack>
        </Center>
      </Center>
    </Center>
  );
}

export default Right;
