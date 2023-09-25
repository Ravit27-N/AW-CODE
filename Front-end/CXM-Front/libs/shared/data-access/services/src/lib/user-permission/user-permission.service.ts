import { Injectable } from '@angular/core';
import { CheckByLevel, CheckByType } from '@cxm-smartflow/shared/data-access/model';
import { UserPermissionData } from './user-permission-adapter';
import { SnackBarService } from '../snack-bar/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserPermissionService extends UserPermissionData {

  message: any;

  constructor(private snackBarService: SnackBarService,
              private translate: TranslateService, 
              private router: Router) {
    super();
    this.translate.get('cannotAccess').subscribe(value => this.message = value);
  }

  hasRight(module: string, feature: string, ownerId: number, checkAdmin: boolean,
           checkByLevel: CheckByLevel, checkByTpe: CheckByType, muteSnackBar: boolean): boolean {
    // init data to user permission.
    if(this.isBlockedAccount()) return false;
    this.initUserPermissionData(module, feature, ownerId, checkAdmin, checkByLevel, checkByTpe, muteSnackBar);

    // validate with admin.
    if (this.isAdmin() && checkAdmin) return true;

    // validate with normal user.
    const hasRight = this.validateNormalUser() || false;
    if ((!hasRight) && (!this.muteSnackbar) && (this.checkByType === CheckByType.service)) {
      this.snackBarService.openWarning(this.message);
    }

    return hasRight;
  }

   hasBlockedAccount(): boolean {
    const currentRoute = this.router.url.toLowerCase();
    if(this.isBlockedAccount() && !((currentRoute === '/blocked-account') || 
    (currentRoute === '/forgot-password') || (currentRoute === '/about'))) {
      this.router.navigateByUrl('blocked-account');
    }
    return this.isBlockedAccount();
  }

  hasRightWithoutLevel(module: string, feature: string, checkAdmin: boolean, muteSnackBar: boolean): boolean {
    // init data to user permission.
    if(this.isBlockedAccount()) return false;
    this.initUserPermissionData(module, feature, 0, checkAdmin, null, null, muteSnackBar);

    // validate with admin.
    if (this.isAdmin() && checkAdmin) return true;

    // validate with normal user.
    const right = this.checkRightWithoutLevel();
    if ((!right) && (!muteSnackBar)) {
      this.snackBarService.openWarning(this.message);
    }

    return right;
  }

  hasModule(module: string, checkAdmin: boolean): boolean {
    if(this.isBlockedAccount()) return false;
    // init data to user permission.
    this.initUserPermissionData(module, '', 0, checkAdmin, null,
      null, true);

    if(this.isAdmin() && checkAdmin) return true;

    return this.checkModule();
  }
}
