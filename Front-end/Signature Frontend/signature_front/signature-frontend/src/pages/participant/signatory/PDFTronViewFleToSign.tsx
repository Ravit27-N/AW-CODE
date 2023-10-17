import {Box} from '@mui/material';
import WebViewer, {WebViewerInstance} from '@pdftron/webviewer';
import React from 'react';
import env from '../../../../env.config';

type IPDFTronViewFileToSign = {
  instance: WebViewerInstance | null;
  setInstance: React.Dispatch<React.SetStateAction<WebViewerInstance | null>>;
  setDisableSignature: React.Dispatch<React.SetStateAction<boolean>>;
  currentTotalPages: number | null;
  currentPage: number | null;
  setCurrentPage: React.Dispatch<React.SetStateAction<number | null>>;
  setCurrentTotalPages: React.Dispatch<React.SetStateAction<number | null>>;
  setReadyPdf?: React.Dispatch<React.SetStateAction<boolean>>;
  file: string;
};

const PDFTronViewFileToSign = ({
  setInstance,
  setCurrentPage,
  setCurrentTotalPages,
  file,
  setReadyPdf,
}: IPDFTronViewFileToSign) => {
  const viewer = React.useRef<HTMLElement | null>(null);

  React.useEffect(() => {
    if (file) {
      WebViewer(
        {
          fullAPI: true,
          path: env.VITE_PUBLIC_FILE_PATH,
          disabledElements: ['ribbons', 'header', 'toolsHeader'],
          annotationUser: 'Veng',
          loadAsPDF: true,
        },
        viewer.current!,
      ).then(async instance => {
        instance?.UI.loadDocument(file, {
          documentId: `documentId_}`,
        });
        const {annotationManager, documentViewer} = instance.Core;
        documentViewer.addEventListener(
          'documentLoaded',
          () => {
            setCurrentTotalPages(instance.Core.documentViewer.getPageCount());
            setCurrentPage(instance.Core.documentViewer.getCurrentPage());
            setReadyPdf!(true);
          },
          {once: true},
        );

        documentViewer.addEventListener('pageComplete', () => {
          annotationManager.enableRedaction();
        });
        documentViewer.addEventListener(
          'pageNumberUpdated',
          (pageNumber: number) => {
            // if (
            //   pageNumber === instance.Core.documentViewer.getPageCount() ||
            //   pageNumber === instance.Core.documentViewer.getPageCount() - 1
            // ) {
            //   setDisableSignature(false);
            // }

            setCurrentPage(instance.Core.documentViewer.getCurrentPage());
          },
        );
        setInstance(instance);
      });
    }
  }, [file]);
  return (
    <Box
      ref={viewer}
      sx={{
        height: 'calc(100vh - 280px)',
        width: '100%',
        overflowY: 'hidden',
      }}></Box>
  );
};

export default PDFTronViewFileToSign;
