import React, {ReactNode} from 'react';
import {CorporateSettingTheme} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {Stack} from '@mui/material';
import {NGArrowRightDropDown} from '@assets/iconExport/Allicon';

export const ItemMenu = ({
  icon,
  text,
  onclick,
  theme,
}: {
  icon: ReactNode;
  text: ReactNode;
  onclick?: any;
  theme: CorporateSettingTheme[];
}) => {
  const [useIcon, setUseIcon] = React.useState<boolean>(false);
  return (
    <Stack
      onClick={onclick}
      direction={'row'}
      justifyContent={'flex-start'}
      alignItems={'center'}
      width={'208px'}
      height={'40px'}
      borderRadius={'6px'}
      padding={'16px'}
      gap={'5px'}
      onMouseOver={() => setUseIcon(true)}
      onMouseOut={() => setUseIcon(false)}
      sx={{
        cursor: 'pointer',
        '&:hover': {
          /** when we hover on row will color main Color for them and opacity 10 **/
          backgroundColor: theme[0].mainColor
            ? theme[0].mainColor + 15
            : 'white',
        },
      }}>
      <Stack
        direction={'row'}
        justifyContent={'space-between'}
        alignItems={'center'}
        width={'100%'}>
        <Stack
          direction={'row'}
          justifyContent={'center'}
          alignItems={'center'}>
          {icon}
          {text}
        </Stack>
        {useIcon && (
          <NGArrowRightDropDown
            sx={{
              color: theme[0].mainColor ? theme[0].mainColor + 90 : 'white',
              height: '16px',
              width: '16px',
            }}
          />
        )}
      </Stack>
    </Stack>
  );
};
