import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { BehaviorSubject, Subscription } from 'rxjs';
import { UserAdminService } from '../../../core/service/user-admin.service';
import { AwSnackbarService } from '../../../shared';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-feature-user-add',
  templateUrl: './feature-user-add.component.html',
  styleUrls: ['./feature-user-add.component.scss'],
})
export class FeatureUserAddComponent implements OnInit, OnDestroy {
  form: FormGroup;
  slideValue = 'Active';
  subscription = new Subscription();
  emailValidator = new BehaviorSubject<boolean>(false);
  usernameValidator = new BehaviorSubject<boolean>(false);
  showPassword: boolean;
  showConfirmPassword: boolean;
  isUpdateUser = new BehaviorSubject<boolean>(false);
  userId: string;

  constructor(
    private formBuilder: FormBuilder,
    private userAdminService: UserAdminService,
    private awSnackbarService: AwSnackbarService,
    private activateRoute: ActivatedRoute,
  ) {
    this.form = this.formBuilder.group({});
  }

  ngOnInit(): void {
    this.initFormUser();
    this.formChanged();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    this.usernameValidator.unsubscribe();
    this.emailValidator.unsubscribe();
  }

  initFormUser(): void {
    this.userId = this.activateRoute.snapshot.queryParams.userId;
    const localData = JSON.parse(localStorage.getItem('create-user'));
    this.form.addControl(
      'username',
      new FormControl({
        value: localData?.username || '',
        disabled: this.userId,
      }),
    );
    this.form.addControl(
      'firstName',
      new FormControl(localData?.firstName || ''),
    );
    this.form.addControl(
      'lastName',
      new FormControl(localData?.lastName || ''),
    );
    this.form.addControl(
      'email',
      new FormControl(
        { value: localData?.email || '', disabled: this.userId },
        [Validators.email],
      ),
    );
    this.form.addControl(
      'enabled',
      new FormControl(localData?.enabled ? localData?.enabled : true),
    );
    this.form.addControl(
      'password',
      new FormControl(localData?.password || ''),
    );
    this.form.addControl('confirm', new FormControl(localData?.confirm || ''));
    if (this.userId) {
      this.userAdminService.getById(this.userId).subscribe((result) => {
        this.form.patchValue(
          {
            ...result,
          },
          { emitEvent: false },
        );
        this.isUpdateUser.next(true);
      });
    }
  }

  formChanged(): void {
    const subscription = this.form.valueChanges.subscribe((result) => {
      localStorage.setItem('create-user', JSON.stringify(result));
    });
    this.subscription.add(subscription);
  }

  onChange(): void {
    if (this.form.get('enabled').value === false) {
      this.slideValue = 'Active';
    } else {
      this.slideValue = 'Inactive';
    }
  }

  async submit(): Promise<void> {
    if (this.validateFormData()) {
      this.setErrorUsername();
      this.setErrorFirstName();
      this.setErrorLastName();
      this.setErrorEmail();
      this.setErrorPassword();
      this.setErrorConfirmPassword();
      return;
    }

    const isDuplicateUsername = await this.validateDuplicateUsername(
      this.form.get('username').value,
    );
    const isDuplicateEmail = await this.validateDuplicateEmail(
      this.form.get('email').value,
    );
    if (!isDuplicateEmail && !isDuplicateUsername) {
      if (this.isUpdateUser.value) {
        await this.updateUser();
      } else {
        await this.saveUser();
      }
    }
  }

  async saveUser(): Promise<void> {
    this.userAdminService
      .create({
        ...this.form.value,
        credentials: [
          {
            value: this.form.get('password').value,
          },
        ],
      })
      .toPromise()
      .then(() => {
        localStorage.removeItem('create-user');
        history.back();
        this.showSuccessMessage('Create user successfully.');
      })
      .catch(() => {
        this.showErrorMessage(
          'Create user error cannot communicating with the server',
        );
      });
  }

  async updateUser(): Promise<void> {
    this.userAdminService
      .update({
        ...this.form.value,
        id: this.userId,
        credentials: [
          {
            value: this.form.get('password').value,
          },
        ],
      })
      .toPromise()
      .then(() => {
        localStorage.removeItem('create-user');
        history.back();
        this.showSuccessMessage('Update user successfully.');
      })
      .catch(() => {
        this.showErrorMessage(
          'Update user error cannot communicating with the server',
        );
      });
  }

  setErrorUsername(): void {
    if (
      !this.form.get('username').value ||
      this.form.get('username').value.length > 255
    ) {
      this.usernameValidator.next(false);
      this.form.get('username').setErrors({ incorrect: true });
    }
  }

  setErrorFirstName(): void {
    if (
      !this.form.get('firstName').value ||
      this.form.get('firstName').value.length > 255
    ) {
      this.form.get('firstName').setErrors({ incorrect: true });
    }
  }

  setErrorLastName(): void {
    if (
      !this.form.get('lastName').value ||
      this.form.get('lastName').value.length > 255
    ) {
      this.form.get('lastName').setErrors({ incorrect: true });
    }
  }

  setErrorEmail(): void {
    if (
      !this.form.get('email').value ||
      this.form.get('email').value.length > 255
    ) {
      this.emailValidator.next(false);
      this.form.get('email').setErrors({ incorrect: true });
    }
  }

  setErrorPassword(): void {
    if (!this.form.get('password').value) {
      this.form.get('password').setErrors({ incorrect: true });
    }
  }

  setErrorConfirmPassword(): void {
    if (
      !this.form.get('confirm').value ||
      this.form.get('confirm').value !== this.form.get('password').value
    ) {
      this.form.get('confirm').setErrors({ incorrect: true });
    }
  }

  async validateDuplicateEmail(email: string): Promise<boolean> {
    return this.isUpdateUser.value
      ? false
      : await this.userAdminService
          .validateEmail(email)
          .toPromise()
          .then((emailValidate: boolean) => {
            if (emailValidate) {
              this.form.get('email').setErrors({ incorrect: true });
            }
            this.emailValidator.next(emailValidate);
            return emailValidate;
          });
  }

  async validateDuplicateUsername(username: string): Promise<boolean> {
    return this.isUpdateUser.value
      ? false
      : await this.userAdminService
          .validateUsername(username)
          .toPromise()
          .then((usernameValidate: boolean) => {
            if (usernameValidate) {
              this.form.get('username').setErrors({ incorrect: true });
            }
            this.usernameValidator.next(usernameValidate);
            return usernameValidate;
          });
  }

  private showErrorMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  validateFormData(): boolean {
    return (
      this.form.invalid ||
      !this.form.get('username').value ||
      this.form.get('username').value.length > 255 ||
      !this.form.get('firstName').value ||
      this.form.get('firstName').value.length > 255 ||
      !this.form.get('lastName').value ||
      this.form.get('lastName').value.length > 255 ||
      !this.form.get('email').value ||
      this.form.get('email').value.length > 255 ||
      !this.form.get('password').value ||
      !this.form.get('confirm').value ||
      this.form.get('confirm').value !== this.form.get('password').value
    );
  }

  cancel(): void {
    history.back();
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
}
