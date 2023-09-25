import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { loadUpdateValidationEmailCampaign } from '@cxm-smartflow/follow-my-campaign/data-access';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { tap } from 'rxjs/operators';
import { FollowMyCampaignService } from '../../services/follow-my-campaign.service';
import {
  loadUpdateEmailCampaign,
  loadUpdateEmailCampaignFail,
  loadUpdateEmailCampaignSuccess,
  loadUpdateCsvEmailCampaign
} from './feature-update-email-campaign.action';

@Injectable({ providedIn: 'root' })
export class FeatureUpdateEmailCampaignEffect {
  routeProps = appRoute;
  messageProps: any;
  constructor(
    private action$: Actions,
    private followMyCampaign: FollowMyCampaignService,
    private snackBar: SnackBarService,
    private translate: TranslateService,
    private router: Router,
    private store: Store
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate
      .get('cxmCampaign.followMyCampaign.generateEmail')
      .subscribe((response) => (this.messageProps = response));
  }

  updateEmailCampaign$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadUpdateEmailCampaign),
        tap((arg) => {
          this.followMyCampaign
            .updateEmailCampaign(arg.emailCampaign)
            .subscribe(
              (response) => {
                if (response) {
                  if(this.router.url.split('/').indexOf('feature-update-setting-parameters-from-list') !== -1){
                    this.router.navigateByUrl(
                      `${this.routeProps.cxmCampaign.followMyCampaign.navigateToUpdateCampaignDestinationFromList}/${response?.id}`
                    );
                  }else{
                    this.router.navigateByUrl(
                      `${this.routeProps.cxmCampaign.followMyCampaign.navigateToUpdateCampaignDestination}/${response?.id}`
                    );
                  }
                  this.store.dispatch(
                    loadUpdateEmailCampaignSuccess({
                      response: response,
                      isLoading: false,
                    })
                  );
                }
              },
              (err) => {
                this.store.dispatch(
                  loadUpdateEmailCampaignFail({
                    error: err,
                    isLoading: false,
                  })
                );
              }
            );
        })
      ),
    { dispatch: false }
  );

  updateCsvEmailCampaign$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadUpdateCsvEmailCampaign),
        tap((arg) => {
          this.followMyCampaign
            .updateEmailCampaign(arg.emailCampaign)
            .subscribe(
              (response) => {
                if (response) {
                  if(this.router.url.split('/').indexOf('update-csv-from-list') !== -1)
                  {
                    this.router.navigateByUrl(
                      `${this.routeProps.cxmCampaign.followMyCampaign.navigateToSummaryCampaignFromList}/${response?.id}`
                    );
                  }else{
                    this.router.navigateByUrl(
                      `${this.routeProps.cxmCampaign.followMyCampaign.navigateToSummaryCampaign}/${response?.id}`
                    );
                  }

                  this.store.dispatch(
                    loadUpdateEmailCampaignSuccess({
                      response: response,
                      isLoading: false,
                    })
                  );
                }
              },
              (err) => {
                this.store.dispatch(
                  loadUpdateEmailCampaignFail({
                    error: err,
                    isLoading: false,
                  })
                );
              }
            );
        })
      ),
    { dispatch: false }
  );

  updateValidationEmailCampaign$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadUpdateValidationEmailCampaign),
        tap((arg) => {
          this.followMyCampaign
            .updateEmailCampaign(arg.emailCampaign)
            .subscribe(
              (response) => {
                if (response) {
                  this.router.navigateByUrl(
                    `${this.routeProps.cxmCampaign.followMyCampaign.navigateToRoot}`
                  );
                  this.store.dispatch(
                    loadUpdateEmailCampaignSuccess({
                      response: response,
                      isLoading: false,
                    })
                  );
                }
              },
              (err) => {
                this.store.dispatch(
                  loadUpdateEmailCampaignFail({
                    error: err,
                    isLoading: false,
                  })
                );
              }
            );
        })
      ),
    { dispatch: false }
  );

  updateEmailCampaignSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadUpdateEmailCampaignSuccess),
        tap(() => {
          this.snackBar.openSuccess(this.messageProps?.message?.updateSuccess);
        })
      ),
    { dispatch: false }
  );

  updateEmailCampaignFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadUpdateEmailCampaignFail),
        tap(() => {
          this.snackBar.openWarning(this.messageProps?.message?.updateFail);
        })
      ),
    { dispatch: false }
  );
}
