import React from 'react';
import styled from '@emotion/styled';
import {Box} from '@mui/material';

interface ResponsiveImageProps {
  src: string;
  alt: string;
}

const Image = styled.img`
  max-width: 100%;
  height: auto;
  padding: 13px 15px 13px 15px;
  @media (min-width: 768px) {
    width: 200px;
  }

  @media (max-width: 480px) {
    width: 40%;
  }
`;

export default function ResponsiveImage({
  src,
  alt,
}: ResponsiveImageProps): JSX.Element {
  return (
    <Box
      sx={{
        display: 'flex',
        flexGrow: 1,
        backgroundColor: 'white',
        position: 'fixed',
        top: 0,
        zIndex: 1,
        width: '100%',
      }}>
      <Image src={src} alt={alt} />
    </Box>
  );
}
