import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';

import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {IThemeEnterPrise} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {GetKeys} from '@/utils/common/GetTypeFromObjectKeys';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb, IDType} from '@/utils/request/interface/type';
import env from '../../../../env.config';
import {baseQuery} from '../RTKBaseQuery';
import {ContentsInterface, CorporateUserAccess} from './corporateSettingSlide';

export type IGetUsersContent = {
  id: number;
  firstName: string;
  lastName: string;
  functional: string;
  userId: number;
  businessId: IDType;
  phone: string;
  userAccessId: IDType;
  totalProjects: number;
  percentage: number;
  email: string;
  password: string;
  companyId: number;
  roles: UserRole[];
  userAccess: CorporateUserAccess | null;
  businessUnit: ContentsInterface | null;
};

export type UserRole = 'super-admin' | 'corporate-admin' | 'end-user';

export type IGetUsers = {
  contents: Array<IGetUsersContent>;
  page: number;
  pageSize: number;
  totalPages: number;
  total: number;
  hasNext: boolean;
};

export type IGetUsersQuery = {
  uuid: string;
  page: number;
  pageSize: number;
  sortField: GetKeys<IGetUsersContent>;
  sortDirection: 'asc' | 'desc';
  unitName?: string;
  search: string;
};

export interface IUpdateSignatureLevel {
  SIMPLE?: {
    companyUuid: string;
    signatureLevel: string;
    personalTerms: string;
    channelReminder: string;
  };
  ADVANCE?: {
    companyUuid: string;
    signatureLevel: string;
    personalTerms: string;
    identityTerms: string;
    documentTerms: string;
    channelReminder: string;
    fileType: string[];
  };
  QUALIFIED?: {
    companyUuid: string;
    signatureLevel: string;
    personalTerms: string;
    identityTerms: string;
    documentTerms: string;
    channelReminder: string;
    fileType: string[];
  };
}

export const corporateUserSlide = createApi({
  reducerPath: 'corporate-user',
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
  tagTypes: ['Corporate-User'],
  endpoints: build => ({
    /** get list of user that relate to the company */
    getUsers: build.query<IGetUsers, IGetUsersQuery>({
      query: ({uuid, page, pageSize, sortDirection, sortField, search}) => ({
        url: `/api${env.VITE_VERSION}/corporate/users/company/${uuid}`,
        method: Verb.Get,
        params: {
          page,
          pageSize,
          sortDirection,
          sortField,
          search,
        },
      }),
      providesTags: ['Corporate-User'],
      keepUnusedDataFor: 0,
    }),

    /** create corporate admin */
    createUser: build.mutation<IGetUsersContent, Partial<IGetUsersContent>>({
      query: body => ({
        url: `/api${env.VITE_VERSION}/corporate/users`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['Corporate-User'],
    }),

    /** update corporate admin */
    updateUser: build.mutation<IGetUsersContent, Partial<IGetUsersContent>>({
      query: body => ({
        url: `/api${env.VITE_VERSION}/corporate/users`,
        method: Verb.Put,
        body,
      }),
    }),

    /** delete corporate admin */
    deleteUser: build.mutation<
      IGetUsersContent,
      {id: IDType; assignTo?: IDType}
    >({
      query: ({id, assignTo}) => ({
        url: `/api${env.VITE_VERSION}/corporate/users/${id}`,
        method: Verb.Delete,
        params: {
          assignTo,
        },
      }),
    }),

    /** validate user email */
    validateUserEmail: build.query<boolean, {email: string}>({
      query: ({email}) => ({
        url: `/api${env.VITE_VERSION}/users/email`,
        method: Verb.Get,
        params: {
          email,
        },
      }),
    }),

    /**
     * update theme by super admin
     * */
    updateThemeSuperAdmin: build.mutation<IThemeEnterPrise, FormData>({
      query: body => ({
        url: `/api${env.VITE_VERSION}/company/settings/themes`,
        method: Verb.Put,
        body,
      }),
      invalidatesTags: ['Corporate-User'],
    }),
    /**
     * update theme by super admin
     * */
    getSettingCorporateByAdmin: build.query<any, {uuid: string}>({
      query: ({uuid}) => ({
        url: `/api${env.VITE_VERSION}/company/settings`,
        method: Verb.Get,
        params: {uuid},
      }),
      providesTags: ['Corporate-User'],
    }),
    /**
     * update setting by super admin
     * */
    addSettingBySuperAdmin: build.mutation<
      any,
      Array<
        | IUpdateSignatureLevel['SIMPLE']
        | IUpdateSignatureLevel['ADVANCE']
        | IUpdateSignatureLevel['QUALIFIED']
      >
    >({
      query: body => ({
        url: `/api${env.VITE_VERSION}/company/settings`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['Corporate-User'],
    }),
  }),
});

export const {
  useUpdateThemeSuperAdminMutation,
  useGetUsersQuery,
  useLazyGetUsersQuery,
  useCreateUserMutation,
  useUpdateUserMutation,
  useDeleteUserMutation,
  useLazyValidateUserEmailQuery,
  useLazyGetSettingCorporateByAdminQuery,
  useAddSettingBySuperAdminMutation,
} = corporateUserSlide;
