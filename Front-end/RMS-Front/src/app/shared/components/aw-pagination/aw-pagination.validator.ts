import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class AwPaginationValidator {
  static mustBeNumberInRange(ranged: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value === '') {
        return null;
      }

      const value = JSON.parse(`${control.value}`);
      const valid = value > 0 && value <= ranged;
      return valid ? null : { notInRanged: true };
    };
  }
}
