import {
  AnalyticsDistributionCriteria,
  FilterCriteriaModel
} from '@cxm-smartflow/analytics/data-access';

export class AnalyticReportUtils {
  public static generateAnalyticsReportTabs(props: { filterCriteria: FilterCriteriaModel, distributionCriteria: AnalyticsDistributionCriteria }): any {
    const analyticsReportTabs = Array.from(props.distributionCriteria.preferences).reduce((prev, curr) => {

      if (curr.name === 'Postal' && curr.active) {
        return [...prev, curr.name];
      }

      if (curr.name === 'Digital' && curr.active) {
        const email = curr.preferences.find(item => item.name === 'Email' && item.active);
        const sms = curr.preferences.find(item => item.name === 'Sms' && item.active);
        const preferences: string[] = [];
        if (email) {
          preferences.push(email.name);
        }
        if (sms) {
          preferences.push(sms.name);
        }
        return [...prev, ...preferences];
      }

      return [...prev];
    }, ['global']);

    const filterChannelDisabled: string[] = [];

    const isPostalEnabled = props.distributionCriteria.preferences.find(item => item.name === "Postal" && item.active);
    const isDigitalEnabled = props.distributionCriteria.preferences.find(item => item.name === "Digital" && item.active);

    if (!isPostalEnabled) {
      filterChannelDisabled.push('Postal');
    }

    if (!isDigitalEnabled) {
      filterChannelDisabled.push('Digital');
    }


    let filterCategoryDisabled: string[] = [];

    if (!isPostalEnabled) {
      const categories = props.filterCriteria.subChannel.find(item => item.key === 'flow.traceability.sub-channel.postal')?.value.split(',') || [];
      filterCategoryDisabled = [...filterCategoryDisabled, ...categories];
    }

    if (!isDigitalEnabled) {
      const categories = props.filterCriteria.subChannel.filter(item => item.key === 'flow.traceability.sub-channel.digital')[0]?.value.split(',');
      filterCategoryDisabled = [...filterCategoryDisabled, ...categories];
    }

    if (isDigitalEnabled) {
      const preferences = props.distributionCriteria.preferences.find(item => item.name === 'Digital');
      if (preferences) {
        const disabledCategories = preferences.preferences.filter(item => !item.active).map(item => item.name);
        filterCategoryDisabled = [...filterCategoryDisabled, ...disabledCategories];
      }
    }

    return { analyticsReportTabs, filterCategoryDisabled, filterChannelDisabled };
  }
}
