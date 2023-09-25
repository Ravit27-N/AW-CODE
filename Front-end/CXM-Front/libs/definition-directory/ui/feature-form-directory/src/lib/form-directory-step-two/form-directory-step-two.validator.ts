import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';

export class FormDirectoryStepTwoValidator extends Validators {
  static fieldDisplayName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | undefined;

      if (control.value?.trim()?.length === 0) {
        message = 'directory.definition_directory_create_step_1_field_name_required';
      } else if (control.value?.trim()?.length > 100) {
        message = 'directory.directory_definition_part2_max_display_name_length';
      }

      return message ? { message } : null;
    };
  }

  static fieldDataType(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | undefined;

      if (control.value?.trim()?.length === 0) {
        message = 'directory.definition_directory_create_step_1_field_display_name_required';
      }

      return message ? { message } : null;
    };
  }

  static fieldKey(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | undefined;

      return message ? { message } : null;
    };
  }

  static fieldData(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | undefined;

      if (control.value?.trim()?.length === 0) {
        message = 'directory.definition_directory_create_step_1_field_display_name_required';
      }

      return message ? { message } : null;
    };
  }

  static fieldPresence(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | undefined;

      return message ? { message } : null;
    };
  }

  static fieldMaxLength(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | undefined;
      if (control.value > 255) {
        message = 'directory.definition_directory_create_step_2_section_field_properties_max_invalid_length';
      } else if (control.value < 0) {
        message = 'directory.definition_directory_create_step_2_section_field_properties_min_invalid_length';
      }

      return message ? { message } : null;
    };
  }

  static fieldMask(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let message: string | undefined;

      return message ? { message } : null;
    };
  }
}
