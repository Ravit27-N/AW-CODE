import {ValidationErrors, ValidatorFn, Validators} from '@angular/forms';

export class AddDirectoryFeedValidation extends Validators {

  static validateString(require?: boolean, length?:number, mask?:string): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;

      if (require !== undefined && require) {
        if (!control.value) {
          // is require
          message = 'directory.insert_directory_feed_error.require';
          return message ? { message } : null;
        }
      }

      if (length !== undefined) {
        if (control.value.trim().length > length) {
          message = 'directory.insert_directory_feed_error.length';
          return message ? { message } : null;
        }
      } else if (mask !== undefined) {
        if (control.value) {
          message = 'directory.insert_directory_feed_error.mask';
          try {
            const exactMatch = new RegExp(this.ensureEndsWithDollar(mask));
            return !exactMatch.test(control.value.trim()) ? { message } : null;
          } catch (err) {
            return { message };
          }
        }
      }

      return message ? { message } : null;
    };
  }

  static validateInteger(require?: boolean, length?:number): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;

      if (require !== undefined && require) {
        if (control.value === null) {
          message = 'directory.insert_directory_feed_error.require';
          return message ? {message} : null;
        }
      }

      if (control.value % 1 !== 0) {
        message = 'directory.insert_directory_feed_error.integer';
        return message ? {message} : null;
      }

      if (control.value !== null && length != undefined) {
        if (String(control.value).trim().length > length) {
          message = 'directory.insert_directory_feed_error.length';
          return message ? {message} : null;
        }
      }

      return message ? {message} : null;

    };
  }


  static validateNumber(require?: boolean, length?:number): ValidatorFn {
    return (control): ValidationErrors | null => {
      let message: string | undefined;

      if (require !== undefined && require) {
        if (control.value === null) {
          message = 'directory.insert_directory_feed_error.require';
          return message ? {message} : null;
        }
      }

      if (control.value !== null && length != undefined) {
        if (String(control.value).trim().length > length) {
          message = 'directory.insert_directory_feed_error.length';
          return message ? {message} : null;
        }
      }

      return message ? {message} : null;

    };
  }


  private static ensureEndsWithDollar(regexPattern: string): string {
    if (!regexPattern.endsWith('$')) {
      return regexPattern + '$';
    }
    return regexPattern;
  }
}
