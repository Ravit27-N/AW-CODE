import {Stack} from '@mui/material';
import TableSection from './sections/TableSection';

type IModelSections = {
  activeFolder: number | null;
};

const ModelsSections = (props: IModelSections) => {
  const {activeFolder} = props;
  return (
    <Stack width="100%" height="100%">
      {/* <EmptySection /> */}
      <TableSection activeFolder={activeFolder} />
    </Stack>
  );
};

export default ModelsSections;
