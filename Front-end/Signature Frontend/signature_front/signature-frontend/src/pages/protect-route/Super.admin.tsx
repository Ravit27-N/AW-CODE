import {Route} from '@/constant/Route';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {Navigate} from '@/utils/common';
import {UserRoleEnum} from '@/utils/request/interface/User.interface';
import React from 'react';
import {Outlet, useLocation, useNavigate} from 'react-router-dom';

export type ISuperOutletContext = {
  setToggleForm: React.Dispatch<React.SetStateAction<boolean>>;
  toggleForm: boolean;
};

export const SuperRoute = () => {
  const navigate = useNavigate();
  const {role} = useAppSelector(state => state.authentication);
  const [toggleForm, setToggleForm] = React.useState<boolean>(false);
  const location = useLocation();
  React.useEffect(() => {
    const handleRefresh = async () => {
      if (role !== UserRoleEnum.SUPERADMIN) {
        store.getState().authentication.role === UserRoleEnum.COPERATE_ADMIN
          ? navigate(Navigate(Route.HOME_CORPORATE))
          : store.getState().authentication.role === UserRoleEnum.ENDUSER
          ? navigate(Navigate(Route.HOME_ENDUSER))
          : store.getState().authentication.role === UserRoleEnum.SUPERADMIN &&
            navigate(Navigate(Route.HOME_SUPER));
      }
    };
    handleRefresh();
  }, [location, role]);

  return role === UserRoleEnum.SUPERADMIN ? (
    <Outlet context={{setToggleForm, toggleForm}} />
  ) : (
    <></>
  );
};
