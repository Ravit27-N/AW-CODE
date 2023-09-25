import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {
  ServiceProvider,
  ServiceProviderFormModel
} from '@cxm-smartflow/client/data-access';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-service-provider-popup',
  templateUrl: './service-provider-popup.component.html',
  styleUrls: ['./service-provider-popup.component.scss'],
})
export class ServiceProviderPopupComponent implements OnInit {

  formGroup: FormGroup;
  error$ = new BehaviorSubject<boolean>(false);

  // Criteria services.
  emailServiceProvider: Array<InputSelectionCriteria> = [];
  smsServiceProvider: Array<InputSelectionCriteria> = [];

  // Selected services
  selectedEmailServiceProviders: Array<number> = [];
  selectedSmsServiceProviders: Array<number> = [];

  // Deletable services
  unRemovableEmailServiceProviders: Array<number> = [];
  unRemovableSmsServiceProviders: Array<number> = [];
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public config: { selected: ServiceProviderFormModel, initial: ServiceProviderFormModel },
    private _dialogRef: MatDialogRef<ServiceProviderFormModel>,
    private _snackbarService: SnackBarService,
    private _translateService: TranslateService,
    private _formBuilder: FormBuilder
  ) {
    this.formGroup = _formBuilder.group({});
  }

  ngOnInit(): void {
    this.formGroup = this._formBuilder.group({
      email: new FormControl(this.config.selected.MAIL),
      sms: new FormControl(this.config.selected.SMS),
    });


    // Criteria services
    this.emailServiceProvider = (JSON.parse(JSON.stringify(this.config.initial.MAIL)) as Array<ServiceProvider>)
      .sort((a: any, b: any) => a.priority - b.priority)
      .map((serviceProvider, key) => ({ value: serviceProvider.label, key: serviceProvider.id }));
    this.smsServiceProvider = (JSON.parse(JSON.stringify(this.config.initial.SMS)) as Array<ServiceProvider>)
      .sort((a, b) => a.priority - b.priority)
      .map((serviceProvider, key) => ({ key: serviceProvider.id, value: serviceProvider.label }));

    // Selected services
    this.selectedEmailServiceProviders = (this.config.selected.MAIL || [])
      .map((value) => this.emailServiceProvider.find(d => d.key === value.id)?.key as number);
    this.selectedSmsServiceProviders = (this.config.selected.SMS || [])
      .map((value) => this.smsServiceProvider.find(d => d.key === value.id)?.key as number);

    // Deletable services
    this.unRemovableSmsServiceProviders = (this.config.selected.SMS || [])
      .filter(item => !item.deletable)
      .map(item => item.id);
    this.unRemovableEmailServiceProviders = (this.config.selected.MAIL || [])
      .filter(item => !item.deletable)
      .map(item => item.id);
  }

  register(): void {
    const { email, sms } = this.formGroup.getRawValue();
    const serviceProviderForm: ServiceProviderFormModel = { SMS: sms, MAIL: email };
    this._dialogRef.close(serviceProviderForm);
  }

  closeModal(): void {
    this._dialogRef.close();
  }

  selectEmailService($event: any[]): void {
    const selectProviders: Array<ServiceProvider> = $event.map((serviceKey, index) => {
      const found = this.config.initial.MAIL.findIndex(item => item.id == serviceKey);
      const element = this.config.initial.MAIL[found];
      return { ...element, priority: index + 1 };
    });

    this.formGroup.controls['email'].patchValue(selectProviders);
  }

  selectSmsService($event: any[]): void {
    const selectProviders: Array<ServiceProvider> = $event.map((serviceKey, index) => {
      const found = this.config.initial.SMS.findIndex(item => item.id == serviceKey);
      const element = this.config.initial.SMS[found];
      return { ...element, priority: index + 1 };
    });

    this.formGroup.controls['sms'].patchValue(selectProviders);
  }

  alertCannotDeleteSource($event: any) {
    this._translateService.get('client.service_provider_cannot_be_delete').toPromise().then(message => {
      this._snackbarService.openCustomSnackbar({ icon: 'close', type: 'error', message })
    });
  }
}
