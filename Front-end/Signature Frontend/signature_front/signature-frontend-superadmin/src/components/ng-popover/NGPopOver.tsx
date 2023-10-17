import {Type} from '@components/ng-popover/Type';
import NGText from '@components/ng-text/NGText';
import {Stack} from '@mui/material';
import Popover from '@mui/material/Popover';
import {styled} from '@mui/material/styles';
import * as React from 'react';
const StyledPopover = styled(Popover)(() => ({
  '& .MuiPopover-paper': {
    boxShadow: '0px 0px 11.2208px rgba(0, 0, 0, 0.1)', // customizing shadow with theme object
  },
}));
function NgPopOver({
  open,
  onClick,
  onClose,
  contain,
  button,
  vertical = 'bottom',
  horizontal = 'right',
  verticalT = 'top',
  horizontalT = 'right',
  Sx,
}: Type) {
  const [anchorEl, setAnchorEl] = React.useState<HTMLButtonElement | null>(
    null,
  );
  const isControl = typeof open === 'boolean';

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
    if (typeof onClick === 'function') {
      onClick(event);
    }
  };

  const handleClose = (
    event: React.MouseEvent<HTMLButtonElement>,
    reason: 'backdropClick' | 'escapeKeyDown',
  ) => {
    setAnchorEl(null);
    if (typeof onClose === 'function') {
      onClose(event, reason);
    }
  };

  React.useEffect(() => {
    if (isControl && !open) {
      setAnchorEl(null);
    }
  }, [open]);

  let controlOpen = Boolean(anchorEl);

  if (isControl) {
    controlOpen = open && controlOpen;
  }

  const id = controlOpen ? 'simple-popover' : undefined;

  return (
    <Stack>
      <NGText text={button} onClick={handleClick} />
      <StyledPopover
        sx={{
          ...Sx,
        }}
        id={id}
        open={controlOpen}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical,
          horizontal,
        }}
        transformOrigin={{
          vertical: verticalT,
          horizontal: horizontalT,
        }}>
        {contain}
      </StyledPopover>
    </Stack>
  );
}

export default NgPopOver;
