import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  backToListOfFlowTraceability,
  confirmCancelDepositFlow,
  confirmFinalizeFlowFromFlowHistoryDetail,
  downloadFlowTraceabilityDepositDocument,
  flowTraceabilityDetailSelector,
  loadFlowHistoryDetail,
  navigateToFlowListDocument,
  unloadFlowHistoryDetail
} from '@cxm-smartflow/flow-traceability/data-access';
import { Subject } from 'rxjs';
import { pluck, take, takeUntil } from 'rxjs/operators';
import { EventHistory } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';
import { TranslateService } from '@ngx-translate/core';
import { formatDate } from '@cxm-smartflow/shared/utils';
import { DepositMode, EventHistoryType, FlowStatusConstant } from '@cxm-smartflow/flow-traceability/util';
import {
  FlowType,
  appLocalStorageConstant,
  FULL_DATE_TIME_FORMAT,
  FULL_DATE_TIME_NO_SECOND_FORMAT
} from '@cxm-smartflow/shared/data-access/model';
import { ActivatedRoute, Router } from '@angular/router';
import { FlowInfo } from './flow-portal-infomartion-board/flow-portal-infomartion-board.component';

@Component({
  selector: 'cxm-smartflow-flow-history',
  templateUrl: './flow-history.component.html',
  styleUrls: ['./flow-history.component.scss']
})
export class FlowHistoryComponent implements OnInit, OnDestroy {
  // Validation properties
  isFlowFinished = false;

  // State properties
  eventHistories: EventHistory[] = [];
  flowInfo: FlowInfo;
  fileName = '';
  flow: any;

  // Unsubscribe properties
  private _destroy$ = new Subject<boolean>();

