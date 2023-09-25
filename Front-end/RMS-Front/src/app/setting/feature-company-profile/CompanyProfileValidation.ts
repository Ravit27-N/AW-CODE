import {AbstractControl, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import { RegexPattern } from '../../shared/constants/regex-parttern';
export class CompanyProfileValidation extends Validators {
  static titleValidation(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'Title is required';
      }

      return message ? { message } : null;
    };
  }
  static telephoneValidate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'Telephone is required';
      }

      if (
        control.value?.trim().length < 11 ||
        control.value?.trim().length > 12
      ) {
        message = 'Telephone must have length 11 or 12';
      }

      return message ? { message } : null;
    };
  }
  static addressValidation(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'Address is required';
      }

      return message ? { message } : null;
    };
  }

  static websiteValidation(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;
      const pattern = RegexPattern.websitePattern;

      if (!control.value?.trim()) {
        message = 'Website is required';
      }

      if (!control.value?.trim().match(pattern)) {
        message = 'Website is invalid';
      }

      return message ? { message } : null;
    };
  }

  static emailValidation(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      const pattern = RegexPattern.emailCompanyPattern;

      if (!control.value?.trim()) {
        message = 'Email is required';
      }
      if (!control.value?.trim().match(pattern)) {
        message = 'Email is invalid';
      }

      return message ? { message } : null;
    };
  }
}
