import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';

import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb} from '@/utils/request/interface/type';
import env from '../../../../env.config';
import {baseQuery} from '../RTKBaseQuery';
import {ApiFacade} from '@/utils/common/ApiFacade';

export type IGetUsersProfile = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  functional: string;
  businessId: number;
  userEntityId: string;
  createdBy: number;
  roles: string[];
  active: boolean;
  businessUnitInfo: {
    id: number;
    unitName: string;
    companyDetailId: number;
    sortOrder: number;
  };
  company: {
    id: number;
    name: string;
    logo: string;
    totalEmployees: number;
    createdAt: string;
    modifiedAt: string;
  };
};

export const endUserProfileSlide = createApi({
  reducerPath: 'end-user-profile',
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
  tagTypes: ['End-User-Profile'],
  endpoints: build => ({
    /** get end user profile **/
    getEndUserProfile: build.query<Omit<IGetUsersProfile, 'businessId'>, null>({
      query: () => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users/end-user`,
        method: Verb.Get,
      }),
      providesTags: ['End-User-Profile'],
    }),

    /** update user profile **/
    updateEndUserProfile: build.mutation<
      IGetUsersProfile,
      Partial<IGetUsersProfile>
    >({
      query: body => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users`,
        method: Verb.Put,
        body,
      }),
      invalidatesTags: ['End-User-Profile'],
    }),

    /** change user password in profile **/
    changeEndUserPassword: build.mutation<
      null,
      {currentPassword: string; newPassword: string; confirmPassword: string}
    >({
      query: body => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users/password/reset`,
        method: Verb.Put,
        body,
      }),
    }),
  }),
});

export const {
  useLazyGetEndUserProfileQuery,
  useUpdateEndUserProfileMutation,
  useChangeEndUserPasswordMutation,
} = endUserProfileSlide;
