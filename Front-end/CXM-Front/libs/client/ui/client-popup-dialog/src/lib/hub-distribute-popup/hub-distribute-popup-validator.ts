import { ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { EMAIL_PATTERN } from '@cxm-smartflow/shared/data-access/model';

export class HubDistributePopupValidator extends Validators {
  static fieldEmail() : ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (!control.value?.trim()) {
        message = 'client.hub_distribute_identifiant_required';
      } else if (!control.value.trim().toLowerCase().match(EMAIL_PATTERN)) {
        message = 'client.hub_distribute_identifiant_invalid';
      } else if (control.value.trim().length > 128) {
        message = 'client.hub_distribute_identifiant_max_length';
      }

      return message? { message } : null;
    };
  }

  static fieldPassword(isModify: boolean): ValidatorFn {
    return (control): ValidationErrors | null => {
      if (isModify) return null;

      let message: string | undefined;
      if (!control.value?.trim()) {
        message = 'client.hub_distribute_password_required';
      }

      return message? { message } : null;
    };
  }
}
