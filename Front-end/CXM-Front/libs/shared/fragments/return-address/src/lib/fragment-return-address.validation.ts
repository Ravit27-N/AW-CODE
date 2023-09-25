import {ValidationErrors, ValidatorFn, Validators} from '@angular/forms';

export class FragmentReturnAddressValidation extends Validators {

  static addressLine(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;

      if (control.value?.trim()?.length > 38) {
        message = 'client.fragment_return_address_address_line_exceed_line';
      }
      return message ? {message} : null;
    };
  }

  static zipcodeCity(): ValidatorFn {
    return (control): ValidationErrors | null => {

      let message: string | undefined;

      if (!control.value) {
        message = 'client.fragment_return_address_zipcode_required';
      }

      if (control.value?.trim()?.length > 38) {
        message = 'client.fragment_return_address_address_line_exceed_line';
      }
      return message ? {message} : null;
    };
  }

}
