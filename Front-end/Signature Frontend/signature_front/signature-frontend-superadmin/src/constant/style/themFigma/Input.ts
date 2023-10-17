import {FONT_TYPE} from '@/constant/NGContant';
import {pixelToRem} from '@/utils/common/pxToRem';
import {SxProps} from '@mui/material';
import {IFigmaGeneric} from './CTA';

export const FigmaInput: {
  [k in IFigmaGeneric<
    [
      'InputTextMediumBold',
      'InputTextMedium',
      'InputTextSmall',
      'InputLabelMedium',
      'InputLabelMediumBold',
      'InputLabelSmall',
    ]
  >]: SxProps;
} = {
  InputTextMediumBold: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 500,
    textDecoration: 'none',
    fontSize: pixelToRem(14),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(16),
    case: 'original',
  },
  InputTextMedium: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 400,
    textDecoration: 'none',
    fontSize: pixelToRem(14),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(16),
    case: 'original',
  },
  InputTextSmall: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 400,
    textDecoration: 'none',
    fontSize: pixelToRem(12),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(16),
    case: 'original',
  },
  InputLabelMedium: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 500,
    textDecoration: 'none',
    fontSize: pixelToRem(13),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(20),
    case: 'original',
  },
  InputLabelMediumBold: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 600,
    textDecoration: 'none',
    fontSize: pixelToRem(14),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(24),
    case: 'original',
  },
  InputLabelSmall: {
    fontFamily: FONT_TYPE.POPPINS,
    letterSpacing: pixelToRem(0),
    fontWeight: 500,
    textDecoration: 'none',
    fontSize: pixelToRem(12),
    paragraph: pixelToRem(0),
    lineHeight: pixelToRem(20),
    case: 'original',
  },
};
