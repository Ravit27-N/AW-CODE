import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as fromActions from './flow-detail.action';
import {
  cancelDepositFlowAfterConfirm,
  finalizedFlowFromFlowHistoryDetail,
  unloadFlowHistoryDetail
} from './flow-detail.action';
import { catchError, exhaustMap, map, take, tap } from 'rxjs/operators';
import { FlowHandleExceptionService, FlowTraceabilityService } from '../../services';
import {
  CanModificationService,
  CanVisibilityService,
  SnackBarService
} from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Store } from '@ngrx/store';
import {
  canCancelFlowTraceability,
  canCancelFlowTraceabilityInStatus,
  canDownloadFileInStatuses,
  canFinalizedFlow,
  canViewUnitShipmentInStatuses,
  FlowStatusConstant,
  FlowTypeMode
} from '@cxm-smartflow/flow-traceability/util';
import { Router } from '@angular/router';
import {
  appRoute,
  FlowDigitalPrivilegeModel,
  FlowTraceability,
  FULL_DATE_TIME_FORMAT,
  PrivilegeModel
} from '@cxm-smartflow/shared/data-access/model';
import { defaultFilterOnViewShipment, loadDocumentTraceabilityList } from '../document-traceability';
import { tabToFlowDocument } from '../flow-traceability';
import { formatDate } from '@cxm-smartflow/shared/utils';

interface HistoryStatus {
  order: number;
  status: string;
  statusLabel: string;
}

interface FlowHistory {
  id: number;
  createdBy: string;
  dateTime: Date;
  flowTraceabilityId: number;
  fullName?: string;
  server?: string;
  historyStatus: HistoryStatus;
}

@Injectable({ providedIn: 'root' })
export class FlowDetailEffect {

  flowMessage: any;

  // Privileges.
  flowTraceabilityPrivilege = FlowTraceability;

  showFullNameInStatus = [
    FlowStatusConstant.DEPOSITED,
    FlowStatusConstant.VALIDATED,
    FlowStatusConstant.REFUSED,
    FlowStatusConstant.REFUSED_DOCUMENT,
    FlowStatusConstant.CANCELED
  ];

  // Flow digital detail.
  isDeliveryStatisticComponentVisibleInStatuses = [
    FlowStatusConstant.IN_PROCESS.toLowerCase(),
    FlowStatusConstant.IN_ERROR.toLocaleLowerCase(),
    FlowStatusConstant.COMPLETED.toLowerCase()
  ];

  exportFileButtonVisibleInStatuses = [
    FlowStatusConstant.COMPLETED.toLowerCase(),
    FlowStatusConstant.IN_PROCESS.toLowerCase()
  ];

  isNonOperationButtonVisibleInStatus = [
    FlowStatusConstant.COMPLETED.toLowerCase(),
    FlowStatusConstant.IN_PROCESS.toLowerCase()
  ];

  transformFlowTraceabilityDetail = (flowTraceability: any) => {
    return {
      ...flowTraceability,
      ...this.transformUnloadingDate(flowTraceability?.unloadingDate, flowTraceability?.histories as FlowHistory[]),
      histories: this.transformHistories(flowTraceability),
      privilege: this.getPrivilege(flowTraceability)
    };
  };

  /**
   *  True, if last histories status === Scheduled .
   * @param unloadingDate
   * @param flowHistories
   */
  transformUnloadingDate = (unloadingDate: Date, flowHistories: FlowHistory[]) => {
    let canShowUnloadingDate: boolean;
    let showScheduleLabel: boolean;
    const lastFlowHistory = (flowHistories[flowHistories.length - 1] as FlowHistory);
    if (lastFlowHistory.historyStatus.status === FlowStatusConstant.SCHEDULED) {
      canShowUnloadingDate = true;
      showScheduleLabel = true;
    } else if ((lastFlowHistory.historyStatus.status === FlowStatusConstant.CANCELED) ||
      lastFlowHistory.historyStatus.status === FlowStatusConstant.REFUSED) {
      canShowUnloadingDate = false;
      showScheduleLabel = false;
    } else {
      canShowUnloadingDate = flowHistories.some(value => value.historyStatus.status === FlowStatusConstant.SCHEDULED);
      showScheduleLabel = false;
    }

    let unloadingDateLabel = '';

    if (unloadingDate) {
      const dateTime = formatDate.formatDateTime(
        unloadingDate,
        FULL_DATE_TIME_FORMAT
      );

      if (showScheduleLabel) {
        unloadingDateLabel = `${dateTime.split(' ')[0]} ${this.flowMessage?.eventHistory?.at} ${dateTime.split(' ')[1]}
        ${this.flowMessage?.informationLabel?.unloadingLabel}`;
      } else {
        unloadingDateLabel = `${dateTime.split(' ')[0]} ${this.flowMessage?.eventHistory?.at} ${dateTime.split(' ')[1]}`;
      }
    }

    return {
      showUnloadingDate: canShowUnloadingDate,
      unloadingDateLabel: unloadingDateLabel
    };
  };

