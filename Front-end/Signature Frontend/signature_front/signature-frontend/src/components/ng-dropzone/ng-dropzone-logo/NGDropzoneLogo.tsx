import {DIMENSIONS_LOGO_UPLOAD} from '@/constant/NGContant';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {NGBinCycle, NGRecycle} from '@assets/Icon';
import {NGButton} from '@components/ng-button/NGButton';
import NGDropzoneComponent from '@components/ng-dropzone/NGDropzone.component';
import NGText from '@components/ng-text/NGText';
import {Stack} from '@mui/material';
import Box from '@mui/material/Box';
import {alertConsole} from '@pages/form/process-upload/common';
import {fromEvent} from 'file-selector';
import React, {Dispatch, useEffect, useMemo, useState} from 'react';
import {DropEvent, FileRejection} from 'react-dropzone';
import {useTranslation} from 'react-i18next';

export interface LogoFile extends File {
  width?: number;
  height?: number;
  preview?: string;
}

export interface LogoFileType {
  preview: string;
  file: LogoFile | null;
}

type DropzoneLogoType = {
  logo: LogoFileType;
  setLogo: Dispatch<React.SetStateAction<LogoFileType>>;
};

export const initialLogoFileType: LogoFileType = {
  preview: '',
  file: null,
};

const NGDropzoneLogo = ({logo, setLogo}: DropzoneLogoType) => {
  const {t} = useTranslation();
  const [alertMessage, setAlertMessage] = React.useState<JSX.Element | null>(
    null,
  );
  const reduxTheme = useAppSelector(state => state.enterprise);

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  const [open, setOpen] = useState<VoidFunction>(() => {});
  const isFileExist = logo.preview.length > 0;
  const handleUploadLogo = async (files: LogoFile[]) => {
    if (files.length > 0) {
      const preview = files[0].preview;
      delete files[0].preview;
      setLogo({
        preview: preview ?? '',
        file: files[0],
      });
    }
    setAlertMessage(null);
  };

  const handleUploadError = (filesRejected: FileRejection[]) => {
    const {errors} = filesRejected[0];
    const {code, message} = errors[0];
    switch (code) {
      case 'too-many-files':
        setAlertMessage(
          alertConsole(
            'error',
            t(Localization('upload-logo', 'maximum-number-of-logos-reached')),
          ),
        );
        break;
      case 'file-invalid-type':
        setAlertMessage(
          alertConsole(
            'error',
            t(Localization('upload-logo', 'format-not-accepted')),
          ),
        );
        break;
      case 'logo-dimensions-error-message':
        setAlertMessage(alertConsole('error', message));
        break;
    }
  };

  const handleGetFilesFromEvent = async (event: DropEvent) => {
    const promises: Promise<LogoFile>[] = [];
    const files = (await fromEvent(event)) as LogoFile[];

    for (let index = 0; files.length > index; index++) {
      const promise: Promise<LogoFile> = new Promise(resolve => {
        const file = files[index];
        const type = file.type.split('/')[0];

        if (type === 'image') {
          const image = new Image();
          const preview = URL.createObjectURL(file);
          // add custom field to file
          image.onload = () => {
            file.width = image.width;
            file.height = image.height;
            file.preview = preview;
            resolve(file);
          };

          image.src = preview;
        } else {
          resolve(file);
        }
      });
      promises.push(promise);
    }

    return await Promise.all(promises);
  };

  const MAX_WIDTH = DIMENSIONS_LOGO_UPLOAD.MAX_WIDTH;
  const MAX_HEIGHT = DIMENSIONS_LOGO_UPLOAD.MAX_HEIGHT;

  const MIN_WIDTH = DIMENSIONS_LOGO_UPLOAD.MIN_WIDTH;
  const MIN_HEIGHT = DIMENSIONS_LOGO_UPLOAD.MIN_HEIGHT;

  const handleValidateFile = (file: LogoFile) => {
    const width = file?.width ?? -1;
    const height = file?.height ?? -1;
    if (
      width > MAX_WIDTH ||
      height > MAX_HEIGHT ||
      width < MIN_WIDTH ||
      height < MIN_HEIGHT
    ) {
      return {
        code: 'logo-dimensions-error-message',
        message: t(
          Localization('upload-logo', 'logo-dimensions-error-message'),
        ),
      };
    }
    return null;
  };

  const renderImage = useMemo(
    () =>
      isFileExist && (
        <Box
          display="inline-flex"
          borderRadius={2}
          height={100}
          boxSizing="border-box"
          key={logo.preview}>
          <Box display="flex" minWidth={0} overflow="hidden">
            <Box
              component="img"
              alt="company-logo"
              src={logo.preview}
              p={1.5}
              height="100%"
              width={'100%'}
              display="flex"
              // Revoke data uri after image is loaded
              onLoad={() => {
                URL.revokeObjectURL(logo.preview);
              }}
            />
          </Box>
        </Box>
      ),
    [logo.preview],
  );

  useEffect(() => {
    return () => {
      // Make sure to revoke the data uris to avoid memory leaks, will run on unmount
      return URL.revokeObjectURL(logo.preview);
    };
  }, []);

  return (
    <Stack sx={{flex: 1, gap: 1}}>
      <NGDropzoneComponent
        accept={{'image/png': ['.png']}}
        alertMessage={alertMessage}
        handleUpload={handleUploadLogo}
        handleUploadError={handleUploadError}
        setOpen={setOpen}
        noClick={!isFileExist}
        multiple={false}
        getFilesFromEvent={handleGetFilesFromEvent}
        validator={handleValidateFile}
        render={isFileExist ? <Box>{renderImage}</Box> : undefined}
        supportFormatText={t(Localization('upload-logo', 'logo-support'))}
        sx={[
          isFileExist && {
            border: '1.4px solid',
            borderColor: theme => theme.palette.grey.A400,
            py: 5,
          },
        ]}
      />
      {isFileExist && (
        <Box>
          <NGText
            text={t(Localization('upload-logo', 'logo-support'))}
            myStyle={{
              ...StyleConstant.textSmall,
              color: 'black.main',
              fontSize: 12,
              display: 'block',
              mb: 1,
            }}
          />
          <Stack direction="row" gap={1}>
            <NGButton
              btnProps={{
                disableFocusRipple: true,
                disableRipple: true,
                disableTouchRipple: true,
              }}
              locationIcon="start"
              icon={<NGRecycle sx={{color: reduxTheme.theme[0].mainColor}} />}
              color={['grey.main', 'black.main']}
              variant="outlined"
              size="small"
              fontSize="11px"
              myStyle={{
                p: 0,
                '&.MuiButtonBase-root': {
                  '&:hover': {
                    borderStyle: 'none',
                  },
                  borderStyle: 'none',
                },
              }}
              fontWeight="600"
              onClick={open}
              title={t(Localization('superadmin-dashboard', 'change-logo'))}
            />
            <NGButton
              btnProps={{
                disableFocusRipple: true,
                disableRipple: true,
                disableTouchRipple: true,
              }}
              locationIcon="start"
              fontWeight="600"
              color={['grey.A400', 'black.main']}
              variant="outlined"
              size="small"
              fontSize="11px"
              icon={<NGBinCycle sx={{color: reduxTheme.theme[0].mainColor}} />}
              myStyle={{
                p: 0,
                '&.MuiButtonBase-root': {
                  '&:hover': {
                    borderStyle: 'none',
                  },
                  borderStyle: 'none',
                },
              }}
              onClick={() => setLogo(initialLogoFileType)}
              title={t(Localization('superadmin-dashboard', 'remove-logo'))}
            />
          </Stack>
          {alertMessage}
        </Box>
      )}
    </Stack>
  );
};

export default NGDropzoneLogo;
