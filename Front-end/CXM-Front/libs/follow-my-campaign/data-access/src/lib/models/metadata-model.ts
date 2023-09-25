export interface MetadataModel {
  id: number;
  value: string;
  order: number;
}

export interface MetadataResponseModel {
  customer: string;
  senderMail: Array<MetadataModel>;
  senderName: Array<MetadataModel>;
  unsubscribeLink: Array<MetadataModel>;
  smsSenderLabel: Array<MetadataModel>;
}

export declare type MetadataPayloadType = 'sender_mail' | 'sender_name' | 'unsubscribe_link' | 'sender_label';
