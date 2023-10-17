import React, {ReactNode} from 'react';
import {Stack} from '@mui/material';
import NGText from '@components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {FigmaHeading} from '@constant/style/themFigma/FigmaHeading';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {StyleConstant} from '@constant/style/StyleConstant';
import {NGCIN} from '@assets/iconExport/Allicon';
import {FigmaCTA} from '@constant/style/themFigma/CTA';

export function HeaderIdentity({
  Title,
  description,
  width = '327px',
}: {
  Title: string;
  description: string;
  width?: string;
}) {
  return (
    <Stack gap={'10px'} width={'100%'} alignItems={'center'} mb={'10px'}>
      <NGText
        text={Title}
        sx={{
          ...FigmaHeading.H3,
          width: '100%',
          textAlign: 'center',
        }}
      />
      <NGText
        sx={{
          ...FigmaBody.BodyMedium,
          width,
          textAlign: 'center',
        }}
        text={description}
      />
    </Stack>
  );
}

export function CardUnderHeaderIdentity({
  icon = <NGCIN sx={{width: '40px', height: '40px'}} />,
  title,
  button,
}: {
  title: string;
  button: ReactNode;
  icon?: JSX.Element;
}) {
  return (
    <Stack
      direction={'row'}
      sx={{
        ...StyleConstant.box.cardIdentity,
      }}>
      {icon}
      <Stack>
        <NGText text={title} sx={{...FigmaBody.BodyMedium}} />
        {button}
      </Stack>
    </Stack>
  );
}
