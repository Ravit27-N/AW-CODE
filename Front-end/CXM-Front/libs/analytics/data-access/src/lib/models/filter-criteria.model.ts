export interface KeyValue {
  key: string;
  value: string;
}
export interface FilterCriteriaModel {
  channel: KeyValue[];
  depositMode: KeyValue[];
  flowStatus: KeyValue[];
  subChannel: KeyValue[];
}
