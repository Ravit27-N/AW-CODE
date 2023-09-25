import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HubDistributePopupModel } from '@cxm-smartflow/client/data-access';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { BehaviorSubject, Subscription } from 'rxjs';
import { HubDistributePopupValidator } from './hub-distribute-popup-validator';

@Component({
  selector: 'cxm-smartflow-hub-distribute-popup',
  templateUrl: './hub-distribute-popup.component.html',
  styleUrls: ['./hub-distribute-popup.component.scss'],
})
export class HubDistributePopupComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;
  unsubscription$: Subscription;
  error$ = new BehaviorSubject<boolean>(false);
  passwordVisible = false;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public config: HubDistributePopupModel,
    private _dialogRef: MatDialogRef<HubDistributePopupComponent>,
    private _formBuilder: FormBuilder,
  ) {}

  ngOnInit(): void {
    this._setupForm();
    this._detectFormChange();
  }

  ngOnDestroy() {
    this.formGroup.reset();
    this.unsubscription$.unsubscribe();
  }

  private _setupForm(): void {
    this.formGroup = this._formBuilder.group({
      email: new FormControl(this.config.email, [HubDistributePopupValidator.fieldEmail()]),
      password: new FormControl(this.config.password, [HubDistributePopupValidator.fieldPassword(false)]),
    });
  }

  private _detectFormChange(): void {
    this.unsubscription$ = this.formGroup.valueChanges.subscribe(() => this.error$.next(false));
  }

  register(): void {
    if (this.formGroup.invalid) {
      this.error$.next(true);
      return;
    }

    const { email, password } = this.formGroup.getRawValue();
    const formData: HubDistributePopupModel = { email: (email as string).toLowerCase(), password };

    this._dialogRef.close(formData);
  }

  closeModal(): void {
    this._dialogRef.close();
  }

  togglePasswordIcon() {
    this.passwordVisible = !this.passwordVisible;
  }
}
