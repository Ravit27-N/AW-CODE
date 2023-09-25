import { FormGroup, ValidatorFn } from '@angular/forms';

export const confirmPasswordValidator = (controlName: string, matchingControlName: string) => (formGroup: FormGroup): ValidatorFn => {
  const control = formGroup.controls[controlName];
  const matchingControl = formGroup.controls[matchingControlName];
  if (
    matchingControl.errors &&
    !matchingControl.errors.confirmPasswordValidator
  ) {
    return;
  }

  if (control.value !== matchingControl.value) {
    matchingControl.setErrors({ confirmPasswordValidator: true });
  } else {
    matchingControl.setErrors(null);
  }
};
