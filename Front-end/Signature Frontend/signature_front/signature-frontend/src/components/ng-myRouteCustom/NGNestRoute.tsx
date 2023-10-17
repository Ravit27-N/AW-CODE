import ListItemButton from '@mui/material/ListItemButton';
import ExpandMore from '@mui/icons-material/ExpandMore';
import ExpandLess from '@mui/icons-material/ExpandLess';
import Collapse from '@mui/material/Collapse';
import List from '@mui/material/List';
import {useDisclose} from './useDisclose';
import {Type} from './Type';
import NGRoute from './NGRoute';

export function NGNestRoute({
  child,
  goto,
  listName,
  listIcon,
  IsNested = false,
}: Type) {
  const disclose = useDisclose();
  return (
    <>
      {IsNested ? (
        <List>
          <ListItemButton onClick={disclose.onToggle} sx={{p: 0}}>
            <NGRoute goto={goto} listName={listName} listIcon={listIcon} />
            {disclose.isOpen ? <ExpandLess /> : <ExpandMore />}
          </ListItemButton>
          <Collapse
            in={disclose.isOpen}
            timeout="auto"
            unmountOnExit
            sx={{pl: 2}}>
            {child}
          </Collapse>
        </List>
      ) : (
        <NGRoute goto={goto} listName={listName} listIcon={listIcon} />
      )}
    </>
  );
}
