import {Localization} from '@/i18n/lan';
import NGText from '@components/ng-text/NGText';
import DeleteOutlineOutlinedIcon from '@mui/icons-material/DeleteOutlineOutlined';
import ModeEditOutlineOutlinedIcon from '@mui/icons-material/ModeEditOutlineOutlined';
import {
  Alert,
  Avatar,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  IconButton,
  Stack,
  Switch,
  SxProps,
  Typography,
} from '@mui/material';
import Snackbar from '@mui/material/Snackbar';
import {styled} from '@mui/material/styles';
import {t} from 'i18next';
import {PDFDocument} from 'pdf-lib';
import {MouseEvent, MouseEventHandler, ReactNode} from 'react';

// Add more documents label
export const AddMore = ({
  name,
  icon,
  onClick,
  fontSize = 14,
  fontWeight = 500,
  sxProps,
  num,
}: {
  name: string;
  num?: number;
  icon?: ReactNode;
  fontSize?: number;
  fontWeight?: number;
  sxProps?: SxProps;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}): JSX.Element => (
  <Stack
    sx={{py: '1rem', px: '2rem', ...sxProps}}
    direction={'row'}
    alignItems={'center'}
    justifyContent={'space-between'}>
    <NGText
      text={name + (num ? ` (${num}) ` : '')}
      myStyle={{fontSize, fontWeight, textTransform: 'capitalize'}}
    />
    {icon && (
      <IconButton sx={{p: 0}} onClick={onClick}>
        {icon}
      </IconButton>
    )}
  </Stack>
);

export const TitleAddMore = ({
  name,
  icon,
  onClick,
  num,
}: {
  name: string;
  num?: number;
  icon?: ReactNode;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}): JSX.Element => (
  <Stack
    sx={{py: '16px', px: '2rem'}}
    direction={'row'}
    alignItems={'center'}
    spacing={2}>
    {icon && (
      <IconButton sx={{p: 0}} onClick={onClick}>
        {icon}
      </IconButton>
    )}

    <NGText
      text={name + (num ? ` (${num}) ` : '')}
      myStyle={{fontSize: 18, fontWeight: 600}}
    />
  </Stack>
);

// Subtitle add more
export const SubTitleAddMore = ({
  name,
  icon,
  onClick,
  num,
}: {
  name: string;
  num?: number;
  icon?: ReactNode;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}): JSX.Element => (
  <Stack
    sx={{py: '1rem', px: '2rem'}}
    direction={'row'}
    alignItems={'center'}
    spacing={1}>
    {icon && (
      <IconButton sx={{p: 0}} onClick={onClick}>
        {icon}
      </IconButton>
    )}
    <Typography
      sx={{fontWeight: 600, textTransform: 'capitalize', fontSize: '13px'}}>
      {name + (num ? ` (${num}) ` : '')}
    </Typography>
  </Stack>
);

// Switch component off / on
export const AntSwitch = styled(Switch)<{overridecolor?: string}>(
  ({theme, overridecolor}) => ({
    width: 38,
    height: 20,
    padding: 0,
    display: 'flex',
    '&:active': {
      '& .MuiSwitch-thumb': {
        width: 15,
      },
      // '& .MuiSwitch-switchBase.Mui-checked': {
      //   transform: 'translateX(9px)',
      // },
    },
    '& .MuiSwitch-switchBase': {
      padding: 2,
      '&.Mui-checked': {
        transform: 'translateX(18px)',
        color: '#fff',
        '& + .MuiSwitch-track': {
          opacity: 1,
          backgroundColor:
            theme.palette.mode === 'dark'
              ? '#177ddc'
              : overridecolor ?? '#121232',
        },
      },
    },
    '& .MuiSwitch-thumb': {
      boxShadow: '0 2px 4px 0 rgb(0 35 11 / 20%)',
      width: 15,
      height: 15.5,

      borderRadius: 7,
      transition: theme.transitions.create(['width'], {
        duration: 200,
      }),
    },
    '& .MuiSwitch-track': {
      borderRadius: 16,
      opacity: 1,
      backgroundColor:
        theme.palette.mode === 'dark'
          ? 'rgba(255,255,255,.35)'
          : 'rgba(0,0,0,.25)',
      boxSizing: 'border-box',
    },
  }),
);

