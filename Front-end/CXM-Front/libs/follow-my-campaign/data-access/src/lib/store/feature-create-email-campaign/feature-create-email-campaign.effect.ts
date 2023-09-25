import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { tap } from 'rxjs/operators';
import { FollowMyCampaignService } from '../../services/follow-my-campaign.service';
import {
  loadCreateEmailCampaign,
  loadCreateEmailCampaignFail,
  loadCreateEmailCampaignSuccess,
} from './feature-create-email-campaign.action';

@Injectable({ providedIn: 'root' })
export class FeatureCreateEmailCampaignEffect {
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

  createEmailCampaign$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadCreateEmailCampaign),
        tap((arg) => {
          this.followMyCampaign.addEmailCampaign(arg.emailCampaign).subscribe(
            (response) => {
              if (response) {
                this.router.navigateByUrl(
                  `${this.routeProps.cxmCampaign.followMyCampaign.navigateToCampaignCreateDestination}/${response?.id}`
                );
                this.store.dispatch(
                  loadCreateEmailCampaignSuccess({
                    response: response,
                    isLoading: false,
                  })
                );
              }
            },
            (err) => {
              this.store.dispatch(
                loadCreateEmailCampaignFail({
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

  createEmailCampaignSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadCreateEmailCampaignSuccess),
        tap(() => {
          this.snackBar.openSuccess(this.messageProps?.message?.createSuccess);
        })
      ),
    { dispatch: false }
  );

  createEmailCampaignFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadCreateEmailCampaignFail),
        tap(() => {
          this.snackBar.openWarning(this.messageProps?.message?.createFail);
        })
      ),
    { dispatch: false }
  );
}
