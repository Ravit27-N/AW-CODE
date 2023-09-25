import { createFeatureSelector, createSelector } from '@ngrx/store';
import { manageAnalyticsReducerKey } from './manage-analytics.reducer';
import {
  ManageAnalyticsStateModel,
} from '../../models';

const manageAnalyticsSelector = createFeatureSelector<ManageAnalyticsStateModel>(
  manageAnalyticsReducerKey
);

export const selectAllStates = createSelector(
  manageAnalyticsSelector,
  (state) => state
);


// Filter options.
export const selectFilterCriteria = createSelector(manageAnalyticsSelector, (state) => state.filterCriteria);
export const selectAnalyticsDisabledChannels = createSelector(manageAnalyticsSelector, (state) => state.filterChannelDisabled);
export const selectAnalyticsDisabledCategories = createSelector(manageAnalyticsSelector, (state) => state.filterCategoryDisabled);
export const selectFillers = createSelector(manageAnalyticsSelector, (state) => state.fillers);
export const selectAnalyticsReportTabs = createSelector(manageAnalyticsSelector, (state) => state.analyticsReportTabs);
export const selectRefreshDate = createSelector(manageAnalyticsSelector, (state) => state.refreshFetchGraphDate);


// Volume receive graph.
export const selectVolumeReceive = createSelector(manageAnalyticsSelector, (state) => state.volumeReceivedGraph);


// Production details graph.
export const selectGlobalProductionDetail = createSelector(manageAnalyticsSelector, (state) => state.globalProductionDetails);
export const selectProductionDetail = createSelector(manageAnalyticsSelector, (state) => state.productionDetails);

// Production progress graph.
export const selectProductionProgresses = createSelector(manageAnalyticsSelector, (state) => state.productionProgresses);

// Distribution volume received graph.
export const selectDistributionVolumeReceive = createSelector(
  manageAnalyticsSelector,
  (state) => state.distributionVolumeReceivedGraph
);

// Distribution pND.
export const selectDistributionPND = createSelector(manageAnalyticsSelector, (state) => state.distributionPNDGraph);
export const selectDistributionPNDLoading = createSelector(manageAnalyticsSelector, state => state.distributionPNDLoading)

export const selectProcessedMail = createSelector(
  manageAnalyticsSelector,
  (state) => state.processedMailGraph
);

export const selectProcessedMailLoading = createSelector(
  manageAnalyticsSelector,
  (state) => state.processedMailLoading
);
export const selectDistributionByStatus = createSelector(manageAnalyticsSelector, (state) => state.distributionByStatusGraph);
export const selectDistributionByStatusLoading = createSelector(manageAnalyticsSelector, state => state.distributionByStatusLoading);
export const selectCanExportCsv = createSelector(manageAnalyticsSelector, state => state.isCanExport);

