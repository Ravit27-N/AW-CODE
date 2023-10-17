import {Route} from '@/constant/Route';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {setOption} from '@/redux/counter/CounterSlice';
import {
  IBodyProjectDetail,
  projectSlide,
  useAddProjectMutation,
  useLazyViewDocumentQuery,
  useUpdateProjectStepFourMutation,
} from '@/redux/slides/project-management/project';
import {VStack} from '@/theme';
import {Navigate} from '@/utils/common';
import {NGArrowRight, NGTelegram} from '@assets/iconExport/Allicon';
import {NGButton} from '@components/ng-button/NGButton';
import NGText from '@components/ng-text/NGText';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ClearIcon from '@mui/icons-material/Clear';
import KeyboardArrowRightIcon from '@mui/icons-material/KeyboardArrowRight';
import {
  Backdrop,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  Stack,
  Step,
  StepLabel,
  Stepper,
} from '@mui/material';
import React from 'react';
import {FileRejection} from 'react-dropzone';
import {useTranslation} from 'react-i18next';
import {useDispatch} from 'react-redux';
import {useNavigate, useParams} from 'react-router-dom';
import UploadReceptient from './add-recepients/UploadRecepient';
import UploadDocument from './add-upload/UploadDocument';
import {alertConsole} from './common';
import {EditPDF} from './edit-pdf';
import Envoi from './envoi/Envoi';

