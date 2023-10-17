import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import {SideBarListItemsCorporate} from '@components/ng-dashboard/NGlistItems';
import {Outlet} from 'react-router-dom';
import Stack from '@mui/material/Stack';
import {StyleConstant} from '@/constant/style/StyleConstant';
import NGText from '@components/ng-text/NGText';

export default function CompanyPage() {
  return (
    <Box sx={{overflow: 'hidden', height: '100vh'}}>
      <CssBaseline />
      <Box height={'100vh'} bgcolor={'white'}>
        <Stack height={'100%'} direction="row">
          <Box>
            <Stack
              sx={{
                padding: '24px',
                borderRight: 2,
                borderColor: '#E9E9E9',
                gap: '24px',
                width: '291px',
              }}>
              <NGText
                text={'Entreprise'}
                myStyle={{fontSize: 22, fontWeight: 600, lineHeight: '32px'}}
              />
              <SideBarListItemsCorporate />
            </Stack>
          </Box>
          <Box
            component="main"
            sx={{
              ...StyleConstant.layoutDashboard,
              bgcolor: 'whtie',
              height: 'auto',
            }}>
            <Stack
              sx={{
                borderRight: 2,
                borderColor: '#E9E9E9',
                width: '100%',
                height: '100%',
              }}>
              <Outlet />
            </Stack>
          </Box>
        </Stack>
      </Box>
    </Box>
  );
}
