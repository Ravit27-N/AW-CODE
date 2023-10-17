import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';
import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb} from '@/utils/request/interface/type';
import env from '../../../../env.config';
import {baseQuery} from '../RTKBaseQuery';

export type CorporateAdmin = {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
  functional: string;
  companyId: number;
};

export type IUserLoginHistory = {
  id: number | null;
  loggedEmail: string;
  userId: number;
  createdAt: string;
};

export const corporateAdminSlide = createApi({
  reducerPath: 'corporate-admin',
  baseQuery: fetchBaseQuery({
    baseUrl: baseQuery.baseUrlProfile,
    prepareHeaders: headers => {
      headers.set(GraviteeTransactionIdKey, graviteeTransactionId());
      headers.set(
        'Authorization',
        `Bearer ` + store.getState().authentication.userToken,
      );
      return headers;
    },
  }),
  tagTypes: ['Corporate-Admin'],
  endpoints: build => ({
    /** user login history */
    userLoginHistory: build.mutation<
      IUserLoginHistory,
      Omit<IUserLoginHistory, 'id' | 'createdAt'> & {token: string}
    >({
      query: ({token, ...rest}) => ({
        url: `/api${env.VITE_VERSION}/admin/save-login`,
        method: Verb.Post,
        body: {...rest},
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }),
    }),
  }),
});

export const {useUserLoginHistoryMutation} = corporateAdminSlide;