// Participants Champ component
export const Participants = ({
  name,
  avatarSx,
  icon,
  onClick,
}: {
  name: string;
  index: number;
  avatarSx: SxProps;
  icon?: ReactNode;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}): JSX.Element => {
  return (
    <Stack
      sx={{
        borderRadius: 0,
        p: 0,
        py: 1.5,
        alignItems: 'center',
      }}>
      <Stack
        direction={'row'}
        alignItems={'center'}
        justifyContent={'space-between'}
        spacing={2}
        sx={{width: '100%'}}>
        <Stack direction={'row'} sx={{alignItems: 'center'}} spacing={1}>
          <Avatar
            sx={{
              fontSize: 11,
              ...avatarSx,
              fontWeight: 600,
            }}>
            {name.split(' ')[0].charAt(0).toUpperCase() +
              name.split(' ')[1].charAt(0).toUpperCase()}
          </Avatar>
          <NGText
            text={name}
            myStyle={{
              textTransform: 'capitalize',
              fontWeight: 500,
              fontSize: 12,
            }}
          />
        </Stack>

        {icon && (
          <IconButton sx={{p: 0}} onClick={onClick}>
            {icon}
          </IconButton>
        )}
      </Stack>
    </Stack>
  );
};

// Approval right side component
export const Approvals = ({
  name,
  popUp,
  index,
  handleClose,
  onDelete,
  onEdit,
  open,
  avatarSx,
}: {
  name: string;
  index?: number;
  popUp?: () => void;
  handleClose?: () => void;
  onDelete?: (
    e: MouseEvent<HTMLButtonElement, globalThis.MouseEvent>,
    index: number,
  ) => void;
  onEdit?: () => void;
  open?: boolean;
  avatarSx?: SxProps;
}): JSX.Element => {
  return (
    <Stack
      sx={{
        borderRadius: 0,
        p: 0,
        py: 0.5,
        alignItems: 'center',
      }}>
      <Stack
        direction={'row'}
        alignItems={'center'}
        justifyContent={'space-between'}
        spacing={2}
        sx={{width: '100%'}}>
        <Stack direction={'row'} sx={{alignItems: 'center'}} spacing={1}>
          <Avatar
            sx={{
              fontSize: 11,
              ...avatarSx,
              fontWeight: 600,
            }}>
            {name.split(' ')[0].charAt(0).toUpperCase() +
              name.split(' ')[1].charAt(0).toUpperCase()}
          </Avatar>
          <NGText
            text={name}
            myStyle={{
              textTransform: 'capitalize',
              fontWeight: 500,
              fontSize: 12,
            }}
          />
        </Stack>
        <AlertConfirmDelete
          content={t(Localization('pdf-edit', 'delete-approval'))!}
          open={open!}
          handleClose={handleClose!}
          index={index!}
          onDelete={onDelete}
        />

        <Stack direction={'row'} spacing={1}>
          <IconButton onClick={onEdit}>
            <ModeEditOutlineOutlinedIcon />
          </IconButton>
          <IconButton onClick={popUp}>
            <DeleteOutlineOutlinedIcon />
          </IconButton>
        </Stack>
      </Stack>
    </Stack>
  );
};

// Signatories right side component
export const Signatories = ({
  name,
  index,
  popUp,
  handleClose,
  open,
  onEdit,
  onDelete,
  avatarSx,
}: {
  name: string;
  index: number;
  open: boolean;
  popUp: () => void;
  handleClose?: () => void;
  onDelete?: (
    e: MouseEvent<HTMLButtonElement, globalThis.MouseEvent>,
    index: number,
  ) => void;
  onEdit?: () => void;
  avatarSx: SxProps;
}): JSX.Element => {
  return (
    <Stack
      sx={{
        borderRadius: 0,
        p: 0,
        py: 0.5,
        alignItems: 'center',
      }}>
      <Stack
        direction={'row'}
        alignItems={'center'}
        justifyContent={'space-between'}
        spacing={2}
        sx={{width: '100%'}}>
        <Stack direction={'row'} sx={{alignItems: 'center'}} spacing={1}>
          <Avatar
            sx={{
              fontSize: 11,
              ...avatarSx,
              fontWeight: 600,
            }}>
            {name.split(' ')[0].charAt(0).toUpperCase() +
              name.split(' ')[1].charAt(0).toUpperCase()}
          </Avatar>
          <NGText
            text={name}
            myStyle={{
              textTransform: 'capitalize',
              fontWeight: 500,
              fontSize: 12,
            }}
          />
        </Stack>

        <AlertConfirmDelete
          content={t(Localization('pdf-edit', 'delete-signatory'))!}
          open={open}
          handleClose={handleClose!}
          index={index}
          onDelete={onDelete}
        />

        <Stack direction={'row'} spacing={1}>
          <IconButton onClick={onEdit}>
            <ModeEditOutlineOutlinedIcon />
          </IconButton>
          <IconButton onClick={popUp}>
            <DeleteOutlineOutlinedIcon />
          </IconButton>
        </Stack>
      </Stack>
    </Stack>
  );
};

