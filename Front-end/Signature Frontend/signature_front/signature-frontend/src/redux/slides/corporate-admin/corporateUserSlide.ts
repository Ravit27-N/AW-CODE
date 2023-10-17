import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';

import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {GetKeys} from '@/utils/common/GetTypeFromObjectKeys';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {IDType, Verb} from '@/utils/request/interface/type';
import {baseQuery} from '../RTKBaseQuery';
import {CorporateUserAccess} from './corporateSettingSlide';
import {ApiFacade} from '@/utils/common/ApiFacade';

export type IGetUsersContent = {
  id: number;
  firstName: string;
  lastName: string;
  functional: string;
  userId: number;
  businessUnitId: number;
  userAccessId: number;
  totalProjects: number;
  percentage: number;
  email?: string;
  department: {
    id: number;
    unitName: string;
    sortOrder: number;
    companyDetailId: number;
  };
  userAccess: CorporateUserAccess;
};

export type IGetUsers = {
  contents: Array<IGetUsersContent>;
  page: number;
  pageSize: number;
  totalPages: number;
  total: number;
  hasNext: boolean;
};

export type IGetUsersQuery = {
  page: number;
  pageSize: number;
  sortField: GetKeys<IGetUsersContent> | 'businessUnit.unitName';
  sortDirection: 'asc' | 'desc';
  businessUnitId?: number;
  companyId: number;
  unitName?: string;
  search: string;
};

export const corporateUserSlide = createApi({
  reducerPath: 'corporate-user',
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
  tagTypes: ['Corporate-User'],
  endpoints: build => ({
    /** get user list */
    getUsers: build.query<IGetUsers, IGetUsersQuery>({
      query: ({
        page,
        pageSize,
        sortDirection,
        sortField,
        unitName,
        businessUnitId,
        companyId,
        search,
      }) => ({
        url: `${ApiFacade(baseQuery.corporateProfile)}/employees`,
        method: Verb.Get,
        params: {
          page,
          pageSize,
          sortDirection,
          sortField,
          unitName,
          businessUnitId,
          companyId,
          search,
        },
      }),
      providesTags: ['Corporate-User'],
      keepUnusedDataFor: 0,
    }),

    /** get user by id */
    getUserById: build.query<IGetUsersContent, {id: number}>({
      query: ({id}) => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users/${id}`,
        method: Verb.Get,
      }),
    }),

    /** create user */
    createUser: build.mutation<IGetUsers, unknown>({
      query: body => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['Corporate-User'],
    }),

    /** validate user email */
    validateUserEmail: build.query<boolean, {email: string}>({
      query: ({email}) => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users/email`,
        method: Verb.Get,
        params: {
          email,
        },
      }),
    }),
    /** create end-user by an upload csv format*/
    createUserByCsv: build.mutation<any, FormData>({
      query: body => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users/csv`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['Corporate-User'],
    }),
    /**
     * delete end-user. currently, we don't delete end-user in database, we just update status to disable. so we can keep history
     * */
    deleteEndUser: build.mutation<unknown, {id: IDType; assignTo?: IDType}>({
      query: ({id, assignTo}) => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/users/delete/${id}`,
        method: Verb.Delete,
        params: {
          assignTo,
        },
      }),
      invalidatesTags: ['Corporate-User'],
    }),
  }),
});

export const {
  useGetUsersQuery,
  useLazyGetUsersQuery,
  useCreateUserMutation,
  useLazyValidateUserEmailQuery,
  useLazyGetUserByIdQuery,
  useCreateUserByCsvMutation,
  useDeleteEndUserMutation,
} = corporateUserSlide;
