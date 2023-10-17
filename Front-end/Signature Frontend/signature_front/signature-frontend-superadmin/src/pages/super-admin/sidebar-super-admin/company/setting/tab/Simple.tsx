import {Grid, Stack} from '@mui/material';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGInput from '@components/ng-inputField/NGInput';
import {NGMessagePlus, NGMessages, NGPhone} from '@assets/iconExport/Allicon';
import {ClosePage} from '@/constant/NGContant';
import {Center} from '@/theme';
import NGText from '@components/ng-text/NGText';
import {StyleConstant} from '@constant/style/StyleConstant';
import {Box_Input} from '@components/ng-box-input/NGBoxTextInput';
import {TitleTabInput} from '@pages/super-admin/sidebar-super-admin/company/setting/SettingSuper';
import {useTheme} from '@mui/material/styles';
import React from 'react';

function FirstTabSuper({
  formSimple,
  setFormSimple,
}: {
  formSimple: {textData: string; remainder: 'sms' | 'email' | 'sms_email'};
  setFormSimple: React.Dispatch<
    React.SetStateAction<{
      textData: string;
      remainder: 'sms' | 'email' | 'sms_email';
    }>
  >;
}) {
  const theme = useTheme();
  if (ClosePage) {
    return (
      <Center sx={{height: '100vh'}}>
        <NGText text={'Simple sign'} />
      </Center>
    );
  }
  return (
    <Stack
      py={'40px'}
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
          setValue={(e: string) => setFormSimple({...formSimple, textData: e})}
          value={formSimple.textData}
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
              borderColorBox={theme.palette.primary.main ?? undefined}
              radioColor={theme.palette.primary.main ?? undefined}
              padding={2}
              icon={
                <NGMessagePlus sx={{fontSize: '25px', color: 'blue.dark',ml:'5px'}} />
              }
              title={'E-mail & SMS'}
              checked={formSimple.remainder === 'sms_email'}
              onClick={() => {
                setFormSimple({...formSimple, remainder: 'sms_email'});
              }}
            />
          </Grid>
          <Grid item lg={12} minWidth={pixelToRem(296)}>
            <Box_Input
              borderColorBox={theme.palette.primary.main ?? undefined}
              radioColor={theme.palette.primary.main ?? undefined}
              padding={2}
              icon={<NGMessages sx={{fontSize: '20px', color: 'blue.dark'}} />}
              title={'E-mail seulement'}
              checked={formSimple.remainder === 'email'}
              onClick={() => {
                setFormSimple({...formSimple, remainder: 'email'});
              }}
            />
          </Grid>
          <Grid item lg={12} minWidth={pixelToRem(296)}>
            <Box_Input
              borderColorBox={theme.palette.primary.main ?? undefined}
              radioColor={theme.palette.primary.main ?? undefined}
              padding={2}
              icon={<NGPhone sx={{fontSize: '25px', color: 'blue.dark'}} />}
              title={'SMS seulement'}
              checked={formSimple.remainder === 'sms'}
              onClick={() => {
                setFormSimple({...formSimple, remainder: 'sms'});
              }}
            />
          </Grid>
        </Grid>
      </Stack>
    </Stack>
  );
}

export default FirstTabSuper;
