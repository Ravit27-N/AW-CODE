import {Verb} from '@/utils/request/interface/type';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
import {baseQuery} from '../../RTKBaseQuery';

// User slide
export const userManagementSlice = createApi({
  reducerPath: 'user-project-management',
  tagTypes: ['User-Management'],
  baseQuery: fetchBaseQuery({
    baseUrl: baseQuery.baseUrlForgotPassword,
  }),
  endpoints: builder => ({
    // Forget password send mail
    forgetPassword: builder.mutation({
      query: ({email}: {email: string}) => ({
        url: `/api/auth/forgot-password`,
        method: Verb.Put,
        body: {email},
      }),
    }),
    // Forget password send mail
    resetPassword: builder.mutation({
      query: ({
                newPassword,
                confirmPassword,
                resetToken,
              }: {
        newPassword: string;
        confirmPassword: string;
        resetToken: string;
      }) => ({
        url: `/api/auth/password/reset`,
        method: Verb.Post,
        body: {newPassword, confirmPassword, resetToken},
      }),
    }),
    /**
     * Auth activate
     * */
    authActivate: builder.mutation<any, {resetToken: string}>({
      query: ({resetToken}) => ({
        url: `api/auth/activate/${resetToken}`,
        method: Verb.Put,
      }),
    }),
    /**
     * User activate
     * */
    userInfo: builder.query<any, {token: string}>({
      query: ({token}) => ({
        url: `api/auth/user/${token}`,
        method: Verb.Get,
      }),
    }),
    /**
     * Redirect forgot password
     * */
    redirectResetPassword: builder.mutation<any, {token: string}>({
      query: ({token}) => ({
        url: `api/auth/redirect/forgot-password/${token}`,
        method: Verb.Put,
      }),
    }),
  }),
});

export const {
  useForgetPasswordMutation,
  useResetPasswordMutation,
  useRedirectResetPasswordMutation,
  useAuthActivateMutation,
  useUserInfoQuery,
} = userManagementSlice;
