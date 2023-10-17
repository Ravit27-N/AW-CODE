import {Avatar, AvatarGroup, Stack} from '@mui/material';
import {
  blue,
  cyan,
  deepOrange,
  deepPurple,
  green,
  red,
} from '@mui/material/colors';
import React from 'react';
import {GroupAvtarVisible} from '@/constant/NGContant';
import {randomIntArray} from '@/utils/common/random';

type IGroupAvatar = {
  character: string[];
};
export const bg = [
  cyan[50],
  deepOrange[50],
  deepPurple[50],
  green[50],
  blue[50],
  red[50],
];
export const textColor = [
  cyan[800],
  deepOrange[800],
  deepPurple[800],
  green[800],
  blue[800],
  red[800],
];

const NGGroupAvatar = ({character}: IGroupAvatar) => {
  const genCharacter = React.useMemo(() => {
    const genArr: string[] = [];
    for (const element of character) {
      genArr.push(element);
    }

    return genArr;
  }, []);
  const genColor = React.useMemo(() => {
    const genArr: number[] = [];
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    for (const _element of bg) {
      const random = randomIntArray(1, 0, bg.length);
      genArr.push(random);
    }
    return genArr;
  }, []);
  return genCharacter.length < 5 ? (
    <Stack direction={'row'} justifyContent={'flex-start'} spacing={0.5}>
      {genCharacter.map((item, index: number) => (
        <Avatar
          key={randomIntArray(12, 0, 10000)}
          sx={{
            bgcolor: bg[genColor[index]],
            width: 30,
            height: 30,
            fontSize: 12,
            fontWeight: 600,
            color: textColor[genColor[index]],
          }}>
          {item.toUpperCase()}
        </Avatar>
      ))}
    </Stack>
  ) : (
    <AvatarGroup
      max={GroupAvtarVisible}
      // total={5}
      componentsProps={{
        additionalAvatar: {
          sx: {
            width: 30,
            height: 30,
            fontSize: 10,
          },
        },
      }}
      sx={{
        justifyContent: 'flex-end',
      }}>
      {genCharacter.map((item, index: number) => (
        <Avatar
          key={randomIntArray(12, 0, 10000)}
          sx={{
            bgcolor: bg[genColor[index]],
            width: 30,
            height: 30,
            fontSize: 12,
            fontWeight: 600,
            color: textColor[genColor[index]],
          }}>
          {item.toUpperCase()}
        </Avatar>
      ))}
    </AvatarGroup>
  );
};

export default NGGroupAvatar;
