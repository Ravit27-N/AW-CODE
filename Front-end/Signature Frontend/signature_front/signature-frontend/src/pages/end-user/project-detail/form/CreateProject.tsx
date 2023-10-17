import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {FONT_TYPE, UNKOWNERROR} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {
  TemplateInterface,
  useGetTemplatesCorporateQuery,
} from '@/redux/slides/profile/template/templateSlide';
import {HandleException} from '@/utils/common/HandleException';
import ClearIcon from '@mui/icons-material/Clear';
import {IconButton, Stack, Typography} from '@mui/material';
import {t} from 'i18next';
import {useSnackbar} from 'notistack';
import React from 'react';
import ProjectSections from './Sections';
import SideNav from './SideNav';

type ICreateProject = {
  open: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const CreateProject = (props: ICreateProject) => {
  const {open, setOpen} = props;
  const [search, setSearch] = React.useState<string>('');
  const {data: currentData, isError, error} = useGetTemplatesCorporateQuery();
  const {enqueueSnackbar, closeSnackbar} = useSnackbar();
  const [activeFolder, setActiveFolder] = React.useState<number | null>(null);

  React.useEffect(() => {
    if (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }

    return () => closeSnackbar();
  }, [error]);

  return (
    <NGDialog
      maxWidth={'xl'}
      sx={{
        '& .MuiPaper-root': {
          boxSizing: 'border-box',
          borderRadius: '16px',
        },
      }}
      sxProp={{
        titleSx: {
          p: 0,
          width: '1080px',
        },
        contentsSx: {
          p: 0,
          height: '611px',
        },
      }}
      open={open}
      titleDialog={
        <Stack
          border="1px solid #E9E9E9"
          direction="row"
          sx={{p: '16px 24px', height: '64px'}}
          justifyContent="space-between"
          alignItems="center">
          <Typography
            sx={{
              fontWeight: 600,
              fontSize: '18px',
              fontFamily: FONT_TYPE,
            }}>
            {t(Localization('project-detail', 'create-project'))}
          </Typography>
          <IconButton
            disableFocusRipple
            disableRipple
            disableTouchRipple
            onClick={() => setOpen(false)}>
            <ClearIcon sx={{color: 'Primary.main'}} />
          </IconButton>
        </Stack>
      }
      contentDialog={
        <ProjectContent
          search={search}
          setSearch={setSearch}
          currentData={currentData}
          activeFolder={activeFolder}
          setActiveFolder={setActiveFolder}
          isError={isError}
          setOpen={setOpen}
        />
      }></NGDialog>
  );
};

type IProjectContent = {
  currentData: TemplateInterface[] | undefined;
  setActiveFolder: React.Dispatch<React.SetStateAction<number | null>>;
  setSearch: React.Dispatch<React.SetStateAction<string>>;
  search: string;
  activeFolder: number | null;
  isError: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const ProjectContent = (props: IProjectContent) => {
  const {
    currentData,
    isError,
    activeFolder,
    setActiveFolder,
    search,
    setSearch,
    setOpen,
  } = props;
  return (
    <Stack
      direction="row"
      sx={{
        height: '611px',
      }}>
      {(currentData || isError) && (
        <SideNav
          currentData={currentData ?? []}
          activeFolder={activeFolder}
          setActiveFolder={setActiveFolder}
        />
      )}

      {(currentData || isError) && (
        <ProjectSections
          setOpen={setOpen}
          setSearch={setSearch}
          search={search}
          activeFolder={activeFolder}
          currentData={currentData ?? []}
        />
      )}
    </Stack>
  );
};

export default CreateProject;
