import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
import {baseQuery} from '../../RTKBaseQuery';
import {Verb} from '@/utils/request/interface/type';
import {store} from '@/redux';
import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {ApiFacade} from '@/utils/common/ApiFacade';

export type IUploadResponse = {
  url: string;
  date: string;
  expires: string;
};

export type ISignatory = {
  id: string | number;
  firstName: string;
  lastName: string;
  role: string;
  email: string;
  phone: string;
  invitationStatus: string;
  sortOrder: number;
  projectId: number | string;
};

export type IGetSignatories = {
  page: number;
  pageSize: number;
  filter: string;
  sortDirection: 'desc' | 'asc';
};

// Signatory slide
export const signatorySlice = createApi({
  reducerPath: 'signatory',
  tagTypes: ['Signatory'],
  baseQuery: fetchBaseQuery({
    baseUrl: baseQuery.baseApiUrl,
    prepareHeaders: headers => {
      headers.set(GraviteeTransactionIdKey, graviteeTransactionId());
      headers.set(
        'Authorization',
        `Bearer ${store.getState().authentication.userToken}`,
      );
    },
  }),
  endpoints: builder => ({
    // Get signatories
    getSignatories: builder.query<any, IGetSignatories>({
      query: ({page, pageSize, filter = '', sortDirection}) => ({
        url: `${ApiFacade(
          baseQuery.projectManagement,
        )}/signatories?page=${page}&pageSize=${pageSize}&filter=${filter}&sortDirection=${sortDirection}`,
        method: Verb.Get,
      }),
      providesTags: () => [{type: 'Signatory', id: 'LIST'}],
    }),
    // Get SignatoryById
    getSignatoryById: builder.query<any, {id: string}>({
      query: ({id}) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/signatories/${id}`,
        method: Verb.Get,
      }),
      providesTags: ['Signatory'],
    }),
    // Add signatory
    addSignatory: builder.mutation<any, ISignatory>({
      query: body => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/signatories`,
        method: Verb.Post,
        body,
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
      }),
      invalidatesTags: [{type: 'Signatory', id: 'LIST'}],
    }),
    // Update signatory
    updateSignatory: builder.mutation<any, ISignatory & {id: string | number}>({
      query: body => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/signatories`,
        method: Verb.Put,
        body,
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
      }),
      invalidatesTags: [{type: 'Signatory', id: 'LIST'}],
    }),
    deleteSignatory: builder.mutation<any, {id: string | number}>({
      query: ({id}) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/signatories/${id}`,
        method: Verb.Delete,
      }),
    }),
  }),
});

export const {useDeleteSignatoryMutation} = signatorySlice;
