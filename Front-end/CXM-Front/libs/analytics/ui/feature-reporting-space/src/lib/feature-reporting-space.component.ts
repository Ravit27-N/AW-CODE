import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  exportCsvFile,
  fetchAnalyticsFilteringCriteria,
  fetchClientFillerList,
  fetchGlobalProductionDetailsGraph,
  fetchProductionProgress,
  fetchVolumeGraph,
  fetchVolumeReceiveGraph,
  FilterCriteriaModel,
  FilterListModel,
  FilterOptionModel,
  GlobalProductionDetailsModel,
  selectAnalyticsDisabledCategories,
  selectAnalyticsDisabledChannels,
  selectAnalyticsReportTabs,
  selectFillers,
  selectFilterCriteria,
  selectGlobalProductionDetail,
  selectProductionProgresses,
  selectRefreshDate,
  selectVolumeReceive,
  VolumeReceiveGraph
} from '@cxm-smartflow/analytics/data-access';
import {Observable} from 'rxjs';
import {filter} from 'rxjs/operators';
import {DateFormatter} from '@cxm-smartflow/analytics/util';
import {filterHistoryManager} from '@cxm-smartflow/analytics/ui/report-space-filtering';
import {DateRequest} from "../../../../util/src/lib/date-request";

@Component({
  selector: 'cxm-smartflow-feature-reporting-space',
  templateUrl: './feature-reporting-space.component.html',
  styleUrls: ['./feature-reporting-space.component.scss'],
})
export class FeatureReportingSpaceComponent implements OnInit {
  filteringConfig$: Observable<FilterCriteriaModel>;
  fillers$: Observable<FilterListModel[]>;
  analyticsReportTabs$: Observable<any>;
  analyticsDisabledChannel$: Observable<any>;
  analyticsDisabledCategory$: Observable<any>;

  refreshDate$: Observable<Date>;
  volumeReceiveGraph$: Observable<VolumeReceiveGraph>;
  globalProductionDetails$: Observable<GlobalProductionDetailsModel>;
  productionProgress$: Observable<any>;

  constructor(private _store$: Store) {
  }

  private static getDefaultDateRange() {
    return {
      startDate: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000),
      endDate: new Date(),
    };
  }

  ngOnInit(): void {
    this._setupCriteria();
    this._store$.dispatch(fetchVolumeReceiveGraph());
    this.initGlobalProductionDetail();
  }

  private initGlobalProductionDetail(): void {


    const date = new Date();
    const formatter = new DateFormatter();


    const requestedAt = formatter
      .setYear(date.getFullYear())
      .setMonth(date.getMonth() + 1)
      .setDay(date.getDate())
      .setHours(date.getHours())
      .setMinutes(date.getMinutes())
      .setSeconds(date.getSeconds())
      .formatDate();

    const restore = filterHistoryManager.shouldRestoreFilterHistory('global');

    this._store$.dispatch(fetchVolumeGraph({
      filterOptionModel: {
        channels: restore?.channels || [],
        categories: restore?.categories || [],
        standAloneCategory: [],
        calendar: {
          startDate: restore?.calendar?.startDate || new Date(Date.now() - 6 * 24 * 60 * 60 * 1000),
          endDate: restore?.calendar?.endDate || new Date(),
          option: restore?.calendar?.option || 3,
        },
        fillers: restore?.fillers || [],
        fillerSearchTerm: restore?.fillerSearchTerm || '',
        fillersGroup: [],
      }, requestedAt
    }));

    this._store$.dispatch(fetchProductionProgress({requestedAt}));
    this._store$.dispatch(fetchGlobalProductionDetailsGraph({requestedAt}));
  }

  private _setupCriteria(): void {
    this._store$.dispatch(fetchAnalyticsFilteringCriteria());
    this._store$.dispatch(fetchClientFillerList());
    this.filteringConfig$ = this._store$.select(selectFilterCriteria);
    this.fillers$ = this._store$.select(selectFillers);
    this.analyticsReportTabs$ = this._store$.select(selectAnalyticsReportTabs);
    this.analyticsDisabledChannel$ = this._store$.select(selectAnalyticsDisabledChannels);
    this.analyticsDisabledCategory$ = this._store$.select(selectAnalyticsDisabledCategories);
    this.refreshDate$ = this._store$.select(selectRefreshDate);
    this.volumeReceiveGraph$ = this._store$
      .select(selectVolumeReceive)
      .pipe(filter((value) => value !== null));
    this.globalProductionDetails$ = this._store$
      .select(selectGlobalProductionDetail)
      .pipe(filter((value) => value !== null));
    this.volumeReceiveGraph$ = this._store$.select(selectVolumeReceive);
    this.productionProgress$ = this._store$.select(selectProductionProgresses);
  }

  onExportReport() {
    const requestedAt = DateRequest.getRequestedAt();
    this._store$.dispatch(exportCsvFile({requestedAt: requestedAt}));
  }

  getCriteriaOptionChange(filterOptionModel: FilterOptionModel) {
    const requestedAt = DateRequest.getRequestedAt();

    this._store$.dispatch(fetchVolumeGraph({filterOptionModel, requestedAt}));
    this._store$.dispatch(fetchProductionProgress({requestedAt}));
    this._store$.dispatch(fetchGlobalProductionDetailsGraph({requestedAt}));
  }


}
