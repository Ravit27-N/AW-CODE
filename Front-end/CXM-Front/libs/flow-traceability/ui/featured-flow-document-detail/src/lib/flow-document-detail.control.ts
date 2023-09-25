import {
  EventModeType,
  FlowDocumentStatus,
} from '@cxm-smartflow/flow-traceability/util';
import { RecipientModel } from './recipient/recipient.component';
import { FlowDocumentDetail } from './document-detail/document-detail.component';
import { formatDate } from '@cxm-smartflow/shared/utils';
import { FULL_DATE_TIME_FORMAT } from '@cxm-smartflow/shared/data-access/model';
import { EventHistoryInfo } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';
import { EventHistoryStatusType } from './flow-document-detail.interface';

export class FlowDocumentDetailControl {

  /**
   * Prepare display event history style base on its mode.
   * @param status
   */
  static getMode(status: string) {
    switch (status.toLowerCase()) {
      case FlowDocumentStatus.CANCELED.toLowerCase():
      case FlowDocumentStatus.IN_ERROR.toLowerCase(): return EventModeType.DANGER;
      case FlowDocumentStatus.COMPLETED.toLowerCase():
      case FlowDocumentStatus.SENT.toLowerCase():
      case FlowDocumentStatus.STAMPED.toLowerCase(): return EventModeType.INFO;
      case FlowDocumentStatus.REFUSED.toLowerCase(): return EventModeType.SUCCESS;
      case FlowDocumentStatus.NPAI.toLowerCase(): return EventModeType.SUCCESS;
      case FlowDocumentStatus.UNCLAIMED.toLowerCase(): return EventModeType.SUCCESS;
      case FlowDocumentStatus.ADDRESSING_FAILURE.toLowerCase(): return EventModeType.SUCCESS;
      case FlowDocumentStatus.UNKNOWN.toLowerCase(): return EventModeType.SUCCESS;
      default: return EventModeType.INFO;
    }
  }

  /**
   * Prepare data for recipient data.
   * @param flowDocument
   */
  static getRecipient(flowDocument: any, channel: string): RecipientModel {
    const addresses: string[] = flowDocument?.address?.split(",")?.map((e: string) => e.trim()).filter((e: string) => e);
    return {
      address: addresses,
      email: flowDocument?.email,
      telephone: flowDocument?.telephone,
      channel: channel,
      subChannel: flowDocument?.subChannel
    };
  }

  static getDocumentDetail(flowDocument: any, messages: any): FlowDocumentDetail {
    let unloadingDate = '';

    if (flowDocument?.unloadingDate) {
      unloadingDate = formatDate.formatDateTime(flowDocument?.unloadingDate, FULL_DATE_TIME_FORMAT);

      if (flowDocument?.status === FlowDocumentStatus.SCHEDULED) {
        unloadingDate = `${unloadingDate} (${messages?.status?.scheduled?.replace('<span>', '')?.replace('</span>', '')})`;
      }
    }


    return {
      channel: flowDocument.channel,
      document: {
        identify: flowDocument.document,
        pageNumber: flowDocument?.pageNumber,
        createdDate: flowDocument?.createdAt? formatDate.formatDateTime(
          flowDocument?.createdAt,
          FULL_DATE_TIME_FORMAT
        ): '',
        fileSize: flowDocument?.fileSize,
        modelName: flowDocument.details.docName.endsWith('.pdf')? flowDocument?.details?.modelName : undefined,
        sheetNumber: flowDocument?.sheetNumber,
        unloadingDate,
        status: flowDocument.status,
        channel: flowDocument.channel
      },
      productionCriteria: {
        postage: flowDocument?.details?.productCriteria?.postage,
        envelope: flowDocument?.details?.productCriteria?.envelope,
        color: flowDocument?.details?.productCriteria?.color,
        printing: flowDocument?.details?.productCriteria?.impression
      },
      dataExtraction: {
        fillers: flowDocument?.details?.fillers,
        reference: flowDocument?.details?.reference
      },
      enrichment:flowDocument.details?.enrichment
    };
  }

  static getStatusInfo(status: EventHistoryStatusType): EventHistoryInfo | undefined {
    const statuses: EventHistoryStatusType[] = [
      'flow.document.status.blocked',
      'flow.document.status.hard_bounce',
      'flow.document.status.soft_bounce',
      'flow.document.status.in_error',
    ];

    return statuses.includes(status)? {
      statuses: [],
      description: ''
    }: undefined;
  }

}
