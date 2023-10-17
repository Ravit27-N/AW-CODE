import {ReactNode} from 'react';
import {Stack} from '@mui/material';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGText from '@components/ng-text/NGText';
interface BoxStatusInterface {
  title: string;
  bgColor?: string;
  textColor?: string;
  borderColor?: string;
  icon?: ReactNode;
}
export const BoxStatus = ({
  title,
  bgColor = 'success.main',
  textColor = 'White.main',
  icon,
  borderColor = '1px solid White.main',
}: BoxStatusInterface) => {
  return (
    <Stack
      direction={'row'}
      justifyContent={'flex-start'}
      // width={{xl: '50%', lg: '70%'}}
      alignItems={'center'}>
      <Stack
        direction={'row'}
        alignItems={'center'}
        bgcolor={bgColor}
        px={pixelToRem(8)}
        py={pixelToRem(4)}
        justifyContent={'center'}
        sx={{
          border: borderColor,
          borderRadius: pixelToRem(2),
          gap: pixelToRem(5),
          height: pixelToRem(24),
        }}>
        {icon}
        <NGText
          text={title.toUpperCase()}
          myStyle={{
            color: textColor,
            fontSize: pixelToRem(8),
            fontWeight: 700,
            lineHeight: pixelToRem(16),
          }}
        />
      </Stack>
    </Stack>
  );
};
