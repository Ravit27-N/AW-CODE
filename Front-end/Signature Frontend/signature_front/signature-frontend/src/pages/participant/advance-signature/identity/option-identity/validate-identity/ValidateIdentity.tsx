import {Localization} from '@/i18n/lan';
import {NGArrowLeft} from '@assets/iconExport/Allicon';
import refuse from '@assets/image/refuse.png';
import valid from '@assets/image/valid.png';
import NGText from '@components/ng-text/NGText';
import {Route} from '@constant/Route';
import {
  colorBlack,
  colorDisable,
  colorPrimary,
  colorWhite,
} from '@constant/style/StyleConstant';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {FigmaCTA} from '@constant/style/themFigma/CTA';
import {FigmaHeading} from '@constant/style/themFigma/FigmaHeading';
import {Button, Stack} from '@mui/material';
import IdentityLayout from '@pages/participant/advance-signature/identity/IdentityLayout';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams} from 'react-router-dom';
import {IGetFlowId} from '@/redux/slides/project-management/project';

export type IFetch = {
  data: IGetFlowId | undefined;
  isLoading: boolean;
  isFetching: boolean;
};

function ValidateIdentity() {
  const navigate = useNavigate();
  const {id} = useParams();
  const queryParameters = new URLSearchParams(window.location.search);
  const [fetch, setData] = React.useState<IFetch | null>(null);

  const {t} = useTranslation();

  return (
    <IdentityLayout setData={setData}>
      {fetch?.isLoading || fetch?.isFetching || !fetch?.data ? (
        <>Loading ... </>
      ) : (
        <>
          <Stack gap={'30px'}>
            {!fetch.data.actor.documentVerified ? (
              <Stack
                direction={'row'}
                sx={{
                  cursor: 'pointer',
                  alignItems: 'center',
                }}
                onClick={() => {
                  return history.go(-1);
                }}>
                <NGArrowLeft sx={{mr: 1, mt: '-3px', color: 'Primary.main'}} />
                <NGText
                  sx={{
                    ...FigmaCTA.CtaMedium,
                  }}
                  text={t(Localization('identity-page', 'back-to-document'))}
                />
              </Stack>
            ) : (
              <Stack></Stack>
            )}

            {/** title **/}
            <Stack
              gap={'10px'}
              width={'100%'}
              alignItems={'center'}
              mb={'10px'}>
              <NGText
                text={t(Localization('identity-page', 'identity-validated'))}
                sx={{
                  ...FigmaHeading.H3,
                  width: '100%',
                  textAlign: 'center',
                }}
              />
              <NGText
                sx={{
                  ...FigmaBody.BodyMedium,
                  width: '250px',
                  textAlign: 'center',
                }}
                text={t(Localization('identity-page', 'ur-identity-success'))}
              />
            </Stack>
            {/** Img **/}

            <Stack width={'100%'} alignItems={'center'}>
              {fetch.data.actor.documentVerified ? (
                <img src={valid} width={'400px'} />
              ) : (
                <img src={refuse} width={'400px'} />
              )}
            </Stack>
          </Stack>
          {fetch.data.actor.documentVerified && (
            <Stack
              position={'absolute'}
              left={0}
              bottom={0}
              width={'100%'}
              bgcolor={'White.main'}
              pt={'20px'}
              pb={'10px'}
              alignItems={'center'}>
              <Button
                onClick={() => {
                  fetch.data!.actor.documentVerified
                    ? navigate(
                        Route.participant.viewSignatoryFile +
                          `/${id}?${queryParameters}`,
                      )
                    : history.go(-1);
                }}
                variant={
                  fetch.data.actor.documentVerified ? 'contained' : 'outlined'
                }
                sx={{
                  minHeight: 0,
                  minWidth: 0,
                  width: '90%',
                  height: '56px',
                  fontSize: '16px',
                  bgcolor: fetch.data.actor.documentVerified
                    ? colorPrimary
                    : 'white',
                  borderRadius: '6px',
                  '&.MuiButton-contained': {
                    fontWeight: 600,
                    textTransform: 'inherit',
                  },
                  '&.Mui-disabled': {
                    bgcolor: colorDisable,
                    color: colorWhite,
                  },
                  '&:hover': {
                    bgcolor: colorBlack,
                  },
                }}>
                <NGText
                  text={t(
                    Localization('end-user-assigned-project', 'sign-the-doc'),
                  )}
                  sx={{
                    ...FigmaCTA.CtaLarge,
                    color: fetch.data.actor.documentVerified
                      ? 'white'
                      : 'Black.main',
                  }}
                />
              </Button>
            </Stack>
          )}
        </>
      )}
    </IdentityLayout>
  );
}

export default ValidateIdentity;
