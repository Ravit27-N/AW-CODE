export interface PreferenceModel {
  name: string;
  active: boolean;
  enabled: boolean;
  preferences: Array<{ name: string; active: boolean; enabled: boolean; }>;
}

export interface PreferenceAPIResponse {
  clientName: string;
  preferences: Array<PreferenceModel>;
}
