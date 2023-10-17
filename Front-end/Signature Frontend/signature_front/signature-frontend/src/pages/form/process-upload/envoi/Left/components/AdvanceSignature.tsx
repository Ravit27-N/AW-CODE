import React from 'react';
import NGText from '@components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {FigmaBody} from '@constant/style/themFigma/Body';
import TypeDocument from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/components/TypeDocument';
import {Divider, Stack} from '@mui/material';
import {NGSetting} from '@assets/iconExport/ExportIcon';
import {
  StyleSecondText,
  Text_Input,
} from '@pages/form/process-upload/envoi/function';
import {useTranslation} from 'react-i18next';
import NGInput from '@components/ng-inputField/NGInput';
import {useAppSelector} from '@/redux/config/hooks';
import {useDispatch} from 'react-redux';
import {setSignatureLevels} from '@/redux/slides/authentication/authenticationSlide';

function AdvanceSignature() {
  const {t} = useTranslation();
  /** select redux slide **/
  const {signatureLevels} = useAppSelector(state => state.authentication);
  /** setting advance signature **/
  const [DocType, setCheck] = React.useState<any>();
  const [PersonData, setPersonData] = React.useState<string>(
    signatureLevels.personalTerms ?? 'Personal Terms',
  );
  const [Identity, setIdentity] = React.useState<string>(
    signatureLevels.identityTerms ?? 'IDENTITY',
  );
  const [DocumentsSupport, setDocumentsSupport] = React.useState<string>(
    signatureLevels.documentTerms ?? 'SUPPORT',
  );

  /** dispatch **/
  const dispatch = useDispatch();
  React.useEffect(() => {
    dispatch(
      setSignatureLevels({
        ...signatureLevels,
        identityTerms: Identity,
        personalTerms: PersonData,
        documentTerms: DocumentsSupport,
        fileTypeSelected: DocType,
      }),
    );
  }, [PersonData, Identity, DocumentsSupport, DocType]);

  return (
    <Stack spacing={2}>
      <Divider sx={{width: '100%', mt: 2}} />
      <NGText
        text={t(Localization('setting', 'advance-signature-setting'))}
        myStyle={{
          fontWeight: 600,
          fontSize: 18,
          ml: '5px',
        }}
        iconStart={<NGSetting sx={{color: 'Primary.main'}} />}
      />
      <Divider sx={{width: '100%', mt: 2}} />
      {/** Personal Data **/}
      <Text_Input
        text={Localization('setting', 'text-personal-data')}
        input={
          <NGInput
            colorOnfocus={'info'}
            autoFocus={true}
            textLabel={
              <NGText
                text={t(
                  Localization('setting', 'semi-signatory-accesses-platform'),
                )}
                myStyle={{
                  ...StyleSecondText,
                }}
                color={'grey'}
              />
            }
            type={'text'}
            placeholder={'Ajoutez un message personnalisé'}
            nameId={'optional'}
            setValue={setPersonData}
            value={PersonData}
          />
        }
      />
      {/** Identity **/}
      <Text_Input
        text={Localization('setting', 'text-proof-identity')}
        input={
          <NGInput
            colorOnfocus={'info'}
            autoFocus={true}
            textLabel={
              <NGText
                text={t(Localization('setting', 'semi-import-voucher'))}
                myStyle={{
                  ...StyleSecondText,
                }}
                color={'grey'}
              />
            }
            type={'text'}
            placeholder={'Ajoutez un message personnalisé'}
            nameId={'optional'}
            setValue={setIdentity}
            value={Identity}
          />
        }
      />
      {/** Supporting Documents **/}
      <Text_Input
        text={Localization('setting', 'text-supporting-documents')}
        input={
          <NGInput
            colorOnfocus={'info'}
            autoFocus={true}
            textLabel={
              <NGText
                text={t(Localization('setting', 'semi-import-document'))}
                myStyle={{
                  ...StyleSecondText,
                }}
                color={'grey'}
              />
            }
            type={'text'}
            placeholder={'Ajoutez un message personnalisé'}
            nameId={'optional'}
            setValue={setDocumentsSupport}
            value={DocumentsSupport}
          />
        }
      />
      {/** Document Types  **/}
      <Stack spacing={'20px'} mx={2}>
        <NGText
          text={t(Localization('setting', 'allow-file-type'))}
          myStyle={{
            ...FigmaBody.BodyMediumBold,
          }}
        />
        {signatureLevels &&
          signatureLevels.fileType &&
          signatureLevels.fileType.length > 0 && (
            <TypeDocument
              setCheck={setCheck}
              data={signatureLevels.fileType.map(item => {
                return {id: item, title: item};
              })}
            />
          )}
      </Stack>
    </Stack>
  );
}

export default AdvanceSignature;
