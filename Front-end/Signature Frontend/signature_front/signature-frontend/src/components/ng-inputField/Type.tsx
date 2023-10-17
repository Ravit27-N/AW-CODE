import React, {CSSProperties, ReactNode} from 'react';
import {TypeValidatePassword} from '@/utils/ValidatePersonalPassword';
import {SxProps} from '@mui/system';
import {IRecipient} from '@pages/form/process-upload/type';

export interface Type {
  id?: string;
  textLabelRight?: ReactNode;
  Icon?: ReactNode;
  textLabel?: string | ReactNode;
  setValue: any;
  value: string;
  nameId: string | 'optional' | 'PP';
  type?: React.InputHTMLAttributes<unknown>['type'];
  placeholder: string;
  require?: boolean;
  state?: TypeValidatePassword;
  rows?: number;
  limitLength?: number;
  messageError?: string;
  color?: string;
  colorOutline?: string;
  fontSizeLabel?: number;
  colorOnfocus?:
    | 'success'
    | 'error'
    | 'primary'
    | 'secondary'
    | 'info'
    | 'warning';
  propsInput?: SxProps;
  autoFocus?: boolean;
  size?: 'small' | 'medium';
  passwordLength?: number;
}
export interface TypeSelect {
  styleSelect?: CSSProperties;
  name: string;
  setName: React.Dispatch<React.SetStateAction<string>>;
  data: IRecipient[];
  label: string;
}
