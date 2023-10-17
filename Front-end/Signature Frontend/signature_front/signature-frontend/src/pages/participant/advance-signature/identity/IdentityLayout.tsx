import {Route} from '@/constant/Route';
import {useAppSelector} from '@/redux/config/hooks';
import {useGetProjectByFlowIdQuery} from '@/redux/slides/process-control/participant';
import {Box, Stack} from '@mui/material';
import React, {PropsWithChildren} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {validateSignatureAccessIdentity} from '../../common/checkRole';
import {IFetch} from '@pages/participant/advance-signature/identity/option-identity/validate-identity/ValidateIdentity';

const IdentityLayout = ({
  children,
  setData,
}: {
  position?: boolean;
  setData?: React.Dispatch<React.SetStateAction<IFetch | null>>;
} & PropsWithChildren) => {
  const {theme} = useAppSelector(state => state.enterprise);
  const navigate = useNavigate();
  const {id} = useParams();
  const queryParameters = new URLSearchParams(window.location.search);
  const {
    data: currentData,
    isLoading,
    isFetching,
  } = useGetProjectByFlowIdQuery({
    id: id + '?' + queryParameters,
  });

  React.useEffect(() => {
    if (setData) {
      setData({data: undefined, isLoading, isFetching});
    }
  }, []);

  React.useMemo(() => {
    if (currentData) {
      if (setData) {
        setData({data: currentData, isLoading: false, isFetching: false});
      }
      const {
        actor: {role},
        phoneNumber: {validated},
      } = currentData;
      const res = validateSignatureAccessIdentity(role);

      if (res) {
        navigate(`${res}/${id}?${queryParameters}`);
      } else if (!validated) {
        navigate(`${Route.participant.root}/${id}?${queryParameters}`);
      }
    }
  }, [currentData]);

  return (
    <>
      <Box sx={{p: '12px 16px', height: '56px'}}>
        <img
          src={theme[0].logo!}
          style={{maxWidth: '120px', height: '32px'}}
          alt={'Logo'}
          onClick={() => {
            history.go(-1);
          }}
        />
      </Box>
      <Stack
        sx={{
          width: '100%',
        }}>
        <Stack
          sx={{
            width: '100%',
            height: `calc(100vh - 56px)`,
            bgcolor: '#F2F5FC',
            py: '10px',
            px: '24px',
            overflow: 'hidden',
          }}
          justifyContent={'flex-start'}>
          {children}
        </Stack>
      </Stack>
    </>
  );
};

export default IdentityLayout;
