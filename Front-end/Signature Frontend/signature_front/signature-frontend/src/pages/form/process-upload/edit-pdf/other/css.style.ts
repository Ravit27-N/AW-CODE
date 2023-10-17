// Customer style for this folder
export const styles = {
  // srcollbar hidden but still can scroll
  scrollbarHidden: {
    // overflowY: 'auto',
    top: 0,
    '&::-webkit-scrollbar': {
      width: '0.0em',
    },
    '&::-webkit-scrollbar-track': {
      boxShadow: 'inset 0 0 6px rgba(0,0,0,0.00)',
      webkitBoxShadow: 'inset 0 0 6px rgba(0,0,0,0.00)',
    },
    // '&::-webkit-scrollbar-thumb': {
    //   backgroundColor: 'rgba(0,0,0,.1)',
    //   outline: '1px solid slategrey',
    // },
  },
};
