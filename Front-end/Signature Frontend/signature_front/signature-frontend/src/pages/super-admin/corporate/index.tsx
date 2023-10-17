import React from 'react';
import Button from '@mui/material/Button';
import {Center, VStack} from '@/theme';
import {useNavigate, useParams} from 'react-router-dom';
import CreateCorporateUserDialog from '@pages/super-admin/corporate/create-dialog';
import {Navigate} from '@/utils/common';

import {Route} from '@/constant/Route';
import {CorporateAdmin} from '@/redux/slides/super-admin/corporateAdminSlide';
import {useSnackbar} from 'notistack';

function SuperAdminCorporate() {
  const navigate = useNavigate();
  const [open, setOpen] = React.useState<boolean>(false);
  const [data, setData] = React.useState<CorporateAdmin>();
  const [companyId, setCompanyId] = React.useState<number>(NaN);
  const {id} = useParams();
  const {enqueueSnackbar, closeSnackbar} = useSnackbar();

  React.useEffect(() => {
    const tempId = Number(id);
    if (!isNaN(tempId)) {
      setCompanyId(tempId);
    } else {
      // display error when id is invalid
      enqueueSnackbar('Company Not Found', {variant: 'errorSnackbar'});
      setCompanyId(NaN);
    }

    return () => closeSnackbar();
  }, [id]);

  return (
    <Center>
      <VStack>
        <Button
          variant={'contained'}
          sx={{my: 3}}
          disabled={isNaN(companyId)}
          onClick={() => setOpen(true)}>
          Create corporate account
        </Button>
        <pre>{JSON.stringify(data, null, 2)}</pre>
        <Center>
          <CreateCorporateUserDialog
            companyId={companyId}
            open={open}
            onClose={() => setOpen(false)}
            onAddSuccess={(data: CorporateAdmin) => {
              setData(data);
            }}
          />
        </Center>
        <Button
          variant={'outlined'}
          sx={{my: 3}}
          onClick={() => {
            navigate(Navigate(Route.HOME_SUPER));
          }}>
          Back
        </Button>
      </VStack>
    </Center>
  );
}

export default SuperAdminCorporate;
