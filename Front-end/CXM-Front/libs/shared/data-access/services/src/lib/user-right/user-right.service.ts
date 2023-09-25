import { Injectable } from '@angular/core';
import { SnackBarService } from '../snack-bar/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import {
  AuthenticationConstant,
  CanAccess,
  CheckByLevel,
  CheckByType,
  keyCloakModel
} from '@cxm-smartflow/shared/data-access/model';
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import { AuthService } from '@cxm-smartflow/auth/data-access';

@Injectable({
  providedIn: 'root'
})
export class UserRightService extends CanAccess {
  message: any;

  constructor(
    private authService: AuthService,
    private snackBarService: SnackBarService,
    private translate: TranslateService
  ) {
    super();
    this.translate.get('cannotAccess').subscribe(value => this.message = value);
  }

  /**
   * Method used to get user right.
   * @param module
   * @param feature
   * @param createdBy
   * @param checkByLevel
   * @param checkByType
   * @param muteSnackbar
   * @param validateAdmin
   * @return value of{@link boolean}
   */
  public getUserRight(module: string, feature: string, createdBy: string, checkByLevel: CheckByLevel,
                      checkByType: CheckByType, muteSnackbar?: boolean, validateAdmin?: boolean): boolean {
    this.module = module;
    this.feature = feature;
    this.createdBy = createdBy;
    this.checkByLevel = checkByLevel;
    this.checkByType = checkByType;
    this.muteSnackbar = muteSnackbar || false;
    this.validateAdmin = validateAdmin || false;

    // check is admin
    this.profile = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    if (this.profile?.admin && this.validateAdmin) return true;

    const right = this.checkRight();
    if (!right) {
      if (this.checkByType === CheckByType.service && !this.muteSnackbar) {
        this.showMessage();
      }
    }
    return right;
  }

  /**
   * Method used to get user right without level.
   * @param module
   * @param feature
   * @param muteSnackbar
   * @return value of {@link boolean}
   */
  public getUserRightWithoutLevel(module: string, feature: string, muteSnackbar?: boolean): boolean {
    const right = this.requiredRight(module, feature);

    if (!right && !this.muteSnackbar && !muteSnackbar) {
      this.showMessage();
    }

    return right;
  }


  public requiredRight(module: string, feature: string, isValidateAdmin?: boolean): boolean {

    // check is admin
    this.profile = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    if (this.profile?.admin){
      return true;
    }
    //check is Validate Admin 
    if(this.profile?.isValidateAdmin){
      return true;
      }
      else{
     
      const result = this.profile?.functionalities?.filter((functional: any) => functional.functionalityKey === module)
        ?.map((i: any) => i?.privileges?.filter((privilege: any) => privilege.key === feature))[0]?.length;

      return result !== undefined && result > 0;
        
    }
  }

  /**
   * Method used to get user privileges from local storage.
   */
  private getUserPrivileges(): void {
    const claims: keyCloakModel = this.authService.getIdentityClaims();
    this.preferredUsername = claims?.preferred_username || '';
    this.profile = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
  }

  /**
   * Method used to filter for get visibilityLevel or modificationLevel.
   * @return value of {@link boolean}
   */
  private checkRight(): boolean {
    this.getUserPrivileges();

    // Check if user don't have profile.
    if (this.profile?.functionalities?.length === 0) {
      return this.createdBy === this.preferredUsername;
    }

    // Check if user have profile and then filter for get modificationLevel or visibilityLevel.
    if (this.checkByLevel === CheckByLevel.visibilityLevel) {
      const visibilityLevel = this.profile?.functionalities?.filter((functional: any) => functional.functionalityKey === this.module)
        ?.map((i: any) => i?.privileges?.filter((privilege: any) => privilege.visibilityLevel))[0]
        ?.filter((feature: any) => feature.key === this.feature)[0]?.visibilityLevel;
      return this.checkUserRight(visibilityLevel);
    } else {
      const modificationLevel = this.profile?.functionalities?.filter((functional: any) => functional.functionalityKey === this.module)
        ?.map((i: any) => i?.privileges?.filter((privilege: any) => privilege.modificationLevel))[0]
        ?.filter((feature: any) => feature.key === this.feature)[0]?.modificationLevel;
      return this.checkUserRight(modificationLevel);
    }
  }

  /**
   * Method used to check modification level or visibility level.
   * @param level
   * @return value of {@link boolean}
   */
  private checkUserRight(level: string): boolean {
    if (level === 'service' || level === 'division' || level === 'client') {
      return this.validateUserRight();
    } else if (level === 'owner' || level === 'user') {
      return this.createdBy === this.preferredUsername;
    } else {
      return false;
    }
  }

  /**
   * Check createdBy with visibilityUsers or modificationUsers.
   * @return value of {@link boolean}
   */
  private validateUserRight(): boolean {
    if (this.checkByLevel === CheckByLevel.visibilityLevel) {
      return this.profile?.functionalities?.filter((functional: any) => functional.functionalityKey === this.module)
        ?.map((i: any) => i?.privileges)[0]?.filter((feature: any) => feature.key === this.feature)[0]?.visibilityUsers?.filter((user: string) => user === this.createdBy)[0] !== undefined;
    } else {
      return this.profile?.functionalities?.filter((functional: any) => functional.functionalityKey === this.module)
        ?.map((i: any) => i?.privileges)[0]?.filter((feature: any) => feature.key === this.feature)[0]?.modificationUsers?.filter((user: string) => user === this.createdBy)[0] !== undefined;
    }
  }

  /**
   * Method used to show message.
   */
  private showMessage(): void {
    this.snackBarService.openWarning(this.message);
  }
}
