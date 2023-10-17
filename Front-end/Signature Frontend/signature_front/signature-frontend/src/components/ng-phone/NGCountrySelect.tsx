import {MenuItem, Select, Box} from '@mui/material';
import {getCountries, getCountryCallingCode} from 'react-phone-number-input';
import {CountryCode, ICountrySelect} from './type';
import React from 'react';

export default function NGFlagComponent({
  country,
  countryName,
  flags,
  flagUrl,
  ...rest
}: any) {
  if (flags) {
    if (flags[country])
      return (
        <Box sx={{width: 30, height: 30}}>
          {flags[country]({title: countryName})}
        </Box>
      );
  }
  return (
    <img
      style={{width: 30, height: 30}}
      {...rest}
      alt={countryName}
      role={countryName ? undefined : 'presentation'}
      src={flagUrl
        .replace('{XX}', country)
        .replace('{xx}', country.toLowerCase())}
    />
  );
}

export const NGCountrySelect = ({
  selectIndex,
  selectToggle,
  labels,
  selectChange,
  handleClose,
  ...props
}: ICountrySelect) => {
  const mapCountries = React.useMemo(
    () =>
      getCountries()
        .map(
          (c: CountryCode) => `${labels[c]} ${getCountryCallingCode(c)} ${c}`,
        )
        .sort(),
    [],
  );

  return (
    <Select
      MenuProps={{
        anchorOrigin: {
          vertical: 'bottom',
          horizontal: 'left',
        },
        transformOrigin: {
          vertical: 'top',
          horizontal: 'left',
        },
        PaperProps: {
          sx: {maxHeight: 200},
        },
      }}
      {...props}
      open={selectToggle}
      size="small"
      value={''}
      onChange={e => selectChange(e.target.value as string, selectIndex)}
      onClose={(event: React.SyntheticEvent<Element, Event>) =>
        handleClose(event, selectIndex)
      }
      displayEmpty
      inputProps={{'aria-label': 'Without label'}}>
      {mapCountries.map(
        (c: string) =>
          parseInt(c.split(' ')[1], 10) && (
            <MenuItem
              value={`${c}`}
              key={c.split('')[0] + c}
              sx={{minWidth: '20rem'}}>
              {c.split(' ')[0] + ' +' + c.split(' ')[1]}
            </MenuItem>
          ),
      )}
    </Select>
  );
};
