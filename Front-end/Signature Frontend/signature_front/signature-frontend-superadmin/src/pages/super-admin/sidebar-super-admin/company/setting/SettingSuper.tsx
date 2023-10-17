import React, {useEffect} from 'react';
import NGText from '@components/ng-text/NGText';
import {Box} from '@mui/system';
import {Backdrop, CircularProgress, Stack} from '@mui/material';
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
import {ClosePage, SignatureLevelType, UNKOWNERROR} from '@/constant/NGContant';
import {Center} from '@/theme';

import SignatureLevel from '@pages/super-admin/sidebar-super-admin/company/setting/components/SignatureLevel';

import {
  IUpdateSignatureLevel,
  useAddSettingBySuperAdminMutation,
  useLazyGetSettingCorporateByAdminQuery,
} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {useParams} from 'react-router-dom';
import SecondTabSuper, {
  AdvancedFormInterface,
} from '@pages/super-admin/sidebar-super-admin/company/setting/tab/Advance';
import FirstTabSuper from '@pages/super-admin/sidebar-super-admin/company/setting/tab/Simple';
import {enqueueSnackbar} from 'notistack';
import Qualify from './tab/Qualify';

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

export interface ISignatureLevels {
  SIMPLE: SignatureLevelType.simple;
  ADVANCE: SignatureLevelType.advance;
  QUALIFY: SignatureLevelType.qualify;
  UNKNOWN?: '';
}

