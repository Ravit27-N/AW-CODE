import {Stack} from '@mui/material';
import HeroProjectDetail, {
  FilterByInterface,
} from '@pages/corporate-admin/sidebar-corporate/signature-projects/components/HeroProjectDetail';
import {TabProjectDetail} from '@pages/corporate-admin/sidebar-corporate/signature-projects/components/TabProjectDetail';
import React from 'react';

const CorporateSignatureProjects = () => {
  /** Get value of select form hero to pass to tab project detail. **/
  const [selectOption, setSelectOption] = React.useState<FilterByInterface>();
  const [open, setOpen] = React.useState<boolean>(false);

  return (
    <Stack width="100%" alignItems="center" height="auto">
      <Stack
        sx={{p: '40px 72px 0px 72px'}}
        width="100%"
        height="112px"
        justifyContent="flex-start"
        alignItems="center">
        {/** Component when we select option  **/}
        <HeroProjectDetail setSelectOption={setSelectOption} />
      </Stack>
      <Stack alignItems="start" width={'100%'} sx={{p: '0px 72px 0px 72px'}}>
        {/** show data depend on selected option  **/}
        <TabProjectDetail selectOption={selectOption} setOpen={setOpen} />
      </Stack>
    </Stack>
  );
};

export default CorporateSignatureProjects;
