import { Injectable } from '@angular/core';
import { CheckByLevel, CheckByType } from '@cxm-smartflow/shared/data-access/model';
import { UserRightService } from '../user-right';
import { UserPermissionService } from '../user-permission';

@Injectable({
  providedIn: 'root'
})
export class CanVisibilityService {
  constructor(
    private userRightService: UserRightService,
    private userPermissionService: UserPermissionService
  ) {}

  /**
   * Method used to get user right.
   * @param module
   * @param feature
   * @param createdBy
   * @param muteSnackbar
   * @param validateAdmin
   * @return value of {@link boolean}
   */
  public getUserRight(module: string, feature: string, createdBy: string, muteSnackbar?: boolean, validateAdmin?: boolean): boolean {
    return this.userRightService.getUserRight(module, feature, createdBy, CheckByLevel.visibilityLevel, CheckByType.service, muteSnackbar, validateAdmin);
  }

  /**
   * Method to validate user can visibility,
   * We call this method only before we pass to reducer (State Management),
   * We used only .ts file.
   * @param module - main module.
   * @param feature - feature of module.
   * @param createdBy - user that created.
   * @param validateAdmin - admin of system.
   * @return value of {@link boolean}.
   */
  canVisibility(module: string, feature: string, createdBy: string, validateAdmin?: boolean): boolean {
    return this.userRightService.getUserRight(module, feature, createdBy, CheckByLevel.visibilityLevel, CheckByType.service, true, validateAdmin);
  }

  hasVisibility(module: string, feature: string, ownerId: number, checkAdmin?: boolean): boolean{
    return this.userPermissionService.hasRight(module, feature, ownerId, (checkAdmin || false), CheckByLevel.visibilityLevel, CheckByType.service, true);
  }
}
