import NGText from '@components/ng-text/NGText';
import {
  Backdrop,
  Button,
  CircularProgress,
  Divider,
  Stack,
} from '@mui/material';
import React from 'react';

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
} from '@assets/iconExport/ExportIcon';
import {
  TypeIdentityTakePhoto,
  TypeIdentityUpload,
  UNKOWNERROR,
} from '@constant/NGContant';
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
import IdentityLayout from '@pages/participant/advance-signature/identity/IdentityLayout';
import BoxUploadAndTakePhoto from '@pages/participant/advance-signature/identity/option-identity/components/BoxUploadAndTakePhoto';
import CniBox from '@pages/participant/advance-signature/identity/option-identity/components/CNIBox';
import {
  CardUnderHeaderIdentity,
  HeaderIdentity,
} from '@pages/participant/advance-signature/identity/option-identity/components/HeaderIdentity';
import {enqueueSnackbar} from 'notistack';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams, useSearchParams} from 'react-router-dom';
import env from '../../../../../../env.config';
import {extensionAccept} from '@/utils/common/extensionAccept';

function NationalCard() {
  const {t} = useTranslation();
  const navigate = useNavigate();
  const {id} = useParams();
  const dispatch = useAppDispatch();
  const [searchQuery] = useSearchParams();
  const queryParameters = new URLSearchParams(window.location.search);
  const {currentData, isSuccess} = useGetProjectByFlowIdQuery({
    id: id + '?' + queryParameters,
  });

  const {processAdvanceSignature} = useAppSelector(
    state => state.authentication,
  );
  const [verifyDoc, {isLoading}] = useVerifyDocumentMutation();

  /** handler message error when upload file or take the photo **/
  const [errorMessage, setErrorMessage] = React.useState<{
    faceCard: {upload: string; take: string};
    backCard: {upload: string; take: string};
  }>({faceCard: {upload: '', take: ''}, backCard: {upload: '', take: ''}});
  /** store file when upload before pass to back end **/
  const [Img, setImg] = React.useState<{
    faceCard: File | null;
    backCard: File | null;
  }>({
    faceCard: processAdvanceSignature.documentFront ?? null,
    backCard: processAdvanceSignature.documentBack ?? null,
  });
  const submitData = async () => {
    const formData = new FormData();
    Img.backCard && formData.append('documentBack', Img.backCard);
    Img.faceCard && formData.append('documentFront', Img.faceCard);
    formData.append('documentRotation', '0');
    formData.append('documentType', KeyAdvanceSignatureProcessDocumentType.CNI);
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
          documentBack: Img.backCard,
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
      <Stack
        gap={'20px'}
        sx={{
          overflow: 'hidden',
          overflowY: 'scroll',
        }}>
        {/** header **/}
        <HeaderIdentity
          Title={t(Localization('identity-page', 'upload-ur'))}
          description={t(Localization('identity-page', 'upload-both'))}
        />
        {/** card **/}
        <CardUnderHeaderIdentity
          title={t(Localization('identity-page', 'card-national'))}
          button={
            <NGText
              text={t(Localization('identity-page', 'change-document'))}
              sx={{...FigmaCTA.CtaSmall, color: 'Primary.main'}}
            />
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
                  text={t(Localization('identity-page', 'face-card'))}
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
                text={t(Localization('identity-page', 'replace-file'))}
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
              /** face of card upload**/
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
                            ...errorMessage,
                            faceCard: {take: '', upload: ''},
                          });
                        } else {
                          setErrorMessage({
                            ...errorMessage,
                            faceCard: {
                              take: '',
                              upload: t(
                                Localization('upload-identity', 'file-size'),
                              ),
                            },
                          });
                        }
                      } else {
                        setErrorMessage({
                          ...errorMessage,
                          faceCard: {
                            take: '',
                            upload: t(
                              Localization('upload-identity', 'file-type'),
                            ),
                          },
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
                  text={errorMessage.faceCard.upload}
                  sx={{...FigmaCTA.CtaSmall, color: 'red'}}
                />
              </label>
            }
            btnTakePhoto={
              /** face of card take photo**/
              <label htmlFor="btn-takePhoto">
                <input
                  id="btn-takePhoto"
                  name="btn-takePhoto"
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
                            ...errorMessage,
                            faceCard: {upload: '', take: ''},
                          });
                        } else {
                          setErrorMessage({
                            ...errorMessage,
                            ...errorMessage,
                            faceCard: {
                              upload: '',
                              take: t(
                                Localization('upload-identity', 'file-size'),
                              ),
                            },
                          });
                        }
                      } else {
                        setErrorMessage({
                          ...errorMessage,
                          faceCard: {
                            upload: '',

                            take: t(
                              Localization('upload-identity', 'file-type'),
                            ),
                          },
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
                  text={errorMessage.faceCard.take}
                  sx={{...FigmaCTA.CtaSmall, color: 'red'}}
                />
              </label>
            }
          />
          {/** behind of card **/}
          <CniBox
            headerCard={
              <Stack direction={'row'} spacing={'10px'}>
                <NGIdentityFaceCard />
                <NGText
                  text={t(Localization('identity-page', 'back-card'))}
                  sx={{...FigmaBody.BodyMediumBold}}
                />
              </Stack>
            }
            IsSuccess={Img.backCard !== null}
            nameFile={Img.backCard?.name}
            btnRemove={
              <NGText
                onClick={() => setImg({...Img, backCard: null})}
                text={t(Localization('identity-page', 'replace-file'))}
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
              /** back of card take upload **/
              <label htmlFor="btn-upload-TakePhoto">
                <input
                  id="btn-upload-TakePhoto"
                  name="btn-upload-TakePhoto"
                  style={{display: 'none'}}
                  type="file"
                  accept="."
                  multiple={false}
                  onChange={e => {
                    if (e.target.files) {
                      const Size = e.target.files[0].size;
                      const Name = e.target.files[0].name;
                      if (extensionAccept(Name)) {
                        if (Size < env.VITE_MAX_FILE_SIZE_UPLOAD_ADVANCE) {
                          setImg({...Img, backCard: e.target.files[0]});
                          setErrorMessage({
                            ...errorMessage,
                            backCard: {
                              take: '',
                              upload: '',
                            },
                          });
                        } else {
                          setErrorMessage({
                            ...errorMessage,
                            backCard: {
                              take: '',
                              upload: t(
                                Localization('upload-identity', 'file-size'),
                              ),
                            },
                          });
                        }
                      } else {
                        setErrorMessage({
                          ...errorMessage,
                          backCard: {
                            take: '',
                            upload: t(
                              Localization('upload-identity', 'file-type'),
                            ),
                          },
                        });
                      }
                    }
                  }}
                />
                <BoxUploadAndTakePhoto
                  label={t(
                    Localization('identity-page', 'upload-file-from-front-end'),
                  )}
                  icon={
                    <NGIdentityExport
                      sx={{color: 'Black.main', width: '16px', height: '16px'}}
                    />
                  }
                />
                <NGText
                  text={errorMessage.backCard.upload}
                  sx={{...FigmaCTA.CtaSmall, color: 'red'}}
                />
              </label>
            }
            btnTakePhoto={
              /** back of card take photo **/
              <label htmlFor="btn-takePhoto-back">
                <input
                  id="btn-takePhoto-back"
                  name="btn-takePhoto-back"
                  style={{display: 'none'}}
                  type="file"
                  accept="."
                  onChange={e => {
                    if (e.target.files) {
                      const Size = e.target.files[0].size;
                      const Name = e.target.files[0].name;
                      if (extensionAccept(Name)) {
                        if (Size < env.VITE_MAX_FILE_SIZE_UPLOAD_ADVANCE) {
                          setImg({...Img, backCard: e.target.files[0]});
                          setErrorMessage({
                            ...errorMessage,
                            backCard: {upload: '', take: ''},
                          });
                        } else {
                          setErrorMessage({
                            ...errorMessage,
                            backCard: {
                              upload: '',
                              take: t(
                                Localization('upload-identity', 'file-size'),
                              ),
                            },
                          });
                        }
                      } else {
                        setErrorMessage({
                          ...errorMessage,
                          backCard: {
                            upload: '',
                            take: t(
                              Localization('upload-identity', 'file-type'),
                            ),
                          },
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
                  text={errorMessage.backCard.take}
                  sx={{...FigmaCTA.CtaSmall, color: 'red'}}
                />
              </label>
            }
          />
        </Stack>
      </Stack>
      {/**
       * Button Next
       **/}
      {Img.faceCard !== null && Img.backCard !== null && (
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
            // disabled={!desktopChecked}
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
    </IdentityLayout>
  );
}

export default NationalCard;
