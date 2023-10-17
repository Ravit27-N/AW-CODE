import {
  AutoReminder,
  GraviteeTransactionIdKey,
  SIGNING_PROCESS,
} from '@/constant/NGContant';
import {store} from '@/redux';
import {ApiFacade} from '@/utils/common/ApiFacade';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb} from '@/utils/request/interface/type';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
import {baseQuery} from '../../RTKBaseQuery';

export interface FolderTemplateInterface {
  id: number;
  unitName: string;
  businessUnitId: number;
  countTemplates: number;
  templates: TemplateInterface[];
}

export interface TemplateService {
  businessUnitId: number;
  id: number;
  templateId: number;
  unitName: string;
}

export interface TemplateInterface {
  id: number;
  name: string;
  signProcess: SIGNING_PROCESS;
  level: number;
  format: number;
  approval: number;
  signature: number;
  recipient: number;
  viewer: number;
  createdBy: number;
  createdByFullName: string;
  createdAt: number;
  folderId: number;
  businessUnitId: number;
  folderName: string;
  modifiedBy: string;
  companyId: number;
  businessUnitName: string;
  notificationService: 'sms_email' | 'email' | 'sms';
  templateServices: Array<TemplateService>;
  templateMessage: {
    titleInvitation: string;
    messageInvitation: string;
    expiration: number;
    sendReminder: AutoReminder | null;
  };
  unitName: string;
}

export type IBodyCreateTemplate = {
  id: number | null;
  name?: string;
  signProcess?: SIGNING_PROCESS;
  level?: number;
  format?: number;
  approval?: number;
  signature?: number;
  recipient?: number;
  viewer?: number;
  folderId?: number;
  businessUnitId?: number;
  companyId?: number;
  notificationService?: string;
  templateServicesIds?: Array<number>;
};

export type IBodyUpdateScenario = {
  id: number;
  approval?: number;
  signature?: number;
  recipient?: number;
  viewer?: number;
  signProcess?: string;
};

export type IBodyUpdateParameters = {
  id: number;
  notificationService?: string;
  templateMessage: TemplateInterface['templateMessage'];
};

// Template slide
export const templateSlice = createApi({
  reducerPath: 'profile-template',
  tagTypes: ['templates'],
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
    /**
     * Get template by id
     * */
    getTemplateById: builder.query<TemplateInterface, {id: number}>({
      query: ({id}: {id: number}) => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/templates/${id}`,
        method: Verb.Get,
      }),
      providesTags: ['templates'],
    }),
    /**
     * Get template for dashboard
     * */
    getTemplates: builder.query<
      Array<FolderTemplateInterface>,
      {filter: string}
    >({
      query: ({filter = ''}) => ({
        url: `${ApiFacade(
          baseQuery.profileManagement,
        )}/templates?filter=${filter}`,
        method: Verb.Get,
      }),
      providesTags: ['templates'],
    }),
    /**
     * Get template corporate not yet supported with uuid
     * so I split uuid out
     * */
    getTemplatesCorporate: builder.query<Array<TemplateInterface>, void>({
      query: () => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/templates/corporate`,
        method: Verb.Get,
      }),
    }),

    /**
     * Create template
     * */
    createTemplate: builder.mutation<TemplateInterface, IBodyCreateTemplate>({
      query: body => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/templates`,
        method: Verb.Post,
        body,
      }),
    }),
    /**
     * Update template
     * */
    updateTemplate: builder.mutation<
      any,
      IBodyCreateTemplate | IBodyUpdateScenario | IBodyUpdateParameters
    >({
      query: body => ({
        url: `${ApiFacade(baseQuery.profileManagement)}/templates`,
        method: Verb.Put,
        body,
      }),
      invalidatesTags: () => ['templates'],
    }),
  }),
});

export const {
  useGetTemplatesQuery,
  useGetTemplateByIdQuery,
  useCreateTemplateMutation,
  useUpdateTemplateMutation,
  useGetTemplatesCorporateQuery,
} = templateSlice;
