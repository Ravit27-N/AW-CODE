export interface FeatureModule {
  id: number;
  name: string;
  permission: Array<PermissionModel>;
  active: boolean;
  description: string;
  createdAt?: string;
  updateAt?: string;
}

export interface UserRoleFeature {
  moduleId?: number;
  userRoleId?: string;
  viewAble: boolean;
  insertAble: boolean;
  deleteAble: boolean;
  editAble: boolean;
}

export interface UserRoleModel {
  id?: number;
  name: string;
  description: string;
  privileges: Array<PrivilegeModel>;
}

export interface CreateUserRoleModel {
  name: string;
  description: string;
  privileges: Array<PrivilegeModel>;
}

export interface DefaultRoleCriteria {
  pageIndex: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
}

export interface RoleCriteria {
  defaultCriteria: DefaultRoleCriteria;
  filter: string;
}

export interface PrivilegeModel {
  id: number;
  name: string;
  description: string;
  permission: PermissionModel;
}

export interface PermissionModel {
  viewAble: boolean;
  insertAble: boolean;
  deleteAble: boolean;
  editAble: boolean;
}
