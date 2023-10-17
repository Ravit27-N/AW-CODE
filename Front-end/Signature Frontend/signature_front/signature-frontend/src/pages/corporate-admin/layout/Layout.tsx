import {styled} from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import MuiAppBar, {AppBarProps as MuiAppBarProps} from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import {MainListItemsCorporate} from '@components/ng-dashboard/NGlistItems';
import {Outlet} from 'react-router-dom';
import {Center} from '@/theme';
import Search from '@components/ng-search/NGSearch';
import Stack from '@mui/material/Stack';
import {StyleConstant} from '@/constant/style/StyleConstant';
import logo from '@/assets/image/LOGO.png';
import React from 'react';
import {useAppSelector} from '@/redux/config/hooks';

import {FigmaBody} from '@/constant/style/themFigma/Body';
import NGProfile from '@components/ng-profile/NGProfile';
import {IsNullUuid} from '@/utils/common/IsNullUuid';

const drawerWidth = 240;

interface AppBarProps extends MuiAppBarProps {
  open?: boolean;
}

const AppBar = styled(MuiAppBar, {
  shouldForwardProp: prop => prop !== 'open',
})<AppBarProps>(({theme, open}) => ({
  zIndex: theme.zIndex.drawer + 1,
  transition: theme.transitions.create(['width', 'margin'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

export default function DashboardContentCorporate() {
  const [setPopup] = React.useState(false);
  const {theme} = useAppSelector(state => state.enterprise);

  IsNullUuid();

  return (
    <Box sx={{height: '100vh'}}>
      <CssBaseline />
      <AppBar
        position="static"
        elevation={0}
        sx={{
          bgcolor: 'transparent',
          height: '65px',
          borderBottom: 1,
          borderColor: '#E9E9E9',
        }}>
        <Box sx={{bgcolor: 'white'}}>
          <Toolbar
            sx={{
              pr: '24px',
            }}>
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                flexGrow: 1,
              }}>
              <img
                src={`${theme[0].logo ?? logo}`}
                alt={`LOGO`}
                style={{...FigmaBody.BodyMediumBold, color: 'black'}}
                loading="lazy"
                width={'auto'}
                height={'32px'}
              />
              <Stack
                spacing={2}
                justifyContent={'center'}
                alignItems={'center'}
                direction="row">
                <Search />
              </Stack>
              <NGProfile />
            </Box>
          </Toolbar>
        </Box>
      </AppBar>
      <Box sx={{height: `calc(100vh - 65px)`}}>
        <Stack height={'100%'} direction="row">
          <Box width={'60px'}>
            <Center
              sx={{
                height: '100%',
                pt: '40%',
                borderRight: 2,
                borderColor: '#E9E9E9',
              }}>
              <MainListItemsCorporate />
            </Center>
          </Box>
          <Box
            component="main"
            sx={{
              ...StyleConstant.layoutDashboard,
              bgcolor: 'whtie',
              height: '100%',
            }}>
            <Outlet context={{setPopup}} />
          </Box>
        </Stack>
      </Box>
    </Box>
  );
}
