import {FONT_TYPE} from '@/constant/NGContant';
import {pixelToRem} from '@/utils/common/pxToRem';
import {SxProps} from '@mui/material';
import {IFigmaGeneric} from './CTA';

export const FigmaHeading: {
  [k in IFigmaGeneric<['H1', 'H2', 'H3', 'H4']>]: SxProps;
} = {
  H1: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 600,
    textDecoration: 'none',
    fontSize: pixelToRem(31),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(40),
    case: 'original',
  },
  H2: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 600,
    textDecoration: 'none',
    fontSize: pixelToRem(27),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(36),
    case: 'original',
  },
  H3: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 600,
    textDecoration: 'none',
    fontSize: pixelToRem(22),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(32),
    case: 'original',
  },
  H4: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 600,
    textDecoration: 'none',
    fontSize: pixelToRem(18),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(28),
    case: 'original',
  },
};
