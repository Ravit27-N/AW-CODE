export interface MetadataModel {
  id: number;
  value: string;
  order: number;
}

export interface MetadataRequestModel {
  customer: string;
  type: MetadataPayloadType;
  metadata: Array<MetadataModel>;
}

export interface MetadataResponseModel {
  customer: string;
  senderMail: Array<MetadataModel>;
  senderName: Array<MetadataModel>;
  unsubscribeLink: Array<MetadataModel>;
  smsSenderLabel: Array<MetadataModel>;
}

export interface HubAccountDto {
  client: string;
  username: string;
  password: string;
}

export interface HubAccountResponse {
  client: string;
  username: string;
}

export declare type MetadataType = 'senderNameMetadata' | 'senderEmailMetadata' | 'unsubscribeMetadata' | 'senderLabelMetadata';
export declare type MetadataPayloadType = 'sender_mail' | 'sender_name' | 'unsubscribe_link' | 'sender_label';
