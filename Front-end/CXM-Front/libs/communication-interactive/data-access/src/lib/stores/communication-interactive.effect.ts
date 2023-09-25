import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  getRemotedUrl,
  getRemotedUrlFail,
  getRemotedUrlSuccess,
  loadCommunicationTemplate,
  loadCommunicationTemplateFail,
  loadCommunicationTemplateSuccess,
  loadCommunicationFilterChanged,
  invalidateRemoteUrl
} from './communication-interactive.action';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { CommunicationInteractiveControlService, CommunicationInteractiveService } from '../services';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Router } from '@angular/router';
import { appRoute } from '@cxm-smartflow/template/data-access';

const validateRemoteUrl = (urlString: string): boolean => {
  const url = new URL(urlString);

  if (url.searchParams.has('id')) {

    try {
      const objString = url.searchParams.get('id');

      if(objString==null) return false;

      const json = JSON.parse(objString);

      if ([400, 401, 403, 500, 503].includes(json.status)) {
        return false;
      }
    } catch (e) {
      // ok
    }

  } else return false;

  return true;
}



@Injectable()
export class CommunicationInteractiveEffect {
  constructor(private readonly action$: Actions,
              private readonly controlService: CommunicationInteractiveControlService,
              private readonly snackbar: SnackBarService,
              private readonly translate: TranslateService,
              private readonly interactiveService: CommunicationInteractiveService,
              private readonly confirmMessageService: ConfirmationMessageService,
              private readonly router: Router
              ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  loadCommunicationTemplate$ = createEffect(() => this.action$.pipe(
    ofType(loadCommunicationTemplate),
    exhaustMap(() => this.interactiveService.getAll().pipe(
      map(communicationResponse => loadCommunicationTemplateSuccess({ communicationResponse })),
      catchError(() => [loadCommunicationTemplateFail()]))
    )));

  loadCommunicationTemplateFail$ = createEffect(() => this.action$.pipe(
    ofType(loadCommunicationTemplateFail),
    tap(() => {
      this.translate.get('communicationInteractive.errorMessage.failToFetchModel').toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ type: 'error', icon: 'close', message: message });
      });
    })
  ), { dispatch: false });

  getRemotedUrl$ = createEffect(() => this.action$.pipe(
    ofType(getRemotedUrl),
    exhaustMap(({ id }) => this.interactiveService.getRemotedUrl(id).pipe(
      map(url => {
        const isValidUrl = validateRemoteUrl(url);

        if(!isValidUrl) {
          return invalidateRemoteUrl();
        }

        return getRemotedUrlSuccess({ url });
      }),
      catchError((err) => [getRemotedUrlFail({ httpError: err })])
    ))
  ));

  getRemotedUrlFail$ = createEffect(() => this.action$.pipe(
    ofType(getRemotedUrlFail),
    tap((args) => {


      const { apierrorhandler } = args.httpError.error;
      if(apierrorhandler) {
        if (apierrorhandler.statusCode === 403) {

          this.translate
          .get('template.message')
          .toPromise().then((messageProps) => {
            this.confirmMessageService.showConfirmationPopup(
              {
                icon: 'close',
                title:  messageProps.unauthorize,
                message:messageProps.unauthorizeAccess,
                cancelButton: messageProps.unauthorizeCancel,
                confirmButton: messageProps.unauthorizeLeave,
                type: 'Warning'
              }
            ).subscribe(() => this.router.navigateByUrl(appRoute.cxmCommunicationInteractive.navigateToChooseModel));
          })

        } else {
          this.translate.get('communicationInteractive.errorMessage.failToGetRemotedUrl').toPromise().then(message => {
            this.snackbar.openCustomSnackbar({ type: 'error', icon: 'close', message: message });
          });
          this.router.navigateByUrl(appRoute.cxmCommunicationInteractive.navigateToChooseModel);
        }
      }

    })
  ), { dispatch: false });


    // response 404 url
  invalidateRemoteUrlEffect$ = createEffect(() => this.action$.pipe(
    ofType(invalidateRemoteUrl),
    tap(() => {
      this.translate
      .get('template.message')
      .toPromise().then((messageProps) => {
        this.confirmMessageService.showConfirmationPopup(
          {
            icon: 'close',
            title:  messageProps.unauthorize,
            message:messageProps.unauthorizeAccess,
            cancelButton: messageProps.unauthorizeCancel,
            confirmButton: messageProps.unauthorizeLeave,
            type: 'Warning'
          }
        ).subscribe(() => this.router.navigateByUrl(appRoute.cxmCommunicationInteractive.navigateToChooseModel));
      })
    })
  ),  { dispatch: false })

  filterChanged$ = createEffect(() => this.action$.pipe(
    ofType(loadCommunicationFilterChanged),
    exhaustMap((args) => this.interactiveService.getAll(args.filters).pipe(
      map(communicationResponse => loadCommunicationTemplateSuccess({ communicationResponse })),
      catchError(() => [loadCommunicationTemplateFail()])))
  ))
}
