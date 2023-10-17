import logo from '@/assets/image/LOGO.png';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {Center} from '@/theme';
import {MainListItemsSuperAdmin} from '@components/ng-dashboard/NGlistItems';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import React from 'react';
import {Outlet, useNavigate} from 'react-router-dom';

import {setCompanyProviderTheme} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import CompanyProvider from '@/theme/CompanyProvider';
import {NGArrowLeft} from '@assets/iconExport/ExportIcon';
import {Route} from '@constant/Route';
import {IconButton} from '@mui/material';

export default function ContentSuperAdmin() {
  const [setPopup] = React.useState(false);
  const {companyProviderTheme} = useAppSelector(state => state.enterprise);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  React.useEffect(() => {
    if (companyProviderTheme.logo) {
      if (companyProviderTheme.logo.split('=')[1] === 'null') {
        dispatch(
          setCompanyProviderTheme({
            companyProviderTheme: {...companyProviderTheme, logo},
          }),
        );
      }
    }
  }, [companyProviderTheme.logo]);

  return (
    <CompanyProvider>
      <Box sx={{height: '100vh'}}>
        <CssBaseline />

        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexGrow: 1,
            borderBottom: 1,
            borderColor: '#E9E9E9',
            height: '56px',
          }}>
          <Stack
            direction={'row'}
            alignItems={'center'}
            spacing={'5px'}
            width={'100%'}>
            <Stack
              width={'60px'}
              height="100%"
              justifyContent={'center'}
              alignItems={'center'}
              sx={{
                height: '56px',
                borderRight: 2,
                borderColor: '#E9E9E9',
              }}>
              <IconButton
                onClick={() => navigate(Route.HOME_SUPER)}
                disableFocusRipple
                disableRipple
                disableTouchRipple>
                <NGArrowLeft
                  sx={{
                    color: 'Primary.main',
                    fontSize: '30px',
                    ml: '10px',
                  }}
                />
              </IconButton>
            </Stack>
            <img
              src={`${companyProviderTheme.logo}`}
              alt={`LOGO`}
              loading="lazy"
              width={'auto'}
              height={'32px'}
            />
          </Stack>
        </Box>
        <Box sx={{height: `calc(100vh - 56px)`}}>
          <Stack height={'100%'} direction="row">
            <Box width={'60px'}>
              <Center
                sx={{
                  height: '100%',
                  pt: '40%',
                  borderRight: 2,
                  borderColor: '#E9E9E9',
                }}>
                <MainListItemsSuperAdmin />
              </Center>
            </Box>
            <Box
              component="main"
              sx={{
                ...StyleConstant.layoutDashboard,
                bgcolor: 'white',
                height: '100%',
              }}>
              <Outlet context={{setPopup}} />
            </Box>
          </Stack>
        </Box>
      </Box>
    </CompanyProvider>
  );
}
