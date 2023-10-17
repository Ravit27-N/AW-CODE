import CustomSnackbar from '@components/ng-notistack/CustomSnackbar';

declare module 'notistack' {
  interface VariantOverrides {
    // adds custom variant
    errorSnackbar: true;
    infoSnackbar: true;
    warningSnackbar: true;
    successSnackbar: true;
  }
}

export {CustomSnackbar};
