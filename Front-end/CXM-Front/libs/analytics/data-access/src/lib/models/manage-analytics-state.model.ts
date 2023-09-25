import { FilterCriteriaModel } from './filter-criteria.model';
import { FilterListModel } from './filter-list.model';
import {
  DistributionByStatus,
  PreferenceDistributionGraphModel, PreferenceDistributionPNDGraphModel,
  PreferenceGraphModel, PreferenceProcessedMailModel, ProcessedMailGraph,
  VolumeReceiveGraph,
} from './preference-graph.model';
import { FilterOptionModel, ReportingPostalParams } from './filter-option.model';
import { GlobalProductionDetailsModel, ProductionDetails } from './global-production-details.model';
import { ProductionProgressModel } from './production-progress.model';
import { AnalyticsDistributionCriteria } from './analytics-distribution-criteria';

export interface ManageAnalyticsStateModel {
  filterCriteria: FilterCriteriaModel;
  filterChannelDisabled: string[];
  filterCategoryDisabled: string[];
  fillers: FilterListModel[];
  volumeReceive: PreferenceGraphModel[];
  refreshFetchGraphDate: Date;
  volumeReceivedGraph: VolumeReceiveGraph;
  filterOption: FilterOptionModel;
  globalProductionDetails: GlobalProductionDetailsModel;
  productionDetails: ProductionDetails;
  productionProgresses: {
    content: ProductionProgressModel[];
    isFetching: boolean;
  };
  distributionVolumeReceived: PreferenceDistributionGraphModel[];
  distributionVolumeReceivedGraph: VolumeReceiveGraph;
  requestedAt: string;
  distributionCriteria: AnalyticsDistributionCriteria;
  analyticsReportTabs: string[];
  postalFilteringCriteria: ReportingPostalParams;
  distributionPND: PreferenceDistributionPNDGraphModel[];
  distributionPNDLoading: boolean;
  processedMail: PreferenceDistributionGraphModel[];
  processedMailGraph: any;
  processedMailLoading: boolean;
  distributionPNDGraph: any;
  distributionByStatus: DistributionByStatus[];
  distributionByStatusGraph: any;
  distributionByStatusLoading: boolean;
  isCanExport:boolean;
}
