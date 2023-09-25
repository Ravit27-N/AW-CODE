import {createAction, props} from '@ngrx/store';
import {HttpErrorResponse} from '@angular/common/http';
import {DistributionByStatus, PreferenceDistributionGraphModel, PreferenceProcessedMailModel} from "../../models";
import {
  FilterCriteriaModel,
  FilterListModel,
  GlobalProductionDetailsModel,
  PreferenceGraphModel, AnalyticsDistributionCriteria, ReportingPostalParams, ProductionDetails,
  FilterOptionModel, PreferenceDistributionPNDGraphModel
} from '../../models';

// Fetch Analytics Criteria
export const fetchAnalyticsFilteringCriteria = createAction('[cxm-analytics / fetch analytics filtering criteria]');
export const fetchAnalyticsFilteringCriteriaSuccess = createAction('[cxm-analytics / fetch analytics filtering criteria success]', props<{ filterCriteria: FilterCriteriaModel, distributionCriteria: AnalyticsDistributionCriteria }>());
export const fetchAnalyticsFilteringCriteriaFail = createAction('[cxm-analytics / fetch analytics filtering criteria fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Fetch client fillers
export const fetchClientFillerList = createAction('[cxm-analytics / fetch client fillers]');
export const fetchClientFillerListSuccess = createAction('[cxm-analytics / fetch client fillers success]', props<{ fillerList: FilterListModel[] }>());
export const fetchClientFillerListFail = createAction('[cxm-analytics / fetch client fillers fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

//#region Reporting Global
// Fetch production progress graph.
export const fetchProductionProgress = createAction('[cxm-analytics / fetch production progress]', props<{ requestedAt: string }>());
export const fetchProductionProgressSuccess = createAction('[cxm-analytics / fetch production progress success]', props<{ productionProgress: PreferenceGraphModel[] }>());
export const fetchProductionProgressFail = createAction('[cxm-analytics / fetch production progress fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Fetch Volume received graph.
export const fetchVolumeGraph = createAction('[cxm-analytics / fetch volume graph]', props<{ filterOptionModel: FilterOptionModel, requestedAt: string }>());
export const fetchVolumeReceiveGraph = createAction('[cxm-analytics / fetch volume receive graph]');
export const fetchVolumeReceiveGraphSuccess = createAction('[cxm-analytics / fetch volume receive graph success]', props<{ volumeReceive: PreferenceGraphModel[] }>());
export const fetchVolumeReceiveGraphFail = createAction('[cxm-analytics / fetch volume receive graph fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Fetch production details graph.
export const fetchGlobalProductionDetailsGraph = createAction('[cxm-analytics / fetch global production detail graph]', props<{ requestedAt: string }>());
export const fetchGlobalProductionDetailsGraphSuccess = createAction('[cxm-analytics / fetch global production detail graph success]', props<{ productionDetails: GlobalProductionDetailsModel }>());
export const fetchGlobalProductionDetailsGraphFail = createAction('[cxm-analytics / fetch global production detail graph fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Fetch production details graph.
export const fetchProductionDetailsGraph = createAction('[cxm-analytics / fetch production detail graph]', props<{ requestedAt: string }>());
export const fetchProductionDetailsGraphSuccess = createAction('[cxm-analytics / fetch  production detail graph success]', props<{ productionDetails: ProductionDetails }>());
export const fetchProductionDetailsGraphFail = createAction('[cxm-analytics / fetch  production detail graph fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Fetch distribution volumes receive graph
export const fetchDistributionVolumeReceiveGraph = createAction('[cxm-analytics / fetch distribution volume graph]', props<{ filterOptionModel: FilterOptionModel, requestedAt: string }>());
export const fetchDistributionVolumeReceiveGraphSuccess = createAction('[cxm-analytics / fetch distribution volume receive graph success]', props<{ distributionVolumeReceive: PreferenceDistributionGraphModel[], message: any }>());
export const fetchDistributionVolumeReceiveGraphFail = createAction('[cxm-analytics / fetch distribution volume receive graph fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

//#region Reporting postal


// Set criteria to store.
export const setPostalCriteriaToStore = createAction('[cxm-analytics / set reporting postal criteria in store]', props<{ filteringCriteria: ReportingPostalParams }>());


//#endregion

// Fetch distribution PND graph
export const fetchDistributionPNGGraph = createAction('[cxm-analytics / fetch distribution PND graph]', props<{ requestedAt: string }>());
export const fetchDistributionPNGGraphSuccess = createAction('[cxm-analytics / fetch distribution PND graph success]', props<{ distributionPND: PreferenceDistributionPNDGraphModel[], messages: any }>());
export const fetchDistributionPNGGraphFail = createAction('[cxm-analytics / fetch distribution PND graph fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Fetch processed mail graph
export const fetchProcessedMailGraph = createAction('[cxm-analytics / fetch processed mail graph]', props<{ requestedAt: string }>());
export const fetchProcessedMailGraphSuccess = createAction('[cxm-analytics / fetch processed mail success]', props<{ processedMail: PreferenceProcessedMailModel[] }>());
export const fetchProcessedMailGraphFail = createAction('[cxm-analytics / fetch processed mail fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

//Fetch distribution by status
export const fetchDistributionByStatus = createAction('[cxm-analytics / fetch distribution by status graph]', props<{ requestedAt: string }>());
export const fetchDistributionByStatusSuccess = createAction('[cxm-analytics / fetch distribution by status success]', props<{ distributionByStatus: DistributionByStatus[], messages: any }>());
export const fetchDistributionByStatusFail = createAction('[cxm-analytics / fetch distribution by status fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

//Export CSV
export const exportCsvFile = createAction('[cxm-analytics / export csv file]', props<{ requestedAt: string }>());
export const exportCsvFileSuccess = createAction('[cxm-analytics / export csv file success]', props<{ object: any }>());
export const exportCsvFileFail = createAction('[cxm-analytics / export csv file fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
