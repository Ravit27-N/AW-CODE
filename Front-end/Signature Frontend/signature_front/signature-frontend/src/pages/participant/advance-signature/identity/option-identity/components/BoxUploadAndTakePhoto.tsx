import React, {ReactNode} from 'react';
import {StyleConstant} from '@constant/style/StyleConstant';
import NGText from '@components/ng-text/NGText';
import {FigmaCTA} from '@constant/style/themFigma/CTA';
import {Stack, StackProps} from '@mui/material';

function BoxUploadAndTakePhoto({
  label,
  icon,
  ...props
}: {
  label: string;
  icon: ReactNode;
} & StackProps) {
  return (
    <Stack
      {...props}
      direction={'row'}
      sx={{
        ...StyleConstant.box.cardIdentity,
        gap: '8px',
        border: '1.5px solid',
        justifyContent: 'center',
        height: '40px',
        alignItems: 'center',
      }}>
      {icon}
      <NGText text={label} sx={{...FigmaCTA.CtaSmall}} />
    </Stack>
  );
}

export default BoxUploadAndTakePhoto;
