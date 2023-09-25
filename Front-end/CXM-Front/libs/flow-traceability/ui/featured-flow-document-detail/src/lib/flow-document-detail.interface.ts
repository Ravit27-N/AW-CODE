export interface AssociateDocument {
  id: number;
  fileId: string;
  flowDocumentId: number;
  extension: string;
  elementName: string;
  src: string;
  description?: {
    line1?: string;
    line2?: string;
  };
  element: {
    key: string;
    value: string;
  };
}

export type EventHistoryStatusType =
  'flow.document.status.scheduled' |
  'flow.document.status.completed' |
  'flow.document.status.in_error' |
  'flow.document.status.in_progress' |
  'flow.document.status.canceled' |
  'flow.document.status.in_production' |
  'flow.document.status.deposited' |
  'flow.document.status.distributed' |
  'flow.document.status.refused' |
  'flow.document.status.receipt' |
  'flow.document.status.accepted' |
  'flow.document.status.certified' |
  'flow.document.status.notified' |
  'flow.document.status.read' |
  'flow.document.status.sent' |
  'flow.document.status.claimed' |
  'flow.document.status.clicked' |
  'flow.document.status.postal_pickup' |
  'flow.document.status.received' |
  'flow.document.status.notFound' |
  'flow.document.status.postal_recovery' |
  'flow.document.status.unclaimed' |
  'flow.document.status.mail_deposit' |
  'flow.document.status.npai' |
  'flow.document.status.stamped' |
  'flow.document.status.soft_bounce' |
  'flow.document.status.hard_bounce' |
  'flow.document.status.opened' |
  'flow.document.status.resent' |
  'flow.document.status.blocked' |
  'flow.document.status.sent_list' |
  'flow.document.status.to_validate' |
  'flow.document.status.validated' |
  'flow.document.status.refuse_document';
