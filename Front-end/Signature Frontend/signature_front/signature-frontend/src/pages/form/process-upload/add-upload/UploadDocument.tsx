import {DeleteIcon, NGIconFile} from '@/assets/Icon';
import NGText from '@/components/ng-text/NGText';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import NGDropzoneComponent from '@components/ng-dropzone/NGDropzone.component';
import {Box, Grid, Stack} from '@mui/material';
import React from 'react';
import {DropEvent, FileRejection} from 'react-dropzone';
import {useTranslation} from 'react-i18next';

type IUploadDocument = {
  handleUploadFile: (files: File[], event: DropEvent) => void;
  handleUploadError: (FileRejection: FileRejection[], event: DropEvent) => void;
  alertMessage: JSX.Element | null;
  errorUpload: null | JSX.Element;
  fileUpload: {file: File; pageCount: number}[];
  setFileUpload: React.Dispatch<
    React.SetStateAction<
      {
        file: File;
        pageCount: number;
      }[]
    >
  >;
};

const UploadDocument = ({
  handleUploadFile,
  handleUploadError,
  alertMessage,
  errorUpload,
  fileUpload,
  setFileUpload,
}: IUploadDocument) => {
  const {t} = useTranslation();
  return (
    <Stack sx={{py: '1rem', px: '1rem'}}>
      <Stack spacing={4}>
        <Stack spacing={2}>
          <NGText
            text={t(Localization('upload-document', 'import-your-file'))}
            myStyle={{fontWeight: 600, fontSize: 18}}
          />

          <NGText
            text={t(
              Localization(
                'upload-document',
                'which-documents-do-you-want-to-send',
              ),
            )}
            myStyle={{color: 'black.main', fontWeight: 400, fontSize: 12}}
          />
          {fileUpload !== undefined && (
            <Grid container spacing={2}>
              {fileUpload.map((item: any, index) => (
                <Grid
                  item
                  md={6}
                  lg={6}
                  sm={12}
                  key={index}
                  sx={{width: '100%'}}>
                  <Box
                    sx={{
                      px: 2,
                      py: 2,
                      borderRadius: 2,
                      bgcolor: '#ffffff',
                      boxShadow: 0.8,
                    }}>
                    <Stack
                      spacing={2}
                      direction={'row'}
                      justifyContent={'space-between'}
                      alignItems={'center'}>
                      <Stack
                        direction={'row'}
                        alignItems={'center'}
                        spacing={2}>
                        <Stack
                          direction={'row'}
                          sx={{p: 1, bgcolor: '#FCEDF5', borderRadius: 2}}
                          justifyContent={'center'}
                          alignItems={'center'}>
                          <NGIconFile
                            sx={{
                              color: 'Primary.main',
                              width: '22px',
                              height: '20px',
                              ml: 0.3,
                            }}
                          />
                        </Stack>
                        <Stack>
                          <NGText
                            text={
                              item.file.path.length - 4 > 12
                                ? item.file.path.slice(0, 12) + '...' + ' .pdf'
                                : item.file.path
                            }
                            myStyle={{...StyleConstant.textBold}}
                          />
                          <NGText text={`${item.pageCount}  pages`} />
                        </Stack>
                      </Stack>

                      <DeleteIcon
                        sx={{color: 'Primary.main', cursor: 'pointer'}}
                        onClick={() => {
                          setFileUpload(prev =>
                            prev.filter((value, i) => i !== index),
                          );
                        }}
                      />
                    </Stack>
                  </Box>
                </Grid>
              ))}
            </Grid>
          )}

          <NGText text={errorUpload} />
        </Stack>
        <NGDropzoneComponent
          accept={{'application/pdf': ['.pdf']}}
          alertMessage={alertMessage}
          handleUpload={handleUploadFile}
          handleUploadError={handleUploadError}
          supportFormatText={t(
            Localization('upload-document', 'documents-support'),
            {size: import.meta.env.VITE_MAX_FILE_SIZE_UPLOAD / 1000000},
          )}
        />
      </Stack>
    </Stack>
  );
};

export default UploadDocument;
