import React from 'react';
import {Divider, IconButton, List, ListItemButton, Stack} from '@mui/material';
import {styles} from './other/css.style';
import {AddMore} from './other/common';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import 'react-pdf/dist/esm/Page/TextLayer.css';
import 'react-pdf/dist/esm/Page/AnnotationLayer.css';
import type {PDFDocumentProxy} from 'pdfjs-dist';
import {WebViewerInstance} from '@pdftron/webviewer';
import {Document, Page} from 'react-pdf/dist/esm/entry.vite';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import {useDispatch} from 'react-redux';
import {updateTempFile} from '@/redux/slides/authentication/authenticationSlide';
import {useAppSelector} from '@/redux/config/hooks';
import {$isarray} from '@/utils/request/common/type';
import NGText from '@components/ng-text/NGText';
import {useTranslation} from 'react-i18next';
import {Localization} from '@/i18n/lan';

const EditLeftSide = ({
  instance,
  pdfFiles,
}: {
  instance: WebViewerInstance | null;
  pdfFiles: {active: boolean; fileUrl: string; name: string}[];
}) => {
  const {t} = useTranslation();
  const {documentViewer, annotationManager} = instance!.Core;
  const dispatch = useDispatch();
  const [files, setFiles] = React.useState<
    {active: boolean; fileUrl: string; name: string}[]
  >([...pdfFiles]);
  const {tempFiles} = useAppSelector(state => state.authentication);
  const [currentView, setCurrentView] = React.useState<number>(0);
  const [currentPage, setCurrentPage] = React.useState<number>(1);
  const [numPages, setNumPages] = React.useState<
    {index: number; pages: number}[]
  >(Array.from({length: files.length}, () => ({index: 0, pages: 0})));
  // Webview Memo
  React.useMemo(() => {
    instance?.UI.loadDocument(files[0]?.fileUrl, {extension: 'pdf'});
    return documentViewer.addEventListener('documentLoaded', async () => {
      const xfdfString = await annotationManager.exportAnnotations();
      const data = await documentViewer.getDocument().getFileData({xfdfString});
      const arr = new Uint8Array(data);
      const blob = new Blob([arr], {type: 'application/pdf'});
      URL.createObjectURL(blob);
      documentViewer.addEventListener(
        'pageNumberUpdated',
        async (e: number) => {
          setCurrentPage(e);
        },
      );
    });
  }, []);

  React.useEffect(() => {
    setFiles([...tempFiles]);
  }, [tempFiles]);

  const onDocumentLoadSuccess = (
    {numPages: nextNumPages}: PDFDocumentProxy,
    index: number,
  ) => {
    const temp = [...numPages];
    temp[index].index = index;
    temp[index].pages = nextNumPages;
    setNumPages(temp);
  };
  const heightPage = (index: number) => {
    return numPages[index].pages > 1
      ? `calc(100vh -  ${68 * (files.length + 1)}px)`
      : 'auto';
  };
  return (
    <Stack
      sx={{
        width: '100%',
        height: '100%',
        borderLeft: 0,
        borderTop: 0,
        borderBottom: 0,
        borderRight: 2,
        borderColor: '#CFD4DA',
        borderStyle: 'solid',
      }}>
      {/* Documents */}
      <AddMore
        name={t(Localization('table', 'documents'))}
        fontSize={18}
        fontWeight={600}
        icon={<AddCircleOutlineIcon sx={{color: 'Primary.main'}} />}
      />
      <Divider sx={{borderBottomWidth: 2}} />

      {/* Lists of PDF files view */}
      <List
        sx={{
          width: '100%',
          height: '100%',
          py: 0,
          ...styles.scrollbarHidden,
        }}>
        {$isarray(files) &&
          files?.map((file, index: number) => (
            <Stack
              key={`doc_${file.fileUrl}`}
              sx={{
                width: '100%',
                height: file.active ? heightPage(index) : '70px',
              }}>
              <Stack sx={{px: '2rem', height: '70px'}}>
                <IconButton
                  onClick={() => {
                    if (currentView !== index) {
                      instance?.UI.loadDocument(files[index].fileUrl, {
                        documentId: `documentId_${index}`,
                      });
                      setCurrentView(index);
                    }
                    dispatch(updateTempFile({index}));
                  }}
                  disableFocusRipple
                  disableTouchRipple
                  disableRipple
                  sx={{
                    borderRadius: 0,
                    p: 0,
                    pt: 1,
                    alignItems: 'center',
                  }}>
                  <Stack sx={{width: '100%'}}>
                    <Stack
                      direction={'row'}
                      alignItems={'center'}
                      justifyContent={'space-between'}
                      spacing={2}
                      sx={{width: '100%'}}>
                      <Stack
                        direction={'row'}
                        sx={{alignItems: 'center'}}
                        spacing={1}>
                        <NGText
                          text={
                            file.name.length - 4 > 15
                              ? file.name.slice(0, 15) + '...' + '.pdf'
                              : file.name
                          }
                          myStyle={{fontSize: 16}}
                        />
                      </Stack>
                      <Stack direction={'row'}>
                        {file.active ? (
                          <KeyboardArrowDownIcon />
                        ) : (
                          <KeyboardArrowUpIcon />
                        )}
                      </Stack>
                    </Stack>
                    <Stack
                      direction={'row'}
                      sx={{alignItems: 'center', mb: '10px'}}
                      spacing={1}>
                      {numPages[index] ? (
                        <NGText
                          text={numPages[index].pages + ' pages'}
                          myStyle={{fontSize: 16}}
                        />
                      ) : (
                        <NGText text={'loading ...'} myStyle={{fontSize: 12}} />
                      )}
                    </Stack>
                  </Stack>
                </IconButton>
              </Stack>
              <Stack
                sx={{
                  display: file.active ? 'flex' : 'none',
                  width: 'auto',
                  height: 'auto',
                  overflow: 'hidden',
                  alignItems: 'center',
                }}>
                <List
                  disablePadding
                  sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    // ...styles.scrollbarHidden,
                    width: '80%',
                    height: '300',
                    alignItems: 'center',
                    overflowY: 'auto',
                  }}>
                  <Document
                    file={file.fileUrl}
                    key={file.fileUrl}
                    loading={<Stack sx={{px: '1.5rem'}}>loading...</Stack>}
                    onLoadSuccess={(numPages: PDFDocumentProxy) =>
                      onDocumentLoadSuccess(numPages, index)
                    }>
                    {Array.from(
                      new Array(numPages[index].pages),
                      (el, index) => (
                        <Stack
                          key={`page_${index + 1}`}
                          justifyContent={'flex-start'}
                          sx={{width: 'auto'}}>
                          <Stack
                            direction={'row'}
                            sx={{width: 'auto'}}
                            justifyContent={'space-between'}
                            spacing={2}>
                            <NGText
                              text={index + 1}
                              myStyle={{width: '10px'}}
                            />
                            <ListItemButton
                              disableRipple
                              disableTouchRipple
                              sx={{
                                '&:hover': {
                                  bgcolor: 'transparent',
                                  width: 'auto',
                                },
                                justifyContent: 'center',
                              }}
                              disableGutters
                              autoFocus={index + 1 === currentPage}>
                              <Stack
                                width="auto"
                                key={`page_${index + 1}`}
                                sx={{
                                  boxShadow: 1,
                                  borderWidth:
                                    currentPage === index + 1 ? 1 : 0,
                                  borderColor:
                                    currentPage === index + 1
                                      ? 'Primary.main'
                                      : undefined,
                                  borderStyle:
                                    currentPage === index + 1
                                      ? 'solid'
                                      : undefined,
                                }}>
                                <Page
                                  scale={0.25}
                                  pageNumber={index + 1}
                                  key={file.fileUrl}
                                  onClick={e => {
                                    e.preventDefault();
                                    return documentViewer.setCurrentPage(
                                      index + 1,
                                      false,
                                    );
                                  }}
                                />
                              </Stack>
                            </ListItemButton>
                          </Stack>
                        </Stack>
                      ),
                    )}
                  </Document>
                </List>
              </Stack>
              <Divider sx={{borderBottomWidth: 2}} />
            </Stack>
          ))}
      </List>
    </Stack>
  );
};

export default EditLeftSide;
