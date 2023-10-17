import {UNKOWNERROR} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {useGetTemplatesQuery} from '@/redux/slides/profile/template/templateSlide';
import {HandleException} from '@/utils/common/HandleException';
import {Skeleton, Stack, Typography} from '@mui/material';
import {t} from 'i18next';
import {useSnackbar} from 'notistack';
import React from 'react';
import ModelsSections from './sidenav/Sections';
import SideNav from './sidenav/SideNav';

type IModelContent = {
  setCountModel: React.Dispatch<React.SetStateAction<number>>;
};

const ModelContent = (props: IModelContent) => {
  const {setCountModel} = props;
  const [search, setSearch] = React.useState<string>('');
  const {
    data: currentData,
    isError,
    error,
  } = useGetTemplatesQuery({
    filter: search,
  });
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

  React.useEffect(() => {
    if (currentData) {
      setCountModel(
        currentData
          .map(item => item.countTemplates)
          .reduce((partialSum, a) => partialSum + a, 0),
      );
    }
  }, [currentData]);

  return (
    <Stack direction="row" height={`calc(100vh - 211px)`}>
      {currentData || isError ? (
        <SideNav
          currentData={currentData ?? []}
          activeFolder={activeFolder}
          setActiveFolder={setActiveFolder}
        />
      ) : (
        <Stack
          p="24px 24px 24px 72px"
          gap="24px"
          height="100%"
          sx={{
            borderRight: '1px solid #E9E9E9',
          }}>
          <Typography
            sx={{
              fontSize: '14px',
              fontWeight: 600,
              fontFamily: 'Poppins',
            }}>
            {t(Localization('models-corporate', 'model-type'))}
          </Typography>
          <Stack gap="8px">
            {Array.from({length: 2}, (_, index) => (
              <Skeleton
                key={index}
                variant="rectangular"
                width="219px"
                sx={{
                  borderRadius: '6px',
                }}
                animation="wave"
                height={'35px'}
              />
            ))}
          </Stack>
        </Stack>
      )}

      {currentData || isError ? (
        <ModelsSections
          setSearch={setSearch}
          search={search}
          activeFolder={activeFolder}
          currentData={currentData ?? []}
        />
      ) : (
        <Stack p="0 40px 40px 40px" width="100%">
          <Stack
            direction="row"
            alignItems="center"
            justifyContent="space-between"
            sx={{
              p: '24px 0px 20px 0px',
            }}>
            <Skeleton
              variant="rectangular"
              width="390px"
              sx={{
                borderRadius: '6px',
              }}
              animation="wave"
              height={'35px'}
            />
            <Stack direction="row" gap="10px" alignItems="center">
              <Skeleton
                variant="rectangular"
                width="200px"
                sx={{
                  borderRadius: '6px',
                }}
                animation="wave"
                height={'35px'}
              />
            </Stack>
          </Stack>

          <Stack gap="8px">
            {Array.from({length: 3}, (_, index) => (
              <Skeleton
                key={index}
                variant="rectangular"
                width="100%"
                sx={{
                  borderRadius: '6px',
                }}
                animation="wave"
                height={'35px'}
              />
            ))}
          </Stack>
        </Stack>
      )}
    </Stack>
  );
};

export default ModelContent;
