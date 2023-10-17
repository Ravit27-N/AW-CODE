import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {Navigate} from '@/utils/common';
import {UserRoleEnum} from '@/utils/request/interface/User.interface';
import React from 'react';
import {useLocation, useNavigate} from 'react-router-dom';

import {Route} from '@/constant/Route';
import DashboardContent from '../end-user/layout/Layout';

export const EndUserRoute = () => {
  const navigate = useNavigate();
  const {role} = useAppSelector(state => state.authentication);
  const location = useLocation();
  React.useEffect(() => {
    const handleRefresh = async () => {
      if (role !== UserRoleEnum.ENDUSER) {
        store.getState().authentication.role === UserRoleEnum.COPERATE_ADMIN
          ? navigate(Navigate(Route.HOME_CORPORATE))
          : store.getState().authentication.role === UserRoleEnum.ENDUSER
          ? navigate(Navigate(Route.HOME_ENDUSER))
          : store.getState().authentication.role === UserRoleEnum.SUPERADMIN &&
            navigate(Navigate(Route.HOME_SUPER));
      }
    };
    handleRefresh();
  }, [location]);

  return role === UserRoleEnum.ENDUSER ? <DashboardContent /> : <></>;
};