  getPrivilege = (flowTraceability: any): PrivilegeModel => {// TODO: Point of test
    return {
      canViewDetail: this.canViewUnitShipment(flowTraceability?.ownerId, flowTraceability?.status),
      canModify: this.canFinalizeFlow(flowTraceability?.ownerId, flowTraceability?.status),
      canDownload: this.canDownload(flowTraceability?.ownerId, flowTraceability?.status),
      canCancel: this.canCancelFlow(flowTraceability?.ownerId, flowTraceability?.status)
    };
  };

  canCancelFlow = (ownerId: number, status: string): boolean => {
    return this.canModificationService.hasModify(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.CANCEL_FLOW, ownerId, true)
      && canCancelFlowTraceability.includes(status.toLowerCase());
  };

  canViewUnitShipment = (ownerId: number, status: string): boolean => {
    return this.canVisibilityService.hasVisibility(
        this.flowTraceabilityPrivilege.CXM_FLOW_TRACEABILITY,
        this.flowTraceabilityPrivilege.SELECT_AND_OPEN,
        ownerId,
        true
      ) &&
      canViewUnitShipmentInStatuses.includes(status.toLowerCase());
  };

  canFinalizeFlow = (ownerId: number, status: string): boolean => {
    return this.canModificationService.hasModify(FlowTraceability.CXM_FLOW_TRACEABILITY,
        FlowTraceability.FINALIZE,
        ownerId,
        true) &&
      canFinalizedFlow.includes(status.toLowerCase());
  };

  canDownload = (ownerId: number, status: string): boolean => {
    return this.canVisibilityService.hasVisibility(
        this.flowTraceabilityPrivilege.CXM_FLOW_TRACEABILITY,
        this.flowTraceabilityPrivilege.DOWNLOAD,
        ownerId,
        true
      ) &&
      canDownloadFileInStatuses.includes(status.toLowerCase());
  };

  transformHistories = (flowTraceability: any): any [] => {
    return flowTraceability?.histories?.map((value: any) => {
      return {
        ...value,
        canSeeFullName: this.canSeeFullName(value)
      };
    });
  };

  canSeeFullName = (flow: any): boolean => {
    const { status } = flow?.historyStatus;
    return this.showFullNameInStatus.includes(status);
  };

