import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';

export class AddCandidateStepThreeValidator extends Validators {
  static fieldCompanyName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value) {
        message = 'Company name is required';
      } else if (control.value?.length > 255) {
        message = 'Company name cannot be exceed 255 characters ';
      }

      return message ? { message } : null;
    };
  }

  static fieldPosition(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value) {
        message = 'Position is required';
      } else if (control.value?.length > 255) {
        message = 'Position cannot be exceed 255 characters ';
      }

      return message ? { message } : null;
    };
  }

  static fieldExperienceStartDate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;
      const startDate = control?.value;
      const endDate = control?.parent?.value?.experienceEndDate;

      if (!control.value) {
        message = 'Start date is required';
      }

      return message ? { message } : null;
    };
  }

  static fieldExperienceEndDate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return null;
    };
  }

  static fieldLevel(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value) {
        message = 'Level is required';
      }

      return message ? { message } : null;
    };
  }

  static fieldProjectType(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (control.value?.trim().length > 255) {
        message = 'Project type should not exceed 255 characters.';
      }

      return message ? { message } : null;
    };
  }

  static fieldTechnology(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (control.value?.trim().length > 255) {
        message = 'Technology should not exceed 255 characters.';
      }

      return message ? { message } : null;
    };
  }

  static fieldRemarks(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (control.value?.trim().length > 255) {
        message = 'Technology should not exceed 255 characters.';
      }

      return message ? { message } : null;
    };
  }
}
