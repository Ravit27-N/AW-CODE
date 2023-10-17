import {Stack} from '@mui/material';
import React from 'react';
import ModelContent from './models/Content';
import ModelsHero from './models/Hero';

const ModelsCorporate = () => {
  const [countModel, setCountModel] = React.useState<number>(0);
  return (
    <Stack width="100%">
      <ModelsHero countModel={countModel} />
      <ModelContent setCountModel={setCountModel} />
    </Stack>
  );
};
export default ModelsCorporate;
