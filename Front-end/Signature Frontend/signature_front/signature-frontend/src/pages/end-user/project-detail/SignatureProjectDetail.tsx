import {Route} from '@/constant/Route';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {storeProject} from '@/redux/slides/authentication/authenticationSlide';
import {useGetProjectByIdQuery} from '@/redux/slides/project-management/project';
import {Center} from '@/theme';
import ProjectContainer from '@components/page-project/ProjectContainer';
import {ProjectHeader} from '@components/page-project/header/ProjectHeader';
import {Stack, Typography} from '@mui/material';
import React from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import ModifiedDate from './actions/ModifiedDate';

export default function SignatureProjectDetail() {
  const {id} = useParams();
  const {projectDetailActions} = useAppSelector(state => state.authentication);
  const navigate = useNavigate();
  const {
    currentData: data,
    isLoading,
    isSuccess,
    isFetching,
    error,
  } = useGetProjectByIdQuery({
    id: id!,
  });
  if (error) {
    navigate(Route.ROOT);
  }
  React.useEffect(() => {
    if (id && isSuccess) {
      store.dispatch(
        storeProject({
          project: {
            id,
            name: store.getState().authentication.project.name!,
            orderSign: false,
            orderApprove: data!.orderApprove,
            step: '',
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
        projectId={id!}
        trigger={projectDetailActions['modified-date']}
        data={data}
      />
    </Stack>
  );
}
