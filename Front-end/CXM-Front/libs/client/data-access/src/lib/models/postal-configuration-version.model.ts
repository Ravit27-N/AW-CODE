export interface PostalConfigurationVersion {

  id: number;
  version: number;
  referenceVersion?: number;
  fileId: string;
  ownerId: number;
  createdBy?: string;
  createdAt: Date;
}
