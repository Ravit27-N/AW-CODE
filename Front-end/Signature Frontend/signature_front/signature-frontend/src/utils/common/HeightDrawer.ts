export const heightDrawer = (activeStep: number) => {
  if (activeStep === 0) {
    return '469px';
  } else if (activeStep === 1) {
    return '104px';
  } else {
    return 'auto';
  }
};
