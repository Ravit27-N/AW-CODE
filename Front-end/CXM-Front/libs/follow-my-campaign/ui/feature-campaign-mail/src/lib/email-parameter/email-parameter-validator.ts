import { ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { EMAIL_PATTERN } from '@cxm-smartflow/shared/data-access/model';

export class EmailParameterValidator extends Validators {
  static fieldSenderEmail(): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;
      if (!control.value?.length) {
        message = 'cxmCampaign.followMyCampaign.settingParameter.validation.required';
      } else if (control.value.length > 128) {
        message = 'cxmCampaign.followMyCampaign.settingParameter.validation.maxLength';
      } else if (!control.value.match(EMAIL_PATTERN)) {
        message = 'cxmCampaign.followMyCampaign.settingParameter.senderEmail.invalid';
      }

      return message ? { message } : null;
    };
  }

  static fieldSenderName(): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;
      if (!control.value?.length) {
        message = 'cxmCampaign.followMyCampaign.settingParameter.validation.required';
      } else if (control.value.length > 128) {
        message = 'cxmCampaign.followMyCampaign.settingParameter.validation.maxLength';
      }

      return message ? { message } : null;
    };
  }

  static fieldUnsubscribeLink(): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;
      if (!control.value?.length) {
        message = 'cxmCampaign.followMyCampaign.settingParameter.validation.required';
      }

      return message ? { message } : null;
    };
  }
}
