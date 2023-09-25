export interface Attachment {
  attachment1: string;
  attachment2: string;
  attachment3: string;
  attachment4: string;
  attachment5: string;
}

export interface BackgroundPage {
  background: string;
  backgroundFirst: string;
  backgroundLast: string;
  position: string;
  positionFirst: string;
  positionLast: string;
}

export interface Enrichment {
  attachments: Attachment;
  backgroundPage: BackgroundPage;
  watermark: string;
  signature: any;
}
