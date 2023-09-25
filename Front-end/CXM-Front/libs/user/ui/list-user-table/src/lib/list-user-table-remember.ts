import { appRoute } from '@cxm-smartflow/shared/data-access/model';

const LIST_USER_TABLE  = 'rem-list-use-table';

export const userTableFiltering = {
  shouldRestoreFilter: () => {
    const userUpdating :string[] = [
       appRoute.cxmUser.navigateToCreate,
      appRoute.cxmUser.navigateToModify,
      appRoute.cxmUser.navigateToModifyBatch,
    ];

    const localUrl = localStorage.getItem('previousURL');
    const previousURL =  localUrl !== null ? JSON.parse(localUrl) : {};

    if (previousURL || ! userUpdating.some((e) => previousURL[previousURL.length - 1]?.includes(e))) {
      localStorage.removeItem(LIST_USER_TABLE);
    }else{
      const filtering = localStorage.getItem(LIST_USER_TABLE);
      return filtering ? JSON.parse(filtering) : false;
    }

  },

  rememberFilter: (filter: any) => {
    localStorage.setItem(LIST_USER_TABLE, JSON.stringify(filter));
  },
}
