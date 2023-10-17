import {isLogoutKey, refreshTokenKey} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {signOut} from '@/redux/slides/authentication/authenticationSlide';
import {logoutFn} from '@/redux/slides/keycloak/user';
import {Button, ButtonTypeMap} from '@mui/material';
import {useDispatch} from 'react-redux';

const NGLogoutButton = ({props}: Partial<ButtonTypeMap>) => {
  const dispatch = useDispatch();
  const Logout = () =>
    logoutFn().then(res => {
      if (res.status === 204) {
        localStorage.removeItem(refreshTokenKey);
        localStorage.setItem(isLogoutKey, 'true');
        dispatch(signOut());
        window.location.reload();
        return window.location.pathname === Route.LOGIN;
      }
    });
  return (
    <Button {...props} variant={'contained'} color={'error'} onClick={Logout}>
      Log out
    </Button>
  );
};

export default NGLogoutButton;
