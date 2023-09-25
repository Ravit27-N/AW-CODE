export interface PrivilegeModel {
  canAccess?: boolean,
  canViewDetail?: boolean,
  canEdit?: boolean,
  canModify?: boolean,
  canDelete?: boolean,
  canDownload?: boolean,
  canCancel?: boolean,
  canShowToggleButton?: boolean,
  canFinalize?: boolean
}

// custom privilege specific components.
export interface FlowDigitalPrivilegeModel extends PrivilegeModel{
  isDeliveryStatisticComponentVisible?: boolean,
  exportFileButtonVisible?: boolean,
  isNonOperationButtonVisible?: boolean
}

// flow document detail privileges.
export interface FlowDocumentDetailPrivilegeModel extends PrivilegeModel{
  canDownloadFeed?: boolean,
  canDownloadAssociate?: boolean
}

// Template privileges.
export interface TemplatePrivilegeModel extends PrivilegeModel{
  canCopy?: boolean;
  canSelect ?: boolean;
  canView?: boolean;
  canCreate?: boolean;
  canList?: boolean;
}
