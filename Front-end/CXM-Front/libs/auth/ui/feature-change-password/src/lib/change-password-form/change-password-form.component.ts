import { AfterContentChecked, AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import { EMAIL_PATTERN, UserInfoModel, UserInFoModelForm } from '@cxm-smartflow/shared/data-access/model';
import { Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { MatPasswordStrengthComponent } from "@angular-material-extensions/password-strength";
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-change-password-form',
  templateUrl: './change-password-form.component.html',
  styleUrls: ['./change-password-form.component.scss']
})
export class ChangePasswordFormComponent implements OnInit, AfterContentChecked, OnDestroy {

  @ViewChild('passwordComponentWithConfirmation', {static: false})
  passwordComponentWithConfirmation: MatPasswordStrengthComponent;

  formGroup: FormGroup;
  subscription$: Subscription [] = [];
  pattern = new RegExp(/^(?=.*?[äöüÄÖÜß])/);

  toggleIcons: any = {
    currentPassword: false,
    // newPassword: false,
    // confirmPassword: false
  };

  newCurPasswordIconToggle= false;

  submitted = false;
  validationLabel: any;
  message: any;

  hide = true;
  toggleConfirmPasswordHide = true;

  constructor(private fb: FormBuilder, private translate: TranslateService,
              private dialogRef: MatDialogRef<ChangePasswordFormComponent>,
              @Inject(MAT_DIALOG_DATA) public userInfo: UserInfoModel,
              private service: AuthService, private snackBar: SnackBarService,
              private changeDetector: ChangeDetectorRef) {
    this.translate.get('changePassword.message').subscribe(label => this.message = label);

    this.formGroup = this.fb.group({
      firstName: new FormControl({ value: '', disabled: true }),
      lastName: new FormControl({ value: '', disabled: true }),
      email: new FormControl({ value: '', disabled: true }, [Validators.pattern(EMAIL_PATTERN)]),
      currentPassword: new FormControl('', [Validators.required]),
      newPassword: new FormControl('', [Validators.required]),
      passwordComponent: ["", [Validators.required, Validators.pattern]],
      confirmPassword: null,
      service: new FormControl({ value: '', disabled: true }),
      profiles: new FormControl({ value: '', disabled: true })
    });
  }

  ngOnInit(): void {
    this.translate.get('changePassword.form.validation').subscribe(label => this.validationLabel = label);

    this.formGroup.patchValue({
      ...this.userInfo,
      service: this.userInfo?.service?.divisionName +' / '+ this.userInfo?.service?.name,
      profiles: this.userInfo?.profiles?.map(item => item.name).join(', ')
    });

    this.formGroup.valueChanges.subscribe(formValue => {
      if (formValue) {
        this.submitted = false;
        const { newPassword, confirmPassword } = formValue;
        if (((newPassword as string) !== (confirmPassword as string))) {
          this.formGroup.get('confirmPassword')?.setErrors({ invalid: true });
        } else {
          this.formGroup.get('confirmPassword')?.setErrors(null);
        }
      }
    });
  }

  ngAfterContentChecked(): void {
    this.changeDetector.detectChanges();
    this.formGroup.setControl('newPassword', this.passwordComponentWithConfirmation.passwordConfirmationFormControl);
    this.formGroup.setControl('confirmPassword', this.passwordComponentWithConfirmation.passwordConfirmationFormControl);
    this.passwordComponentWithConfirmation.passwordConfirmationFormControl.valueChanges.subscribe(() => {
      this.passwordComponentWithConfirmation.passwordConfirmationFormControl.markAsTouched();
    });
  }

  onStrengthChanged(strength: number) {
    //console.log("",strength);
}

  close(): void {
    this.dialogRef.close();
  }

  submit(): void {
    //this.formGroup.setControl('newPassword', this.passwordComponentWithConfirmation.passwordConfirmationFormControl);
    if (this.formGroup.valid) {
      this.formGroup.removeControl('passwordComponent');
      this.service.updateUserPassword(this.formGroup.value as UserInFoModelForm)
        .subscribe(() => {
            this.snackBar.openCustomSnackbar({ message: this.message?.success, type: 'success', icon: 'close' });
            this.dialogRef.close();
          },
          (error: HttpErrorResponse) => {
            if (error.status) {
              this.submitted = true;
              if (error.status === HttpStatusCode.NotFound) { // Invalid current password.
                this.formGroup.get('currentPassword')?.setErrors({ invalid: true });
                this.translate.get('forgot-password.current_password_is_incorrect').toPromise().then(message => {
                  this.snackBar.openCustomSnackbar({ icon: 'close', type: 'error', message })});
              } else if (error.status === HttpStatusCode.BadRequest) { // New password not match with confirm password.
                this.formGroup.get('confirmPassword')?.setErrors({ invalid: true });
              } else if (error.status === HttpStatusCode.PreconditionRequired){
                this.formGroup.addControl('passwordComponent', new FormControl('', [Validators.required]));
                this.formGroup.get('passwordComponent')?.setErrors({ invalid: true });
                this.translate.get('forgot-password.error_password_already_used').toPromise().then(message => {
                this.snackBar.openCustomSnackbar({ icon: 'close', type: 'error', message })});
               }
              else {
                this.formGroup.get('currentPassword')?.setErrors(null);
                this.formGroup.get('confirmPassword')?.setErrors(null);
                this.snackBar.openCustomSnackbar({ message: this.message?.fail, type: 'error', icon: 'close' });
              }
            }
          });
    } else {
      this.submitted = true;
    }
  }

  toggleIcon(name: string) {
    this.toggleIcons[`${name}`] = !this.toggleIcons[`${name}`];
  }

  newCurPasswordToggleIcon(){
    this.newCurPasswordIconToggle = !this.newCurPasswordIconToggle;
  }

  get currentPasswordTooltip() {
    return ((this.formGroup.get('currentPassword')?.errors?.required
      || this.formGroup.get('currentPassword')?.errors?.invalid) && this.submitted) === true;
  }

  get currentPasswordTooltipLabel() {
    if (this.formGroup.get('currentPassword')?.errors?.required) {
      return this.validationLabel?.currentPasswordRequired;
    }

    return this.validationLabel?.currentPasswordIncorrect;
  }

  get newPasswordTooltip() {
    return (this.formGroup.get('newPassword')?.errors?.required && this.submitted) === true;
  }

  get newPasswordTooltipLabel() {
    if (this.formGroup.get('newPassword')?.errors?.required) {
      return this.validationLabel?.newPasswordRequired;
    }
    return '';
  }

  get confirmPasswordTooltip() {
    return ((this.formGroup.get('confirmPassword')?.errors?.required
        || this.formGroup.get('confirmPassword')?.errors?.invalid)
      && this.formGroup.get('newPassword')?.valid
      && this.submitted) === true;
  }

  get confirmPasswordTooltipLabel() {
    // if (this.formGroup.get('confirmPassword')?.errors?.required) {
    //   return this.validationLabel?.confirmNewPasswordRequired;
    // }
    return this.validationLabel?.confirmNewPasswordNotMatch;
  }

  ngOnDestroy(): void {
    this.subscription$.forEach(sub => sub.unsubscribe());
  }

}
