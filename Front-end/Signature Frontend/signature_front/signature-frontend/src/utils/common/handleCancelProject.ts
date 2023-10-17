import {ProjectStatusInterfaces} from '@components/ng-switch-case-status/interface';
import {ProjectStatus} from '@constant/NGContant';
import React from 'react';

export const handleCancelProject = <
  T extends {id: string},
  P extends React.Dispatch<React.SetStateAction<T[]>>,
>(
  status: keyof ProjectStatusInterfaces,
  setVisibleRows: P,
  visibleRows: T[],
  id: string | number,
) => {
  switch (status) {
    case ProjectStatus.DRAFT: {
      setVisibleRows(visibleRows.filter(item => Number(item.id) !== id));
      break;
    }
    case ProjectStatus.ABANDON: {
      break;
    }
    case ProjectStatus.IN_PROGRESS:
    case ProjectStatus.REFUSED:
    case ProjectStatus.EXPIRED:
    case 'URGENT': {
      const index = visibleRows.findIndex(item => Number(item.id) === id);
      const updatedTodos = [...visibleRows];
      updatedTodos[index] = {
        ...updatedTodos[index],
        completion: 'ABANDON',
        completions: 'ABANDON',
      };
      setVisibleRows(updatedTodos);
      break;
    }
    default: {
      break;
    }
  }
};
