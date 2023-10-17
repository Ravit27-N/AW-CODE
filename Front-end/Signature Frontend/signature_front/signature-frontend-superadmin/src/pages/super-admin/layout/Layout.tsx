import NGProfile from '@/components/ng-profile/NGProfile';
import {useAppSelector} from '@/redux/config/hooks';
import {UserRoleEnum} from '@/utils/request/interface/User.interface';
import logo from '@assets/image/LOGO.png';
import {Stack} from '@mui/material';
import React from 'react';
import {Outlet} from 'react-router-dom';

const Layout = () => {
  const [toggleForm, setToggleForm] = React.useState<boolean>(false);
  const {role, userToken} = useAppSelector(state => state.authentication);

  return userToken && role === UserRoleEnum.SUPERADMIN ? (
    <Stack>
      <Stack
        direction="row"
        height="56px"
        sx={{
          borderBottom: 1,
          borderColor: '#E9E9E9',
          justifyContent: 'space-between',
          alignItems: 'center',
          px: '20px',
        }}>
        <img src={logo} alt="LOGO" loading="lazy" width="auto" height="32px" />
        <NGProfile />
      </Stack>
      <Outlet context={{setToggleForm, toggleForm}} />
    </Stack>
  ) : (
    <></>
  );
};

export default Layout;
