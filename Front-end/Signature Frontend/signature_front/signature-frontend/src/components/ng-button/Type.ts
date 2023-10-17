import {ReactNode} from 'react';
import {SxProps} from '@mui/material';
import {OverridableStringUnion} from '@mui/types';
import {
  ButtonProps,
  ButtonPropsColorOverrides,
  ButtonPropsVariantOverrides,
} from '@mui/material/Button/Button';

export interface TypeMyButton {
  disabled?: boolean;
  onClick?: () => void;
  color?: string[] | string;
  title: string | ReactNode;
  myStyle?: SxProps;
  type?: 'button' | 'submit' | 'reset';
  form?: string;
  variant?: OverridableStringUnion<
    'text' | 'outlined' | 'contained',
    ButtonPropsVariantOverrides
  >;
  // bgColor?: OverridableStringUnion<
  //   | 'inherit'
  //   | 'primary'
  //   | 'secondary'
  //   | 'success'
  //   | 'error'
  //   | 'info'
  //   | 'warning'
  //   | 'buttonWhite'
  //   | 'black'
  //   | 'Primary',
  //   ButtonPropsColorOverrides
  // >;
  bgColor?: OverridableStringUnion<
    | 'inherit'
    | 'primary'
    | 'secondary'
    | 'success'
    | 'error'
    | 'info'
    | 'warning',
    ButtonPropsColorOverrides
  >;
  size?: 'small' | 'medium' | 'large';
  icon?: ReactNode;
  locationIcon?: 'start' | 'end';
  fontWeight?:
    | 'bold'
    | 'normal'
    | 'lighter'
    | 'bolder'
    | Omit<'bold' | 'normal' | 'lighter' | 'bolder', number | string>;
  fontSize?:
    | '12px'
    | '24px'
    | '32px'
    | '64px'
    | Omit<'12px' | '24px' | '32px' | '64px', string>;
  borderStyle?: 'dashed' | 'solid' | 'none';
  borderTop?: number;
  borderBottom?: number;
  borderLeft?: number;
  borderRight?: number;
  borderColor?: string;
  textSx?: SxProps;
  btnProps?: ButtonProps;
}
