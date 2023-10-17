/**
 *  Component that display user shortened name in Navbar. When click show drop down menu of:
 *  - username
 *  - option to go to 'My Profile' page
 *  - option to 'log out'
 * */

import React from 'react';
import NgPopOver from '@components/ng-popover/NGPopOver';
import Avatar from '@mui/material/Avatar';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGText from '@components/ng-text/NGText';
import {shortName} from '@/utils/common/SortName';
import {Divider, Grid} from '@mui/material';
import {Center} from '@/theme';
import Stack from '@mui/material/Stack';
import {NGLogoutPopover, NGSettingDashboard} from '@assets/iconExport/Allicon';
import {Localization} from '@/i18n/lan';
import {logoutFn} from '@/redux/slides/keycloak/user';
import {isLogoutKey, refreshTokenKey} from '@/constant/NGContant';
import {signOut} from '@/redux/slides/authentication/authenticationSlide';
import {Route} from '@/constant/Route';
import Box from '@mui/material/Box';
import {useTranslation} from 'react-i18next';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {Navigate} from '@/utils/common';
import {useNavigate} from 'react-router-dom';

export default function NGProfile() {
  const user = useAppSelector(state => state.authentication.user);
  const {t} = useTranslation();
  const navigate = useNavigate();
  const [open, setOpen] = React.useState<boolean>(false);
  const dispatch = useAppDispatch();
  const handlerLogout = () => {
    logoutFn()
      .then(res => {
        if (res.status === 204) {
          localStorage.removeItem(refreshTokenKey);
          localStorage.setItem(isLogoutKey, 'true');
          dispatch(signOut());
          window.location.reload();
          return window.location.pathname === Route.LOGIN;
        }
      })
      .catch(e => e);
  };
  return (
    <Box>
      <NgPopOver
        open={open}
        onClick={() => setOpen(true)}
        onClose={() => setOpen(false)}
        button={
          <Avatar
            sx={{
              bgcolor: 'blue.light',
              fontSize: {md: 12, lg: 12},
              width: pixelToRem(32),
              height: pixelToRem(32),
              cursor: 'pointer',
            }}>
            <NGText
              text={shortName(user.username)}
              myStyle={{
                color: '#0080FF',
                fontSize: pixelToRem(11),
                fontWeight: 700,
                liineHeight: pixelToRem(16),
                textTransform: 'uppercase',
              }}
            />
          </Avatar>
        }
        Sx={{mt: pixelToRem(15)}}
        contain={
          // pop over in home user
          <Grid
            container
            padding={'12px'}
            gap={'8px'}
            width={pixelToRem(216)}
            // height={pixelToRem(140)}
            borderRadius={pixelToRem(6)}
            boxShadow={'0px 0px 11.2208px rgba(0, 0, 0, 0.1)'}
            bgcolor={'white'}
            spacing={pixelToRem(8)}>
            {/* First Line*/}
            <Grid item container lg={12} sx={{cursor: 'pointer'}}>
              <Grid item lg={3}>
                <Center height={'100%'}>
                  <Avatar
                    sx={{
                      bgcolor: 'blue.light',
                      fontSize: {md: 12, lg: 12},
                      width: pixelToRem(32),
                      height: pixelToRem(32),
                    }}>
                    <NGText
                      text={shortName(user.username)}
                      myStyle={{
                        color: '#0080FF',
                        fontSize: pixelToRem(11),
                        fontWeight: 700,
                        liineHeight: pixelToRem(16),
                      }}
                    />
                  </Avatar>
                </Center>
              </Grid>
              <Grid item lg={9}>
                <Stack justifyContent={'center'} height={'100%'}>
                  <NGText
                    text={user.username}
                    myStyle={{
                      fontSize: pixelToRem(14),
                      fontWeight: 400,
                      liineHeight: pixelToRem(24),
                    }}
                  />
                </Stack>
              </Grid>
            </Grid>
            <Divider sx={{width: '100%'}} />
            {/* Seconds Line*/}
            <Grid
              item
              container
              lg={12}
              sx={{cursor: 'pointer'}}
              onClick={() => {
                navigate(Navigate(Route.endUser.viewProfile));
                setOpen(false);
              }}>
              <Grid item lg={3}>
                <Center height={'100%'}>
                  <NGSettingDashboard />
                </Center>
              </Grid>
              <Grid item lg={9}>
                <Stack justifyContent={'center'} height={'100%'}>
                  <NGText
                    text={t(Localization('form', 'my-profile'))}
                    myStyle={{
                      fontSize: pixelToRem(14),
                      fontWeight: 400,
                      liineHeight: pixelToRem(24),
                    }}
                  />
                </Stack>
              </Grid>
            </Grid>
            {/* Third Line*/}
            <Grid
              item
              container
              lg={12}
              onClick={handlerLogout}
              sx={{cursor: 'pointer'}}>
              <Grid item lg={3}>
                <Center height={'100%'}>
                  <NGLogoutPopover sx={{color: '#CE0500'}} />
                </Center>
              </Grid>
              <Grid item lg={9}>
                <Stack justifyContent={'center'} height={'100%'}>
                  <NGText
                    text={t(Localization('form', 'logout'))}
                    myStyle={{
                      color: '#CE0500',
                      fontSize: pixelToRem(14),
                      fontWeight: 400,
                      liineHeight: pixelToRem(24),
                    }}
                  />
                </Stack>
              </Grid>
            </Grid>
          </Grid>
        }
      />
    </Box>
  );
}
