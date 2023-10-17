import React from 'react';
import {SnackbarContent, CustomContentProps, useSnackbar} from 'notistack';
import {IconButton, lighten} from '@mui/material';
import {NGCrossAlert} from '@/assets/iconExport/Allicon';
import NGText from '@components/ng-text/NGText';
import {pixelToRem} from '@/utils/common/pxToRem';
import {useTheme} from '@mui/material/styles';

const ariaDescribedby = 'custom-notistack-snackbar';

/**
 * `CustomSnackbar` use exclusively with notistack package, that have 4 different variant:
 * `infoSnackbar`, `warningSnackbar`, `successSnackbar`, `errorSnackbar`
 * */
const CustomSnackbar = React.forwardRef<HTMLDivElement, CustomContentProps>(
  (props, ref) => {
    const {
      id,
      message,
      action: componentOrFunctionAction,
      iconVariant,
      variant,
      hideIconVariant,
      style,
      className,
    } = props;
    const theme = useTheme();

    const {closeSnackbar} = useSnackbar();

    const icon = iconVariant[variant];

    const handleDismiss = React.useCallback(() => {
      closeSnackbar(id);
    }, [id, closeSnackbar]);

    let action = componentOrFunctionAction;
    if (typeof action === 'function') {
      action = action(id);
    } else if (typeof action === 'undefined') {
      action = (() => (
        <IconButton
          size="small"
          style={{padding: '8px 8px'}}
          onClick={handleDismiss}>
          <NGCrossAlert fontSize="small" />
        </IconButton>
      ))();
    }

    const getVariantColor = () => {
      if (variant === 'infoSnackbar') {
        return theme.palette.Info.main;
      }
      if (variant === 'warningSnackbar') {
        return theme.palette.Warning.main;
      }
      if (variant === 'successSnackbar') {
        return theme.palette.Success.main;
      }
      if (variant === 'errorSnackbar') {
        return theme.palette.ColorDisabled.main;
      }
      return theme.palette.primary.main;
    };

    return (
      <SnackbarContent
        ref={ref}
        role="alert"
        style={{
          backgroundColor: lighten(getVariantColor(), 0.85),
          fontSize: '0.875rem',
          lineHeight: 1.43,
          letterSpacing: '0.01071em',
          color: getVariantColor(),
          alignItems: 'center',
          padding: '6px 16px',
          borderRadius: '4px',
          ...style,
        }}
        className={className}
        aria-describedby={ariaDescribedby}>
        <div
          id={ariaDescribedby}
          style={{
            display: 'flex',
            alignItems: 'center',
            padding: '8px 0',
            flex: 1,
          }}>
          {!hideIconVariant ? icon : null}
          <NGText
            text={message}
            myStyle={{
              fontSize: pixelToRem(13),
              color: getVariantColor(),
            }}
          />
        </div>
        {action && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              marginLeft: 'auto',
              paddingLeft: '16px',
              marginRight: '-8px',
            }}>
            {action}
          </div>
        )}
      </SnackbarContent>
    );
  },
);

CustomSnackbar.displayName = 'CustomSnackbar';

export default CustomSnackbar;
