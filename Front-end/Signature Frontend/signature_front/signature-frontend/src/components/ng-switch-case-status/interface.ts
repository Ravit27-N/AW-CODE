import {InvitationStatus, ProjectStatus} from '@/constant/NGContant';

export interface DocStatusInterfaces {
  SIGNED: InvitationStatus.SENT;
  APPROVED: InvitationStatus.APPROVED;
  REFUSED: InvitationStatus.REFUSED;
  IN_PROGRESS: InvitationStatus.IN_PROGRESS;
  RECEIVED: InvitationStatus.RECEIVED;
  READ: InvitationStatus.READ;
}
export interface ProjectStatusInterfaces {
  COMPLETED: ProjectStatus.COMPLETED;
  DRAFT: ProjectStatus.DRAFT;
  IN_PROGRESS: InvitationStatus.IN_PROGRESS;
  EXPIRED: ProjectStatus.EXPIRED;
  REFUSED: InvitationStatus.REFUSED;
  ABANDON: ProjectStatus.ABANDON;
  URGENT: 'URGENT';
}
