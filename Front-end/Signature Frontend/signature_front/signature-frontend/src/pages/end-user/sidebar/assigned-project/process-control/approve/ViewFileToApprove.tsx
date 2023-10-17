import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {FONT_TYPE, UNKOWNERROR, trailPhoneLength} from '@/constant/NGContant';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {
  useApproveDocumentMutation,
  useGenerateOTPMutation,
} from '@/redux/slides/process-control/internal/processControlSlide';
import {useRefuseDocumentMutation} from '@/redux/slides/process-control/participant';
import {IGetFlowId} from '@/redux/slides/project-management/project';
import {HandleException} from '@/utils/common/HandleException';
import ClearIcon from '@mui/icons-material/Clear';
import {
  Backdrop,
  Button,
  CircularProgress,
  IconButton,
  List,
  ListItemButton,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {useIntersectionObserver} from '@wojtekmaj/react-hooks';
import {t} from 'i18next';
import {MuiOtpInput} from 'mui-one-time-password-input';
import {enqueueSnackbar} from 'notistack';
import type {PDFDocumentProxy} from 'pdfjs-dist';
import React from 'react';
import {Document, Page, pdfjs} from 'react-pdf/dist/esm/entry.vite';
import {AssignedProjectEndUserType} from '../../project-card';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;
const observerConfig = {
  // How much of the page needs to be visible to consider page visible
  threshold: 0,
};

type IViewFileToSign = {
  storeData: AssignedProjectEndUserType;
  file: string | null;
  projectFlowId: IGetFlowId;
};

const ViewFileToApprove = (props: IViewFileToSign) => {
  const {storeData, file, projectFlowId} = props;
  const [toggleRefuse, setToggleRefuse] = React.useState<boolean>(false);
  const [pageNum, setPageNum] = React.useState<number>(0);
  const [signEnable, setSignEnable] = React.useState<boolean>(false);
  const [currPage, setCurrPage] = React.useState<number>(1);
  const [zoom, setZoom] = React.useState(2);
  const [visiblePages, setVisiblePages] = React.useState<
    Record<string, unknown>
  >({});
  const [, setReadyPdf] = React.useState<boolean>(false);
  const [textArea, setTextarea] = React.useState<string>('');
  const [generateOtp, {isLoading: generateLoading}] = useGenerateOTPMutation();
  const [approveDoc, {isLoading: approveDocLoading}] =
    useApproveDocumentMutation();
  const [refuseDoc, {isLoading: refuseDocLoading}] =
    useRefuseDocumentMutation();

  const handleToggleRefuse = () => {
    setToggleRefuse(!toggleRefuse);
  };

  const handleChangeTextArea = (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => {
    setTextarea(e.target.value);
  };

  const submitApproveDocument = async (): Promise<void> => {
    try {
      await generateOtp({
        flowId: storeData.flowId,
        uuid: storeData.uuid,
      }).unwrap();
      await approveDoc({
        flowId: storeData.flowId,
        uuid: storeData.uuid,
      }).unwrap();
    } catch (error) {
      /* empty */
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

  React.useEffect(() => {
    if (currPage === pageNum) {
      setSignEnable(true);
    }
  }, [currPage, pageNum]);

  React.useEffect(() => {
    Object.entries(visiblePages).forEach(
      ([key, value]) => value === true && setCurrPage(Number(key)),
    );
  }, [visiblePages]);

  return (
    <Stack border="1.5px solid #E9E9E9" height="100%">
      <Stack>
        <>
          <Header
            signEnable={signEnable}
            storeData={storeData}
            submit={submitApproveDocument}
            refuseToggle={handleToggleRefuse}
            projectFlowId={projectFlowId}
          />
          {file && (
            <ViewPdf
              file={file}
              pageNum={pageNum}
              setPageNum={setPageNum}
              zoom={zoom}
              setZoom={setZoom}
              setVisiblePages={setVisiblePages}
              setReadyPdf={setReadyPdf}
            />
          )}
        </>
      </Stack>
      <RefuseDocument
        open={toggleRefuse}
        handleClose={handleToggleRefuse}
        onChange={handleChangeTextArea}
        textArea={textArea}
        submit={submitRefuseDocument}
      />

      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.modal + 1}}
        open={approveDocLoading || generateLoading || refuseDocLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

type IViewPdf = {
  file: string | null;
  pageNum: number;
  setPageNum: React.Dispatch<React.SetStateAction<number>>;
  setVisiblePages: React.Dispatch<
    React.SetStateAction<Record<string, unknown>>
  >;
  setZoom: React.Dispatch<React.SetStateAction<number>>;
  zoom: number;
  setReadyPdf: React.Dispatch<React.SetStateAction<boolean>>;
};

const ViewPdf = (props: IViewPdf) => {
  const {pageNum, setPageNum, setVisiblePages, zoom, file, setReadyPdf} = props;
  const setPageVisibility = React.useCallback(
    (pageNumber: any, isIntersecting: any) => {
      setVisiblePages(prevVisiblePages => ({
        ...prevVisiblePages,
        [pageNumber]: isIntersecting,
      }));
    },
    [],
  );
  const onLoadSuccess = async ({numPages}: PDFDocumentProxy) => {
    setPageNum(numPages);
    setReadyPdf(true);
  };
  return (
    <Stack>
      {file && (
        <Document
          file={`data:application/pdf;base64, ${file}`}
          key={`I`}
          loading={<Stack sx={{px: '1.5rem'}}>loading...</Stack>}
          onLoadSuccess={onLoadSuccess}>
          <List
            sx={{
              overflow: 'scroll',
              ...StyleConstant.scrollNormal,
              height: `calc(100vh - 280px)`,
            }}>
            {Array.from(new Array(pageNum), (el, index) => (
              <Stack
                key={`page_${index + 1}`}
                justifyContent={'flex-start'}
                sx={{width: '100%'}}>
                <Stack
                  direction={'row'}
                  sx={{width: '100%'}}
                  justifyContent={'space-between'}
                  spacing={2}>
                  <ListItemButton
                    focusRipple
                    disableRipple
                    disableTouchRipple
                    sx={{
                      '&:hover': {
                        bgcolor: 'transparent',
                        width: '100%',
                      },
                      justifyContent: 'center',
                    }}
                    disableGutters>
                    <Stack width="auto" key={`page_${index + 1}`}>
                      <PageWithObserver
                        scale={zoom}
                        pageNumber={index + 1}
                        key={`page_${index + 1}`}
                        setPageVisibility={setPageVisibility}
                        // height={100}
                        width={350}
                      />
                    </Stack>
                  </ListItemButton>
                </Stack>
              </Stack>
            ))}
          </List>
        </Document>
      )}
    </Stack>
  );
};

function PageWithObserver({pageNumber, setPageVisibility, ...otherProps}: any) {
  const [page, setPage] = React.useState(null);

  const onIntersectionChange = React.useCallback(
    ([entry]: any) => {
      setPageVisibility(pageNumber, entry.isIntersecting);
    },
    [pageNumber, setPageVisibility],
  );

  useIntersectionObserver(page, observerConfig, onIntersectionChange);

  return <Page canvasRef={setPage} pageNumber={pageNumber} {...otherProps} />;
}

type IHeader = {
  signEnable: boolean;
  storeData: AssignedProjectEndUserType;
  submit: () => Promise<void>;
  refuseToggle: () => void;
  projectFlowId: IGetFlowId;
};

const Header = (props: IHeader) => {
  const {signEnable, storeData, submit, refuseToggle, projectFlowId} = props;
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
          disabled={!signEnable}
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
          disabled={!signEnable}
          onClick={submit}
          variant="contained"
          sx={{
            fontSize: '11px',
            fontWeight: 600,
            fontFamily: FONT_TYPE.POPPINS,
            textTransform: 'none',
          }}>
          {t(Localization('end-user-assigned-project', 'approve-doc'))}
        </Button>
      </Stack>
    </Stack>
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
              {t(Localization('refuse-document', 'refuse-to-approve'))}
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

export default ViewFileToApprove;
