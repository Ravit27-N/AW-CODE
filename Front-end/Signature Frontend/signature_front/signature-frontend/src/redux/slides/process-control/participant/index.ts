import {baseQuery} from '@/redux/slides/RTKBaseQuery';
import {IGetFlowId} from '@/redux/slides/project-management/project';
import {formatBaseAPI} from '@/utils/common/ApiFacade';
import {Verb} from '@/utils/request/interface/type';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
type IProcess = {
  flowId: string;
  uuid: string;
};

// Participant slide
export const participantSlide = createApi({
  reducerPath: 'participant',
  tagTypes: ['Participant', 'Project'],
  baseQuery: fetchBaseQuery({
    baseUrl: baseQuery.baseApiUrl,
  }),

  endpoints: builder => ({
    // Generate OTP
    generateOTP: builder.mutation<any, {uuid: string; flowId: string}>({
      query: ({uuid, flowId}) => ({
        url:
          `${formatBaseAPI(
            baseQuery.signProcess,
            `/${flowId}`,
          )}/api/otp/generate/` + flowId,
        method: Verb.Post,
        params: {token: uuid},
      }),
      // invalidatesTags: () => ['Participant'],
    }),
    /**
     *  generateOTP: builder.mutation<any, {token: string, corporate-uuid: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/otp/generate/${corporate-uuid}`,
            method: Verb.Post,
            params: {token},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    getProjectByFlowId: builder.query<IGetFlowId, {id: string}>({
      query: ({id}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${id.split('?')[0]}`,
        )}/api/sign-info/${id}`,
        method: Verb.Get,
      }),
      providesTags: ['Participant'],
    }),
    /**
     *  getProjectByFlowId: builder.query<IGetFlowId, {token: string, corporate-uuid: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/sign-info/${corporate-uuid}`,
            method: Verb.Get,
            params: {token},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Validate OTP
    validateOTP: builder.mutation<
      any,
      {uuid: string; flowId: string; otp: string}
    >({
      query: ({uuid, flowId, otp}) => ({
        url:
          `${formatBaseAPI(
            baseQuery.signProcess,
            `/${flowId}`,
          )}/api/otp/validate/` + flowId,
        method: Verb.Post,
        params: {token: uuid, otp},
      }),
      invalidatesTags: () => ['Participant'],
    }),

    /**
     *  validateOTP: builder.mutation<any, {token: string, corporate-uuid: string, otp: string}>({
          query: ({token, corporate-uuid, otp}) => ({
            url: `api/sign-info/${corporate-uuid}`,
            method: Verb.Post,
            params: {token, otp},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Validate phone number
    validatePhoneNumber: builder.mutation<any, IProcess & {phone: string}>({
      query: ({flowId, phone, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/otp/validate/phone-number/${flowId}`,
        method: Verb.Post,
        params: {token: uuid, phone},
      }),
      invalidatesTags: () => ['Participant'],
    }),
    /**
     *  validatePhoneNumber: builder.mutation<any, {token: string, corporate-uuid: string, phone: string}>({
          query: ({token, corporate-uuid, phone}) => ({
            url: `api/otp/validate/phone-number/${corporate-uuid}`,
            method: Verb.Post,
            params: {token, phone},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Sign the document
    signDocument: builder.mutation<any, IProcess>({
      query: ({flowId, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/sign/${flowId}`,
        method: Verb.Post,
        params: {token: uuid},
        invalidatesTags: () => ['Participant'],
      }),
    }),
    /**
     *  signDocument: builder.mutation<any, {token: string, corporate-uuid: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/sign/${corporate-uuid}`,
            method: Verb.Post,
            params: {token},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Approve document
    approveDocument: builder.mutation<any, IProcess>({
      query: ({flowId, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/approval/approve/${flowId}`,
        method: Verb.Post,
        params: {token: uuid},
        invalidatesTags: () => ['Participant'],
      }),
    }),
    /**
     *  approveDocument: builder.mutation<any, {token: string, corporate-uuid: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/sign/${corporate-uuid}`,
            method: Verb.Post,
            params: {token},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Recipient document
    recipientDocument: builder.mutation<any, IProcess>({
      query: ({flowId, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/recipients/receive/${flowId}`,
        method: Verb.Post,
        params: {token: uuid},
        invalidatesTags: () => ['Participant'],
      }),
    }),
    /**
     *  recipientDocument: builder.mutation<any, {token: string, corporate-uuid: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/recipients/receive/${corporate-uuid}`,
            method: Verb.Post,
            params: {token},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Individual sign
    individualSign: builder.mutation<
      any,
      {body: FormData; flowId: string; docId: string; uuid: string}
    >({
      query: ({body, flowId, docId, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/sign/upload/${flowId}`,
        method: Verb.Post,
        body,
        params: {docId, token: uuid},
        invalidatesTags: () => ['Participant'],
      }),
    }),
    /**
     *  individualSign: builder.mutation<any, {token: string, corporate-uuid: string, body: FormData; docId: string}>({
          query: ({token, corporate-uuid, docId, body}) => ({
            url: `api/sign/upload/${corporate-uuid}`,
            method: Verb.Post,
            body,
            params: {token, docId},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Setup individual
    setupIndividual: builder.mutation<any, {flowId: string; uuid: string}>({
      query: ({flowId, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/sign/individual/setup/${flowId}`,
        method: Verb.Post,
        params: {token: uuid},
        invalidatesTags: () => ['Participant'],
      }),
    }),
    /**
     *  setupIndividual: builder.mutation<any, {token: string, corporate-uuid: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/sign/individual/setup/${corporate-uuid}`,
            method: Verb.Post,
            params: {token},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // Refuse document
    refuseDocument: builder.mutation<
      any,
      {flowId: string; uuid: string; comment: string}
    >({
      query: ({flowId, uuid, comment}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/project/refuse/${flowId}`,
        method: Verb.Post,
        params: {token: uuid, comment},
        invalidatesTags: () => ['Participant'],
      }),
    }),
    /**
     *  refuseDocument: builder.mutation<any, {token: string, corporate-uuid: string, comment: string}>({
          query: ({token, corporate-uuid, comment}) => ({
            url: `api/refuse/${corporate-uuid}`,
            method: Verb.Post,
            params: {token, comment},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // View document
    viewDocument: builder.mutation<any, {flowId: string; uuid: string}>({
      query: ({flowId, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/approval/read/${flowId}`,
        method: Verb.Post,
        params: {token: uuid},
        invalidatesTags: () => ['Participant'],
      }),
    }),
    verifyDocument: builder.mutation<
      any,
      {flowId: string; uuid: string; body: FormData}
    >({
      query: ({flowId, body, uuid}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/validate/document/${flowId}`,
        method: Verb.Post,
        params: {token: uuid},
        body,
      }),
      invalidatesTags: () => ['Participant'],
    }),
    /**
     *  viewDocument: builder.mutation<any, {token: string, corporate-uuid: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/approval/read${corporate-uuid}`,
            method: Verb.Post,
            params: {token},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
    // View file document
    viewFile: builder.mutation<
      any,
      {flowId: string; uuid: string; docId: string}
    >({
      query: ({flowId, uuid, docId}) => ({
        url: `${formatBaseAPI(
          baseQuery.signProcess,
          `/${flowId}`,
        )}/api/documents/view/${flowId}`,
        method: Verb.Get,
        params: {uuid, docId},
        responseHandler: 'text',
        invalidatesTags: () => ['Participant'],
      }),
    }),
    /**
     *  viewDocument: builder.mutation<any, {token: string, corporate-uuid: string, docId: string}>({
          query: ({token, corporate-uuid}) => ({
            url: `api/documents/view/${corporate-uuid}`,
            method: Verb.Get,
            params: {token, docId},
            invalidatesTags: () => ['Participant'],
          }),
        }),
     */
  }),
  /**
   * download a file in sign process (process control of signatory)
   *For a download, We don't use RTK, You run the risk of crashing the browser!
   * we will (MyService)-> rename this service name.
   * **/
});

export const {
  useValidatePhoneNumberMutation,
  useGenerateOTPMutation,
  useValidateOTPMutation,
  useSignDocumentMutation,
  useApproveDocumentMutation,
  useIndividualSignMutation,
  useSetupIndividualMutation,
  useRecipientDocumentMutation,
  useRefuseDocumentMutation,
  useGetProjectByFlowIdQuery,
  useViewDocumentMutation,
  useVerifyDocumentMutation,
} = participantSlide;
