import { AbstractControl } from '@angular/forms';

/**
 * Custom validator for validate special characters.
 * @param control
 */
export function validateSpacialCharacter(control: AbstractControl) {
  if (!/^[^`~!@#$%\^&*()_+={}|[\]\\:';"<>?,./]*$/.test(control.value)) {
    return { isSpecialCharacter: true };
  }
  return null;
}
