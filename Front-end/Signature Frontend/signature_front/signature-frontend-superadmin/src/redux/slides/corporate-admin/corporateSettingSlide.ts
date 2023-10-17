import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';

import {ProjectStatusInterfaces} from '@/components/ng-switch-case-status/interface';
import {GraviteeTransactionIdKey, ProjectStatus} from '@/constant/NGContant';
import {store} from '@/redux';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {Verb} from '@/utils/request/interface/type';
import env from '../../../../env.config';
import {baseQuery} from '../RTKBaseQuery';
import {GetKeys} from '@/utils/common/GetTypeFromObjectKeys';

export interface CorporateCompanyInterface {
  id?: number | string;
  name: string | null;
  siret?: string;
  mobile?: string | null;
  email: string | null;
  contactFirstName: string | null;
  contactLastName: string | null;
  fixNumber: string | null;
  addressLine1?: string;
  addressLine2?: string;
  postalCode?: string;
  state?: string;
  country: string;
  city?: string;
  territory?: string;
  createdBy?: number;
  theme: CorporateSettingTheme[];
  companyProviderTheme: CorporateSettingTheme;
}

export interface DocumentTableInterface {
  contents: ContentsDocumentTableInterface[];
  hasNext?: boolean;
  pageSize?: number;
  page?: number;
  total?: number;
  totalPages?: number;
}

export interface ContentsDocumentTableInterface {
  businessUnitId: number;
  id: number;
  firstName: string;
  percentage: number;
  lastName: string;
  totalCompleted: number;
  totalProjects: number;
  userId: number;
}

export interface CorporateSettingTheme {
  id?: number | string;
  companyId?: number | string;
  mainColor?: string | null;
  secondaryColor?: string | null;
  linkColor?: string | null;
  logo: string | null;
  default?: boolean;
}

export interface CorporateUserAccess {
  id: number;
  name: string;
}

export type IGetDashboardContent = {
  cards: Array<IContentCard>;
  statuses: Array<IContentStatus>;
};

export type IContentCard = {
  id: keyof Pick<ProjectStatusInterfaces, 'COMPLETED' | 'REFUSED'> | 'ABANDON';
  label:
    | 'Temps moyen de signture'
    | 'Taux d’abandon de dossiers'
    | 'Taux de refus de signer';
  value: string | number;
};

export type IContentStatus = {
  id: keyof ProjectStatusInterfaces | 'URGENT';
  label: (typeof ProjectStatus)[keyof typeof ProjectStatus] | 'URGENT';
  value: string | number;
};

export type IGetDashboard = {
  startDate: string;
  endDate: string;
  totalProjects: number;
  contents: IGetDashboardContent;
};
// ** Corporate template type **//
export type ITemplateService = {
  id: number;
  templateId: number;
  unitName: string;
  businessUnitId: number;
};

export type ITemplateMessage = {
  templateId: number;
  titleInvitation: string;
  messageInvitation: string;
  expireDate: string;
  sendReminder: number;
};
export type ITemplate = {
  id: number;
  name: string;
  signProcess: string;
  level: number;
  format: number;
  approval: number;
  signature: number;
  recipient: number;
  folderId: number;
  businessUnitId: number;
  companyId: number;
  notificationService: string;
  createdByFullName: string;
  folderName: string | null;
  businessUnitName: string | null;
  createdAt: number;
  createdBy: number;
  modifiedAt: number;
  modifiedBy: number | null;
  templateServices: Array<ITemplateService>;
  templateMessage: Array<ITemplateMessage>;
};

export type IBusinessChildren = {
  id: number;
  unitName: string;
  sortOrder: number;
  companyDetailId: number;
  parentId: number;
};

export type IBusinessUnit = {
  id: number;
  unitName: string;
  sortOrder: number;
  companyDetailId: number;
  createdBy: number;
  modifiedBy: number | null;
  children: Array<IBusinessChildren>;
};

export type IGetCorporateModelFolder = {
  createdAt: number;
  createdBy: number;
  modifiedAt: number | null;
  modifiedBy: number | null;
  id: number;
  unitName: string;
  businessUnitId: number;
  businessUnits: IBusinessUnit;
};

