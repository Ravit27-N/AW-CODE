import { ChannelKeyValue, KeyValue } from './flow-key-value.model';

export interface FlowDocumentFilterCriteriaModel {
  sendingChannel?: {
    key: string;
    value: string;
    sendingSubChannel: string[];
  };
  sendingSubChannel?: string[];
  flowDocumentHistoryEvent?: ChannelKeyValue[];
  flowDocumentStatus?: KeyValue[];
  flowDocumentSubStatus?: KeyValue[];
}

export interface FlowDocumentSubChannelModel {
  sendingSubChannel?: string[];
}
