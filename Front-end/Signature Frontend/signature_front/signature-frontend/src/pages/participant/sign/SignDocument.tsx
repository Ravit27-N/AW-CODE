import {NGArrowLeft} from '@assets/iconExport/Allicon';
import {Box, Divider, OutlinedInput, Stack} from '@mui/material';
import {useTranslation} from 'react-i18next';

import {Localization} from '@/i18n/lan';
import NGText from '@components/ng-text/NGText';
import {TFunction} from 'i18next';
import React from 'react';

import {
  NavLink,
  useLocation,
  useNavigate,
  useParams,
  useSearchParams,
} from 'react-router-dom';
import {validateDownloadRoleSign} from '../common/checkRole';

import {LuApproval, ProjectStatus, SIGNING_PROCESS} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {colorBlack} from '@/constant/style/StyleConstant';
import {styles} from '@/pages/form/process-upload/edit-pdf/other/css.style';
import {
  useGetProjectByFlowIdQuery,
  useSignDocumentMutation,
} from '@/redux/slides/process-control/participant';
import {$ok} from '@/utils/request/common/type';
import CheckIcon from '@mui/icons-material/Check';
import Button from '@mui/material/Button';
import InvitationLayout from '../invitation/InvitationLayout';
import WriteTab from '../signatory/tab-list/WriteTab';

import {downloadLink} from '@/utils/common/DownloadDocLink';
import {LoadingButton} from '@mui/lab';

type IState = {
  loadingSignDocument: boolean;
  download: boolean;
  isCorrect: boolean;
  loading: boolean;
  role: string;
};

