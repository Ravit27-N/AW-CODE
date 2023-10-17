import React from 'react';
import NGText from '@components/ng-text/NGText';
import {Box} from '@mui/system';
import {Button, Stack} from '@mui/material';
import {NGButton} from '@components/ng-button/NGButton';
import {
  colorBlack,
  colorDisable,
  colorWhite,
} from '@/constant/style/StyleConstant';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NGTabs} from '@components/ng-tab';
import {NGKeyTab} from '@assets/iconExport/Allicon';
import {Localization} from '@/i18n/lan';
import {useTranslation} from 'react-i18next';

import {useAppSelector} from '@/redux/config/hooks';
import {ClosePage} from '@/constant/NGContant';
import {Center} from '@/theme';
import FirstTab from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/tab/FirstTab';
import SecondTab from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/tab/SecondTab';
import {FigmaBody} from '@constant/style/themFigma/Body';

export const TitleTabInput = ({
  title,
  semiTitle,
}: {
  title: string;
  semiTitle: string;
}) => {
  return (
    <Stack>
      <NGText
        text={title}
        myStyle={{
          ...FigmaBody.BodyMediumBold,
        }}
      />
      <NGText
        text={semiTitle}
        myStyle={{
          ...FigmaBody.BodySmall,
        }}
      />
    </Stack>
  );
};

function SettingsCorporate() {
  const {t} = useTranslation();
  const reduxTheme = useAppSelector(state => state.enterprise);
  const [title, setTitle] = React.useState(
    'Pour continuer, consultez les conditions générales d’utilisation de la plateforme.',
  );
  const [signatureAdvance, setSignatureAdvance] = React.useState<{
    title1: string;
    title2: string;
    title3: string;
  }>({
    title1:
      'Pour continuer, consultez les conditions générales d’utilisation de la plateforme.',
    title2:
      'En continuant, vous acceptez de partager vos pièces justificatives.',
    title3: 'Importez votre pièce d’identité afin de prouver votre identité.',
  });
  const [emailOrPhoneOrSms, setEmailOrPhoneOrSms] = React.useState<
    '1' | '2' | '3'
  >('3');
  if (ClosePage) {
    return (
      <Center sx={{height: '100vh'}}>
        <NGText text={t(Localization('setting', 'setting'))} />
      </Center>
    );
  }
  return (
    <Box width={'100%'} height={'100%'}>
      <Stack
        width={'100%'}
        direction={'row'}
        justifyContent={'space-between'}
        borderBottom={2}
        py={'8px'}
        px={'20px'}
        borderColor={'bg.main'}
        alignItems={'center'}>
        <NGText
          text={t(Localization('setting', 'setting'))}
          myStyle={{fontSize: 16, fontWeight: 600}}
        />
        <NGButton
          title={
            <NGText
              text={t(Localization('enterprise-brand', 'save-change'))}
              myStyle={{color: 'white', fontSize: 12}}
            />
          }
          disabled={false}
          myStyle={{
            bgcolor: reduxTheme.theme[0].mainColor,
            py: 1,
            '&.MuiButton-contained': {
              fontWeight: 600,
            },
            '&.Mui-disabled': {
              bgcolor: colorDisable,
              color: colorWhite,
            },
            '&:hover': {
              bgcolor: colorBlack,
            },
          }}
        />
      </Stack>

      {/** Main Content  **/}
      <Box
        sx={{
          width: 'full',
          height: [
            pixelToRem(160),
            pixelToRem(150),
            pixelToRem(140),
            pixelToRem(165),
          ],
          backgroundImage: `url(${bgLogo})`,
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
          pt: {md: 2, lg: 5},
          px: 5,
          position: 'relative',
          borderBottom: 1,
          borderColor: '#E9E9E9',
        }}>
        <Stack>
          <NGText
            text={t(Localization('setting', 'signature-settings'))}
            myStyle={{
              fontSize: pixelToRem(16),
              fontWeight: 600,
              lineHeight: pixelToRem(28),
            }}
          />
          <NGText
            text={t(Localization('setting', 'different-signature-levels'))}
            myStyle={{
              fontSize: pixelToRem(14),
              fontWeight: 400,
              lineHeight: pixelToRem(24),
            }}
          />
          {/** Tab  **/}
          <NGTabs
            defaultTap={'Signature simple'}
            tapStyle={{pt: '1px'}}
            locationIcon={'end'}
            data={[
              /** Tab One  **/
              {
                active: true,
                label: 'Signature simple',
                /** Content Tab One  **/
                contain: (
                  <FirstTab
                    emailOrPhoneOrSms={emailOrPhoneOrSms}
                    setEmailOrPhoneOrSms={setEmailOrPhoneOrSms}
                    setTitle={setTitle}
                    title={title}
                    activeColor={reduxTheme.theme[0].mainColor}
                  />
                ),
              },
              /** Tab Two  **/
              {
                active: true,
                label: 'Signature avancée',
                /** Content Tab Two  **/
                contain: (
                  <SecondTab
                    title={signatureAdvance}
                    setTitle={setSignatureAdvance}
                    emailOrPhoneOrSms={emailOrPhoneOrSms}
                    setEmailOrPhoneOrSms={setEmailOrPhoneOrSms}
                  />
                ),
              },
              /** Tab Three  **/
              {
                active: false,
                label: 'Signature qualifiée',
                /** Content Tab Three  **/
                contain: <Button>In Processing (Signature qualifiée)</Button>,
                icon: <NGKeyTab sx={{bgcolor: 'inherit', pl: pixelToRem(5)}} />,
              },
            ]}
          />
        </Stack>
      </Box>
    </Box>
  );
}

export default SettingsCorporate;
