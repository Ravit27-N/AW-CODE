import { SharedListCriteria } from '../../../shared';

export interface ScopedDemandListFilterCriteria extends SharedListCriteria {
  isDeleted: boolean;
}
