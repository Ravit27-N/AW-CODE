import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {storeProject} from '@/redux/slides/authentication/authenticationSlide';
import {useGetProjectByIdQuery} from '@/redux/slides/project-management/project';
import {Center} from '@/theme';
import ProjectContainer from '@components/page-project/ProjectContainer';
import {ProjectHeader} from '@components/page-project/header/ProjectHeader';
import {Stack, Typography} from '@mui/material';
import React from 'react';
import {useParams} from 'react-router-dom';
import ModifiedDate from '@pages/end-user/project-detail/actions/ModifiedDate';

export default function SignatureProjectDetail() {
  const {projectId} = useParams();
  const {projectDetailActions} = useAppSelector(state => state.authentication);
  const {
    currentData: data,
    isLoading,
    isSuccess,
    isFetching,
    error,
  } = useGetProjectByIdQuery({
    id: projectId!,
  });
  if (error) {
    // navigate(Route.ROOT);
  }
  React.useEffect(() => {
    if (projectId && isSuccess) {
      store.dispatch(
        storeProject({
          project: {
            id: projectId,
            name: store.getState().authentication.project.name!,
            orderSign: false,
            orderApprove: data!.orderApprove,
            step: data!.step.toString(),
          },
        }),
      );
    }
  }, [isSuccess]);

  return !isSuccess || isLoading || isFetching ? (
    <Center>
      <Typography>Loading ... </Typography>
    </Center>
  ) : (
    <Stack width={'100%'}>
      <ProjectHeader
        sxRight={{p: 0, height: '20vh'}}
        sxLeft={{p: 0, height: '20vh'}}
        data={data}
      />
      <ProjectContainer data={data} />
      <ModifiedDate
        projectId={projectId!}
        trigger={projectDetailActions['modified-date']}
        data={data}
      />
    </Stack>
  );
}