function SettingSuper() {
  const {t} = useTranslation();
  const {uuid} = useParams();
  /**  Signature level Simple **/
  const [formSimple, setFormSimple] = React.useState<{
    textData: string;
    remainder: 'sms' | 'email' | 'sms_email';
  }>({
    remainder: 'sms_email',
    textData:
      'Pour continuer, consultez les conditions générales d’utilisation de la plateforme.',
  });

  /**  Signature level Advance **/
  const [formAdvance, setFormAdvance] = React.useState<AdvancedFormInterface>({
    textData:
      'Pour continuer, consultez les conditions générales d’utilisation de la plateforme.',
    identity:
      'En continuant, vous acceptez de partager vos pièces justificatives.',
    supportingDoc:
      'Importez votre pièce d’identité afin de prouver votre identité.',
    typeDocument: ['JPG'],
    remainder: 'sms_email',
  });

  /**  Signature level Qualify **/
  const [formQualify, setFormQualify] = React.useState<AdvancedFormInterface>({
    textData:
      'Pour continuer, consultez les conditions générales d’utilisation de la plateforme.',
    identity:
      'En continuant, vous acceptez de partager vos pièces justificatives.',
    supportingDoc:
      'Importez votre pièce d’identité afin de prouver votre identité.',
    typeDocument: ['JPG'],
    remainder: 'sms_email',
  });
  const [checked, setChecked] = React.useState<Array<keyof ISignatureLevels>>([
    'UNKNOWN',
  ]);
  const [defaultTab, setDefaultTab] = React.useState<
    'Signature simple' | 'Signature avancée' | 'Signature qualifiée'
  >('Signature simple');
  /** function for trigger get template or service **/
  const [trigger, result] = useLazyGetSettingCorporateByAdminQuery();
  const [addSettingByAdmin] = useAddSettingBySuperAdminMutation();
  const [loading, setLoading] = React.useState(false);

  useEffect(() => {
    setChecked([]);
    if (result.isSuccess) {
      result.currentData <= 0 &&
        enqueueSnackbar('No properties! Company must have settings!', {
          variant: 'errorSnackbar',
        });
      result.currentData.map((item: any) => {
        if (item.signatureLevel === 'SIMPLE') {
          if (!checked.includes('SIMPLE')) {
            setChecked(prevState => [...prevState, item.signatureLevel]);
          }
          setFormSimple({
            ...formSimple,
            textData: item.personalTerms ?? formSimple.textData,
            remainder: item.channelReminder,
          });
        }
        if (item.signatureLevel === 'ADVANCE') {
          if (!checked.includes('ADVANCE')) {
            setChecked(prevState => [...prevState, item.signatureLevel]);
          }
          setFormAdvance({
            ...formAdvance,
            textData: item.personalTerms,
            remainder: item.channelReminder,
            typeDocument: item.fileType,
            supportingDoc: item.documentTerms,
            identity: item.identityTerms,
          });
        }
        if (item.signatureLevel === 'QUALIFY') {
          if (!checked.includes('QUALIFY')) {
            setChecked(prevState => [...prevState, item.signatureLevel]);
          }
          setFormQualify({
            ...formQualify,
            textData: item.personalTerms,
            remainder: item.channelReminder,
            typeDocument: item.fileType,
            supportingDoc: item.documentTerms,
            identity: item.identityTerms,
          });
        }
      });
    }
  }, [result.isSuccess]);
  const [preDataSubmit, setPreDataSubmit] = React.useState<any[]>([]);
  const onSubmitChangeSetting = async () => {
    setPreDataSubmit([]);
    const preData: {
      [k in keyof ISignatureLevels]: k extends 'SIMPLE'
        ? IUpdateSignatureLevel['SIMPLE']
        : k extends 'ADVANCE'
        ? IUpdateSignatureLevel['ADVANCE']
        : IUpdateSignatureLevel['QUALIFIED'];
    } = {
      SIMPLE: {
        channelReminder: formSimple.remainder,
        companyUuid: uuid!,
        personalTerms: formSimple.textData,
        signatureLevel: SignatureLevelType.simple,
      },
      ADVANCE: {
        identityTerms: formAdvance.identity,
        documentTerms: formAdvance.supportingDoc,
        channelReminder: formAdvance.remainder,
        companyUuid: uuid!,
        personalTerms: formAdvance.textData,
        signatureLevel: SignatureLevelType.advance,
        fileType: formAdvance.typeDocument,
      },
      QUALIFY: {
        identityTerms: formQualify.identity,
        documentTerms: formQualify.supportingDoc,
        channelReminder: formQualify.remainder,
        companyUuid: uuid!,
        personalTerms: formQualify.textData,
        signatureLevel: SignatureLevelType.qualify,
        fileType: formQualify.typeDocument,
      },
    };
    checked.forEach(item => {
      preDataSubmit.push(preData[item]);
    });
    setLoading(true);
    try {
      const res = await addSettingByAdmin(preDataSubmit).unwrap();
      enqueueSnackbar(
        t(Localization('corporate-form', 'your-changes-have-been-saved')),
        {
          variant: 'successSnackbar',
        },
      );
      if (res) {
        setLoading(false);
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      }
    } catch (error: any) {
      enqueueSnackbar(error ? error.message : UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };
  const handleFetch = async (companyId: string) => {
    await trigger({
      uuid: companyId,
    }).unwrap();
  };
  React.useEffect(() => {
    handleFetch(uuid!).then(r => r);
  }, [uuid]);
  React.useEffect(() => {
    if (checked[0] === 'SIMPLE') setDefaultTab('Signature simple');
    else if (checked[0] === 'ADVANCE') setDefaultTab('Signature avancée');
    else if (checked[0] === 'QUALIFY') setDefaultTab('Signature qualifiée');
  }, [checked]);
  if (ClosePage) {
    return (
      <Center sx={{height: '100vh'}}>
        <NGText text={'Parameter'} />
      </Center>
    );
  }

  return (
    <Box width={'100%'} height={'100%'}>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={loading}>
        <CircularProgress color="inherit" />
      </Backdrop>
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
          onClick={onSubmitChangeSetting}
          disabled={loading}
          title={
            <NGText
              text={t(Localization('enterprise-brand', 'save-change'))}
              myStyle={{color: 'white', fontSize: 12}}
            />
          }
          myStyle={{
            bgcolor: 'primary.main',
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
          {defaultTab && (
            <NGTabs
              defaultTap={defaultTab}
              tapStyle={{pt: '1px'}}
              locationIcon={'end'}
              data={[
                /** Tab One  **/
                {
                  active: checked.includes(SignatureLevelType.simple),
                  label: 'Signature simple',
                  /** Content Tab One  **/
                  contain: (
                    <FirstTabSuper
                      setFormSimple={setFormSimple}
                      formSimple={formSimple}
                    />
                  ),

                  icon: checked.includes(SignatureLevelType.simple) ? (
                    <></>
                  ) : (
                    <NGKeyTab sx={{bgcolor: 'inherit', pl: pixelToRem(5)}} />
                  ),
                },
                /** Tab Two  **/
                {
                  active: checked.includes(SignatureLevelType.advance),
                  label: 'Signature avancée',
                  /** Content Tab Two  **/
                  contain: (
                    <SecondTabSuper
                      formAdvance={formAdvance}
                      setFormAdvance={setFormAdvance}
                    />
                  ),
                  icon: checked.includes(SignatureLevelType.advance) ? (
                    <></>
                  ) : (
                    <NGKeyTab sx={{bgcolor: 'inherit', pl: pixelToRem(5)}} />
                  ),
                },
                /** Tab Three  **/
                {
                  active: checked.includes(SignatureLevelType.qualify),
                  label: 'Signature qualifiée',
                  /** Content Tab Three  **/
                  contain: (
                    <Qualify
                      formAdvance={formQualify}
                      setFormAdvance={setFormQualify}
                    />
                  ),
                  icon: checked.includes(SignatureLevelType.qualify) ? (
                    <></>
                  ) : (
                    <NGKeyTab sx={{bgcolor: 'inherit', pl: pixelToRem(5)}} />
                  ),
                },
              ]}
            />
          )}
        </Stack>
      </Box>
    </Box>
  );
}

export default SettingSuper;