export const UniqueSignatory = ({
  name,
  index,
  handleClose,
  open,
  onDelete,
  avatarSx,
}: {
  name: string;
  index: number;
  open: boolean;
  handleClose?: () => void;
  onDelete?: (
    e: MouseEvent<HTMLButtonElement, globalThis.MouseEvent>,
    index: number,
  ) => void;
  avatarSx: SxProps;
}): JSX.Element => {
  return (
    <Stack
      sx={{
        borderRadius: 0,
        p: 0,
        py: 0.5,
        alignItems: 'center',
      }}>
      <Stack
        direction={'row'}
        alignItems={'center'}
        justifyContent={'space-between'}
        spacing={2}
        sx={{width: '100%'}}>
        <Stack direction={'row'} sx={{alignItems: 'center'}} spacing={1}>
          <Avatar
            sx={{
              fontSize: 11,
              ...avatarSx,
              fontWeight: 600,
            }}>
            {name.split(' ')[0].charAt(0).toUpperCase() +
              name.split(' ')[1].charAt(0).toUpperCase()}
          </Avatar>
          <NGText
            text={name}
            myStyle={{
              textTransform: 'capitalize',
              fontWeight: 500,
              fontSize: 12,
            }}
          />
        </Stack>

        <AlertConfirmDelete
          content={t(Localization('pdf-edit', 'delete-signatory'))!}
          open={open}
          handleClose={handleClose!}
          index={index}
          onDelete={onDelete}
        />
      </Stack>
    </Stack>
  );
};

export const Receptient = ({
  name,
  index,
  popUp,
  handleClose,
  open,
  onEdit,
  onDelete,
  avatarSx,
}: {
  name: string;
  index: number;
  open: boolean;
  popUp: () => void;
  handleClose?: () => void;
  onDelete?: (
    e: MouseEvent<HTMLButtonElement, globalThis.MouseEvent>,
    index: number,
  ) => void;
  onEdit?: () => void;
  avatarSx: SxProps;
}): JSX.Element => {
  return (
    <Stack
      sx={{
        borderRadius: 0,
        p: 0,
        alignItems: 'center',
      }}>
      <Stack
        direction={'row'}
        alignItems={'center'}
        justifyContent={'space-between'}
        spacing={2}
        sx={{width: '100%'}}>
        <Stack direction={'row'} sx={{alignItems: 'center'}} spacing={1}>
          <Avatar sx={{fontSize: 14, ...avatarSx, fontWeight: 600}}>
            {name.includes(' ')
              ? name.split(' ')[0].charAt(0).toUpperCase() +
                name.split(' ')[1].charAt(0).toUpperCase()
              : name.split(' ')[0].charAt(0)}
          </Avatar>
          <NGText
            text={name}
            myStyle={{
              textTransform: 'capitalize',
              fontWeight: 500,
              fontSize: 12,
            }}
          />
        </Stack>

        <AlertConfirmDelete
          content={t(Localization('pdf-edit', 'delete-signatory'))!}
          open={open}
          handleClose={handleClose!}
          index={index}
          onDelete={onDelete}
        />

        <Stack direction={'row'} spacing={1}>
          <IconButton onClick={onEdit}>
            <ModeEditOutlineOutlinedIcon />
          </IconButton>
          <IconButton onClick={popUp}>
            <DeleteOutlineOutlinedIcon />
          </IconButton>
        </Stack>
      </Stack>
    </Stack>
  );
};

// Popup dialog before delete cancel/confirm
export const AlertConfirmDelete = ({
  open,
  content,
  handleClose,
  onDelete,
  index,
}: {
  open: boolean;
  content: string | typeof t;
  handleClose: () => void;
  onDelete?: (
    e: MouseEvent<HTMLButtonElement, globalThis.MouseEvent>,
    index: number,
  ) => void;
  index: number;
}) => {
  return (
    <>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description">
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {content as string}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Disagree</Button>
          <Button onClick={e => onDelete!(e, index)} autoFocus>
            Agree
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export const SnackBarMui = ({
  open,
  handleClose,
  message,
  severity,
}: {
  open: boolean;
  handleClose: () => void;
  message: string;
  severity: 'success' | 'error' | 'warning' | 'info';
}) => {
  return (
    <Snackbar
      open={open}
      autoHideDuration={3000}
      onClose={handleClose}
      anchorOrigin={{vertical: 'top', horizontal: 'right'}}>
      <Alert onClose={handleClose} severity={severity}>
        {message}
      </Alert>
    </Snackbar>
  );
};

const readFile = (file: File) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = () => resolve(reader.result);
    reader.onerror = error => reject(error);

    reader.readAsArrayBuffer(file);
  });
};

export const getNumPages = async (file: File) => {
  const arrayBuffer = await readFile(file);

  const pdf = await PDFDocument.load(
    arrayBuffer as string | ArrayBuffer | Uint8Array,
    {ignoreEncryption: true},
  );

  return pdf.getPages();
};
