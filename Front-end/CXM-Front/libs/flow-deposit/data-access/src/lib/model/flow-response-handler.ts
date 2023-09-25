import {boolean} from "@storybook/addon-knobs";

export interface FlowResponseHandler<T> {
  message: string,
  status: number,
  timestamp: Date,
  data: T
}

export interface ModifiedFlowDocumentAddress {
  docId?: string,
  modified?: boolean,
  address?:string
}
