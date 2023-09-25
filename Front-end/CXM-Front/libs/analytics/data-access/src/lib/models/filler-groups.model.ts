import { FilterListModel } from './filter-list.model';

export interface FillerGroupsModel {
  fillerName: string;
  fillerItems: FilterListModel[];
  fillerDisabledItems: string[];
  fillerHiddenItems: string[];
  fillerSelectedItem: string;
  fillerSearchTerms: string;
  isDisabled: boolean;
}
