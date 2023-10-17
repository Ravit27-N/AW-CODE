import {ClosePage} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {Center} from '@/theme';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NGMessagePlus, NGMessages, NGPhone} from '@assets/iconExport/Allicon';
import {Box_Input} from '@components/ng-box-input/NGBoxTextInput';
import NGInput from '@components/ng-inputField/NGInput';
import NGText from '@components/ng-text/NGText';
import {StyleConstant} from '@constant/style/StyleConstant';
import {Grid, Stack} from '@mui/material';
import {useTheme} from '@mui/material/styles';
import {TitleTabInput} from '@pages/super-admin/sidebar-super-admin/company/setting/SettingSuper';
import TypeDocument from '@pages/super-admin/sidebar-super-admin/company/setting/components/TypeDocument';
import React, {useEffect} from 'react';
import {useTranslation} from 'react-i18next';

export interface AdvancedFormInterface {
  textData: string;
  identity: string;
  supportingDoc: string;
  typeDocument: ['JPG' | 'PNG' | 'PDF'];
  remainder: 'sms' | 'email' | 'sms_email';
}

function Qualify({
  setFormAdvance,
  formAdvance,
}: {
  formAdvance: AdvancedFormInterface;
  setFormAdvance: React.Dispatch<React.SetStateAction<AdvancedFormInterface>>;
}) {
  const {t} = useTranslation();
  const theme = useTheme();
  const [checked, setChecked] = React.useState<['JPG' | 'PNG' | 'PDF']>([
    'JPG',
  ]);
  useEffect(() => {
    setChecked(formAdvance.typeDocument);
  }, []);

  useEffect(() => {
    setFormAdvance({...formAdvance, typeDocument: checked});
  }, [checked]);
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
          setValue={(e: string) =>
            setFormAdvance({...formAdvance, textData: e})
          }
          value={formAdvance.textData}
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
          setValue={(e: string) =>
            setFormAdvance({...formAdvance, identity: e})
          }
          value={formAdvance.identity}
          nameId={'PP'}
          placeholder={''}
          rows={4}
        />
      </Stack>
      {/** INPUT 3 **/}
      <Stack spacing={'10px'}>
        <TitleTabInput
          title={t(Localization('setting', 'text-supporting-documents'))}
          semiTitle={t(Localization('setting', 'semi-import-document'))}
        />

        <NGInput
          propsInput={{width: '612px'}}
          setValue={(e: string) =>
            setFormAdvance({...formAdvance, supportingDoc: e})
          }
          value={formAdvance.supportingDoc}
          nameId={'PP'}
          placeholder={''}
          rows={4}
        />
      </Stack>
      {/** type of documents **/}
      <TitleTabInput
        title={t(Localization('setting', 'allow-file-type'))}
        semiTitle={t(Localization('setting', 'relaunch-participants'))}
      />
      <TypeDocument setChecked={setChecked} checked={checked} />

      {/** type of remainder **/}
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
                <NGMessagePlus sx={{fontSize: '25px', color: 'blue.dark'}} />
              }
              title={'E-mail & SMS'}
              checked={formAdvance.remainder === 'sms_email'}
              onClick={() => {
                setFormAdvance({...formAdvance, remainder: 'sms_email'});
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
              checked={formAdvance.remainder === 'email'}
              onClick={() => {
                setFormAdvance({...formAdvance, remainder: 'email'});
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
              checked={formAdvance.remainder === 'sms'}
              onClick={() => {
                setFormAdvance({...formAdvance, remainder: 'sms'});
              }}
            />
          </Grid>
        </Grid>
      </Stack>
    </Stack>
  );
}

export default Qualify;
