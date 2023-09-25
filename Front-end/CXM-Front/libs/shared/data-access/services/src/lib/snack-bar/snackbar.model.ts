export declare type type = 'success' | 'info' | 'error';
export declare type bgColor = type;

export interface SnackbarModel {
  message?: string;
  icon?: string;
  type?: type;
  details?: string
}
