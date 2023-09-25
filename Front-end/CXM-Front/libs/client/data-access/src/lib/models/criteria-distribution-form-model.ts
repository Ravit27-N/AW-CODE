export interface CriteriaDistributionFormModel {
  name: string;
  key: string;
  active: boolean;
  enabled: boolean;
  manageable: boolean;
  categories: Array<CriteriaCategoryFormModel>;
}

export interface CriteriaCategoryFormModel {
  name: string;
  key: string;
  active: boolean;
  enabled: boolean;
}
