export enum FlowStatusConstant {
  DEPOSITED = 'Deposited',
  TO_FINALIZED = 'To finalize',
  FINALIZED = 'Finalized',
  SCHEDULED = 'Scheduled',
  IN_PROCESSING = 'In processing',
  IN_PROCESS = 'In process',
  PROCESSED = 'Processed',
  CANCELED = 'Canceled',
  IN_ERROR = 'In error',
  COMPLETED = 'Completed',
  TO_VALIDATE = 'To validate',
  VALIDATED = 'Validated',
  REFUSED = 'Refused',
  REFUSED_DOCUMENT = 'Refuse document',
  TREATMENT = 'Treatment',
  IN_PROGRESS = 'In progress',
}

export enum EventHistoryType {
  INFO = 'info',
  SUCCESS = 'success',
  SECONDARY = 'secondary',
  DANGER = 'danger',
  DISABLED = 'disabled',
  CONTINUED = 'continued',
}

export enum EventStatusType {
  INFO = 'info',
  SUCCESS = 'success',
  SECONDARY = 'secondary',
  DANGER = 'danger',
  PRIMARY = 'primary',
}

export enum EventHistorySize {
  SMALL = 'small',
  MEDIUM = 'medium',
}

export enum EventModeType {
  INFO = 'info',
  SUCCESS = 'success',
  DANGER = 'danger',
  SECONDARY = 'secondary',
  PRIMARY = 'primary',
  GRAY = 'gray',
}

export enum DepositMode {
  PORTAL = 'Portal',
  BATCH = 'Batch',
  API = 'API',
  IV = 'IV',
}

export enum FlowTypeMode {
  POSTAL = 'Postal',
  DIGITAL = 'Digital',
}

export enum SendingSubChannel {
  RECO = 'Reco',
  RECO_AR = 'Reco AR',
  CSE = 'CSE',
  LRE = 'LRE',
  EMAIL = 'email',
  SMS = 'sms',
  CSE_AR = 'CSE AR',
}

export enum FlowDocumentStatus {
  SCHEDULED = 'Scheduled',
  COMPLETED = 'Completed',
  IN_ERROR = 'In error',
  IN_PROGRESS = 'In progress',
  CANCELED = 'Canceled',
  IN_PRODUCTION = 'In production',
  MAIL_DEPOSIT = 'Mail deposit',
  DEPOSITED = 'Deposited',
  DISTRIBUTED = 'Distributed',
  REFUSED = 'Refused',
  RECEIVED = 'Received',
  ACCEPTED = 'Accepted',
  CERTIFIED = 'Certified',
  NOTIFIED = 'Notified',
  READ = 'Read',
  CLICKED = 'Clicked',
  POSTAL_RECOVERY = 'Postal recovery',
  SENT = 'Sent',
  UNCLAIMED = 'Unclaimed',
  NPAI = 'NPAI',
  STAMPED = 'Stamped',
  SOFT_BOUNCE = 'Soft bounce',
  HARD_BOUNCE = 'Hard bounce',
  OPENED = 'Opened',
  BLOCKED = 'Blocked',
  RESENT = 'Resent',
  REFUSED_DOC = 'Refuse document',
  ACCESS_OR_ADDRESSING_FAILURE = 'Access or addressing failure',
  RECIPIENT_UNKNOWN = 'Recipient Unknown',
  UNKNOWN = 'Unknown',
  ADDRESSING_FAILURE = 'Address_failure',
}

export enum AssociateDocumentType {
  ACCUSE_RECEPTION = 'Accuse Reception',
  SLIP_SHEET = 'Slip Sheet',
  DOCUMENT = 'Document',
}

export const DocumentNotificationStatus = [
  FlowDocumentStatus.STAMPED.toLowerCase(),
  FlowDocumentStatus.NPAI.toLowerCase(),
  FlowDocumentStatus.UNCLAIMED.toLowerCase(),
  FlowDocumentStatus.DISTRIBUTED.toLowerCase(),
  FlowDocumentStatus.REFUSED.toLowerCase(),
  FlowDocumentStatus.SOFT_BOUNCE.toLowerCase(),
  FlowDocumentStatus.HARD_BOUNCE.toLowerCase(),
  FlowDocumentStatus.OPENED.toLowerCase(),
];

