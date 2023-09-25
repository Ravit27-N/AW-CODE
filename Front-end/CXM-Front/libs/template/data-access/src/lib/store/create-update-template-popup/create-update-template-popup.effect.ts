import { Injectable, OnDestroy } from '@angular/core';
import { CreateUpdateTemplatePopupService } from '@cxm-smartflow/template/ui/feature-create-update-template-popup';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as fromTemplateActionPopup from './create-update-template-popup.action';
import { takeUntil, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { GrapeJsUtil } from '@cxm-smartflow/template/util';

@Injectable({ providedIn: 'root' })
export class CreateUpdateTemplatePopupEffect implements OnDestroy {
  destroy$ = new Subject<boolean>();

  constructor(
    private readonly createUpdateTemplatePopupService: CreateUpdateTemplatePopupService,
    private readonly router: Router,
    private readonly action$: Actions,
    private readonly emailTemplateUtil: GrapeJsUtil
  ) {}

  showCreateTemplatePopup$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(fromTemplateActionPopup.showCreateTemplatePopup),
        tap((args) => {
          this.createUpdateTemplatePopupService
            .showCreateTemplatePopup(0, args.modelType)
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
              if (value) {
                GrapeJsUtil.removeGrapeJsProperties().subscribe(() => {
                  if (value.modelType == 'SMS') {
                    this.router.navigate(
                      [
                        `${appRoute.cxmTemplate.template.smsTemplateComposition}`,
                      ],
                      { queryParams: { ...value } }
                    );
                  } else {
                    this.router.navigate(
                      [
                        `${appRoute.cxmTemplate.template.emailTemplateComposition}`,
                      ],
                      { queryParams: { ...value } }
                    );
                  }
                });
              }
            });
        })
      ),
    { dispatch: false }
  );

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
}
