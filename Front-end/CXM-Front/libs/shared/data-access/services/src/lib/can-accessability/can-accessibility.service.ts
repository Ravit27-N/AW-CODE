import { Injectable } from '@angular/core';
import { UserRightService } from '../user-right';
import { UserPermissionService } from '../user-permission';

@Injectable({
  providedIn: 'root'
})
export class CanAccessibilityService {

  constructor(private userRightService: UserRightService,
              private userPermissionService: UserPermissionService) {
  }

  /**
   * Method used to get user right.
   * @param module
   * @param feature
   * @param muteSnackbar
   * @param isValidateAdmin
   * @return value of {@link boolean}
   */
  public getUserRight(module: string, feature: string, muteSnackbar?: boolean, isValidateAdmin?: boolean): boolean {
    if(this.userPermissionService.hasBlockedAccount()) return false;
    return this.canAccessible(module, feature, isValidateAdmin, muteSnackbar);
  }

  /**
   * Method to validate user can accessible or can activated,
   * We call this method only in .ts file.
   * @param module
   * @param feature
   * @param validateAdmin
   * @param muteSnackbar
   * @return value of {@link boolean}.
   */
  canAccessible(module: string, feature: string, validateAdmin?: boolean, muteSnackbar?: boolean): boolean {
    if(this.userPermissionService.hasBlockedAccount()) return false;
    return this.userPermissionService.hasRightWithoutLevel(module, feature, (validateAdmin || false), (muteSnackbar || true));
  }

  hasActiveAccount(): boolean {
    if(this.userPermissionService.hasBlockedAccount()) return false;
    return true;
  }
}
