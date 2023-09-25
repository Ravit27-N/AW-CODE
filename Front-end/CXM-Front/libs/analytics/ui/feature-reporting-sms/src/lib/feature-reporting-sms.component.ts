import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {
  exportCsvFile,
  fetchAnalyticsFilteringCriteria,
  fetchClientFillerList,
  fetchDistributionByStatus,
  fetchDistributionVolumeReceiveGraph,
  fetchProductionDetailsGraph,
  FilterCriteriaModel,
  FilterListModel,
  FilterOptionModel,
  ProductionDetails,
  selectAnalyticsReportTabs,
  selectDistributionByStatus,
  selectDistributionByStatusLoading,
  selectDistributionVolumeReceive,
  selectFillers,
  selectFilterCriteria,
  selectProcessedMail,
  selectProcessedMailLoading,
  selectProductionDetail,
  selectRefreshDate,
  setPostalCriteriaToStore
} from "@cxm-smartflow/analytics/data-access";
import {Store} from "@ngrx/store";
import {DateFormatter, DateRequest, filterHistoryManager} from "@cxm-smartflow/analytics/util";
import {filter, map} from "rxjs/operators";

@Component({
  selector: 'cxm-smartflow-feature-reporting-sms',
  templateUrl:  './feature-reporting-sms.component.html',
  styleUrls: ['./feature-reporting-sms.component.scss']
})
export class FeatureReportingSmsComponent implements OnInit {

  filteringConfig$: Observable<FilterCriteriaModel>;
  fillers$: Observable<FilterListModel[]>;
  refreshDate$: Observable<any>;
  analyticsReportTabs$: Observable<any>;

  productionDetails$: Observable<ProductionDetails>;

  constructor(
    private _store$: Store
  ) {}

  distribution_volume_receive$: Observable<any>;

  distribution_PND$: Observable<any>;

  distributionLoading$: Observable<any>;


  processedMail$: Observable<any>;

  processedMailLoading$: Observable<any>;

  ngOnInit(): void {
    this._setupCriteria();
  }

  private _setupCriteria(): void {
    this._store$.dispatch(fetchAnalyticsFilteringCriteria());
    this._store$.dispatch(fetchClientFillerList());
    this.filteringConfig$ = this._store$.select(selectFilterCriteria);
    this.fillers$ = this._store$.select(selectFillers);
    this.analyticsReportTabs$ = this._store$.select(selectAnalyticsReportTabs);

    this.distribution_volume_receive$ = this._store$
      .select(selectDistributionVolumeReceive)
      .pipe(filter((value) => value !== null));
    this.distribution_volume_receive$ = this._store$.select(selectDistributionVolumeReceive);

    this.distribution_PND$ = this._store$.select(selectDistributionByStatus).pipe(map((value) => {
      if (!value.series.length) {
        value = {
          series: [100],
          events: [],
          chart: {
            width: 380,
            type: 'pie',
          },
          stroke: {
            width: 0,
          },
          colors: ['#ececec'],
          labels: [],
          dataLabels: {
            enabled: false, // Disable data labels
          },
          legend: {
            show: false,
          },
          plotOptions: {
            pie: {
              events: {},
            },
          },
          tooltip: {
            enabled: false,
          },
          responsive: [
            {
              breakpoint: 2298,
              options: {
                chart: {
                  width: 230,
                  height: 230,
                },
              },
            },
            {
              breakpoint: 4000,
              options: {
                chart: {
                  width: 250,
                  height: 250,
                },
              },
            },
          ],
          isEmpty: true,
          isHidden: value?.isHidden || false
        };
      } else {
        value = {
          ...value, responsive: [
            {
              breakpoint: 2298,
              options: {
                chart: {
                  width: 230,
                  height: 230,
                },
                dataLabels: {
                  enabled: true,
                  style: {
                    fontSize: '13px',
                  },
                },
              },
            },
            {
              breakpoint: 4000,
              options: {
                chart: {
                  width: 250,
                  height: 250,
                },
                dataLabels: {
                  enabled: true,
                  style: {
                    fontSize: '14px',
                  },
                },
              },
            },
          ],
          dataLabels: {
            enabled: true,
            style: {
              fontWeight: '400',
              colors: ['white'],
              fontFamily: 'Rubik',
            },
            formatter: function (val: any) {
              return `${val.toFixed(2)}%`;
            },
          },
          isHidden: value?.isHidden || false
        };
      }
      return value;
    }));
    this.distributionLoading$ = this._store$.select(selectDistributionByStatusLoading);

    const payload: any = filterHistoryManager.shouldRestoreFilterHistory('SMS');
    this.getCriteriaOptionChange(payload);
    this.refreshDate$ = this._store$.select(selectRefreshDate);

    this.productionDetails$ = this._store$
      .select(selectProductionDetail)
      .pipe(filter((value) => value !== null));

    this.processedMailLoading$= this._store$.select(selectProcessedMailLoading);

    this.processedMail$ = this._store$.select(selectProcessedMail);

  }
  getCriteriaOptionChange(filterOptionModel: FilterOptionModel) {

    const requestedAt = DateRequest.getRequestedAt();
    this._store$.dispatch(setPostalCriteriaToStore({ filteringCriteria: {
        channels: ['Digital'],
        categories: ['SMS'],
        startDate:  new DateFormatter().setDate(filterOptionModel?.calendar?.startDate || new Date(Date.now() - 6 * 24 * 60 * 60 * 1000)).formatToYYYYMMdd(),
        endDate: new DateFormatter().setDate(filterOptionModel?.calendar?.endDate || new Date()).formatToYYYYMMdd(),
        fillers: filterOptionModel?.fillersGroup?.[0]?.fillerSelectedItem || '',
        secondFillerKey: filterOptionModel?.fillersGroup?.[1]?.fillerSelectedItem || '',
        thirdFillerKey: filterOptionModel?.fillersGroup?.[2]?.fillerSelectedItem || '',
        searchByFiller: filterOptionModel?.fillersGroup?.[0]?.fillerSearchTerms || '',
        secondFillerText: filterOptionModel?.fillersGroup?.[1]?.fillerSearchTerms || '',
        thirdFillerText: filterOptionModel?.fillersGroup?.[2]?.fillerSearchTerms || '',
        requestedAt,
      }}));

    this._store$.dispatch(fetchDistributionVolumeReceiveGraph({ filterOptionModel, requestedAt }));
    this._store$.dispatch(fetchProductionDetailsGraph({requestedAt: requestedAt}));
    this._store$.dispatch(fetchDistributionByStatus({requestedAt: requestedAt}));
  }

  onExportReport() {
    const requestedAt = DateRequest.getRequestedAt();
    this._store$.dispatch(exportCsvFile({requestedAt: requestedAt}));
  }


}
