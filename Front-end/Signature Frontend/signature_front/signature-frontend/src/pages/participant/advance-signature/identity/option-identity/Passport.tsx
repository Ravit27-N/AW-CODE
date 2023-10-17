import {Localization} from '@/i18n/lan';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {setProcessAdvancedSignature} from '@/redux/slides/authentication/authenticationSlide';
import {KeyAdvanceSignatureProcessDocumentType} from '@/redux/slides/authentication/type';
import {
  useGetProjectByFlowIdQuery,
  useVerifyDocumentMutation,
} from '@/redux/slides/process-control/participant';
import {ErrorServer, IResponseServer} from '@/utils/common/HandleException';
import {
  NGIdentityCamera,
  NGIdentityDelete,
  NGIdentityExport,
  NGIdentityFaceCard,
  NGPassport,
} from '@assets/iconExport/Allicon';
import NGText from '@components/ng-text/NGText';
import {UNKOWNERROR} from '@constant/NGContant';
import {Route} from '@constant/Route';
import {
  colorBlack,
  colorDisable,
  colorPrimary,
  colorWhite,
  StyleConstant,
} from '@constant/style/StyleConstant';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {FigmaCTA} from '@constant/style/themFigma/CTA';
import {
  Backdrop,
  Button,
  CircularProgress,
  Divider,
  Stack,
} from '@mui/material';
import IdentityLayout from '@pages/participant/advance-signature/identity/IdentityLayout';
import BoxUploadAndTakePhoto from '@pages/participant/advance-signature/identity/option-identity/components/BoxUploadAndTakePhoto';
import CniBox from '@pages/participant/advance-signature/identity/option-identity/components/CNIBox';
import {
  CardUnderHeaderIdentity,
  HeaderIdentity,
} from '@pages/participant/advance-signature/identity/option-identity/components/HeaderIdentity';
import {enqueueSnackbar} from 'notistack';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams, useSearchParams} from 'react-router-dom';
import env from '../../../../../../env.config';
import {extensionAccept} from '@/utils/common/extensionAccept';

