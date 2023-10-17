import {DocumentIcon} from '@/assets/svg/document/document';

import {
  OtpLength,
  Participant,
  ProjectStatus,
  trailPhoneLength,
} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {
  StyleConstant,
  colorDisable,
  colorWhite,
} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {SnackBarMui} from '@/pages/form/process-upload/edit-pdf/other/common';
import {useAppSelector} from '@/redux/config/hooks';

import {
  useGenerateOTPMutation,
  useGetProjectByFlowIdQuery,
  useIndividualSignMutation,
  useRefuseDocumentMutation,
  useSetupIndividualMutation,
  useValidateOTPMutation,
  useValidatePhoneNumberMutation,
} from '@/redux/slides/process-control/participant';
import {Center} from '@/theme';

import {pixelToRem} from '@/utils/common/pxToRem';
import {getFilePdf} from '@/utils/request/services/MyService';
import certignaLogo from '@assets/background/login/NGLogo.svg';
import {NGAlert, NGFalse} from '@assets/iconExport/Allicon';
import {LockIcon} from '@assets/svg/lock/lock';
import {SignatureIcon} from '@assets/svg/signature/signature';
import {NGButton} from '@components/ng-button/NGButton';
import NGDialog from '@components/ng-dailog/NGDialog';
import NGText from '@components/ng-text/NGText';

import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import {
  Box,
  Button,
  IconButton,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';

import {WebViewerInstance} from '@pdftron/webviewer';
import {MuiOtpInput} from 'mui-one-time-password-input';
import React, {ChangeEvent, useMemo} from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams, useSearchParams} from 'react-router-dom';

import {validateFileRoleSign} from '../common/checkRole';
import PDFTronViewFileToSign from './PDFTronViewFleToSign';

export type IViewFileSignState = {
  validateLoading: boolean;
  loadingGen: boolean;
  blockAttemp: boolean;
  otpToggle: boolean;
  disableSignature: boolean;
  phoneNumberToggle: boolean;
  pageNum: number;
  loading: boolean;
};

