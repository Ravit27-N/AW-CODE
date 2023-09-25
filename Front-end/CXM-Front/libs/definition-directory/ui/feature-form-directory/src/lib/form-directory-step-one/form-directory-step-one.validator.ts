import {AbstractControl, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';

export class FormDirectoryStepOneValidator extends Validators {

  static fieldName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {

      let message: string | undefined;

      if (control.value?.trim()?.length === 0) {
        message = 'directory.definition_directory_create_step_1_field_display_name_required';
      }

      if (control.value?.includes(' ')) {
        message = 'directory.definition_directory_create_step_1_field_display_name_cannot_contains_space';
      }

      if (control.value?.length > 100) {
        message = 'directory.definition_directory_create_step_1_field_name_maxlength';
      }

      return message ? { message } : null;
    };
  }

  static fieldDisplayName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {

      let message: string | undefined;

      if (!control.value) {
        message = 'directory.definition_directory_create_step_1_field_display_name_required';
      }

      if (control.value?.length > 255) {
        message = 'directory.definition_directory_create_step_1_field_display_name_maxlength';
      }

      return message ? {message} : null;
    };
  }

}
