import {useAppSelector} from '@/redux/config/hooks';
import {Box, Stack} from '@mui/material';
import {PropsWithChildren} from 'react';

const InvitationLayout = ({
  children,
}: {position?: boolean} & PropsWithChildren) => {
  const {theme} = useAppSelector(state => state.enterprise);
  return (
    <>
      <Box sx={{p: '12px 16px'}}>
        <img
          src={theme[0].logo!}
          style={{maxWidth: '120px', height: '32px'}}
          alt={'Logo'}
        />
      </Box>
      <Stack
        sx={{
          width: '100%',
        }}>
        <Stack
          sx={{
            width: '100%',
            height: `calc(100vh - 65px)`,
          }}
          justifyContent={'flex-start'}>
          {children}
        </Stack>
      </Stack>
    </>
  );
};

export default InvitationLayout;
