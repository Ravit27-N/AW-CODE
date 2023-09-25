import { SubChannel } from './check-list.model';

export interface FilterCriteria {
  channel?: SubChannel[];
  depositMode?: SubChannel[];
  flowStatus?: SubChannel[];
  subChannel?: SubChannel[];
}
