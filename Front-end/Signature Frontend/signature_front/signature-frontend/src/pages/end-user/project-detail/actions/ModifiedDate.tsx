import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {projectDetailAction} from '@/redux/slides/authentication/authenticationSlide';
import {
  IProjectById,
  useUpdateProjectExpiredDateMutation,
} from '@/redux/slides/project-management/project';
import {
  Button,
  CircularProgress,
  Stack,
  SxProps,
  TextField,
} from '@mui/material';
import {DesktopDatePicker} from '@mui/x-date-pickers';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import dayjs, {Dayjs} from 'dayjs';
import {t} from 'i18next';
import React, {useState} from 'react';
import {useSnackbar} from 'notistack';
import {HandleException} from '@/utils/common/HandleException';
import {UNKOWNERROR} from '@/constant/NGContant';
import {IDType} from '@/utils/request/interface/type';

export type IModifiedDate = {
  trigger: boolean;
  projectId: IDType;
  data: IProjectById;
};

const ModifiedDate = (props: IModifiedDate) => {
  const {
    projectId,
    trigger,
    data: {name: projectName, expireDate = dayjs(new Date()).valueOf(), status},
  } = props;
  const {enqueueSnackbar} = useSnackbar();
  const [date, setModifiedDate] = useState<Dayjs | null>(
    dayjs(
      new Date(expireDate)
        .toLocaleDateString('zh-Hans-CN', {
          month: '2-digit',
          day: '2-digit',
          year: 'numeric',
        })
        .replace(/\//g, '-'),
    ),
  );
  const [modifiedDate, {isLoading}] = useUpdateProjectExpiredDateMutation();
  const onSubmitDate = async (): Promise<unknown> => {
    try {
      // show error popup if the project status other than IN_PROGRESS
      if (status !== 'IN_PROGRESS') {
        enqueueSnackbar(
          t(
            Localization(
              'project-detail',
              'expired-date-modified-error-status-message',
            ),
            {
              status: t(Localization('documentStatus', status)),
            },
          ),
          {
            variant: 'errorSnackbar',
          },
        );
        return;
      }

      await modifiedDate({
        id: projectId,
        expiredDate: date!.toISOString(),
      }).unwrap();
      enqueueSnackbar(
        t(Localization('project-detail', 'expired-date-modified-message'), {
          name: projectName,
        }),
        {
          variant: 'successSnackbar',
        },
      );
      return store.dispatch(projectDetailAction({'modified-date': false}));
    } catch (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  return (
    <Stack>
      <NGDialog
        sx={{
          '& .MuiPaper-root': {
            borderRadius: '16px',
            padding: '50px',
            gap: '32px',
          },
        }}
        sxProp={{
          titleSx: {
            padding: 0,
          },
          contentsSx: {
            padding: 0,
          },
          actionSx: {
            padding: 0,
          },
        }}
        open={trigger}
        titleDialog={<ModifiedTitle />}
        contentDialog={
          <ModifiedContent
            expireDate={dayjs(
              new Date(expireDate)
                .toLocaleDateString('zh-Hans-CN', {
                  month: '2-digit',
                  day: '2-digit',
                  year: 'numeric',
                })
                .replace(/\//g, '-'),
            )}
            setModifiedDate={setModifiedDate}
            date={date}
          />
        }
        actionDialog={
          <ModifiedActions onSubmit={onSubmitDate} isLoading={isLoading} />
        }
      />
    </Stack>
  );
};

const ModifiedTitle = () => {
  return (
    <Stack gap="20px" textAlign="center">
      <NGText
        text={t(Localization('project-detail', 'modified-date'))}
        myStyle={{
          fontWeight: 600,
          fontSize: '27px',
        }}
      />
      <NGText text={t(Localization('project-detail', 'expiry-date-field'))} />
    </Stack>
  );
};
type IModifiedContent = {
  expireDate: dayjs.Dayjs | null;
  setModifiedDate: React.Dispatch<React.SetStateAction<dayjs.Dayjs | null>>;
  date: dayjs.Dayjs | null;
};

const ModifiedContent = (props: IModifiedContent) => {
  const {setModifiedDate, date, expireDate} = props;
  const [limitedDate] = React.useState<Dayjs | null>(expireDate);

  const handleChangeValue = (value: Dayjs | null) => {
    setModifiedDate(value);
  };

  return (
    <Stack gap="6px">
      <NGText
        text={t(Localization('project-detail', 'date-expired'))}
        myStyle={{
          fontWeight: '500',
          fontSize: '13px',
        }}
      />
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DesktopDatePicker
          minDate={limitedDate!}
          value={date}
          inputFormat="MM/DD/YYYY"
          onChange={handleChangeValue}
          renderInput={(params: any) => (
            <TextField
              size="small"
              sx={{
                '& .MuiInputBase-root': {
                  borderRadius: '6px',
                  fieldset: {
                    borderColor: 'Primary.main',
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: 'Primary.main',
                    borderWidth: '0.2px',
                  },
                  '&:hover fieldset': {
                    borderColor: '#E9E9E9',
                  },
                  '& .MuiSelect-icon': {
                    color: 'black.main', // set the color of the arrow icon
                  },
                },
              }}
              {...params}
            />
          )}
        />
      </LocalizationProvider>
    </Stack>
  );
};

type IModifiedActions = {
  onSubmit: () => Promise<unknown>;
  isLoading: boolean;
};

const modifiedActionButtonSx: SxProps = {
  width: '240px',
  p: '16px 32px 16px 32px',
  fontWeight: 600,
  fontSize: '16px',
  borderRadius: '6px',
  textTransform: 'none',
};

const ModifiedActions = (props: IModifiedActions) => {
  const {onSubmit, isLoading} = props;
  return (
    <Stack
      direction="row"
      justifyContent="space-between"
      width="100%"
      gap="20px">
      <Button
        disabled={isLoading}
        onClick={() =>
          store.dispatch(projectDetailAction({'modified-date': false}))
        }
        variant="outlined"
        sx={{
          color: '#000000',
          borderColor: '#000000',
          ...modifiedActionButtonSx,
        }}>
        {t(Localization('upload-document', 'cancel'))}
      </Button>
      <Button
        onClick={onSubmit}
        disabled={isLoading}
        variant="contained"
        sx={{
          ...modifiedActionButtonSx,
        }}>
        {isLoading ? (
          <CircularProgress sx={{color: '#ffffff'}} size={'1.5rem'} />
        ) : (
          t(Localization('project-detail', 'changed-date'))
        )}
      </Button>
    </Stack>
  );
};

export default ModifiedDate;
