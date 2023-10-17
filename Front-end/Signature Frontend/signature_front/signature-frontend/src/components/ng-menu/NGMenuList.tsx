import React from 'react';
import {MenuItem, MenuList, Stack} from '@mui/material';
import {NGMenuListInterface} from '@components/ng-menu/type';

function NGMenuList({MenuItemData, Sx}: NGMenuListInterface) {
  return (
    <MenuList>
      <Stack sx={{...Sx?.SxMenuList}}>
        {MenuItemData.map((menu, index: number) => (
          <MenuItem key={menu.key} sx={{mx: 'auto', ...Sx?.SxMenuItem}}>
            {menu.NameMenu}
          </MenuItem>
        ))}
      </Stack>
    </MenuList>
  );
}

export default NGMenuList;
