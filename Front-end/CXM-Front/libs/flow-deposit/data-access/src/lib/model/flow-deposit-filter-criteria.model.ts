export interface FlowDepositFilterCriteriaModel{
  sortByField?: string;
  sortDirection?: string;
  filter?: string;
  channels?: string[];
  subChannels?: string[];
  users?: string[];
  depositModes?: string[];
  fileId?: string;
}
