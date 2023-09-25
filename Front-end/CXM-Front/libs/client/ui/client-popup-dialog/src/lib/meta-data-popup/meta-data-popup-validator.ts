import { ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { EMAIL_PATTERN } from '@cxm-smartflow/shared/data-access/model';
import { MetadataPayloadType } from '@cxm-smartflow/client/data-access';

export class MetaDataPopupValidator extends Validators {
  static fields(validateType: MetadataPayloadType) : ValidatorFn {

    if (validateType === 'sender_name') {
      return this.fieldSenderName();
    }

    if (validateType === 'sender_mail') {
      return this.fieldSenderEmail();
    }

    if (validateType === 'unsubscribe_link') {
      return this.fieldUnsubscribeLink();
    }

    return this.fieldSenderLabel();
  }

  private static fieldSenderName(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (!control.value.metadata?.trim()) {
        message = 'client.metadata_sender_name_is_required';
      } else if (control.value.metadata.trim().length > 128) {
        message = 'client.hub_distribute_identifiant_max_length';
      }

      return message? { message } : null;
    };
  }

  private static fieldSenderEmail(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (!control.value.metadata?.trim()) {
        message = 'client.metadata_sender_mail_is_required';
      } else if (!control.value.metadata.trim().toLowerCase().match(EMAIL_PATTERN)) {
        message = 'client.hub_distribute_identifiant_invalid';
      } else if (control.value.metadata.trim().length > 128) {
        message = 'client.hub_distribute_identifiant_max_length';
      }

      return message? { message } : null;
    };
  }

  private static fieldUnsubscribeLink(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (!control.value.metadata?.trim()) {
        message = 'client.metadata_sender_unsubscribe_link_is_required';
      } else if (!control.value.metadata.trim().toLowerCase().match(EMAIL_PATTERN)) {
        message = 'client.hub_distribute_identifiant_invalid';
      } else if (control.value.metadata.trim().length > 128) {
        message = 'client.hub_distribute_identifiant_max_length';
      }

      return message? { message } : null;
    };
  }

  private static fieldSenderLabel(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (!control.value.metadata?.trim()) {
        message = 'client.metadata_sender_label_is_required';
      } else if(control.value.metadata.trim().length > 11) {
        message = 'client.metadata_sender_label_max_length';
      }

      return message? { message } : null;
    };
  }

  static fieldDuplicated(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;

      if (control?.parent?.getRawValue()) {
        const rarValue: Array<string> = Object.entries(control?.parent?.getRawValue()).map((kv: any) => kv[1].metadata.trim());
        const invalid = rarValue.filter(data => data === control.value.metadata.trim() && control.value.metadata.length).length > 1;

        if (invalid) {
          message = 'client.metadata_is_duplicated';
        }
      }

      return message? { message } : null;
    };
  }
}