const PDFTronView = () => {
  const navigate = useNavigate();
  const param = useParams();
  const {t} = useTranslation();
  const [instance, setInstance] = React.useState<WebViewerInstance | null>(
    null,
  );
  //Redux endpoint
  const [validatePhoneNumber] = useValidatePhoneNumberMutation();
  const [btnEnable, setBtnEnable] = React.useState(false);
  const [currentTotalPages, setCurrentTotalPages] = React.useState<
    number | null
  >(null);
  const [currentPage, setCurrentPage] = React.useState<number | null>(null);
  const [disableSignature, setDisableSignature] = React.useState<boolean>(true);
  const [allFileBase64, setAllFileBase64] = React.useState<string[]>([]);
  const [dataInfo, setDataInfo] = React.useState<{
    otpInfo: {
      expired: boolean;
      validated: boolean;
    };
    actor: {
      role: string;
    };
    phoneNumber: {
      removedNumber: string | number;
      totalAttempts: number;
      validated: boolean;
      number: string;
    };
  }>();
  const [responseInfo, setResponseInfo] = React.useState<{
    severity: 'error' | 'warning' | 'info' | 'success';
    message: string;
  } | null>(null);
  const queryParameters = new URLSearchParams(window.location.search);
  const [searchQuery] = useSearchParams();
  // Redux endpoint
  const {
    currentData: data,
    isLoading,
    refetch,
    isSuccess,
  } = useGetProjectByFlowIdQuery({
    id: param.id + '?' + queryParameters,
  });
  const [validateOtp] = useValidateOTPMutation();
  const [individualSignPost] = useIndividualSignMutation();
  const [setupIndividual] = useSetupIndividualMutation();

  const [state, setState] = React.useState<IViewFileSignState>({
    disableSignature: true,
    blockAttemp: false,
    loadingGen: false,
    otpToggle: false,
    validateLoading: false,
    phoneNumberToggle: false,
    pageNum: 0,
    loading: true,
  });
  const [individualSignLoading, setIndividualSignLoading] =
    React.useState(false);
  const [phoneOtp, setPhoneOtp] = React.useState<string>('');
  const [invalidPhoneMessage, setInvalidPhoneMessage] =
    React.useState<string>('');
  const [fullPhoneNumber, setFullPhoneNumber] = React.useState<string>('');
  const [generateOpt] = useGenerateOTPMutation();

  const [otp, setOtp] = React.useState<string>('');
  const handleOtpChange = (newValue: string) => {
    setOtp(newValue);
  };

  const [refuseToggle, setRefuseToggle] = React.useState(false);
  const [textArea, setTextArea] = React.useState('');
  const onChangeTextArea = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setTextArea(e.target.value);
  };
  const [refuseDocument, {isLoading: refuseDocumentLoading}] =
    useRefuseDocumentMutation();

  // Handle submit refuse document
  const submitRefuseDocument = async (): Promise<unknown> => {
    try {
      await refuseDocument({
        flowId: param.id!,
        uuid: searchQuery.get('token') ?? '',
        comment: textArea,
      }).unwrap();
      refetch();
    } catch (error) {
      return error;
    }
  };

  const handleChangeOtp = (value: string) => {
    setPhoneOtp(value);
  };
  const handleClose = () => {
    setResponseInfo(null);
  };

  const handleGenerateOtp = async () => {
    if (dataInfo?.otpInfo.expired) {
      try {
        await generateOpt({
          flowId: param.id!,
          uuid: searchQuery.get('token') ?? '',
        }).unwrap();
        return setState({...state, otpToggle: true, validateLoading: false});
      } catch (e) {
        setResponseInfo({
          message: 'An error occurred!.',
          severity: 'error',
        });
      }
    } else {
      if (!dataInfo?.otpInfo.validated) {
        try {
          await generateOpt({
            flowId: param.id!,
            uuid: searchQuery.get('token') ?? '',
          }).unwrap();
          return setState({...state, otpToggle: true, validateLoading: false});
        } catch (e) {
          setResponseInfo({
            message: 'An error occurred!.',
            severity: 'error',
          });
        }
      }
    }
  };

  const handleSetUpIndividual = async () => {
    try {
      await setupIndividual({
        flowId: param.id!,
        uuid: searchQuery.get('token') ?? '',
      });
      setIndividualSignLoading(false);
      return setState({...state, otpToggle: true, validateLoading: false});
    } catch (e) {
      return setResponseInfo({
        message: 'An error occurred!.',
        severity: 'error',
      });
    }
  };

  const handleIndividualSign = async () => {
    setIndividualSignLoading(true);
    const xfdfString =
      await instance?.Core.annotationManager.exportAnnotations();
    const annotLists = instance?.Core.annotationManager.getAnnotationsList();
    const file = await instance?.Core.documentViewer
      .getDocument()
      .getFileData({xfdfString})!;
    const arr = new Uint8Array(file);
    const blob = new Blob([arr], {type: 'application/pdf'});
    const {docId} = data!.documents[0];
    const formData = new FormData();
    formData.append('file', blob);

    if (!annotLists!.length) {
      return handleSetUpIndividual();
    }

    try {
      await individualSignPost({
        body: formData,
        flowId: param.id!,
        docId,
        uuid: searchQuery.get('token') ?? '',
      }).unwrap();

      await handleSetUpIndividual();
      setIndividualSignLoading(false);
      return setState({...state, otpToggle: true, validateLoading: false});
    } catch (error) {
      setIndividualSignLoading(false);
      return setResponseInfo({
        message: 'An error occurred!.',
        severity: 'error',
      });
    }
  };

  // Handle submit OTP
  const submitConfirmOtp = async () => {
    setState({...state, validateLoading: true});
    setInvalidPhoneMessage('');
    try {
      await validateOtp({
        flowId: param.id!,
        otp,
        uuid: searchQuery.get('token') ?? '',
      })
        .unwrap()
        .then(data => {
          if (data) {
            setState({
              ...state,
              otpToggle: false,
              validateLoading: false,
              phoneNumberToggle: false,
            });
            setInvalidPhoneMessage('');
            setOtp('');
            return navigate(
              `${Route.participant.signDocument}/${param.id}?${queryParameters}`,
            );
          } else {
            setInvalidPhoneMessage('Code incorrect');
            setState({...state, validateLoading: false});
            setOtp('');
          }
        });
    } catch (e: any) {
      if (e.status === 406) {
        const {error} = e.data as any;
        if (error) {
          setInvalidPhoneMessage('Code incorrect!');
        }
      }

      return setState({
        ...state,
        validateLoading: false,
        phoneNumberToggle: false,
      });
    }
  };

  // Handle submit phone number confirmation
  const submitPhoneNumberOtp = async () => {
    if (!phoneOtp.length) {
      setInvalidPhoneMessage('OTP required.');
    } else {
      setState({...state, validateLoading: true});
      const data: {
        totalAttempts: number;
        number: string;
        missingLength: number;
        valid: boolean;
      } = await validatePhoneNumber({
        flowId: param.id!,
        phone: phoneOtp,
        uuid: searchQuery.get('token') ?? '',
      }).unwrap();
      if (data) {
        setFullPhoneNumber(data.number);
        const {totalAttempts, valid} = data;
        if (!valid) {
          setState({...state, validateLoading: false});

          if (totalAttempts >= 3) {
            setInvalidPhoneMessage('');
            return setState({
              ...state,
              blockAttemp: true,
              phoneNumberToggle: false,
            });
          }
          return setInvalidPhoneMessage(
            t(Localization('confirm-phone-number-stage', 'wrong-number')) +
              t(Localization('confirm-phone-number-stage', 'try-left')) +
              ' ' +
              (3 - totalAttempts) +
              ' ' +
              t(Localization('confirm-phone-number-stage', 'left')) +
              '(s)',
          );
        }
        setState({...state, phoneNumberToggle: false, validateLoading: false});
        setInvalidPhoneMessage('');
      }
    }
  };

  React.useEffect(() => {
    if (data) {
      const {
        projectStatus,
        actor: {processed, role, comment},
        otpInfo: {validated},
      } = data;
      if (projectStatus === ProjectStatus.EXPIRED) {
        navigate(
          `${Route.participant.expiredProject}/${param.id}?${queryParameters}`,
        );
      }
      const validate = validateFileRoleSign(role);
      if (validate) {
        navigate(`${validate}/${param.id}?${queryParameters}`);
      }
      if (validated) {
        navigate(
          `${Route.participant.signDocument}/${param.id}?${queryParameters}`,
        );
      }
      if (processed) {
        if (comment) {
          navigate(
            `${Route.participant.refuseDocument}/${param.id}?${queryParameters}`,
          );
        } else {
          navigate(
            `${Route.participant.signDocument}/${param.id}?${queryParameters}`,
          );
        }
      } else {
        if (role === Participant.Approval) {
          navigate(
            `${Route.participant.viewSignatoryFile}/${param.id}?${queryParameters}`,
          );
        }
        // if ('signingProcess' in data) {
        //   const {signingProcess} = data as {signingProcess: SIGNING_PROCESS};
        //   let goto = Route.participant.viewFile;
        //   if (signingProcess) {
        //     if (
        //       [SIGNING_PROCESS.COSIGN, SIGNING_PROCESS.COUNTER_SIGN].indexOf(
        //         signingProcess,
        //       ) > -1
        //     ) {
        //       goto = Route.participant.viewSignatoryFile;
        //     } else if (signingProcess === SIGNING_PROCESS.INDIVIDUAL_SIGN) {
        //       goto = Route.participant.viewFile;
        //     }
        //   }
        //   navigate(`${goto}/${param.id}?${queryParameters}`);
        // }
      }
    }
  }, [data]);

  useMemo(() => {
    if (currentPage && currentTotalPages) {
      if (currentPage === currentTotalPages) {
        setDisableSignature(false);
      }
    }
  }, [currentPage, currentTotalPages]);

  React.useEffect(() => {
    const storeData = async () => {
      const allFileDocId: string[] = [];
      data!.documents.map((item: any) => allFileDocId.push(item.docId));
      const filesBase64: string[] = [];
      const {phoneNumber, otpInfo, actor} = data!;
      const {totalAttempts} = phoneNumber;

      for (const item of allFileDocId) {
        const fileBase64 = await getFilePdf({
          flowId: param.id + '?' + queryParameters,
          docId: item,
        });
        filesBase64.push(fileBase64.toString());
      }
      setAllFileBase64(filesBase64);
      setDataInfo({
        otpInfo,
        phoneNumber,
        actor,
      });
      setState({...state, phoneNumberToggle: true});
      totalAttempts >= 3 && setState({...state, blockAttemp: true});
    };
    if (isSuccess) {
      storeData().then(r => r);
    }
  }, [isSuccess]);

  if (isLoading) {
    return <>loading...</>;
  }
  const checkDisableSignature = (disableSignature: any) => {
    return disableSignature ? <LockIcon /> : <SignatureIcon />;
  };
  return (
    <Stack
      sx={{
        width: '100%',
        height: '100vh',
      }}>
      <Stack sx={{height: '55px', p: '15px'}}>
        <img
          src={certignaLogo}
          style={{width: '120px', height: 30}}
          alt={'Logo'}
        />
      </Stack>
      <Stack
        sx={{
          width: '100%',
          height: `calc(100vh - 55px)`,
        }}>
        <Stack direction={'row'} sx={{alignItems: 'center', p: '16px'}}>
          <DocumentIcon />
          <Typography
            sx={{
              color: 'black',
              fontWeight: 600,
              fontFamily: 'Poppins',
              fontSize: '12px',
            }}>
            {data!.documents[0]?.name.length < 30
              ? data!.documents[0]?.name
              : `${data!.documents[0]?.name.substring(0, 30)} ...`}
            &nbsp;
          </Typography>
          <Typography
            component={'span'}
            sx={{fontFamily: 'Poppins', fontSize: '12px'}}>
            -&nbsp;{data!.documents[0]?.totalPages}
            {' pages'}
          </Typography>
        </Stack>
        {dataInfo && (
          <Stack height={`calc(100vh - 55px)`}>
            <PDFTronViewFileToSign
              file={`data:application/pdf;base64,${allFileBase64[0]}`}
              currentPage={currentPage}
              setCurrentPage={setCurrentPage}
              currentTotalPages={currentTotalPages}
              setCurrentTotalPages={setCurrentTotalPages}
              instance={instance}
              setInstance={setInstance}
              setDisableSignature={setDisableSignature}
            />
          </Stack>
        )}
        <Box
          sx={{
            justifyContent: 'center',
            alignItems: 'center',
          }}>
          <Stack
            direction={'row'}
            sx={{
              justifyContent: 'space-between',
              alignItems: 'center',
              width: '100%',
              height: '50px',
              bgcolor: '#121232',
              color: '#ffffff',
            }}>
            <Stack
              direction={'row'}
              alignItems={'center'}
              spacing={2}
              sx={{px: '5px'}}>
              <IconButton
                onClick={() =>
                  instance?.UI.setZoomLevel(instance.UI.getZoomLevel() - 0.1)
                }>
                <RemoveIcon sx={{color: '#ffffff'}} />
              </IconButton>
              <Typography>Zoom</Typography>
              <IconButton
                sx={{color: '#ffffff'}}
                onClick={() =>
                  instance?.UI.setZoomLevel(instance.UI.getZoomLevel() + 0.1)
                }>
                <AddIcon />
              </IconButton>
            </Stack>
            <Stack
              direction={'row'}
              alignItems={'center'}
              spacing={2}
              sx={{px: '20px'}}>
              <Typography component={'span'}>
                {`Page ${currentPage ?? '...'}/${currentTotalPages ?? '...'}`}
              </Typography>
            </Stack>
          </Stack>
          <Stack
            sx={{
              justifyContent: 'center',
              alignItems: 'center',
              width: '100%',
            }}>
            <Stack
              direction={'row'}
              sx={{
                p: '12px 20px',
                justifyContent: 'space-between',
                alignItems: 'center',
                height: '72px',
                width: '375px',
              }}>
              <Button
                disabled={
                  state.pageNum === 1 || currentTotalPages === 1
                    ? false
                    : disableSignature || state.validateLoading
                }
                onClick={() => setRefuseToggle(true)}
                variant="outlined"
                sx={{
                  width: '157px',
                  height: '48px',
                  '&.MuiButton-outlined': {
                    fontSize: '13px',
                    color: '#000000',
                    fontWeight: 600,
                    textTransform: 'capitalize',
                    borderColor: '#000000',
                  },
                  '&.Mui-disabled': {
                    bgcolor: '#6D676A',
                    color: '#ffffff',
                    borderStyle: 'none',
                  },
                }}>
                {t(Localization('invitation', 'refuse'))}
              </Button>

              <Button
                startIcon={
                  state.pageNum === 1 || currentTotalPages === 1
                    ? !individualSignLoading && <SignatureIcon />
                    : individualSignLoading
                    ? undefined
                    : checkDisableSignature(disableSignature)
                }
                disabled={
                  (state.pageNum === 1 || currentTotalPages === 1) &&
                  !individualSignLoading
                    ? false
                    : state.validateLoading ||
                      individualSignLoading ||
                      disableSignature
                }
                variant="contained"
                onClick={handleIndividualSign}
                sx={{
                  width: '157px',
                  height: '48px',
                  py: '12px',
                  bgcolor: 'Primary.main',
                  '&.MuiButton-contained': {
                    fontWeight: 600,
                    textTransform: 'capitalize',
                  },
                  '&.Mui-disabled': {
                    bgcolor: '#6D676A',
                    color: '#ffffff',
                  },
                }}>
                {individualSignLoading ? (
                  <CircularProgress color="inherit" size={'22px'} />
                ) : (
                  <> {t(Localization('invitation', 'sign'))}</>
                )}
              </Button>
            </Stack>
          </Stack>
        </Box>
      </Stack>

      {/* Confirm phone number dialog */}
      <ConfirmPhoneNumber
        phoneNumberToggle={state.phoneNumberToggle}
        handleChangeOtp={handleChangeOtp}
        otp={phoneOtp}
        phoneNumber={dataInfo?.phoneNumber.removedNumber ?? '....'}
        submitPhoneNumberOtp={submitPhoneNumberOtp}
        invalidPhoneMessage={invalidPhoneMessage}
        validateLoading={state.validateLoading}
      />
      <ConfirmOtpDialog
        setInvalidPhoneMessage={setInvalidPhoneMessage}
        setOtp={setOtp}
        otp={otp}
        state={state}
        setState={setState}
        handleOtpChange={handleOtpChange}
        invalidPhoneMessage={invalidPhoneMessage}
        submitConfirmOtp={submitConfirmOtp}
        fullPhoneNumber={fullPhoneNumber}
        handleGenerateOtp={handleGenerateOtp}
      />

      {state.blockAttemp && <BlockAttemps />}
      <SnackBarMui
        open={!!responseInfo}
        handleClose={handleClose}
        message={responseInfo?.message ?? ''}
        severity={responseInfo?.severity ?? 'info'}
      />
      <RefuseDialog
        refuseToggle={refuseToggle}
        textArea={textArea}
        setRefuseToggle={setRefuseToggle}
        onChangeTextArea={onChangeTextArea}
        refuseDocumentLoading={refuseDocumentLoading}
        submitRefuseDocument={submitRefuseDocument}
      />
    </Stack>
  );
};