// view unit shipment or flow document.
export const canViewUnitShipmentInStatuses = [
  FlowStatusConstant.TO_VALIDATE.toLowerCase(), // A valider
  FlowStatusConstant.REFUSED.toLowerCase(), // Refusé
  FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase(), // Refusé
  FlowStatusConstant.SCHEDULED.toLowerCase(), // Planifié,
  FlowStatusConstant.IN_PROCESS.toLowerCase(), //  En cours
  FlowStatusConstant.COMPLETED.toLowerCase(), // Terminé
  FlowStatusConstant.IN_ERROR.toLowerCase(), //  Erreur
  FlowStatusConstant.CANCELED.toLowerCase(), // Annulé
];

// cancel flow traceability.
export const canCancelFlowTraceabilityInStatus = [
  FlowStatusConstant.DEPOSITED.toLowerCase(),
  FlowStatusConstant.SCHEDULED.toLowerCase(),
  FlowStatusConstant.TO_FINALIZED.toLowerCase(),
];

// Download file of flow traceability.
export const canDownloadFileInStatuses = [
  FlowStatusConstant.TREATMENT.toLowerCase(), // Traitement
  FlowStatusConstant.TO_VALIDATE.toLowerCase(), // A valider
  FlowStatusConstant.REFUSED.toLowerCase(), // Refusé
  FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase(), // Refusé document
  FlowStatusConstant.SCHEDULED.toLowerCase(), // Planifié,
  FlowStatusConstant.IN_PROCESS.toLowerCase(), // En cours
  FlowStatusConstant.COMPLETED.toLowerCase(), // Terminé
  FlowStatusConstant.IN_ERROR.toLowerCase(), //  Erreur
  FlowStatusConstant.CANCELED.toLowerCase(), // Annulé
];

// Finalized flow traceability.
export const canFinalizedFlow = [FlowStatusConstant.TO_FINALIZED.toLowerCase()];

// Cancel flow traceability.
export const canCancelFlowTraceability = [
  FlowStatusConstant.TO_FINALIZED.toLowerCase(),
  FlowStatusConstant.SCHEDULED.toLowerCase(),
];

// Download file of flow document postal.
export const canDownloadFileOfFlowDocumentPostalInStatuses = [
  FlowStatusConstant.TO_VALIDATE.toLowerCase(), // A valider
  FlowStatusConstant.REFUSED.toLowerCase(), // Refusé
  FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase(), // Refusé document
  FlowStatusConstant.SCHEDULED.toLowerCase(), // Planifié,
  FlowStatusConstant.IN_PROGRESS.toLowerCase(), // En cours

  // Terminé
  FlowStatusConstant.COMPLETED.toLowerCase(),
  FlowStatusConstant.DEPOSITED.toLowerCase(),
  FlowDocumentStatus.STAMPED.toLowerCase(),
  FlowDocumentStatus.NPAI.toLowerCase(),
  FlowDocumentStatus.UNCLAIMED.toLowerCase(),
  FlowDocumentStatus.DISTRIBUTED.toLowerCase(),
  FlowDocumentStatus.REFUSED.toLowerCase(),
  FlowDocumentStatus.ACCESS_OR_ADDRESSING_FAILURE.toLowerCase(),
  FlowDocumentStatus.RECIPIENT_UNKNOWN.toLowerCase(),
  // end

  FlowStatusConstant.IN_ERROR.toLowerCase(), //  Erreur
  FlowStatusConstant.CANCELED.toLowerCase(), // Annulé
];

// Download file of flow document digital.
export const canDownloadFileOfFlowDocumentDigitalInStatuses = [
  FlowStatusConstant.TO_VALIDATE.toLowerCase(), // A valider
  FlowStatusConstant.REFUSED.toLowerCase(), // Refusé
  FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase(), // Refusé document
  FlowStatusConstant.SCHEDULED.toLowerCase(), // Planifié,
  FlowStatusConstant.IN_PROGRESS.toLowerCase(), // En cours

  // Terminé
  FlowStatusConstant.COMPLETED.toLowerCase(),
  FlowDocumentStatus.SOFT_BOUNCE.toLowerCase(),
  FlowDocumentStatus.HARD_BOUNCE.toLowerCase(),
  FlowDocumentStatus.SENT.toLowerCase(),
  FlowDocumentStatus.OPENED.toLowerCase(),
  FlowDocumentStatus.CLICKED.toLowerCase(),
  FlowDocumentStatus.BLOCKED.toLowerCase(),
  // end

  FlowStatusConstant.IN_ERROR.toLowerCase(), //  Erreur
  FlowStatusConstant.CANCELED.toLowerCase(), // Annulé
];
