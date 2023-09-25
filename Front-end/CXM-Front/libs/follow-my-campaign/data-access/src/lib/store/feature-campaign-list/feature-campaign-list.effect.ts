import {Injectable} from '@angular/core';
import {FollowMyCampaignService} from '../../services/follow-my-campaign.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {catchError, exhaustMap, map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import * as fromAction from './feature-campaign-list.action';
import {
  cancelCampaignFail,
  cancelCampaignSuccess,
  loadFeatureCampaignList,
  loadFeatureCampaignListFail,
  loadFeatureCampaignListSuccess
} from './feature-campaign-list.action';
import {of} from 'rxjs';
import {unloadEmailCampaignFormData} from '../feature-campaign-email';
import {
  CanAccessibilityService,
  CanModificationService,
  CanVisibilityService,
  SnackBarService
} from '@cxm-smartflow/shared/data-access/services';
import {TranslateService} from '@ngx-translate/core';
import {Router} from '@angular/router';
import {
  appRoute,
  CampaignConstant,
  EntityResponseHandler,
  PrivilegeModel
} from '@cxm-smartflow/shared/data-access/model';
import {unloadCampaignSms} from '../feature-campaign-sms';
import {selectCampaignFilterList} from './feature-campaign-list.selector';
import {Store} from '@ngrx/store';
import {CampaignModel, CampaignType} from '../../models';

@Injectable({ providedIn: 'root' })
export class FeatureCampaignListEffect {

  cancelPropLabel: any;
  routeProps = appRoute;
  downloadCsvPropLabel: any;
  fetchListCampaignMessage: any;

  // Privileges.
  campaignPrivilege = CampaignConstant;

  constructor(
    private actions$: Actions,
    private followMyCampaignService: FollowMyCampaignService,
    private snackBarService: SnackBarService,
    private translate: TranslateService,
    private router: Router,
    private canVisibilityService: CanVisibilityService,
    private canAccessibilityService: CanAccessibilityService,
    private canModifyService: CanModificationService,
    private store: Store
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.get('cxmCampaign.followMyCampaign.cancelEmailCampaignPopUp')
      .subscribe((response) => {
        this.cancelPropLabel = response;
      });

    this.translate.get('cxmCampaign.followMyCampaign.list.downloadCsv.message')
      .subscribe(response => this.downloadCsvPropLabel = response);

    this.translate.get('cxmCampaign.followMyCampaign.list.message')
      .subscribe(response => this.fetchListCampaignMessage = response);
  }

