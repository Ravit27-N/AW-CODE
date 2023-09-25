import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges, ViewChild,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ClientService } from '@cxm-smartflow/client/data-access';
import { REGEXP } from '@cxm-smartflow/shared/utils';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { checkDuplicatedClientName } from './client.validator';
import { EMAIL_PATTERN } from '@cxm-smartflow/shared/data-access/model';
import { FileValidator } from '@cxm-smartflow/shared/common-typo';
import { TranslateService } from '@ngx-translate/core';
import {
  FragmentReturnAddressComponent,
  FragmentReturnAddressType
} from "@cxm-smartflow/shared/fragments/return-address";

interface IClientFormSubmitEvent {
  form: any;
  error: any;
}

declare type clientFormCtrl = 'name' | 'email';

@Component({
  selector: 'cxm-smartflow-client-form',
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.scss']
})
export class ClientFormComponent implements OnInit, OnDestroy, OnChanges {

  @Input() clientData: any;
  @Input() mode: 0 | 1 = 0;
  isOpenDetailsSummary = true;


  @ViewChild('returnAddressElement') returnAddressElement: FragmentReturnAddressComponent;
  returnAddressForm: FragmentReturnAddressType;

  clientForm: FormGroup;
  errorNameMsg: string;
  fileValidator: FileValidator = { fileSize: 5 };

  messages = {
    emailFormat: 'client.formError.emailFormat',
    nameRequired: 'client.formError.nameRequired',
    nameExisted: 'client.formError.doubleName',
    alphaNum: 'client.formError.invalidName'
  };

  hasSubmit$ = new BehaviorSubject<boolean>(false);

  @Input() uploadState: Observable<any>;

  @Output() submitEvent = new EventEmitter<IClientFormSubmitEvent>();
  @Output() uploadFileEvent = new EventEmitter<any>();
  @Output() formValueChanged = new EventEmitter<IClientFormSubmitEvent>();

  destroy$ = new Subject<boolean>();

  async submit() {
    this.hasSubmit$.next(true);
    this.clientForm.markAllAsTouched();
    const f = this.clientForm.getRawValue();
    const address: FragmentReturnAddressType | null | undefined = await this.returnAddressElement.getValueAndValidity(true);

    let errors = this.getFormValidationErrors();
    if (address === null) {
      errors = { ...errors, returnAddressErrorExisted: true };
    }
    this.submitEvent.emit({ error: errors, form: { ...f, address: address || null } });
    this.formValueChanged.emit({ error: errors, form: { ...f, address: address || null } });
    return Promise.resolve({ error: errors, form: { ...f, address: address || null } })
  }

  ngOnInit(): void {
    this.clientForm.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((form) => {
        this.hasSubmit$.next(false);
        const error = this.getFormValidationErrors();
        this.formValueChanged.emit({ form: { ...form, address: form.address || null }, error });
      });

    this.translate.get('client.messages.file_size_exceed').subscribe(value => {
      this.fileValidator = {
        ...this.fileValidator,
        fileSizeMessage: value
      };
    });
  }

  ngOnDestroy(): void {
    this.hasSubmit$.complete();
    this.destroy$.next(true);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.clientData
      // && !changes.clientData.firstChange
      ) {
      const { contactFirstName, contactLastname, email, name, address } = changes.clientData.currentValue;
      this.clientForm.patchValue({ contactFirstName, contactLastname, email, name }, { emitEvent: false });
      this.returnAddressForm = address;
    }

    if(changes.mode && changes.mode.currentValue !== changes.mode.previousValue) {
      if(changes.mode.currentValue === 1) { this.clientForm.controls['name'].disable() }
      else this.clientForm.controls['name'].enable();
     }
  }


  getFormValidationErrors() {
    const controlErrors = {};
    Object.keys(this.clientForm.controls).forEach(key => {
      const e = this.clientForm.get(key)?.errors;
      if (e) Object.assign(controlErrors, { [key]: e });

    });

    return controlErrors;
  }


  returnAddressFormChange(address: FragmentReturnAddressType): void {
    const f = this.clientForm.getRawValue();
    let errors = this.getFormValidationErrors();
    if (address === null) {
      errors = { ...errors, returnAddressErrorExisted: true };
    }


    this.formValueChanged.emit({ error: errors, form: { ...f, address: address || null } });
  }


  get isCompanyNameError() {
    if (this.clientForm.controls['name']?.errors?.required && this.hasSubmit$.value) {
      return this.clientForm.controls['name']?.invalid;
    } else if (this.clientForm.controls['name']?.errors?.existed || this.clientForm.controls['name'].errors?.pattern) {
      return this.clientForm.controls['name']?.touched && this.clientForm.controls['name']?.invalid;
    } else {
      return false;
    }
  }

  get isInvalidEmail() {
    return this.clientForm.controls['email']?.touched && this.clientForm.controls['email']?.invalid;
  }

  get getCompanyNameError() {
    if (this.clientForm.controls['name'].errors?.required) return this.messages.nameRequired;
    if (this.clientForm.controls['name'].errors?.existed) return this.messages.nameExisted;
    if (this.clientForm.controls['name'].errors?.pattern) return this.messages.alphaNum;

    return this.messages.nameRequired;
  }

  get getEmailError() {
    if (this.clientForm.controls['email'].errors?.pattern) return this.messages.emailFormat;
    return this.messages.emailFormat;
  }

  constructor(private fb: FormBuilder, private clientService: ClientService, private translate: TranslateService) {
    this.clientForm = this.fb.group({
      name: new FormControl({ value: '', disabled: this.mode === 1 },
        [Validators.required, Validators.nullValidator, Validators.pattern(REGEXP.alphaNumeric)],
        [checkDuplicatedClientName(this.clientService)]),
      contactFirstName: new FormControl(''),
      contactLastname: new FormControl(''),
      email: new FormControl('', [Validators.pattern(EMAIL_PATTERN)])
    });
  }
}
