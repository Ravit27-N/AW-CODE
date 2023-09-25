import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import * as fromAction$ from './flow-deposit-list.action';
import { loadFlowDepositList } from './flow-deposit-list.action';
import {catchError, exhaustMap, map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {forkJoin, of} from 'rxjs';
import { FlowDepositService } from '../../services/flow-deposit.service';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { FlowTraceabilityService, validateFlowSubChannel } from '@cxm-smartflow/flow-traceability/data-access';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import {
  CanAccessibilityService,
  CanModificationService,
  SnackBarService
} from '@cxm-smartflow/shared/data-access/services';
import { selectFlowDepositListState } from './flow-deposit-list-selector';
import { getDepositPath, replaceStatusLabelByDash } from '@cxm-smartflow/flow-traceability/util';
import { FlowDepositList, FlowDepositModel } from '../../model';
import { DepositManagement, EntityResponseHandler, PrivilegeModel } from '@cxm-smartflow/shared/data-access/model';
import {
  cancelFlowDepositSuccess,
  detectPortalConfigurationChanged,
  isModelNameConfigurationChangedSuccess
} from '../flow-deposit';
import { FileSaverUtil } from '@cxm-smartflow/shared/utils';
import * as moment from "moment";

@Injectable({ providedIn: 'root' })
export class FlowDepositListEffect {

  // Privilege.
  depositManagement = DepositManagement;

  constructor(private action$: Actions, private route: Router, private flowDepositService: FlowDepositService,
              private confirmMessage: ConfirmationMessageService, private store: Store, private flowTraceabilityService: FlowTraceabilityService,
              private translate: TranslateService, private snackBar: SnackBarService, private canModifyService: CanModificationService,
              private canAccessService: CanAccessibilityService, private base64Converter: FileSaverUtil) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  transformListData = (response: EntityResponseHandler<FlowDepositModel>): FlowDepositList => {
    const contents = response.contents?.map((flowDeposit: FlowDepositModel) => {
      return {
        ...flowDeposit,
        category: this.transformCategory(flowDeposit?.flowSubChannel?.status || ''),
        statusColorClass: this.transformStatusToColorClass(flowDeposit?.flowStatus?.status || ''),
        privilege: this.validatePrivilege(flowDeposit)
      };
    });

    return {
      contents: contents,
      page: response.page,
      pageSize: response.pageSize,
      total: response.total
    };
  };

  validatePrivilege = (row: FlowDepositModel): PrivilegeModel => {
    return {
      canEdit: this.canEdit(),
      canModify: this.canModify(row.ownerId || 0),
      canDelete: this.canDelete(row.ownerId || 0)
    };
  };

  canEdit = (): boolean => {
    return this.canAccessService.canAccessible(this.depositManagement.CXM_FLOW_DEPOSIT, this.depositManagement.CXM_FLOW_DEPOSIT_LIST_DEPOSITS);
  };

  canModify = (ownerId: number): boolean => {
    return this.canModifyService.hasModify(this.depositManagement.CXM_FLOW_DEPOSIT, this.depositManagement.CXM_FLOW_DEPOSIT.concat('_' + this.depositManagement.MODIFY_A_DEPOSIT), ownerId, true);
  };

  canDelete = (ownerId: number): boolean => {
    return this.canModifyService.hasModify(this.depositManagement.CXM_FLOW_DEPOSIT, this.depositManagement.CXM_FLOW_DEPOSIT_DELETE_A_DEPOSIT, ownerId, true);
  };

  transformCategory = (subChannel: string): string => {
    return validateFlowSubChannel(subChannel);
  };

  transformStatusToColorClass = (status: string): string => {
    return replaceStatusLabelByDash(status);
  };

  loadFlowDepositList$ = createEffect(() =>
    this.action$.pipe(
      ofType(fromAction$.loadFlowDepositList),
      exhaustMap((args) => this.flowDepositService.getDepositList(args.page, args.pageSize, args.params)
        .pipe(
          map((response) => fromAction$.loadFlowDepositListSuccess(
            {
              response: this.transformListData(response),
              params: args.params,
              isLoading: false
            }
          )),
          catchError((error: any) => of((fromAction$.loadFlowDepositListFail({
            error: error,
            isLoading: false
          }))))
        )))
  );

  confirmDeleteFlowDeposit$ = createEffect(() =>
      this.action$.pipe(
        ofType(fromAction$.confirmDeleteFlowDeposit),
        tap((args) => {
          const flowInfo = args?.data.information;
          const confirmMessage = args?.data.confirmMessage;
          this.confirmMessage
            .showConfirmationPopup({
              icon: 'error',
              title: confirmMessage.title,
              message: confirmMessage.message,
              cancelButton: confirmMessage.cancelButton,
              confirmButton: confirmMessage.confirmButton,
              type: 'Warning'
            })
            .toPromise()
            .then((e) => {
              if (e && flowInfo.fileId) {
                this.store.dispatch(
                  fromAction$.deleteFlowDeposit({
                    fileId: flowInfo.fileId,
                    hideShowMessage: true
                  })
                );
              }
            });
        })
      ),
    { dispatch: false }
  );

  deleteFlowDeposit$ = createEffect(() =>
    this.action$.pipe(
      ofType(fromAction$.deleteFlowDeposit),
      exhaustMap(({ fileId, hideShowMessage }) =>
        this.flowTraceabilityService.deleteFlowDeposit(fileId)
          .pipe(
            map(() => fromAction$.deleteFlowDepositSuccess({hideShowMessage: hideShowMessage})),
            catchError((httpErrorResponse) => [fromAction$.deleteFlowDepositFail({ httpErrorResponse: httpErrorResponse })])
          ))
    ));

  deleteFlowDepositSuccess$ = createEffect(() =>
      this.action$.pipe(
        ofType(fromAction$.deleteFlowDepositSuccess),
        withLatestFrom(this.store.select(selectFlowDepositListState)),
        tap(([args, selectFlowDepositListState]) => {
          if(args.hideShowMessage){ // If true, we show success message, and reload list data.
            this.translate.get('flow.deposit.list.confirmation.deleteFlowMessage')
              .toPromise()
              .then((messages) => {
                this.snackBar.openCustomSnackbar({ icon: 'close', type: 'success', message: messages?.success });

                // Refresh data.
                const { params, response } = selectFlowDepositListState;
                const { page, pageSize } = response;
                this.store.dispatch(loadFlowDepositList({ page: page, pageSize: pageSize, params: params }));
              });
          }else{ // We call to cancelFlow action for redirect to acquisition step to re-upload pdf.
            this.store.dispatch(cancelFlowDepositSuccess());
          }
        })),
    { dispatch: false }
  );

  deleteFlowDepositFail$ = createEffect(() =>
      this.action$.pipe(
        ofType(fromAction$.deleteFlowDepositFail),
        tap(() => {
          this.translate.get('flow.deposit.list.confirmation.deleteFlowMessage')
            .toPromise()
            .then((messages) => {
              this.snackBar.openCustomSnackbar({ icon: 'close', type: 'error', message: messages?.fail });
            });
        })),
    { dispatch: false }
  );

  downloadFile$ = createEffect(() =>
      this.action$.pipe(
        ofType(fromAction$.downloadFile),
        tap((args: any) => {
          const { flowDeposit, _type } = args;
          this.flowTraceabilityService
            .getBase64File(flowDeposit.fileId, flowDeposit.subChannel, _type)
            .subscribe((data) => {
              this.base64Converter.downloadBase64(data, flowDeposit?.flowName);
            });
        })
      ),
    { dispatch: false }
  );

  // modifiedFlowDeposit$ = createEffect(() =>
  //     this.action$.pipe(
  //       ofType(fromAction$.modifiedFlowDeposit),
  //       tap((args: any) => {
  //         const flowDeposit = args?.row as FlowDepositModel;
  //         console.log({flowDeposit})
  //         this.flowDepositService.getLastModifiedPortalConfiguration().subscribe(data => {
  //           const isConfigChanged = moment(flowDeposit.lastModified).isSameOrBefore(data.lastModified);
  //           if (isConfigChanged && (flowDeposit.step && flowDeposit.step >= 3 && flowDeposit.fileId)) {
  //             // this.flowDepositService.
  //             this.store.dispatch(detectPortalConfigurationChanged({isConfigChanged}))
  //             this.flowDepositService.reAnalyzeModelChanged(flowDeposit.fileId).subscribe(config => {
  //               this.store.dispatch(isModelNameConfigurationChangedSuccess({isModelNameChanged: config.data.isModelChanged}))
  //               this.route.navigate([getDepositPath(3)], {
  //                 queryParams: {
  //                   step: flowDeposit.step || 2,
  //                   fileId: flowDeposit.fileId,
  //                   composedFileId: flowDeposit.composedFileId || '',
  //                   ownerId: flowDeposit.ownerId || 0,
  //                   validation: flowDeposit.validated || false
  //                 }
  //               });
  //             })
  //           }
  //         })
  //
  //         this.route.navigate([getDepositPath(flowDeposit.step || 2)], {
  //           queryParams: {
  //             step: flowDeposit.step || 2,
  //             fileId: flowDeposit.fileId,
  //             composedFileId: flowDeposit.composedFileId || '',
  //             ownerId: flowDeposit.ownerId || 0,
  //             validation: flowDeposit.validated || false
  //           }
  //         });
  //       })
  //     ),
  //   {dispatch: false}
  // );

  modifiedFlowDeposit$ = createEffect(() =>
      this.action$.pipe(
        ofType(fromAction$.modifiedFlowDeposit),
        tap((args: any) => {
          const flowDeposit = args?.row as FlowDepositModel;

          this.flowDepositService.getLastModifiedPortalConfiguration().pipe(
            switchMap(data => {
              const isConfigChanged = moment(flowDeposit.lastModified).isSameOrBefore(data.lastModified);

              if (isConfigChanged && flowDeposit.step && flowDeposit.step >= 3 && flowDeposit.fileId) {
                const reAnalyzeModelChanged$ = this.flowDepositService.reAnalyzeModelChanged(flowDeposit.fileId);

                return forkJoin([
                  of(isConfigChanged),
                  reAnalyzeModelChanged$
                ]);
              } else {
                return of([null, null]);
              }
            })
          ).subscribe(([isConfigChanged, config]) => {
            if (isConfigChanged) {
              this.store.dispatch(detectPortalConfigurationChanged({isConfigChanged}));

              if (config && config.data && config.data.isModelChanged) {
                this.store.dispatch(isModelNameConfigurationChangedSuccess({isModelNameChanged: config.data.isModelChanged}));
              }

              this.route.navigate([getDepositPath(3)], {
                queryParams: {
                  step: 3,
                  fileId: flowDeposit.fileId,
                  composedFileId: flowDeposit.composedFileId || '',
                  ownerId: flowDeposit.ownerId || 0,
                  validation: flowDeposit.validated || false,
                },
              });
            } else {
              this.route.navigate([getDepositPath(flowDeposit.step || 2)], {
                queryParams: {
                  step: flowDeposit.step || 2,
                  fileId: flowDeposit.fileId,
                  composedFileId: flowDeposit.composedFileId || '',
                  ownerId: flowDeposit.ownerId || 0,
                  validation: flowDeposit.validated || false,
                },
              });
            }
          });
        })
      ),
    {dispatch: false}
  );

  loadFilterCriteria$ = createEffect(() =>
  this.action$.pipe(
    ofType(fromAction$.loadFilterCriteria),
    exhaustMap(() => {
      return this.flowTraceabilityService.getFlowTraceabilityFilterCriteria()
        .pipe(
          map((response) => fromAction$.loadFilterCriteriaSuccess({response: response})),
          catchError((httpErrorResponse) => [fromAction$.loadFilterCriteriaFail({error: httpErrorResponse})])
        )
    })
  ));

  loadAllUserByService$ = createEffect(() =>
    this.action$.pipe(
      ofType(fromAction$.loadAllUserByService),
      exhaustMap((args) => {
        return this.flowTraceabilityService.loadAllUserOfDepositByService(args.serviceId)
          .pipe(map((response) => fromAction$.loadAllUserByServiceSuccess({ response: response })),
            catchError((httpErrorResponse) => [fromAction$.loadAllUserByServiceFail({ error: httpErrorResponse })]));
      })
    ));

  loadAllUserByServiceFail$ = createEffect(() =>
  this.action$.pipe(
    ofType(fromAction$.loadAllUserByServiceFail),
    tap(() => {
      // TODO: Show error message.
    })),
    {dispatch: false}
    )

  deleteFlowDepositAfterDocumentNoOK$ = createEffect(() =>
    this.action$.pipe(
      ofType(fromAction$.deleteFlowDepositAfterDocumentNoOK),
      exhaustMap(({ fileId }) => this.flowTraceabilityService.deleteFlowDeposit(fileId)
        .pipe(
          map(() => fromAction$.deleteFlowDepositAfterDocumentNoOKSuccess()),
          catchError(err => of(fromAction$.deleteFlowDepositAfterDocumentNoOKFail({ error: err })))
        )
      )));

  deleteFlowDepositAfterDocumentNoOKFail$ = createEffect(() =>
      this.action$.pipe(
        ofType(fromAction$.deleteFlowDepositAfterDocumentNoOKFail),
        tap(() => {
          this.translate.get('flow.deposit.list.confirmation.deleteFlowMessage')
            .toPromise()
            .then((messages) => {
              this.snackBar.openCustomSnackbar({ icon: 'close', type: 'error', message: messages?.fail });
            });
        })
      ),
    { dispatch: false }
  );
}
