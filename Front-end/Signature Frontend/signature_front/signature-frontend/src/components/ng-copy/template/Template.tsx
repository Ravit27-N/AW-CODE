import React, {ReactNode} from 'react';
import {Box} from '@mui/material';
import {styled} from '@mui/system';

import logo from '@assets/image/LOGO.png';
import ResponsiveImage from '@components/ng-copy/template/photo/ResponsiveImage';

interface TemplateProps {
  child: ReactNode;
  customSx?: React.CSSProperties;
}

export default function Template({
  child,
  customSx,
}: TemplateProps): JSX.Element {
  return (
    <Box
      sx={{
        minHeight: '100dvh',
        position: 'relative',
      }}>
      <img src={logo} alt={'logo'} style={{height: '40px', width: '100px'}} />
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          // backgroundColor: '#f2f5fc',
          // backgroundColor: '#ffffff',
          height: '100dvh',
          width: '100%',
          px: '20px',
          position: 'relative',
          flexGrow: 1,
          ...customSx,
        }}>
        {child}
      </Box>
    </Box>
  );
}
