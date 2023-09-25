import {FragmentReturnAddressType} from "@cxm-smartflow/shared/fragments/return-address";

export interface UserModel {
  id: string;
  fullName: string;
  email: string;
  password: string;
  confirmedPassword: string;
  serviceId: number;
  profiles: ProfileAssigned [];
}

export interface UserList {
  contents: UserModel[];
  page: number;
  pageSize: number;
  total: number;
}

export interface ISingleEditedUser {
  id?: string;
  email?: string;
  serviceId?: number;
  profiles?: ProfileAssigned [];
  name?: string;
}

export interface UserDetail {
  id: string;
  // name?: string;
  firstName: string;
  lastName: string;
  email: string;
  admin: boolean;
  client: ClientAssigned;
  service: ServiceAssigned;
  profiles: ProfileAssigned [];
  returnAddressLevel?: string;
  userReturnAddress?: FragmentReturnAddressType
}

export interface ClientAssigned{
  id: number,
  name: string
}

export interface ServiceAssigned {
  id: number,
  name: string,
  clientName:string //add new
}

export interface ProfileAssigned {
  id: number;
  name: string;
}

export interface ServiceModel {
  id: number,
  name: string,
  active: boolean,
  divisionId: number
}

export interface CreateUserRequestModel {
  firstName: string,
  lastName: string,
  email: string,
  serviceId: number,
  password: string,
  confirmedPassword: string,
  profiles: number[],
  returnAddressLevel?: string,
  userReturnAddress?: FragmentReturnAddressType
}

export interface UpdateUserRequestModel {
  id: string,
  firstName: string,
  lastName: string,
  serviceId: number,
  password?: string,
  confirmedPassword?: string,
  profiles: number[],
  admin: boolean;
  returnAddressLevel?: string,
  userReturnAddress?: FragmentReturnAddressType
}

export interface exportUsersCsv {
  profileIds: number[];
  userType: string[];
  clientIds: number[];
  divisionIds: number[];
  serviceIds: number[];
  filter: string;
  filename: string;
}

export interface KeyVal {
  key: number | string;
  value: string;
  val: string;
}
