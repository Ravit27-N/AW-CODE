import {Box, Grid, SxProps} from '@mui/material';
import {ProjectLeftSide} from '@components/page-project/header/ProjectLeftSide';
import {ProjectRightSide} from '@components/page-project/header/ProjectRightSide';
import bgLogo from '@assets/background/projectDetail/NGProjectDetailBackground.svg';
import {Center} from '@/theme';

export interface Type {
  sxLeft?: SxProps;
  sxRight?: SxProps;
  data?: any;
  isLoading?: boolean;
  isSuccess?: boolean;
}
export function ProjectHeader({data}: Type) {
  return (
    <Box>
      <Center
        sx={{
          width: 'full',
          backgroundImage: `url(${bgLogo})`,
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
          pt: {md: 2, lg: 5},
          position: 'relative',
        }}>
        <Grid container>
          <Grid item lg={8} md={8} sm={6} px={2} sx={{mb: 7, pl: 5}}>
            {/* Project detail left side */}
            <ProjectLeftSide data={data} />
          </Grid>
          <Grid item lg={4} md={4} sm={6} px={2}>
            {/* Project detail right side */}
            <ProjectRightSide data={data} />
          </Grid>
        </Grid>
      </Center>
    </Box>
  );
}