  /**
   * Constructor
   */
  constructor(
    private _store: Store,
    private _translate: TranslateService,
    private _router: Router,
    private _activateRoute: ActivatedRoute
  ) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    this._translate.use(
      localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) ||
      appLocalStorageConstant.Common.Locale.Fr
    );

    // Load flow detail.
    this._activateRoute.queryParams.pipe(take(1), pluck('id')).subscribe((v) => {
      if (v) this._store.dispatch(loadFlowHistoryDetail({ id: v }));
    });

    // Subscribe flow traceability detail.
    this._store
      .select(flowTraceabilityDetailSelector).pipe(takeUntil(this._destroy$))
      .subscribe((flowDetail) => {
        if (flowDetail) {
          this.flow = flowDetail;
          this._translate.get('flow').pipe(take(1)).subscribe((message) => {

              const temp = this._prepareEventHistory(flowDetail?.histories, message?.history, flowDetail?.commentedDocument);
              this.eventHistories = this.getUnique(temp);
              this.flowInfo = this._getFlowInfo(flowDetail, message);
            });
        }
      });
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    // Unsubscribe all state.
    this._destroy$.next(true);
    this._store.dispatch(unloadFlowHistoryDetail());
  }

  // TODO: provide backend to remove duplicate key.
  getUnique(arr: any) {
    return [...new Map(arr?.map((item: any) => [item['status'], item])).values()] as [];
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _prepareEventHistory(events: any[], message: any, commentedDocument:number): EventHistory[] {
    let eventHistories: any[] = [];
    // Filter success, in error and canceled status.
    const finaleStatus = events?.filter(
      (e) =>
        e?.historyStatus?.status?.toLowerCase() === FlowStatusConstant.PROCESSED?.toLowerCase() ||
        e?.historyStatus?.status?.toLowerCase() === FlowStatusConstant.IN_ERROR?.toLowerCase() ||
        e?.historyStatus?.status?.toLowerCase() === FlowStatusConstant.REFUSED?.toLowerCase() ||
        e?.historyStatus?.status?.toLowerCase() === FlowStatusConstant.REFUSED_DOCUMENT?.toLowerCase() ||
        e?.historyStatus?.status?.toLowerCase() === FlowStatusConstant.CANCELED?.toLowerCase()
    );
    this.isFlowFinished = events?.filter(
      (e) => e?.historyStatus?.status === FlowStatusConstant.PROCESSED
    )?.length !== 0;
    eventHistories = events?.map((e) => {
      // Prepare date label.
      const dateTime = formatDate.formatDateTime(
        e.dateTime,
        FULL_DATE_TIME_NO_SECOND_FORMAT
      );
      const dateLabel = `${message?.eventHistory?.on} ${
        dateTime.split(' ')[0]
      } ${message?.eventHistory?.at} ${dateTime.split(' ')[1]}`;

      if (e?.canSeeFullName) { // return with createdBy property.
        return {
          mode: this._prepareMode(e?.historyStatus?.status),
          createdBy: `${message?.eventHistory?.by} ${
            e?.fullName ? e?.fullName : e?.createdBy
          }`,
          status: this._prepareStatusTitle(e?.historyStatus?.status, message),
          dateStatus: dateLabel,
          countComment: commentedDocument,
          validatedOrRefused: this.checkStatusIsRefusedOrValidate(e?.historyStatus?.status)
        };
      } else { // return not have createdBy property.
        return {
          mode: this._prepareMode(e?.historyStatus?.status),
          status: this._prepareStatusTitle(e?.historyStatus?.status, message),
          dateStatus: dateLabel,
          countComment: commentedDocument,
          validatedOrRefused: this.checkStatusIsRefusedOrValidate(e?.historyStatus?.status)
        };
      }
    });
    // If status is not finished status, add next status.
    if (finaleStatus?.length === 0) {
      const lastStatus = events[events.length - 1]?.historyStatus?.status;
      eventHistories = [
        ...eventHistories,
        this._prepareNextStatus(lastStatus, message)
      ];
    }
    return eventHistories;
  }

  private _prepareMode(status: string) {
    switch (status.toLowerCase()) {
      case FlowStatusConstant.PROCESSED.toLowerCase():
        return EventHistoryType.SUCCESS;
      case FlowStatusConstant.IN_ERROR.toLowerCase():
        return EventHistoryType.DANGER;
      default:
        return EventHistoryType.INFO;
    }
  }

  private _prepareNextStatus(status: string, message: any): EventHistory {
    switch (status.toLowerCase()) {
      case FlowStatusConstant.DEPOSITED.toLowerCase():
        return { mode: EventHistoryType.DISABLED, status: message?.status?.scheduled };
      case FlowStatusConstant.TO_FINALIZED.toLowerCase():
        return { mode: EventHistoryType.DISABLED, status: message?.status?.finalized };
      case FlowStatusConstant.FINALIZED.toLowerCase():
      case FlowStatusConstant.TO_VALIDATE.toLowerCase():
        return { mode: EventHistoryType.DISABLED, status: message?.status?.validated};
      case FlowStatusConstant.VALIDATED.toLowerCase():
        return { mode: EventHistoryType.DISABLED, status: message?.status?.scheduled};
      case FlowStatusConstant.SCHEDULED.toLowerCase():
      case FlowStatusConstant.TREATMENT.toLowerCase():
        return { mode: EventHistoryType.DISABLED, status: message?.status?.in_process };
      case FlowStatusConstant.IN_PROCESS.toLowerCase():
        return { mode: EventHistoryType.DISABLED, status: message?.status?.completed };
      default:
        return { mode: EventHistoryType.DISABLED, status: message?.status?.completed };
    }
  }

  private _prepareStatusTitle(status: string, message: any) {
    switch (status.toLowerCase()) {
      case FlowStatusConstant.DEPOSITED.toLowerCase():
        return message?.status?.deposited;
      case FlowStatusConstant.IN_ERROR.toLowerCase():
        return message?.status?.in_error;
      case FlowStatusConstant.CANCELED.toLowerCase():
        return message?.status?.canceled;
      case FlowStatusConstant.TO_FINALIZED.toLowerCase():
        return message?.status?.to_finalize;
      case FlowStatusConstant.FINALIZED.toLowerCase():
        return message?.status?.finalized;
      case FlowStatusConstant.SCHEDULED.toLowerCase():
        return message?.status?.scheduled;
      case FlowStatusConstant.IN_PROCESSING.toLowerCase():
        return message?.status?.in_process;
      case FlowStatusConstant.PROCESSED.toLowerCase():
        return message?.status?.completed;
      case FlowStatusConstant.TO_VALIDATE.toLowerCase():
        return message?.status?.to_validate;
      case FlowStatusConstant.VALIDATED.toLowerCase():
        return message?.status?.validated;
      case FlowStatusConstant.REFUSED.toLowerCase():
        return message?.status?.refused;
      case FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase():
        return message?.status?.refuse_document;
      case FlowStatusConstant.TREATMENT.toLowerCase():
        return message?.status?.treatment;
      default:
        return;
    }
  }

  private _getFlowInfo(flowInfo: any, message: any): FlowInfo {
    // Prepare state for filling flow-info component.
    this.fileName = flowInfo?.flowName;
    let dateLabel = '';
    if (flowInfo?.createdAt) {
      const dateTime = formatDate.formatDateTime(
        flowInfo?.createdAt,
        FULL_DATE_TIME_FORMAT
      );

      dateLabel = `${dateTime.split(' ')[0]} ${message?.history?.eventHistory?.at} ${dateTime.split(' ')[1]}`;
    }

    const getMode = (status: string) => {
      switch (status?.toLowerCase()) {
        case FlowStatusConstant.DEPOSITED.toLowerCase():
          return message?.traceability?.status?.deposited;
        case FlowStatusConstant.FINALIZED.toLowerCase():
          return message?.traceability?.status?.finalized;
        case FlowStatusConstant.SCHEDULED.toLowerCase():
          return message?.traceability?.status?.scheduled;
        case FlowStatusConstant.TO_FINALIZED.toLowerCase():
          return message?.traceability?.status?.to_finalize;
        case FlowStatusConstant.IN_PROCESS.toLowerCase():
          return message?.traceability?.status?.in_process;
        case FlowStatusConstant.PROCESSED.toLowerCase():
          return message?.traceability?.status?.completed;
        case FlowStatusConstant.COMPLETED.toLowerCase():
          return message?.traceability?.status?.completed;
        case FlowStatusConstant.IN_ERROR.toLowerCase():
          return message?.traceability?.status?.in_error;
        case FlowStatusConstant.CANCELED.toLowerCase():
          return message?.traceability?.status?.canceled;
        case FlowStatusConstant.TO_VALIDATE.toLowerCase():
          return message?.traceability?.status?.to_validate;
        case FlowStatusConstant.VALIDATED.toLowerCase():
          return message?.traceability?.status?.validated;
        case FlowStatusConstant.REFUSED.toLowerCase():
          return message?.traceability?.status?.refused;
        case FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase():
          return message?.traceability?.status?.refuse_document;
        case FlowStatusConstant.TREATMENT.toLowerCase():
          return message?.traceability?.status?.treatment;
        default:
          return '';
      }
    };

    return {
      mode: this._getFlowDepositMode(
        flowInfo?.flowDepositMode?.status,
        message
      ),
      flowType: flowInfo?.channel,
      category: flowInfo?.subChannel,
      dateStatus: dateLabel,
      user: flowInfo?.fullName,
      service: flowInfo?.service,
      status: getMode(flowInfo?.status),
      statusStyle: flowInfo?.status,
      unloadingDate: flowInfo?.unloadingDateLabel,
      showUnloadingDate: flowInfo?.showUnloadingDate,
      modelName: flowInfo?.modelName,
      division: flowInfo?.division,
    };
  }

  private _getFlowDepositMode(status: string, message: any): string {
    switch (status?.toLowerCase()) {
      case DepositMode.PORTAL.toLowerCase():
        return message?.traceability?.deposit?.mode?.portal;
      case DepositMode.BATCH.toLowerCase():
        return message?.traceability?.deposit?.mode?.batch;
      case DepositMode.IV.toLowerCase():
        return message?.traceability?.deposit?.mode?.virtualPrinter;
      default:
        return '';
    }
  }


  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Finalize the flow from draft
   * @param flow
   */
  finalizeFlow(flow: any) {
    // Finalize flow.
    if (!this.flow?.privilege?.canModify) return;
    this._translate
      .get('flow.history.confirmFinalizeDialog')
      .toPromise()
      .then((messages) => {
        this._store.dispatch(
          confirmFinalizeFlowFromFlowHistoryDetail({
            confirmMessage: { flow, messages }
          })
        );
      });
  }

  /**
   * Download flow document
   */
  downloadFeed() {
    // Download document.
    if (!this.flow?.privilege?.canDownload) return;
    this._store.dispatch(
      downloadFlowTraceabilityDepositDocument({ flowDeposit: this.flow, _type: FlowType.FLOW})
    );
  }

  /**
   * Cancel flow
   * @param flow
   */
  cancelFlow(flow: any) {
    // Cancel flow.
    if (!this.flow?.privilege?.canCancel) return;
    this._translate
      .get('flow.history.confirmCancelDialog')
      .toPromise()
      .then((messages) => {
        const confirmMessage = {
          icon: 'error',
          messages,
          flowId: flow?.id,
          flowName: flow?.flowName,
          fileId: flow?.fileId,
          createdBy: flow?.createdBy,
          depositMode: flow?.depositMode,
          ownerId: flow?.ownerId
        };
        this._store.dispatch(confirmCancelDepositFlow({ confirmMessage }));
      });
  }

  /**
   * Redirect to list
   */
  backToList() {
    // Navigate to list of flow.
    this._store.dispatch(backToListOfFlowTraceability());
  }

  /**
   * View document detail
   * @param flow
   */
  viewFlowListDocuments(flow: any) {
    // view flow list documents.
    if (!this.flow?.privilege?.canViewDetail) return;
    this._store.dispatch(navigateToFlowListDocument({ flow }));
  }

  checkStatusIsRefusedOrValidate(status: string): boolean {
    return status.toLowerCase() === FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase() || status.toLowerCase() == FlowStatusConstant.VALIDATED.toLowerCase();
  }
}
