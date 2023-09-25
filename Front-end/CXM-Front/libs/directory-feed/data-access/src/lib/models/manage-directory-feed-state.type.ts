import {
  DirectoryFeedField,
  DirectoryFeedListResponse,
  ListDirectoryFeedValue,
} from './directory-feed-model';
import { FieldDetail } from './directory-feed-form.model';

export interface ManageDirectoryFeedStateType {
  definitionDirectoryDetail: DirectoryFeedField;
  directoryFeedTables: DirectoryFeedListResponse;
  importDirectoryFeedCsvListCriteria: {
    directoryId: number;
    page: number;
    pageSize: number;
    filter?: string;
    ignoreHeader: boolean;
    removeDuplicated: boolean;
  };
  directoryFeedField: DirectoryFeedField;
  details: ListDirectoryFeedValue;
  dataDirectoryFeeds: FieldDetail[];
  labelFieldKey: string;
  errorCode: number;
  fieldError: string;
  isLocked: boolean;
  isSubmitError: boolean;
}
