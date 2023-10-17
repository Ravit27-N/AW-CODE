import React from 'react';
import {Stack, Typography} from '@mui/material';
import MenuItem from '@mui/material/MenuItem';
import Select, {SelectChangeEvent} from '@mui/material/Select';
import {$ok} from '@/utils/request/common/type';

const WriteTab = ({data}: any) => {
  const defaultFont = 'arial';
  const [value, setValue] = React.useState<string>('');

  const handleSelectChange = (e: SelectChangeEvent): void => {
    setValue(e.target.value);
  };

  const menuItems: {
    key: 'italianno' | 'poppins';
    font: 'Italianno' | 'Poppins';
    disabled: boolean;
  }[] = [
    {key: 'italianno', font: 'Italianno', disabled: true},
    {key: 'poppins', font: 'Poppins', disabled: true},
  ];

  return (
    <Stack sx={{py: 2}}>
      <Select
        size="small"
        value={value}
        onChange={handleSelectChange}
        displayEmpty
        sx={{
          '&.MuiInputBase-root': {
            color: 'black.main', // set the color of the text
            fieldset: {
              borderColor: 'Primary.main',
            },
            '&.Mui-focused fieldset': {
              borderColor: value.length ? 'Primary.main' : 'inherit',
              borderWidth: '0.2px',
            },
            '& .MuiSelect-icon': {
              color: 'black.main', // set the color of the arrow icon
            },
          },
        }}
        inputProps={{'aria-label': 'Without label'}}>
        {menuItems.map(item => (
          <MenuItem key={item.key} value={item.key} disabled={item.disabled}>
            {item.font}
          </MenuItem>
        ))}
      </Select>

      <Stack
        sx={{height: '200px', width: '100%'}}
        justifyContent={'center'}
        alignItems={'center'}>
        <Typography sx={{fontFamily: value || defaultFont, fontSize: '40px'}}>
          {$ok(data?.actor) &&
            `${data.actor?.firstName} ${data.actor?.lastName}`}
        </Typography>
      </Stack>
    </Stack>
  );
};

export default WriteTab;
