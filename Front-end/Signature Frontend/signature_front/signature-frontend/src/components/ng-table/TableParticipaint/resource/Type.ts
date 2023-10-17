import {DocStatusInterfaces} from '@components/ng-switch-case-status/interface';

export interface NGTableParticipantTypeCreateData {
  id?: string;
  order: string;
  nom: string;
  phone: string;
  email: string;
  role: string;
  status: keyof DocStatusInterfaces;
  invitation: string;
  action: number; // It is used for ID for each row
  comment: string;
  flowId?: string;
}
