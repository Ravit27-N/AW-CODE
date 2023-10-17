import App from './App';

import {Route} from '@/constant/Route';
import NotFound404 from '@pages/NotFound404';
import {createBrowserRouter, Navigate} from 'react-router-dom';

import Login from '@pages/super-admin/auth/Login';
import HomeSuper from '@pages/super-admin/Home.super';
import Layout from '@pages/super-admin/layout/Layout';
import CompanyPageSuper from '@pages/super-admin/sidebar-super-admin/company/EntrepriseSuper';
import User from '@pages/super-admin/sidebar-super-admin/customer/Customers';
import QuestionCorporate from '@pages/super-admin/sidebar-super-admin/Question';

import Brand from '@pages/super-admin/sidebar-super-admin/company/brand/Brand';
import SettingSuper from '@pages/super-admin/sidebar-super-admin/company/setting/SettingSuper';
import ContentSuperAdmin from '@pages/super-admin/sidebar-super-admin/layout/Layout';

import ForgotPassword from '@pages/super-admin/auth/ForgotPassword';
import PasswordModify from '@pages/super-admin/auth/PassworModify';
import ResetPassword from '@pages/super-admin/auth/ResetPassword';
import SetPersonalPassword from '@pages/super-admin/auth/SetPersonalPassword';
import Services from './pages/super-admin/sidebar-super-admin/company/Services';

const companyPath = `${Route.HOME_SUPER}/:uuid`;

export const router = createBrowserRouter([
  {
    path: Route.FORGOT_PASSWORD,
    element: <ForgotPassword />,
  },
  {
    path: Route.PASSWORD_MODIFY,
    element: <PasswordModify />,
  },
  {
    path: Route.RESET_PASSWORD + '/:token',
    element: <ResetPassword />,
  },
  {
    path: Route.ROOT,
    element: <App />,
    errorElement: <NotFound404 />,
    children: [
      {
        index: true,
        path: Route.LOGIN,
        element: <Login />,
      },
      /** SuperAdmin-Route */
      {
        path: '/',
        element: <Layout />,
        children: [
          {
            index: true,
            element: <Navigate to={`${Route.HOME_SUPER}`} replace />,
          },
          {
            path: Route.HOME_SUPER,
            element: <HomeSuper />,
          },
        ],
      },
      {
        path: companyPath,
        element: <ContentSuperAdmin />,
        children: [
          {
            index: true,
            element: <Navigate to={`${Route.superAdmin.company}`} replace />,
          },
          {
            path: `${Route.superAdmin.company}`,
            element: <CompanyPageSuper />,
            children: [
              {
                index: true,
                element: (
                  <Navigate
                    to={`${Route.superAdmin.companyPage.BRAND}`}
                    replace
                  />
                ),
              },
              {
                path: `${Route.superAdmin.companyPage.BRAND}`,
                element: <Brand />,
              },
              {
                path: `${Route.corporate.companyPage.SERVICE}`,
                element: <Services />,
              },
              {
                path: `${Route.superAdmin.companyPage.SETTING}`,
                element: <SettingSuper />,
              },
            ],
          },
          {
            path: `${Route.superAdmin.user}`,
            element: <User />,
          },
          {
            path: `${Route.superAdmin.question}`,
            element: <QuestionCorporate />,
          },
        ],
      },
      {
        path: Route.CHANGE_PASSWORD,
        element: <SetPersonalPassword />,
      },
    ],
  },
]);
