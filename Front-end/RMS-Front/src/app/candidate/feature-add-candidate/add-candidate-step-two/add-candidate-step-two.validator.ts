import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';

export class AddCandidateStepTwoValidator extends Validators {
  static fieldUniversity(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value) {
        message = 'University is required';
      }

      return message ? { message } : null;
    };
  }

  static fieldAcademicYearStart(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value) {
        message = 'Start year is required';
      }

      return message ? { message } : null;
    };
  }

  static fieldAcademicYearEnd(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      return message ? { message } : null;
    };
  }

  static fieldGraduate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return null;
    };
  }

  static fieldGPA(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      // Check if the GPA is required
      if (!control.value) {
        message = 'GPA is required';
      } else {
        // Regular expression to match a valid GPA with up to two decimal places
        const gpaRegex = /^[0-9](\.[0-9]{1,2})?$/;

        // Check if the GPA matches the regex
        if (!gpaRegex.test(control.value)) {
          message =
            'Invalid GPA format. GPA should be a number between 0 and 4 with up to two decimal places.';
        } else {
          // Parse the GPA value as a float
          const gpaValue = parseFloat(control.value);

          // Check if the GPA is within the valid range
          if (isNaN(gpaValue) || gpaValue < 0 || gpaValue > 4) {
            message = 'Invalid GPA. GPA should be a number between 0 and 4.';
          }
        }
      }

      return message ? { message } : null;
    };
  }

  static fieldRemarks(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (control.value?.trim().length > 255) {
        message = 'Remarks should not exceed 255 characters.';
      }

      return message ? { message } : null;
    };
  }

  static fieldMajor(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'Major is required.';
      }

      return message ? { message } : null;
    };
  }

  static fieldDegree(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | null = null;

      if (!control.value?.trim()) {
        message = 'Degree is required.';
      }

      return message ? { message } : null;
    };
  }
}
