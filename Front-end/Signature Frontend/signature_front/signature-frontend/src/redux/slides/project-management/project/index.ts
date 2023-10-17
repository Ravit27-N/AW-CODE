import {
  AutoReminder,
  ChannelOptions,
  GraviteeTransactionIdKey,
  HistoryStatus,
  KeySignatureLevel,
  Participant,
  ProjectStatus,
  SIGNING_PROCESS,
} from '@/constant/NGContant';
import {IRecipient} from '@/pages/form/process-upload/type';
import {store} from '@/redux';
import {ISignatureLevels} from '@/redux/slides/authentication/type';
import {ApiFacade} from '@/utils/common/ApiFacade';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {
  CorporateProjectData,
  ProjectData,
  CountProjectData,
} from '@/utils/request/interface/Project.interface';
import {Verb} from '@/utils/request/interface/type';
import {Detail} from '@/utils/request/services/MyService';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/dist/query/react';
import env from '../../../../../env.config';
import {baseQuery} from '../../RTKBaseQuery';
import {ISignatory} from '../signatory';
import {ProjectStatusInterfaces} from '@/components/ng-switch-case-status/interface';

export type IProjectStep1 = {
  step: 1 | 2 | 3 | 4;
  name: string;
  files: File[];
  status: string | number;
  templateId?: string | number | null;
  orderSign: boolean;
  orderApprove: boolean;
};

export type IDetail = {
  expiresDate: number;
  id: number;
  messageInvitation: string;
  titleInvitation: string;
  type: Participant.Approval | Participant.Receipt | Participant.Signatory;
};

export type IProjectById = {
  details: Array<IDetail>;
  signatories: IRecipient[];
  expireDate: number;
  id: string;
  name: string;
  orderSign: boolean;
  orderApprove: boolean;
  templateId: number;
  step: string | number;
  documents: Array<IDocuments>;
  setting: ISignatureLevels;
  signatureLevel: KeySignatureLevel;
  status: keyof ProjectStatusInterfaces;
};
/** type document details */
export type IDocumentDetails = {
  x?: number;
  y?: number;
  width?: number;
  height?: number;
  contentType?: string;
  fileName?: string;
  text?: string;
  textAlign?: number;
  fontSize?: number;
  fontName?: string;
  pageNum?: number;
  type?: string;
  signatoryId?: number | string;
  id?: string | number | null;
};

/** type documents */
export type IDocuments = {
  contentType: string;
  documentDetails: Array<IDocumentDetails>;
  editedFileName: string | null;
  extension: string | 'pdf';
  fileName: string;
  fullPath: string;
  id: number;
  originalFileName: string;
  projectId: number;
  signedDocUrl: string | null;
  size: number;
  totalPages: number;
};

export type IHistories = {
  id: number;
  dateStatus: number;
  action: HistoryStatus;
  actionBy: string;
  sortOrder: number;
};

export type IGetFlowId = {
  flowId: string;
  uuid: string;
  projectName: string;
  signingProcess: SIGNING_PROCESS;
  creatorInfo: {
    id: number;
    firstName: string;
    lastName: string;
  };
  invitationDate: number;
  phoneNumber: {
    removedNumber: string;
    missingLength: number;
    totalAttempts: number;
    number: string;
    validated: boolean;
  };
  otpInfo: {
    validated: boolean;
    expired: boolean;
  };
  actor: {
    firstName: string;
    lastName: string;
    documentVerified: boolean;
    role: Participant;
    processed: boolean;
    comment: string | null;
  };
  documents: [
    {
      docId: string;
      name: string;
      signedDocUrl: string;
      totalPages: number;
    },
  ];
  projectStatus: ProjectStatus;
  signatureLevel: KeySignatureLevel;
  setting: Required<ISignatureLevels>;
};

export type IGetProjects = {
  page: number;
  pageSize: number;
  filterBy?: string;
  statuses?: string[];
  sortDirection: 'desc' | 'asc';
  sortByField: string;
  search?: string;
  startDate?: string;
  endDate?: string;
};

export type IProXSig = Omit<IProjectStep1, 'files' | 'status'> & {
  signatories: ISignatory[] | IRecipient[];
} & {id: string | number; details: any[]} & {
  signatureLevel: KeySignatureLevel;
};

export type IBodyProjectDetail = {
  type:
    | Participant.Approval
    | Participant.Signatory
    | Participant.Receipt
    | Participant.Viewer;
  titleInvitation: string;
  messageInvitation: string;
  projectId: number;
  id: number | null;
};

