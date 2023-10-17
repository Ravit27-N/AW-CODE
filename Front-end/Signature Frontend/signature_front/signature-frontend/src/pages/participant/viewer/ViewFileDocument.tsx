import NGText from '@/components/ng-text/NGText';
import {ProjectStatus} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';

import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray} from '@/utils/request/common/type';
import {getFilePdf} from '@/utils/request/services/MyService';
import AddIcon from '@mui/icons-material/Add';

import RemoveIcon from '@mui/icons-material/Remove';
import {
  Button,
  IconButton,
  List,
  ListItemButton,
  Stack,
  Typography,
} from '@mui/material';
import {useIntersectionObserver} from '@wojtekmaj/react-hooks';

import type {PDFDocumentProxy} from 'pdfjs-dist';
import React, {useEffect, useState} from 'react';

import {useTranslation} from 'react-i18next';
import {Document, Page, pdfjs} from 'react-pdf/dist/esm/entry.vite';
import {useNavigate, useParams, useSearchParams} from 'react-router-dom';

import {
  useGetProjectByFlowIdQuery,
  useViewDocumentMutation,
} from '@/redux/slides/process-control/participant';
import {validateFileRoleView} from '../common/checkRole';
import InvitationLayout from '../invitation/InvitationLayout';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;
const observerConfig = {
  // How much of the page needs to be visible to consider page visible
  threshold: 0,
};

const ViewFileDocument = () => {
  const navigate = useNavigate();
  const {t} = useTranslation();

  const param = useParams();
  const [searchQuery] = useSearchParams();
  const [zoom, setZoom] = React.useState(1);
  const queryParameters = new URLSearchParams(window.location.search);
  const [btnEnable, setBtnEnable] = React.useState<boolean>(false);
  const {data, isLoading, isSuccess, isFetching} = useGetProjectByFlowIdQuery({
    id: param.id + '?' + queryParameters,
  });
  const [viewDocument] = useViewDocumentMutation();
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

  const setPageVisibility = React.useCallback(
    (pageNumber: any, isIntersecting: any) => {
      setVisiblePages(prevVisiblePages => ({
        ...prevVisiblePages,
        [pageNumber]: isIntersecting,
      }));
    },
    [],
  );

  const onLoadPagesSuccess = (pdf: PDFDocumentProxy) => {
    const temp = [...multiDocPages];
    temp.push({index: 0, pages: pdf.numPages});
    setDocument({...documents, page: pdf.numPages});
    setMultiDocPages(temp);
    return null;
  };

  useEffect(() => {
    Object.entries(visiblePages).forEach(
      ([key, value]) => value === true && setCurrPage(Number(key)),
    );
  }, [visiblePages]);
  const triggerViewDocument = React.useCallback(async () => {
    const {
      actor: {processed},
    } = data!;

    if (!processed) {
      try {
        await viewDocument({
          flowId: param.id!,
          uuid: searchQuery.get('token')!,
        });
      } catch (error) {
        return error;
      }
    }
    return null;
  }, [data]);

  React.useEffect(() => {
    if (btnEnable) {
      triggerViewDocument().then(r => r);
    }
  }, [btnEnable]);

  React.useEffect(() => {
    if (currPage === (multiDocPages.length > 0 && multiDocPages[0].pages)) {
      setBtnEnable(true);
    }
  }, [multiDocPages.length > 0, currPage]);

  useEffect(() => {
    if (isSuccess) {
      const {
        projectStatus,
        actor: {role},
      } = data;
      const flowId = param.id as any;
      if (projectStatus === ProjectStatus.EXPIRED) {
        navigate(
          `${Route.participant.expiredProject}/${flowId}?${queryParameters}`,
        );
      }
      const validate = validateFileRoleView(role);
      if (validate) {
        navigate(`${validate}/${flowId}?${queryParameters}`);
      }
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

      fetchFile().then(r => r);
    }
  }, [isSuccess]);

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
              justifyContent="center"
              sx={{p: '12px 20px'}}>
              <Button
                disabled={!btnEnable}
                onClick={() => {
                  // close current tab
                  window.close();
                }}
                variant="contained"
                sx={{
                  width: '100%',
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
                {t(Localization('invitation', 'close'))}
              </Button>
            </Stack>
          </Stack>
        </Stack>
      </Stack>
    </InvitationLayout>
  );
};

export default ViewFileDocument;

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
