import {PropsWithChildren} from 'react';
import {makeStyles} from '@mui/styles';
import {SnackbarProvider} from 'notistack';
import {CustomSnackbar} from '@/components/ng-notistack';
import {
  NGErrorIcon,
  NGInfo,
  NGInfoSnackbar,
  NGSuccessSnackbar,
} from '@assets/iconExport/ExportIcon';
import {SnackBarTimeout} from '@/constant/NGContant';

export const useSnackbarStyles = makeStyles({
  center: {
    '@media (min-width:600px)': {
      alignItems: 'normal',
      width: '70%',
    },
    '@media (min-width:1200px)': {
      alignItems: 'normal',
      width: '50%',
    },
  },
});

const CustomSnackbarProvider = ({children}: PropsWithChildren) => {
  const classes = useSnackbarStyles();
  return (
    <SnackbarProvider
      Components={{
        infoSnackbar: CustomSnackbar,
        warningSnackbar: CustomSnackbar,
        successSnackbar: CustomSnackbar,
        errorSnackbar: CustomSnackbar,
      }}
      classes={{containerRoot: classes.center}}
      iconVariant={{
        /** In the future should update to map color, so we don't have to change color in two places,
          one in Icon component, and another in CustomSnackbar.*/
        infoSnackbar: (
          <NGInfoSnackbar sx={{color: 'Info.main', mr: 1}} fontSize="small" />
        ),
        warningSnackbar: (
          <NGInfo sx={{color: 'Warning.main', mr: 1}} fontSize="small" />
        ),
        successSnackbar: (
          <NGSuccessSnackbar
            sx={{color: 'Success.main', mr: 1}}
            fontSize="small"
          />
        ),
        errorSnackbar: (
          <NGErrorIcon
            sx={{color: 'ColorDisabled.main', mr: 1}}
            fontSize="small"
          />
        ),
      }}
      action={null}
      anchorOrigin={{horizontal: 'center', vertical: 'bottom'}}
      maxSnack={2}
      autoHideDuration={SnackBarTimeout}>
      {children}
    </SnackbarProvider>
  );
};

export default CustomSnackbarProvider;