export type IBodyProjectStepFour = {
  step?: string;
  name: string;
  status: string;
  orderSign: boolean;

  id: string | number;
  signatories: IRecipient[];
  details: Detail[];
  documents: any[];
  expireDate?: string;
  autoReminder?: boolean;
  channelReminder?: ChannelOptions;
  reminderOption?: AutoReminder | null;
  signatureLevel: KeySignatureLevel;
  setting: ISignatureLevels;
};

type IProject = {
  id: number;
  name: string;
  flowId: string;
  status: Participant;
  createdAt: number;
  expireDate: number;
  createdBy: number;
  createdByUser: {
    id: number;
    userEntityId: string;
    firstName: string;
    lastName: string;
    email: string;
  };
};

export type IGetParticipantProject = {
  signatories: {
    contents: Array<IRecipient & {uuid: string; project: IProject}>;
    page: number;
    pageSize: number;
    totalPages: number;
    total: number;
    hasNext: boolean;
  };
  totalInProgress: number;
  totalDone: number;
};

export type IGetProject = {
  createdBy: number;
  id: number;
  name: string;
  documents: IGetDocument[];
  step: string;
  orderSign: boolean;
  orderApprove: boolean;
  templateId: number;
  templateName: string;
  autoReminder: boolean;
  channelReminder: any;
  reminderOption: any;
  flowId: any;
  status: string;
  modifiedBy: any;
  createdAt: number;
  modifiedAt: any;
  expireDate: any;
  signatories: IRecipient[];
  details: any[];
  histories: IGetHistory[];
  createdByUser: any;
};

export type IGetDocument = {
  id: number;
  fileName: string;
  signedDocUrl: any;
  editedFileName: any;
  originalFileName: string;
  contentType: string;
  fullPath: string;
  size: number;
  extension: string;
  projectId: number;
  totalPages: number;
  documentDetails: any;
};

export interface IGetHistory {
  id: number;
  dateStatus: number;
  action: string;
  actionBy: string;
  sortOrder: number;
}

