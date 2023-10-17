import {Document, Page, pdfjs} from 'react-pdf/dist/esm/entry.vite';
import {styles} from '@/pages/form/process-upload/edit-pdf/other/css.style';
import {getWindowDimensions} from '@/utils/common/WindowResize';
import {$count, $isarray, $ok} from '@/utils/request/common/type';
import {DocumentIcon} from '@assets/svg/document/document';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import {
  Box,
  IconButton,
  List,
  ListItemButton,
  ListSubheader,
  Stack,
  Typography,
} from '@mui/material';
import {useIntersectionObserver} from '@wojtekmaj/react-hooks';
import type {PDFDocumentProxy} from 'pdfjs-dist';
import React, {Dispatch, SetStateAction} from 'react';
import {Waypoint} from 'react-waypoint';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;
const observerConfig = {
  // How much of the page needs to be visible to consider page visible
  threshold: 0,
};

export type IViewFileSignState = {
  validateLoading: boolean;
  loadingGen: boolean;
  blockAttemp: boolean;
  otpToggle: boolean;
  disableSignature: boolean;
  phoneNumberToggle: boolean;
  pageNum: number;
};

export const PDFsView = ({
  files,
  setDisableSignature,
  state,
  setState,
  data,
}: {
  files: string[];
  state?: IViewFileSignState;
  setDisableSignature: Dispatch<SetStateAction<boolean>>;
  setState?: React.Dispatch<React.SetStateAction<IViewFileSignState>>;
  data?: any;
}) => {
  const [zoom, setZoom] = React.useState<number>(1);
  const [visiblePages, setVisiblePages] = React.useState({});

  // Nums page
  const [multiDocPages, setMultiDocPages] = React.useState<
    {
      index: number;
      pages: number;
    }[]
  >(Array.from({length: files.length}, () => ({index: 0, pages: 0})));
  // PDFS load success
  const onLoadPagesSuccess = async (
    {numPages}: PDFDocumentProxy,
    index: number,
  ) => {
    const temp = [...multiDocPages];
    temp.push({index, pages: numPages});
    setState!({...state!, pageNum: numPages});
    setMultiDocPages(temp);
  };

  const handleLastItem = ({previousPosition}: {previousPosition: string}) => {
    if (previousPosition == 'below') {
      // To make signature btn active
      setDisableSignature(false);
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
  const [windowDimensions, setWindowDimensions] = React.useState(
    getWindowDimensions(),
  );

  React.useEffect(() => {
    function handleResize() {
      setWindowDimensions(getWindowDimensions());
    }

    window.addEventListener('resize', handleResize);

    return () => window.removeEventListener('resize', handleResize);
  }, []);
  /** hanlder hieght **/
  const handlerHeight = () => {
    if (windowDimensions.height === 640) {
      return '70vh';
    } else if (windowDimensions.height === 896) {
      return '79vh';
    } else if (windowDimensions.height === 800) {
      return '76vh';
    } else {
      return '70vh';
    }
  };
  /** handler multi Doc pages **/
  const handlerMultiPages = () => {
    if (multiDocPages.length > 0) {
      return multiDocPages[0].pages;
    } else {
      return '...';
    }
  };

  return (
    <Stack
      sx={{
        alignContent: 'center',
        justifyContent: 'center',
        alignItems: 'center',
      }}>
      <List
        sx={{
          width: '100%',
          maxWidth: 500,
          bgcolor: 'background.paper',
          position: 'relative',
          overflow: 'scroll',
          height: [handlerHeight(), '90vh', '79vh', '84vh'],
          ...styles.scrollbarHidden,
          '& ul': {padding: 0},
        }}
        subheader={<li />}>
        {files.map((file, index: number) => (
          <Box key={file}>
            {$ok(data) &&
              $isarray(data.documents) &&
              $count(data.documents) && (
                <ListSubheader sx={{background: '#f5f5ffed'}}>
                  <Stack direction={'row'} sx={{alignItems: 'center'}}>
                    <DocumentIcon />
                    <Typography sx={{color: 'black', fontWeight: 600}}>
                      {data.documents[index]?.name.length < 30
                        ? data.documents[index]?.name
                        : `${data.documents[index]?.name.substring(0, 30)} ...`}
                      &nbsp;
                    </Typography>
                    -&nbsp;{data.documents[index]?.totalPages}
                    {' pages'}
                  </Stack>
                </ListSubheader>
              )}
            <DocumentCompo
              file={file}
              index={index}
              multiDocPages={multiDocPages}
              onLoadPagesSuccess={onLoadPagesSuccess}
              setPageVisibility={setPageVisibility}
              zoom={zoom}
            />
          </Box>
        ))}
        <Waypoint onEnter={handleLastItem} debug={false} />
      </List>
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
          <IconButton onClick={() => setZoom(zoom - 0.1)}>
            <RemoveIcon sx={{color: '#ffffff'}} />
          </IconButton>
          <Typography>Zoom</Typography>
          <IconButton
            sx={{color: '#ffffff'}}
            onClick={() => setZoom(zoom + 0.1)}>
            <AddIcon />
          </IconButton>
        </Stack>
        <Stack
          direction={'row'}
          alignItems={'center'}
          spacing={2}
          sx={{px: '20px'}}>
          <Typography component={'span'}>
            {$isarray(
              Object.entries(visiblePages).filter(([key, value]) => value),
            ) &&
            Object.entries(visiblePages).filter(([key, value]) => value)
              .length > 1
              ? `Page ${
                  Object.entries(visiblePages).filter(([key, value]) => value)[
                    Object.entries(visiblePages).filter(([key, value]) => value)
                      .length - 1
                  ][0]
                }/${handlerMultiPages()}`
              : `Page 1/ ${handlerMultiPages()}`}
          </Typography>
        </Stack>
      </Stack>
    </Stack>
  );
};

type IDocumentCompo = {
  file?: string;
  index?: number;
  multiDocPages?: {
    index?: number;
    pages?: number;
  }[];
  onLoadPagesSuccess?: ({numPages}: PDFDocumentProxy, index: number) => void;
  zoom?: number;
  setPageVisibility?: (pageNumber: any, isIntersecting: any) => void;
};

export const DocumentCompo = ({
  file,
  index,
  multiDocPages,
  onLoadPagesSuccess,
  zoom,
  setPageVisibility,
}: IDocumentCompo) => {
  return (
    <Document
      file={'Data:application/pdf;base64,' + file}
      key={`document_d${index}`}
      loading={<Stack sx={{px: '1.5rem'}}>loading...</Stack>}
      onLoadSuccess={(e: PDFDocumentProxy) => {
        onLoadPagesSuccess!(e, index!);
      }}>
      {$isarray(multiDocPages) &&
        $count(multiDocPages) &&
        Array.from(new Array(multiDocPages![index!]?.pages), (el, index) => {
          return (
            <ListItemButton
              // autoFocus={true}
              sx={{justifyContent: 'center'}}
              key={`page_${index + 1}`}
              // autoFocus={index + 1 === currentPage}
            >
              <Stack key={`page_${index + 1}`} direction={'row'} spacing={1}>
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
          );
        })}
    </Document>
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