  loadFeatureCampaignList$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadFeatureCampaignList),
      exhaustMap((arg) => this.followMyCampaignService.getAllEmailCampaign(
        arg.page, arg.pageSize, arg.sortByField, arg.sortDirection, arg.filter, arg._type, arg.mode
      )
        .pipe(
          map((response: EntityResponseHandler<CampaignModel>) => loadFeatureCampaignListSuccess({
            response: this.transformCampaignListData(response),
            isLoading: false
          })),
          catchError((error: any) => of(loadFeatureCampaignListFail({
            error: error,
            isLoading: false
          })))
        ))
    )
  );

  transformCampaignListData = (response: EntityResponseHandler<CampaignModel>): EntityResponseHandler<CampaignModel> => {
    const finalContents = response.contents?.map((campaign: CampaignModel) => {
      return {
        ...campaign,
        privilege: this.getPrivilege(campaign)
      };
    });

    return {
      contents: finalContents,
      page: response.page,
      pageSize: response.pageSize,
      total: response.total
    };
  };

  getPrivilege = (campaign: CampaignModel): PrivilegeModel => {
    return {
      canModify: this.canModify(campaign),
      canCancel: this.canCancel(campaign)
    };
  };

  canModify = (campaign: CampaignModel): boolean => {
    let module = this.campaignPrivilege.CXM_CAMPAIGN;
    let feature = this.campaignPrivilege.FINALIZE;

    if (campaign.type.toUpperCase() === CampaignType.SMS) {
      module = this.campaignPrivilege.CXM_CAMPAIGN_SMS;
      feature = this.campaignPrivilege.FINALIZE_SMS;
    }

    return this.canModifyService.hasModify(module, feature, campaign.ownerId || 0, true);
  };

  canCancel = (campaign: CampaignModel): boolean => {
    let module = this.campaignPrivilege.CXM_CAMPAIGN;
    let feature = this.campaignPrivilege.CANCEL;

    if (campaign.type.toUpperCase() === CampaignType.SMS) {
      module = this.campaignPrivilege.CXM_CAMPAIGN_SMS;
      feature = this.campaignPrivilege.CANCEL_SMS;
    }

    return this.canModifyService.hasModify(module, feature, campaign.ownerId || 0, true);
  };

  loadFeatureCampaignListSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadFeatureCampaignListSuccess),
      switchMap(() => [unloadEmailCampaignFormData(), unloadCampaignSms()])
    )
  );

  loadFeatureCampaignListFail$ = createEffect(() =>
      this.actions$.pipe(
        ofType(loadFeatureCampaignListFail),
        tap(() => {
          this.snackBarService.openCustomSnackbar({
            message: this.fetchListCampaignMessage?.failToFetchData,
            type: 'error',
            icon: 'close'
          });
        })
      ),
    { dispatch: false }
  );

  cancelCampaign$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.cancelCampaign),
    switchMap(args => this.followMyCampaignService.changeEmailCampaignStatus(args.id, args.status).pipe(
      map(() => cancelCampaignSuccess()),
      catchError(() => of(cancelCampaignFail()))
    ))
  ));

  cancelCampaignSuccess$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.cancelCampaignSuccess),
    switchMap(() => {
      this.snackBarService.openCustomSnackbar({
        message: this.cancelPropLabel?.successMessage,
        icon: 'close',
        type: 'success'
      });
      return [fromAction.doLoadCampaignList()];
    }))
  );

  doLoadCampaignList$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.doLoadCampaignList),
    withLatestFrom(this.store.select(selectCampaignFilterList)),
    switchMap(([args, campaignListFilter]) =>
      [fromAction.loadFeatureCampaignList({
        page: campaignListFilter.page,
        pageSize: campaignListFilter.pageSize,
        sortByField: campaignListFilter?.sortByField,
        sortDirection: campaignListFilter?.sortDirection,
        filter: campaignListFilter?.filter,
        mode: campaignListFilter?.mode,
        _type: campaignListFilter?._type
      })]
    )
  ));

  cancelCampaignFail$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.cancelCampaignFail),
      tap(() => {
        this.snackBarService.openCustomSnackbar({
          message: this.cancelPropLabel?.errorMessage,
          icon: 'close',
          type: 'error'
        });
      })),
    { dispatch: false }
  );

  gotoEmailDetail$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.gotoEmailCampaignDetail),
      tap((args) => {
        if (this.canVisibilityService.hasVisibility(CampaignConstant.CXM_CAMPAIGN, CampaignConstant.SELECT_AND_VIEW, args.ownerId)) {
          this.router.navigate([this.routeProps.cxmCampaign.followMyCampaign.navigateToCampaignDetail, args.id]);
        }
      })
    ),
    { dispatch: false }
  );

  gotoSmsDetail$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.gotoSmsCampaignDetail),
      tap((args) => {
        if (this.canVisibilityService.hasVisibility(CampaignConstant.CXM_CAMPAIGN_SMS, CampaignConstant.SELECT_AND_VIEW_SMS, args.ownerId)) {
          this.router.navigate([this.routeProps.cxmCampaign.followMyCampaign.navigateToCampaignSMSDetail, args.id]);
        }
      })
    ),
    { dispatch: false }
  );

  gotoUpdateEmail$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.gotoUpdateEmail),
      tap((args) => {
        this.store.dispatch(fromAction.getEmailCampaignDetail({ id: args.id }));
      })
    ),
    { dispatch: false }
  );

  getEmailCampaign$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.getEmailCampaignDetail),
    exhaustMap(args => this.followMyCampaignService.getEmailCampaignById(args.id).pipe(
      map(campaign => fromAction.getEmailCampaignDetailSuccess({ campaign }))
    ))
  ));

  getEmailCampaignDetailSuccess$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.getEmailCampaignDetailSuccess),
    tap(({ campaign }) => {
      const isValidCSVRecord = (campaign?.details?.csvRecordCount || 0) - (campaign?.details?.errorCount || 0) > 0;
      if (isValidCSVRecord) {
        this.router.navigateByUrl(
          `${this.routeProps.cxmCampaign.followMyCampaign.navigateToUpdateEmailingParameter}/${campaign.id}`
        );
      } else {
        this.router.navigate([this.routeProps.cxmCampaign.followMyCampaign.emailCampaignDestination], {
          queryParams: { templateId: campaign.templateId, id: campaign.id }
        });
      }
    })
  ), { dispatch: false });

  gotoUpdateSms$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.gotoUpdateSms),
      tap((args) => {
        this.store.dispatch(fromAction.getSMSCampaignDetail({ id: args.id }));
      })
    ),
    { dispatch: false }
  );

  getSMSCampaignDetail$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.getSMSCampaignDetail),
    exhaustMap(args => this.followMyCampaignService.getEmailCampaignById(args.id).pipe(
      map(campaign => fromAction.getSMSCampaignDetailSuccess({ campaign }))
    ))
  ));

  getSMSCampaignDetailSuccess$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.getSMSCampaignDetailSuccess),
    tap(({ campaign }) => {
      const isValidCSVRecord = (campaign?.details?.csvRecordCount || 0) - (campaign?.details?.errorCount || 0) > 0;
      if (isValidCSVRecord) {
        this.router.navigateByUrl(
          `${this.routeProps.cxmCampaign.followMyCampaign.smsCampaignParameter}/${campaign.id}`
        );
      } else {
        this.router.navigate([this.routeProps.cxmCampaign.followMyCampaign.smsCampaignDestination], {
          queryParams: { templateId: campaign.templateId, id: campaign.id }
        });
      }
    })
  ), { dispatch: false });

  gotoCreateCampaign$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.gotoCreateCampaign),
      tap(() => {
        this.router.navigateByUrl(appRoute.cxmCampaign.followMyCampaign.campaign);
      })),
    { dispatch: false });

  downloadCsvFile$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction.downloadCsvFile),
    switchMap((args) => this.followMyCampaignService.downloadCsvFile(<number>args.campaign?.id).pipe(
      map((response) => fromAction.downloadCsvFileSuccess({ file: response, campaign: args?.campaign })),
      catchError(() => of(fromAction.downloadCsvFileFail()))
    ))
  ));

  downloadCsvFileSuccess$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.downloadCsvFileSuccess),
      tap((args) => {
        this.encodeBlobToUTF8(args?.file)
          .then(encodedText => {
            const dataType = args?.file?.type;
            const downloadLink = document.createElement('a');
            downloadLink.href = window.URL.createObjectURL(new Blob(['\uFEFF' + encodedText], {type: dataType + ';charset=utf-8'}));
            downloadLink.setAttribute('download', `${args?.campaign?.campaignName}.csv`);
            document.body.appendChild(downloadLink);
            downloadLink.click();
            // Use the UTF-8 encoded text as needed
          })
          .catch(error => {
            console.error('Encoding error:', error);
          });
        // download.
        // message.
        this.snackBarService.openCustomSnackbar({
          message: this.downloadCsvPropLabel?.success,
          icon: 'close',
          type: 'success'
        });
      })
    ),
    {dispatch: false});

  downloadCsvFileFail$ = createEffect(() => this.actions$.pipe(
      ofType(fromAction.downloadCsvFileFail),
      tap((args) => {
        this.snackBarService.openCustomSnackbar({
          message: this.downloadCsvPropLabel?.fail,
          icon: 'close',
          type: 'error'
        });
      })
    ),
    { dispatch: false });

  async encodeBlobToUTF8(blob: Blob): Promise<string> {
    const arrayBuffer = await blob.arrayBuffer();
    return new TextDecoder('utf-8').decode(arrayBuffer);
  }
}


