import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {baseQuery} from '@/redux/slides/RTKBaseQuery';
import {ApiFacade} from '@/utils/common/ApiFacade';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb} from '@/utils/request/interface/type';
import {handleDownloadFileToLocal} from '@/utils/request/services/MyService';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
import {IGetFlowId} from '../../project-management/project';

export type IValidatePhoneNumber = {
  totalAttempts: number;
  number: string;
  missingLength: number;
  valid: boolean;
};

// Participant slide
export const processControlSlide = createApi({
  reducerPath: 'internal-processControl',
  tagTypes: ['process-control', 'Project'],
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

  endpoints: builder => ({
    /** Get project by flowId */
    getProjectByFlowId: builder.query<
      IGetFlowId,
      {flowId: string; uuid: string}
    >({
      query: ({flowId, uuid}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/sign-info/${flowId}`,
        method: Verb.Get,
        params: {uuid},
      }),
      providesTags: ['process-control'],
    }),
    /** View document */
    viewDocument: builder.query<any, {flowId: string; docId: string}>({
      query: ({flowId, docId}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/documents/view/${flowId}`,
        method: Verb.Get,
        params: {docId},
        responseHandler: 'text',
      }),
    }),
    // Validate phone number
    validatePhoneNumber: builder.mutation<
      IValidatePhoneNumber,
      {flowId: string; uuid: string; phone: string}
    >({
      query: ({flowId, phone, uuid}) => ({
        url: `${ApiFacade(
          baseQuery.signProcess,
        )}/otp/validate/phone-number/${flowId}`,
        method: Verb.Post,
        params: {uuid, phone},
      }),
      invalidatesTags: () => ['process-control'],
    }),
    /** Generate OTP */
    generateOTP: builder.mutation<any, {uuid: string; flowId: string}>({
      query: ({uuid, flowId}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/otp/generate/` + flowId,
        method: Verb.Post,
        params: {uuid},
      }),
    }),
    /** Validate OTP */
    validateOTP: builder.mutation<
      boolean,
      {uuid: string; flowId: string; otp: string}
    >({
      query: ({uuid, flowId, otp}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/otp/validate/${flowId}`,
        method: Verb.Post,
        params: {uuid, otp},
      }),
    }),
    /** Sign the document*/
    signDocument: builder.mutation<any, {flowId: string; uuid: string}>({
      query: ({flowId, uuid}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/sign/${flowId}`,
        method: Verb.Post,
        params: {uuid},
      }),
      invalidatesTags: () => ['process-control'],
    }),
    /** Approve document */
    approveDocument: builder.mutation<any, {flowId: string; uuid: string}>({
      query: ({flowId, uuid}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/approval/${flowId}/approve`,
        method: Verb.Post,
        params: {uuid},
      }),
      invalidatesTags: () => ['process-control'],
    }),
    /** Refuse document */
    refuseDocument: builder.mutation<
      any,
      {flowId: string; uuid: string; comment: string}
    >({
      query: ({flowId, uuid, comment}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/project/refuse/${flowId}`,
        method: Verb.Post,
        params: {uuid, comment},
      }),
      invalidatesTags: () => ['process-control'],
    }),
    /** Setup individual */
    setupIndividual: builder.mutation<any, {flowId: string; uuid: string}>({
      query: ({flowId, uuid}) => ({
        url: `${ApiFacade(
          baseQuery.signProcess,
        )}/sign/individual/setup/${flowId}`,
        method: Verb.Post,
        params: {uuid},
      }),
      invalidatesTags: () => ['process-control'],
    }),
    /** Individual sign */
    individualSign: builder.mutation<
      any,
      {body: FormData; flowId: string; docId: string}
    >({
      query: ({body, flowId, docId}) => ({
        url: `${ApiFacade(baseQuery.signProcess)}/sign/upload/${flowId}`,
        method: Verb.Post,
        body,
        params: {docId},
      }),
      invalidatesTags: () => ['process-control'],
    }),
    /** Download file */
    downloadDoc: builder.query<any, {flowId: string; docId: string}>({
      query: ({docId, flowId}) => ({
        url: `${ApiFacade(
          baseQuery.signProcess,
        )}/sign/download/${flowId}?docId=${docId}`,
        method: Verb.Get,
        responseHandler: async response => {
          const blob = await response.blob();
          const file = window.URL.createObjectURL(blob);
          handleDownloadFileToLocal(blob);
          return window.open(file);
        },
      }),
    }),
  }),
});

export const {
  useGetProjectByFlowIdQuery,
  useViewDocumentQuery,
  useValidatePhoneNumberMutation,
  useGenerateOTPMutation,
  useValidateOTPMutation,
  useSignDocumentMutation,
  useApproveDocumentMutation,
  useRefuseDocumentMutation,
  useSetupIndividualMutation,
  useIndividualSignMutation,
  useLazyDownloadDocQuery,
} = processControlSlide;
