import {store} from '@/redux';
import {useGetCorporateModelFolderQuery} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {splitUserCompany} from '@/utils/common/String';
import {Stack} from '@mui/material';
import React from 'react';
import ModelsSections from './sidenav/Sections';
import SideNav from './sidenav/SideNav';

const ModelContent = () => {
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const {currentData} = useGetCorporateModelFolderQuery(
    {
      id: Number(company.companyId),
    },
    {skip: !company.companyId},
  );
  const [activeFolder, setActiveFolder] = React.useState<number | null>(null);
  return (
    <Stack direction="row" height={`calc(100vh - 211px)`}>
      {currentData && (
        <SideNav
          currentData={currentData}
          activeFolder={activeFolder}
          setActiveFolder={setActiveFolder}
        />
      )}
      <ModelsSections activeFolder={activeFolder} />
    </Stack>
  );
};

export default ModelContent;
