import {
  Box,
  LinearProgress,
  linearProgressClasses,
  Stack,
  Tooltip,
} from '@mui/material';

import {styled, SxProps} from '@mui/material/styles';

import NGText from '@components/ng-text/NGText';

import {ProjectStatus} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {pixelToRem} from '@/utils/common/pxToRem';
import {$isstring} from '@/utils/request/common/type';
import {NGCorrect} from '@assets/iconExport/Allicon';
import {tooltipClasses, TooltipProps} from '@mui/material/Tooltip';

const BorderLinearProgress = styled(LinearProgress)(({theme}) => ({
  height: 6,
  borderRadius: 4,
  [`&.${linearProgressClasses.colorPrimary}`]: {
    backgroundColor:
      theme.palette.grey[theme.palette.mode === 'light' ? 200 : 800],
  },
  [`& .${linearProgressClasses.bar}`]: {
    borderRadius: 5,
    backgroundColor:
      theme.palette.mode === 'light' ? 'Primary.main' : '#308fe8',
    backgroundImage: `linear-gradient(235deg, ${theme.palette.primary.main} 15%, rgba(255,255,255,1) 20%, ${theme.palette.primary.main} 25%, ${theme.palette.primary.main} 100%)`,
    backgroundSize: '8.5px 35px',
  },
}));

export const HtmlTooltip = styled(({className, ...props}: TooltipProps) => (
  <Tooltip {...props} classes={{popper: className}} />
))(({theme}) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: '#ffffff',
    color: 'rgba(0, 0, 0, 0.87)',
    width: 'auto',
    fontSize: theme.typography.pxToRem(12),
    border: '1px solid #dadde9',
  },
}));
export const styleInTable: SxProps = {
  fontWeight: 500,
  fontSize: 12,
  // textTransform: 'capitalize',
};
export const StatusProject = ({
  status,
  t,
}: {
  status: number | string;
  t: any;
}) => {
  if ($isstring(status)) {
    if (status === ProjectStatus.COMPLETED) {
      return (
        <Stack
          direction={'row'}
          justifyContent={'flex-start'}
          width={{xl: '50%', lg: '70%'}}
          alignItems={'center'}>
          <Stack
            direction={'row'}
            alignItems={'center'}
            bgcolor={'success.main'}
            px={pixelToRem(8)}
            py={pixelToRem(4)}
            justifyContent={'center'}
            sx={{
              border: '1px solid',
              borderColor: 'success.main',
              borderRadius: pixelToRem(2),
              gap: pixelToRem(8),
              height: pixelToRem(24),
            }}>
            <NGCorrect
              sx={{
                fontSize: pixelToRem(9),
                color: 'white',
              }}
            />

            <NGText
              text={t(
                Localization('projectStatus', ProjectStatus.COMPLETED),
              ).toUpperCase()}
              myStyle={{
                color: 'white',
                fontSize: pixelToRem(8),
                fontWeight: 700,
                lineHeight: pixelToRem(16),
              }}
            />
          </Stack>
        </Stack>
      );
    } else {
      /**
       * Draft status
       * */
      return (
        <Box
          sx={{
            border: '1px solid',
            borderColor: 'black.main',
            borderRadius: pixelToRem(2),
            gap: pixelToRem(8),
            height: pixelToRem(24),
          }}>
          <NGText
            text={t(
              Localization('projectStatus', ProjectStatus.DRAFT),
            ).toUpperCase()}
            myStyle={{
              color: 'black.main',
              fontSize: pixelToRem(8),
              fontWeight: 700,
              lineHeight: pixelToRem(16),
              px: pixelToRem(8),
              py: pixelToRem(4),
            }}
          />
        </Box>
      );
    }
  } else {
    return (
      <Stack direction="row" alignItems="center" gap="10px">
        <BorderLinearProgress
          sx={{width: '191px'}}
          variant="determinate"
          value={Number(status) * 100}
        />
        <NGText
          myStyle={{...styleInTable, fontWeight: 400}}
          text={(Number(status) * 100).toFixed(0) + '%'}
        />
      </Stack>
    );
  }
};
