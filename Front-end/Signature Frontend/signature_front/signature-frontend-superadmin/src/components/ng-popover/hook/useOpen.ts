import React from 'react';
import {isLogoutKey} from '@/constant/NGContant';

export const useOpen = () => {
  const [state, setState] = React.useState<boolean>(false);
  const onOpen = () => setState(true);
  const onClose = () => {
    setState(false);
    localStorage.setItem(isLogoutKey, 'false');
  };
  const onToggle = () => setState(!state);

  return {onOpen, onClose, onToggle, isOpen: state};
};
