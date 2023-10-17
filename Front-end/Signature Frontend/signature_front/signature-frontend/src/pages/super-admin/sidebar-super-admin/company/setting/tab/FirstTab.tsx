import {Grid, Stack} from '@mui/material';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGInput from '@components/ng-inputField/NGInput';
import {Box_Input} from '@pages/form/process-upload/envoi/function';
import {NGMessagePlus, NGMessages, NGPhone} from '@assets/iconExport/Allicon';
import EmailOutlinedIcon from '@mui/icons-material/EmailOutlined';
import SendToMobileOutlinedIcon from '@mui/icons-material/SendToMobileOutlined';
import {TitleTabInput} from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/Settings';
import {ClosePage} from '@/constant/NGContant';
import {Center} from '@/theme';
import NGText from '@components/ng-text/NGText';
import {StyleConstant} from '@constant/style/StyleConstant';

function FirstTabSuper({
  title,
  setTitle,
  activeColor,
  emailOrPhoneOrSms,
  setEmailOrPhoneOrSms,
}: {
  title: string;
  setTitle: any;
  activeColor?: string | null;
  emailOrPhoneOrSms: string;
  setEmailOrPhoneOrSms: any;
}) {
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
        height: `calc(100vh - (56px + 228px + 204px))`,
        overflowY: 'scroll',
        ...StyleConstant.scrollNormal,
      }}>
      <Stack spacing={'10px'}>
        <TitleTabInput
          title={'Données personnelles'}
          semiTitle={
            'Ce message apparaîtra au moment où le signataire accède à la plateforme.'
          }
        />

        <NGInput
          propsInput={{width: '612px'}}
          setValue={setTitle}
          value={title}
          nameId={'PP'}
          placeholder={''}
          rows={4}
        />
      </Stack>
      <Stack spacing={pixelToRem(20)}>
        <TitleTabInput
          title={'Canal de relance'}
          semiTitle={
            'Le canal de relance correspond au moyen utilisé pour relancer manuellement les participants.'
          }
        />

        <Grid container width={'296px'} spacing={1}>
          <Grid item lg={12} minWidth={pixelToRem(296)}>
            <Box_Input
              borderColorBox={activeColor ?? undefined}
              radioColor={activeColor ?? undefined}
              padding={2}
              icon={
                <NGMessagePlus sx={{fontSize: '25px', color: 'blue.dark'}} />
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

export default FirstTabSuper;