import {
  currentProjectIdKey,
  Participant,
  STEP,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {store} from '@/redux';
import {
  resetToInitial,
  setActiveActorRole,
  storeAnnotation,
  storeApprovals,
  storeEnvoiByRole,
  storeProject,
  storeRecipient,
  storeSignatories,
  storeSignatureTemplate,
  storeTempFile,
  storeViewers,
  updateDocumentToAnnotaitons,
} from '@/redux/slides/authentication/authenticationSlide';
import {templateSlice} from '@/redux/slides/profile/template/templateSlide';
import {router} from '@/router';
import {dateFormatStepFour} from '@/utils/common/Date';
import {HandleException} from '@/utils/common/HandleException';
import {pixelToRem} from '@/utils/common/pxToRem';
import {getTemporaryParticipants} from '@/utils/common/SignatureProjectCommon';
import {$ok} from '@/utils/request/common/type';
import {useMatchMedia} from '@wojtekmaj/react-hooks';
import dayjs from 'dayjs';
import {enqueueSnackbar} from 'notistack';
import UploadReceptientByTemplate from './add-recepient-by-template/UploadRecepient';
import {getNumPages} from './edit-pdf/other/common';

const steps = ['Document(s)', 'Participant(s)', 'Dossier', 'Envoi'];

const style = {
  muiActive: {
    '& .MuiSvgIcon-root.Mui-active': {
      color: 'Primary.main',
      borderStyle: 'none',
    },
  },
  muiComplete: {
    '& .MuiSvgIcon-root.Mui-completed': {
      color: '#1C8752',
      borderStyle: 'none',
      backgroundColor: '#ffffff',
    },
  },
  // muiArrowNext: {
  //   '& .css-kyqqjw-MuiSvgIcon-root': {
  //     color: 'Primary.main',
  //   },
  // },
  muiStepIcon: {
    '& .MuiStepIcon-root': {
      borderStyle: 'solid',
      borderColor: '#ffffff',
      borderWidth: '1px',
      borderRadius: '50%',
    },
  },
};
const UploadForm = ({
  popUp,
  closePopup,
}: {
  popUp: boolean;
  closePopup: () => void;
}) => {
  const matches = useMatchMedia('(min-width:1441px)');
  const navigate = useNavigate();
  const param = useParams();
  const {t} = useTranslation();
  const [loading, setLoading] = React.useState(true);
  const dispatch = useDispatch();
  const [activeStep, setActiveStep] = React.useState(0);
  const [isSignatoryFill, setIsSignatoryFill] = React.useState(false);
  const [completed, setCompleted] = React.useState<{
    [k: number]: boolean;
  }>({0: false, 1: false, 2: false, 3: false});
  const [windowSize, setWindowSize] = React.useState([window.innerWidth]);
  const [alertMessage, setAlertMessage] = React.useState<JSX.Element | null>(
    null,
  );
  const {optionT} = useAppSelector(state => state.counter);
  const {project} = useAppSelector(state => state.authentication);
  const [uploadLoading, setUploadLoading] = React.useState<boolean>(false);
  const [errorUpload, setErrorUpload] = React.useState<JSX.Element | null>(
    null,
  );
  const [uploadStep2, setUploadStep2] = React.useState(false);
  const [uploadStep3, setUploadStep3] = React.useState(false);
  const [fileUpload, setFileUpload] = React.useState<
    {file: File; pageCount: number}[]
  >([]);
  const [addProject] = useAddProjectMutation();
  const [updateStepFour] = useUpdateProjectStepFourMutation();
  const [viewDocument] = useLazyViewDocumentQuery();

  React.useEffect(() => {
    setAlertMessage(null);
    setErrorUpload(null);
    /** Message will alert when file length more than one **/
    if (fileUpload.length > 1) {
      setAlertMessage(
        alertConsole(
          'error',
          t(Localization('upload-document', 'maximum-number-of-files-reached')),
        ),
      );
    }
  }, [popUp, fileUpload.length]);

  React.useEffect(() => {
    const fetchProject = async (projectId: string) => {
      try {
        const data = await store
          .dispatch(
            projectSlide.endpoints.getProjectById.initiate({
              id: projectId,
            }),
          )
          .unwrap();
        const {
          signatories,
          id,
          name,
          orderSign,
          templateId,
          step,
          details,
          orderApprove,
          documents,
        } = data;
        if (step === '4' || step === 4) {
          setLoading(false);
          closePopup();
          return router.navigate(`${Route.HOME_ENDUSER}`);
        }
        dispatch(storeEnvoiByRole({signatories, projectDetails: details}));
        dispatch(
          storeProject({
            project: {id, name, orderSign, orderApprove, step: step.toString()},
          }),
        );

        if (templateId) {
          const {
            data: templateData,
            status: templateStatus,
            isSuccess,
            isError,
          } = await store.dispatch(
            templateSlice.endpoints.getTemplateById.initiate({
              id: templateId,
            }),
          );
          if (templateStatus === 'rejected' || isError) {
            setLoading(false);
            closePopup();
            return router.navigate(`${Route.HOME_ENDUSER}`);
          }

          if (templateStatus === 'fulfilled' || isSuccess) {
            dispatch(
              storeSignatureTemplate({
                template: templateData!,
              }),
            );
          }
        }

        if ('projectId' in param && 'templateId' in param) {
          for (const item of documents) {
            try {
              const resFiles = await viewDocument({
                docId: item.fileName,
              }).unwrap();
              dispatch(
                storeTempFile({
                  name: item.fileName,
                  documentId: item.id,
                  file: `data:application/pdf;base64,${resFiles}`,
                }),
              );
            } catch (e) {
              enqueueSnackbar(
                HandleException((e as any).status) ?? UNKOWNERROR,
                {
                  variant: 'errorSnackbar',
                },
              );
            }
          }
        }

        if (signatories.length > 0) {
          const tempParticipants = getTemporaryParticipants(signatories);
          store.dispatch(
            storeAnnotation({
              signatories: tempParticipants.tempSignatories,
            }),
          );
          dispatch(
            storeSignatories({
              data: tempParticipants.tempSignatories,
            }),
          );
          dispatch(storeApprovals({data: tempParticipants.tempApprovals}));
          dispatch(
            storeRecipient({
              data: tempParticipants.tempRecipients,
            }),
          );
          dispatch(
            storeViewers({
              data: tempParticipants.tempViewers,
            }),
          );
          dispatch(
            setActiveActorRole({
              role: `${signatories[0].role}` as Participant,
              id: Number(signatories[0].id),
              signatoryName:
                signatories[0].firstName + ' ' + signatories[0].lastName,
            }),
          );
          if ('projectId' in param && 'templateId' in param) {
            documents.forEach((doc: any) => {
              doc.documentDetails.forEach((detail: any) => {
                dispatch(updateDocumentToAnnotaitons({documentDetail: detail}));
              });
            });
          }

          setActiveStep(STEP.STEP3);
        } else {
          setActiveStep(STEP.STEP2);
        }
        setLoading(false);
      } catch (e) {
        setLoading(false);
        await router.navigate(`${Route.HOME_ENDUSER}`);
      }
    };

    if (param) {
      if ('projectId' in param) {
        const {projectId} = param as {projectId: string};
        fetchProject(projectId).then(r => r);
        return;
      }
    }
    setLoading(false);
  }, []);

  /** When upload to signature-project done will continue an upload to Project-Management */
  const handleAddProject = React.useCallback(async () => {
    if (!fileUpload.length) {
      setErrorUpload(
        alertConsole('error', t(Localization('upload-document', 'empty-file'))),
      );
      return;
    }
    setUploadLoading(true);
    setErrorUpload(null);
    const formData = new FormData();
    fileUpload.forEach(({file}) => {
      formData.append('files', file);
    });
    formData.append('name', 'Signature');
    formData.append('step', '1');
    formData.append('status', '1');
    formData.append(
      'templateId',
      $ok(store.getState().authentication.project.template)
        ? (store.getState().authentication.project.template!
            .id as unknown as string)
        : '',
    );
    return await addProject(formData)
      .unwrap()
      .then((res: any) => {
        if ('error' in res) {
          setUploadLoading(false);
          setErrorUpload(
            alertConsole('error', 'Time out!, please a try again'),
          );
          setUploadLoading(true);
        } else {
          setAlertMessage(null);
          setUploadLoading(false);
          dispatch(
            storeProject({
              project: {
                id: res.id.toString(),
                name: res.name,
                orderSign: false,
                orderApprove: true,
                step: res.step,
              },
            }),
          );
          setActiveStep(v => v + 1);
          return router.navigate(`${Route.HOME_ENDUSER}/${res.id}`);
        }

        return res;
      })
      .catch(() => {
        setUploadLoading(false);
        setErrorUpload(alertConsole('error', UNKOWNERROR));
      });
  }, [fileUpload]);

  const handleAddSignatory = React.useCallback(async () => {
    setUploadStep2(true);
  }, []);

  const handleSignAndApproval = React.useCallback(async () => {
    setUploadStep3(true);
  }, []);

  // Handle upload file
  const handleUploadFile = async (files: File[]) => {
    files.forEach(file => {
      const reader = new FileReader();
      reader.readAsBinaryString(file);
      reader.onload = async () => {
        const numPages = await getNumPages(file);
        setFileUpload(prev => [...prev, {file, pageCount: numPages.length}]);
      };
    });

    return setErrorUpload(null);
  };

  // Handle upload error
  const handleUploadError = (filesRejected: FileRejection[]) => {
    const {errors} = filesRejected[0];
    const {code} = errors[0];
    switch (code) {
      case 'file-too-large':
        setAlertMessage(
          alertConsole(
            'error',
            t(Localization('upload-document', 'large-file')),
          ),
        );
        break;
      case 'too-many-files':
        setAlertMessage(
          alertConsole(
            'error',
            t(
              Localization(
                'upload-document',
                'maximum-number-of-files-reached',
              ),
            ),
          ),
        );
        break;
      case 'file-invalid-type':
        setAlertMessage(
          alertConsole(
            'error',
            t(Localization('upload-document', 'format-not-accepted')),
          ),
        );
        break;
    }
  };
  // Handle step completed
  const handleComplete = React.useCallback(() => {
    const defaultStep: {[k: number]: boolean} = {
      0: false,
      1: false,
      2: false,
      3: false,
    };
    for (let i = 0; i < activeStep; i++) {
      defaultStep[i] = true;
    }
    setCompleted(defaultStep);
  }, [activeStep]);
  /*
   ** Handle update project step 4
   */
  const {signatories, approvals, recipients, viewers, signatureLevels} =
    useAppSelector(state => state.authentication);
  const updateStep4 = async ({id}: {id: number | string}) => {
    const {selectEnvoiData, project, activeActorEnvoi} =
      store.getState().authentication;
    const details: IBodyProjectDetail[] = [];
    Object.keys(selectEnvoiData!).forEach(item => {
      const detail = selectEnvoiData![item];

      if (detail.id) {
        details.push({
          id: detail.id,
          type: item as Participant,
          projectId: Number(project.id!),
          titleInvitation: detail.title,
          messageInvitation: detail.description,
        });
      }
    });

    setUploadLoading(true);
    try {
      await updateStepFour({
        id,
        step: '4',
        name: optionT.docName! || 'Signature',
        status: '1',
        signatories: [],
        documents: [],
        details:
          details.length > 0
            ? details.length <
              (signatories.length > 0 ? 1 : 0) +
                (approvals.length > 0 ? 1 : 0) +
                (recipients.length > 0 ? 1 : 0) +
                (viewers.length > 0 ? 1 : 0)
              ? ![...details.map(item => item.type)].includes(
                  {
                    projectId: Number(project.id!),
                    messageInvitation: optionT.message,
                    titleInvitation: optionT.title,
                    type: activeActorEnvoi?.role!,
                    id: null,
                  }.type,
                )
                ? [
                    ...details,
                    {
                      projectId: Number(project.id!),
                      messageInvitation: optionT.message,
                      titleInvitation: optionT.title,
                      type: activeActorEnvoi?.role!,
                      id: null,
                    },
                  ]
                : [...details]
              : details
            : [
                {
                  projectId: Number(project.id!),
                  messageInvitation: optionT.message,
                  titleInvitation: optionT.title,
                  type: activeActorEnvoi?.role!,
                  id: null,
                },
              ],
        orderSign: store.getState().authentication.project.orderSign,
        expireDate: dateFormatStepFour(optionT.opt4),
        autoReminder: !!optionT.opt3 ?? false,
        channelReminder: optionT.opt2,
        reminderOption: optionT.opt3 ?? null,
        setting: {
          companyUuid: signatureLevels.companyUuid,
          signatureLevel: signatureLevels.signatureLevel,
          fileType: signatureLevels.fileTypeSelected,
          documentTerms: signatureLevels.documentTerms,
          personalTerms: signatureLevels.personalTerms,
          identityTerms: signatureLevels.identityTerms,
          channelReminder: signatureLevels.remainderSelected,
        },
        signatureLevel:
          store.getState().authentication.signatureLevels.signatureLevel,
      }).unwrap();

      dispatch(resetToInitial());
      setFileUpload([]);
      setUploadLoading(false);
      setActiveStep(0);
      closePopup();
      localStorage.setItem(currentProjectIdKey, project.id!);
      return navigate(Navigate(Route.project.projectDetail + '/' + project.id));
    } catch (error) {
      setUploadLoading(false);
      enqueueSnackbar((error as any).statusCode ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  React.useEffect(() => {
    handleComplete();
  }, [activeStep]);

  // Handle screen size change event
  React.useEffect(() => {
    const handleWindowResize = () => {
      setWindowSize([window.innerWidth]);
    };

    window.addEventListener('resize', handleWindowResize);
    return () => {
      window.removeEventListener('resize', handleWindowResize);
    };
  }, []);
  React.useEffect(() => {
    setFileUpload([]);
  }, [closePopup]);
  return (
    <Dialog
      sx={{
        '& .MuiPaper-root': {
          boxSizing: 'border-box',
          borderRadius: [STEP.STEP3, STEP.STEP4].includes(activeStep) ? 0 : 4,
        },
      }}
      open={popUp ?? true}
      fullWidth={true}
      maxWidth={matches ? 'lg' : 'md'}
      fullScreen={[3, 2].indexOf(activeStep) > -1}>
      <DialogTitle
        sx={{
          display: 'flex',
          justifyContent: 'center',
          backgroundColor: '#121232',
        }}>
        <Stack
          direction={'row'}
          justifyContent={'center'}
          alignItems={'center'}
          width={'100%'}>
          {[STEP.STEP3, STEP.STEP4].indexOf(activeStep) > -1 ? (
            <Stack spacing={2} direction={'row'} width={'30%'}>
              <VStack>
                <Stack direction={'row'} alignItems={'center'} spacing={2}>
                  <ArrowBackIcon
                    sx={{color: '#ffffff', cursor: 'pointer'}}
                    onClick={() => setActiveStep(v => v - 1)}
                  />
                  <Button
                    variant="text"
                    sx={{
                      textTransform: 'none',
                      color: '#ffffff',
                      visibility: activeStep > 0 ? 'visible' : 'hidden',
                      ...StyleConstant.textBold,
                      borderBottom: 1.5,
                      borderRadius: 0,
                      width: '80%',
                      borderColor: 'Text2.main',
                    }}
                    size={'small'}>
                    <NGText
                      text={project.name}
                      myStyle={{color: 'white', fontWeight: 700, fontSize: 16}}
                    />
                  </Button>
                </Stack>
              </VStack>
            </Stack>
          ) : (
            <Stack sx={{width: '30%'}}></Stack>
          )}

          <Stack
            sx={{
              width: '40%',
              alignItems: 'center',
            }}>
            <Stepper
              connector={
                <KeyboardArrowRightIcon
                  sx={{opacity: '100%', color: 'white'}}
                />
              }
              nonLinear
              activeStep={activeStep}
              sx={{
                width: '100%',
                justifyContent: 'center',
                ...style.muiActive,
                ...style.muiComplete,
                ...style.muiStepIcon,
                // ...style.muiArrowNext,
              }}>
              {windowSize[0] < 900
                ? steps
                    .filter(
                      (step, index: number) => index === activeStep && step,
                    )
                    .map(label => (
                      <StepLabel
                        sx={{
                          textAlign: 'center',
                        }}
                        color="inherit"
                        key={label}>
                        <NGText
                          text={label}
                          myStyle={{
                            color: 'primary.contrastText',
                            fontSize: '14px',
                          }}
                        />
                      </StepLabel>
                    ))
                : steps.map((label, index) => (
                    <Step key={label} completed={completed[index]}>
                      <StepLabel>
                        <NGText
                          text={label}
                          myStyle={{
                            color: 'primary.contrastText',
                            fontSize: pixelToRem(12),
                          }}
                        />
                      </StepLabel>
                    </Step>
                  ))}
            </Stepper>
          </Stack>
          {[STEP.STEP3, STEP.STEP4].indexOf(activeStep) > -1 ? (
            <Stack
              direction={'row'}
              spacing={1}
              justifyContent={'flex-end'}
              alignItems={'center'}
              width={'30%'}>
              {[3].indexOf(activeStep) > -1 && (
                <NGButton
                  onClick={() => setActiveStep(v => v - 1)}
                  title={'Précédent'}
                  icon={
                    <ArrowBackIcon
                      sx={{color: '#ffffff', ml: 1, cursor: 'pointer'}}
                    />
                  }
                  locationIcon={'start'}
                  myStyle={{
                    border: 1,
                    borderColor: 'white',
                    bgcolor: '#121232',
                  }}
                />
              )}

              <NGButton
                onClick={async () => {
                  if (activeStep === STEP.STEP3) {
                    await handleSignAndApproval();
                  } else {
                    optionT.opt4 < new Date() ||
                    optionT.opt4 > dayjs().add(9, 'd').toDate()
                      ? dispatch(setOption({...optionT, checkDate: true}))
                      : await updateStep4({
                          id: project.id! as string | number,
                        });
                  }
                }}
                myStyle={{px: 3}}
                title={activeStep < steps.length - 1 ? 'Suivant' : 'Envoyer'}
                icon={
                  activeStep === 3 ? (
                    <NGTelegram sx={{color: 'white', fontSize: 20, ml: 1}} />
                  ) : (
                    activeStep === 2 && (
                      <NGArrowRight
                        sx={{color: 'white', fontSize: 20, ml: 1}}
                      />
                    )
                  )
                }
                locationIcon="end"
                size={'large'}
                bgColor={'primary'}
              />
            </Stack>
          ) : (
            <IconButton
              disableRipple
              sx={{
                padding: 0,
                width: '30%',
                justifyContent: 'flex-end',
                color: '#ffffff',
              }}
              onClick={async () => {
                dispatch(resetToInitial());
                closePopup();
                await router.navigate(Route.HOME_ENDUSER);
                setActiveStep(STEP.STEP1);
              }}>
              <ClearIcon />
            </IconButton>
          )}
        </Stack>
      </DialogTitle>
      <Divider />
      {loading ? (
        <>loading...</>
      ) : (
        <DialogContent
          sx={{
            display: 'flex',
            justifyContent: 'center',
            p: 0,
            m: 0,
            backgroundColor: 'bg.main',
          }}>
          {activeStep === 0 ? (
            <Stack
              sx={{
                padding: '2rem 1rem',
                width: {sm: '100%', md: '90%'},
              }}>
              <UploadDocument
                setFileUpload={setFileUpload}
                fileUpload={fileUpload}
                errorUpload={errorUpload}
                alertMessage={alertMessage}
                handleUploadFile={handleUploadFile}
                handleUploadError={handleUploadError}
              />
            </Stack>
          ) : activeStep === 1 ? (
            <Stack>
              {store.getState().authentication.project.template ? (
                <UploadReceptientByTemplate
                  setActiveStep={setActiveStep}
                  uploadStep2={uploadStep2}
                  setIsSignatoryFill={setIsSignatoryFill}
                  setUploadStep2={setUploadStep2}
                />
              ) : (
                <UploadReceptient
                  setActiveStep={setActiveStep}
                  uploadStep2={uploadStep2}
                  setIsSignatoryFill={setIsSignatoryFill}
                  setUploadStep2={setUploadStep2}
                />
              )}
            </Stack>
          ) : activeStep === 2 ? (
            <EditPDF
              setActiveStep={setActiveStep}
              uploadStep3={uploadStep3}
              setUploadStep3={setUploadStep3}
            />
          ) : (
            <Box width={'100%'} overflow={'hidden'}>
              <Envoi />
            </Box>
          )}
        </DialogContent>
      )}
      {[STEP.STEP1, STEP.STEP2].indexOf(activeStep) > -1 && (
        <DialogActions sx={{justifyContent: 'center', py: 2}}>
          <Stack
            width={{xs: '100%', sm: '80%', md: '95%'}}
            direction={'row'}
            alignItems={'center'}
            justifyContent={'space-between'}>
            <Button
              sx={{textTransform: 'capitalize'}}
              onClick={async () => {
                dispatch(resetToInitial());
                closePopup();
                await router.navigate(Route.HOME_ENDUSER);
                setActiveStep(STEP.STEP1);
              }}>
              <NGText
                text={'Annuler'}
                onClick={closePopup}
                myStyle={{
                  color: 'black.main',
                  cursor: 'pointer',
                  fontSize: 11,
                  fontWeight: 600,
                }}
              />
            </Button>

            <Stack direction={'row'} spacing={1}>
              <Button
                onClick={() => setActiveStep(v => v - 1)}
                disabled={activeStep === 0}
                variant={'outlined'}
                color={'primary'}
                sx={{textTransform: 'capitalize'}}>
                <NGText
                  text={'Précédent'}
                  disable={activeStep === 0}
                  myStyle={{fontSize: 11, fontWeight: 600}}
                />
              </Button>
              {/* Submit  */}
              <Button
                onClick={async () => {
                  if (activeStep === STEP.STEP1) {
                    await handleAddProject();
                  } else if (activeStep === STEP.STEP2) {
                    await handleAddSignatory();
                  }
                }}
                variant={'contained'}
                color={fileUpload === undefined ? 'secondary' : 'primary'}
                disabled={
                  (activeStep === STEP.STEP1 && fileUpload.length <= 0) ||
                  /** will disable button when file length more than one **/
                  fileUpload.length > 1 ||
                  (activeStep === STEP.STEP2 && !isSignatoryFill)
                }
                sx={{
                  textTransform: 'capitalize',
                  py: 1.5,
                  px: 4,
                  '&:disabled': {
                    backgroundColor: '#6D676A' || 'info.main',
                  },
                }}>
                <NGText
                  text={'Suivant'}
                  myStyle={{color: 'white', fontSize: 11, fontWeight: 600}}
                />
              </Button>
            </Stack>
          </Stack>
        </DialogActions>
      )}

      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={uploadLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Dialog>
  );
};

export default UploadForm;
