import {Alert, AlertColor} from '@mui/material';

/** Alert message*/
export const alertConsole = (color: AlertColor, message: string) => {
  return (
    <Alert sx={{bgcolor: 'transparent'}} severity={color}>
      {message}
    </Alert>
  );
};

/** Handle signature steps*/
