import * as React from 'react';
import {ReactNode} from 'react';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Slide from '@mui/material/Slide';
import {TransitionProps} from '@mui/material/transitions';
import {SxProps} from '@mui/material/styles';

const Transition = React.forwardRef(function Transition(
  props: TransitionProps & {
    children: React.ReactElement<any, any>;
  },
  ref: React.Ref<unknown>,
) {
  return <Slide direction="up" ref={ref} {...props} />;
});

interface Type {
  open: boolean;
  style?: SxProps;
  setOpen?: React.Dispatch<React.SetStateAction<boolean>>;
  header?: ReactNode;
  body?: ReactNode;
  footer?: ReactNode;
  iconClose?: ReactNode;
  dialogAction?: boolean;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  height?: string;
  width?: string;
}

export default function NGDialog({
  open,
  footer,
  body,
  header,
  iconClose,
  dialogAction = true,
  style,
  height,
  width,
}: Type) {
  return (
    <Dialog
      PaperProps={{
        style: {
          borderRadius: '12px',
        },
      }}
      sx={{
        '& .MuiDialog-container': {
          '& .MuiPaper-root': {
            pt: 3,
            px: 2,
            width: width ?? '80%',
            height: height ?? undefined,
            maxWidth: '327px',
          },
        },
        ...style,
      }}
      open={open}
      TransitionComponent={Transition}
      keepMounted
      // onClose={handleClose}
      aria-describedby="alert-dialog-slide-description">
      {iconClose}
      <DialogTitle sx={{p: 0}}>{header}</DialogTitle>
      <DialogContent sx={{p: 0}}>
        <DialogContentText component={'span'}>{body}</DialogContentText>
      </DialogContent>
      {dialogAction && <DialogActions>{footer}</DialogActions>}
    </Dialog>
  );
}
