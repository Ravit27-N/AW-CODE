import {NGMenuListInterface} from '@components/ng-menu/type';
import {MenuItem, MenuList, Stack} from '@mui/material';

function NGMenuList({MenuItemData, Sx}: NGMenuListInterface) {
  return (
    <MenuList>
      <Stack sx={{...Sx?.SxMenuList}}>
        {MenuItemData.map(menu => (
          <MenuItem key={menu.key} sx={{mx: 'auto', ...Sx?.SxMenuItem}}>
            {menu.NameMenu}
          </MenuItem>
        ))}
      </Stack>
    </MenuList>
  );
}

export default NGMenuList;
