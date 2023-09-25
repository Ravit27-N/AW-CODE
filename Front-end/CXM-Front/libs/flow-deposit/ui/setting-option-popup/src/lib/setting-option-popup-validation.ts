import {ValidationErrors, ValidatorFn, Validators} from '@angular/forms';

export class SettingOptionPopupValidation extends Validators {

  static fieldChoiceOfAttachement(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (!control.value?.trim() && control.parent?.getRawValue()?.sourceFrom == '1') {
        message = 'true';
      }

      return message ? { message } : null;
    };
  }

  static fieldUploading(): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;
      if (!control.value?.trim() && control.parent?.getRawValue()?.sourceFrom == '2') {
        message = 'true';
      }
      return message ? { message } : null;
    };
  }

  static waterMarkText(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (!control.value?.trim()) {
        message = 'flow.deposit.setting_option_popup_water_mark_text.require';
      } else if (control.value.trim().length > 30) {
        message = 'flow.deposit.setting_option_popup_water_mark_text.length';
      }
      return message ? {message} : null;
    };
  }

  static watermarkSize(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (control.value != 0 && !control.value) {
        message = 'flow.deposit.setting_option_popup_water_mark_text_size.require';
      } else if (control.value < 1) {
        message = 'flow.deposit.setting_option_popup_water_mark_text_size.positive';
      }
      return message ? {message} : null;
    };
  }

  static watermarkRotation(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (control.value != 0 && !control.value) {
        message = 'flow.deposit.setting_option_popup_water_mark_text_rotation.require';
      } else if (control.value < -360 || control.value > 360) {
        message = 'flow.deposit.setting_option_popup_water_mark_text_rotation.limit';
      }
      return message ? {message} : null;
    };
  }
}
