import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';

import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb} from '@/utils/request/interface/type';
import env from '../../../../env.config';
import {baseQuery} from '../RTKBaseQuery';
import {ApiFacade} from '@/utils/common/ApiFacade';

export type CorporateAdmin = {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
  functional: string;
  companyId: number;
};

export const corporateAdminSlide = createApi({
  reducerPath: 'corporate-admin',
  baseQuery: fetchBaseQuery({
    baseUrl: baseQuery.baseApiUrl,
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
    /** create corporate admin */
    createUser: build.mutation<CorporateAdmin, Partial<CorporateAdmin>>({
      query: body => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/corporate/users`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['Corporate-Admin'],
    }),
  }),
});

export const {useCreateUserMutation} = corporateAdminSlide;
