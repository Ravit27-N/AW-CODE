import {NGAlert} from '@/assets/Icon';
import {NGFalse} from '@/assets/iconExport/Allicon';
import {LockIcon} from '@/assets/svg/lock/lock';
import {SignatureIcon} from '@/assets/svg/signature/signature';
import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@components/ng-dailog/NGDialog';

import NGText from '@/components/ng-text/NGText';
import {
  KeySignatureLevel,
  OtpLength,
  processAdvancedInvitation,
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
import {useAppSelector} from '@/redux/config/hooks';
import {
  useGenerateOTPMutation,
  useGetProjectByFlowIdQuery,
  useRefuseDocumentMutation,
  useValidateOTPMutation,
} from '@/redux/slides/process-control/participant';
import {router} from '@/router';
import {Center} from '@/theme';

import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray} from '@/utils/request/common/type';
import AddIcon from '@mui/icons-material/Add';

import RemoveIcon from '@mui/icons-material/Remove';
import {
  Box,
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
import {MuiOtpInput} from 'mui-one-time-password-input';

import type {PDFDocumentProxy} from 'pdfjs-dist';
import React, {ChangeEvent, useEffect} from 'react';

import {useTranslation} from 'react-i18next';
import {Document, Page, pdfjs} from 'react-pdf/dist/esm/entry.vite';
import {useNavigate, useParams, useSearchParams} from 'react-router-dom';

import {getFilePdf} from '@/utils/request/services/MyService';
import {validateFileRoleSign} from '../common/checkRole';
import InvitationLayout from '../invitation/InvitationLayout';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;
const observerConfig = {
  // How much of the page needs to be visible to consider page visible
  threshold: 0,
};

const ViewFileToSignAdvance = () => {
  const {t} = useTranslation();

  const param = useParams();
  const [searchQuery] = useSearchParams();
  const [zoom, setZoom] = React.useState(1);
  const [currPage, setCurrPage] = React.useState<number>(1);
  const queryParameters = new URLSearchParams(window.location.search);
  const {
    currentData: data,
    isLoading,
    isFetching,
    refetch,
  } = useGetProjectByFlowIdQuery({
    id: param.id + '?' + queryParameters,
  });
  const [generateOpt, {isLoading: genOtpLoading}] = useGenerateOTPMutation();
  const [file, setFile] = React.useState<{
    url: string;
  }>({
    url: '',
  });
  const [documents, setDocument] = React.useState<{name: string; page: number}>(
    {
      name: '',
      page: 0,
    },
  );
  const [visiblePages, setVisiblePages] = React.useState({});
  const [multiDocPages, setMultiDocPages] = React.useState<
    {
      index: number;
      pages: number;
    }[]
  >(Array.from({length: 0}, () => ({index: 0, pages: 0})));

  // State controller otp phone number
  const [phoneOtp, setPhoneOtp] = React.useState<string>('');
  const [btnEnable, setBtnEnable] = React.useState<boolean>(false);
  const [togglePhoneNumbr, setTogglePhoneNumber] =
    React.useState<boolean>(false);
  const [blockAttemp, setBlockAttemp] = React.useState<boolean>(false);
  const [fullPhoneNumber, setFullPhoneNumber] = React.useState<string>('');
  const [inValidePhoneNumber, setInvalidPhoneNumber] =
    React.useState<string>('');
  const [removedPhoneNumber, setRemovePhoneNumber] = React.useState<string>('');

  const handleChangePhoneOtp = (value: string) => {
    setPhoneOtp(value);
  };

  // State controller OTP
  const [otp, setOtp] = React.useState<string>('');
  const [otpToggle, setOtpToggle] = React.useState<boolean>(false);
  const [inValidOtp, setInvalidOtp] = React.useState<string>('');
  const [validateOtp, {isLoading: otpLoading}] = useValidateOTPMutation();
  const setPageVisibility = React.useCallback(
    (pageNumber: any, isIntersecting: any) => {
      setVisiblePages(prevVisiblePages => ({
        ...prevVisiblePages,
        [pageNumber]: isIntersecting,
      }));
    },
    [],
  );

  // OTP change
  const handleOtpChange = (newValue: string) => {
    setOtp(newValue);
  };
  // Generate OTP
  const handleGenerateOtp = async () => {
    if (data!.otpInfo.expired) {
      try {
        await generateOpt({
          flowId: param.id!,
          uuid: searchQuery.get('token') ?? '',
        }).unwrap();
        setOtpToggle(true);
      } catch (error) {
        return error;
      }
    } else {
      if (!data!.otpInfo.validated) {
        await generateOpt({
          flowId: param.id!,
          uuid: searchQuery.get('token') ?? '',
        }).unwrap();
        setOtpToggle(true);
      }
    }
  };

  // Handle submit OTP
  const submitConfirmOtp = async () => {
    setInvalidOtp('');
    try {
      await validateOtp({
        flowId: param.id!,
        otp,
        uuid: searchQuery.get('token') ?? '',
      }).unwrap();
      setOtpToggle(false);
      setOtp('');
      router.navigate(
        `${Route.participant.signDocument}/${param.id}?${queryParameters}`,
      );
    } catch (e: any) {
      if (e.status === 406) {
        const {error} = e.data as any;
        if (error) {
          setInvalidOtp('Code incorrect!');
        }
      }
    }
  };

  // State refuse document
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

  const onLoadPagesSuccess = (pdf: PDFDocumentProxy) => {
    const temp = [...multiDocPages];
    temp.push({index: 0, pages: pdf.numPages});
    setDocument({...documents, page: pdf.numPages});
    setMultiDocPages(temp);
    return null;
  };
  const {id} = useParams();
  const navigate = useNavigate();
  const {signatureLevels} = useAppSelector(state => state.authentication);
  // ** redirect to identity if advance_signature
  const gotoAdvance = () => {
    if (signatureLevels.signatureLevel === KeySignatureLevel.ADVANCE)
      return navigate(
        `${Route.participant.advance.identity}/${id}?${queryParameters}`,
      );
  };

  React.useEffect(() => {
    if (currPage === (multiDocPages.length > 0 && multiDocPages[0].pages)) {
      setBtnEnable(true);
    }
  }, [multiDocPages.length > 0, currPage]);

  useEffect(() => {
    Object.entries(visiblePages).forEach(
      ([key, value]) => value === true && setCurrPage(Number(key)),
    );
  }, [visiblePages]);

  useEffect(() => {
    if (data) {
      const {
        projectStatus,
        actor: {processed, comment, role},
      } = data;
      const flowId = param.id as any;
      if (projectStatus === ProjectStatus.EXPIRED) {
        router.navigate(
          `${Route.participant.expiredProject}/${flowId}?${queryParameters}`,
        );
      }
      const validate = validateFileRoleSign(role);
      if (validate) {
        router.navigate(`${validate}/${flowId}?${queryParameters}`);
      } else {
        if (processed) {
          if (comment) {
            router.navigate(
              `${Route.participant.refuseDocument}/${flowId}?${queryParameters}`,
            );
          } else {
            router.navigate(
              `${Route.participant.signDocument}/${flowId}?${queryParameters}`,
            );
          }
        }
      }
    }
  }, [data]);

  React.useEffect(() => {
    if (data) {
      const fetchFile = async () => {
        data.documents.map(async item => {
          const dataFile = await getFilePdf({
            flowId: param.id + '?' + queryParameters,
            docId: item.docId,
          });
          setFile({...file, url: dataFile});
          setDocument({...documents, name: item.name});
        });
      };
      const {phoneNumber} = data;
      const {totalAttempts, removedNumber} = phoneNumber;
      if (totalAttempts >= 3) {
        setBlockAttemp(true);
      } else {
        setTogglePhoneNumber(true);
        setRemovePhoneNumber(removedNumber);
      }
      fetchFile().then(r => r);
    }
  }, [data]);
  const handlerBeforeProcess = () => {
    if (isLoading) {
      return <>loading...</>;
    }
    if (isFetching) {
      return <>fetching...</>;
    }
  };
  handlerBeforeProcess();
  const handlerButton = () => {
    if (genOtpLoading) {
      return undefined;
    } else if (!btnEnable) {
      return <LockIcon />;
    } else {
      return <SignatureIcon />;
    }
  };
  return (
    <InvitationLayout>
      <Stack
        width="100%"
        sx={{position: 'relative'}}
        height={`calc(100vh - 56px)`}>
        <Stack
          width="100%"
          height="48px"
          direction="row"
          sx={{p: '12px 16px', bgcolor: '#f5f5ffed'}}>
          <NGText
            text={`${
              documents.name.length > 15
                ? documents.name.slice(0, 15) + '... .pdf'
                : documents.name
            } - `}
            myStyle={{
              textAlign: 'center',
              fontSize: pixelToRem(14),
              fontWeight: 400,
            }}
          />
          <NGText
            text={`${documents.page} pages`}
            myStyle={{
              textAlign: 'center',
              fontSize: pixelToRem(14),
              fontWeight: 400,
            }}
          />
        </Stack>

        <Stack
          width="100%"
          justifyContent="center"
          alignItems="center"
          sx={{overflowY: 'hidden'}}
          height={`calc(100vh - (48px + 128px + 55px))`}>
          {file.url && (
            <Document
              file={`data:application/pdf;base64, ${file.url}`}
              key={`document_d${file.url}`}
              loading={<Stack sx={{px: '1.5rem'}}>loading...</Stack>}
              onLoadSuccess={onLoadPagesSuccess}>
              <List
                sx={{
                  overflow: 'scroll',
                  ...StyleConstant.scrollNormal,
                  height: `calc(100vh - (48px + 128px ))`,
                }}>
                {Array.from(new Array(documents.page), (el, index) => (
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
                {/* <Waypoint onEnter={handleLastItem} debug={false} /> */}
              </List>
            </Document>
          )}
        </Stack>

        <Stack
          height="124px"
          width="100%"
          sx={{position: 'absolute', bottom: 0}}>
          <Stack
            height="52px"
            direction="row"
            justifyContent="space-between"
            sx={{p: '14px 20px', background: 'rgba(0, 0, 0, 0.8);'}}>
            <Stack
              direction="row"
              width="128px"
              height="24px"
              justifyContent="space-between">
              <IconButton sx={{p: 0}} onClick={() => setZoom(pre => pre - 0.1)}>
                <RemoveIcon sx={{color: '#ffffff'}} />
              </IconButton>
              <Typography sx={{color: '#ffffff'}}>Zoom</Typography>
              <IconButton sx={{p: 0}} onClick={() => setZoom(pre => pre + 0.1)}>
                <AddIcon sx={{color: '#ffffff'}} />
              </IconButton>
            </Stack>
            <Stack
              direction="row"
              width="auto"
              height="24px"
              justifyContent="space-between">
              <Typography
                sx={{
                  color: '#ffffff',
                  fontSize: '14px',
                  fontFamily: 'Poppins',
                  fontWeight: 400,
                }}>
                {$isarray(Object.entries(visiblePages).filter(v => v[1])) &&
                  `Page ${currPage}/${
                    multiDocPages.length > 0 ? multiDocPages[0].pages : '...'
                  }`}
              </Typography>
            </Stack>
          </Stack>
          <Stack width="100%" alignItems="center">
            <Stack
              height="72px"
              width="375px"
              direction="row"
              justifyContent="space-between"
              sx={{p: '12px 20px'}}>
              <Button
                disabled={!btnEnable}
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
                startIcon={handlerButton()}
                onClick={handleGenerateOtp}
                disabled={!btnEnable || genOtpLoading}
                variant="contained"
                sx={{
                  width: '157px',
                  height: '48px',
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
                {genOtpLoading ? (
                  <CircularProgress color="inherit" size={'22px'} />
                ) : (
                  t(Localization('invitation', 'sign'))
                )}
              </Button>
            </Stack>
          </Stack>
        </Stack>
      </Stack>

      <ConfirmOtpDialog
        setInvalidPhoneMessage={setInvalidOtp}
        setOtp={setOtp}
        otp={otp}
        isLoading={otpLoading}
        otpToggle={otpToggle}
        setOtpToggle={setOtpToggle}
        handleOtpChange={handleOtpChange}
        invalidPhoneMessage={inValidOtp}
        submitConfirmOtp={submitConfirmOtp}
        fullPhoneNumber={fullPhoneNumber}
        handleGenerateOtp={handleGenerateOtp}
      />
      <RefuseDialog
        refuseToggle={refuseToggle}
        textArea={textArea}
        setRefuseToggle={setRefuseToggle}
        onChangeTextArea={onChangeTextArea}
        refuseDocumentLoading={refuseDocumentLoading}
        submitRefuseDocument={submitRefuseDocument}
      />

      {blockAttemp && <BlockAttemps />}
    </InvitationLayout>
  );
};

export default ViewFileToSignAdvance;

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
      style={{
        '&.css-hz1bth-MuiDialog-container': {
          alignItems: 'flex-start',
        },
      }}
      width="327px"
      height="343px"
      dialogAction={false}
      open={phoneNumberToggle}
      header={
        <Box sx={{position: 'relative'}}>
          <Stack alignItems={'center'} spacing={1}>
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
                fontSize: '14px',
                fontFamily: 'Poppins',
                textAlign: 'center',
                fontWeight: 400,
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
        <Stack spacing={2} mt={4}>
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
                  gap: 1,
                  borderColor: 'red',
                  ...StyleConstant.muiInputOtpTelephone,
                  '& .MuiOutlinedInput-notchedOutline ': {
                    borderColor:
                      otp.length === 4 ? theme[0].mainColor : 'black.main',
                    borderRadius: '5px',
                  },
                }}
                TextFieldsProps={{
                  disabled: validateLoading,
                  sx: {
                    borderRadius: '5px',
                    ':focus': {
                      color: 'red',
                      outlineStyle: 'none',
                      borderWidth: '1px',
                      borderStyle: 'solid',
                      borderColor: 'red',
                    },
                    ':focus-visible': {
                      color: 'red',
                      outlineStyle: 'none',
                      borderWidth: '1px',
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
                title={!validateLoading && t(Localization('title', 'validate'))}
                onClick={async () => submitPhoneNumberOtp()}
                // disabled={otpFromBackend !== otp}
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

type IConfirmOtp = {
  otpToggle: boolean;
  setOtpToggle: React.Dispatch<React.SetStateAction<boolean>>;
  otp: string;
  handleOtpChange: (value: string) => void;
  submitConfirmOtp: () => Promise<void>;
  fullPhoneNumber: string;
  invalidPhoneMessage: string;
  setInvalidPhoneMessage: React.Dispatch<React.SetStateAction<string>>;
  setOtp: React.Dispatch<React.SetStateAction<string>>;
  handleGenerateOtp: any;
  isLoading: boolean;
};

const ConfirmOtpDialog = ({
  otp,
  handleOtpChange,
  submitConfirmOtp,
  invalidPhoneMessage,
  setInvalidPhoneMessage,
  fullPhoneNumber,
  setOtp,
  handleGenerateOtp,
  otpToggle,
  setOtpToggle,
  isLoading,
}: IConfirmOtp) => {
  const {theme} = useAppSelector(state => state.enterprise);
  const {t} = useTranslation();
  return (
    <NGDialog
      open={otpToggle}
      width="327px"
      height="440px"
      // sx={{bgcolor: 'red', width: '300px'}}
      header={
        <Box sx={{position: 'relative'}}>
          <Stack alignItems="center">
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
                width: '263px',
                fontSize: pixelToRem(22),
              }}
            />
            <IconButton
              sx={{position: 'absolute', top: -15, right: -10}}
              onClick={() => {
                setInvalidPhoneMessage('');
                setOtp('');
                setOtpToggle(false);
              }}>
              <NGFalse sx={{fontSize: [12, 14, 16], color: 'Primary.main'}} />
            </IconButton>
          </Stack>
        </Box>
      }
      body={
        <Stack alignItems="center" mt={2}>
          <Typography
            width="270px"
            sx={{
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
          <Center sx={{width: ['100%', '80%']}} mt={2}>
            <NGText text={invalidPhoneMessage} sx={{color: 'red', mb: 1}} />
            <MuiOtpInput
              value={otp}
              onChange={handleOtpChange}
              inputMode="numeric"
              length={OtpLength}
              sx={{
                width: '263px',
                gap: 1,
                ...StyleConstant.muiInputOtp,
                '& .MuiOutlinedInput-notchedOutline ': {
                  borderColor:
                    otp.length == OtpLength ? theme[0].mainColor : 'black.main',
                },
              }}
              TextFieldsProps={{
                size: 'small',
                placeholder: '_',
              }}
            />

            <NGButton
              onClick={async () => submitConfirmOtp()}
              locationIcon="end"
              icon={
                isLoading && (
                  <CircularProgress sx={{color: '#ffffff'}} size={'1.5rem'} />
                )
              }
              title={!isLoading && 'Valider'}
              disabled={otp?.length !== OtpLength || isLoading}
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
          <Stack width={'100%'} mt={1}>
            <NGText
              text={t(Localization('opt-dialog', 'code-received'))}
              myStyle={{
                width: '150px',
                fontFamily: 'Poppins',
                fontStyle: 'normal',
                fontWeight: 400,
                fontSize: '14px',
                lineHeight: '22px',
                textAlign: 'center',
                color: ' #000000',
              }}
              iconStart={<NGAlert sx={{fontSize: 20, color: 'Primary.main'}} />}
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
