import {
  IconButton,
  List,
  ListItemButton,
  ListProps,
  Stack,
  useMediaQuery,
} from '@mui/material';
import 'react-pdf/dist/esm/Page/TextLayer.css';
import type {PDFDocumentProxy} from 'pdfjs-dist';
import 'react-pdf/dist/esm/Page/AnnotationLayer.css';
import {Document, Page} from 'react-pdf/dist/esm/entry.vite';
import {Box} from '@mui/system';
import NGText from '@components/ng-text/NGText';
import React from 'react';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {NGDownload} from '@assets/iconExport/ExportIcon';
import {Center} from '@/theme';
import {grey} from '@mui/material/colors';
import {HtmlTooltip} from '@components/ng-table/TableDashboard/resource/TCell';
import {FigmaBody} from '@/constant/style/themFigma/Body';
import {pixelToRem} from '@/utils/common/pxToRem';
import CircularProgress from '@mui/material/CircularProgress';
import ReactPDF from '@react-pdf/renderer';
import DocumentProps = ReactPDF.DocumentProps;
export const NGPdfView = ({
  file,
  width = 100,
  height = 200,
  handleSuccess,
}: {
  file: any;
  width?: number;
  height?: number;
  handleSuccess?: (pdf: PDFDocumentProxy) => void;
}) => {
  return (
    <>
      {/* PDF View in thumbnail mode  */}
      <Document
        file={file}
        onLoadSuccess={handleSuccess}
        loading={<Stack sx={{px: '1.5rem'}}>loading...</Stack>}>
        <Page
          pageIndex={0}
          height={height}
          width={width}
          renderAnnotationLayer={false}
          renderTextLayer={false}
        />
      </Document>
    </>
  );
};
export const NGPdfBox = ({
  file,
  fileName,
  downloadFile,
  downloading = false,
}: {
  file: any;
  fileName: string;
  downloadFile?: () => Promise<void>;
  downloading?: boolean;
}) => {
  const [dataPdf, setDataPDF] = React.useState<{page: number; Name: string}>();
  const xl = useMediaQuery(`(max-width:1440px)`);
  const xxl = useMediaQuery(`(min-width:2000px)`);
  const successLoadingDocuments = ({numPages, ...res}: PDFDocumentProxy) => {
    setDataPDF({
      page: numPages,
      Name: res._transport._params.url.split('/')[4],
    });
  };
  let h = 0;
  let w = 0;
  const height = () => {
    if (xl) {
      h = 250;
    } else if (xxl) {
      h = 350;
    } else {
      h = 300;
    }
    return h;
  };
  const width = () => {
    if (xl) {
      w = 200;
    } else if (xxl) {
      w = 320;
    } else {
      w = 250;
    }
    return w;
  };
  return (
    <Box
      bgcolor={'bg.main'}
      sx={{boxShadow: `1px 2px 2px ${grey[400]}`, mt: pixelToRem(30)}}
      overflow={'hidden'}
      height={height()}
      position={'relative'}
      borderRadius={1}>
      <Center>
        <NGPdfView
          file={file}
          height={height()}
          width={width()}
          handleSuccess={successLoadingDocuments}
        />
      </Center>
      <Stack
        bgcolor={'white'}
        height={'30%'}
        width={'100%'}
        justifyContent={'space-between'}
        pl={xl ? 1 : 2}
        pr={xl ? 0 : 2}
        direction={'row'}
        bottom={0}
        left={0}
        position={'absolute'}
        alignItems={'center'}
        boxShadow={0}>
        <Stack minWidth={0}>
          <HtmlTooltip
            placement={'top-start'}
            title={
              <NGText
                text={fileName}
                myStyle={{
                  ...FigmaBody.BodyMediumBold,
                }}
              />
            }>
            <Stack width={'100%'} alignItems={'flex-start'}>
              <NGText
                text={fileName}
                noWrap={true}
                myStyle={{
                  width: '100%',
                  textAlign: 'start',
                  ...FigmaBody.BodyMediumBold,
                }}
              />
              <NGText
                text={dataPdf?.page + ' Pages'}
                sx={{...StyleConstant.textSmall}}
              />
            </Stack>
          </HtmlTooltip>
        </Stack>
        <IconButton size="small" onClick={downloadFile}>
          {downloading ? (
            <CircularProgress
              color="inherit"
              size={'15px'}
              sx={{
                mr: pixelToRem(8),
                mt: pixelToRem(8),
              }}
            />
          ) : (
            <NGDownload sx={{color: 'Primary.main', m: '5px 0 0 6px'}} />
          )}
        </IconButton>
      </Stack>
    </Box>
  );
};
export const NGPdfViewMultiplePage = ({
  scale = 1,
  src,
  propsDocument,
  propList,
}: {
  scale: number;
  src: string;
  propsDocument?: DocumentProps;
  propList?: ListProps;
}) => {
  const [documents, setDocument] = React.useState<{name: string; page: number}>(
    {
      name: '',
      page: 0,
    },
  );

  const onLoadPagesSuccess = (pdf: PDFDocumentProxy) => {
    setDocument({...documents, page: pdf.numPages});
    return null;
  };

  return (
    <Document
      file={src}
      key={src}
      loading={<Stack sx={{px: '1.5rem'}}>loading...</Stack>}
      onLoadSuccess={onLoadPagesSuccess}
      {...propsDocument}>
      <List
        sx={{
          overflow: 'scroll',
          ...StyleConstant.scrollNormal,
          scrollNormal: {
            '&::-webkit-scrollbar': {
              width: '0.05em',
            },
          },
          height: `calc(100vh - 55px)`,
          ...propList,
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
                  <Page pageNumber={index + 1} scale={scale} />
                </Stack>
              </ListItemButton>
            </Stack>
          </Stack>
        ))}
      </List>
    </Document>
  );
};
