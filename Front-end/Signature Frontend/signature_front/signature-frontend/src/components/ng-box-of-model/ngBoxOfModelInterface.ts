import {ReactNode} from 'react';
import {SxProps} from '@mui/material';

export interface TemplateBoxInterface {
  title: string;
  sub1: string | ReactNode;
  sub2?: string | ReactNode;
  textLabelInBox?: string;
  style?: SxProps;
  noActionOnClickPlus?: boolean;
}

export interface TypeContentInterface {
  text: string | ReactNode;
  sizeOfCircle?: number;
  sxCircle?: SxProps;
  textLabel?: ReactNode;
  color?: string;
  hasLabel?: boolean;
}
