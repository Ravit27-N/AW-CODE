import {styled} from '@mui/material/styles';
import {LinearProgress, linearProgressClasses} from '@mui/material';

export const NgProgress = styled(LinearProgress)(({theme}) => ({
  height: 6,
  borderRadius: 4,
  [`&.${linearProgressClasses.colorPrimary}`]: {
    backgroundColor:
      theme.palette.grey[theme.palette.mode === 'light' ? 200 : 800],
  },
  [`& .${linearProgressClasses.bar}`]: {
    borderRadius: 5,
    backgroundColor: theme.palette.mode === 'light' ? '#D60A6A' : '#308fe8',
  },
}));
