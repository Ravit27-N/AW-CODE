import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { FlowDepositService } from '../../services/flow-deposit.service';
import { catchError, exhaustMap, map, switchMap, take, tap } from 'rxjs/operators';
import * as actions from './flow-deposit.action';
import {clearDepositFlow, defaultBase64File, missingSignatureConfiguration} from './flow-deposit.action';
import { interval, of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { checkIsDocumentCannotIdentify, unloadUploadFileAction } from '../file-upload-feature';
import { goBackToAnalysisResult, goToFinishedPage } from '../flow-tab-menu';
import { ProfileService } from '@cxm-smartflow/profile/data-access';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { appRoute } from '@cxm-smartflow/template/data-access';

@Injectable({ providedIn: 'root' })
export class FlowDepositEffect {

  propertiesLabel: any;

  launchProcessControlStep = createEffect(() =>
    this.action$.pipe(
      ofType(actions.launchProcessControl),
      switchMap((args) => {
          this.flowDepositService.loadFileMetaData(args.request.fileId, args.funcKey, args.privKey).subscribe(response => {
            this.store.dispatch(defaultBase64File({ base64: response.content }));
          });
          return this.flowDepositService.launchProcessControlStep(args.request).pipe(
            map((response) => {
              return actions.launchProcessControlSuccess({ response: response, portalResponse: args.request });
            }),
            catchError(() =>
              of(actions.launchProcessControlFail())
            )
          );
        }
      )
    )
  );

  saveDirectorySuccess = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.launchProcessControlSuccess),
        tap((v) => {
          const { fileId, idCreator } = v?.portalResponse;

          // Validate document is identifiable.
          if (!v.response.data.ModeleName?.trim()) {
            this.store.dispatch(checkIsDocumentCannotIdentify({ isCannotIdentify: true }));
          } else {
            this.store.dispatch(checkIsDocumentCannotIdentify({ isCannotIdentify: false }));

            // Apply document identifiable action.
            interval(1000).pipe(take(1)).subscribe(() =>  {
              this.messageService.openCustomSnackbar({ message: this.propertiesLabel?.launchedSuccess, icon: 'close', type: 'success' });
              this.router.navigate(['cxm-deposit/pre-analysis'], {queryParams: { fileId, step: 2, ownerId: idCreator }});
            });
          }
        })
      ), { dispatch: false }
  );

  saveDirectoryFail = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.launchProcessControlFail),
        tap(() => {
          // this.messageService.openError(
          //   this.propertiesLabel?.message?.createdFail
          // );
        })
      ),
    { dispatch: false }
  );

  analyzeFlow$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.analyseFlow),
      switchMap((args) =>
        this.flowDepositService.analyseFlow(args.request).pipe(
          map((response) => {
            return actions.analyseFlowSuccess({
              response: response,
              productionForm: response?.data?.document?.DOCUMENT?.[0]?.PRODUCTION
            });
          }),
          catchError(() =>
            of(actions.analyseFlowFail())
          )
        )
      )
    )
  );

  analyzeFlowSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.analyseFlowSuccess),
        tap(() => {
          // Navigate to step 3.
          this.store.dispatch(goBackToAnalysisResult());
        })
      ), { dispatch: false }
  );

  analyzeFlowFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.analyseFlowFail),
        tap(() => {
          // this.messageService.openError(
          //   this.propertiesLabel?.message?.createdFail
          // );
        })
      ),
    { dispatch: false }
  );

  treatmentFlow$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.treatmentFlow),
      switchMap((args) =>
        this.flowDepositService.treatmentFlowStep(args.request).pipe(
          map((response) => {
            return actions.treatmentFlowSuccess({ response: response, productionForm: args?.request?.production });
          }),
          catchError((httpError) => of(actions.treatmentFlowFail({ httpError })))
        )
      )
    )
  );

  treatmentFlowSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.treatmentFlowSuccess),
        tap(() => {
          // Navigate to step 5.
          this.store.dispatch(goToFinishedPage());
        })
      ), { dispatch: false }
  );

  treatmentFlowFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.treatmentFlowFail),
        switchMap((args) => {
          const { apierrorhandler } = args.httpError.error;

          if(apierrorhandler) {
            if([4005].includes(apierrorhandler.statusCode)) {
              return [actions.completeFlowWithMissingResource()]
            }
          }

          if(apierrorhandler) {
            if([4006].includes(apierrorhandler.statusCode)) {
              return [actions.missingSignatureConfiguration()]
            }
          }

          return [actions.NonceAction()]
        })
      ),
    { dispatch: true }
  );


  completeFlowWithUnloadConfigEffect$ = createEffect(() => this.action$.pipe(
    ofType(actions.completeFlowWithUnloadConfig),
    tap(() => {
      Promise.all([
        this.translateService.get('client.confirmLeave').toPromise(),
        this.translateService.get('client.messages').toPromise()
      ])
      .then(([buttons, messages]) => {
        this.confirmMessageService.showConfirmationPopup({
          icon: 'close',
          title: messages.noOffloadConfig,
          message: messages.noOffload_hint,
          paragraph: messages.noOffload_hint2,
          cancelButton: buttons.cancelButton,
          confirmButton: buttons.confirmButton,
          type: 'Warning'
        }).subscribe(ok => {
          if(ok) {
            // quit
            this.router.navigateByUrl(`${appRoute.cxmDeposit.list}?nl=1`);
          }
        })
      })
    })
  ), { dispatch: false })

  completeFlowWithMissingResourceEffect$ = createEffect(() => this.action$.pipe(
    ofType(actions.completeFlowWithMissingResource),
    tap(() => {
      Promise.all([
        this.translateService.get('background.messages').toPromise(),
        this.translateService.get('client.confirmLeave').toPromise()
      ]) .then(([messages, buttons]) => {
        this.confirmMessageService.showConfirmationPopup({
          icon: 'close',
          title: messages.missingRes,
          message: messages.missingRes_hint,
          paragraph: messages.missingRes_hint2,
          cancelButton: buttons.cancelButton,
          confirmButton: buttons.confirmButton,
          type: 'Warning'
        }).subscribe(ok => {
          if(ok) {
            // quit
            this.router.navigateByUrl(`${appRoute.cxmDeposit.list}?nl=1`);
          }
        })
       })
    })
  ), { dispatch: false })

  distributionChannelDoesNotConfig$ = createEffect(() => this.action$.pipe(
    ofType(actions.distributionChannelDoesNotConfig),
    tap(() => {
      this.translateService.get('flow.deposit.channel_category_not_activated').toPromise().then(messages => {
        this.confirmMessageService.showConfirmationPopup({
          icon: 'close',
          title: messages.title,
          message: messages.message,
          paragraph: messages.message2,
          cancelButton: messages.cancelButton,
          confirmButton: messages.confirmButton,
          type: 'Warning'
        }).subscribe(ok => {
          if(ok) {
            // quit
            this.router.navigateByUrl(`${appRoute.cxmDeposit.list}?nl=1`);
          }
        })
      });
    })
  ), { dispatch: false });


  switchProcessFailEffect$ = createEffect(() => this.action$.pipe(
    ofType(actions.switchProcessFail),
    switchMap((args) => {
      const { apierrorhandler } = args.httpError.error;
      if(apierrorhandler) {
        if([4003].includes(apierrorhandler.statusCode)) {
          return [actions.completeFlowWithUnloadConfig()];
        }

        if([4005].includes(apierrorhandler.statusCode)) {
          return [actions.distributionChannelDoesNotConfig()];
        }
      }
      return [actions.switchFlowFail()];
    })
  ))

  switchFlow$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.switchFlow),
      switchMap((args) =>
        this.flowDepositService.switchFlowStep(args.request).pipe(
          map(() => {
            return actions.switchFlowSuccess();
          }),
          catchError((e) =>
            of(actions.switchProcessFail({ httpError: e }))
          )
        )
      )
    )
  );

  switchFlowSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.switchFlowSuccess),
        tap(() => {
          // clear state of deposit component
          this.store.dispatch(unloadUploadFileAction());
          this.store.dispatch(clearDepositFlow());
          this.router.navigateByUrl('cxm-deposit/validate-result');
        })
      ), { dispatch: false }
  );

  // switchFlowFail$ = createEffect(
  //   () =>
  //     this.action$.pipe(
  //       ofType(actions.treatmentFlowFail),
  //       tap(() => {
  //         //
  //       })
  //     ),
  //   { dispatch: false }
  // );

  loadComposedBase64File$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.loadComposedBase64File),
      switchMap((args) =>
        this.flowDepositService.loadFileMetaData(args.request.docId || '', args.funcKey, args.privKey).pipe(
          map((response) => {
            return actions.loadComposedBase64FileSuccess({ base64: response.content });
          }),
          catchError(() =>
            of(actions.switchFlowFail())
          )
        )
      )
    )
  );

  loadComposedBase64FileSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadComposedBase64FileSuccess)
      ), { dispatch: false }
  );

  loadComposedBase64FileFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(actions.loadComposedBase64FileFail)
      ),
    { dispatch: false }
  );


  documentAnalyseResultMessageEffect$ = createEffect(() => this.action$.pipe(
    ofType(actions.documentAnalyseResultMessage),
    tap(() => {
      this.messageService.openCustomSnackbar({message: this.propertiesLabel.atLeastOneDocOK, type: 'error', icon: 'close'})
    })
  ), { dispatch: false });

  updateFlowDepositStep$ = createEffect(() => this.action$.pipe(
      ofType(actions.updateFlowDepositStep),
      switchMap((args) => this.flowDepositService.updateFlowDepositStep(args.uuid, args.step, args?.validation, args?.composedFileId).pipe(
          map(() => actions.unloadFlowDepositForm()),
          catchError(() => of(actions.unloadFlowDepositForm()))
        )
      )
    )
  );

  updateToFinalizeStatus$ = createEffect(() => this.action$.pipe(
    ofType(actions.updateToFinalizeStatus),
    switchMap((args) =>
      this.flowDepositService.updateFlowDepositStep(args.uuid, args.step, args?.validation, args?.composedFileId)
    )
  ), { dispatch: false });

  cancelFlowDeposit$ = createEffect(() => this.action$.pipe(
    ofType(actions.cancelFlowDeposit),
    exhaustMap(({ uuid, ownerId }) => {
      return this.flowDepositService.cancelFlowDeposit(uuid, ownerId).pipe(
        map(() => actions.cancelFlowDepositSuccess()),
        catchError(httpErrorResponse => [actions.cancelFlowDepositFails(httpErrorResponse)])
      )
    })
  ));

  cancelFlowDepositSuccess$ = createEffect(() => this.action$.pipe(
    ofType(actions.cancelFlowDepositSuccess),
    tap(() => {
      this.store.dispatch(unloadUploadFileAction());
      this.store.dispatch(clearDepositFlow());
      // this.messageService.openCustomSnackbar({message: this.propertiesLabel?.cancelSuccess, icon: 'close', type: 'success'})
      this.router.navigateByUrl('/cxm-deposit/acquisition');
    })
  ), { dispatch: false });

  cancelFlowDepositFails$ = createEffect(() => this.action$.pipe(
    ofType(actions.cancelFlowDepositFails),
    tap(({ httpErrorResponse }) => {
      this.messageService.openCustomSnackbar({message: this.propertiesLabel?.cancelFail, type: 'error', icon: 'close'});
    })
  ), { dispatch: false });

  getLimitUploadFileSize$ = createEffect(() => this.action$.pipe(
    ofType(actions.getLimitUploadFileSize),
    switchMap(() => this.flowDepositService.getLimitUploadFileSize().pipe(
      map(fileSize => actions.getLimitUploadFileSizeSuccess({ fileSize })),
      catchError(httpErrorResponse => [actions.getLimitUploadFileSizeFails({ httpErrorResponse })])
    ))
  ));

  getLimitUploadFileSizeFails$ = createEffect(() => this.action$.pipe(
    ofType(actions.getLimitUploadFileSizeFails),
    tap(({ httpErrorResponse }) => {
      this.messageService.openCustomSnackbar({ message: this.propertiesLabel?.cannotGetLimitFileSize, type: 'error', icon: 'close' });
    })
  ), {dispatch: false})

  updateStatusToFinalize$ = createEffect(() => this.action$.pipe(
    ofType(actions.updateStatusToFinalize),
    exhaustMap(({step, fileId, composedFileId, validation}) =>
      this.flowDepositService.updateFlowDepositStep(fileId, step, validation, composedFileId).pipe(
        map(() => actions.updateStatusToFinalizeSuccess())
      ))
  ));

  missingSignatureConfiguration$ = createEffect(() => this.action$.pipe(
    ofType(actions.missingSignatureConfiguration),
    tap(() => {
      this.translateService.get('flow.deposit.missing_config_signature').toPromise().then(messages => {
        this.confirmMessageService.showConfirmationPopup({
          icon: 'close',
          title: messages.title,
          message: messages.message,
          paragraph: messages.message2,
          cancelButton: messages.cancelButton,
          confirmButton: messages.confirmButton,
          type: 'Warning'
        }).subscribe(ok => {
          if(ok) {
            // quit
            this.router.navigateByUrl(`${appRoute.cxmDeposit.list}?nl=1`);
          }
        })
      });
    })
  ), { dispatch: false });

  validateModelConfigurationChanged$ = createEffect(() =>
    this.action$.pipe(
      ofType(actions.isModelNameConfigurationChanged),
      exhaustMap((args) =>
        this.flowDepositService
          .validateModelConfigurationChanged(args.modelName)
          .pipe(
            map((isModelNameChanged) =>
              actions.isModelNameConfigurationChangedSuccess({ isModelNameChanged })
            )
          )
      )
    )
  );

  constructor(
    private store: Store,
    private router: Router,
    private action$: Actions,
    private messageService: SnackBarService,
    private translateService: TranslateService,
    private confirmMessageService: ConfirmationMessageService,
    private flowDepositService: FlowDepositService,
    private profileService: ProfileService) {
    this.translateService.use(localStorage.getItem('locale') || 'fr');
    this.translateService
      .get('flow.deposit.message')
      .subscribe((v) => (this.propertiesLabel = v));
  }
}
