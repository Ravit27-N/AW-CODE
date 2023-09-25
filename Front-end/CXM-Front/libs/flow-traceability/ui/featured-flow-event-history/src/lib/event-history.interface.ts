/** Options used to set Event history properties. */
export interface EventHistory {
  status?: string;
  createdBy?: string;
  dateStatus?: string;
  size?: 'small' | 'medium';
  mode: 'info' | 'success' | 'secondary' | 'danger' | 'disabled' | 'continued';
  eventHistoryInfo?: EventHistoryInfo;
  comment?: string;
  countComment?: number;
  flowCommentStatus?:boolean;
  validatedOrRefused?:boolean;
}

/** Options used to set Event history info properties. (Question mark icon)*/
export interface EventHistoryInfo {
  statuses: string[];
  description: string;
}
