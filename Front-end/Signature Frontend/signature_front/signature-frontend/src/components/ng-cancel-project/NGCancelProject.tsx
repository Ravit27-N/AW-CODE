import {Localization} from '@/i18n/lan';
import {useCancelProjectMutation} from '@/redux/slides/project-management/project';
import {
  Backdrop,
  Button,
  CircularProgress,
  Stack,
  SxProps,
  Typography,
} from '@mui/material';
import {t} from 'i18next';
import {enqueueSnackbar} from 'notistack';
import NGDialog from '../ng-dialog-corporate/NGDialog';
import {useTranslation} from 'react-i18next';
import {ProjectStatusInterfaces} from '@components/ng-switch-case-status/interface';

type IData = {
  projectName: string;
  projectId: string;
  completions?: keyof ProjectStatusInterfaces;
};

export const CancelProjectDialog = ({
  data,
  open,
  setCancelProject,
  setFieldCancelProject,
}: {
  data: IData;
  open: boolean;
  setCancelProject: React.Dispatch<React.SetStateAction<boolean>>;
  setFieldCancelProject?: React.Dispatch<
    React.SetStateAction<{
      id: number;
      status: keyof ProjectStatusInterfaces | 'NONE';
    }>
  >;
}) => {
  /** cancel project end-point */
  const [cancelProjectMutation, {isLoading: cancelProjectLoading}] =
    useCancelProjectMutation();
  const {t} = useTranslation();

  const handleCancelProjectMutation = async (): Promise<void> => {
    try {
      await cancelProjectMutation({
        projectId: data.projectId,
      }).unwrap();
      setFieldCancelProject!({
        id: Number(data.projectId),
        status: data.completions as any,
      });
      enqueueSnackbar(
        t(Localization('messageAlertError', 'cancel-project-success'), {
          nameProject: data.projectName,
        }),
        {
          variant: 'successSnackbar',
        },
      );
      setCancelProject(false);
    } catch (error) {
      /** error 403  */
      if ((error as any).data.error.statusCode === 403) {
        enqueueSnackbar(
          t(Localization('messageAlertError', 'cannot-cancel-project')),
          {
            variant: 'errorSnackbar',
          },
        );
      } else {
        /** empty */
        enqueueSnackbar((error as any).data.error.message, {
          variant: 'errorSnackbar',
        });
      }
    }
  };

  return (
    <NGDialog
      open={open}
      sx={{
        '& .MuiPaper-root': {
          boxSizing: 'border-box',
          borderRadius: '16px',
          padding: '50px',
          gap: '20px',
        },
      }}
      sxProp={{
        contentsSx: {
          p: 0,
        },
      }}
      contentDialog={
        <CancelProjectContentDialog projectName={data.projectName} />
      }
      actionDialog={
        <CancelProjectActionDialog
          setCancelProject={setCancelProject}
          handleCancelProjectMutation={handleCancelProjectMutation}
          cancelProjectLoading={cancelProjectLoading}
        />
      }
    />
  );
};

const CancelProjectContentDialog = ({projectName}: {projectName: string}) => {
  return (
    <Stack gap="32px">
      <Stack gap="20px" alignItems="center">
        {/* Title */}
        <Typography
          sx={{
            fontWeight: 600,
            fontSize: 27,
          }}>
          {t(Localization('cancel-project', 'cancel-project'))}
        </Typography>

        {/* Description */}
        <Stack direction="row">
          <Typography
            component="span"
            sx={{
              fontSize: 18,
              textAlign: 'center',
            }}>
            {t(Localization('cancel-project', 'action-description'))}
            <Typography
              component="p"
              sx={{
                fontWeight: 600,
                fontSize: 16,
                display: 'inline',
              }}>
              {` " ${projectName} " ?`}
            </Typography>
          </Typography>
        </Stack>
      </Stack>
    </Stack>
  );
};

const CancelProjectActionDialog = ({
  setCancelProject,
  handleCancelProjectMutation,
  cancelProjectLoading,
}: {
  setCancelProject: React.Dispatch<React.SetStateAction<boolean>>;
  handleCancelProjectMutation: () => Promise<void>;
  cancelProjectLoading: boolean;
}) => {
  const btnStyle: SxProps = {
    width: '240px',
    height: '56px',
    textTransform: 'none',
    borderRadius: '6px',
    fontSize: 16,
    fontWeight: 600,
  };

  return (
    <Stack direction="row" gap="20px" alignItems="center">
      {/* cancel button */}
      <Button
        variant="outlined"
        onClick={() => setCancelProject(false)}
        sx={{
          color: '#000000',
          borderColor: '#000000',
          borderWidth: '1.5px',
          ...btnStyle,
        }}>
        {t(Localization('upload-document', 'cancel'))}
      </Button>

      {/* confirm button */}
      <Button
        onClick={handleCancelProjectMutation}
        variant="contained"
        sx={{
          ...btnStyle,
        }}>
        {t(Localization('cancel-project', 'confirm-cancel'))}
      </Button>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.modal + 1}}
        open={cancelProjectLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};
