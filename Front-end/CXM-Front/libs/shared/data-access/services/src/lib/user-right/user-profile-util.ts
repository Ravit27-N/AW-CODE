import {
  appLocalStorageConstant, CheckByLevel, CheckByType
} from '@cxm-smartflow/shared/data-access/model';
import { UserPermissionData } from '../user-permission';

export class UserProfileUtil extends UserPermissionData {

  public static canAccess(func: string, priv: string, withSupperAdmin?: boolean): boolean {
    const profiles = JSON.parse(
      <string>(
        localStorage.getItem(
          appLocalStorageConstant.UserManagement.UserPrivilege
        )
      )
    );

    if (withSupperAdmin && profiles.admin) {
      return true;
    }

    const result = profiles?.functionalities
      ?.filter((functional: any) => functional.functionalityKey === func)
      ?.map((f: any) =>
        f?.privileges?.filter((privilege: any) => privilege.key === priv)
      )[0]?.length;
    return result !== undefined && result > 0;
  }

  static getInstance() {
    return new UserProfileUtil();
  }

  public canModify(params: {
    func: string;
    priv: string;
    ownerId: number;
    checkAdmin: boolean;
  }): boolean {
    this.initUserPermissionData(params.func, params.priv, params.ownerId, params.checkAdmin, CheckByLevel.modificationLevel, CheckByType.service, true);
    // validate with admin.
    if (this.isAdmin() && params.checkAdmin) return true;
    // validate with normal user.

    return this.validateNormalUser();
  }

  public canVisibility(params: {
    func: string;
    priv: string;
    ownerId: number;
    checkAdmin: boolean;
  }): boolean {
    this.initUserPermissionData(params.func, params.priv, params.ownerId, params.checkAdmin, CheckByLevel.visibilityLevel, CheckByType.service, true);
    // validate with admin.
    if (this.isAdmin() && params.checkAdmin) return true;
    // validate with normal user.
    return  this.validateNormalUser();
  }
}
