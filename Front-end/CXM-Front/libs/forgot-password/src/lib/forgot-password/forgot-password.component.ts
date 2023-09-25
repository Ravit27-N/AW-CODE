import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import {
  clearErrorForgotState,
  requesetResetPassword,
  requestChangePassword,
  selectForgotState,
  unloadForgotForm
} from '../store/forgot-password.store';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import { ActivatedRoute, Router } from '@angular/router';
import { combineDynamicVersion, CustomFooterModel } from '@cxm-smartflow/shared/common-typo';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

const matchMatchPassword: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.parent?.get('password');
  const confirmPassword = control.parent?.get('confirmPassword');

  if(!password?.value || !confirmPassword?.value) {
    return { notMatched: true };
  }

  if(password?.touched || confirmPassword?.touched || password?.valueChanges || confirmPassword?.valueChanges) {
    if(confirmPassword?.valueChanges && confirmPassword?.value.length > 0 && confirmPassword?.value !== password?.value) {
      confirmPassword?.setErrors({ notMatched: true });
    } else {
      confirmPassword?.setErrors(null);
    }

    if(password?.valueChanges && password?.value.length > 0 && password?.value !== confirmPassword?.value) {
      password?.setErrors({ notMatched: true });
    } else {
      password?.setErrors(null);
    }
  }
  return null;
}

@Component({
  selector: 'cxm-smartflow-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit, OnDestroy {

  forgotPassword: FormGroup;
  resetPassword: FormGroup;

  forgotState: any;
  subscription: Subscription;

  showPassword = false;

  leftFooters: CustomFooterModel [] = [];
  rightFooters: CustomFooterModel [] = [];

  ngOnInit(): void {
    this.authService.clearHeader();
    this.subscription = this.store.select(selectForgotState).subscribe(state => this.forgotState = state);
  }


  forgot() {
   if(!this.forgotPassword.valid) {
    this.forgotPassword.markAllAsTouched();
   } else {
     const { email } = this.forgotPassword.value;
    this.store.dispatch(requestChangePassword({ email }));
   }
  }

  reset() {
    if(!this.resetPassword.valid) {
      this.resetPassword.markAllAsTouched();
     } else {
      const { password } = this.resetPassword.value;
      this.store.dispatch(requesetResetPassword({ password }));
     }
  }

  togglerShowPw() {
    this.showPassword = !this.showPassword;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.store.dispatch(unloadForgotForm());
  }

  backToLogin(): void {
    this.router.navigateByUrl('/login');
  }

  get getPassword() {
    return this.resetPassword.get('password')?.value;
  }

  get getConfirm() {
    return this.resetPassword.get('confirmPassword')?.value;
  }

  get isEmailError() {
    return this.forgotPassword.controls['email']?.touched && this.forgotPassword.controls['email']?.invalid;
  }

  get isPasswordError() {
    return this.resetPassword.controls['password']?.touched && this.resetPassword.controls['password']?.invalid;
  }

  get isConfirmPasswordError() {
    return this.resetPassword.controls['confirmPassword']?.touched && this.resetPassword.controls['confirmPassword']?.invalid;
  }

  get isEmailNotExisted() {
    return this.forgotState && this.forgotState.error && this.forgotState.error.status >= 400;
  }

  constructor(private fb: FormBuilder, private translate: TranslateService, private store: Store, private authService: AuthService, private activateRoute: ActivatedRoute,
    private router: Router
    ) {

    this.translate.get('footerLogin').subscribe(response => {
      this.leftFooters = combineDynamicVersion(response?.left) || [];
      this.rightFooters = response?.right;
    });

    this.forgotPassword = fb.group({
      email: new FormControl('', [Validators.required, Validators.email])
     })

     this.resetPassword = fb.group({
      password: new FormControl('', [Validators.required, matchMatchPassword]),
      confirmPassword: new FormControl('', [Validators.required, matchMatchPassword])
     })


     this.forgotPassword.valueChanges.pipe(distinctUntilChanged(), debounceTime(300))
     .subscribe(() => this.store.dispatch(clearErrorForgotState()))
   }

}
