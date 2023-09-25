import { Directive, EventEmitter, Input, Output } from '@angular/core';
import {
  AbstractControl,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { ErrorValidationDirectiveModel } from './error-validation-directive-model';
import * as moment from 'moment';
import { FieldProperty } from '@cxm-smartflow/directory-feed/data-access';

@Directive({
  selector: '[cxmSmartflowDateValidation]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: DateValidationDirective,
      multi: true,
    },
  ],
})
export class DateValidationDirective implements Validator {
  @Input() fieldProperty: FieldProperty;
  @Output() onError = new EventEmitter<ErrorValidationDirectiveModel>();

  validate(control: AbstractControl): ValidationErrors | null {
    const definedLength = this.fieldProperty.option['length'];
    const definedMask = this.fieldProperty.option['mask'];
    const value = (control?.value as string)?.trim();

    if (value?.length === 0) {
      if (this.fieldProperty.required) {
        this.onError.next({ required: true });
      } else {
        this.onError.next({ required: false });
      }
    } else if (definedLength) {
      const valid = moment(value, 'DD/MM/YYYY HH:mm:ss', true).isValid();
      this.onError.next({ mask: !valid });
    } else if (definedMask) {
      const valid = moment(value, definedMask, true).isValid();
      this.onError.next({ mask: !valid });
    } else {
      this.onError.next({ required: false, maximum: false, mask: false });
    }
    return null;
  }
}
