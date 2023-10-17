import {NGAlertSuccessICon} from '@/assets/Icon';
import {SnackBarTimeout} from '@/constant/NGContant';
import {Alert} from '@mui/material';
import Snackbar from '@mui/material/Snackbar';
import {PropsWithChildren, ReactNode} from 'react';

type IAlertSuccess = {
  message: string | ReactNode;
  open: boolean;
  handleClose?: () => void;
};

export const AlertSuccess = (props: IAlertSuccess & PropsWithChildren) => {
  const {message, open, handleClose, children} = props;
  return (
    <Snackbar
      anchorOrigin={{vertical: 'bottom', horizontal: 'center'}}
      autoHideDuration={SnackBarTimeout}
      open={open}
      onClose={handleClose}>
      <Alert
        onClose={handleClose}
        iconMapping={{
          success: <NGAlertSuccessICon fontSize="inherit" />,
        }}>
        {message ?? children}
      </Alert>
    </Snackbar>
  );
};
