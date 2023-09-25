import {FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';

export class AddressDestinationPopupValidation extends Validators {
  static addressLine(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;
      if (control.value.trim().length > 38) {
        message = 'client.fragment_return_address_address_line_exceed_line';
      }
      return message ? {message} : null;
    };
  }

  static addressLineSix(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;

      if (!control.value) {
        message = 'client.fragment_return_address_zipcode_required';
      }

      return message ? {message} : null;
    };
  }

}
