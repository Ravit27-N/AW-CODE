import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {BaseQuery, Verb} from '@/utils/request/interface/type';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';
import env from '../../../../../env.config';
import {baseQuery} from '../../RTKBaseQuery';

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
  archiving: boolean;
};

export type IUploadCompanyLogo = {
  logoFile: null | File;
};

export type ICompaniesList = {usersCount: number} & Omit<
  ICreateCompany,
  'createdAt' | 'fixNumber' | 'email' | 'address' | 'mobile'
>;

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
  uuid: string;
}
export interface CompanyQuery extends BaseQuery{
  search:string;
}

export const companyAPI = createApi({
  reducerPath: 'company',
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
  tagTypes: ['Company'],

  endpoints: build => ({
    getCompanies: build.query<ICompany, CompanyQuery>({
      query: ({search, page, pageSize}) => ({
        url: `/api${env.VITE_VERSION}/companies?search=${search}`,
        params: {
          page,
          pageSize,
        },
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
     * get logo.
     * */
    getCompanyLogo: build.query<any, string>({
      query: fileName => ({
        url: `${baseQuery.baseUrlCorporate}/api/corporate-settings/view/content`,
        method: Verb.Get,
        responseHandler: response =>
          response.blob().then(myBlob => {
            // create url from blob for logo so we can display it
            const objectURL = URL.createObjectURL(myBlob);
            return objectURL;
          }),
        params: {
          fileName,
        },
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

    /**
     * validate company name to avoid duplicate name.
     * */
    validateCompanyName: build.query<any, {name: string}>({
      query: params => ({
        url: `/api${env.VITE_VERSION}/companies/validate/name`,
        method: Verb.Get,
        params,
      }),
    }),
  }),
});

export const {
  useGetCompaniesQuery,
  useGetCompanyLogoQuery,
  useLazyGetCompanyLogoQuery,
  useCreateCompanyMutation,
  useUploadLogoMutation,
  useLazyValidateCompanyNameQuery,
} = companyAPI;
