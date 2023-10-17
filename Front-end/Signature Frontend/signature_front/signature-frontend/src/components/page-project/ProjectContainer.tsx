import {NGPdfBox} from '@components/ng-pdf-view/NGPdfView';
import NGTabs from '@components/ng-tab/NGTab';
import NGTableParticipant from '@components/ng-table/TableParticipaint/NGTableParticipant';
import NGTImeLine from '@components/ng-timeline/NGTimeline';
import Grid from '@mui/material/Grid';
import React from 'react';

import {StyleConstant} from '@/constant/style/StyleConstant';
import {renameOriginalFile} from '@/utils/common/FileCommon';
import {getSignatureProgress} from '@/utils/common/SignatureProjectCommon';
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  downloadCurrentDocumentProject,
  downloadOrignalDocumentProject,
  getFilePdfProjectDetail,
} from '@/utils/request/services/MyService';
import Stack from '@mui/material/Stack';

const COMPLETED_PROJECT = 1;
function ProjectContainer({data}: {data: any}) {
  const [countDocument, setCountDocument] = React.useState(0);
  const [downloading, setDownloading] = React.useState<boolean>(false);
  const [dataResponse, setDataResponse] = React.useState<
    {
      fileName: string;
      file64: string;
      docId: string;
    }[]
  >([]);
  const getData = () => {
    data.documents.map(async (item: any) => {
      const fileBase64 = await getFilePdfProjectDetail({
        docName: item.fileName,
      });
      setDataResponse(prevState => [
        ...prevState,
        {file64: fileBase64, fileName: item.originalFileName, docId: item.id},
      ]);
      setCountDocument(countDocument => countDocument + 1);
      if (getSignatureProgress(data.signatories) === COMPLETED_PROJECT) {
        setCountDocument(countDocument => countDocument + 1);
      }
    });
  };
  React.useEffect(() => {
    getData();
  }, [data]);
  return (
    <Grid container height={'100%'} px={5}>
      <Grid item md={9} lg={9} sm={3} height={'100%'} p={0} m={0}>
        <NGTabs
          defaultTap={'Participants (' + data.signatories.length + ')'}
          tapStyle={{width: '100%'}}
          data={[
            {
              active: true,
              label: 'Participants (' + data.signatories.length + ')',
              contain: (
                <NGTableParticipant
                  data={data.signatories!}
                  projectId={data.id}
                />
              ),
            },
            {
              active: true,
              label: 'Documents (' + countDocument + ')',
              contain: (
                <Grid container spacing={pixelToRem(20)} width={'100%'}>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    {dataResponse.map(item => (
                      <NGPdfBox
                        key={item.docId}
                        file={'Data:application/pdf;base64,' + item.file64}
                        fileName={item.fileName}
                        downloadFile={async () =>
                          downloadOrignalDocumentProject(item.docId)
                        }
                      />
                    ))}
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    {getSignatureProgress(data.signatories) ===
                      COMPLETED_PROJECT &&
                      dataResponse.map(item => (
                        <NGPdfBox
                          downloading={downloading}
                          key={item.docId}
                          file={'Data:application/pdf;base64,' + item.file64}
                          fileName={renameOriginalFile(item.fileName)}
                          downloadFile={async () => {
                            setDownloading(true);
                            const res = await downloadCurrentDocumentProject(
                              item.docId,
                            );
                            if (res) {
                              setDownloading(false);
                              window.open(res);
                            }
                          }}
                        />
                      ))}
                  </Grid>
                </Grid>
              ),
            },
          ]}
        />
      </Grid>
      <Grid item md={3} lg={3} sm={3} height={'100%'} width={'100%'} mt={12}>
        <Stack
          alignItems={'flex-end'}
          height={'58vh'}
          sx={{
            overflowY: 'scroll',
            ...StyleConstant.scrollNormal,
          }}>
          <NGTImeLine histories={data.histories} />
        </Stack>
      </Grid>
    </Grid>
  );
}

export default ProjectContainer;
