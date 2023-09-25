export interface CheckedListKeyValue {
  key: string;
  value: string;
  checked?: boolean;
  other?: any;
}

export interface KeyValue {
  key: string;
  value: string;
}

export type ChannelKeyValue = {
  key: string;
  value: string;
  sendingSubChannel: string[];
};
