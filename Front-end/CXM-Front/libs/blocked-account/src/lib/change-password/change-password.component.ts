import { 
  Component, 
  Inject, 
  OnDestroy, 
  OnInit,
  ChangeDetectionStrategy,
  ViewEncapsulation,
  ViewChild,
  AfterViewInit,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import { PasswordActionType, UserInFoModelForm, UserLoginAttempt } from '@cxm-smartflow/shared/data-access/model';
import { BehaviorSubject, Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { CanAccessibilityService, SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { combineDynamicVersion, CustomFooterModel } from '@cxm-smartflow/shared/common-typo';
import { CustomHeaderHandlerService } from 'libs/shared/ui/layout/src/lib/header/custom-header-handler.service';
import { Router } from '@angular/router';
import { MatPasswordStrengthComponent } from "@angular-material-extensions/password-strength";

@Component({
  selector: 'cxm-smartflow-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangePasswordComponent implements AfterViewInit {

  formGroup: FormGroup;
  showDetails: boolean;
  pattern = new RegExp(/^(?=.*?[äöüÄÖÜß])/);
  leftFooters: CustomFooterModel [] = [];
  rightFooters: CustomFooterModel [] = [];
  validationLabel: any;
  submitted$ = new BehaviorSubject<boolean>(false);
  message: any;
  @ViewChild('passwordComponentWithConfirmation', {static: false})
  passwordComponentWithConfirmation: MatPasswordStrengthComponent;

  constructor(private fb: FormBuilder, private translate: TranslateService,
    private authService: AuthService, private snackBar: SnackBarService,
    private headerHandlerService: CustomHeaderHandlerService,
    private router: Router,
    private canAccess: CanAccessibilityService) {
      this.submitted$.next(false);
      this.translate.get('changePassword.message').subscribe(label => this.message = label);
      if(this.canAccess.hasActiveAccount()) this.router.navigateByUrl('dashboard');
      this.translate.get('footerLogin').subscribe(response => {
        this.leftFooters = combineDynamicVersion(response?.left) || [];
        this.rightFooters = response?.right;
      });
      
      this.formGroup = this.fb.group({
        passwordComponent: ["", [Validators.required, Validators.pattern]],
        confirmPassword:null
      });
    }

ngAfterViewInit(): void {
  this.formGroup.setControl('confirmPassword', this.passwordComponentWithConfirmation.passwordConfirmationFormControl);
  this.passwordComponentWithConfirmation.passwordConfirmationFormControl.valueChanges.subscribe(() => {
    this.formGroup.markAllAsTouched();
  });
}

onStrengthChanged(strength: number) {
    //console.log("",strength);
}

submit(): void {
  if (this.formGroup.valid) {
    this.submitted$.next(true);
    const passwordDto = <UserInFoModelForm>{
      newPassword : this.formGroup.value.passwordComponent,
      confirmPassword: this.formGroup.value.confirmPassword,
      actionType: PasswordActionType.UNBLOCKED
     };
    this.authService.updateUserPassword(passwordDto)
      .subscribe(() => {
         const userLoginAttemptRequest = <UserLoginAttempt>{
            userName : "",
            loginStatus: false
          };
          this.headerHandlerService.getUser().subscribe(user => userLoginAttemptRequest.userName = user);
          this.authService.postUserLoginAttempt(userLoginAttemptRequest).subscribe(response => {
                this.submitted$.next(false);
                setTimeout(() => {
                  this.snackBar.openCustomSnackbar({ message: this.message?.success, type: 'success', icon: 'close' });
                  this.authService.logout();
              }, 5000);
              
          });
        },
        (error: HttpErrorResponse) => {
          if (error.status) {
            this.submitted$.next(false);
            if (error.status === HttpStatusCode.NotFound) { 
              // Invalid current password.
              this.formGroup.get('passwordComponent')?.setErrors({ invalid: true });
            } else if (error.status === HttpStatusCode.BadRequest) { 
              // New password not match with confirm password.
              this.formGroup.get('confirmPassword')?.setErrors({ invalid: true });
            } else if (error.status === HttpStatusCode.PreconditionRequired){
              this.formGroup.get('passwordComponent')?.setErrors({ invalid: true });
              this.translate.get('forgot-password.error_password_already_used').toPromise().then(message => {
              this.snackBar.openCustomSnackbar({ icon: 'close', type: 'error', message })});
             } else {
              this.formGroup.get('passwordComponent')?.setErrors(null);
              this.formGroup.get('confirmPassword')?.setErrors(null);
            }
          }
          this.submitted$.next(false);
        });
  } else {
    this.submitted$.next(false);
  }
}

public cancel() {
  this.authService.logout();
}

}

