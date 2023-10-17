import React, {ReactNode} from 'react';
import {Typography, TypographyProps} from '@mui/material';
import {OverridableStringUnion} from '@mui/types';
import {Variant} from '@mui/material/styles/createTypography';
import {TypographyPropsVariantOverrides} from '@mui/material/Typography/Typography';
import {SxProps} from '@mui/system';
import {StyleConstant} from '@/constant/style/StyleConstant';
import Stack from '@mui/material/Stack';
import {$ok} from '@/utils/request/common/type';

interface Type extends TypographyProps {
  disable?: boolean;
  iconStart?: ReactNode;
  iconEnd?: ReactNode;
  dataMulti?: string[];
  multiLine?: boolean;
  propsParentMulti?: SxProps;
  propsChildMulti?: SxProps;
  rows?: number;
  text: string | ReactNode;
  font?:
    | 'Tahoma'
    | 'poppins'
    | 'fredoka'
    | 'verdana-bold-italic'
    | 'verdana-bold'
    | 'verdana';
  fontWeight?: '100' | '200' | '300' | '400' | '500' | '600';

  myStyle?: SxProps;
  variant?: OverridableStringUnion<
    Variant | 'inherit',
    TypographyPropsVariantOverrides
  >;
  component?: React.ElementType;
  styleTextHaveIcon?: SxProps;
}
const NGText = ({
  text,
  font = 'poppins',
  variant,
  myStyle,
  fontWeight,
  component = 'span',
  multiLine = false,
  dataMulti,
  propsParentMulti,
  propsChildMulti,
  iconStart,
  iconEnd,
  disable,
  styleTextHaveIcon,
  ...props
}: Type) => {
  /** When have Icon **/
  const handleHaveIcon = () => {
    if (!$ok(iconEnd) && !$ok(iconStart)) {
      return (
        <Typography
          component={component}
          sx={{
            color: 'black.main',
            ...myStyle,
            opacity: !disable ? 1 : 0.5,
            fontFamily: font,
          }}
          variant={variant}
          fontWeight={fontWeight}
          {...props}>
          {text}
        </Typography>
      );
    } else {
      return (
        <Stack
          direction={'row'}
          alignItems={'center'}
          width={'100%'}
          justifyContent={'flex-start'}
          sx={{...styleTextHaveIcon}}>
          {$ok(iconStart) && iconStart}
          <Typography
            component={component}
            sx={{
              color: 'black.main',
              ...myStyle,
              opacity: !disable ? 1 : 0.5,
              fontFamily: font,
            }}
            variant={variant}
            fontWeight={fontWeight}
            {...props}>
            {text}
          </Typography>
          {$ok(iconEnd) && iconEnd}
        </Stack>
      );
    }
  };
  return (
    <>
      {multiLine ? (
        <Typography
          sx={{lineHeight: 1, ...propsParentMulti}}
          component={component}>
          {multiLine &&
            dataMulti?.map(i => (
              <Typography
                key={i}
                sx={{
                  ...StyleConstant.textSmall,
                  fontWeight: 'bold',
                  opacity: !disable ? 1 : 0.5,
                  ...propsChildMulti,
                  fontFamily: font,
                }}>
                {i}
              </Typography>
            ))}
        </Typography>
      ) : (
        handleHaveIcon()
      )}
    </>
  );
};
export default NGText;
