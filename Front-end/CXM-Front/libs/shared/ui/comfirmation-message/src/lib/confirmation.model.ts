// export enum formType{
//   warning = 'Warning',
//   active = 'Active'
// }

export interface ConfirmationMessage{
  icon?: string;
  heading?: string;
  title?: string;
  message?: string;
  // description?: string;
  importanceWorld?: string;
  importanceWordSuffix?: string;
  paragraph?: string;
  cancelButton?: string;
  cancelButtonColor?: string;
  confirmButton?: string;
  confirmButtonColor?: string;
  type?: 'Warning' | 'Active' | 'Secondary',
  isNoEventBtnCancel?: boolean;
}
