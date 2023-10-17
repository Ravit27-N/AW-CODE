export type UserRole = 'super-admin' | 'coperate-admin' | 'end-user';

export enum UserRoleEnum {
  SUPERADMIN = 'super-admin',
  COPERATE_ADMIN = 'corporate-admin',
  ENDUSER = 'end-user',
}

export interface UserCreationInterface {
  realm: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  enabled: boolean;
  roles: UserRole[];
}

export interface UserQuery {
  email?: string;
  first?: number;
  firstName?: string;
  lastName?: string;
  max?: number;
  search?: string;
  username?: string;
  exact?: boolean;
  [key: string]: string | number | undefined | boolean;
}

export type User = {
  id: number;
  idRef: string;
  name: string;
  phone: string;
  email: string;
  firstLogin: boolean;
  position: string;
  role: string;
  fixNumber: string | null;
  password: string;
  createdAt: Date | null;
  companyId: number | null;
};
