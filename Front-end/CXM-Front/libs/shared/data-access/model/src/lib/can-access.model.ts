
export enum CheckByLevel{
  modificationLevel= "modificationLevel",
  visibilityLevel = "visibilityLevel"
}

export enum CheckByType{
  service = "service",
  directive = "directive"
}

export class CanAccess {
  protected module: string;
  protected feature: string;
  protected subFeature: string;
  protected right: string;
  protected profile: any;
  protected preferredUsername: string;
  protected createdBy: string;
  protected ownerId: number;
  protected checkByLevel: CheckByLevel;
  protected checkByType: CheckByType;
  protected muteSnackbar: boolean;
  protected validateAdmin: boolean;
}
