import { ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export class SmsParameterValidator extends Validators {
  static fieldSenderLabel(): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;
      if (!control.value?.trim()) {
        message = 'cxmCampaign.followMyCampaign.settingParameter.validation.required';
      } else if (control.value.length > 11) {
        message = 'client.metadata_sender_label_max_length';
      }
      return message ? { message } : null;
    };
  }
}
