import {SignatureProject} from '@/utils/request/interface/Project.interface';

export interface ParticipantData {
  contents: SignatureProject[];
  page: number;
  pageSize: number;
  total: number;
}
