import { FlowTraceabilityService } from '../../services';
import {
  cancelFlowDepositPortal,
  cancelFlowTraceabilityAfterConfirm,
  cancelFlowTraceabilityAfterConfirmFail,
  cancelFlowTraceabilityAfterConfirmSuccess,
  confirmCancelFlowTraceability,
  downloadFeedAction,
  filterCriteriaFlowChangeAction,
  loadFeatureFlowTraceabilityList,
  loadFeatureFlowTraceabilityListFail,
  loadFeatureFlowTraceabilityListSuccess,
  navigateToDocumentTraceabilityAction,
} from './flow-traceability-list.action';
import { Injectable, OnDestroy } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  map,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { of, Subject } from 'rxjs';
import { Router } from '@angular/router';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import {
  appRoute,
  DepositMode,
  EntityResponseHandler,
  FlowTraceability,
  PrivilegeModel,
} from '@cxm-smartflow/shared/data-access/model';
import { Store } from '@ngrx/store';
import { tabToFlowDocument } from '../flow-traceability';
import {
  canCancelFlowTraceabilityInStatus,
  canDownloadFileInStatuses,
  canViewUnitShipmentInStatuses,
  FlowStatusConstant,
  getDepositPath,
  replaceStatusLabelByDash, SendingSubChannel
} from '@cxm-smartflow/flow-traceability/util';
import { flowTraceabilityEnv } from '@env-flow-traceability';
import {
  CanAccessibilityService,
  CanModificationService,
  CanVisibilityService,
  SnackBarService,
} from '@cxm-smartflow/shared/data-access/services';
import {
  FlowTraceabilityList,
  FlowTraceabilityModel,
  validateFlowSubChannel,
} from '../../models';
import { selectPaginationWithFilterCriteria } from './flow-traceability-list.selector';

@Injectable({ providedIn: 'root' })
export class FeatureFlowTraceabilityListEffect implements OnDestroy {
  confirmPopupLabel: any;
  private destroyed$ = new Subject<void>();

  // Privileges.
  flowTraceabilityPrivilege = FlowTraceability;

  constructor(
    private action$: Actions,
    private flowTraceabilityService: FlowTraceabilityService,
    private router: Router,
    private confirmMessage: ConfirmationMessageService,
    private translate: TranslateService,
    private snackbar: SnackBarService,
    private store: Store,
    private canModifyService: CanModificationService,
    private canAccessService: CanAccessibilityService,
    private canVisibilityService: CanVisibilityService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate
      .get('flowTraceability.confirmationPopUp')
      .subscribe((response) => (this.confirmPopupLabel = response));
  }

  loadFeatureFlowTraceabilityList$ = createEffect(() =>
    this.action$.pipe(
      ofType(loadFeatureFlowTraceabilityList),
      exhaustMap((arg) => {
        let params = {};
        if (arg.params) {
          const { isDisplayLabel, ...res } = arg.params;
          params = res;
        }
        return this.flowTraceabilityService
          .getFlowTraceabilityList(arg.page, arg.pageSize, params)
          .pipe(
            map((response: EntityResponseHandler<FlowTraceabilityModel>) =>
              loadFeatureFlowTraceabilityListSuccess({
                response: this.transformFlowTraceabilityListData(response),
                params: arg.params,
              })
            ),
            catchError((error: any) =>
              of(loadFeatureFlowTraceabilityListFail({ error: error }))
            )
          );
      })
    )
  );

  loadFeatureFlowTraceabilityListFail = createEffect(
    () =>
      this.action$.pipe(
        ofType(loadFeatureFlowTraceabilityListFail),
        tap(() => {
          // TODO: Show error message.
        })
      ),
    { dispatch: false }
  );

  filterOptionChangedEffect = createEffect(() =>
    this.action$.pipe(
      ofType(filterCriteriaFlowChangeAction),
      switchMap((args) => [loadFeatureFlowTraceabilityList(args as any)])
    )
  );

