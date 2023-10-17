import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import {Button, IconButton, Stack, SxProps, Theme} from '@mui/material';
import {grey} from '@mui/material/colors';
import {fromEvent} from 'file-selector';
import {t} from 'i18next';
import React, {ReactNode, useEffect} from 'react';
import {
  DropEvent,
  Accept as DropzoneAcceptType,
  FileError,
  FileRejection,
  useDropzone,
} from 'react-dropzone';
import env from '../../../env.config';
import NGText from '../ng-text/NGText';
import {NGPlus} from '@/assets/Icon';

const focusedStyle = {
  borderColor: '#2196f3',
};

const acceptStyle = {
  borderColor: '#00e676',
};

const rejectStyle = {
  borderColor: '#ff1744',
};

type IDropzone = {
  handleUpload: (files: File[], event: DropEvent) => void;
  handleUploadError: (
    fileRejecttions: FileRejection[],
    event: DropEvent,
  ) => void;
  alertMessage: JSX.Element | null;
  getFilesFromEvent?: (
    event: DropEvent,
  ) => Promise<Array<File | DataTransferItem>>;
  validator?: <T extends File>(file: T) => FileError | FileError[] | null;
  accept?: DropzoneAcceptType;
  setOpen?: React.Dispatch<React.SetStateAction<VoidFunction>>;
  multiple?: boolean;
  supportFormatText?: string | ReactNode;
  noClick?: boolean;
  render?: JSX.Element;
  sx?: SxProps<Theme>;
  open?: boolean;
  title?: string;
  field?: string;
  contentSx?: SxProps<Theme>;
  proDetailstyle?: boolean;
};

const NGDropzoneComponent = ({
  supportFormatText = 'Support Format Text',
  handleUpload,
  handleUploadError,
  alertMessage,
  accept,
  setOpen,
  multiple = true,
  getFilesFromEvent = fromEvent,
  validator,
  noClick = true,
  render,
  sx = [],
  contentSx,
  proDetailstyle,
  title = t(Localization('upload-document', 'drag-and-drop-your-files-here'))!,
  field = t(Localization('upload-document', 'adding-files'))!,
}: IDropzone) => {
  const {
    getRootProps,
    getInputProps,
    open,
    isFocused,
    isDragAccept,
    isDragReject,
  } = useDropzone({
    noClick,
    noKeyboard: true,
    maxSize: env.VITE_MAX_FILE_SIZE_UPLOAD,
    maxFiles: env.VITE_MAX_FILES,
    accept,
    multiple,
    getFilesFromEvent,
    validator,
    onDropAccepted(files, event) {
      handleUpload(files, event);
    },
    onDropRejected(fileRejections, event) {
      handleUploadError(fileRejections, event);
    },
  });
  const style = React.useMemo(
    () => ({
      ...(isFocused ? focusedStyle : {}),
      ...(isDragAccept ? acceptStyle : {}),
      ...(isDragReject ? rejectStyle : {}),
    }),
    [isFocused, isDragAccept, isDragReject],
  );

  useEffect(() => {
    if (setOpen) {
      setOpen(() => open);
    }
  }, []);

  return (
    <Stack
      alignItems={'center'}
      justifyContent="center"
      sx={[
        {
          border: '2px dashed',
          // border: '1px dashed',
          borderColor: 'Primary.main',
          bgcolor: 'whiteColor.dark',
          borderRadius: 3,
          py: 8,
          ...style,
        },
        ...(Array.isArray(sx) ? sx : [sx]),
      ]}
      {...getRootProps({className: 'dropzone'})}>
      <Stack>
        <input {...getInputProps()} />
        {render ? (
          render
        ) : proDetailstyle ? (
          <Stack direction="row" gap="24px" alignItems="center">
            <IconButton
              onClick={open}
              disableFocusRipple
              disableRipple
              disableTouchRipple
              sx={{
                bgcolor: 'Primary.main',
                width: '32px',
                height: '32px',
                borderRadius: '30%',
              }}>
              <NGPlus
                sx={{
                  fontSize: '12px',
                  color: '#ffffff',
                }}
              />
            </IconButton>
            <Stack
              width="100%"
              alignItems={'center'}
              sx={{
                ...contentSx,
              }}>
              <NGText text={title} myStyle={{fontWeight: 600}} />

              <NGText
                text={supportFormatText}
                myStyle={{
                  ...StyleConstant.textSmall,
                  color: 'black.main',
                  fontSize: 14,
                  textAlign: 'center',
                }}
              />
              <NGText text={alertMessage} myStyle={{color: grey[500]}} />
            </Stack>
          </Stack>
        ) : (
          <Stack
            spacing={0.5}
            width="100%"
            alignItems={'center'}
            sx={{
              ...contentSx,
            }}>
            <NGText text={title} myStyle={{fontWeight: 800}} />

            <NGText
              text={supportFormatText}
              myStyle={{
                ...StyleConstant.textSmall,
                color: 'black.main',
                fontSize: 14,
                fontWeight: 500,
                textAlign: 'center',
              }}
            />
            <NGText text={alertMessage} myStyle={{color: grey[500]}} />

            <Button
              variant={'text'}
              sx={{fontWeight: 'bold', textTransform: 'none'}}
              onClick={open}
              endIcon={<AddOutlinedIcon sx={{color: 'Primary.main'}} />}>
              <NGText
                text={field}
                myStyle={{
                  color: 'Primary.main',
                  fontWeight: 600,
                  fontSize: 13,
                }}
              />
            </Button>
          </Stack>
        )}
      </Stack>
    </Stack>
  );
};

export default NGDropzoneComponent;