export interface ContentsInterface {
  id: number;
  children: any[];
  unitName: string;
  employees: {firstName: string; lastName: string}[];
  percentage: number;
  totalProjects: number;
}

export interface ServiceTemplatesInterface {
  contents: ContentsInterface[];
  hasNext?: boolean;
  pageSize?: number;
  page?: number;
  total?: number;
  totalPages?: number;
}

export interface IGetDataInterface<T> {
  contents: T[];
  hasNext?: boolean;
  pageSize?: number;
  page?: number;
  total?: number;
  totalPages?: number;
}

export type ISortField =
  | 'unitName'
  | 'totalProjects'
  | 'id'
  | 'percentage'
  | 'employees';

export type ISortFieldUser =
  | 'firstName'
  | 'lastName'
  | 'totalProjects'
  | 'id'
  | 'percentage';

export type IBodyUserByDepartmentOrService = {
  companyId: string;
  businessUnitId?: string | number;
  sortField: ISortFieldUser;
  sortDirection: 'asc' | 'desc';
  startDate: string;
  endDate: string;
  search: string;
};

export type IBodyDashboardStatus = {
  companyId: string | number;
  startDate?: string;
  endDate?: string;
  businessUnitId: string | number;
};

export type IBodyDepartmentOrServiceDashboard = Omit<
  IBodyUserByDepartmentOrService,
  'sortField' | 'businessUnitId'
> & {
  sortField: ISortField;
  page: number;
  pageSize: number;
};

export const corporateSettingSlide = createApi({
  reducerPath: 'corporate',
  baseQuery: fetchBaseQuery({
    baseUrl: baseQuery.baseUrlCorporate,
    prepareHeaders: headers => {
      headers.set(GraviteeTransactionIdKey, graviteeTransactionId());
      headers.set(
        'Authorization',
        `Bearer ` + store.getState().authentication.userToken,
      );
      return headers;
    },
  }),
  tagTypes: ['corporate-setting-votre-marque'],
  endpoints: build => ({
    getDepartmentOrService: build.query<
      ServiceTemplatesInterface,
      {
        companyId: string | number;
        search?: string;
        page: number;
        pageSize: number;
      }
    >({
      query: ({search = '', ...rest}) => ({
        url: `/api${env.VITE_VERSION}/business-units`,
        params: {search, ...rest},
      }),
      // providesTags: ['corporate-setting-votre-marque'],
    }),

    /** Add service or departments **/
    addServiceOrDepartments: build.mutation<
      ContentsInterface,
      {unitName: string; sortOrder: number | null; companyId: number | string}
    >({
      query: ({unitName, sortOrder, companyId}) => ({
        url: `/api${env.VITE_VERSION}/business-units`,
        method: Verb.Post,
        body: {
          unitName,
          sortOrder,
          companyId,
        },
      }),
      invalidatesTags: ['corporate-setting-votre-marque'],
    }),

    /** Create model folder **/
    getCorporateModelFolder: build.query<
      Array<IGetCorporateModelFolder>,
      {id: number}
    >({
      query: ({id}) => ({
        url: `/api${env.VITE_VERSION}/folders/company/${id}`,
        method: Verb.Get,
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),

    /** Get User Access **/
    getUserAccess: build.query<
      IGetDataInterface<CorporateUserAccess>,
      {
        page?: number;
        pageSize?: number;
        sortField?: GetKeys<CorporateUserAccess>;
        sortDirection?: 'asc' | 'desc';
      }
    >({
      query: ({
        page = 1,
        pageSize = 15,
        sortField = 'id',
        sortDirection = 'desc',
      }) => ({
        url: `/api${env.VITE_VERSION}/user-access`,
        params: {page, pageSize, sortField, sortDirection},
      }),
    }),
  }),
});

export const {
  useLazyGetUserAccessQuery,
  useLazyGetDepartmentOrServiceQuery,
  useAddServiceOrDepartmentsMutation,
} = corporateSettingSlide;