export default PDFTronView;

type IConfirmPhoneNumber = {
  phoneNumberToggle: boolean;
  setPhoneNumberToggle?: React.Dispatch<React.SetStateAction<boolean>>;
  otp: string;
  handleChangeOtp: (value: string) => void;
  phoneNumber: string | number;
  submitPhoneNumberOtp: () => Promise<void>;
  invalidPhoneMessage: string;
  validateLoading: boolean;
};

type IConfirmOtp = {
  state: IViewFileSignState;
  setState: React.Dispatch<React.SetStateAction<IViewFileSignState>>;
  otp: string;
  handleOtpChange: (value: string) => void;
  submitConfirmOtp: () => Promise<void>;
  fullPhoneNumber: string;
  invalidPhoneMessage: string;
  setInvalidPhoneMessage: React.Dispatch<React.SetStateAction<string>>;
  setOtp: React.Dispatch<React.SetStateAction<string>>;
  handleGenerateOtp: any;
};

const BlockAttemps = () => {
  const {t} = useTranslation();
  return (
    <NGDialog
      maxWidth="xs"
      dialogAction={false}
      open={true}
      header={
        <Box sx={{position: 'relative'}}>
          <Stack alignItems={'center'} sx={{px: '1.2rem', pt: '3rem'}}>
            <Typography
              component={'div'}
              sx={{
                fontSize: '1.3rem',
                fontWeight: 600,
                textAlign: 'center',
              }}>
              {t(Localization('block-attempts', 'block-title'))}
            </Typography>
          </Stack>
        </Box>
      }
      body={
        <Box sx={{position: 'relative'}}>
          <Stack
            alignItems={'center'}
            sx={{px: '1.2rem', pb: '6rem', pt: '2rem'}}>
            <Typography
              component={'h3'}
              sx={{textAlign: 'center', fontSize: '0.9rem'}}>
              {t(Localization('block-attempts', 'unable-to-sign'))}{' '}
            </Typography>
          </Stack>
        </Box>
      }
    />
  );
};

