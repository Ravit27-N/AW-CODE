import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { pluck, take } from 'rxjs/operators';
import { UserFormUpdateMode, UserFormUpdateModel } from '@cxm-smartflow/user/util';
import { TranslateService } from '@ngx-translate/core';
import { appLocalStorageConstant, appRoute, UserManagement } from '@cxm-smartflow/shared/data-access/model';
import { UserService } from './user.service';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import {
  CanAccessibilityService,
  CanModificationService,
  CanVisibilityService,
  SnackBarService
} from '@cxm-smartflow/shared/data-access/services';
import { deleteUser } from '../stores';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Store } from '@ngrx/store';

@Injectable({
  providedIn: 'any'
})
export class UserFormControlService {

  // Validation user's privileges properties.
  public isCanList = this.canAccessService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.LIST_USER, true, true);
  public isCanEdit = this.canAccessService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.EDIT_USER, true, true);
  public isCanCreate = this.canAccessService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.CREATE_USER, true, true);
  public isCanDelete = this.canAccessService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.DELETE_USER, true, true);
  public isCanModify = this.canAccessService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.MODIFY_USER, true, true);

  constructor(private readonly activatedRoute: ActivatedRoute,
              private readonly translate: TranslateService,
              private readonly router: Router,
              private readonly userService: UserService,
              private readonly authService: AuthService,
              private readonly snackbar: SnackBarService,
              private readonly store: Store,
              private readonly confirmService: ConfirmationMessageService,
              private readonly canAccessService: CanAccessibilityService,
              private readonly canVisibilityService: CanVisibilityService,
              private readonly canModificationService: CanModificationService) {
                this.translate.use(localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) || appLocalStorageConstant.Common.Locale.Fr);
  }

  // public async userFormUpdateMode(): Promise<UserFormUpdateModel> {
  //   const mode = await this.activatedRoute.queryParams.pipe(take(1), pluck('mode')).toPromise();

  //   if (mode === UserFormUpdateMode.UPDATE_MULTIPLE) {
  //     return UserFormUpdateMode.UPDATE_MULTIPLE;
  //   } else if (mode === UserFormUpdateMode.UPDATE_SINGLE) {
  //     return UserFormUpdateMode.UPDATE_SINGLE;
  //   }

  //   return UserFormUpdateMode.CREATE;
  // }

  userFormUpdateMode(): UserFormUpdateModel {
    const mode = this.activatedRoute.snapshot.queryParams.mode;
  
    if (mode === UserFormUpdateMode.UPDATE_MULTIPLE) {
      return UserFormUpdateMode.UPDATE_MULTIPLE;
    } else if (mode === UserFormUpdateMode.UPDATE_SINGLE) {
      return UserFormUpdateMode.UPDATE_SINGLE;
    }
  
    return UserFormUpdateMode.CREATE;
  }

  public navigateToList(): void {
    this.router.navigateByUrl(appRoute.cxmUser.navigateToList);
  }

  public navigateToCreate(): void {
    this.router.navigateByUrl(appRoute.cxmUser.navigateToCreate);
  }

  public async checkDuplicatedEmail(email: string): Promise<boolean> {
    return await this.userService.checkIsDuplicatedEmail(email).toPromise();
  }

  public isAdminUser(): boolean {
    return this.authService.isAdminUser();
  }

  public async getUserId() {
    return await this.activatedRoute.queryParams.pipe(take(1), pluck('id')).toPromise();
  }

  public setModificationUsersCriteriaStorage(users: string[]): void {
    localStorage.setItem(appLocalStorageConstant.UserManagement.UpdateBatchUser, JSON.stringify(users));
  }

  public getModificationUsers(): string[] {
    return JSON.parse(localStorage.getItem(appLocalStorageConstant.UserManagement.UpdateBatchUser) || '');
  }

  public removeModificationUsersCriteriaStorage(): void {
    localStorage.removeItem(appLocalStorageConstant.UserManagement.UpdateBatchUser);
  }

  public alertFailToModify(): void {
    this.translate.get('user.event.failModified').toPromise().then(message => this.snackbar.openCustomSnackbar({
      icon: 'close',
      message,
      type: 'error'
    }));
  }

  public alertDeleteSuccess(): void {
    this.translate.get('user.event.successDelete').toPromise().then(message => {
      this.snackbar.openCustomSnackbar({
        icon: 'close',
        message,
        type: 'success'
      });

      this.navigateToList();
    });
  }

  public alertFailToDelete(): void {
    this.translate.get('user.event.failDelete').toPromise().then(message => this.snackbar.openCustomSnackbar({
      icon: 'close',
      message,
      type: 'error'
    }));
  }

  public alertSuccess(keyTranslation: string): void {
    this.translate.get(keyTranslation).toPromise().then(message => {
      this.snackbar.openCustomSnackbar({
        icon: 'close',
        message,
        type: 'success'
      });

      this.navigateToList();
    });
  }

  public alertError(keyTranslation: string, navigateToList?: boolean): void {
    this.translate.get(keyTranslation).toPromise().then(message => {
      this.snackbar.openCustomSnackbar({
        icon: 'close',
        message,
        type: 'error'
      });

      if (navigateToList) {
        this.navigateToList();
      }
    });
  }

  public navigateToEditUser(param: any, isBatch?: boolean) {
    if (isBatch) {
      this.router.navigate([appRoute.cxmUser.navigateToModifyBatch], { queryParams: param });
    } else {
      this.router.navigate([appRoute.cxmUser.navigateToModify], { queryParams: param });
    }
  }

  public async confirmDelete() {
    const messages = await Promise.all([
      this.translate.get('user.delete.heading').toPromise(),
      this.translate.get('user.delete.message').toPromise(),
      this.translate.get('user.delete.cancelButton').toPromise(),
      this.translate.get('user.delete.confirmButton').toPromise(),
      this.translate.get('user.delete.action_undone').toPromise()
    ]);

    return await this.confirmService.showConfirmationPopup({
      type: 'Warning',
      icon: 'Warning',
      title: messages[0],
      message: messages[1],
      paragraph: messages[4],
      cancelButton: messages[2],
      confirmButton: messages[3]
    }).toPromise();
  }

  public async deleteUser() {
    if (await this.getUserId() && await this.confirmDelete()) {
      this.store.dispatch(deleteUser({ userIds: [await this.getUserId()] }));
    }
  }

  public checkIsCanModify(createdBy: string[]) {
    const closer = (email: string) => {
      return this.canModificationService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.MODIFY_USER, email, true, true);
    };

    if (createdBy.length > 1) {
      this.isCanModify = createdBy.every(email => closer(email));
    } else {
      this.isCanModify = this.canModificationService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.MODIFY_USER, createdBy[0], true, true);
    }
  }

  public checkIsCanDelete(createdBy: string[]) {
    const closer = (email: string) => {
      return this.canModificationService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.DELETE_USER, email, true, true);
    };

    if (createdBy.length > 1) {
      this.isCanDelete = createdBy.every(email => closer(email));
    } else {
      this.isCanDelete = this.canModificationService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.DELETE_USER, createdBy[0], true, true);
    }

  }

  public checkIsCanEdit(createdBy: string[]) {
    const closer = (email: string) => {
      return this.canVisibilityService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.EDIT_USER, email, true, true);
    };

    if (createdBy.length > 1) {
      this.isCanEdit = createdBy.every(email => closer(email));
    } else {
      this.isCanEdit = this.canVisibilityService.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.EDIT_USER, createdBy[0], true, true);
    }
  }

  /**
   * Update user privilege in localstorage.
   */
  public async updateUserPrivilegeInStorage() {
    const userPrivilege = await this.authService.getUserPrivileges().toPromise();
    localStorage.setItem(appLocalStorageConstant.UserManagement.UserPrivilege, JSON.stringify(userPrivilege));
  }

}
