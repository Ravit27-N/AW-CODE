import { createFeatureSelector, createSelector } from '@ngrx/store';
import { featureFlowTabMenuKey, FlowTabmenuState, ITabData } from './flow-tab-menu.reducer';

export const getFeatureMenuTab = createFeatureSelector<FlowTabmenuState>(featureFlowTabMenuKey)

function flatDeep(arr: [], d = 1): any {
  return d > 0 ? arr.reduce((acc, val) => acc.concat(Array.isArray(val) ? flatDeep(val, d - 1) : val), [])
               : arr.slice();
};

function flattern(tab: ITabData): any {
  if(tab.parent) {
    return [flattern(tab.parent), tab]
  } else {
    return [tab];
  }
}

export const getMenuTab = createSelector(getFeatureMenuTab, (menu) => {
  const afterFlattern = menu.current ? flattern(menu.current) : [];
  return flatDeep(afterFlattern, 5);
});

export const getTabState = createSelector(getFeatureMenuTab, (menu) => menu);
