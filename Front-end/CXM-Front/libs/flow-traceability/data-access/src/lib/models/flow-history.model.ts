import { EntityResponseHandler } from "@cxm-smartflow/shared/data-access/model";


export interface FlowHistoryModel {
  id: string;
  event: string;
  createdBy: string;
  service: string;
  server?: string;
  flowTraceabilityId: string;
  historyStatus: {
    statusLabel: string;
    status: string;
  },
  dateTime: string;
}

export type FlowHistoryList = EntityResponseHandler<FlowHistoryModel>;

/* eslint-disable @typescript-eslint/no-explicit-any */
export enum FlowEventName {
  in_error = 'In Error' as any,
  completed = 'Processed' as any,
  to_validate = 'Finalized' as any,
  canceled = 'Canceled' as any,
  in_creation = 'Deposited' as any,
  scheduled = 'Waiting to be Processed' as any,
  in_process = 'In Processing' as any
}
