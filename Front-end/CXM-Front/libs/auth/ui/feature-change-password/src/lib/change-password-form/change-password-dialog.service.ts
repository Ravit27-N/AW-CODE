import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ChangePasswordFormComponent } from './change-password-form.component';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import { HttpErrorResponse } from '@angular/common/http';
import { UserInfoModel } from '@cxm-smartflow/shared/data-access/model';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';

@Injectable({
  providedIn: 'root'
})
export class ChangePasswordDialogService {

  dialogRef: MatDialogRef<ChangePasswordFormComponent>;
  message: any;

  constructor(private matDialog: MatDialog, private authService: AuthService,
              private translate: TranslateService, private snackBar: SnackBarService) {

    this.translate.get('changePassword.message').subscribe(label => this.message = label);
  }

  public showForm(): void {
    this.authService.getUserInfoByToken()
      .subscribe((userInfo: UserInfoModel) => {
        this.dialogRef = this.matDialog.open(ChangePasswordFormComponent, {
          width: '1010px',
          panelClass: 'custom-change-password-pop-up-dialog',
          data: userInfo,
          disableClose: true
        });
      }, (error: HttpErrorResponse) => {
        this.snackBar.openCustomSnackbar({ message: this.message?.loadUserInFail, type: 'error', icon: 'close' });
      });
  }
}
