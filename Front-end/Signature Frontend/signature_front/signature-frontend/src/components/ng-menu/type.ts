import {SxProps} from '@mui/material/styles';
import {ReactNode} from 'react';

interface IMenuItem {
  key: string | number;
  NameMenu: ReactNode;
  ValueMenu: string | number;
  IconLeft?: ReactNode;
  IconRight?: ReactNode;
}
interface SxInterface {
  SxMenuList?: SxProps;
  SxMenuItem?: SxProps;
}
export interface NGMenuListInterface {
  MenuItemData: IMenuItem[];
  Sx?: SxInterface;
}
