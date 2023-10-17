import {FolderTemplateInterface} from '@/redux/slides/profile/template/templateSlide';
import {Stack} from '@mui/material';
import React from 'react';
import CreateModel from '../form/create-model/CreateModel';
import EmptySection from './sections/EmptySection';
import TableSection from './sections/TableSection';

type IModelSections = {
  activeFolder: number | null;
  currentData: Array<FolderTemplateInterface>;
  setSearch: React.Dispatch<React.SetStateAction<string>>;
  search: string;
};

const ModelsSections = (props: IModelSections) => {
  const {activeFolder, currentData, search, setSearch} = props;
  const [trigger, setTrigger] = React.useState<boolean>(false);
  return (
    <Stack width="100%" height="100%">
      {!currentData.length ? (
        <EmptySection setTrigger={setTrigger} />
      ) : (
        <TableSection
          trigger={trigger}
          setTrigger={setTrigger}
          activeFolder={activeFolder}
          currentData={currentData}
          search={search}
          setSearch={setSearch}
        />
      )}

      <CreateModel trigger={trigger} setTrigger={setTrigger} />
    </Stack>
  );
};

export default ModelsSections;
