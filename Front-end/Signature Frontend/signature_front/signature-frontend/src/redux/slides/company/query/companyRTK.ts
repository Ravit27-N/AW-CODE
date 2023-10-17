import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb} from '@/utils/request/interface/type';

import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';
import env from '../../../../../env.config';
import {baseQuery} from '../../RTKBaseQuery';
import {formatBaseAPI} from '@/utils/common/ApiFacade';
export interface ICompany {
  contents: Content[];
  page: number;
  pageSize: number;
  total: number;
  hasNext: boolean;
  totalPages: number;
}
export interface Content {
  addressLine1: string;
  addressLine2: string;
  archiving: boolean;
  city: string;
  contactFirstName: string;
  contactLastName: string;
  country: string;
  email: string;
  fixNumber: string;
  mobile: string;
  postalCode: string;
  state: string;
  territory: string;
  totalEmployees: number;
  id: number;
  name: string;
  siret: string;
  createdBy: number;
  logo: string | null;
  modifiedBy: number | null;
  createdAt: number;
  modifiedAt: number;
}
export type ICreateCompany = {
  id?: number;
  name: string;
  siret: string;
  contactFirstName: string;
  contactLastName: string;
  mobile: string;
  fixNumber?: string | null;
  email?: string | null;
  addressLine1?: string | null;
  logo?: string | null;
  createdAt?: Date | string | null;
};

export type IUploadCompanyLogo = {
  logoFile: null | File;
};

export type ICompaniesList = {usersCount: number} & Omit<
  ICreateCompany,
  'createdAt' | 'fixNumber' | 'email' | 'address' | 'mobile'
>;

export const companyAPI = createApi({
  reducerPath: 'company',
  baseQuery: fetchBaseQuery({
    baseUrl: formatBaseAPI(baseQuery.profileManagement),
    prepareHeaders: headers => {
      headers.set(GraviteeTransactionIdKey, graviteeTransactionId());
      headers.set(
        'Authorization',
        `Bearer ` + store.getState().authentication.userToken,
      );
      return headers;
    },
  }),
  tagTypes: ['Company'],

  endpoints: build => ({
    getCompanies: build.query<ICompany, void | string>({
      query: search => ({
        url: `/api${env.VITE_VERSION}/companies?search=${search}`,
      }),
      providesTags: ['Company'],
    }),

    getCompanyDetail: build.query<any, {id: string}>({
      query: ({id}) => ({
        url: `/api${env.VITE_VERSION}/companies/` + id,
        method: Verb.Get,
      }),
    }),

    /**
     * upload logo.
     * */
    uploadLogo: build.mutation<{fileName: string}, FormData>({
      query: body => ({
        url: `/api${env.VITE_VERSION}/companies/upload-logo`,
        method: Verb.Post,
        body,
      }),
    }),

    /**
     * create company.
     * */
    createCompany: build.mutation<ICreateCompany, Partial<ICreateCompany>>({
      query: body => ({
        url: `/api${env.VITE_VERSION}/companies`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['Company'],
    }),
  }),
});

export const {
  useGetCompaniesQuery,
  useCreateCompanyMutation,
  useUploadLogoMutation,
} = companyAPI;
