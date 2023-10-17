import React, {ReactNode} from 'react';
import {SxProps} from '@mui/material';

export interface TypeText {
  text: string;
  input?: ReactNode;
  box?: ReactNode;
  isInput?: boolean;
  secondText?: ReactNode;
  haveUnderline?: boolean;
}
export interface TypeBoxInput {
  borderColorBox?: string;
  radioColor?: string;
  padding?: number;
  isDisable?: boolean;
  haveIcon?: boolean;
  icon?: ReactNode;
  title?: string;

  text?: string | ReactNode;
  checked?: boolean;
  onClick?: React.MouseEventHandler<HTMLDivElement>;
  textStyle?: SxProps;
}
export interface TypeProps {
  props: SxProps;
}
export interface TypeHeaderEmail {
  style?: SxProps;
  option: string;
  position: string;
  email: string;
}
