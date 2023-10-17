import React from 'react';
import {Box} from '@mui/material';

import {useTranslation} from 'react-i18next';
import NGText from '@components/ng-text/NGText';
import {FigmaBody} from '@constant/style/themFigma/Body';

interface CardProps {
  src: string;
  alt: string;
  firstText: string;
  secondText: string;
  onClick: () => void;
}

export const GridCard = ({
  src,
  alt,
  firstText,
  secondText,
  onClick,
}: CardProps) => {
  const {t} = useTranslation();
  // const {primaryColor} = useGlobalContext();

  return (
    <Box
      onClick={onClick}
      sx={{
        // boxShadow: '3px 2px 8px 1px #0000001A inset',
        boxShadow: '-1px 0px 1px 10px #00000002 inset',
        backgroundColor: 'white',
        borderRadius: '8px',
        padding: '20px',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        height: '140px',
        cursor: 'pointer',
      }}>
      <img
        style={{width: '40px', marginBottom: '10px', color: 'primary.main'}}
        alt={alt}
        src={src}
      />

      <NGText sx={{...FigmaBody.BodyMedium}} text={firstText} />
      <NGText sx={{...FigmaBody.BodyMedium}} text={secondText} />
    </Box>
  );
};
