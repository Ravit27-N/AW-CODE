import {Stack} from '@mui/material';
import HeroProjectDetail, {
  FilterByInterface,
} from '@pages/end-user/project-detail/empty/HeroProjectDetail';
import {TabProjectDetail} from '@pages/end-user/project-detail/empty/TabProjectDetail';
import React from 'react';
import CreateProject from '../form/CreateProject';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
const EmptyProjectDetail = () => {
  /** Get value of select form hero to pass to tab project detail. **/
  const [selectOption, setSelectOption] = React.useState<FilterByInterface>();
  const [open, setOpen] = React.useState<boolean>(false);

  return (
    <Stack width="100%" alignItems="center" height="auto">
      <Stack
        sx={{
          p: '40px 72px 0px 72px',
          backgroundImage: `url(${bgLogo})`,
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
        }}
        width="100%"
        height="152px"
        gap="24px"
        justifyContent="flex-start"
        alignItems="center">
        {/** Component when we select option  **/}
        <HeroProjectDetail setSelectOption={setSelectOption} />
        <Stack alignItems="start" width={'100%'}>
          {/** show data depend on selected option  **/}
          <TabProjectDetail selectOption={selectOption} setOpen={setOpen} />
        </Stack>
      </Stack>

      <CreateProject open={open} setOpen={setOpen} />
    </Stack>
  );
};

export default EmptyProjectDetail;
