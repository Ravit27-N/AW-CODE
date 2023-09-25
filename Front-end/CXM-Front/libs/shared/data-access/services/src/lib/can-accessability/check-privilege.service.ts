import { SnackBarService } from '../snack-bar/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { AuthenticationConstant } from '@cxm-smartflow/shared/data-access/model';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'any'
})
export class CheckPrivilegeService {

  profile = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
  privileges: string[] = [];

  constructor(private snackbarService: SnackBarService,
              private translate: TranslateService,
              private router: Router) {
             this.setup();
  }

  private setup() {
    this.profile = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    this.profile?.functionalities.forEach((v: any) => {
      // add feature privileges
      v?.privileges?.forEach((v: any) => {
        if (!this.privileges.includes(v?.key)) {
          this.privileges = [...this.privileges, v?.key];
        }
      });
      // add module privileges
      if (v?.privileges?.length > 0 && !this.privileges.includes(v?.functionalityKey)) {
        this.privileges = [...this.privileges, v?.functionalityKey];
      }
    });
  }

  /**
   * Method used to get user right.
   * @param module
   * @param feature
   * @param isValidateAdmin
   * @return value of {@link boolean}
   */
  public getUserRight(module: string, feature: string, isValidateAdmin?: boolean): boolean {
    // check is admin
    if (this.profile?.admin && isValidateAdmin) return true;

    const userPrivileges = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    const isUserRight = userPrivileges?.functionalities.filter((v: any) => v?.functionalityKey === module)[0]
      ?.privileges?.filter((v: any) => v?.key === feature)[0]
      ?.key;

    return !!isUserRight;
  }

  public validateUserRight(module: string, feature: string, isValidateAdmin?: boolean): boolean {
    // check is admin
    if (this.profile?.admin && isValidateAdmin) return true;

    const userPrivileges = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    const isUserRight = userPrivileges?.functionalities.filter((v: any) => v?.functionalityKey === module)[0]
      ?.privileges?.filter((v: any) => v?.key === feature)[0]
      ?.key;

    if (!isUserRight) {
      this.translate.get('cannotAccess').subscribe(value => this.snackbarService.openWarning(value));
    }
    return !!isUserRight;
  }

  public validateUserRightAndNavigate(module: string, feature: string, url: string, isValidateAdmin?: boolean): boolean {
    // check is admin
    if (this.profile?.admin && isValidateAdmin) return true;

    const userPrivileges = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    const isUserRight = userPrivileges?.functionalities.filter((v: any) => v?.functionalityKey === module)[0]
      ?.privileges?.filter((v: any) => v?.key === feature)[0]
      ?.key;

    if (!isUserRight) {
      this.router.navigateByUrl(url);
    }
    return !!isUserRight;
  }

  public validateUserRightAndNavigateWithSnackbar(module: string, feature: string, url: string, isValidateAdmin?: boolean): boolean {
    // check is admin
    if (this.profile?.admin && isValidateAdmin) return true;

    const userPrivileges = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    const isUserRight = userPrivileges?.functionalities.filter((v: any) => v?.functionalityKey === module)[0]
      ?.privileges?.filter((v: any) => v?.key === feature)[0]
      ?.key;

    if (!isUserRight) {
      this.translate.get('cannotAccess').subscribe(value => this.snackbarService.openWarning(value));
      this.router.navigateByUrl(url);
    }
    return !!isUserRight;
  }

  public validateUserPrivilege(feature: string, isValidateAdmin?: boolean): boolean {
    this.setup();
    // check is admin
    if (this.profile?.admin && isValidateAdmin) return true;
    // normal user
    return this.privileges.includes(feature);
  }

  public clearOldData(): void {
    this.privileges = [];
  }
}
