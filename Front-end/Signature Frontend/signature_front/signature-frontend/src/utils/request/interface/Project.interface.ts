import {InvitationStatus, Participant} from '@/constant/NGContant';

export interface ProjectData {
  contents: SignatureProject[];
  page: number;
  pageSize: number;
  total: number;
  hasNext: boolean;
}
export interface CorporateProjectData {
  contents: CorporateSignatureProject[];
  page: number;
  pageSize: number;
  total: number;
  hasNext: boolean;
}

export interface CountProjectData {
  totalProjects: number;
  statuses: StatusesInterface[];
}
export interface StatusesInterface {
  id: string;
  label: string;
  value: number;
}
export const defaultListProjectResp: ProjectData = {
  contents: [],
  page: 0,
  pageSize: 0,
  total: 0,
  hasNext: true,
};

export interface ProjectDetailInterface {
  id?: string | number;
  type: string;
  titleInvitation: string;
  messageInvitation: string;
}
export interface DocumentInterface {
  id: string;
  fileName: string;
  signedDocUrl: string;
}
export interface SignatureProject {
  id: number;
  name: string;
  templateName?: string;
  flowId: string;
  documents: DocumentInterface[];
  expireDate?: number;
  details: ProjectDetailInterface[];
  createdAt: number;
  step: string;
  status: string;
  signatories: Signatory[];
}

export interface CorporateSignatureProject extends SignatureProject {
  createdBy: string;
}

export interface Signatory {
  id: number;
  firstName: FirstName;
  lastName: LastName;
  role: Participant;
  email: string;
  phone: string;
  invitationStatus: InvitationStatus;
  documentStatus: null;
  sortOrder: number;
}

export enum FirstName {
  Weak1 = 'weak1',
  Weak2 = 'weak2',
}

export enum LastName {
  Jonh = 'Jonh',
}
