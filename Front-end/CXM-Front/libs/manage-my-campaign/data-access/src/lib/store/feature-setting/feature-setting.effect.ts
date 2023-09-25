
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import { ShowComfirmationComponent } from '@cxm-smartflow/manage-my-campaign/ui/show-comfirmation';
import {ShowInformationComponent} from '@cxm-smartflow/shared/ui/show-information';
import { MatDialog } from '@angular/material/dialog';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { ManageMyCampaignService } from '../../services/manage-my-campaign.service';
import {
  createFeaturedSetting,
  createFeaturedSettingSuccess,
  createFeaturedSettingFail,
  clearCreateFeatureSetting,
} from './feature-setting.action';


@Injectable({ providedIn: 'root' })
export class SetFeaturedSettingEffect {
  constructor(
    private manageMyCampaignService: ManageMyCampaignService,
    private action$: Actions,
    public dialog: MatDialog,

  ) {}

  createFeatureSetting$ = createEffect(() =>
    this.action$.pipe(
      ofType(createFeaturedSetting),
      exhaustMap((arg) =>
        this.manageMyCampaignService.create(arg.data).pipe(
          map((response) =>
            createFeaturedSettingSuccess({
              response: response,
              isLoading: false,
            })
          ),
          catchError( () => of(createFeaturedSettingFail({isLoading: false})))
        )
      )
    )
  );

  createFeaturedSettingSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(createFeaturedSettingSuccess),
        tap(() => {
          this.dialog.open(ShowComfirmationComponent, {
            minWidth: '700px',
          });
        })
      ),
    { dispatch: false }
  );

  createFeatureSettingFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(createFeaturedSettingFail),
        tap(() => {
          this.dialog.open(ShowInformationComponent, {
            width: '550px',
            data: {
              title: 'Show Information',
              body: 'Your data send has not success ! Please, try agian ...'
            }
          });
        })
      ),
    { dispatch: false }
  );

  clearCreateFeatureSetting$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(clearCreateFeatureSetting),
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        tap(() => {
        })
      ),
    { dispatch: false }
  );

}
