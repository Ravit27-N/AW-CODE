import logo from '@/assets/image/LOGO.png';
import {StyleConstant} from '@/constant/style/StyleConstant';
import UploadForm from '@/pages/form/process-upload/Upload.form';
import {Center} from '@/theme';
import {pixelToRem} from '@/utils/common/pxToRem';
import {MainListItems} from '@components/ng-dashboard/NGlistItems';
import Search from '@components/ng-search/NGSearch';
import MuiAppBar, {AppBarProps as MuiAppBarProps} from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Toolbar from '@mui/material/Toolbar';
import {styled} from '@mui/material/styles';
import React from 'react';
import {Outlet} from 'react-router-dom';

import {FigmaBody} from '@/constant/style/themFigma/Body';
import {useAppSelector} from '@/redux/config/hooks';
import NGProfile from '@components/ng-profile/NGProfile';
import {useTranslation} from 'react-i18next';
import {IsNullUuid} from '@/utils/common/IsNullUuid';
import {Skeleton} from '@mui/material';

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

export default function DashboardContent({ResizeWindow}: any) {
  const [popUp, setPopup] = React.useState(false);
  const {t} = useTranslation();
  const [setTemplatePopup] = React.useState(false);
  const {theme} = useAppSelector(state => state.enterprise);

  const handleClosePopup = () => {
    setPopup(false);
  };
  IsNullUuid();
  return (
    <Box sx={{height: '100vh'}}>
      <AppBar
        position="static"
        sx={{
          bgcolor: 'transparent',
          height: '65px',
          borderBottom: 1,
          borderColor: '#E9E9E9',
        }}>
        <Box sx={{bgcolor: 'white'}}>
          <Toolbar
            sx={{
              pl: '12px',
              bgcolor: 'white',
              borderBottom: 1,
              borderColor: '#E9E9E9',
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
                loading="lazy"
                width={'auto'}
                height={'32px'}
                style={{...FigmaBody.BodyMediumBold, color: 'black'}}
              />
              <Stack
                spacing={2}
                justifyContent={'center'}
                alignItems={'center'}
                direction="row">
                <Search />
              </Stack>
              <Stack
                direction={'row'}
                spacing={2}
                height={pixelToRem(20)}
                justifyContent={'center'}
                alignItems={'center'}>
                <NGProfile />
              </Stack>
            </Box>
          </Toolbar>
        </Box>
      </AppBar>

      <Box bgcolor={'white'} sx={{height: `calc(100vh - 65px)`}}>
        <Stack height={'100%'} direction="row">
          <Box width={'60px'} height={'100%'}>
            <Center
              sx={{borderRight: 2, borderColor: '#E9E9E9', height: '100%'}}>
              {<MainListItems />}
            </Center>
          </Box>
          <Box
            component="main"
            sx={{
              ...StyleConstant.layoutDashboard,
            }}>
            <Outlet context={{setPopup, setTemplatePopup}} />
          </Box>
        </Stack>
      </Box>
      <UploadForm popUp={popUp} closePopup={handleClosePopup} />
    </Box>
  );
}
