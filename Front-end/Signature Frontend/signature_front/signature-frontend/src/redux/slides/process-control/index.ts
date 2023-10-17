import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
import {baseQuery} from '@/redux/slides/RTKBaseQuery';
import {GraviteeTransactionIdKey} from '@/constant/NGContant';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {store} from '@/redux';
import {Verb} from '@/utils/request/interface/type';
import {ApiFacade} from '@/utils/common/ApiFacade';

export const processControlNotificationSlide = createApi({
  reducerPath: 'processControl',
  tagTypes: ['Process Control', 'Notification'],
  baseQuery: fetchBaseQuery({
    baseUrl: `${baseQuery.baseApiUrl}`,
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
    /** send reminder to participant*/
    sendReminder: builder.mutation<any, {flowId: string}>({
      query: ({flowId}) => ({
        url: `${ApiFacade(
          baseQuery.processControl,
        )}/process-controls/project/send-reminder/${flowId}`,
        method: Verb.Post,
      }),
    }),
  }),
});

export const {useSendReminderMutation} = processControlNotificationSlide;
