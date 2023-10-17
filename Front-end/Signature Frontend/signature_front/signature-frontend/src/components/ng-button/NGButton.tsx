import {ReactNode} from 'react';
import Button from '@mui/material/Button';
import {SxProps} from '@mui/material';
import {Box} from '@mui/system';
import {TypeMyButton} from '@/components/ng-button/Type';
import NGText from '@components/ng-text/NGText';
import {FONT_TYPE} from '@/constant/NGContant';

interface Type {
  title: string | ReactNode;
  prop?: SxProps;
}
export function BorderBox({title, prop}: Type) {
  return (
    <Box
      sx={{
        color: 'red',
        fontSize: 10,
        fontWeight: 'bold',
        border: 1,
        py: 0.5,
        px: 1,
        borderRadius: 1,
        fontFamily: FONT_TYPE.POPPINS,
        ...prop,
      }}>
      {title}
    </Box>
  );
}
export function NGButton({
  borderTop = 1,
  borderBottom = 1,
  borderLeft = 1,
  borderRight = 1,
  title,
  type,
  form,
  bgColor = 'primary',
  color = ['white', 'white'],
  size,
  variant = 'contained',
  borderStyle = 'none',
  fontSize = '14px',
  fontWeight,
  borderColor,
  icon = <></>,
  myStyle,
  locationIcon = 'start',
  disabled = false,
  onClick,
  textSx,
  btnProps,
}: TypeMyButton) {
  return (
    <Button
      {...btnProps}
      type={type}
      disabled={disabled}
      onClick={onClick}
      variant={variant}
      color={bgColor}
      form={form}
      sx={{
        textTransform: 'none',
        borderTop,
        borderBottom,
        borderRight,
        borderLeft,
        borderStyle,
        borderColor,
        ...myStyle,
      }}
      size={size}>
      {locationIcon === 'start' && icon}
      {color.length === 0 ? (
        <NGText
          text={title}
          myStyle={{color: color[0], fontSize, fontWeight, ...textSx}}
        />
      ) : (
        <NGText
          text={title}
          myStyle={{
            color: disabled ? color[0] : color[1],
            fontSize,
            fontWeight,
            ...textSx,
          }}
        />
      )}

      {locationIcon === 'end' && icon}
    </Button>
  );
}