export default function SignDocument() {
  const {t} = useTranslation();
  const {id} = useParams();
  const queryParameters = new URLSearchParams(window.location.search);
  const [state, setState] = React.useState<IState>({
    download: false,
    isCorrect: false,
    loadingSignDocument: false,
    loading: false,
    role: '',
  });
  const [signDocument] = useSignDocumentMutation();
  const [searchQuery] = useSearchParams();
  const {currentData: data, refetch} = useGetProjectByFlowIdQuery({
    id: id + '?' + queryParameters,
  });
  const [approveInput, setApproveInput] = React.useState('');
  const [validateApproved, setValidateApproved] = React.useState<string>('');
  const boxShadow = '2px 4px 16px rgba(112, 144, 176, 0.16)';
  // const dataTabs = [
  //   {
  //     label: t(Localization('text', 'Écrire')),
  //     contain: ecrireLabel(t),
  //     element: <WriteTab data={data} />,
  //   },
  //   {
  //     label: t(Localization('text', 'Dessiner')),
  //     contain: 'Dessiner',
  //     element: <></>,
  //   },
  //   {
  //     label: t(Localization('text', 'Importer')),
  //     contain: 'Importer',
  //     element: <></>,
  //   },
  // ];

  const handleInputChange = (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => {
    setApproveInput(e.target.value);
  };

  const handleSignDocument = async () => {
    setState({
      ...state,
      loadingSignDocument: true,
      download: true,
    });

    try {
      await signDocument({
        flowId: id!,
        uuid: searchQuery.get('token') ?? '',
      }).unwrap();
      setState({
        ...state,
        download: true,
        loadingSignDocument: false,
      });
      return refetch();
    } catch (e) {
      setState({
        ...state,
        loadingSignDocument: false,
      });
    }
  };

  React.useEffect(() => {
    if (LuApproval.startsWith(approveInput)) {
      setValidateApproved(approveInput);
    }
  }, [approveInput]);

  React.useEffect(() => {
    if (approveInput === LuApproval) {
      setState({
        ...state,
        isCorrect: true,
      });
    } else {
      setState({
        ...state,
        isCorrect: false,
      });
    }
  }, [approveInput]);

  const handleBackStep = (): string => {
    if ('signingProcess' in data!) {
      const {signingProcess} = data as {signingProcess: SIGNING_PROCESS};
      let goto = Route.participant.viewFile;
      if (signingProcess) {
        if (
          [SIGNING_PROCESS.COSIGN, SIGNING_PROCESS.COUNTER_SIGN].indexOf(
            signingProcess,
          ) > -1
        ) {
          goto = Route.participant.viewSignatoryFile;
        } else if (signingProcess === SIGNING_PROCESS.INDIVIDUAL_SIGN) {
          goto = Route.participant.viewFile;
        }
      }
      return `${goto}/${id}?${queryParameters}`;
    }
    return `${Route.participant.viewSignatoryFile}/${id}?${queryParameters}`;
  };
  const navigate = useNavigate();
  React.useMemo(() => {
    if (data) {
      const {
        projectStatus,
        otpInfo: {validated},
        actor: {role, processed, comment},
      } = data;
      if (projectStatus === ProjectStatus.EXPIRED) {
        navigate(
          `${Route.participant.expiredProject}/${id}?${queryParameters}`,
        );
      }
      const validate = validateDownloadRoleSign(role);
      if (validate) {
        navigate(`${validate}/${id}?${queryParameters}`);
      } else {
        if (!validated) {
          navigate(handleBackStep());
        }
        if (processed) {
          const {
            actor: {processed, role},
          } = data as {actor: {processed: boolean; role: string}};
          if (processed) {
            if (comment) {
              navigate(
                `${Route.participant.refuseDocument}/${id}?${queryParameters}`,
              );
            } else {
              navigate(
                `${Route.participant.signDocument}/${id}?${queryParameters}`,
              );
            }
          }
          return setState({
            ...state,
            loading: true,
            download: true,
            loadingSignDocument: false,
            role,
          });
        }
        setState({
          ...state,
          loading: true,
          role,
        });
      }
    }
  }, [data]);
  const handleMuiDisable = () => {
    if (!LuApproval.startsWith(approveInput)) {
      return 'red';
    } else {
      return '#000000';
    }
  };

  return (
    <InvitationLayout position={false}>
      {!state.loading ? (
        <>loading... </>
      ) : (
        <>
          {state.download ? (
            <Stack
              alignItems={'center'}
              justifyContent={'center'}
              sx={{
                ...styles.scrollbarHidden,
                px: '10px',
              }}>
              <SignatorySign
                state={state}
                flowId={id!}
                docId={$ok(data!.documents[0]) ? data!.documents[0]?.docId : ''}
                actor={
                  $ok(data?.actor)
                    ? data?.actor
                    : {
                        firstName: 'Florian',
                        lastName: '',
                        role: '',
                        processed: false,
                      }
                }
              />
            </Stack>
          ) : (
            <Stack
              alignItems={'center'}
              justifyContent={'flex-start'}
              sx={{
                overflow: 'auto',
                ...styles.scrollbarHidden,
                px: '30px',
                textDecoration: 'none',
              }}>
              <Box
                display={'flex'}
                mt={2}
                sx={{
                  width: '100%',
                }}>
                <NavLink to={handleBackStep()}>
                  <Stack direction={'row'}>
                    <NGText
                      text={<NGArrowLeft sx={{color: 'Primary.main'}} />}
                      myStyle={{display: 'flex', alignItems: 'center'}}
                    />
                    <NGText
                      text={t(Localization('text', 'Revenir au document'))}
                    />
                  </Stack>
                </NavLink>
              </Box>

              <Box>
                <Box p={'1.3rem 0'} textAlign={'center'}>
                  <NGText
                    text={t(Localization('text', 'Signez le document'))}
                    fontSize={20}
                    textAlign={'center'}
                    fontWeight={'600'}
                  />
                </Box>
                <Box
                  borderRadius={'10px'}
                  boxShadow={boxShadow}
                  width={'350px'}
                  sx={{position: 'relative'}}>
                  <Box p={'1rem 0'} textAlign={'center'}>
                    <NGText
                      text={t(
                        Localization('text', 'Recopiez la mention ci-dessous'),
                      )}
                      textAlign={'center'}
                    />
                  </Box>
                  <Divider />
                  <Box p={'1rem'}>
                    <Stack
                      justifyContent={'center'}
                      sx={{width: '100%'}}
                      alignItems={'center'}>
                      <Stack
                        justifyContent={'center'}
                        alignItems={'center'}
                        sx={{width: '200px'}}>
                        <OutlinedInput
                          disabled
                          value={LuApproval}
                          sx={{
                            position: 'absolute',
                            color: '#000000',
                            fontWeight: 600,
                            fontSize: '20px',
                            textAlign: 'center',
                            fontFamily: 'cursive',
                            outlineColor: 'none',
                            width: '200px',
                            '& .MuiInputBase-input.Mui-disabled': {
                              WebkitTextFillColor: '#e0e0e3',
                            },
                            '&.MuiOutlinedInput-root': {
                              fieldset: {
                                borderWidth: 0,
                              },
                              '&.Mui-focused fieldset': {
                                borderWidth: 0,
                              },
                            },
                          }}
                          size="small"
                          color="primary"
                          fullWidth={true}
                        />
                        <Stack direction={'row'} alignItems={'center'}>
                          <OutlinedInput
                            disabled
                            value={validateApproved}
                            sx={{
                              color: '#000000',
                              fontWeight: 600,
                              fontSize: '20px',
                              textAlign: 'center',
                              width: 'auto',
                              fontFamily: 'cursive',
                              outlineColor: 'none',
                              '& .MuiInputBase-input.Mui-disabled': {
                                WebkitTextFillColor: state.isCorrect
                                  ? 'green'
                                  : handleMuiDisable(),
                              },
                              '&.MuiOutlinedInput-root': {
                                fieldset: {
                                  borderWidth: 0,
                                },
                                '&.Mui-focused fieldset': {
                                  borderWidth: 0,
                                },
                              },
                            }}
                            size="small"
                            color="primary"
                            fullWidth={true}
                          />
                          <Stack>
                            {state.isCorrect ? (
                              <CheckIcon sx={{color: 'green'}} />
                            ) : undefined}
                          </Stack>
                        </Stack>
                      </Stack>
                    </Stack>
                    <OutlinedInput
                      onKeyPress={e => {
                        if (state.isCorrect) return e.preventDefault();
                        if (!LuApproval.startsWith(approveInput))
                          return e.preventDefault();
                        return e;
                      }}
                      size="small"
                      color="secondary"
                      fullWidth={true}
                      sx={{
                        mb: '10px',
                        '&.MuiOutlinedInput-root': {
                          fieldset: {
                            borderWidth: '0.2px',
                            borderColor: approveInput.length
                              ? '#F7CDE1'
                              : 'black.main',
                          },
                          '&.Mui-focused fieldset': {
                            borderColor: approveInput.length
                              ? '#F7CDE1'
                              : 'inherit',
                            borderWidth: '0.2px',
                          },
                        },
                      }}
                      value={approveInput}
                      onChange={handleInputChange}
                    />
                    <NGText
                      text={t(
                        Localization(
                          'text',
                          'Respectez les majuscules et les accents',
                        ),
                      )}
                      fontSize={14}
                    />
                  </Box>
                </Box>

                <Box
                  borderRadius={'10px'}
                  boxShadow={boxShadow}
                  mt={3}
                  width={'350px'}>
                  <Box pt={'1rem'} textAlign={'center'}>
                    <NGText
                      text={t(
                        Localization('text', 'Choisissez votre mode signature'),
                      )}
                      textAlign={'center'}
                    />
                  </Box>
                  <Box p={'0 1rem'}>
                    {/** Please don't remove this code below, It will use next sprint.*/}
                    {/*<NGTabs data={dataTabs} defaultTap={dataTabs[0].label} />*/}
                    <Box sx={{mt: 4}}>
                      <NGText
                        text={t(
                          Localization('text', 'Choix de la typographie'),
                        )}
                        fontSize={'14px'}
                      />
                    </Box>
                    <WriteTab data={data} />
                  </Box>
                </Box>
              </Box>

              <Button
                disabled={!state.isCorrect}
                onClick={handleSignDocument}
                variant="contained"
                sx={{
                  mt: '10px',
                  mb: '20px',
                  '&:hover': {
                    bgcolor: '#71717A',
                  },
                  '&.Mui-disabled': {
                    bgcolor: '#71717A',
                    color: '#ffffff',
                  },
                  '&.Mui-hover': {
                    bgcolor: colorBlack,
                  },
                  fontWeight: 600,
                  bgcolor: 'Primary.main',
                  textTransform: 'none',
                  px: '80px',
                  py: '15px',
                }}>
                {t(Localization('form', 'accept-and-sign'))}
              </Button>
            </Stack>
          )}
        </>
      )}
    </InvitationLayout>
  );
}

const ecrireLabel = (t: TFunction<'translation', undefined, 'translation'>) => {
  return (
    <Box>
      <NGText text={t(Localization('text', 'Choix de la typographie'))} />
    </Box>
  );
};

// Been sign and download
type SignAndDownloadDocument = {
  state: IState;
  flowId: string;
  docId: string;
  actor:
    | {
        firstName: string;
        lastName: string;
        processed: boolean;
        role: string;
      }
    | undefined;
};
const SignatorySign = ({
  actor,
  state,
  flowId,
  docId,
}: SignAndDownloadDocument) => {
  const {t} = useTranslation();
  /** get token **/
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);

  // Accessing specific query parameters
  const token = queryParams.get('token');
  const boxShadow = '2px 4px 16px rgba(112, 144, 176, 0.16)';
  return (
    <Stack
      borderRadius={'10px'}
      boxShadow={boxShadow}
      width={'350px'}
      spacing={4}
      sx={{
        p: '30px',
        fontFamily: 'Poppins',
        justifyContent: 'center',
        alignItems: 'center',
      }}>
      <NGText
        fontSize={25}
        font={'poppins'}
        fontWeight={'600'}
        textAlign={'center'}
        text={t(Localization('sign-document', 'its-signed!'))}
      />
      <NGText
        font={'poppins'}
        textAlign={'center'}
        text={`${actor?.firstName},
        ${t(
          Localization('sign-document', 'your-signature-has-been-validated'),
        )}`}
      />

      <NGText
        font={'poppins'}
        fontWeight={'600'}
        textAlign={'center'}
        text={t(Localization('sign-document', 'you-can-view-and-download'))}
      />

      {state.loadingSignDocument ? (
        <LoadingButton
          loading={state.loadingSignDocument}
          loadingPosition="end"
          variant="contained"
          sx={{
            width: '100%',
            mt: '10px',
            mb: '20px',
            '&:hover': {
              bgcolor: '#71717A',
            },
            '&.Mui-disabled': {
              bgcolor: '#71717A',
              color: '#ffffff',
            },
            textTransform: 'none',
            fontWeight: 600,
            bgcolor: 'Primary.main',
            py: '15px',
          }}>
          <NGText
            font={'poppins'}
            sx={{color: '#ffffff'}}
            text={
              state.loadingSignDocument
                ? t(Localization('sign-document', 'in-preparation'))
                : t(Localization('sign-document', 'download-document'))
            }
          />
        </LoadingButton>
      ) : (
        <a
          target={'_blank'}
          style={{textDecoration: 'none'}}
          href={downloadLink({
            docId,
            token: token!,
            company_uuid: flowId,
          })}
          rel="noreferrer">
          <LoadingButton
            variant="contained"
            sx={{
              width: '100%',
              mt: '10px',
              mb: '20px',

              '&:hover': {
                bgcolor: '#71717A',
              },
              '&.Mui-disabled': {
                bgcolor: '#71717A',
                color: '#ffffff',
              },
              textTransform: 'none',
              fontWeight: 600,
              bgcolor: 'Primary.main',
              py: '15px',
            }}>
            <NGText
              font={'poppins'}
              sx={{color: '#ffffff'}}
              text={t(Localization('sign-document', 'download-document'))}
            />
          </LoadingButton>
        </a>
      )}
    </Stack>
  );
};
