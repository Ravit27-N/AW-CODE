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
import FirstTab from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/tab/FirstTab';
import {useAppSelector} from '@/redux/config/hooks';
import {ClosePage} from '@/constant/NGContant';
import {Center} from '@/theme';
import SecondTab from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/tab/SecondTab';
import FirstTabSuper from '@pages/super-admin/sidebar-super-admin/company/setting/tab/FirstTab';
import SignatureLevel from '@pages/super-admin/sidebar-super-admin/company/setting/components/SignatureLevel';
import SecondTabSuper from '@pages/super-admin/sidebar-super-admin/company/setting/tab/SecondTab';

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
          fontSize: pixelToRem(14),
          fontWeight: 600,
          lineHeight: pixelToRem(24),
        }}
      />
      <NGText
        text={semiTitle}
        myStyle={{
          fontSize: pixelToRem(12),
          fontWeight: 500,
          lineHeight: pixelToRem(16),
        }}
      />
    </Stack>
  );
};

function SettingSuper() {
  const {t} = useTranslation();
  const reduxTheme = useAppSelector(state => state.enterprise);
  const [title, setTitle] = React.useState(
    'Pour continuer, consultez les conditions générales d’utilisation de la plateforme.',
  );
  const [checked, setChecked] = React.useState<string[]>(['0']);
  const [emailOrPhoneOrSms, setEmailOrPhoneOrSms] = React.useState<
    '1' | '2' | '3'
  >('1');
  if (ClosePage) {
    return (
      <Center sx={{height: '100vh'}}>
        <NGText text={'Parameter'} />
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
        <NGText text={'Paramètres'} myStyle={{fontSize: 16, fontWeight: 600}} />
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
      <SignatureLevel checked={checked} setChecked={setChecked} />
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
            text={'Paramètres de signature'}
            myStyle={{
              fontSize: pixelToRem(16),
              fontWeight: 600,
              lineHeight: pixelToRem(28),
            }}
          />
          <NGText
            text={
              'Définissez les paramètres pour vos différents niveaux de signature.'
            }
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
                active: checked.includes('0'),
                label: 'Signature simple',
                /** Content Tab One  **/
                contain: (
                  <FirstTabSuper
                    emailOrPhoneOrSms={emailOrPhoneOrSms}
                    setEmailOrPhoneOrSms={setEmailOrPhoneOrSms}
                    setTitle={setTitle}
                    title={title}
                    activeColor={reduxTheme.theme[0].mainColor}
                  />
                ),
                icon: checked.includes('0') ? (
                  <></>
                ) : (
                  <NGKeyTab sx={{bgcolor: 'inherit', pl: pixelToRem(5)}} />
                ),
              },
              /** Tab Two  **/
              {
                active: checked.includes('1'),
                label: 'Signature avancée',
                /** Content Tab Two  **/
                contain: (
                  <SecondTabSuper
                    title={title}
                    setTitle={setTitle}
                    emailOrPhoneOrSms={emailOrPhoneOrSms}
                    setEmailOrPhoneOrSms={setEmailOrPhoneOrSms}
                  />
                ),
                icon: checked.includes('1') ? (
                  <></>
                ) : (
                  <NGKeyTab sx={{bgcolor: 'inherit', pl: pixelToRem(5)}} />
                ),
              },
              /** Tab Three  **/
              {
                active: checked.includes('2'),
                label: 'Signature qualifiée',
                /** Content Tab Three  **/
                contain: <Button>In Processing (Signature qualifiée)</Button>,
                icon: checked.includes('2') ? (
                  <></>
                ) : (
                  <NGKeyTab sx={{bgcolor: 'inherit', pl: pixelToRem(5)}} />
                ),
              },
            ]}
          />
        </Stack>
      </Box>
    </Box>
  );
}

export default SettingSuper;
