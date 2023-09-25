export interface CriteriaDistributionModel {
  customer: string;
  preferences: Array<Preference>;
}

export interface Preference {
  name: string;
  active: boolean;
  enabled: boolean;
  preferences: Array<PreferenceCategory>;
}

export interface PreferenceCategory {
  name: string;
  active: boolean;
  enabled: boolean;
}

export interface CriteriaDistributionPayload {
  customer: string;
  preference: PreferencePayload;
}

export interface PreferencePayload {
  name: string;
  active: boolean;
}

export interface CritteriaDistributionPayload {
  customer: string;
  preference: PreferencePayload;
}
