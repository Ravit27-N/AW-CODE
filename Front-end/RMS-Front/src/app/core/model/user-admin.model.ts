import { KeyValue } from '@angular/common';

export interface UserPayload {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  enabled: boolean;
  credentials?: Array<KeyValue<string, any>>;
  userGroup: {
    userId: string;
    groupId: number;
  };
}

export interface DefaultUserCriteria {
  pageIndex: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
}

export interface UserCriteria {
  defaultCriteria: DefaultUserCriteria;
  filter: string;
}
