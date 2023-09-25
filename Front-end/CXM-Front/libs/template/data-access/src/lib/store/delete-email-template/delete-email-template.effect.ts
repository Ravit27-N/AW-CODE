import { Injectable } from '@angular/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { TemplateService } from '../../services/template.service';
import {
  deleteEmailTemplate,
  deleteEmailTemplateFail,
  deleteEmailTemplateSuccess,
} from './delete-email-template.action';

@Injectable({
  providedIn: 'root',
})
export class DeleteEmailTemmplateEffect {
  messageProps: any;
  constructor(
    private translate: TranslateService,
    private snackBar: SnackBarService,
    private action$: Actions,
    private emailTemplateService: TemplateService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.get('errorCases.cxmTemplate').subscribe((res) => {
      this.messageProps = res;
    });
  }

  /**
   *Effect for delete email template
   *
   * @memberof DeleteEmailTemmplateEffect
   */
  deleteEmailTemplate$ = createEffect(() =>
    this.action$.pipe(
      ofType(deleteEmailTemplate),
      exhaustMap((arg) =>
        this.emailTemplateService.deleteEmailTemplate(arg.id).pipe(
          map((response) =>
            deleteEmailTemplateSuccess({
              response: response,
              isLoading: false,
              modelName: arg.modelName,
            })
          ),
          catchError((error) =>
            of(deleteEmailTemplateFail({ response: error, isLoading: false }))
          )
        )
      )
    )
  );

  /**
   *Effect for delete email template when success
   *
   * @memberof DeleteEmailTemmplateEffect
   */
  deleteEmailTemplateSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(deleteEmailTemplateSuccess),
        tap((arg) => {
          // show message success
          this.translate
            .get('errorCases.cxmTemplate.emailTemplate.deleteSuccess', {
              modelName: arg.modelName,
            })
            .toPromise()
            .then((msg) => this.snackBar.openCustomSnackbar({ message: msg, type: 'success', icon: 'close' }));
        })
      ),
    { dispatch: false }
  );

  /**
   *Effect for delete email template whenn fail
   *
   * @memberof DeleteEmailTemmplateEffect
   */
  deleteEmailTemplateFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(deleteEmailTemplateFail),
        tap((args) => {
          if (args.response) {
            this.snackBar.openCustomSnackbar({message: `${this.messageProps.deleteFail}${args.response.message}`, type: 'error', icon: 'close'});
          } else {
            this.snackBar.openCustomSnackbar({message: this.messageProps.deleteFail, type: 'error', icon: 'close'});
          }
        })
      ),
    { dispatch: false }
  );
}
