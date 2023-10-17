import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';

import {ProjectStatusInterfaces} from '@/components/ng-switch-case-status/interface';
import {
  GraviteeTransactionIdKey,
  KeySignatureLevel,
  ProjectStatus,
} from '@/constant/NGContant';
import {store} from '@/redux';
import {IThemeEnterPrise} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {GetKeys} from '@/utils/common/GetTypeFromObjectKeys';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {IDType, Verb} from '@/utils/request/interface/type';
import {initialCorporate} from '@/utils/roles/corporate/corporate-dashboard';
import {baseQuery} from '../RTKBaseQuery';
import {ApiFacade} from '@/utils/common/ApiFacade';
import {ISignatureLevels} from '@/redux/slides/authentication/type';

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

export type CorporateModelFolder = {
  createdAt: string;
  createdBy: IDType;
  modifiedAt: string | null;
  modifiedBy: IDType;
  id: IDType;
  unitName: string;
  businessUnitId: IDType;
  inputValue?: string;
  businessUnits?: IBusinessUnit;
};

export type IGetCorporateModelFolder = {
  contents: CorporateModelFolder[];
  hasNext?: boolean;
  pageSize?: number;
  page?: number;
  total?: number;
  totalPages?: number;
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
    baseUrl: `${baseQuery.baseApiUrl}`,
    // baseQuery.baseUrlCorporate,
    prepareHeaders: headers => {
      headers.set(GraviteeTransactionIdKey, graviteeTransactionId());
      headers.set(
        'Authorization',
        `Bearer ` + store.getState().authentication.userToken,
      );
      return headers;
    },
  }),
  tagTypes: ['corporate-setting-votre-marque', 'corporate-model-folder'],
  endpoints: build => ({
    getCorporateSetting: build.query<CorporateCompanyInterface, unknown>({
      query: () => ({
        url: `${ApiFacade(
          baseQuery.corporateProfile,
        )}/corporate/settings/themes`,
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),
    updateThemeCorporate: build.mutation<IThemeEnterPrise, FormData>({
      query: body => ({
        url: `${ApiFacade(
          baseQuery.corporateProfile,
        )}/corporate/settings/save-or-update`,
        method: Verb.Put,
        body,
      }),
      invalidatesTags: ['corporate-setting-votre-marque'],
    }),
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
        url: `${ApiFacade(baseQuery.corporateProfile)}/business-units`,
        params: {search, ...rest},
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
        url: `${ApiFacade(baseQuery.corporateProfile)}/user-access`,
        params: {page, pageSize, sortField, sortDirection},
      }),
    }),

    getDepartmentOrServiceDashboard: build.query<
      ServiceTemplatesInterface,
      IBodyDepartmentOrServiceDashboard
    >({
      query: ({search = '', ...rest}) => ({
        url: `${ApiFacade(
          baseQuery.corporateProfile,
        )}/business-units/dashboard`,
        params: {search, ...rest},
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),
    /** Get User by department **/
    getUserByDepartmentOrService: build.query<
      DocumentTableInterface,
      IBodyUserByDepartmentOrService
    >({
      query: ({startDate, endDate, ...rest}) => ({
        url: `${ApiFacade(
          baseQuery.corporateProfile,
        )}/employees/dashboard?startDate=${startDate}&endDate=${endDate}`,
        params: {...rest},
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),
    // get dashboard status
    getDashboardStatus: build.query<IGetDashboard, IBodyDashboardStatus>({
      query: ({companyId, startDate, endDate, businessUnitId}) => ({
        url: `${ApiFacade(
          baseQuery.corporateProfile,
        )}/dashboard?&startDate=${startDate}&endDate=${endDate}`,
        method: Verb.Get,
        params: {
          companyId,
          businessUnitId,
        },
        responseHandler: async response => {
          const res = await response.json();
          return res.contents ? res : initialCorporate;
        },
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),
    /** Add service or departments **/
    addServiceOrDepartments: build.mutation<
      any,
      {unitName: string; sortOrder: number | null; companyId: number | string}
    >({
      query: ({unitName, sortOrder, companyId}) => ({
        url: `${ApiFacade(baseQuery.corporateProfile)}/business-units`,
        method: Verb.Post,
        body: {
          unitName,
          sortOrder,
          companyId,
        },
      }),
      invalidatesTags: ['corporate-setting-votre-marque'],
    }),
    /** get model folder by id **/
    getModelFolderById: build.query<
      CorporateModelFolder,
      {
        id: IDType;
      }
    >({
      query: ({id}) => ({
        url: `${ApiFacade(baseQuery.corporateProfile)}/folders/${id}`,
        method: Verb.Get,
      }),
    }),
    /** get model folder by company id **/
    getCorporateModelFolder: build.query<
      Array<CorporateModelFolder>,
      {id: number}
    >({
      query: ({id}) => ({
        url: `${ApiFacade(baseQuery.corporateProfile)}/folders/${id}`,
        method: Verb.Get,
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),
    /** get model folder **/
    getModelFolder: build.query<
      IGetCorporateModelFolder,
      {
        page?: number;
        pageSize?: number;
        sortField?: GetKeys<CorporateUserAccess>;
        sortDirection?: 'asc' | 'desc';
        search?: string;
      }
    >({
      query: params => ({
        url: `${ApiFacade(baseQuery.corporateProfile)}/folders/user`,
        method: Verb.Get,
        params,
      }),
      providesTags: ['corporate-model-folder'],
      keepUnusedDataFor: 0,
    }),
    /**
     * Create folder
     * */
    createFolder: build.mutation<
      CorporateModelFolder,
      Partial<CorporateModelFolder>
    >({
      query: body => ({
        url: `${ApiFacade(baseQuery.corporateProfile)}/folders`,
        method: Verb.Post,
        body,
      }),
      invalidatesTags: ['corporate-model-folder'],
    }),
    /** get all signatureLevels form corporate **/
    getSignatureLevels: build.query<ISignatureLevels[], {uuid: string}>({
      query: ({uuid}) => ({
        url: `${ApiFacade(
          baseQuery.corporateProfile,
        )}/corporate/settings/levels`,
        params: {uuid},
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),
    /** get specific signatureLevels form corporate **/
    getSpecificSignatureLevels: build.query<
      ISignatureLevels,
      {uuid: string; signatureLevel: KeySignatureLevel}
    >({
      query: ({uuid, signatureLevel}) => ({
        url: `${ApiFacade(
          baseQuery.corporateProfile,
        )}/corporate/settings/level/${uuid}`,
        params: {signatureLevel},
      }),
      providesTags: ['corporate-setting-votre-marque'],
    }),
  }),
});

export const {
  useUpdateThemeCorporateMutation,
  useLazyGetDepartmentOrServiceQuery,
  useLazyGetUserAccessQuery,
  useGetUserByDepartmentOrServiceQuery,
  useAddServiceOrDepartmentsMutation,
  useGetDepartmentOrServiceDashboardQuery,
  useGetCorporateModelFolderQuery,
  useGetModelFolderQuery,
  useLazyGetModelFolderByIdQuery,
  useCreateFolderMutation,
  useGetSignatureLevelsQuery,
  useLazyGetSpecificSignatureLevelsQuery,
  useLazyGetDashboardStatusQuery,
} = corporateSettingSlide;
