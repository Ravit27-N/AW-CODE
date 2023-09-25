import {
  AuthenticationConstant,
  CheckByLevel,
  CheckByType,
  Functionality,
  LevelConstant,
  Privilege,
  UserPrivilegeModel
} from '@cxm-smartflow/shared/data-access/model';

export class UserPermissionData {
  // protected static userPrivilege = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES)) as UserPrivilegeModel;

  protected module: string;
  protected feature: string;
  protected subFeature: string;
  protected checkByLevel: CheckByLevel | null;
  protected checkByType: CheckByType | null;
  protected muteSnackbar: boolean;
  protected checkAdmin: boolean;
  protected ownerId: number;
  protected userPrivilege: UserPrivilegeModel;

  protected initUserPermissionData(module: string, feature: string, ownerId: number, checkAdmin: boolean,
                                   checkByLevel: CheckByLevel | null, checkByType: CheckByType | null,
                                   muteSnackBar: boolean) {
    this.module = module;
    this.feature = feature;
    this.ownerId = ownerId;
    this.checkAdmin = checkAdmin;
    this.checkByLevel = checkByLevel;
    this.checkByType = checkByType;
    this.muteSnackbar = muteSnackBar;
    this.userPrivilege = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES)) as UserPrivilegeModel;
  }

  protected isBlockedAccount(): boolean {
    this.userPrivilege = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES)) as UserPrivilegeModel;
    if(this.userPrivilege?.functionalities == null || this.userPrivilege?.functionalities?.length === 0) return true;
    return false
  }

  protected getFunctionalities(): Functionality[] | null {
    return this.userPrivilege.functionalities || null;
  }

  protected getFunctionality(): Functionality | null {
    return this.userPrivilege.functionalities?.filter((func: Functionality) => func.functionalityKey === this.module)[0] || null;
  }

  protected getPrivilege(): Privilege | null {
    return this.getFunctionality()?.privileges?.filter((privilege: Privilege) => privilege.key === this.feature)[0] || null;
  }

  protected checkModule(): boolean {
    return this.userPrivilege.functionalities?.some(func => func.functionalityKey === this.module) || false;
  }

  protected checkFeature(): boolean {
    return this.getFunctionality()?.privileges?.some(priv => priv.key === this.feature) || false;
  }

  protected getVisibilityLevel(): string | null {
    return this.getPrivilege()?.visibilityLevel || null;
  }

  protected getModificationLevel(): string | null {
    return this.getPrivilege()?.modificationLevel || null;
  }

  protected getVisibilityUsers(): string[] | null {
    return this.getPrivilege()?.visibilityUsers || null;
  }

  protected getModificationUsers(): string[] | null {
    return this.getPrivilege()?.modificationUsers || null;
  }

  protected getVisibilityOwners(): number [] | null {
    return this.getPrivilege()?.visibilityOwners || null;
  }

  protected getModificationOwners(): number [] | null {
    return this.getPrivilege()?.modificationOwners || null;
  }

  protected isAdmin(): boolean {
    return this.userPrivilege.admin;
  }

  validateNormalUser(): boolean {
    // validate with not functionalities.
    if (this.userPrivilege.functionalities?.length === 0) {
      return this.userPrivilege.id === this.ownerId;
    }

    // validate with functionalities.
    if (CheckByLevel.visibilityLevel === this.checkByLevel) {
      return this.checkRightWithLevel(this.getVisibilityLevel());
    } else {
      return this.checkRightWithLevel(this.getModificationLevel());
    }
  }

  checkRightWithLevel(level: string | null): boolean {

    if ((level === null) || (level === undefined)) return false;

    if (LevelConstant.CASE1.some(value => value === level)) {
      return this.ownerId === this.userPrivilege.id;
    }

    if (LevelConstant.CASE2.some(value => value === level)) {
      if (CheckByLevel.visibilityLevel === this.checkByLevel) {
        return this.getVisibilityOwners()?.some((owner: number) => owner === this.ownerId) || false;
      } else {
        return this.getModificationOwners()?.some((owner: number) => owner === this.ownerId) || false;
      }
    }

    return false;
  }

  checkRightWithoutLevel(): boolean {
    return this.getPrivilege() !== null;
  }
}
