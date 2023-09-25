import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IsLoadingService } from '@service-work/is-loading';
import { UserPayload } from 'src/app/core/model/user-admin.model';
import { confirmPasswordValidator } from './form-validator';
import { UserAdminService } from '../../../core/service/user-admin.service';

@Component({
  selector: 'app-user-password-form',
  templateUrl: './user-password-form.component.html',
  styleUrls: ['./user-password-form.component.css'],
})
export class UserPasswordFormComponent implements OnInit {
  @Input() user: UserPayload;

  @Output() onsuccess = new EventEmitter();

  model: IUserPasswordForm;
  validateForm: FormGroup;

  constructor(
    private userService: UserAdminService,
    private isloadingService: IsLoadingService,
  ) {}

  ngOnInit(): void {
    this.model = { confirm: '', istemp: false, password: '' };

    this.validateForm = new FormGroup(
      {
        password: new FormControl({ value: this.model.password }, [
          Validators.required,
        ]),
        confirm: new FormControl({ value: this.model.confirm }, [
          Validators.required,
        ]),
        istemp: new FormControl({ value: this.model.istemp }),
      },
      {
        validators: confirmPasswordValidator('password', 'confirm'),
      },
    );
  }

  saveChange(): void {
    if (this.validateForm.valid) {
      const subscription = this.userService
        .setUserPassword(this.user, this.model.password, this.model.istemp)
        .subscribe(() => this.onsuccess.emit());
      this.isloadingService.add(subscription, {
        key: 'UserPasswordFormComponent',
        unique: 'UserPasswordFormComponent',
      });
    }
  }
}

interface IUserPasswordForm {
  password: string;
  confirm: string;
  istemp: boolean;
}
