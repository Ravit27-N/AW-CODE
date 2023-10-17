import React, {Dispatch} from 'react';
import {Input, Stack, Typography} from '@mui/material';
import {SIZE_FILE_UPLOAD} from '@/constant/NGContant';
import {useAppSelector} from '@/redux/config/hooks';

function PreviewLogo({
  upload,
  setUpload,
}: {
  upload: {base64: string; file: File | null} | null;
  logo: string;
  setUpload: Dispatch<
    React.SetStateAction<{base64: string; file: File | null} | null>
  >;
  setSourceFile: Dispatch<React.SetStateAction<string>>;
  setAlertMessage: Dispatch<React.SetStateAction<string>>;
}) {
  const [errorFile, setErrorFile] = React.useState<string | null>(null);
  const corporateTheme = useAppSelector(state => state.enterprise).theme[0];
  const handleUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e?.target.files![0];
    const fileType = ['image/png'];

    if (file) {
      const {size, type} = file;
      if (fileType.indexOf(type) <= -1) {
        setUpload(null);
        return setErrorFile('file type not allowed.');
      }

      if (size < SIZE_FILE_UPLOAD.MIN && size > SIZE_FILE_UPLOAD.MAX) {
        setUpload(null);
        return setErrorFile('file size exceed.');
      }
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = async () => {
        setErrorFile(null);
        setUpload({file, base64: reader.result as string});
      };
    }
  };
  return (
    <>
      <Stack
        direction="row"
        width="100%"
        height="164px"
        sx={{
          border: '1px solid #E9E9E9',
          borderRadius: '4px',
          backgroundColor: '#FAFAFA',
          alignItems: 'center',
          display: 'flex',
          justifyContent: 'center',
          padding: '16px',
        }}>
        {/* Preview image */}

        {upload && (
          <img src={upload.base64} width="auto" height="21px" alt={'base64'} />
        )}
        <Typography
          component={'span'}
          sx={{
            fontSize: '12px',
            fontFamily: 'Poppins',
            color: corporateTheme.mainColor,
          }}>
          {errorFile}
        </Typography>
        <Input
          sx={{position: 'absolute', opacity: 0}}
          type={'file'}
          onChange={handleUpload}
        />
      </Stack>
    </>
  );
}

export default PreviewLogo;
