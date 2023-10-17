import {NGPlusIcon} from '@/assets/Icon';
import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGDropzoneComponent from '@/components/ng-dropzone/NGDropzone.component';
import NGText from '@/components/ng-text/NGText';
import {colorDisable} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useCreateUserByCsvMutation} from '@/redux/slides/corporate-admin/corporateUserSlide';
import AttachFileIcon from '@mui/icons-material/AttachFile';
import {Alert, Stack} from '@mui/material';
import {t} from 'i18next';
import React from 'react';
import {DropEvent, FileRejection} from 'react-dropzone';

export type IModifiedDate = {
  trigger: boolean;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

const AddUserCSV = (props: IModifiedDate) => {
  const {trigger, setTrigger} = props;
  const [addUser, {isLoading}] = useCreateUserByCsvMutation();
  const [file, setFile] = React.useState<File[]>([]);
  const [alertMessage] = React.useState<JSX.Element | null>(null);

  const onClose = (): void => {
    setTrigger(false);
    setFile([]);
  };

  const handleUpload = (files: File[], event: DropEvent): void => {
    setFile([files[0]]);
  };

  const handleUploadError = (
    fileRejecttions: FileRejection[],
    event: DropEvent,
  ): void => {
    console.log();
  };

  const onSubmit = async () => {
    const formData = new FormData();
    formData.append('file', file[0]);
    try {
      await addUser(formData).unwrap();
    } catch (error) {
      return error;
    }
  };
  return (
    <Stack>
      <NGDialog
        sx={{
          '& .MuiPaper-root': {
            borderRadius: '16px',
          },
        }}
        open={trigger}
        sxProp={{
          titleSx: {
            p: '20px',
          },
          contentsSx: {
            py: '0',
          },
          actionSx: {
            padding: '14px 24px',
          },
        }}
        titleDialog={<ModifiedTitle />}
        contentDialog={
          <ModifiedContent
            setFile={setFile}
            files={file}
            alertMessage={alertMessage}
            handleUpload={handleUpload}
            handleUploadError={handleUploadError}
          />
        }
        actionDialog={
          <ModifiedActions
            isLoading={isLoading}
            files={file}
            onClose={onClose}
            onSubmit={onSubmit}
          />
        }
      />
    </Stack>
  );
};

const ModifiedTitle = () => {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  return (
    <Stack gap={'12px'} alignItems={'center'} direction={'row'}>
      <NGPlusIcon
        sx={{
          height: '20px',
          mt: '-1px',
          color: activeColor,
        }}
      />
      <NGText
        myStyle={{width: '517px', height: '28px'}}
        text={t(Localization('corporate-form', 'import-a-user-file'))}
        fontSize={'18px'}
        fontWeight="600"
      />
    </Stack>
  );
};
type IModifiedContent = {
  files: File[];
  setFile: React.Dispatch<React.SetStateAction<File[]>>;
  handleUpload: (file: File[], event: DropEvent) => void;
  handleUploadError: (
    fileRejecttions: FileRejection[],
    event: DropEvent,
  ) => void;
  alertMessage: JSX.Element | null;
};

const ModifiedContent = (props: IModifiedContent) => {
  const {handleUpload, handleUploadError, alertMessage, files, setFile} = props;

  return (
    <Stack justifyContent={'center'} spacing={1} py={'24px'}>
      <NGDropzoneComponent
        multiple={false}
        accept={{
          'text/csv': ['.csv'],
        }}
        sx={[{height: '164px', py: 4}]}
        title={t(Localization('corporate-form', 'drag-drop-file'))!}
        field={t(Localization('corporate-form', 'select-file'))!}
        supportFormatText={
          t(Localization('corporate-form', 'support-csv')) + 'CSV'
        }
        alertMessage={alertMessage}
        handleUpload={handleUpload}
        handleUploadError={handleUploadError}
      />
      {files
        ? files.map((f, index) => (
            <Alert
              key={f.name + f.lastModified + f.size}
              icon={<AttachFileIcon sx={{fontSize: '15px'}} />}
              onClose={() => setFile(files.filter((f, i) => i !== index))}
              severity="warning"
              sx={{
                alignItems: 'center',
                fontSize: '12px',
                fontWeight: 500,
                fontFamily: 'Poppins',
              }}>
              {f.name}
            </Alert>
          ))
        : undefined}
    </Stack>
  );
};

type IModifiedActions = {
  files: File[];
  isLoading: boolean;
  onSubmit: () => void;
  onClose: () => void;
};

const ModifiedActions = (props: IModifiedActions) => {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  const {isLoading, onSubmit, onClose, files} = props;
  return (
    <Stack gap={'10px'} width={'100%'} justifyContent={'center'}>
      <Stack direction={'row'} justifyContent={'flex-end'} gap={'10px'}>
        <NGButton
          onClick={onClose}
          disabled={isLoading}
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          locationIcon="start"
          color={['#ffffff', '#000000']}
          variant="outlined"
          fontSize="11px"
          myStyle={{
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            width: '77px',
            height: '36px',
            borderRadius: '6px',
            border: '1px solid #000000',
            // '&.MuiButtonBase-root': {
            '&:hover': {
              borderColor: activeColor ?? 'info.main',
              // '.MuiTypography-root': {color: activeColor ?? 'info.main'},
            },
            // },
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('upload-document', 'cancel'))}
        />
        <NGButton
          onClick={onSubmit}
          type={'submit'}
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          disabled={!files || isLoading}
          locationIcon="end"
          color={['#ffffff', '#ffffff']}
          variant="contained"
          fontSize="11px"
          myStyle={{
            '&.Mui-disabled': {
              bgcolor: colorDisable,
            },
            '&.MuiButtonBase-root': {
              borderColor: activeColor ?? 'Primary.main',
            },
            bgcolor: activeColor ?? 'Primary.main',
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            height: '36px',
            borderRadius: '6px',
            borderColor: '#000000',
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('enterprise-services', 'add'))}
        />
      </Stack>
    </Stack>
  );
};

export default AddUserCSV;
