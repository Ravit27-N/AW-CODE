import {
  AbstractControl,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { ErrorValidationDirectiveModel } from './error-validation-directive-model';
import {
  DirectoryDataType,
  FieldProperty,
} from '@cxm-smartflow/directory-feed/data-access';

@Directive({
  selector: '[cxmSmartflowNumberValidation]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: NumberValidationDirective,
      multi: true,
    },
  ],
})
export class NumberValidationDirective implements Validator {
  @Input() fieldProperty: FieldProperty;
  @Input() dataType: 'Number' | 'Integer';
  @Output()
  onError: EventEmitter<ErrorValidationDirectiveModel> = new EventEmitter<ErrorValidationDirectiveModel>();
  private regexNumeric = '^[-+]?\\d+(?:\\.\\d+)?$';
  private regexInteger = '^[-+]?\\d+$';

  validate(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (value?.length === 0) {
      if (this.fieldProperty.required) {
        this.onError.next({ required: true });
      } else {
        this.onError.next({ required: false });
      }
    } else if (
      value?.length > Number(this.fieldProperty.option?.length || 255)
    ) {
      this.onError.next({ maximum: true });
    } else {
      let exactMatch = null;
      if (this.dataType === DirectoryDataType.NUMBER) {
        exactMatch = new RegExp(this.regexNumeric);
      } else {
        exactMatch = new RegExp(this.regexInteger);
      }
      this.onError.next({
        required: false,
        maximum: false,
        dateType: !exactMatch.test(value),
      });
    }
    return null;
  }
}
