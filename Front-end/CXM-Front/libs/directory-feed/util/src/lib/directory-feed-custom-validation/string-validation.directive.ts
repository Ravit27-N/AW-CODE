import { Directive, EventEmitter, Input, Output } from '@angular/core';
import {
  AbstractControl,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { ErrorValidationDirectiveModel } from './error-validation-directive-model';
import { FieldProperty } from '@cxm-smartflow/directory-feed/data-access';

@Directive({
  selector: '[cxmSmartflowStringValidation]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: StringValidationDirective,
      multi: true,
    },
  ],
})
export class StringValidationDirective implements Validator {
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
    } else if (definedLength && value?.length > Number(definedLength)) {
      this.onError.next({ maximum: true });
    } else if (definedMask) {
      try {
        const exactMatch = new RegExp(this.ensureEndsWithDollar(definedMask));
        this.onError.next({ mask: !exactMatch.test(value) });
      }catch (err) {
        this.onError.next({ mask: true });
      }
    } else {
      this.onError.next({ required: false, maximum: false });
    }
    return null;
  }

  private ensureEndsWithDollar(regexPattern: string): string {
    if (!regexPattern.endsWith('$')) {
      return regexPattern + '$';
    }
    return regexPattern;
  }
}