function Passport() {
  const {t} = useTranslation();
  const navigate = useNavigate();
  const {id} = useParams();
  const dispatch = useAppDispatch();
  const [searchQuery] = useSearchParams();
  const queryParameters = new URLSearchParams(window.location.search);
  const {currentData} = useGetProjectByFlowIdQuery({
    id: id + '?' + queryParameters,
  });

  const {processAdvanceSignature} = useAppSelector(
    state => state.authentication,
  );
  const [verifyDoc, {isLoading, error}] = useVerifyDocumentMutation();
  /** handler message error when upload file **/
  const [errorMessage, setErrorMessage] = React.useState<{
    uploadCard: string;
    takeCard: string;
  }>({uploadCard: '', takeCard: ''});
  /** store file when upload before pass to end point **/
  const [Img, setImg] = React.useState<{
    faceCard: File | null;
  }>({
    faceCard: processAdvanceSignature.documentFront ?? null,
  });
  const submitData = async () => {
    const formData = new FormData();

    Img.faceCard && formData.append('documentFront', Img.faceCard);
    formData.append('documentRotation', '0');
    formData.append(
      'documentType',
      KeyAdvanceSignatureProcessDocumentType.PASSPORT,
    );
    formData.append('documentCountry', 'fr');
    try {
      await verifyDoc({
        body: formData,
        flowId: id!,
        uuid: searchQuery.get('token') ?? '',
      }).unwrap();
      navigate(
        Route.participant.advance.identityDoc.cardNationalElement
          .cardNationalValidatePage + `/${id}?${queryParameters}`,
      );
      dispatch(
        setProcessAdvancedSignature({
          ...processAdvanceSignature,
          documentFront: Img.faceCard,
        }),
      );
    } catch (error) {
      navigate(
        Route.participant.advance.identityDoc.cardNationalElement
          .cardNationalValidatePage + `/${id}?${queryParameters}`,
      );
      enqueueSnackbar(ErrorServer(error as IResponseServer) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  React.useMemo(() => {
    if (currentData) {
      const {
        actor: {documentVerified},
      } = currentData;

      if (documentVerified) {
        return navigate(
          `${Route.participant.viewSignatoryFile}/${id}?${queryParameters}`,
        );
      }
    }
  }, [currentData]);

  return (
    <IdentityLayout>
      <Stack gap={'20px'}>
        {/** header **/}
        <HeaderIdentity
          Title={t(Localization('identity-page', 'upload-ur'))}
          description={`Veuillez télécharger la pièce justificative de votre pièce d'identité sélectionnée.`}
        />
        {/** card **/}
        <CardUnderHeaderIdentity
          title={t(Localization('identity-page', 'passport'))}
          button={
            <NGText
              text={t(Localization('identity-page', 'change-document'))}
              sx={{...FigmaCTA.CtaSmall, color: 'Primary.main'}}
            />
          }
          icon={
            <NGPassport sx={{color: 'white', width: '40px', height: '40px'}} />
          }
        />
        {/** line break **/}
        <Divider
          style={{
            ...StyleConstant.line.lineIdentityPage,
          }}
        />
        {/** Contents **/}
        <Stack spacing={'12px'}>
          {/** face of card **/}
          <CniBox
            headerCard={
              <Stack direction={'row'} spacing={'10px'}>
                <NGIdentityFaceCard />
                <NGText
                  text={t(Localization('identity-page', 'identity-page'))}
                  sx={{...FigmaBody.BodyMediumBold}}
                />
              </Stack>
            }
            /**  if upload success **/
            IsSuccess={Img.faceCard !== null}
            /**  file Name form file upload **/
            nameFile={Img.faceCard?.name}
            /**  modify btn Remove in card **/
            btnRemove={
              <NGText
                onClick={() => setImg({...Img, faceCard: null})}
                text={'Remplacer le fichier'}
                iconStart={
                  <NGIdentityDelete
                    sx={{
                      width: '13px',
                      height: '16px',
                      color: 'primary.main',
                      mr: '5px',
                    }}
                  />
                }
                sx={{...FigmaCTA.CtaSmall, color: 'primary.main'}}
              />
            }
            btnUpload={
              <label htmlFor="btn-upload">
                <input
                  id="btn-upload"
                  name="btn-upload"
                  style={{display: 'none'}}
                  type="file"
                  accept="."
                  onChange={e => {
                    if (e.target.files) {
                      const Size = e.target.files[0].size;
                      const Name = e.target.files[0].name;
                      if (extensionAccept(Name)) {
                        if (Size < env.VITE_MAX_FILE_SIZE_UPLOAD_ADVANCE) {
                          setImg({...Img, faceCard: e.target.files[0]});
                          setErrorMessage({
                            takeCard: '',
                            uploadCard: '',
                          });
                        } else {
                          setErrorMessage({
                            takeCard: '',
                            uploadCard: t(
                              Localization('upload-identity', 'file-size'),
                            ),
                          });
                        }
                      } else {
                        setErrorMessage({
                          takeCard: '',
                          uploadCard: t(
                            Localization('upload-identity', 'file-type'),
                          ),
                        });
                      }
                    }
                  }}
                />

                <BoxUploadAndTakePhoto
                  className="btn-choose"
                  label={t(
                    Localization('identity-page', 'upload-file-from-front-end'),
                  )}
                  icon={
                    <NGIdentityExport
                      sx={{
                        color: 'Black.main',
                        width: '16px',
                        height: '16px',
                      }}
                    />
                  }
                />
                <NGText
                  text={errorMessage.uploadCard}
                  sx={{...FigmaCTA.CtaSmall, color: 'red'}}
                />
              </label>
            }
            btnTakePhoto={
              <label htmlFor="btn-take-photo">
                <input
                  id="btn-take-photo"
                  name="btn-take-photo"
                  style={{display: 'none'}}
                  type="file"
                  accept="."
                  onChange={e => {
                    if (e.target.files) {
                      const Size = e.target.files[0].size;
                      const Name = e.target.files[0].name;
                      if (extensionAccept(Name)) {
                        if (Size < env.VITE_MAX_FILE_SIZE_UPLOAD_ADVANCE) {
                          setImg({...Img, faceCard: e.target.files[0]});
                          setErrorMessage({
                            takeCard: '',
                            uploadCard: '',
                          });
                        } else {
                          setErrorMessage({
                            takeCard: t(
                              Localization('upload-identity', 'file-size'),
                            ),
                            uploadCard: '',
                          });
                        }
                      } else {
                        setErrorMessage({
                          takeCard: t(
                            Localization('upload-identity', 'file-type'),
                          ),
                          uploadCard: '',
                        });
                      }
                    }
                  }}
                />

                <BoxUploadAndTakePhoto
                  label={t(
                    Localization('identity-page', 'take-photo-from-front-end'),
                  )}
                  icon={
                    <NGIdentityCamera
                      sx={{color: 'Black.main', width: '16px', height: '16px'}}
                    />
                  }
                />
                <NGText
                  text={errorMessage.takeCard}
                  sx={{...FigmaCTA.CtaSmall, color: 'red'}}
                />
              </label>
            }
          />
        </Stack>
        {/**
         * Button Next
         **/}
        {Img.faceCard !== null && (
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
              onClick={submitData}
              variant="contained"
              sx={{
                minHeight: 0,
                minWidth: 0,
                width: '90%',
                height: '56px',
                fontSize: '16px',
                bgcolor: colorPrimary,
                borderRadius: '6px',
                '&.MuiButton-contained': {
                  fontWeight: 600,
                  textTransform: 'capitalize',
                },
                '&.Mui-disabled': {
                  bgcolor: colorDisable,
                  color: colorWhite,
                },
                '&:hover': {
                  bgcolor: colorBlack,
                },
              }}>
              {t(Localization('form', 'next'))}
            </Button>
            {/** Loading */}
            <Backdrop
              sx={{color: '#fff', zIndex: theme => theme.zIndex.modal + 1}}
              open={isLoading}>
              <CircularProgress color="inherit" />
            </Backdrop>
          </Stack>
        )}
      </Stack>
    </IdentityLayout>
  );
}

export default Passport;
