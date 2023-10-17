import {Outlet} from 'react-router-dom';
import AppProvider from './theme/AppProvider';
import {SnackbarProvider} from 'notistack';
import {SnackBarTimeout} from './constant/NGContant';
import {CustomSnackbar} from './components/ng-notistack';
import {
  NGErrorIcon,
  NGInfo,
  NGInfoSnackbar,
  NGSuccessSnackbar,
} from './assets/iconExport/Allicon';
import {makeStyles} from '@mui/styles';

const useStyles = makeStyles({
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

function App() {
  const classes = useStyles();
  return (
    <AppProvider>
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
        <Outlet />
      </SnackbarProvider>
    </AppProvider>
  );
}

export default App;
