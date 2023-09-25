export interface GrapeJs {
  id?: number;
  templateId?: number;
  assets?: any;
  components?: any;
  css?: string;
  html?: string;
  styles?: string;
  htmlFile: string;
  variables?: string[];
  width?: number;
  height?: number;
  createdBy?: string;
  createdAt?: Date;
  lastModifiedBy?: Date;
  lastModified?: Date;
}

export interface GrapeJsAsset {
  assetId?: number;
  createdBy?: string;
  type?: string;
  name?: string;
  src?: string;
  height?: number;
  width?: number;
  unitDim?: string;
  ownerId?: number;
  canModify?: boolean;
}

export interface UploadImageAsset {
  imageUrl: string;
}
