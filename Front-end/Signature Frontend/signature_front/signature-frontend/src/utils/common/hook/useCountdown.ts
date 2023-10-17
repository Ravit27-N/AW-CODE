import React from 'react';
export const useCountdown = ({second}: {second: number}) => {
  const [state, setState] = React.useState(second);
  React.useEffect(() => {
    const interval = setInterval(() => {
      setState(state - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, [state]);

  return state;
};
