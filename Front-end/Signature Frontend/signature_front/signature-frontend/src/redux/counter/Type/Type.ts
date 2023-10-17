import {
  AutoReminder,
  ChannelOptions,
  InvitationStatus,
  Participant,
} from '@/constant/NGContant';
import {FirstName, LastName} from '@/utils/request/interface/Project.interface';

export interface TypeOption {
  opt1: string;
  opt2: ChannelOptions;
  opt3: AutoReminder | null;
  opt4: Date;
  title: string;
  message: string;
  checkDate?: boolean;
  docName?: string;
}
export interface TypeUserInfo {
  name: string;
  projectName?: string;
  idProject?: number | string;
}
export interface TypeRecipientInfo {
  name: string;
  firstName?: string;
  role?: string;
  email?: string;
  phoneNumber?: string;
  id: number | string;
  invitationStatus: string;
  sortOrder: number;
  projectId: number | string;
}

export interface TypeTableComponent {
  id: string;
  project: string;
  model: string;
  document: string;
  recipient: number;
  completions: number;
  createdAt: string;
  deadline: string;
  actions: number;
  participants: string[];
  signatories: Signatory[];
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
