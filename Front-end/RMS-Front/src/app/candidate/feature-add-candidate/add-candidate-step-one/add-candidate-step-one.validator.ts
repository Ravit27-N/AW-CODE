import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { RegexPattern } from '../../../shared';

export class AddCandidateStepOneValidator extends Validators {
  static fieldSalutation(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'Salutation is required';
      }

      return message ? { message } : null;
    };
  }

  static fieldFirstName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'First name is required';
      } else if (control.value?.trim()?.length > 255) {
        message = 'The first name cannot exceed 255 characters.';
      }

      return message ? { message } : null;
    };
  }

  static fieldLastName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'Last name is required';
      } else if (control.value?.trim()?.length > 255) {
        message = 'The last name cannot exceed 255 characters.';
      }

      return message ? { message } : null;
    };
  }

  static fieldGender(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!['Female', 'Male'].includes(control.value?.trim())) {
        message = 'Please select a valid gender';
      }

      return message ? { message } : null;
    };
  }

  static fieldDateOfBirth(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (control.value && new Date(control.value) > new Date()) {
        message = 'Invalid date of birth.';
      }

      return message ? { message } : null;
    };
  }

  static fieldEmail(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value) {
        message = 'Email is required';
      } else if (
        !control.value?.toLowerCase()?.match(RegexPattern.emailPattern)
      ) {
        message = 'Please input a valid email!';
      } else if (control.value?.trim()?.length > 255) {
        message = 'The email cannot exceed 255 characters.';
      }

      return message ? { message } : null;
    };
  }

  static phoneNumberValidator(value: any) {
    const findDuplicates = (arr: string[]): string[] => {
      const set = new Set<string>();
      const duplicates: string[] = [];

      for (const item of arr) {
        if (set.has(item)) {
          duplicates.push(item);
        } else {
          set.add(item);
        }
      }

      return duplicates;
    };

    const ownerProperties: any = this;
    const values = ownerProperties?.values as string[];
    const duplicated = findDuplicates(values);

    let message: string | null = null;

    if (!value) {
      message = 'Phone number is required';
    } else if (!value?.toLowerCase()?.match(RegexPattern.phoneNumberPattern)) {
      message = 'Please input a valid phone number!';
    } else if (duplicated.includes(value)) {
      message = 'Phone number cannot be duplicated!';
    }

    return message ? { message } : null;
  }
}
