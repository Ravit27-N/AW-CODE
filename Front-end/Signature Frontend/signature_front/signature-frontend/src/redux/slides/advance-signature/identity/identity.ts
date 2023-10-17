import {Verb} from '@/utils/request/interface/type';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';
import env from '../../../../../env.config';

export const identitySlide = createApi({
  reducerPath: 'identity',
  baseQuery: fetchBaseQuery({
    baseUrl: import.meta.env.VITE_GATEWAY_RITH,
  }),
  tagTypes: ['Identity'],

  endpoints: build => ({
    /** verify document**/
    verifyDocument: build.mutation<any, {dossierId: string; body: FormData}>({
      query: ({dossierId, body}) => ({
        url: `${env.VITE_VERSION}/validate/${dossierId}/verify`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['Identity'],
    }),
  }),
});

export const {useVerifyDocumentMutation} = identitySlide;
