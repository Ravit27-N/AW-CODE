import {KeySignatureLevel, Participant} from '@/constant/NGContant';
import {DTable} from '@/pages/end-user/sidebar/models/sidenav/sections/TableSection';
import {IRecipient} from '@/pages/form/process-upload/type';
import {TypeTemplate} from '@/utils/request/services/MyService';
import {TemplateInterface} from '../profile/template/templateSlide';
import {IDocumentDetails} from '../project-management/project';

export type IAuthentication = {
  user: {
    username: string;
  };
  files: string[];
  tempFiles: {
    active: boolean;
    fileUrl: string;
    name: string;
    documentId: string;
  }[];
  signatories: (IRecipient & {fillForm?: boolean})[];
  approvals: (IRecipient & {fillForm?: boolean})[];
  viewers: (IRecipient & {fillForm?: boolean})[];
  recipients: (IRecipient & {fillForm?: boolean})[];
  role: null | string;
  isLoading: boolean;
  isSignout: boolean;
  userToken: null | string;
  sid: null | string; // userid for a login, It should be end-user/corporate/super-admin as uuid format.
  C_UUID?: null | string; // corporateID as uuid format
  USER_COMPANY?: null | string;
  invitation: {
    inviter: string | null;
    receiver?: string | null;
    timeDelivery: string | Date | null | number;
    documents: number | string | null;
    projectName: string | null;
  };
  annotations: IAnnotaions[];
  project: {
    id: string | null;
    name: string | null;
    orderSign: boolean;
    orderApprove: boolean;
    template: TypeTemplate | null;
    step: string | null;
  };
  activeActorEnvoi: {
    role: Participant;
    id: number;
    signatoryName: string;
    email?: string;
  } | null;
  selectEnvoiData: ISelectEnvoiById | null;
  projectDetailActions: {
    [k in IProjectDetailActions]: boolean;
  };
  createModel: ITempCreateModel | null;
  storeModel: DTable | null;
  createProjectActiveRole: {
    role: Participant;
    id: string | number;
  } | null;
  signatureLevels: ISignatureLevels;
  processAdvanceSignature: IProcessAdvancedSignature;
  gbc: IIn;
};

type IIn = {
  title: string;
  description: number;
};

export type IProjectDetailActions = 'modified-date';

export type ISelectEnvoiById = {
  [k: string]: {
    id: number | null;
    name: string;
    title: string;
    description: string;
    expired: Date;
  };
};

export type IAnnotaions = {
  signatoryId: number | string | null;
  dCreateStamp: boolean;
  dMention: boolean;
  dParaph: boolean;
  pan: boolean;
  signatoryName: string;
  sortOrder?: number;
  annotationStamp: string[];
  annotaionmention: string[];
  annotationParaph: number | string | null;
  documentDetails?: IDocumentDetails[] | null;
};

export type IAction = {
  payload: any;
  type: string;
};

export interface ISignatureLevels {
  companyUuid: string;
  signatureLevel: KeySignatureLevel;
  personalTerms?: string;
  identityTerms?: string;
  documentTerms?: string;
  channelReminder?: string;
  fileType?: string[];
  companyFileType?: string[];
  companyChannel?: string;
  fileTypeSelected?: string[];
  remainderSelected?: string;
}
export interface IProcessAdvancedSignature {
  documentBack: File | null;
  documentFront: File | null;
  documentCountry: string;
  documentType: KeyAdvanceSignatureProcessDocumentType;
  documentRotation: 0 | 45 | 90 | 180;
}
export enum KeyAdvanceSignatureProcessDocumentType {
  CNI = 'id_card',
  PASSPORT = 'passport',
  STAY_BOOK = 'residency_permit',
  NONE = '',
}

export type ITempCreateModel = TemplateInterface;
