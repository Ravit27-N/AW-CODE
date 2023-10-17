import {NGAlert} from '@/assets/Icon';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {
  FONT_TYPE,
  OtpLength,
  UNKOWNERROR,
  trailPhoneLength,
} from '@/constant/NGContant';

import {Localization} from '@/i18n/lan';
import PDFTronViewFileToSign from '@/pages/participant/signatory/PDFTronViewFleToSign';
import {
  useIndividualSignMutation,
  useRefuseDocumentMutation,
  useSetupIndividualMutation,
  useSignDocumentMutation,
  useValidateOTPMutation,
  useValidatePhoneNumberMutation,
} from '@/redux/slides/process-control/internal/processControlSlide';

import {IGetFlowId} from '@/redux/slides/project-management/project';
import {HandleException} from '@/utils/common/HandleException';
import ClearIcon from '@mui/icons-material/Clear';
import {
  Backdrop,
  Button,
  CircularProgress,
  IconButton,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {WebViewerInstance} from '@pdftron/webviewer';
import {SerializedError} from '@reduxjs/toolkit';
import {FetchBaseQueryError} from '@reduxjs/toolkit/dist/query';
import {t} from 'i18next';
import {MuiOtpInput} from 'mui-one-time-password-input';
import {enqueueSnackbar} from 'notistack';
import React from 'react';
import {AssignedProjectEndUserType} from '../../project-card';
import SignDocument from '../sign/SignDocument';

type IViewFileToSign = {
  storeData: AssignedProjectEndUserType;
  file: string | null;
  projectFlowId: IGetFlowId;
};

const ViewFileToSignIndividual = (props: IViewFileToSign) => {
  const {storeData, file, projectFlowId} = props;
  const [togglePhone, setTogglePhone] = React.useState<boolean>(true);
  const [toggleOTP, setToggleOTP] = React.useState<boolean>(false);
  const [toggleRefuse, setToggleRefuse] = React.useState<boolean>(false);
  const [pageNum, setPageNum] = React.useState<number | null>(0);
  const [signEnable, setSignEnable] = React.useState<boolean>(false);
  const [currPage, setCurrPage] = React.useState<number | null>(1);
  const [instance, setInstance] = React.useState<WebViewerInstance | null>(
    null,
  );
  const [phoneNumber, setPhoneNumber] = React.useState<string>('');
  const [textArea, setTextarea] = React.useState<string>('');
  const [otp, setOtp] = React.useState<string>('');
  const [validatePhoneNumber, {isLoading: phoneLoading}] =
    useValidatePhoneNumberMutation();

  const [
    validateOtp,
    {isLoading: validateOtpLoading, error, data: validateOtpData},
  ] = useValidateOTPMutation();
  const [signDocument, {isLoading: signDocLoading, reset}] =
    useSignDocumentMutation();
  const [refuseDoc, {isLoading: refuseDocLoading}] =
    useRefuseDocumentMutation();
  const [individualUpload, {isLoading: individualUploadDocLoading}] =
    useIndividualSignMutation();
  const [setUpIndividual, {isLoading: setUpIndividualLoading}] =
    useSetupIndividualMutation();

  const {phoneNumber: infoPhoneNumber} = projectFlowId;
  const [readyPdf, setReadyPdf] = React.useState<boolean>(false);

  const handleOtpChange = (value: string): void => {
    setOtp(value);
  };
  const handlePhoneNumberChange = (value: string): void => {
    setPhoneNumber(value);
  };
  const handleToggleRefuse = () => {
    setToggleRefuse(!toggleRefuse);
  };

  const handleChangeTextArea = (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => {
    setTextarea(e.target.value);
  };

  const handleCloseOtp = () => {
    setToggleOTP(false);
  };

  const handleValidateOtp = async (): Promise<void> => {
    try {
      await validateOtp({
        flowId: storeData.flowId,
        uuid: storeData.uuid,
        otp,
      }).unwrap();
      setToggleOTP(false);
    } catch (error) {
      /** empty */
      setOtp('');
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  const submitRefuseDocument = async (): Promise<void> => {
    try {
      await refuseDoc({
        comment: textArea,
        flowId: storeData.flowId,
        uuid: storeData.uuid,
      }).unwrap();
      setToggleRefuse(false);
      setTextarea('');
      window.history.go(0);
    } catch (error) {
      /* empty */
      setTextarea('');
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  const submitValidatePhoneNumber = async (): Promise<void> => {
    try {
      await validatePhoneNumber({
        flowId: storeData.flowId,
        uuid: storeData.uuid,
        phone: phoneNumber,
      }).unwrap();
      setTogglePhone(false);
      setPhoneNumber('');
    } catch (error) {
      /* empty */
      setPhoneNumber('');
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  const handleSetUpIndividual = async () => {
    try {
      await setUpIndividual({
        flowId: storeData.flowId,
        uuid: storeData.uuid,
      }).unwrap();
    } catch (e) {
      /** empty */
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  const generateOTP = async (): Promise<void> => {
    const xfdfString =
      await instance?.Core.annotationManager.exportAnnotations();
    const annotLists = instance?.Core.annotationManager.getAnnotationsList();
    const file = await instance?.Core.documentViewer
      .getDocument()
      .getFileData({xfdfString})!;
    const arr = new Uint8Array(file);
    const blob = new Blob([arr], {type: 'application/pdf'});
    const {docId} = projectFlowId.documents[0];
    const formData = new FormData();
    formData.append('file', blob);

    if (!annotLists!.length) {
      handleSetUpIndividual();
      return setToggleOTP(!toggleOTP);
    }
    try {
      await individualUpload({
        body: formData,
        flowId: storeData.flowId,
        docId,
      }).unwrap();

      await handleSetUpIndividual();
      setToggleOTP(!toggleOTP);
    } catch (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  const submitSignDocument = async (): Promise<void> => {
    try {
      await signDocument({
        flowId: storeData.flowId,
        uuid: storeData.uuid,
      }).unwrap();
      reset();
    } catch (error) {
      /** empty */
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  React.useEffect(() => {
    if (currPage === pageNum) {
      setSignEnable(true);
    }
  }, [currPage, pageNum]);

  React.useEffect(() => {
    if (infoPhoneNumber.totalAttempts >= 3) {
      setTogglePhone(false);
    } else {
      setTogglePhone(true);
    }
  }, [infoPhoneNumber.totalAttempts]);

  React.useEffect(() => {
    if (otp.length === OtpLength) {
      handleValidateOtp();
    }
  }, [otp.length === OtpLength]);

  return (
    <Stack border="1.5px solid #E9E9E9" height="100%">
      <Stack>
        {infoPhoneNumber.totalAttempts >= 3 ? (
          <Stack
            alignItems="center"
            height={`calc(100vh - 280px)`}
            justifyContent="center">
            <BlockAttempt />
          </Stack>
        ) : (
          <>
            <Header
              signEnable={signEnable}
              storeData={storeData}
              projectFlowId={projectFlowId}
              handleToggleOtp={generateOTP}
              refuseToggle={handleToggleRefuse}
            />
            {file && (
              <PDFTronViewFileToSign
                file={`data:application/pdf;base64,${file}`}
                currentPage={currPage}
                setCurrentPage={setCurrPage}
                currentTotalPages={pageNum}
                setCurrentTotalPages={setPageNum}
                instance={instance}
                setInstance={setInstance}
                setDisableSignature={setSignEnable}
                setReadyPdf={setReadyPdf}
              />
            )}
          </>
        )}
      </Stack>
      {readyPdf && (
        <ValidationPhoneNumber
          phoneNumber={infoPhoneNumber.removedNumber}
          handleChange={handlePhoneNumberChange}
          otp={phoneNumber}
          info={infoPhoneNumber}
          submit={submitValidatePhoneNumber}
          setTogglePhone={setTogglePhone}
          togglePhone={togglePhone}
          setPhoneNumber={setPhoneNumber}
          setReadyPdf={setReadyPdf}
        />
      )}

      <RefuseDocument
        open={toggleRefuse}
        handleClose={handleToggleRefuse}
        onChange={handleChangeTextArea}
        textArea={textArea}
        submit={submitRefuseDocument}
      />

      <ValidationOTP
        phoneNumber={projectFlowId.phoneNumber.number}
        handleChange={handleOtpChange}
        otp={otp}
        toggleOtp={toggleOTP}
        handleToggleOtp={handleCloseOtp}
        error={error}
      />
      <SignDocument
        open={validateOtpData ?? false}
        projectFlowId={projectFlowId}
        submit={submitSignDocument}
      />
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.modal + 1}}
        open={
          phoneLoading ||
          validateOtpLoading ||
          signDocLoading ||
          refuseDocLoading ||
          individualUploadDocLoading ||
          setUpIndividualLoading
        }>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

type IHeader = {
  signEnable: boolean;
  storeData: AssignedProjectEndUserType;
  handleToggleOtp: () => void;
  refuseToggle: () => void;
  projectFlowId: IGetFlowId;
};

const Header = (props: IHeader) => {
  const {signEnable, storeData, handleToggleOtp, refuseToggle, projectFlowId} =
    props;
  return (
    <Stack
      direction="row"
      p="12px 24px"
      justifyContent="space-between"
      borderBottom="1.5px solid #E9E9E9">
      <Typography
        sx={{
          fontWeight: 600,
          fontSize: '18px',
          fontFamily: FONT_TYPE.POPPINS,
        }}>
        {storeData.title}
      </Typography>
      <Stack direction="row" gap="12px">
        <Button
          onClick={refuseToggle}
          disabled={!signEnable || !projectFlowId.phoneNumber.validated}
          variant="outlined"
          sx={{
            fontSize: '11px',
            fontWeight: 600,
            fontFamily: FONT_TYPE.POPPINS,
            textTransform: 'none',
            borderColor: '#000000',
            color: '#000000',
            '&:hover': {
              color: 'Primary.main',
            },
          }}>
          {t(Localization('invitation', 'refuse'))}
        </Button>
        <Button
          disabled={!signEnable || !projectFlowId.phoneNumber.validated}
          onClick={() => {
            handleToggleOtp();
          }}
          variant="contained"
          sx={{
            fontSize: '11px',
            fontWeight: 600,
            fontFamily: FONT_TYPE.POPPINS,
            textTransform: 'none',
          }}>
          {t(Localization('end-user-assigned-project', 'sign-the-doc'))}
        </Button>
      </Stack>
    </Stack>
  );
};

type IValidationOTP = {
  phoneNumber: string;
  otp: string;
  handleChange: (value: string) => void;
  toggleOtp: boolean;
  handleToggleOtp: () => void;
  error: FetchBaseQueryError | SerializedError | undefined;
};

export const ValidationOTP = (props: IValidationOTP) => {
  const {phoneNumber, otp, handleChange, toggleOtp, handleToggleOtp, error} =
    props;
  return (
    <NGDialog
      open={toggleOtp}
      maxWidth="xl"
      sx={{
        '& .MuiPaper-root': {
          boxSizing: 'border-box',
          borderRadius: '16px',
        },
      }}
      sxProp={{
        titleSx: {
          width: '548px',
          p: 0,
        },
        contentsSx: {
          p: 0,
        },
      }}
      titleDialog={
        <Stack alignItems="flex-end" p="20px 15px 0 20px">
          <IconButton
            disableFocusRipple
            disableRipple
            disableTouchRipple
            onClick={handleToggleOtp}>
            <ClearIcon sx={{color: 'Primary.main'}} />
          </IconButton>
        </Stack>
      }
      contentDialog={
        <Stack p="10px 33px 73px 33px" alignItems="center" gap="20px">
          <Stack alignItems="center" width="483px" gap="8px">
            <Typography
              sx={{
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 22,
                fontWeight: 600,
              }}>
              {t(Localization('end-user-assigned-project', 'confirm-you-id'))}
            </Typography>
            <Typography
              sx={{
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 14,
                textAlign: 'center',
              }}>
              {t(Localization('end-user-assigned-project', 'enter-security'))}
              <Typography
                component="span"
                display="inline-flex"
                sx={{
                  fontFamily: FONT_TYPE.POPPINS,
                  fontSize: 14,
                  fontWeight: 600,
                }}>
                {phoneNumber}
              </Typography>
              {' ' +
                t(Localization('end-user-assigned-project', 'validate-doc'))}
            </Typography>
          </Stack>
          <MuiOtpInput
            value={otp}
            onChange={handleChange}
            inputMode="decimal"
            length={OtpLength}
            sx={{
              width: '263px',
              gap: 0.5,
              '& .MuiInputBase-input.MuiOutlinedInput-input': {
                height: '55px',
                width: '40px',
                p: 0,
              },
              '& .MuiOutlinedInput-notchedOutline ': {
                borderColor:
                  otp.length === OtpLength ? 'Primary.main' : '#BABABA', // set your desired color here
                borderWidth: '0.2px',
                '&:focus': {
                  borderColor: 'red',
                },
              },
            }}
            TextFieldsProps={{
              size: 'small',
              placeholder: '_',
            }}
          />
          {error && (
            <Typography
              sx={{
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 14,
                color: 'red',
              }}>
              OTP is incorrect
            </Typography>
          )}
          <Stack alignItems="center">
            <Stack direction="row">
              <NGAlert sx={{fontSize: 20, color: 'Primary.main'}} />
              <Typography
                sx={{
                  fontFamily: FONT_TYPE.POPPINS,
                  fontSize: 14,
                }}>
                {t(Localization('opt-dialog', 'code-received'))}
              </Typography>
            </Stack>
            <Typography
              sx={{
                fontSize: '14px',
                fontFamily: FONT_TYPE.POPPINS,
                textUnderlineOffset: 1,
                fontWeight: 600,
              }}>
              {t(Localization('opt-dialog', 'otp-code'))}
            </Typography>
          </Stack>
        </Stack>
      }
    />
  );
};

type IValidationPhoneNumber = {
  phoneNumber: string;
  otp: string;
  handleChange: (value: string) => void;
  submit: () => void;
  setTogglePhone: React.Dispatch<React.SetStateAction<boolean>>;
  togglePhone: boolean;
  info: {
    removedNumber: string;
    missingLength: number;
    totalAttempts: number;
    number: string;
    validated: boolean;
  };
  setPhoneNumber: React.Dispatch<React.SetStateAction<string>>;
  setReadyPdf: React.Dispatch<React.SetStateAction<boolean>>;
};

export const ValidationPhoneNumber = (props: IValidationPhoneNumber) => {
  const {
    phoneNumber,
    otp,
    handleChange,
    info,
    submit,
    setTogglePhone,
    togglePhone,
    setPhoneNumber,
    setReadyPdf,
  } = props;
  return (
    <NGDialog
      open={togglePhone}
      maxWidth="xl"
      sx={{
        '& .MuiPaper-root': {
          boxSizing: 'border-box',
          borderRadius: '16px',
        },
      }}
      sxProp={{
        titleSx: {
          width: '548px',
          p: 0,
        },
        contentsSx: {
          p: 0,
        },
      }}
      titleDialog={
        <Stack alignItems="flex-end" p="20px 15px 0 20px">
          <IconButton
            disableFocusRipple
            disableRipple
            disableTouchRipple
            onClick={() => {
              setTogglePhone(false);
              setPhoneNumber('');
              setReadyPdf(false);
            }}>
            <ClearIcon sx={{color: 'Primary.main'}} />
          </IconButton>
        </Stack>
      }
      contentDialog={
        <Stack p="10px 33px 53px 33px" alignItems="center" gap="20px">
          <Stack alignItems="center" width="483px" gap="8px">
            <Typography
              sx={{
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 22,
                fontWeight: 600,
              }}>
              {t(
                Localization(
                  'confirm-phone-number-stage',
                  'confirm-your-phone-number',
                ),
              )}
            </Typography>
            <Typography
              sx={{
                width: '400px',
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 14,
                textAlign: 'center',
              }}>
              {t(
                Localization(
                  'confirm-phone-number-stage',
                  'fill-four-digits-number',
                ),
              ) + ' '}
              <Typography
                component="span"
                display="inline-flex"
                sx={{
                  fontFamily: FONT_TYPE.POPPINS,
                  fontSize: 14,
                  fontWeight: 600,
                }}>
                {phoneNumber + '...'}
              </Typography>
            </Typography>
          </Stack>
          <MuiOtpInput
            value={otp}
            onChange={handleChange}
            inputMode="decimal"
            length={trailPhoneLength}
            sx={{
              width: '250px',
              gap: 1,
              '& .MuiInputBase-input.MuiOutlinedInput-input': {
                height: '55px',
                width: '55px',
                p: 0,
              },
              '& .MuiOutlinedInput-notchedOutline ': {
                borderColor:
                  otp.length === trailPhoneLength ? 'Primary.main' : '#BABABA', // set your desired color here
                borderWidth: '0.2px',
                '&:focus': {
                  borderColor: 'red',
                },
              },
            }}
            TextFieldsProps={{
              size: 'small',
              placeholder: '_',
            }}
          />
          {info.totalAttempts > 0 && !info.validated && (
            <Typography
              sx={{
                color: 'red',
                fontSize: 14,
                fontFamily: FONT_TYPE.POPPINS,
              }}>
              {t(Localization('confirm-phone-number-stage', 'wrong-number')) +
                t(Localization('confirm-phone-number-stage', 'try-left')) +
                ' ' +
                (3 - info.totalAttempts) +
                ' ' +
                t(Localization('confirm-phone-number-stage', 'left')) +
                '(s)'}
            </Typography>
          )}

          <Stack alignItems="center" width="60%">
            <Button
              disabled={otp.length !== 4}
              fullWidth
              onClick={submit}
              variant="contained"
              sx={{
                fontSize: 14,
                p: '10px 20px',
                fontFamily: FONT_TYPE.POPPINS,
                textTransform: 'none',
              }}>
              {t(Localization('title', 'validate'))}
            </Button>
          </Stack>
        </Stack>
      }
    />
  );
};

export const BlockAttempt = () => {
  return (
    <Stack p="53px 33px 53px 33px" alignItems="center" gap="20px">
      <Stack alignItems="center" width="483px" gap="8px">
        <Typography
          sx={{
            fontFamily: FONT_TYPE.POPPINS,
            fontSize: 22,
            fontWeight: 600,
            textAlign: 'center',
          }}>
          {t(Localization('block-attempts', 'block-title'))}
        </Typography>
        <Typography
          sx={{
            width: '400px',
            fontFamily: FONT_TYPE.POPPINS,
            fontSize: 14,
            textAlign: 'center',
          }}>
          {t(Localization('block-attempts', 'unable-to-sign'))}
        </Typography>
      </Stack>
    </Stack>
  );
};

export type IRefuseDocument = {
  open: boolean;
  handleClose: () => void;
  textArea: string;
  onChange: (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => void;
  submit: () => void;
};

export const RefuseDocument = (props: IRefuseDocument) => {
  const {open, handleClose, textArea, onChange, submit} = props;
  return (
    <NGDialog
      open={open}
      maxWidth="xl"
      sx={{
        '& .MuiPaper-root': {
          boxSizing: 'border-box',
          borderRadius: '16px',
        },
      }}
      sxProp={{
        titleSx: {
          width: '548px',
          p: 0,
        },
        contentsSx: {
          p: 0,
        },
      }}
      titleDialog={
        <Stack alignItems="flex-end" p="20px 15px 0 20px">
          <IconButton
            disableFocusRipple
            disableRipple
            disableTouchRipple
            onClick={handleClose}>
            <ClearIcon sx={{color: 'Primary.main'}} />
          </IconButton>
        </Stack>
      }
      contentDialog={
        <Stack p="5px 53px 53px 53px" alignItems="center" gap="20px">
          <Stack alignItems="center" gap="8px">
            <Typography
              sx={{
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 22,
                fontWeight: 600,
              }}>
              {t(Localization('refuse-document', 'refuse-to-sign'))}
            </Typography>
            <Typography
              sx={{
                width: '400px',
                fontFamily: FONT_TYPE.POPPINS,
                fontSize: 14,
                textAlign: 'center',
              }}>
              {t(Localization('refuse-document', 'reason'))}
            </Typography>
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
            rows={6}
            value={textArea}
            onChange={onChange}
          />

          <Stack alignItems="center" width="60%" gap="10px">
            <Button
              disabled={textArea.length < 50 || textArea.length > 100}
              fullWidth
              onClick={submit}
              variant="contained"
              sx={{
                fontSize: 14,
                p: '10px 20px',
                fontFamily: FONT_TYPE.POPPINS,
                textTransform: 'none',
              }}>
              {t(Localization('title', 'validate'))}
            </Button>

            <Button
              onClick={handleClose}
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
    />
  );
};

export default ViewFileToSignIndividual;
