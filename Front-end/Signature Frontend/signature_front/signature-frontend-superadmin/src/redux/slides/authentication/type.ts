import {Participant, SIGNING_PROCESS} from '@/constant/NGContant';

type TypeParticipant = Participant;

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

export type IRecipient = {
  lastName?: string;
  firstName?: string;
  role?: TypeParticipant;
  email?: string;
  phone?: string;
  id?: number | string;
  invitationStatus?: string;
  documentStatus?: string;
  sortOrder?: number;
  projectId?: number | string;
  checked?: boolean;
};

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
  signatories: IRecipient[];
  approvals: IRecipient[];
  viewers: IRecipient[];
  recipients: IRecipient[];
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
    template: any;
  };
  activeActorEnvoi: {
    role: Participant;
    id: number;
    signatoryName: string;
  } | null;
  selectEnvoiData: ISelectEnvoiById | null;
  projectDetailActions: {
    [k in IProjectDetailActions]: boolean;
  };
  createModel: ITempCreateModel | null;
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
  pan: boolean;
  signatoryName: string;
  annotationStamp: number | string | null;
  annotaionmention: number | string | null;
  documentDetails?: IDocumentDetails[] | null;
};

export type IAction = {
  payload: any;
  type: string;
};

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
  templateMessage?: {
    titleInvitation: string;
    messageInvitation: string;
    expireDate: string;
    sendReminder: 1 | 2 | 3 | 4;
  };
  unitName: string;
}

export type ITempCreateModel = TemplateInterface;
