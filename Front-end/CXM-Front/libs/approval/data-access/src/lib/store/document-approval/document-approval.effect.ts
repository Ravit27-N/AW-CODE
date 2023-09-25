import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {catchError, exhaustMap, map, switchMap, tap, withLatestFrom,} from 'rxjs/operators';
import * as fromActions from './document-approval.action';
import {ApprovalService} from '../../services/approval.service';
import {of} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from '@cxm-smartflow/shared/data-access/services';
import {ApprovalControlService} from '@cxm-smartflow/approval/data-access';
import {Store} from '@ngrx/store';
import * as fromSelector from './document-approval.selector';
import {ConfirmationMessageService} from '@cxm-smartflow/shared/ui/comfirmation-message';
import {ActivatedRoute, Router} from '@angular/router';
import {appRoute} from '@cxm-smartflow/template/data-access';
import {StatusUtils} from '../../models';
import {FileSaverUtil} from '@cxm-smartflow/shared/utils';

@Injectable()
export class DocumentApprovalEffect {
  loadDocumentLsitByIdEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.loadApprovalDocumentList),
      exhaustMap((args) => {
        const { id, filters } = args;

        return this.service.getDocumentByFlowId(id, filters).pipe(
          map((response) => {
            let { contents } = response;
            contents = contents.map((x) =>
              Object.assign(x, {
                _editable: this.approvalService.checkIsCanValidateOrRefuse(
                  x.ownerId
                ),
              })
            );

            if (contents.length == 0 && args.redirect === true) {
              // should redirect flow has no document after validate
              return fromActions.loadApprovalDocumentListSuccess({
                response: { ...response, contents },
                redirectOnEmpty: true,
              });
            }

            return fromActions.loadApprovalDocumentListSuccess({
              response: { ...response, contents },
            });
          }),
          catchError((httpError) => [
            fromActions.loadApprovalDocumentListFail({ httpError }),
          ])
        );
      })
    )
  );

  submitValidateApprovalDocEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.submitApproveDoc),
      exhaustMap((args) => {
        const { docs } = args;
        return this.service
          .updateValidateDoc(
            docs.map((x) => x.fileId),
            StatusUtils.VALIDATED,
            args.flowId,
            args.comment
          )
          .pipe(
            map((response) =>
              fromActions.submitValidationSuccess({
                message: 'espace.selection.validate_success',
                response,
                flowId: args.flowId,
              })
            ),
            catchError((err) =>
              of(fromActions.submitValidationFail({ httpError: err }))
            )
          );
      })
    )
  );

  submitRefuseApprovalDocEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.submitRefuseDoc),
      exhaustMap((args) => {
        const { docs } = args;
        return this.service
          .updateValidateDoc(
            docs.map((x) => x.fileId),
            StatusUtils.REFUSE_DOC,
            args.flowId,
            args.comment
          )
          .pipe(
            map((response) =>
              fromActions.submitValidationSuccess({
                message: 'espace.selection.validate_fail',
                response,
                flowId: args.flowId,
              })
            ),
            catchError((err) =>
              of(fromActions.submitValidationFail({ httpError: err }))
            )
          );
      })
    )
  );

  submitValidationSuccessEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.submitValidationSuccess),
        withLatestFrom(this.store.select(fromSelector.selectApprovalDocFilter)),
        tap(([args, filter]) => {
          this.store.dispatch(
            fromActions.loadApprovalDocumentList({
              id: args.flowId,
              filters: filter,
              redirect: true,
            })
          );

          this.translate
            .get(args.message)
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message: message,
              });
            });
        })
        // switchMap(([args, filters]) => [fromActions.filterFlowApproveChanged({ filters: filters,  })])
      ),
    { dispatch: false }
  );

  submitValidationFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.submitValidationFail),
        tap(({ httpError }) => {
          const key = (httpError?.error?.apierrorhandler?.statusCode === 4003)?
            'espace.selection.not_yet_configure_unloading' : 'espace.selection.validate_error';
          this.translate
            .get(key)
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message: message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  loadApprovalDocumentListFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.loadApprovalDocumentListFail),
        tap((args) => {
          const { apierrorhandler } = args.httpError.error;
          if (apierrorhandler) {
            if ([403, 404, 401].includes(apierrorhandler.statusCode)) {
              this.translate
                .get('template.message')
                .toPromise()
                .then((messageProps) => {
                  this.confirmMessageService
                    .showConfirmationPopup({
                      icon: 'close',
                      title: messageProps.unauthorize,
                      message: messageProps.unauthorizeAccess,
                      cancelButton: messageProps.unauthorizeCancel,
                      confirmButton: messageProps.unauthorizeLeave,
                      type: 'Warning',
                    })
                    .subscribe(() =>
                      this.router.navigateByUrl(
                        appRoute.cxmApproval.navigateToValidateFlow
                      )
                    );
                });
            } else {
              this.translate
                .get('espace.errorMessage.failtoFetchList')
                .toPromise()
                .then((message) => {
                  this.snackbar.openCustomSnackbar({
                    type: 'error',
                    icon: 'close',
                    message: message,
                  });
                });
              this.router.navigateByUrl(
                appRoute.cxmApproval.navigateToValidateFlow
              );
            }
          }
        })
      ),
    { dispatch: false }
  );

  loadApprovalDocumentListSuccessEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.loadApprovalDocumentListSuccess),
        tap((args) => {
          if (args.redirectOnEmpty) {
            this.router.navigateByUrl(
              appRoute.cxmApproval.navigateToValidateFlow
            );
          }
        })
      ),
    { dispatch: false }
  );

  filterchangedEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.filterFlowApproveChanged),
      switchMap((args) => [
        fromActions.loadApprovalDocumentList({
          filters: args.filters,
          id: args.id,
        }),
      ])
    )
  );

  downloadFile$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.downloadFile),
        tap((args) => {
          this.service.downloadBase64(args.fileId).subscribe(
            (base64: string) => {
              this.base64Converter.downloadBase64(base64, args.filename);
            },
            () => {
              this.translate
                .get('espace.listFlowDocument.table.message')
                .toPromise()
                .then((message) => {
                  this.snackbar.openCustomSnackbar({
                    message: message?.downloadFileFail,
                    type: 'error',
                    icon: 'close',
                  });
                });
            }
          );
        })
      ),
    { dispatch: false }
  );

  constructor(
    private actions: Actions,
    private service: ApprovalService,
    private translate: TranslateService,
    private snackbar: SnackBarService,
    private approvalService: ApprovalControlService,
    private store: Store,
    private confirmMessageService: ConfirmationMessageService,
    private router: Router,
    private readonly activateRoute: ActivatedRoute,
    private base64Converter: FileSaverUtil
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }
}