  loadFlowHistoryDetail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.loadFlowHistoryDetail),
    exhaustMap(({ id }) => this.flowService.getFlowTraceabilityById(id).pipe(
      map(flowTraceabilityDetail => fromActions.loadFlowHistoryDetailSuccess(
        {
          flowTraceabilityDetail: this.transformFlowTraceabilityDetail(flowTraceabilityDetail)
        })),
      catchError(httpErrorResponse => [fromActions.loadFlowHistoryDetailFail({ httpErrorResponse })])
    ))));

  loadFlowHistoryDetailFail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.loadFlowHistoryDetailFail),
    tap((err) => {
      this.translate.get('flow.history.flowErrorMessage.failToLoadFlowId').pipe(take(1))
        .subscribe(v => this.snackbar.openCustomSnackbar({ icon: 'close', type: 'error', message: v }));
    })), { dispatch: false });

  downloadFlowDepositDocument$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.downloadFlowTraceabilityDepositDocument),
    tap(({ flowDeposit, _type }) => {
      if (flowDeposit?.fileId) {
        this.flowService.getBase64File(flowDeposit.fileId, flowDeposit.subChannel, _type).pipe(take(1)).subscribe(data => {
          // Download file.
          this.downloadBase64File(data, flowDeposit.flowName);
        });
      }
    })), { dispatch: false });

  confirmCancelDepositFlow$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.confirmCancelDepositFlow),
    tap(({ confirmMessage }) => {
      this.confirmationMessage
        .showConfirmationPopup({
          icon: confirmMessage?.icon,
          title: confirmMessage?.messages?.title,
          message: confirmMessage?.messages?.message,
          importanceWorld: confirmMessage?.flowName,
          importanceWordSuffix: '?',
          paragraph: confirmMessage?.messages?.paragraph,
          cancelButton: confirmMessage?.messages?.cancelButton,
          confirmButton: confirmMessage?.messages?.confirmButton,
          type: 'Warning',
        })
        .toPromise()
        .then((e) => {
          // Cancel flow.
          if (e && confirmMessage?.flowId) {

            this.store.dispatch(
              cancelDepositFlowAfterConfirm({
                flowId: confirmMessage.flowId,
                fileId: confirmMessage.fileId,
                ownerId: confirmMessage?.ownerId,
              })
            );
          }
        });
    })), { dispatch: false });

  cancelDepositFlowAfterConfirm$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.cancelDepositFlowAfterConfirm),
    exhaustMap(({ fileId, ownerId }) => this.flowService.cancelFlowDepositPortal(fileId, ownerId).pipe(
      map(() => fromActions.cancelDepositFlowAfterConfirmSuccess()),
      catchError((httpErrorResponse) => [fromActions.cancelDepositFlowAfterConfirmFail({ httpErrorResponse })])
    ))));

  cancelDepositFlowAfterConfirmSuccess$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.cancelDepositFlowAfterConfirmSuccess),
    tap(() => {
      this.translate.get('flow.history.flowSuccessMessage.cancelSuccess').toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ icon: 'close', type: 'success', message });
        this.store.dispatch(fromActions.backToListOfFlowTraceability());
      });
    })), { dispatch: false });

  cancelDepositFlowAfterConfirmFail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.cancelDepositFlowAfterConfirmFail),
    tap(() => {
      this.translate.get('flow.history.flowErrorMessage.cancelFail').toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ icon: 'close', type: 'error', message });
      });
    })), { dispatch: false });

  backToListOfFlowTraceability$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.backToListOfFlowTraceability),
    tap(() => {
      // Unload data in flow history detail before navigate.
      this.store.dispatch(unloadFlowHistoryDetail());

      // Navigate to list of flow traceability.
      this.router.navigateByUrl('/cxm-flow-traceability/list');
    })
  ), { dispatch: false });

  confirmFinalizeFlowFromFlowHistoryDetail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.confirmFinalizeFlowFromFlowHistoryDetail),
    tap(({ confirmMessage }) => {
      this.confirmationMessage.showConfirmationPopup({
        title: confirmMessage?.messages?.title,
        message: confirmMessage?.messages?.message,
        importanceWorld: confirmMessage?.flow?.flowName,
        cancelButton: confirmMessage?.messages?.cancelButton,
        confirmButton: confirmMessage?.messages?.confirmButton,
        type: 'Active'
      }).toPromise().then(e => {
        if (e && confirmMessage?.flow) {
          this.store.dispatch(finalizedFlowFromFlowHistoryDetail({ flow: confirmMessage?.flow }));
        }
      });
    })), { dispatch: false });

  finalizedFlowFromFlowHistoryDetail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.finalizedFlowFromFlowHistoryDetail),
    tap(({ flow }) => {

      this.flowService.getComposedIdAndStep(flow?.id).toPromise().then((res) => {
        // Navigate to flow deposit.
        this.router.navigate([this.prepareDepositPath(flow?.step)], {
          queryParams: {
            composedFileId: res?.composedId,
            fileId: flow?.fileId,
            ownerId: flow?.ownerId,
            step: flow?.step,
            validation: res?.validation
          }
        });
      });

    })), { dispatch: false });

  navigateToFlowListDocument$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.navigateToFlowListDocument),
    tap(({ flow }) => {
      // Unload data in flow history detail before navigate.
      this.store.dispatch(unloadFlowHistoryDetail());
      this.store.dispatch(
        loadDocumentTraceabilityList({
          flowTraceabilityId: flow?.id || 0,
          ...defaultFilterOnViewShipment,
          params: { ...defaultFilterOnViewShipment.params, filter: flow?.flowName || '' }
        })
      );

      this.store.dispatch(tabToFlowDocument({ isToDocument: true }));

      if (flow?.channel === FlowTypeMode.POSTAL) {
        this.router.navigate([appRoute.cxmFlowTraceability.navigateToViewDocumentShipment], {
          queryParams: { flowTraceabilityId: flow?.id, flowName: flow?.flowName }
        });
      } else {
        this.router.navigate([appRoute.cxmFlowTraceability.navigateToViewDocumentShipment], {
          queryParams: { flowTraceabilityId: flow?.id, flowName: flow?.detail?.campaignName }
        });
      }
    })), { dispatch: false });

  transformFlowCampaignDetail = (response: any) => {
    return {
      ...response,
      privilege: this.getFlowCampaignPrivilege(response)
    };
  };

  getFlowCampaignPrivilege = (flow: any): FlowDigitalPrivilegeModel => {
    return {
      canViewDetail: this.canViewFlowDocumentButtonVisible(flow?.ownerId, flow?.flowStatus?.status),
      canCancel: this.canCanFlowButtonVisible(flow?.ownerId, flow?.flowStatus?.status),
      isDeliveryStatisticComponentVisible: this.isDeliveryStatisticComponentVisible(flow?.ownerId, flow?.flowStatus?.status),
      exportFileButtonVisible: this.exportFileButtonVisible(flow?.ownerId, flow?.flowStatus?.status),
      isNonOperationButtonVisible: this.isNonOperationButtonVisible(flow?.flowStatus?.status)
    };
  };

  canViewFlowDocumentButtonVisible = (ownerId: number, status: string): boolean => {
    return this.canVisibilityService.hasVisibility(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.SELECT_AND_OPEN, ownerId, true)
      && canViewUnitShipmentInStatuses.includes(status.toLowerCase());
  };

  canCanFlowButtonVisible = (ownerId: number, status: string): boolean => {
    return this.canModificationService.hasModify(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.CANCEL_FLOW, ownerId, true)
      && canCancelFlowTraceabilityInStatus.includes(status.toLowerCase());
  };

  isDeliveryStatisticComponentVisible = (ownerId: number, status: string): boolean => {
    return this.isDeliveryStatisticComponentVisibleInStatuses.includes(status.toLowerCase());
  };

  exportFileButtonVisible = (ownerId: number, status: string): boolean => {
    return this.canVisibilityService.hasVisibility(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.DOWNLOAD, ownerId, true)
      // && this.exportFileButtonVisibleInStatuses.includes(status.toLowerCase());
      && canDownloadFileInStatuses.includes(status.toLowerCase());
  };

  isNonOperationButtonVisible = ( status: string): boolean => {
    return this.isNonOperationButtonVisibleInStatus.includes(status.toLowerCase());
  };

  flowCampaignDetail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.flowCampaignDetail),
    exhaustMap(({ flowId }) => this.flowService.getFlowCampaignId(flowId).pipe(
      map(flow => fromActions.flowCampaignDetailSuccess({ flow: this.transformFlowCampaignDetail(flow) })),
      catchError(httpErrorResponse => [fromActions.flowCampaignDetailFail({ httpErrorResponse })])
    ))
  ));

  flowCampaignDetailFail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.flowCampaignDetailFail),
    tap(() => {
      this.translate.get('flow.history.flowErrorMessage.failToLoadFlowId').toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ icon: 'close', type: 'error', message });
      });
    })
  ), { dispatch: false });

  downloadBase64File(base64: string, filename: string) {
    const source = `data:application/octet-stream;base64,${base64}`;
    const link = document.createElement('a');
    link.href = source;
    link.download = filename;
    link.click();
  }

  prepareDepositPath(step: number): string {
    switch (step) {
      case 1:
        return '/cxm-deposit/acquisition';
      case 2:
        return '/cxm-deposit/pre-analysis';
      case 3:
        return '/cxm-deposit/analysis-result';
      case 4:
        return '/cxm-deposit/production-criteria';
      case 5:
        return '/cxm-deposit/finished';
      default:
        return '';
    }
  }


  constructor(private actions: Actions,
              private flowService: FlowTraceabilityService,
              private snackbar: SnackBarService,
              private translate: TranslateService,
              private confirmationMessage: ConfirmationMessageService,
              private store: Store,
              private handleException: FlowHandleExceptionService,
              private router: Router,
              private canVisibilityService: CanVisibilityService,
              private canModificationService: CanModificationService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.get('flow.history').subscribe(value => this.flowMessage = value);
  }
}
