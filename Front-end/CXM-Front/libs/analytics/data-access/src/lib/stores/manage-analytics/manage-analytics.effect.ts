import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Store} from '@ngrx/store';
import * as fromActions$ from './manage-analytics.action';
import {catchError, debounceTime, exhaustMap, map, tap, withLatestFrom} from 'rxjs/operators';
import {ManageAnalyticsService} from '../../services';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from '@cxm-smartflow/shared/data-access/services';
import {selectAllStates} from './manage-analytics.selector';
import {FilterOptionParam, PreferenceProcessedMailModel, ProductionDetails, ReportingPostalParams} from '../../models';
import {forkJoin} from 'rxjs';
import {Router} from '@angular/router';
import {appRoute} from '@cxm-smartflow/shared/data-access/model';
import {DateFormatter, TimeZoneUtil} from '@cxm-smartflow/analytics/util';
import {FileSaverUtil} from "@cxm-smartflow/shared/utils";
import * as fromAction
  from "../../../../../../follow-my-campaign/data-access/src/lib/store/feature-campaign-list/feature-campaign-list.action";


@Injectable({
  providedIn: 'root',
})
export class ManageAnalyticsEffect {

  blankLabel: string;
  constructor(private _action$: Actions, private _store$: Store, private _manageAnalyticsService: ManageAnalyticsService,
              private _router: Router,
              private _translateService: TranslateService, private _snackbarService: SnackBarService,
              private translate: TranslateService,
              private fileSaverUtil: FileSaverUtil) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.get('cxm_analytics.production_detail_table').subscribe(translate => {this.blankLabel = translate?.blank});
  }

  //#region Analytic Criteria
  fetchAnalyticsFilteringCriteria$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchAnalyticsFilteringCriteria),
    exhaustMap(() => {

      const response = forkJoin(
        this._manageAnalyticsService.getFilterCriteria(),
        this._manageAnalyticsService.fetchDistributionCriteria(),
      );

      return response.pipe(
        map(([filterCriteria, distributionCriteria]) => {
          const isPostalEnabled = distributionCriteria.preferences.find(item => item.name === 'Postal' && item.active);
          const isEmailEnabled = distributionCriteria.preferences
            .find(item => item.name === 'Digital' && item.active)?.preferences
            .find(item => item.name === 'Email' && item.active);
          const isSmsEnabled = distributionCriteria.preferences
            .find(item => item.name === 'Digital' && item.active)?.preferences
            .find(item => item.name === 'Sms' && item.active);

          if (!isPostalEnabled && location.pathname.includes(appRoute.cxmAnalytics.navigateToPostal)) {
            this._router.navigateByUrl(appRoute.cxmAnalytics.navigateToGlobal);
          }

          if (!isEmailEnabled && location.pathname.includes(appRoute.cxmAnalytics.navigateToEmail)) {
            this._router.navigateByUrl(appRoute.cxmAnalytics.navigateToGlobal);
          }
          if (!isSmsEnabled && location.pathname.includes(appRoute.cxmAnalytics.navigateToSms)) {
            this._router.navigateByUrl(appRoute.cxmAnalytics.navigateToGlobal);
          }

          return fromActions$.fetchAnalyticsFilteringCriteriaSuccess({ filterCriteria, distributionCriteria });
        }),
        catchError(httpErrorResponse => [fromActions$.fetchAnalyticsFilteringCriteriaFail({ httpErrorResponse })]),
      )
    }),
  ));

  fetchAnalyticsFilteringCriteriaFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchAnalyticsFilteringCriteriaFail),
    tap(() => {
      this._translateService.get('cxm_analytics.fetch_filter_criteria_fail').toPromise().then(message => {
        this._snackbarService.openCustomSnackbar({ message, icon: 'close', type: 'error' });
      });
    })
  ), { dispatch: false });

  fetchClientFillerList$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchClientFillerList),
    exhaustMap(() => this._manageAnalyticsService.getFillerList().pipe(
      map(fillerList => fromActions$.fetchClientFillerListSuccess({ fillerList })),
      catchError(httpErrorResponse => [fromActions$.fetchClientFillerListFail({ httpErrorResponse })]),
    )),
  ));

  fetchClientFillerListFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchClientFillerListFail),
    tap(() => {
      this._translateService.get('cxm_analytics.fetch_filler_list_fail').toPromise().then(message => {
        this._snackbarService.openCustomSnackbar({ message, icon: 'close', type: 'error' });
      });
    }),
  ), { dispatch: false });
  //#endregion

  //#region Reporting global
  fetchProductionProgress$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchProductionProgress),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {
      const startDate = allStates.filterOption.calendar?.startDate || new Date(Date.now() - 6 * 24 * 60 * 60 * 1000);
      const endDate = allStates.filterOption.calendar?.endDate || new Date();

      const params: FilterOptionParam = {
        channels: allStates.filterOption.channels,
        categories: allStates.filterOption.categories,
        fillers: allStates.filterOption.fillers,
        searchByFiller: allStates.filterOption.fillerSearchTerm,
        startDate: new DateFormatter().setDate(startDate).formatToYYYYMMdd(),
        endDate: new DateFormatter().setDate(endDate).formatToYYYYMMdd(),
        requestedAt: args.requestedAt,
      };


      return this._manageAnalyticsService.fetchGlobalProductionProgress(params).pipe(
        map(productionProgress => fromActions$.fetchProductionProgressSuccess({ productionProgress })),
        catchError(httpErrorResponse => [fromActions$.fetchProductionProgressFail({ httpErrorResponse })]),
      );
    })
  ));


  fetchVolumeGraph$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchVolumeGraph),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {

      const startDate = allStates.filterOption.calendar?.startDate || new Date(Date.now() - 6 * 24 * 60 * 60 * 1000);
      const endDate = allStates.filterOption.calendar?.endDate || new Date();

      const params: FilterOptionParam = {
        channels: allStates.filterOption.channels,
        categories: allStates.filterOption.categories,
        fillers: allStates.filterOption.fillers,
        searchByFiller: allStates.filterOption.fillerSearchTerm,
        startDate: new DateFormatter().setDate(startDate).formatToYYYYMMdd(),
        endDate: new DateFormatter().setDate(endDate).formatToYYYYMMdd(),
        requestedAt: args.requestedAt,
      };

      return this._manageAnalyticsService.fetchVolumeReceived(params).pipe(
        map(volumeReceive => fromActions$.fetchVolumeReceiveGraphSuccess({ volumeReceive })),
        catchError(httpErrorResponse => [fromActions$.fetchVolumeReceiveGraphFail({ httpErrorResponse })]),
      );
    })
  ));


  fetchGlobalProductionDetailsGraph$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchGlobalProductionDetailsGraph),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {

      const startDate = allStates.filterOption.calendar?.startDate || new Date(Date.now() - 6 * 24 * 60 * 60 * 1000);
      const endDate = allStates.filterOption.calendar?.endDate || new Date();

      const params: FilterOptionParam = {
        channels: allStates.filterOption.channels,
        categories: allStates.filterOption.categories,
        fillers: allStates.filterOption.fillers,
        searchByFiller: allStates.filterOption.fillerSearchTerm,
        startDate: new DateFormatter().setDate(startDate).formatToYYYYMMdd(),
        endDate: new DateFormatter().setDate(endDate).formatToYYYYMMdd(),
        requestedAt: args.requestedAt,
      };

      return this._manageAnalyticsService.fetchGlobalProductionDetails(params).pipe(
        map(productionDetails => fromActions$.fetchGlobalProductionDetailsGraphSuccess({ productionDetails }),
        catchError(httpErrorResponse => [fromActions$.fetchGlobalProductionDetailsGraphFail({ httpErrorResponse })]),
      ));
    }),
  ));
  //#endregion

  fetchDistributionVolumeGraph$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchDistributionVolumeReceiveGraph),
    debounceTime(400),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {
      const blank$ = this._translateService.get("cxm_analytics");
      const params: ReportingPostalParams = allStates.postalFilteringCriteria;
      return forkJoin(this._manageAnalyticsService.fetchDistributionVolumeReceived(params), blank$).pipe(
        map(([distributionVolumeReceive, message]) => {
          return fromActions$.fetchDistributionVolumeReceiveGraphSuccess({ distributionVolumeReceive, message });
        }),
        catchError(httpErrorResponse => [fromActions$.fetchDistributionVolumeReceiveGraphFail({ httpErrorResponse })]),
      );
    })
  ));

  fetchProductionDetailsGraph$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchProductionDetailsGraph),
    debounceTime(400),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {

      const startDate = allStates.filterOption?.calendar?.startDate || new Date(Date.now() - 6 * 24 * 60 * 60 * 1000);
      const endDate = allStates.filterOption?.calendar?.endDate || new Date();

      const params: ReportingPostalParams = {
        ...allStates.postalFilteringCriteria,
        startDate: new DateFormatter().setDate(startDate).formatToYYYYMMdd(),
        endDate: new DateFormatter().setDate(endDate).formatToYYYYMMdd(),
        requestedAt: args.requestedAt
      };

      return this._manageAnalyticsService.fetchProductionDetails(params).pipe(
        map(postalProductionDetails => fromActions$.fetchProductionDetailsGraphSuccess({ productionDetails: this.transformProductionDetail(postalProductionDetails) })),
        catchError(httpErrorResponse => [fromActions$.fetchProductionDetailsGraphFail({ httpErrorResponse })]));
    })
  ));

  fetchDistributionPNDGraph$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchDistributionPNGGraph),
    debounceTime(400),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {
      const pnd_status$ = this._translateService.get("cxm_analytics.pnd_status");
      const params: ReportingPostalParams = allStates.postalFilteringCriteria;

      return forkJoin(this._manageAnalyticsService.fectDistributionPND(params), pnd_status$).pipe(
        map(([distributionPND, messages]) => {
          return fromActions$.fetchDistributionPNGGraphSuccess({ distributionPND, messages });
        }),
        catchError(httpErrorResponse => [fromActions$.fetchDistributionPNGGraphFail({ httpErrorResponse })]),
      );

    })
  ));

  fetchProcessedMailGraph$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchProcessedMailGraph),
    debounceTime(400),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {
      const pnd_status$ = this._translateService.get("cxm_analytics.none_pnd");
      const params: ReportingPostalParams = allStates.postalFilteringCriteria;

      return forkJoin(this._manageAnalyticsService.fetchProcessedMail(params), pnd_status$).pipe(
        map(([processedMail, message]) => {

          processedMail = Object.entries(processedMail)
            .sort(([keyA], [keyB]) => (keyA === 'Non_PND' ? -1 : keyB === 'Non_PND' ? 1 : 0))
            .map(([key, value]): PreferenceProcessedMailModel => ({
              key: key !== "Non_PND" ? key : message,
              value: Number(value),
            }));
          return fromActions$.fetchProcessedMailGraphSuccess({ processedMail });
        }),
        catchError(httpErrorResponse => [fromActions$.fetchProcessedMailGraphFail({ httpErrorResponse })]),
      );

    })
  ));

  fetchDistributionByStatus$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.fetchDistributionByStatus),
    debounceTime(400),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {
      const pnd_status$ = this._translateService.get("cxm_analytics");
      const params: ReportingPostalParams = allStates.postalFilteringCriteria;

      return forkJoin(this._manageAnalyticsService.fetchDistributionByStatus(params), pnd_status$).pipe(
        map(([distributionByStatus, messages]) => {
          return fromActions$.fetchDistributionByStatusSuccess({distributionByStatus, messages});
        }),
        catchError(httpErrorResponse => [fromActions$.fetchDistributionByStatusFail({httpErrorResponse})]),
      );

    })
  ));

  exportCsvFile$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.exportCsvFile),
    withLatestFrom(this._store$.select(selectAllStates)),
    exhaustMap(([args, allStates]) => {

      const timeZone = TimeZoneUtil.getTimeZone();
      const startDate = allStates.filterOption?.calendar?.startDate || new Date(Date.now() - 6 * 24 * 60 * 60 * 1000);
      const endDate = allStates.filterOption?.calendar?.endDate || new Date();

      if (location.pathname.includes(appRoute.cxmAnalytics.navigateToGlobal)) {
        const params: ReportingPostalParams = {
          ...allStates.postalFilteringCriteria,
          timeZone:timeZone,
          channels: allStates.filterOption.channels,
          categories: allStates.filterOption.categories,
          fillers: allStates.filterOption.fillers,
          searchByFiller: allStates.filterOption.fillerSearchTerm,
          exportingType: "global",
          startDate: new DateFormatter().setDate(startDate).formatToYYYYMMdd(),
          endDate: new DateFormatter().setDate(endDate).formatToYYYYMMdd(),
          requestedAt: args.requestedAt
        };
        return this._manageAnalyticsService.exportCsvFile(params).pipe(
          map(object => {
            return fromActions$.exportCsvFileSuccess({object});
          }),
          catchError(httpErrorResponse => [fromActions$.exportCsvFileFail({httpErrorResponse})]),
        );
      } else {
        const params: ReportingPostalParams = {
          ...allStates.postalFilteringCriteria,
          timeZone:timeZone,
          exportingType: "specific",
          startDate: new DateFormatter().setDate(startDate).formatToYYYYMMdd(),
          endDate: new DateFormatter().setDate(endDate).formatToYYYYMMdd(),
          requestedAt: args.requestedAt
        };
        return this._manageAnalyticsService.exportCsvFile(params).pipe(
          map(object => {
            return fromActions$.exportCsvFileSuccess({object});
          }),
          catchError(httpErrorResponse => [fromActions$.exportCsvFileFail({httpErrorResponse})]),
        );
      }

    })
  ));

  exportCsvFileFail$ = createEffect(() => this._action$.pipe(
      ofType(fromActions$.exportCsvFileFail),
      tap((args) => {
        this._snackbarService.openCustomSnackbar({
          message: 'Export CSV file fail...!',
          icon: 'close',
          type: 'error'
        });
      })
    ),
    {dispatch: false});

  exportCsvFileSuccess$ = createEffect(() => this._action$.pipe(
      ofType(fromActions$.exportCsvFileSuccess),
      tap((args) => {
        this.encodeBlobToUTF8(args?.object?.file)
          .then(encodedText => {
            const dataType = args?.object?.file.type;
            const downloadLink = document.createElement('a');
            downloadLink.href = window.URL.createObjectURL(new Blob(['\uFEFF' + encodedText], {type: dataType + ';charset=utf-8'}));
            downloadLink.setAttribute('download', `${args?.object?.filename}.csv`);
            document.body.appendChild(downloadLink);
            downloadLink.click();
          })
          .catch(error => {
            console.error('Encoding error:', error);
          });
        // this._snackbarService.openCustomSnackbar({
        //   message: "Download success...",
        //   icon: 'close',
        //   type: 'success'
        // });
      })
    ),
    {dispatch: false});

  async encodeBlobToUTF8(blob: Blob): Promise<string> {
    const arrayBuffer = await blob.arrayBuffer();
    return new TextDecoder('utf-8').decode(arrayBuffer);
  };

   getFileNameFromBlob(blob: Blob): string {
    const contentDisposition = blob.type.indexOf('text/csv') !== -1
      ? 'attachment; filename=myfile.csv'
      : null; // Set the desired filename and content type

    if (contentDisposition) {
      const regex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
      const matches = regex.exec(contentDisposition);
      if (matches != null && matches[1]) {
        return matches[1].replace(/['"]/g, '');
      }
    }

    return 'file.csv';
  }

  mappingFiller = (data: any): string => {
    return data?.filler === 'Blank' ? this.blankLabel : data?.filler;
  };

  transformProductionDetail = (productionDetails: ProductionDetails): ProductionDetails => {
    const mappingData = productionDetails?.data?.map(level1 => {
      const level2 = level1?.data?.map((level2: any) => {
        const level3 = level2?.data?.map((level3: any) => {

          return {
            ... level3,
            filler: this.mappingFiller(level3)
          }
        });

        const finalLevel2 = {
          ... level2,
          data: level3
        }
        return {
          ... finalLevel2,
          filler: this.mappingFiller(level2)
        }
      });

      const finalLevel1 = {
          ... level1,
        data: level2
      }

      return {
        ... finalLevel1,
        filler: this.mappingFiller(level1)
      }
    });

    return {
      ... productionDetails,
      data: mappingData
    }
  }
}
