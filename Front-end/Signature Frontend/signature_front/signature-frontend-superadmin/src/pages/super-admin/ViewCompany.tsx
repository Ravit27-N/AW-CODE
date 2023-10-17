import CompanyProvider from '@/theme/CompanyProvider';
import {Button} from '@mui/material';
import {Outlet} from 'react-router-dom';

const ViewCompany = () => {
  return (
    <CompanyProvider>
      <Button variant="contained">ABC</Button>
      <Outlet />
    </CompanyProvider>
  );
};
export default ViewCompany;
