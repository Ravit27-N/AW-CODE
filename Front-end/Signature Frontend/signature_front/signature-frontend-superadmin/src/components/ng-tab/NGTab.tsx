import * as React from 'react';
import {ReactNode, useEffect} from 'react';
import Box from '@mui/material/Box';
import Tab from '@mui/material/Tab';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';

import NGText from '@components/ng-text/NGText';
import {Stack, SxProps, Typography} from '@mui/material';
import {pixelToRem} from '@/utils/common/pxToRem';
import {TabPanel} from '@mui/lab';

export interface Type {
  data: {
    readonly active: boolean;
    label: string;
    contain: string | React.ReactNode;
    element?: JSX.Element;
    icon?: ReactNode;
  }[];
  defaultTap?: string;
  tapStyle?: SxProps;
  textTransform?: string;
  locationIcon?: 'bottom' | 'top' | 'end' | 'start';
}

export default function NGTabs({
  data,
  defaultTap = '',
  tapStyle,
  textTransform,
  locationIcon,
}: Type) {
  const [value, setValue] = React.useState(defaultTap);
  useEffect(() => {
    setValue(defaultTap);
  }, [defaultTap]);
  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setValue(newValue);
  };
  return (
    <Box
      sx={{
        width: '100%',
        height: '100%',
        typography: 'body1',
        ...tapStyle,
      }}>
      <TabContext value={value}>
        <Box>
          <TabList
            TabIndicatorProps={{
              style: {
                backgroundColor: 'Primary.main',
              },
            }}
            onChange={handleChange}
            aria-label="setting signature option"
            indicatorColor="primary">
            {data.map(item => {
              return (
                <Tab
                  disabled={!item.active}
                  disableFocusRipple
                  disableRipple
                  disableTouchRipple
                  label={
                    <NGText
                      text={item.label}
                      fontSize={'14px'}
                      disable={!item.active}
                    />
                  }
                  value={item.label}
                  key={item.label}
                  icon={
                    <Stack sx={{color: !item.active ? 'grey' : 'Black.main'}}>
                      {item.icon ?? undefined}
                    </Stack>
                  }
                  iconPosition={locationIcon}
                  sx={{
                    '&.MuiButtonBase-root.MuiTab-textColorPrimary': {
                      color: '#000000',
                    },
                    fontWeight: '600',
                    py: pixelToRem(24),
                    textTransform: textTransform ? textTransform : 'none',
                  }}
                />
              );
            })}
          </TabList>
        </Box>
        <>
          {data.map(item => {
            return (
              <TabPanel
                sx={{fontFamily: 'poppins', p: 0}}
                value={item.label}
                key={item.label}>
                <Typography
                  component={'div'}
                  sx={{fontFamily: 'poppins'}}
                  variant="h2">
                  {item.contain}
                </Typography>
                {item.element}
              </TabPanel>
            );
          })}
        </>
      </TabContext>
    </Box>
  );
}
