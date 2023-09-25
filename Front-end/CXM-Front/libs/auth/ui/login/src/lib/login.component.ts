import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AuthService, loadAuthLogin } from '@cxm-smartflow/auth/data-access';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subject } from 'rxjs';
import {
  combineDynamicVersion,
  CustomFooterModel,
} from '@cxm-smartflow/shared/common-typo';
import { REGEXP } from '@cxm-smartflow/shared/utils';
import { distinctUntilChanged, pluck, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-login',
  templateUrl: './login-new.component.html',
  styleUrls: ['./login-new.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {

  signIn: FormGroup;
  showSpinner = new BehaviorSubject(false);

  showPassword = false;

  leftFooters: CustomFooterModel [] = [];
  rightFooters: CustomFooterModel [] = [];
  isUsernameErrorOnBlur = false;

  hasSubmit = false;
  destroy$ = new Subject<boolean>();

  constructor(
    private formBuilder: FormBuilder,
    private store: Store,
    private authService: AuthService,
    private translate: TranslateService
  ) {
    this.signIn = this.formBuilder.group({
      username: ['', [Validators.required, Validators.pattern(REGEXP.emailPattern)]],
      password: ['', [Validators.required]]
    });

    this.translate.get('footerLogin').subscribe(response => {
      this.leftFooters = combineDynamicVersion(response?.left) || [];
      this.rightFooters = response?.right;
    });
  }

  ngOnInit(): void {
    this.authService.removeUserPrivilegesFromStorage();

    // this.store.pipe(select(getAuth)).subscribe((response) => this.showSpinner.next(response?.showSpinner));
    // this.store.pipe(select(selectUserCredential)).subscribe((response: UserCredentialModel) => {
    //   if (!response?.usernameValid) this.username?.setErrors({ notexisted: true });
    //   if (!response?.passwordValid) this.password?.setErrors({ notexisted: true });
    // });
    this.transformUsername();
  }

  transformUsername(): void {
    // Transform username by removing space character.
    this.signIn.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        pluck('username'),
        distinctUntilChanged((prev, curr) => prev === curr)
      )
      .subscribe((username) => {
        this.signIn.controls['username'].setValue(username.trim());
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  get username() {
    return this.signIn.get('username');
  }

  get password() {
    return this.signIn.get('password');
  }

  get isUsernameError() {
    if(!this.username?.errors?.pattern) {
      this.isUsernameErrorOnBlur = false;
    }
    return this.signIn.controls['username']?.invalid && this.hasSubmit;
  }

  usernameErrorOnBlur() {
    this.isUsernameErrorOnBlur = this.signIn.controls['username']?.invalid && this.username?.errors?.pattern;
  }

  get usernameErrorMessage() {
    let value = '';
    // if (this.username?.errors?.notexisted) {
    //   this.translate.get('login.form.username.notexisted').subscribe(v => value = v);
    // } else
    if(this.username?.errors?.pattern)
    {
      this.translate.get('login.form.username.invalid').subscribe(v => value = v);
    }
    else {
      this.translate.get('login.form.username.required').subscribe(v => value = v);
    }
    return value;
  }

  get isPasswordError() {
    return this.signIn.controls['password']?.invalid && this.hasSubmit;
  }

  get passwordErrorMessage() {
    let value = '';
    // if (this.password?.errors?.notexisted) {
    //   this.translate.get('login.form.password.invalid').subscribe(v => value = v);
    // } else {
      this.translate.get('login.form.password.required').subscribe(v => value = v);
    // }
    return value;
  }

  login(): void {
    this.hasSubmit = true;
    if (this.signIn.valid) {
      this.store.dispatch(loadAuthLogin({
        loginForm: {
          username: this.username?.value,
          password: this.password?.value
        }
      }));
    }
  }

  togglerShowPw() {
    this.showPassword = !this.showPassword;
  }

}
