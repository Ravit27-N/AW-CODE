import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { pluck, take, takeUntil } from 'rxjs/operators';
import { InformationBoard } from './information-board/information-board.component';
import { Store } from '@ngrx/store';
import {
  backToListOfFlowTraceability,
  confirmCancelDepositFlow,
  flowCampaignDetail,
  flowTraceabilityCampaignDetailSelector,
  navigateToFlowListDocument,
  unloadDocumentTraceabilityList
} from '@cxm-smartflow/flow-traceability/data-access';
import { Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { formatDate } from '@cxm-smartflow/shared/utils';
import { FULL_DATE_TIME_NO_SECOND_FORMAT } from '@cxm-smartflow/shared/data-access/model';
import { CanModificationService, CanVisibilityService } from '@cxm-smartflow/shared/data-access/services';
import { EventStatusType, FlowStatusConstant } from '@cxm-smartflow/flow-traceability/util';
import { CampaignRate } from './campaign-rate/campaign-rate.component';
import { DeliverabilityRate } from './deliverability-rate/deliverability-rate.component';

@Component({
  selector: 'cxm-smartflow-featured-flow-digital-detail',
  templateUrl: './featured-flow-digital-detail.component.html',
  styleUrls: ['./featured-flow-digital-detail.component.scss']
})
export class FeaturedFlowDigitalDetailComponent implements OnInit, OnDestroy {

  // Validation properties
  isViewFlowDocumentButtonVisible = false;
  isCancelFlowButtonVisible = false;
  isDeliveryStatisticComponentVisible = false;
  exportFileButtonVisible = false;
  isNonOperationButtonVisible = false;
  campaignType = 'EMAIL';

  // State properties.
  fileName = '';
  informationBoard: InformationBoard;
  campaignRate: CampaignRate;
  deliverabilityRate: DeliverabilityRate;
  flow: any;

  // Unsubscribe properties.
  destroy$ = new Subject<boolean>();

  constructor(private activatedRoute: ActivatedRoute,
              private canVisibilityService: CanVisibilityService,
              private canModificationService: CanModificationService,
              private store: Store,
              private translateService: TranslateService) {
  }

  ngOnInit(): void {
    // Load flow detail.
    this.activatedRoute.queryParams.pipe(take(1), pluck('id')).subscribe(v => {
      if (v) this.store.dispatch(flowCampaignDetail({ flowId: v }));
    });

    // Subscribe flow traceability detail.
    this.store.select(flowTraceabilityCampaignDetailSelector).pipe(takeUntil(this.destroy$)).subscribe(flow => {
      if (Object.keys(flow).length > 0) {
        this.translateService.get('flow.history').toPromise().then(messages => {
          this.flow = flow;
          this.campaignType = flow?.detail?.campaignType;
          this.validateButton(flow);
          this.preparePageHeader(flow);
          this.prepareInformationBoard(flow, messages);
          this.prepareDeliverabilityRate(flow);
          this.prepareCampaignRate(flow, messages);
        });
      }
    });
  }

  ngOnDestroy(): void {
    // Clear old state.
    this.destroy$.next(true);
  }

  preparePageHeader(flow: any): void {
    // Prepare page header.
    this.fileName = flow?.detail?.campaignName;
  }

  prepareInformationBoard(flow: any, messages: any): void {
    // prepare information board.
    const createdDate = formatDate.formatDateTime(flow?.createdAt, FULL_DATE_TIME_NO_SECOND_FORMAT);
    const createdDataLabel = `${createdDate.split(' ')[0]} ${messages?.eventHistory?.at} ${createdDate.split(' ')[1]}`;

    const dateStatus = formatDate.formatDateTime(flow?.dateStatus, FULL_DATE_TIME_NO_SECOND_FORMAT);
    const dateStatusLabel = `${dateStatus.split(' ')[0]} ${messages?.eventHistory?.at} ${dateStatus.split(' ')[1]}`;

    const getMode = (status: string): 'success' | 'danger' | 'info' | 'secondary' | 'primary' => {
      switch (status) {
        case FlowStatusConstant.DEPOSITED:
        case FlowStatusConstant.FINALIZED:
        case FlowStatusConstant.SCHEDULED:
        case FlowStatusConstant.TREATMENT:
        case FlowStatusConstant.TO_FINALIZED:
          return EventStatusType.INFO;
        case FlowStatusConstant.IN_PROCESS:
          return EventStatusType.PRIMARY;
        case FlowStatusConstant.PROCESSED:
        case FlowStatusConstant.COMPLETED:
          return EventStatusType.SUCCESS;
        case FlowStatusConstant.IN_ERROR:
          return EventStatusType.DANGER;
        case FlowStatusConstant.CANCELED:
          return EventStatusType.SECONDARY;
        default:
          return EventStatusType.INFO;
      }
    };

    const getStatus = (status: string): string => {
      switch (status) {
        case FlowStatusConstant.DEPOSITED:
          return messages?.campaignStatus?.deposited;
        case FlowStatusConstant.FINALIZED:
          return messages?.campaignStatus?.finalized;
        case FlowStatusConstant.SCHEDULED:
          return messages?.campaignStatus?.scheduled;
        case FlowStatusConstant.TO_FINALIZED:
          return messages?.campaignStatus?.to_finalize;
        case FlowStatusConstant.IN_PROCESS:
          return messages?.campaignStatus?.in_process;
        case FlowStatusConstant.PROCESSED:
          return messages?.campaignStatus?.completed;
        case FlowStatusConstant.COMPLETED:
          return messages?.campaignStatus?.completed;
        case FlowStatusConstant.IN_ERROR:
          return messages?.campaignStatus?.in_error;
        case FlowStatusConstant.CANCELED:
          return messages?.campaignStatus?.canceled;
        case FlowStatusConstant.TREATMENT:
          return messages?.campaignStatus?.treatment;
        default:
          return '';
      }
    };

    this.informationBoard = {
      createdDate: createdDataLabel,
      createdBy: flow?.fullName,
      type: getMode(flow?.flowStatus?.status),
      sentDate: dateStatusLabel,
      status: getStatus(flow?.flowStatus?.status),
      service: flow?.service,
      division: flow?.division
    };
  }

  prepareCampaignRate(flow: any, messages: any) {
    // Prepare data for display campaign rate component.
    this.campaignRate = {
      htmlTemplate: flow?.detail?.htmlTemplate,
      rates: {
        open: {
          percentage: this.isDeliveryStatisticComponentVisible
            ? flow?.detail?.openedPercentage % 1 === 0
              ? flow?.detail?.openedPercentage
              : flow?.detail?.openedPercentage?.toFixed(2)
            : 0,
          messages: this.isDeliveryStatisticComponentVisible
            ? `${flow?.detail?.totalOpened} ${messages?.eventHistory?.outOf} ${flow?.detail?.totalRecord}`
            : '',
        },
        clicked: {
          percentage: this.isDeliveryStatisticComponentVisible
            ? flow?.detail?.clickedPercentage % 1 === 0
              ? flow?.detail?.clickedPercentage
              : flow?.detail?.clickedPercentage?.toFixed(2)
            : 0,
          messages: this.isDeliveryStatisticComponentVisible
            ? `${flow?.detail?.totalClicked} ${messages?.eventHistory?.outOf} ${flow?.detail?.totalRecord}`
            : '',
        },
        error: {
          percentage: this.isDeliveryStatisticComponentVisible
            ? flow?.detail?.errorPercentage % 1 === 0
              ? flow?.detail?.errorPercentage
              : flow?.detail?.errorPercentage?.toFixed(2)
            : 0,
          messages: this.isDeliveryStatisticComponentVisible
            ? `${flow?.detail?.totalError} ${messages?.eventHistory?.outOf} ${flow?.detail?.totalRecord}`
            : '',
        },
        bound: {
          percentage: this.isDeliveryStatisticComponentVisible
            ? flow?.detail?.bouncePercentage % 1 === 0
              ? flow?.detail?.bouncePercentage
              : flow?.detail?.bouncePercentage?.toFixed(2)
            : 0,
          messages: this.isDeliveryStatisticComponentVisible
            ? `${flow?.detail?.totalBounce} ${messages?.eventHistory?.outOf} ${flow?.detail?.totalRecord}`
            : '',
        },
        delivered: {
          percentage: this.isDeliveryStatisticComponentVisible
            ? flow?.detail?.deliveredPercentage % 1 === 0
              ? flow?.detail?.deliveredPercentage
              : flow?.detail?.deliveredPercentage?.toFixed(2)
            : 0,
          messages: this.isDeliveryStatisticComponentVisible
            ? `${flow?.detail?.totalDelivered} ${messages?.eventHistory?.outOf} ${flow?.detail?.totalRecord}`
            : '',
        },
        cancel: {
          percentage: this.isDeliveryStatisticComponentVisible
            ? flow?.detail?.canceledPercentage % 1 === 0
              ? flow?.detail?.canceledPercentage
              : flow?.detail?.canceledPercentage?.toFixed(2)
            : 0,
          messages: this.isDeliveryStatisticComponentVisible
            ? `${flow?.detail?.totalCanceled} ${messages?.eventHistory?.outOf} ${flow?.detail?.totalRecord}`
            : '',
        },
      },
      type: flow?.detail?.campaignType?.toLowerCase() || 'email',
      exportFileButtonVisible: this.exportFileButtonVisible,
    };
  }

  validateButton(flow: any): void {
    const {
      canViewDetail,
      canCancel,
      isDeliveryStatisticComponentVisible,
      exportFileButtonVisible,
      isNonOperationButtonVisible
    } = flow?.privilege;

    this.isViewFlowDocumentButtonVisible = canViewDetail;
    this.isCancelFlowButtonVisible = canCancel;
    this.isDeliveryStatisticComponentVisible = isDeliveryStatisticComponentVisible;
    this.exportFileButtonVisible = exportFileButtonVisible;
    this.isNonOperationButtonVisible = isNonOperationButtonVisible;
  }

  backToFlow() {
    // Navigate to list of flow.
    this.store.dispatch(backToListOfFlowTraceability());
  }

  viewDocument(flow: any) {
    // View list flow document.
    if (!this.isViewFlowDocumentButtonVisible) return;
    this.store.dispatch(unloadDocumentTraceabilityList());
    this.store.dispatch(navigateToFlowListDocument({ flow }));
  }

  async cancelFlow(flow: any) {// TODO: Test cancel flow

    // Cancel flow.
    if (!this.isCancelFlowButtonVisible) return;
    const { fileId } = await this.activatedRoute.queryParams.pipe(take(1)).toPromise();
    this.translateService.get('flow.history.confirmCancelCampaignDialog').toPromise().then(messages => {
      const confirmMessage = { icon: 'error', messages, flowId: flow?.id, flowName: flow?.detail?.campaignName, ownerId: flow?.ownerId, fileId };
      this.store.dispatch(confirmCancelDepositFlow({ confirmMessage }));
    });
  }

  prepareDeliverabilityRate(flow: any) {
    // Prepare data for display in deliver ability rate component.
    this.deliverabilityRate = {
      rates: {
        block: this.calcPercentage(flow?.detail?.blockedPercentage),
        dismissal: this.calcPercentage(flow?.detail?.resentPercentage),
        permanentError: this.calcPercentage(flow?.detail?.permanentErrorPercentage),
        temporaryError: this.calcPercentage(flow?.detail?.temporaryErrorPercentage),
        canceled: this.calcPercentage(flow?.detail?.canceledPercentage),
        smsTotalError: this.calcPercentage(flow?.detail?.errorPercentage)
      },
      deliveredPercentage: this.calcPercentage(flow?.detail?.deliveredPercentage),
      totalDeliveredMail: flow?.detail?.totalDelivered,
      totalMail: flow?.detail?.totalRecord,
      type: flow?.detail?.campaignType?.toLowerCase() || 'email',
    };
  }

  calcPercentage(value: any): number {
    return value % 1 === 0 ? value : value?.toFixed(2);
  }
}
