import {NGFalse} from '@/assets/iconExport/Allicon';
import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@components/ng-dailog/NGDialog';

import NGText from '@/components/ng-text/NGText';
import {ProjectStatus} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {
  StyleConstant,
  colorDisable,
  colorWhite,
} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {
  useGenerateOTPMutation,
  useGetProjectByFlowIdQuery,
  useRefuseDocumentMutation,
} from '@/redux/slides/process-control/participant';

import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray} from '@/utils/request/common/type';
import {getFilePdf} from '@/utils/request/services/MyService';
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

import type {PDFDocumentProxy} from 'pdfjs-dist';
import React, {ChangeEvent, useEffect, useState} from 'react';

import {useTranslation} from 'react-i18next';
import {Document, Page, pdfjs} from 'react-pdf/dist/esm/entry.vite';
import {useNavigate, useParams, useSearchParams} from 'react-router-dom';

import {validateFileRoleApprove} from '../common/checkRole';
import InvitationLayout from '../invitation/InvitationLayout';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;
const observerConfig = {
  // How much of the page needs to be visible to consider page visible
  threshold: 0,
};

const ViewFileToApprove = () => {
  const {t} = useTranslation();
  const param = useParams();
  const company_uuid = param.id;
  const [searchQuery] = useSearchParams();
  const [zoom, setZoom] = React.useState(1);
  const queryParameters = new URLSearchParams(window.location.search);
  const {data, isLoading, isFetching, refetch} = useGetProjectByFlowIdQuery({
    id: company_uuid + '?' + queryParameters,
  });
  const [generateOpt] = useGenerateOTPMutation();
  const [btnEnable, setBtnEnable] = React.useState<boolean>(false);
  const [file, setFile] = React.useState<{
    url: string;
  }>({
    url: '',
  });
  const [currPage, setCurrPage] = useState<number>(1);
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

  // Refuse document
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
        flowId: company_uuid!,
        uuid: searchQuery.get('token') ?? '',
        comment: textArea,
      }).unwrap();
      refetch();
    } catch (error) {
      return error;
    }
  };

  const setPageVisibility = React.useCallback(
    (pageNumber: any, isIntersecting: any) => {
      setVisiblePages(prevVisiblePages => ({
        ...prevVisiblePages,
        [pageNumber]: isIntersecting,
      }));
    },
    [],
  );

  React.useEffect(() => {
    if (currPage === (multiDocPages.length > 0 && multiDocPages[0].pages)) {
      setBtnEnable(true);
    }
  }, [multiDocPages.length > 0, currPage]);

  // Submit approve document
  const submitApproveDocument = async () => {
    try {
      await generateOpt({
        flowId: company_uuid!,
        uuid: searchQuery.get('token') ?? '',
      }).unwrap();
      return navigate(
        `${Route.participant.approveDocument}/${company_uuid}?${queryParameters}`,
      );
    } catch (e) {
      return e;
    }
  };

  const onLoadPagesSuccess = (pdf: PDFDocumentProxy) => {
    const temp = [...multiDocPages];
    temp.push({index: 0, pages: pdf.numPages});
    setDocument({...documents, page: pdf.numPages});
    setMultiDocPages(temp);
    return null;
  };
  const navigate = useNavigate();
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
      const flowId = company_uuid as any;
      if (projectStatus === ProjectStatus.EXPIRED) {
        navigate(
          `${Route.participant.expiredProject}/${flowId}?${queryParameters}`,
        );
      }
      const validate = validateFileRoleApprove(role);
      if (validate) {
        navigate(`${validate}/${flowId}?${queryParameters}`);
      }
      if (processed) {
        if (comment) {
          navigate(
            `${Route.participant.refuseDocument}/${flowId}?${queryParameters}`,
          );
        } else {
          navigate(
            `${Route.participant.approveDocument}/${flowId}?${queryParameters}`,
          );
        }
      }
      const fetchFile = async () => {
        data.documents.map(async item => {
          const dataFile = await getFilePdf({
            flowId: company_uuid + '?' + queryParameters,
            docId: item.docId,
          });
          setFile({...file, url: dataFile});
          setDocument({...documents, name: item.name});
        });
      };

      fetchFile();
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
                onClick={() => {
                  setRefuseToggle(true);
                }}
                disabled={!btnEnable}
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
                onClick={submitApproveDocument}
                disabled={!btnEnable}
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
                {t(Localization('invitation', 'approve'))}
              </Button>
            </Stack>
          </Stack>
        </Stack>
      </Stack>
      <RefuseDialog
        refuseToggle={refuseToggle}
        textArea={textArea}
        setRefuseToggle={setRefuseToggle}
        onChangeTextArea={onChangeTextArea}
        refuseDocumentLoading={refuseDocumentLoading}
        submitRefuseDocument={submitRefuseDocument}
      />
    </InvitationLayout>
  );
};

export default ViewFileToApprove;

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
              text={t(Localization('refuse-document', 'refuse-to-approve'))}
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
