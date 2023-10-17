import {Participant} from '@/constant/NGContant';
import {TypeValidatePassword} from '@/utils/ValidatePersonalPassword';
import {TextFieldProps} from '@mui/material';
import {SxProps} from '@mui/system';
import React, {CSSProperties, ReactNode} from 'react';

type TypeParticipant = Participant;

export type IRecipient = {
  lastName?: string;
  firstName?: string;
  role?: TypeParticipant;
  email?: string;
  phone?: string;
  id?: number | string;
  invitationStatus?: string;
  documentStatus?: string;
  sortOrder?: number;
  projectId?: number | string;
  checked?: boolean;
};

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
  textFieldProps?: TextFieldProps;
  passwordLength?:number
}
export interface TypeSelect {
  styleSelect?: CSSProperties;
  name: string;
  setName: React.Dispatch<React.SetStateAction<string>>;
  data: IRecipient[];
  label: string;
}
