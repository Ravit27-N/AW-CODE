import logo from '@/assets/image/LOGO.png';
import NGProfile from '@/components/ng-profile/NGProfile';
import {Stack} from '@mui/material';
import {PropsWithChildren} from 'react';

const Layout = ({children}: PropsWithChildren) => {
  return (
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
      {children}
    </Stack>
  );
};

export default Layout;
