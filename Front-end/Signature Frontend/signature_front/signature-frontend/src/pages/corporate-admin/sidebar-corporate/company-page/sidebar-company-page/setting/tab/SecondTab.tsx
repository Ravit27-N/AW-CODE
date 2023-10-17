import {Divider, Grid, Stack} from '@mui/material';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGInput from '@components/ng-inputField/NGInput';
import {Box_Input} from '@pages/form/process-upload/envoi/function';
import {NGMessagePlus, NGMessages, NGPhone} from '@assets/iconExport/Allicon';
import {TitleTabInput} from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/Settings';
import {ClosePage} from '@/constant/NGContant';
import {Center} from '@/theme';
import NGText from '@components/ng-text/NGText';
import {StyleConstant} from '@constant/style/StyleConstant';
import {useTranslation} from 'react-i18next';
import {Localization} from '@/i18n/lan';
import TypeDocument from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/components/TypeDocument';
import React from 'react';
import {FigmaBody} from '@constant/style/themFigma/Body';

function SecondTab({
  title,
  setTitle,
  activeColor,
  emailOrPhoneOrSms,
  setEmailOrPhoneOrSms,
}: {
  title: {title1: string; title2: string; title3: string};
  setTitle: React.Dispatch<
    React.SetStateAction<{title1: string; title2: string; title3: string}>
  >;
  activeColor?: string | null;
  emailOrPhoneOrSms: string;
  setEmailOrPhoneOrSms: any;
}) {
  const {t} = useTranslation();
  /** type of documents **/
  const [, setCheck] = React.useState<any>();
  if (ClosePage) {
    return (
      <Center sx={{height: '100vh'}}>
        <NGText text={'Simple sign'} />
      </Center>
    );
  }
  return (
    <Stack
      padding={'40px'}
      spacing={'35px'}
      sx={{
        overflow: 'hidden',
        height: `calc(100vh - (56px +  234px))`,
        overflowY: 'scroll',
        ...StyleConstant.scrollNormal,
      }}>
      {/** INPUT 1 **/}
      <Stack spacing={'10px'}>
        <TitleTabInput
          title={t(Localization('setting', 'text-personal-data'))}
          semiTitle={t(
            Localization('setting', 'semi-signatory-accesses-platform'),
          )}
        />

        <NGInput
          propsInput={{width: '612px'}}
          setValue={(e: any) => setTitle({...title, title1: e.target.value})}
          value={title.title1}
          nameId={'PP'}
          placeholder={''}
          rows={4}
        />
      </Stack>
      {/** INPUT 2 **/}
      <Stack spacing={'10px'}>
        <TitleTabInput
          title={t(Localization('setting', 'text-proof-identity'))}
          semiTitle={t(Localization('setting', 'semi-import-voucher'))}
        />

        <NGInput
          propsInput={{width: '612px'}}
          setValue={(e: any) => setTitle({...title, title2: e.target.value})}
          value={title.title2}
          nameId={'PP'}
          placeholder={''}
          rows={4}
        />
      </Stack>
      <Divider />
      {/** INPUT 3 **/}
      <Stack spacing={'10px'}>
        <TitleTabInput
          title={t(Localization('setting', 'text-supporting-documents'))}
          semiTitle={t(Localization('setting', 'semi-import-document'))}
        />

        <NGInput
          propsInput={{width: '612px'}}
          setValue={(e: any) => setTitle({...title, title3: e.target.value})}
          value={title.title3}
          nameId={'PP'}
          placeholder={''}
          rows={4}
        />
      </Stack>
      {/** type of documents **/}
      <Stack spacing={'20px'}>
        {/*<TitleTabInput*/}
        {/*  title={t(Localization('setting', 'allow-file-type'))}*/}
        {/*  semiTitle={t(Localization('setting', 'relaunch-participants'))}*/}
        {/*/>*/}
        <NGText
          text={t(Localization('setting', 'allow-file-type'))}
          myStyle={{
            ...FigmaBody.BodyMediumBold,
          }}
        />
        <TypeDocument setCheck={setCheck} />
      </Stack>
      <Divider />
      {/** type of remainder **/}
      <Stack spacing={pixelToRem(20)}>
        <TitleTabInput
          title={t(Localization('setting', 'channel-glance'))}
          semiTitle={t(Localization('setting', 'follow-up-channel'))}
        />

        <Grid container width={'296px'} spacing={1}>
          <Grid item lg={12} minWidth={pixelToRem(296)}>
            <Box_Input
              borderColorBox={activeColor ?? undefined}
              radioColor={activeColor ?? undefined}
              padding={2}
              icon={
                <NGMessagePlus
                  sx={{fontSize: '25px', color: 'blue.dark', ml: '5px'}}
                />
              }
              title={'E-mail & SMS'}
              checked={emailOrPhoneOrSms === '3'}
              onClick={() => {
                setEmailOrPhoneOrSms('3');
              }}
            />
          </Grid>
          <Grid item lg={12} minWidth={pixelToRem(296)}>
            <Box_Input
              borderColorBox={activeColor ?? undefined}
              radioColor={activeColor ?? undefined}
              padding={2}
              icon={<NGMessages sx={{fontSize: '20px', color: 'blue.dark'}} />}
              title={'E-mail seulement'}
              checked={emailOrPhoneOrSms === '2'}
              onClick={() => {
                setEmailOrPhoneOrSms('2');
              }}
            />
          </Grid>
          <Grid item lg={12} minWidth={pixelToRem(296)}>
            <Box_Input
              borderColorBox={activeColor ?? undefined}
              radioColor={activeColor ?? undefined}
              padding={2}
              icon={<NGPhone sx={{fontSize: '25px', color: 'blue.dark'}} />}
              title={'SMS seulement'}
              checked={emailOrPhoneOrSms === '1'}
              onClick={() => {
                setEmailOrPhoneOrSms('1');
              }}
            />
          </Grid>
        </Grid>
      </Stack>
    </Stack>
  );
}

export default SecondTab;
