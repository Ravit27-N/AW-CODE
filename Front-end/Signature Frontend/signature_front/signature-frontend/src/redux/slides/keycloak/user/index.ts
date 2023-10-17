import {refreshTokenKey} from '@/constant/NGContant';
import {store} from '@/redux';
import {Verb} from '@/utils/request/interface/type';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
import axios from 'axios';
import qs from 'qs';
import env from '../../../../../env.config';
import {formatKeycloakAPI} from '@/utils/common/ApiFacade';

const kcConfig = {
  client_id: env.VITE_CLIENT_ID,
  grant_type: env.VITE_GRANT_TYPE,
  client_secret: env.VITE_CLIENT_SECRET,
};

type IUserLogoutBody = {
  refresh_token: string;
  client_id: string;
};

type IUserLoginBody = {
  username: string;
  password: string;
  client_secret?: string;
};

type IUserIntroSpect = {
  token: string;
  client_secret?: string;
};

export type IUserLoginHistory = {
  id: number | null;
  loggedEmail: string;
  userId: number;
  createdAt: string;
};

export const userSlide = createApi({
  reducerPath: 'user',
  baseQuery: fetchBaseQuery({
    baseUrl: formatKeycloakAPI,
    prepareHeaders: header => {
      header.set(
        'Authorization',
        `Bearer ${store.getState().authentication.userToken ?? ''}`,
      );
    },
  }),
  endpoints: builder => ({
    // Logout user
    logout: builder.mutation({
      query: ({refresh_token, client_id}: IUserLogoutBody) => ({
        url: '/logout',
        method: Verb.Post,
        headers: {
          'content-type': 'application/x-www-form-urlencoded',
        },
        body: qs.stringify({refresh_token, client_id}),
      }),
    }),
    // Login user
    login: builder.mutation({
      query: (body: IUserLoginBody) => ({
        url: `realms/${env.VITE_REALM}/protocol/openid-connect/token`,
        method: Verb.Post,
        headers: {
          'content-type': 'application/x-www-form-urlencoded',
        },
        body: qs.stringify({...kcConfig, ...body}),
      }),
    }),
    // Introspect user
    userIntrospect: builder.mutation({
      query: (body: IUserIntroSpect) => ({
        url: `realms/${env.VITE_REALM}/protocol/openid-connect/token/introspect`,
        method: Verb.Post,
        headers: {
          'content-type': 'application/x-www-form-urlencoded',
        },
        body: qs.stringify({...kcConfig, ...body}),
        transformResponse: (response: {data: any}) => response.data,
      }),
    }),
    // Reset user password
    resetPassword: builder.mutation({
      query: ({
        userId,
        body,
      }: {
        userId: string;
        body: {type: string; temporary: boolean; value: string};
      }) => ({
        url: `admin/realms/${env.VITE_REALM}/users/${userId}/reset-password`,
        method: Verb.Put,
        headers: {
          'content-type': 'application/json',
        },
        body,
      }),
    }),
    // Get user by ID
    getUserById: builder.query({
      query: ({userId}: {userId: string}) => ({
        url: `admin/realms/${env.VITE_REALM}/users/${userId}`,
        method: Verb.Get,
      }),
    }),
    // Update password first login
    updatePassword: builder.mutation({
      query: ({userId, attributes}: {userId: string; attributes: any}) => ({
        url: `admin/realms/${env.VITE_REALM}/users/${userId}`,
        method: Verb.Put,
        headers: {
          'content-type': 'application/json',
        },
        body: {attributes},
        transformResponse: (response: {data: any}) => response.data,
      }),
    }),
  }),
});

export const {
  useLoginMutation,
  useUpdatePasswordMutation,
  useResetPasswordMutation,
} = userSlide;

export const logoutFn = () => {
  const refresh_token = localStorage.getItem(refreshTokenKey)!;
  return axios.post(
    `${formatKeycloakAPI}/realms/${env.VITE_REALM}/protocol/openid-connect/logout`,
    {
      refresh_token,
      client_id: env.VITE_CLIENT_ID,
      client_secret: env.VITE_CLIENT_SECRET,
    },
    {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    },
  );
};
