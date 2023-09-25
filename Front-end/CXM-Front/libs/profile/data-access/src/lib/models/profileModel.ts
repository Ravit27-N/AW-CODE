import { EntityResponseHandler } from '@cxm-smartflow/shared/data-access/model';

export interface ProfileModel {
  id?: number;
  name: string;
  displayName: string;
  createdOn?: Date;
  clientName?: string;
  modifiedOn?: Date;
  createdBy?: string;
  ownerId?: number;
  can?: string[]
}

export type ProfileListModel = EntityResponseHandler<ProfileModel>;

export interface UserProfileModel {
  name: string;
  displayName: string;
  clientId: number;
  createdBy?: string;
  ownerId?: number;
  functionalities: Array<FunctionalityModel>;
}

export interface FunctionalityModel extends PrivilegesModel {
  privileges: Array<PrivilegesModel>
}

export interface PrivilegesModel {
  functionalityKey: string;
  visibilityLevel: string;
  modificationLevel: string;
  id: string;
}

export interface clientModel{
  id?: number;
  name?: string;
  active?: boolean;
}
