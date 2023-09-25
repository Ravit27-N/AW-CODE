export interface AnalyticsDistributionCriteria {
  customer: string;
  preferences: {
    name: string;
    active: boolean,
    enable: boolean,
    preferences: AnalyticsSubDistributionCriteria[],
  }[]
}

export interface AnalyticsSubDistributionCriteria {
  name: string,
  active: boolean,
  enabled: boolean,
}
