import {Participant} from '@/constant/NGContant';

type TypeParticipant = Participant;

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
export type IUploadForm = {
  recipients: IRecipient[];
};