// Project slide
export const projectSlide = createApi({
  reducerPath: 'project',
  tagTypes: ['Project'],
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
  /**
   * count project not yet supported with uuid
   * **/
  endpoints: builder => ({
    countProjects: builder.query<CountProjectData, {filterBy: string}>({
      query: ({filterBy}) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/projects/count`,
        method: Verb.Get,
        params: {
          filterBy,
        },
      }),
      providesTags: () => [{type: 'Project', id: 'LIST'}],
    }),

    /** get number of project in corporate */
    countCorporateProjects: builder.query<
      CountProjectData,
      {filterBy: string; userId?: string}
    >({
      query: ({filterBy, userId}) => ({
        url: `${ApiFacade(
          baseQuery.projectManagement,
        )}/projects/corporate/count`,
        method: Verb.Get,
        params: {
          // filterBy,
          userId,
        },
      }),
      // providesTags: () => [{type: 'Project', id: 'LIST'}],
    }),

    // Get project
    getProjects: builder.query<ProjectData, IGetProjects>({
      query: ({
        page,
        pageSize,
        statuses,
        sortDirection,
        sortByField,
        search,
        filterBy,
        startDate,
        endDate,
      }) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/projects/user`,
        method: Verb.Get,
        params: {
          page,
          pageSize,
          statuses,
          sortDirection,
          sortByField,
          search,
          filterBy,
          startDate,
          endDate,
        },
      }),
      providesTags: () => [{type: 'Project', id: 'LIST'}],
    }),

    /** get corporate project */
    getCorporateProjects: builder.query<
      CorporateProjectData,
      Omit<IGetProjects, 'sortByField'> & {userId?: string; sortField: string}
    >({
      query: ({
        page,
        pageSize,
        statuses,
        sortDirection,
        sortField,
        search,
        filterBy,
        startDate,
        endDate,
        userId,
      }) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/projects/corporate`,
        method: Verb.Get,
        params: {
          page,
          pageSize,
          statuses,
          sortDirection,
          sortField,
          search,
          filterBy,
          startDate,
          endDate,
          userId,
        },
      }),
      // providesTags: () => [{type: 'Project', id: 'LIST'}],
    }),

    // Get project by ID
    getProjectById: builder.query<IProjectById, {id: string}>({
      query: ({id}) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/projects/${id}`,
        method: Verb.Get,
      }),
      providesTags: () => [
        {type: 'Project', id: 'single'},
        {type: 'Project', id: 'LIST'},
      ],
    }),

    // Get corporate project by ID
    getCorporateProjectById: builder.query<IProjectById, {id: string}>({
      query: ({id}) => ({
        url: `${env.VITE_CONTEXT_API}${env.VITE_VERSION}/projects/${id}`,
        method: Verb.Get,
      }),
      providesTags: () => [{type: 'Project', id: 'single'}],
    }),

    // Add project
    addProject: builder.mutation<IGetProject, IProjectStep1 | FormData>({
      query: body => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/projects`,
        method: Verb.Post,
        body,
      }),
      // invalidatesTags: () => [{type: 'Project', id: 'LIST'}],
    }),
    // Update project
    updateProject: builder.mutation<any, IProXSig>({
      query: body => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/projects`,
        method: Verb.Put,
        body,
        headers: {
          'Content-Type': 'application/json',
        },
      }),
      // invalidatesTags: () => [{type: 'Project', id: 'LIST'}],
    }),
    // Cancel project
    cancelProject: builder.mutation<any, {projectId: string}>({
      query: ({projectId}) => ({
        url: `${ApiFacade(
          baseQuery.projectManagement,
        )}/projects/cancel/${projectId}`,
        method: Verb.Put,
        headers: {
          'Content-Type': 'application/json',
        },
      }),
      invalidatesTags: () => [{type: 'Project', id: 'LIST'}],
    }),
    // View document
    viewDocument: builder.query<any, {docId: string}>({
      query: ({docId}) => ({
        url: `${ApiFacade(
          baseQuery.projectManagement,
        )}/projects/view-documents`,
        method: Verb.Get,
        params: {docName: docId},
        responseHandler: 'text',
      }),
      providesTags: () => [{type: 'Project', id: 'LIST'}],
    }),

    /** Update project step 3 */
    updateProjectStepThree: builder.mutation<
      IProjectById,
      {
        step?: number;
        name: string;
        status: string;
        id: string | number;
        orderSign: boolean;
        signatories: IRecipient[];
        signatureLevel: KeySignatureLevel;
        details: Detail[];
        documents: any[];
        documentDetails: IDocumentDetails[];
      }
    >({
      query: ({
        id,
        step,
        details,
        name,
        status,
        orderSign,
        signatories,
        signatureLevel,
        documents,
        documentDetails,
      }) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/projects`,
        method: Verb.Put,
        body: {
          id,
          step,
          details,
          name,
          status,
          orderSign,
          signatories,
          signatureLevel,
          documents,
          documentDetails,
        },
      }),
      invalidatesTags: () => [{type: 'Project', id: 'LIST'}],
    }),
    /** Update project detail */
    updateProjectDetail: builder.mutation<IDetail, IBodyProjectDetail>({
      query: body => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/project-details`,
        body,
        method: Verb.Post,
      }),
    }),
    /** Update project step 4 */
    updateProjectStepFour: builder.mutation<IProjectById, IBodyProjectStepFour>(
      {
        query: body => ({
          url: `${ApiFacade(baseQuery.projectManagement)}/projects`,
          method: Verb.Put,
          body,
        }),
        invalidatesTags: () => [{type: 'Project', id: 'single'}],
      },
    ),

    /** Update project expired date */
    updateProjectExpiredDate: builder.mutation<
      IProjectById,
      {id: string | number; expiredDate: string}
    >({
      query: ({id, expiredDate}) => ({
        url: `${ApiFacade(
          baseQuery.projectManagement,
        )}/projects/update/expired/${id}?expiredDate=${expiredDate}`,
        method: Verb.Put,
      }),
      invalidatesTags: () => [{type: 'Project', id: 'single'}],
    }),

    /** Get participant projects */
    getParticipantProject: builder.query<
      IGetParticipantProject,
      {status: 'IN_PROGRESS' | 'DONE'}
    >({
      query: ({status}) => ({
        url: `${ApiFacade(baseQuery.projectManagement)}/signatories/projects`,
        method: Verb.Get,
        params: {status},
      }),
    }),
  }),
});

export const {
  useAddProjectMutation,
  useGetProjectByIdQuery,
  useGetProjectsQuery,
  useUpdateProjectMutation,
  useUpdateProjectStepFourMutation,
  useUpdateProjectStepThreeMutation,
  useUpdateProjectDetailMutation,
  useUpdateProjectExpiredDateMutation,
  useCountProjectsQuery,
  useLazyViewDocumentQuery,
  useGetParticipantProjectQuery,
  useGetCorporateProjectsQuery,
  useLazyGetCorporateProjectsQuery,
  useGetCorporateProjectByIdQuery,
  useCountCorporateProjectsQuery,
  /** cancel project hook */
  useCancelProjectMutation,
} = projectSlide;
