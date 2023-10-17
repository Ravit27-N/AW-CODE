import logo from '@/assets/image/LOGO.png';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {useAppSelector} from '@/redux/config/hooks';
import {Center} from '@/theme';
import {MainListItemsSuperAdmin} from '@components/ng-dashboard/NGlistItems';
import MuiAppBar, {AppBarProps as MuiAppBarProps} from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import {styled} from '@mui/material/styles';
import Toolbar from '@mui/material/Toolbar';
import React from 'react';
import {Outlet, useNavigate} from 'react-router-dom';

import {FigmaBody} from '@/constant/style/themFigma/Body';
import {NGArrowLeft} from '@assets/iconExport/ExportIcon';
import NGProfile from '@components/ng-profile/NGProfile';
import {Route} from '@constant/Route';
import {IconButton} from '@mui/material';

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

export default function ContentSuperAdmin({ResizeWindow}: any) {
  const [setPopup] = React.useState(false);
  const {theme} = useAppSelector(state => state.enterprise);
  const navigate = useNavigate();
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
            disableGutters
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
              <Stack direction={'row'} alignItems={'center'} spacing={'5px'}>
                <Stack
                  width={'60px'}
                  justifyContent={'center'}
                  alignItems={'center'}
                  sx={{
                    height: `calc(100vh - (100vh - 64px))`,
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
                        color: theme[0].mainColor,
                        fontSize: '30px',
                        ml: '10px',
                      }}
                    />
                  </IconButton>
                </Stack>
                <img
                  src={`${theme[0].logo ?? logo}`}
                  alt={`LOGO`}
                  style={{...FigmaBody.BodyMediumBold, color: 'black'}}
                  loading="lazy"
                  width={'auto'}
                  height={'32px'}
                />
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
              <MainListItemsSuperAdmin />
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