const ConfirmOtpDialog = ({
  state,
  setState,
  otp,
  handleOtpChange,
  submitConfirmOtp,
  invalidPhoneMessage,
  setInvalidPhoneMessage,
  fullPhoneNumber,
  setOtp,
  handleGenerateOtp,
}: IConfirmOtp) => {
  const {theme} = useAppSelector(state => state.enterprise);
  const {t} = useTranslation();
  return (
    <NGDialog
      open={state.otpToggle}
      width="327px"
      height="440px"
      // sx={{bgcolor: 'red', width: '300px'}}
      header={
        <Box sx={{position: 'relative'}}>
          <NGText
            text={''}
            multiLine={true}
            dataMulti={[
              t(Localization('opt-dialog', 'confirm')),
              t(Localization('opt-dialog', 'your-identity')),
            ]}
            propsChildMulti={{
              color: 'black.main',
              fontWeight: 600,
              textAlign: 'center',
              width: '100%',
              fontSize: pixelToRem(22),
            }}
          />
          <IconButton
            sx={{position: 'absolute', top: -15, right: -10}}
            onClick={() => {
              setInvalidPhoneMessage('');
              setOtp('');
              setState({...state, otpToggle: false});
            }}>
            <NGFalse sx={{fontSize: [12, 14, 16], color: 'Primary.main'}} />
          </IconButton>
        </Box>
      }
      body={
        <Stack alignItems="center" mt={2}>
          <Typography
            sx={{
              width: '270px',
              textAlign: 'center',
            }}>
            <NGText
              text={t(Localization('opt-dialog', 'enter-security-code'))}
              myStyle={{
                textAlign: 'center',
                fontSize: pixelToRem(14),
                fontWeight: 400,
              }}
            />
            <NGText
              text={' ' + fullPhoneNumber + ' '}
              myStyle={{
                textAlign: 'center',
                fontSize: pixelToRem(14),
                fontWeight: 600,
              }}
            />
            <NGText
              text={t(Localization('opt-dialog', 'document-signature'))}
              myStyle={{
                textAlign: 'center',
                fontSize: pixelToRem(14),
                fontWeight: 400,
              }}
            />
          </Typography>
          <Center
            sx={{width: ['100%', '80%']}}
            mt={invalidPhoneMessage ? 1 : 2}>
            <NGText
              text={invalidPhoneMessage}
              sx={{color: 'red', mb: 1, fontSize: '14px'}}
            />
            <MuiOtpInput
              value={otp}
              onChange={handleOtpChange}
              inputMode="numeric"
              length={OtpLength}
              sx={{
                width: '263px',
                gap: 0.5,
                ...StyleConstant.muiInputOtp,
                '& .MuiOutlinedInput-notchedOutline ': {
                  borderColor:
                    otp.length == OtpLength ? theme[0].mainColor : 'black.main',
                },
              }}
              TextFieldsProps={{
                size: 'small',
                placeholder: '__',
              }}
            />

            <NGButton
              onClick={async () => submitConfirmOtp()}
              locationIcon="end"
              icon={
                state.validateLoading && (
                  <CircularProgress sx={{color: '#ffffff'}} size={'1.5rem'} />
                )
              }
              title={!state.validateLoading && 'Valider'}
              color={['bg.main', 'white']}
              disabled={otp?.length !== OtpLength || state.validateLoading}
              myStyle={{
                width: '263px',
                height: '48px',
                borderRadius: '6px',
                my: 2,
                '&.Mui-disabled': {
                  bgcolor: colorDisable,
                  color: colorWhite,
                },
              }}
            />
          </Center>
          <Stack mt={1} width={'100%'}>
            <NGText
              text={t(Localization('opt-dialog', 'code-received'))}
              myStyle={{
                width: '120px',
                fontFamily: 'Poppins',
                fontStyle: 'normal',
                fontWeight: 400,
                fontSize: '14px',
                lineHeight: '22px',
                textAlign: 'center',
                color: ' #000000',
              }}
              iconStart={<NGAlert sx={{fontSize: 20}} />}
              styleTextHaveIcon={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            />
            <NGText
              text={t(Localization('opt-dialog', 'otp-code'))}
              onClick={handleGenerateOtp}
              myStyle={{
                fontFamily: 'Poppins',
                fontStyle: 'normal',
                fontWeight: 600,
                fontSize: '14px',
                lineHeight: '20px',
                textAlign: 'center',
                textDecoration: 'underline',
                color: ' #000000',
              }}
            />
          </Stack>
        </Stack>
      }
    />
  );
};
const ConfirmPhoneNumber = ({
  phoneNumberToggle,
  handleChangeOtp,
  otp,
  phoneNumber,
  submitPhoneNumberOtp,
  invalidPhoneMessage,
  validateLoading,
}: IConfirmPhoneNumber) => {
  const {t} = useTranslation();
  const {theme} = useAppSelector(state => state.enterprise);
  return (
    <NGDialog
      width="327px"
      height="343px"
      dialogAction={false}
      open={phoneNumberToggle}
      // setOpen={setPhoneNumberToggle}
      header={
        <Box sx={{position: 'relative'}}>
          <Stack
            alignItems={'center'}
            spacing={1}
            // sx={{px: pixelToRem(32), pt: pixelToRem(24)}}
          >
            <NGText
              text={t(
                Localization(
                  'confirm-phone-number-stage',
                  'confirm-your-phone-number',
                ),
              )}
              myStyle={{
                width: '263px',
                fontSize: pixelToRem(22),
                fontWeight: 600,
                textAlign: 'center',
              }}
            />
            <Typography
              sx={{
                width: '263px',
                textAlign: 'center',
              }}>
              <NGText
                text={
                  t(
                    Localization(
                      'confirm-phone-number-stage',
                      'fill-four-digits-number',
                    ),
                  ) + ' '
                }
                myStyle={{
                  textAlign: 'center',
                  fontSize: pixelToRem(14),
                  fontWeight: 400,
                }}
              />
              <NGText
                text={phoneNumber + '...'}
                myStyle={{
                  textAlign: 'center',
                  fontSize: pixelToRem(14),
                  fontWeight: 600,
                }}
              />
            </Typography>

            <IconButton sx={{position: 'absolute', top: -15, right: -10}}>
              <NGFalse sx={{fontSize: [12, 14, 16], color: 'Primary.main'}} />
            </IconButton>
          </Stack>
        </Box>
      }
      body={
        <Stack spacing={2} mt={2}>
          <Center>
            <Stack
              sx={{width: '263px'}}
              justifyContent={'center'}
              alignItems={'center'}>
              <MuiOtpInput
                onKeyPress={(e: any) => {
                  if (!/\d/.test(e.nativeEvent.key)) {
                    return e.preventDefault();
                  }
                }}
                inputMode="numeric"
                value={otp}
                onChange={handleChangeOtp}
                length={trailPhoneLength}
                sx={{
                  mt: 1,
                  gap: 1,
                  borderColor: 'red',
                  ...StyleConstant.muiInputOtpTelephone,
                  '& .MuiOutlinedInput-notchedOutline ': {
                    borderColor:
                      otp.length == 4 ? theme[0].mainColor : 'black.main',
                  },
                }}
                TextFieldsProps={{
                  sx: {
                    ':focus': {
                      color: 'red',
                      outlineStyle: 'none',
                      borderWidth: '1px',
                      borderRadius: '10px',
                      borderStyle: 'solid',
                      borderColor: 'red',
                    },
                    ':focus-visible': {
                      color: 'red',
                      outlineStyle: 'none',
                      borderWidth: '1px',
                      borderRadius: '10px',
                      borderStyle: 'solid',
                      borderColor: 'red',
                    },
                  },
                  size: 'small',
                  placeholder: '___',
                }}
              />
              <Typography sx={{pt: '1rem', color: 'red', fontSize: 14}}>
                {invalidPhoneMessage}
              </Typography>
              <NGButton
                disabled={otp?.length !== trailPhoneLength || validateLoading}
                locationIcon="end"
                icon={
                  validateLoading && (
                    <CircularProgress sx={{color: '#ffffff'}} size={'1.5rem'} />
                  )
                }
                title={!validateLoading && 'Valider'}
                onClick={async () => submitPhoneNumberOtp()}
                myStyle={{
                  fontSize: '13px',
                  mt: 1,
                  width: '261px',
                  height: '48px',
                  borderRadius: '6px',
                  '&.Mui-disabled': {
                    bgcolor: colorDisable,
                    color: colorWhite,
                  },
                }}
              />
            </Stack>
          </Center>
        </Stack>
      }
    />
  );
};

type IRefuseDialog = {
  refuseToggle: boolean;
  setRefuseToggle: React.Dispatch<React.SetStateAction<boolean>>;
  textArea: string;
  onChangeTextArea: (e: ChangeEvent<HTMLTextAreaElement>) => void;
  refuseDocumentLoading: boolean;
  submitRefuseDocument: () => Promise<unknown>;
};

const RefuseDialog = (props: IRefuseDialog) => {
  const {
    refuseToggle,
    setRefuseToggle,
    textArea,
    onChangeTextArea,
    refuseDocumentLoading,
    submitRefuseDocument,
  } = props;
  const {t} = useTranslation();
  return (
    <NGDialog
      width="327px"
      height="403px"
      style={{
        p: 0,
      }}
      open={refuseToggle}
      header={
        <Box sx={{position: 'relative'}}>
          <IconButton
            sx={{position: 'absolute', top: -15, right: -10}}
            onClick={() => {
              setRefuseToggle(false);
            }}>
            <NGFalse sx={{fontSize: 10, color: 'Primary.main'}} />
          </IconButton>
        </Box>
      }
      body={
        <Stack width="100%" alignItems="center" gap="24px" px="16px">
          <Stack gap="9px">
            <NGText
              text={t(Localization('refuse-document', 'refuse-to-sign'))}
              myStyle={{
                width: '263px',
                height: '32px',
                fontSize: '22px',
                fontWeight: 600,
                textAlign: 'center',
              }}
            />
            <NGText
              text={t(Localization('refuse-document', 'reason'))}
              myStyle={{
                width: '263px',
                height: '48px',
                fontSize: '14px',
                textAlign: 'center',
              }}
            />
          </Stack>
          <TextField
            sx={{
              '& .MuiOutlinedInput-root': {
                p: 1,
              },
            }}
            required
            fullWidth
            placeholder={t(Localization('refuse-document', 'input'))!}
            multiline
            rows={4}
            maxRows={4}
            value={textArea}
            onChange={onChangeTextArea}
          />
          <Stack gap="10px" width="100%">
            <NGButton
              onClick={submitRefuseDocument}
              locationIcon="end"
              icon={
                refuseDocumentLoading && (
                  <CircularProgress sx={{color: '#ffffff'}} size={'1.5rem'} />
                )
              }
              title={!refuseDocumentLoading && t(Localization('form', 'Send'))}
              disabled={
                textArea.length < 50 ||
                textArea.length > 100 ||
                refuseDocumentLoading
              }
              myStyle={{
                width: '100%',
                height: '48px',
                py: pixelToRem(12),
                px: pixelToRem(16),
                borderRadius: '6px',
                '&.Mui-disabled': {
                  bgcolor: colorDisable,
                  color: colorWhite,
                },
              }}
            />
            <Button
              onClick={() => setRefuseToggle(false)}
              fullWidth
              variant="outlined"
              sx={{
                borderRadius: '6px',
                height: '48px',
                '&.MuiButton-outlined': {
                  fontSize: '13px',
                  color: '#000000',
                  fontWeight: 600,
                  textTransform: 'capitalize',
                  borderColor: '#000000',
                },
                '&.Mui-disabled': {
                  bgcolor: '#6D676A',
                  color: '#ffffff',
                },
              }}>
              {t(Localization('upload-document', 'cancel'))}
            </Button>
          </Stack>
        </Stack>
      }
      footer={
        <Stack
          alignItems={'center'}
          justifyContent={'center'}
          width={'100%'}></Stack>
      }
    />
  );
};
