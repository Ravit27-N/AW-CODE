import {
  FONT_TYPE,
  Participant,
  SIGNING_PROCESS,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {
  useGetProjectByFlowIdQuery,
  useViewDocumentQuery,
} from '@/redux/slides/process-control/internal/processControlSlide';
import {
  IGetFlowId,
  useGetParticipantProjectQuery,
} from '@/redux/slides/project-management/project';
import {HandleException} from '@/utils/common/HandleException';
import {pixelToRem} from '@/utils/common/pxToRem';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
import img from '@assets/image/task-list 2.png';
import NGText from '@components/ng-text/NGText';
import {TabContext, TabList, TabPanel} from '@mui/lab';
import {
  Backdrop,
  CircularProgress,
  Grid,
  Stack,
  Tab,
  Typography,
  styled,
} from '@mui/material';
import {Box} from '@mui/system';

import {closeSnackbar, enqueueSnackbar} from 'notistack';
import React from 'react';
import {useTranslation} from 'react-i18next';
import EmptySection from './assigned-project/EmptySection';

import ViewFileToApprove from './assigned-project/process-control/approve/ViewFileToApprove';
import ViewFileToSignIndividual from './assigned-project/process-control/individual-sign/ViewFileToSignIndividual';
import DownloadSignDocument from './assigned-project/process-control/sign/DownloadSignDoc';
import ViewFileToSign from './assigned-project/process-control/sign/ViewFileToSign';
import ProjectCardEndUser, {
  AssignedProjectEndUserType,
} from './assigned-project/project-card';

const CustomTab = styled(Tab)(({theme}) => {
  return {
    fontFamily: 'Poppins',
    fontWeight: theme.typography.fontWeightMedium,
    fontSize: theme.typography.fontSize,
    textTransform: 'none',
  };
});

const AssignedProjectEndUser = () => {
  const {t} = useTranslation();
  const [value, setValue] = React.useState('1');
  const [selected, setSelected] = React.useState<null | string>(null);
  const [file, setFile] = React.useState<string | null>(null);
  const [storeData, setStoreData] = React.useState<
    Array<AssignedProjectEndUserType>
  >([]);
  const {data, error, isLoading} = useGetParticipantProjectQuery({
    status: value === '1' ? 'IN_PROGRESS' : 'DONE',
  });

  const [viewFile, setViewFile] = React.useState<{
    uuid: string;
    flowId: string;
  } | null>(null);

  /** Get project by flowId */
  const {
    currentData: projectByFlowId,
    isLoading: projectFlowIdLoading,
    isFetching: projectFlowIdFetching,
    error: projectFlowIdError,
  } = useGetProjectByFlowIdQuery(
    {
      flowId: viewFile?.flowId!,
      uuid: viewFile?.uuid!,
    },
    {
      skip: !viewFile,
    },
  );

  /** View document  */
  const {
    data: viewDocument,
    isLoading: viewDocLoading,
    isFetching: viewDocFetching,
  } = useViewDocumentQuery(
    {
      flowId: viewFile?.flowId!,
      docId: projectByFlowId?.documents[0].docId!,
    },
    {
      skip:
        !projectByFlowId ||
        !viewFile ||
        projectByFlowId.phoneNumber.totalAttempts >= 3,
    },
  );

  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setValue(newValue);
    setSelected(null);
  };

  const handleSelectCard = (d: AssignedProjectEndUserType) => {
    // select on the same card twice remove select
    setSelected(
      `${d.id}-${d.signatoryId}` === selected
        ? null
        : `${d.id}-${d.signatoryId}`,
    );
    setViewFile(prev =>
      `${d.id}-${d.signatoryId}` === selected
        ? null
        : {...prev, uuid: d.uuid, flowId: d.flowId},
    );
  };

  React.useMemo(() => {
    if (data) {
      const temp: Array<AssignedProjectEndUserType> = [];
      data.signatories.contents.forEach(item => {
        const {
          id,
          uuid,
          role,
          documentStatus,
          project: {
            id: projectId,
            createdAt,
            expireDate,
            flowId,
            createdByUser: {firstName, lastName},
            name,
          },
        } = item;

        temp.push({
          id: projectId,
          title: name,
          expireAt: `${expireDate}`,
          createdAt: `${createdAt}`,
          statue: role!,
          name: `${firstName} ${lastName ?? ' '}`,
          uuid,
          flowId,
          signatoryId: Number(id),
          documentStatus: documentStatus as any,
        });
      });

      setStoreData(temp);
    }
  }, [data]);

  React.useMemo(() => {
    if (viewDocument) {
      setFile(viewDocument);
    }
  }, [viewDocument]);

  React.useMemo(() => {
    if (projectFlowIdError) {
      enqueueSnackbar(
        HandleException((projectFlowIdError as any).statusCode) ?? UNKOWNERROR,
        {
          variant: 'errorSnackbar',
        },
      );
    }
  }, [projectFlowIdError]);

  React.useEffect(() => {
    if (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }

    return () => closeSnackbar();
  }, [error]);
  /** handle  **/
  const handleViewSign = (
    option: 'ViewFileToApprove' | 'ViewFileToSign' | 'ViewFileToSignIndividual',
    projectByFlowId: IGetFlowId,
  ) => {
    if (!projectByFlowId.actor.processed) {
      switch (option) {
        case 'ViewFileToApprove': {
          return (
            <ViewFileToApprove
              projectFlowId={projectByFlowId}
              storeData={
                storeData.find(
                  i =>
                    i.flowId === viewFile?.flowId && i.uuid === viewFile.uuid,
                )!
              }
              file={file}
            />
          );
        }
        case 'ViewFileToSign': {
          return (
            <ViewFileToSign
              projectFlowId={projectByFlowId}
              storeData={
                storeData.find(
                  i =>
                    i.flowId === viewFile?.flowId && i.uuid === viewFile.uuid,
                )!
              }
              file={file}
            />
          );
        }

        case 'ViewFileToSignIndividual': {
          return (
            <ViewFileToSignIndividual
              projectFlowId={projectByFlowId}
              storeData={
                storeData.find(
                  i =>
                    i.flowId === viewFile?.flowId && i.uuid === viewFile.uuid,
                )!
              }
              file={file}
            />
          );
        }
      }
    } else {
      return <DownloadSignDocument projectFlowById={projectByFlowId} />;
    }
  };
  return (
    <Stack width={'100%'} bgcolor={'white'} overflow={'auto'}>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={
          projectFlowIdLoading ||
          viewDocLoading ||
          projectFlowIdFetching ||
          viewDocFetching ||
          isLoading
        }>
        <CircularProgress color="inherit" />
      </Backdrop>
      <TabContext value={value}>
        <Box
          sx={{
            backgroundImage: `url(${bgLogo})`,
            backgroundRepeat: 'no-repeat',
            backgroundSize: 'cover',
            pt: 5,
            pb: 0,
            px: '72px',
            position: 'relative',
            borderBottom: 1,
            borderColor: 'divider',
          }}>
          <Stack gap={pixelToRem(32)}>
            <NGText
              text={`${t(
                Localization(
                  'end-user-assigned-project',
                  'my-assigned-projects',
                ),
              )}`}
              myStyle={{
                fontSize: pixelToRem(27),
                fontWeight: 600,
                lineHeight: pixelToRem(36),
                textTransform: 'capitalize',
              }}
            />
            <TabList onChange={handleChange} aria-label="lab API tabs example">
              <CustomTab
                disableRipple
                label={`${t(
                  Localization(
                    'end-user-assigned-project',
                    'for-signature-or-approval',
                  ),
                )} (${data?.totalInProgress ?? 0})`}
                value="1"
              />
              <CustomTab
                disableRipple
                label={`${t(
                  Localization(
                    'end-user-assigned-project',
                    'signed-or-approved',
                  ),
                )} (${data?.totalDone ?? 0})`}
                value="2"
              />
            </TabList>
          </Stack>
        </Box>
        {storeData.length ? (
          <Stack spacing={5} sx={{pl: '72px'}}>
            <Grid container>
              {/* Left side */}
              <Grid item xs={5}>
                <TabPanel value="1" sx={{pl: 0}}>
                  <Stack
                    gap={2}
                    sx={{
                      height: `calc(100vh - 270px)`,
                      overflow: 'scroll',
                      overflowX: 'hidden',
                      pr: '60px',
                    }}>
                    {storeData.map(item => {
                      const isSelected =
                        `${item.id}-${item.signatoryId}` === selected;

                      return (
                        <ProjectCardEndUser
                          active={isSelected}
                          data={item}
                          key={`${item.id}-${item.signatoryId}`}
                          onClick={handleSelectCard}
                        />
                      );
                    })}
                  </Stack>
                </TabPanel>
                <TabPanel value="2" sx={{pl: 0}}>
                  <Stack
                    gap={2}
                    sx={{
                      height: `calc(100vh - 270px)`,
                      overflow: 'scroll',
                      overflowX: 'hidden',
                      pr: '60px',
                    }}>
                    {storeData.map(item => {
                      const isSelected =
                        `${item.id}-${item.signatoryId}` === selected;

                      return (
                        <ProjectCardEndUser
                          active={isSelected}
                          data={item}
                          key={`${item.id}-${item.signatoryId}`}
                          onClick={handleSelectCard}
                        />
                      );
                    })}
                  </Stack>
                </TabPanel>
              </Grid>
              {/* Right side */}
              {selected && projectByFlowId ? (
                <Grid
                  container
                  item
                  xs={7}
                  direction="column"
                  gap="28px"
                  sx={{
                    height: `calc(100vh - 230px)`,
                  }}>
                  {projectByFlowId?.actor.role === Participant.Approval &&
                  [
                    SIGNING_PROCESS.COSIGN,
                    SIGNING_PROCESS.COUNTER_SIGN,
                  ].indexOf(projectByFlowId.signingProcess) > -1
                    ? handleViewSign('ViewFileToApprove', projectByFlowId)
                    : undefined}
                  {projectByFlowId?.actor.role === Participant.Signatory &&
                  [
                    SIGNING_PROCESS.COSIGN,
                    SIGNING_PROCESS.COUNTER_SIGN,
                  ].indexOf(projectByFlowId.signingProcess) > -1
                    ? handleViewSign('ViewFileToSign', projectByFlowId)
                    : undefined}

                  {projectByFlowId?.actor.role === Participant.Signatory &&
                  projectByFlowId.signingProcess ===
                    SIGNING_PROCESS.INDIVIDUAL_SIGN
                    ? handleViewSign(
                        'ViewFileToSignIndividual',
                        projectByFlowId,
                      )
                    : undefined}
                </Grid>
              ) : (
                <Grid
                  container
                  item
                  xs={7}
                  direction="column"
                  gap="28px"
                  sx={{
                    height: `calc(100vh - 265px)`,
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}>
                  <img src={img} width="158px" height="235px" alt="image" />
                  <Stack gap="8px" alignItems="center">
                    <Typography
                      sx={{
                        fontSize: '20px',
                        fontWeight: 500,
                        fontFamily: FONT_TYPE.POPPINS,
                      }}>
                      {t(
                        Localization(
                          'end-user-assigned-project',
                          'select-a-project',
                        ),
                      )}
                    </Typography>
                    <Typography
                      sx={{
                        fontSize: '14px',
                        fontFamily: FONT_TYPE.POPPINS,
                      }}>
                      {t(
                        Localization(
                          'end-user-assigned-project',
                          'need-to-select-a-project',
                        ),
                      )}
                    </Typography>
                  </Stack>
                </Grid>
              )}
            </Grid>
          </Stack>
        ) : (
          <EmptySection />
        )}
      </TabContext>
    </Stack>
  );
};

export default AssignedProjectEndUser;