  downloadFeedEffect$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(downloadFeedAction),
        tap((args: any) => {
          if (args.flow.fileId) {
            let flowTypeValue;
            if (args.flow.depositMode !== DepositMode.BATCH) {
              flowTypeValue = args.flow.subChannel;
            }

            this.flowTraceabilityService
              .getBase64File(args.flow.fileId, flowTypeValue, args._type)
              .subscribe((data) => {
                this.downloadBase64File(data, args.flow);
              });
          }
        })
      ),
    { dispatch: false }
  );

  navigateToShipmentTrackingAction$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(navigateToDocumentTraceabilityAction),
        tap((args) => {
          this.store.dispatch(tabToFlowDocument({ isToDocument: true }));

          this.router.navigate(
            [appRoute.cxmFlowTraceability.navigateToViewDocumentShipment],
            {
              queryParams: {
                flowTraceabilityId: args?.id,
                flowName: args?.flowName,
              },
            }
          );
        })
      ),
    { dispatch: false }
  );

  confirmCancelFlowTraceability$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(confirmCancelFlowTraceability),
        tap((args) => {
          const flowInfo = args?.data.information;
          const confirmMessage = args?.data.confirmMessage;
          this.confirmMessage
            .showConfirmationPopup({
              icon: 'error',
              title: confirmMessage.title,
              message: confirmMessage.message,
              importanceWorld: flowInfo.flowName,
              importanceWordSuffix: '?',
              paragraph: confirmMessage.paragraph,
              cancelButton: confirmMessage.cancelButton,
              confirmButton: confirmMessage.confirmButton,
              type: 'Warning',
            })
            .toPromise()
            .then((e) => {
              if (e && flowInfo.flowId) {
                if (flowInfo?.depositMode === 'Portal') {
                  this.store.dispatch(
                    cancelFlowDepositPortal({
                      flowId: flowInfo.flowId,
                      uuid: flowInfo.fileId,
                      ownerId: flowInfo?.ownerId,
                    })
                  );
                } else {
                  this.store.dispatch(
                    cancelFlowTraceabilityAfterConfirm({
                      flowId: flowInfo.flowId,
                    })
                  );
                }

              }
            });
        })
      ),
    { dispatch: false }
  );

  cancelFlowTraceabilityAfterConfirm$ = createEffect(() =>
    this.action$.pipe(
      ofType(cancelFlowTraceabilityAfterConfirm),
      exhaustMap(({ flowId }) =>
        this.flowTraceabilityService
          .updateStatus(
            flowId,
            FlowStatusConstant.CANCELED,
            flowTraceabilityEnv.server
          )
          .pipe(
            map(() => cancelFlowTraceabilityAfterConfirmSuccess({ flowId })),
            catchError((httpErrorResponse) => [
              cancelFlowTraceabilityAfterConfirmFail({
                httpErrorResponse,
              }),
            ])
          )
      )
    )
  );

  cancelFlowTraceabilityAfterConfirmSuccess$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(cancelFlowTraceabilityAfterConfirmSuccess),
        withLatestFrom(this.store.select(selectPaginationWithFilterCriteria)),
        tap(([args, selectPaginationWithFilterCriteria]) => {
          this.translate
            .get('flow.traceability.message.cancelFlowSuccess')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message,
              });

              // Refresh data.
              const {
                page,
                pageSize,
                params,
              } = selectPaginationWithFilterCriteria;
              this.store.dispatch(
                loadFeatureFlowTraceabilityList({
                  page: page,
                  pageSize: pageSize,
                  params: params,
                })
              );
            });
        })
      ),
    { dispatch: false }
  );

  cancelFlowTraceabilityAfterConfirmFail$ = createEffect(
    () =>
      this.action$.pipe(
        ofType(cancelFlowTraceabilityAfterConfirmFail),
        tap(() => {
          this.translate
            .get('flow.traceability.message.cancelFlowFail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  cancelFlowDeposit$ = createEffect(() =>
    this.action$.pipe(
      ofType(cancelFlowDepositPortal),
      exhaustMap(({ uuid, ownerId, flowId }) => {
        return this.flowTraceabilityService
          .cancelFlowDepositPortal(uuid, ownerId)
          .pipe(
            map(() => cancelFlowTraceabilityAfterConfirmSuccess({flowId: flowId})),
            catchError((httpErrorResponse) => [
              cancelFlowTraceabilityAfterConfirmFail(httpErrorResponse),
            ])
          );
      })
    )
  );

  getFilename = (flowTraceability: any): string => {
    let filename = '';
    switch ((flowTraceability?.depositMode as string)?.toUpperCase()) {
      case DepositMode.PORTAL.toUpperCase():
        filename =
          flowTraceability.subChannel?.toLowerCase() === SendingSubChannel.SMS.toLowerCase() ||
          flowTraceability.subChannel?.toLowerCase() === SendingSubChannel.EMAIL.toLowerCase()
            ? flowTraceability.campaignFilename
            : flowTraceability.flowName;
        break;
      case DepositMode.BATCH.toUpperCase():
        filename = flowTraceability.flowName;
        break;
      case DepositMode.API.toUpperCase():
        break;
      default:
        filename = flowTraceability?.flowName;
    }
    return filename;
  };

  downloadBase64File(base64: string, flowTraceability: any) {
    const source = `data:application/octet-stream;base64,${base64}`;
    const link = document.createElement('a');
    link.href = source;
    link.download = this.getFilename(flowTraceability);
    link.click();
  }

  confirmPopup(row: any) {
    const messageInfo = row.information;
    this.confirmMessage
      .showConfirmationPopup({
        icon: 'feedback',
        title: row.confirmMessage.title,
        message: row.confirmMessage.message,
        importanceWorld: row.information.flowName,
        cancelButton: row.confirmMessage.cancelButton,
        confirmButton: row.confirmMessage.confirmButton,
        type: 'Active',
      })
      .pipe(takeUntil(this.destroyed$))
      .subscribe((v) => {


        if (v) {
          this.router.navigate([getDepositPath(row?.step || 2)], {
            queryParams: {
              step: row?.step || 2,
              fileId: messageInfo.fileId,
              composedFileId: row?.composedId || '',
              ownerId: messageInfo.ownerId,
              validation: row?.validation,
            },
          });
        }
      });
  }

  transformFlowTraceabilityListData = (
    response: EntityResponseHandler<FlowTraceabilityModel>
  ): FlowTraceabilityList => {
    const finalContents = response.contents?.map(
      (flowTraceability: FlowTraceabilityModel) => {
        return {
          ...flowTraceability,
          flowStatus: this.transformFlowStatus(flowTraceability?.flowStatus),
          category: this.getCategoryBySubChannel(
            flowTraceability?.subChannel || ''
          ),
          privilege: this.getPrivilege(flowTraceability),
        };
      }
    );

    return {
      contents: finalContents,
      page: response.page,
      pageSize: response.pageSize,
      total: response.total,
    };
  };

  getCategoryBySubChannel = (subChannel: string): string => {
    return validateFlowSubChannel(subChannel);
  };

  transformFlowStatus = (flowStatus: any) => {
    return {
      status: flowStatus?.status,
      statusLabel: flowStatus?.statusLabel,
      statusLabelReplacement: this.replaceStatusLabel(flowStatus?.status),
    };
  };

  replaceStatusLabel = (status: string): string => {
    return replaceStatusLabelByDash(status);
  };

  getPrivilege = (flow: FlowTraceabilityModel): PrivilegeModel => {
    const privilege = {
      canViewDetail: this.canViewUnitShipment(
        flow.ownerId,
        flow.status || ''
      ),
      canDownload: this.canDownload(flow.ownerId || 0, flow.status || ''),
      canCancel: this.canCancel(flow.ownerId || 0, flow.status || ''),
    };

    return {
      ...privilege,
      canShowToggleButton:
        privilege.canViewDetail || privilege.canDownload || privilege.canCancel,
    };
  };

  canViewUnitShipment = (ownerId: number, status: string): boolean => {
    return (
      this.canVisibilityService.hasVisibility(
        this.flowTraceabilityPrivilege.CXM_FLOW_TRACEABILITY,
        this.flowTraceabilityPrivilege.SELECT_AND_OPEN,
        ownerId,
        true
      ) && canViewUnitShipmentInStatuses.includes(status.toLowerCase())
    );
  };

  canDownload = (ownerId: number, status: string): boolean => {
    return (
      this.canVisibilityService.hasVisibility(
        this.flowTraceabilityPrivilege.CXM_FLOW_TRACEABILITY,
        this.flowTraceabilityPrivilege.DOWNLOAD,
        ownerId,
        true
      ) && canDownloadFileInStatuses.includes(status.toLowerCase())
    );
  };

  canCancel = (ownerId: number, status: string): boolean => {
    return (
      this.canModifyService.hasModify(
        this.flowTraceabilityPrivilege.CXM_FLOW_TRACEABILITY,
        this.flowTraceabilityPrivilege.CANCEL_FLOW,
        ownerId,
        true
      ) && canCancelFlowTraceabilityInStatus.includes(status.toLowerCase())
    );
  };

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }
}
